package com.onyem.jtracer.reader.events.internal.converter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.events.model.IMethodEntryInvocationEvent;
import com.onyem.jtracer.reader.events.model.IMethodExitInvocationEvent;
import com.onyem.jtracer.reader.events.model.InvocationEventType;
import com.onyem.jtracer.reader.events.model.internal.InvocationEventComparator;
import com.onyem.jtracer.reader.events.model.internal.InvocationLoopEvent;

@NotThreadSafe
public class LoopEventLoader implements IEventConverter {

  private final int count;
  private final List<IInvocationEvent> events;
  private final List<IInvocationEvent> delayedEvents;
  private final InvocationEventComparator eventComparator;

  private static final int EXTRA_LOAD_COUNT = 25;

  // How many events need to be searched for the corresponding exit for an entry?
  private static final int MAX_EXIT_EVENT_SEARCH = 100;

  public LoopEventLoader(int count) {
    this.count = count;
    events = new ArrayList<IInvocationEvent>();
    delayedEvents = new ArrayList<IInvocationEvent>();
    eventComparator = new InvocationEventComparator();
  }

  @Override
  public void convertEvents(List<IInvocationEvent> newEvents,
      final boolean complete) {

    // Do we need to load more events?
    if (!loadMoreEvents()) {
      return;
    }

    List<IInvocationEvent> localEvents = new ArrayList<IInvocationEvent>(
        newEvents);

    // Add the delayed events
    localEvents.addAll(0, delayedEvents);
    delayedEvents.clear();

    List<List<IInvocationEvent>> threadList = groupEventsByThread(localEvents);
    IInvocationEvent newEvent = getNextEvent(threadList, complete);
    while (loadMoreEvents() && newEvent != null) {
      events.add(newEvent);
      newEvent = getNextEvent(threadList, complete);
    }

    if (!complete) {
      // Add the remaining events to the delayed events
      List<IInvocationEvent> remainingEvents = new ArrayList<IInvocationEvent>();
      for (List<IInvocationEvent> eventsList : threadList) {
        remainingEvents.addAll(eventsList);
      }
      delayedEvents.addAll(0, remainingEvents);
    }

  }

  private List<List<IInvocationEvent>> groupEventsByThread(
      List<IInvocationEvent> events) {

    List<List<IInvocationEvent>> threadList = new ArrayList<List<IInvocationEvent>>();
    IInvocationThread thread = null;
    List<IInvocationEvent> eventList = null;
    for (IInvocationEvent event : events) {
      if (thread == null /* First event */
          || !thread.equals(event.getThread()) /* New thread event */) {
        thread = event.getThread();
        eventList = new ArrayList<IInvocationEvent>();
        threadList.add(eventList);
      }
      eventList.add(event);
    }
    return threadList;
  }

  /**
   * This method will return the first event from the threadList after trying to
   * create a loop
   * 
   * If no event can be created then null is returned. Otherwise the threadList
   * is mutated to extract some {@link IInvocationEvent}s to create a new one.
   * 
   */
  private IInvocationEvent getNextEvent(
      List<List<IInvocationEvent>> threadList, final boolean complete) {
    // Case when there are no threads
    if (threadList.isEmpty()) {
      return null;
    }
    // Case when the first threadList has no events, rollover to the next group
    final List<IInvocationEvent> events = threadList.get(0);
    if (events.isEmpty()) {
      threadList.remove(0);
      return getNextEvent(threadList, complete);
    }
    final IInvocationEvent firstEvent = events.remove(0);
    // Case for a non MethodEntry event. That event is removed and returned.
    if (firstEvent.getType() != InvocationEventType.MethodEntry) {
      return firstEvent;
    }
    // Search for the next exit event
    final IMethodEntryInvocationEvent entryEvent = (IMethodEntryInvocationEvent) firstEvent;
    final int firstExitIndex = getFirstExitEvent(entryEvent, events);

    // Cannot find the exit index
    if (firstExitIndex == -1) {
      if (!complete && threadList.size() == 1
          && isExitEventUnknown(entryEvent, events)) {
        // We don't have enough data to know if there is a loop
        // Only the last thread group events can potentially be continued with more data
        // Revert the mutation done
        events.add(0, entryEvent);

        return null;
      } else {
        // Stop loop matching since there is no exit event
        return entryEvent;
      }
    }
    // Stop if firstExitIndex + 1 is not the same as entryEvent
    if (events.size() == (firstExitIndex + 1)
        || !eventComparator.compare(entryEvent, events.get(firstExitIndex + 1))) {
      return entryEvent;
    }

    // Let's try and form the loop
    List<IInvocationEvent> loopEvents = new ArrayList<IInvocationEvent>();
    loopEvents.add(entryEvent);
    for (int i = 0; i <= firstExitIndex; i++) {
      loopEvents.add(events.get(i));
    }
    int loopCount = getLoopCount(loopEvents, events, firstExitIndex + 1);
    if (loopCount == 0) {
      return entryEvent;
    }
    // We got a loop so create the new event
    // But first try and look for nested loops
    // We do not want to limit the number of events 
    LoopEventLoader nestedLoader = new LoopEventLoader(loopEvents.size());
    nestedLoader.convertEvents(loopEvents, true);
    List<IInvocationEvent> nestedLoop = nestedLoader.getEvents();

    IInvocationEvent loopEvent = new InvocationLoopEvent(
        InvocationEventType.NULL, InvocationEventType.NULL,
        entryEvent.getThread(), loopCount + 1, nestedLoop);
    // Remove the events used
    Iterator<IInvocationEvent> eventsIter = events.iterator();
    for (int removeCount = ((loopCount + 1) * loopEvents.size() - 1); removeCount > 0
        && eventsIter.hasNext(); removeCount--) {
      eventsIter.next();
      eventsIter.remove();
    }
    return loopEvent;
  }

  /**
   * TODO
   * Handle exit throws and exceptions cases
   * An exception thrown could have unrolled the stack beyond the entryEvent
   * 
   * Get the index of the first MethodExitEvent that matches the entryEvent
   * passed or -1 if none exist
   * 
   * @param entryEvent
   * @param events
   * @return
   * 
   */
  private int getFirstExitEvent(IMethodEntryInvocationEvent entryEvent,
      List<IInvocationEvent> events) {
    int stackDepth = 1;
    for (int i = 0; i < events.size(); i++) {
      IInvocationEvent event = events.get(i);
      if (event.getType() == InvocationEventType.MethodExit) {
        IMethodExitInvocationEvent exitEvent = (IMethodExitInvocationEvent) event;
        if (exitEvent.getMethod().equals(entryEvent.getMethod())) {
          if (stackDepth == 1) {
            return i;
          } else {
            stackDepth--;
          }
        }
      } else if (event.getType() == InvocationEventType.MethodEntry) {
        IMethodEntryInvocationEvent otherEntryEvent = (IMethodEntryInvocationEvent) event;
        if (otherEntryEvent.getMethod().equals(entryEvent.getMethod())) {
          stackDepth++;
        }
      }
    }
    return -1;
  }

  /**
   * TODO
   * Handle exit throws and exceptions cases
   * An exception thrown could have unrolled the stack beyond the entryEvent
   * 
   * @param entryEvent
   * @param events
   * @return
   * 
   */
  private boolean isExitEventUnknown(IMethodEntryInvocationEvent entryEvent,
      List<IInvocationEvent> events) {

    if (events.size() > MAX_EXIT_EVENT_SEARCH) {
      return false;
    }
    return true;
  }

  /**
   * Find the number of times the loopEvents are repeated in otherEvents. Search
   * is started from startIndex.
   * 
   * @param loopEvents
   * @param otherEvents
   * @param startIndex
   * @return
   */
  private int getLoopCount(List<IInvocationEvent> loopEvents,
      List<IInvocationEvent> otherEvents, int startIndex) {
    // If there are not enough elements for even 1 loop then return
    if (otherEvents.size() < loopEvents.size() + startIndex) {
      return 0;
    }
    for (int i = 0; i < loopEvents.size(); i++) {
      IInvocationEvent loopEvent = loopEvents.get(i);
      IInvocationEvent otherEvent = otherEvents.get(startIndex + i);
      if (!eventComparator.compare(loopEvent, otherEvent)) {
        return 0;
      }
    }
    return 1 + getLoopCount(loopEvents, otherEvents,
        startIndex + loopEvents.size());
  }

  @Override
  public int getFetchCount() {
    int fetchCount = count - events.size();
    return (fetchCount < 0) ? 0 : fetchCount + EXTRA_LOAD_COUNT;
  }

  @Override
  public boolean loadMoreEvents() {
    return events.size() < count;
  }

  @Override
  public List<IInvocationEvent> getEvents() {
    return events;
  }
}
