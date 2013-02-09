package com.onyem.jtracer.reader.meta;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ MetaClassTest.class, MetaMethodTest.class, PrimitiveTest.class,
    SignatureTest.class })
public class AllTestsMeta {

}
