package com.onyem.jtracer.reader.meta;

import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class MergeClassTest extends AbstractMetaTest {

  @Override
  protected String getMetaPath() {
    return "/test-data/meta1.txt";
  }

  //<ic|0|50|33|org/world/HelloWorld||java/lang/Object|org/world/Marker,org/world/Talker,|>
  @Test
  public void testClassMerge() {

    IClass existingClass = metaService
        .getClassByCanonicalName("Lorg/world/HelloWorld;");
    {
      Assert.assertTrue(existingClass.getId().getId() > 0);
      Assert.assertNull(existingClass.getMetaId());
      Assert.assertNull(existingClass.getAccess());
      Assert.assertEquals(ClassType.CLASS, existingClass.getClassType());
      Assert.assertEquals("org.world", existingClass.getPackageName());
      Assert.assertEquals("HelloWorld", existingClass.getClassName());
      Assert.assertNull(existingClass.getComponentType());
      Assert.assertNull(existingClass.getCanonicalSignature());

      Assert.assertEquals("HelloWorld", existingClass.getSimpleName());
      Assert.assertEquals("org.world.HelloWorld",
          existingClass.getCompleteName());
      Assert.assertEquals("Lorg/world/HelloWorld;",
          existingClass.getCanonicalName());
    }

    final long metaId = 0l;
    IClass clazz = metaService.getClassByMetaId(metaId);
    {
      Assert.assertTrue(clazz.getId().getId() == existingClass.getId().getId());
      Assert.assertEquals(metaId, clazz.getMetaId().longValue());
      Assert.assertEquals(33, clazz.getAccess().longValue());
      Assert.assertEquals(ClassType.CLASS, clazz.getClassType());
      Assert.assertEquals("org.world", clazz.getPackageName());
      Assert.assertEquals("HelloWorld", clazz.getClassName());
      Assert.assertNull(clazz.getComponentType());
      Assert.assertNull(clazz.getCanonicalSignature());

      Assert.assertEquals("HelloWorld", clazz.getSimpleName());
      Assert.assertEquals("org.world.HelloWorld", clazz.getCompleteName());
      Assert.assertEquals("Lorg/world/HelloWorld;", clazz.getCanonicalName());
    }
    {
      IClass superClass = metaService.getClassById(clazz.getSuperClass()
          .getId());
      assertClass(superClass);
    }
    Set<ClassId> interfaceIds = clazz.getInterfaces();
    Set<IClass> interfaces = new LinkedHashSet<IClass>();
    for (ClassId classId : interfaceIds) {
      interfaces.add(metaService.getClassById(classId.getId()));
    }

    String[][] interfaceInfo = new String[][] {
        new String[] { "org.world.Marker", "org.world", "Marker", "Marker",
            "Lorg/world/Marker;" },
        new String[] { "org.world.Talker", "org.world", "Talker", "Talker",
            "Lorg/world/Talker;" } };
    for (int i = 0; i < interfaceInfo.length; i++) {
      IClass anInterface = getInterface(interfaces, interfaceInfo[i][0]);
      Assert.assertTrue(anInterface.getId().getId() > 0);
      Assert.assertNull(anInterface.getMetaId());
      Assert.assertNull(anInterface.getAccess());
      Assert.assertEquals(ClassType.INTERFACE, anInterface.getClassType());
      Assert.assertNull(anInterface.getComponentType());
      Assert.assertEquals(interfaceInfo[i][1], anInterface.getPackageName());
      Assert.assertEquals(interfaceInfo[i][2], anInterface.getClassName());
      Assert.assertNull(anInterface.getComponentType());
      Assert.assertNull(anInterface.getCanonicalSignature());

      Assert.assertEquals(interfaceInfo[i][3], anInterface.getSimpleName());
      Assert.assertEquals(interfaceInfo[i][0], anInterface.getCompleteName());
      Assert.assertEquals(interfaceInfo[i][4], anInterface.getCanonicalName());
    }
  }

  public static void assertClass(IClass objectClass) {
    Assert.assertTrue(objectClass.getId().getId() > 0);
    Assert.assertNull(objectClass.getMetaId());
    Assert.assertNull(objectClass.getAccess());
    Assert.assertEquals(ClassType.CLASS, objectClass.getClassType());
    Assert.assertEquals("java.lang", objectClass.getPackageName());
    Assert.assertEquals("Object", objectClass.getClassName());
    Assert.assertNull(objectClass.getComponentType());
    Assert.assertNull(objectClass.getCanonicalSignature());

    Assert.assertEquals("Object", objectClass.getSimpleName());
    Assert.assertEquals("java.lang.Object", objectClass.getCompleteName());
    Assert.assertEquals("Ljava/lang/Object;", objectClass.getCanonicalName());
  }

  private IClass getInterface(Set<IClass> interfaces, String name) {
    for (IClass iClass : interfaces) {
      if (iClass.getCompleteName().equals(name)) {
        return iClass;
      }
    }
    return null;
  }
}
