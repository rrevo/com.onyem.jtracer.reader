package com.onyem.jtracer.reader.events.internal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ EventsWithSmallFileTest.class, EventsWithMediumFileTest.class,
    EventsWithMediumFile2Test.class })
public class AllTestsEvents {

}
