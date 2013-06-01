package com.onyem.jtracer.reader.events.internal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ EventsWithSmallFileTest.class, EventsWithMediumFileTest.class,
    EventsWithMediumFile2Test.class, LoadEventsTest.class,
    ExceptionEventTest.class, LoopEventTest.class,
    LoopEventWithLastLoopEventTest.class })
public class AllTestsEvents {

}
