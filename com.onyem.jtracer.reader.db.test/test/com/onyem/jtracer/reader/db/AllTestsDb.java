package com.onyem.jtracer.reader.db;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.onyem.jtracer.reader.db.internal.MigrationTest;
import com.onyem.jtracer.reader.db.transactional.ExecuteTransactionTest;
import com.onyem.jtracer.reader.db.transactional.UpdateReturnTransactionTest;
import com.onyem.jtracer.reader.db.transactional.UpdateTransactionTest;

@RunWith(Suite.class)
@SuiteClasses({ MigrationTest.class, UpdateTransactionTest.class,
    UpdateReturnTransactionTest.class, ExecuteTransactionTest.class })
public class AllTestsDb {

}
