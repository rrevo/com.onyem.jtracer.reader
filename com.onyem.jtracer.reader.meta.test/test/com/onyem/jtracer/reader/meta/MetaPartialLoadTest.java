package com.onyem.jtracer.reader.meta;

import org.junit.Assert;
import org.junit.Test;

public class MetaPartialLoadTest extends AbstractMetaTest {

  @Override
  protected String getMetaPath() {
    return "/test-data/meta1.txt";
  }

  @Test
  public void testPlainClassByNameInsertAndSearch() {
    // Insert the class
    IClass clazz = metaService.getPlainClassByName("org.world.NewClass");
    long id = clazz.getId();
    assertNewClass(clazz);

    // Search for the class
    clazz = metaService.getPlainClassByName("org.world.NewClass");
    Assert.assertEquals(id, clazz.getId());
    assertNewClass(clazz);
  }

  private void assertNewClass(IClass clazz) {
    Assert.assertTrue(clazz.getId() > 0);
    Assert.assertNull(clazz.getMetaId());
    Assert.assertNull(clazz.getAccess());
    Assert.assertEquals(ClassType.CLASS, clazz.getClassType());
    Assert.assertEquals("org.world", clazz.getPackageName());
    Assert.assertEquals("NewClass", clazz.getClassName());
    Assert.assertNull(clazz.getComponentType());
    Assert.assertNull(clazz.getCanonicalSignature());

    Assert.assertEquals("NewClass", clazz.getSimpleName());
    Assert.assertEquals("org.world.NewClass", clazz.getCompleteName());
    Assert.assertEquals("Lorg/world/NewClass;", clazz.getCanonicalName());
  }

  @Test
  public void testMethodByNameSearch() {
    final long metaId = 12l;
    IMethod loadedMethod = metaService.getMethodByMetaId(metaId);

    IMethod method = metaService.getMethodByNameDescription("anotherMethod",
        "()Z", loadedMethod.getIClass());

    Assert.assertEquals(loadedMethod, method);
  }

  @Test
  public void testMethodByNameInsert() {
    IClass clazz = metaService.getPlainClassByName("AClass");
    IMethod method = metaService.getMethodByNameDescription("foo",
        "(ILfoo.bar.Baz;)V", clazz);

    Assert.assertTrue(method.getId() > 0);
    Assert.assertNull(method.getMetaId());
    Assert.assertEquals(clazz, method.getIClass());
    Assert.assertNull(method.getAccess());
    Assert.assertEquals("foo", method.getName());

    Assert.assertEquals(2, method.getParameters().size());
    Assert.assertEquals(metaService.getClassByCanonicalName("I"), method
        .getParameters().get(0));
    Assert.assertEquals(metaService.getClassByCanonicalName("Lfoo.bar.Baz;"),
        method.getParameters().get(1));

    Assert.assertEquals(metaService.getClassByCanonicalName("V"),
        method.getReturn());
    Assert.assertTrue(method.getExceptions().isEmpty());
    Assert.assertEquals("(ILfoo.bar.Baz;)V", method.getCanonicalDescription());
    Assert.assertNull(method.getCanonicalSignature());
  }

}
