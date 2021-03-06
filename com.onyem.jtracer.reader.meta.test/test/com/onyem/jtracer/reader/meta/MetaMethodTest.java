package com.onyem.jtracer.reader.meta;

import junit.framework.Assert;

import org.junit.Test;

import com.onyem.jtracer.reader.meta.internal.TypeConstants;

public class MetaMethodTest extends AbstractMetaTest {

  @Override
  protected String getMetaPath() {
    return "/test-data/meta1.txt";
  }

  long getClassMetaId(IMethod method) {
    return metaService.getClassById(method.getIClass().getId()).getMetaId()
        .longValue();
  }

  //<im|1|0|1|<init>|()V|||>
  @Test
  public void testMethodByMetaId() {
    final long metaId = 1l;
    IMethod method = metaService.getMethodByMetaId(metaId);
    long id = method.getId().getId();
    Assert.assertTrue(id > 0);
    Assert.assertEquals(metaId, method.getMetaId().longValue());
    Assert.assertEquals(0l, getClassMetaId(method));
    Assert.assertEquals(1, method.getAccess().intValue());
    Assert.assertEquals("<init>", method.getName());

    Assert.assertTrue(method.getParameters().isEmpty());
    Assert.assertEquals(
        metaService.getClassByCanonicalName(TypeConstants.VOID),
        getClass(method.getReturn()));
    Assert.assertTrue(method.getExceptions().isEmpty());
    Assert.assertEquals("()V", method.getCanonicalDescription());
    Assert.assertNull(method.getCanonicalSignature());

    Assert.assertEquals(method, metaService.getMethodById(id));
  }

  //<im|2|0|9|main|([Ljava/lang/String;)V|||>
  @Test
  public void testMethodWithParam() {
    final long metaId = 2l;
    IMethod method = metaService.getMethodByMetaId(metaId);
    Assert.assertTrue(method.getId().getId() > 0);
    Assert.assertEquals(metaId, method.getMetaId().longValue());
    Assert.assertEquals(0l, getClassMetaId(method));
    Assert.assertEquals(9, method.getAccess().intValue());
    Assert.assertEquals("main", method.getName());

    Assert.assertEquals(1, method.getParameters().size());
    Assert.assertEquals(
        metaService.getClassByCanonicalName("[Ljava/lang/String;"),
        getClass(method.getParameters().get(0)));
    Assert.assertEquals(
        metaService.getClassByCanonicalName(TypeConstants.VOID),
        getClass(method.getReturn()));
    Assert.assertTrue(method.getExceptions().isEmpty());
    Assert.assertEquals("([Ljava/lang/String;)V",
        method.getCanonicalDescription());
    Assert.assertNull(method.getCanonicalSignature());
  }

  //<im|11|0|2|aMethod|(Ljava/lang/Runnable;DJ)Ljava/lang/String;|||>
  @Test
  public void testMethodWithMultipleParamAndReturn() {
    final long metaId = 11l;
    IMethod method = metaService.getMethodByMetaId(metaId);
    Assert.assertTrue(method.getId().getId() > 0);
    Assert.assertEquals(metaId, method.getMetaId().longValue());
    Assert.assertEquals(0l, getClassMetaId(method));
    Assert.assertEquals(2, method.getAccess().intValue());
    Assert.assertEquals("aMethod", method.getName());

    Assert.assertEquals(3, method.getParameters().size());
    Assert.assertEquals(
        metaService.getClassByCanonicalName("Ljava/lang/Runnable;"),
        getClass(method.getParameters().get(0)));
    Assert.assertEquals(metaService.getClassByCanonicalName("D"),
        getClass(method.getParameters().get(1)));
    Assert.assertEquals(metaService.getClassByCanonicalName("J"),
        getClass(method.getParameters().get(2)));

    Assert.assertEquals(
        metaService.getClassByCanonicalName("Ljava/lang/String;"),
        getClass(method.getReturn()));
    Assert.assertTrue(method.getExceptions().isEmpty());
    Assert.assertEquals("(Ljava/lang/Runnable;DJ)Ljava/lang/String;",
        method.getCanonicalDescription());
    Assert.assertNull(method.getCanonicalSignature());
  }

  //<im|12|0|1|anotherMethod|()Z||java/lang/IllegalArgumentException,java/lang/UnsupportedOperationException,|>
  @Test
  public void testMethodWithExceptions() {
    final long metaId = 12l;
    IMethod method = metaService.getMethodByMetaId(metaId);
    Assert.assertTrue(method.getId().getId() > 0);
    Assert.assertEquals(metaId, method.getMetaId().longValue());
    Assert.assertEquals(0l, getClassMetaId(method));
    Assert.assertEquals(1, method.getAccess().intValue());
    Assert.assertEquals("anotherMethod", method.getName());

    Assert.assertTrue(method.getParameters().isEmpty());
    Assert.assertEquals(metaService.getClassByCanonicalName("Z"),
        getClass(method.getReturn()));

    Assert.assertEquals(2, method.getExceptions().size());
    {
      IClass clazz = metaService.getClassById(method.getExceptions().get(0)
          .getId());
      Assert.assertTrue(clazz.getId().getId() > 0);
      Assert.assertNull(clazz.getMetaId());
      Assert.assertNull(clazz.getAccess());
      Assert.assertEquals(ClassType.CLASS, clazz.getClassType());
      Assert.assertEquals("java.lang.IllegalArgumentException",
          clazz.getCompleteName());
    }
    {
      IClass clazz = metaService.getClassById(method.getExceptions().get(1)
          .getId());
      Assert.assertTrue(clazz.getId().getId() > 0);
      Assert.assertNull(clazz.getMetaId());
      Assert.assertNull(clazz.getAccess());
      Assert.assertEquals(ClassType.CLASS, clazz.getClassType());
      Assert.assertEquals("java.lang.UnsupportedOperationException",
          clazz.getCompleteName());
    }
    Assert.assertEquals("()Z", method.getCanonicalDescription());
    Assert.assertNull(method.getCanonicalSignature());
  }
}
