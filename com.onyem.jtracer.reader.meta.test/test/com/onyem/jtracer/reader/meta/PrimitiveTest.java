package com.onyem.jtracer.reader.meta;

import junit.framework.Assert;

import org.junit.Test;

import com.onyem.jtracer.reader.meta.internal.TypeConstants;

public class PrimitiveTest extends AbstractMetaTest implements TypeConstants {

  @Override
  protected String getMetaPath() {
    return "/test-data/meta1.txt";
  }

  @Test
  public void getPrimitives() {
    String[] primitiveCanonicalNames = new String[] { BYTE, CHAR, DOUBLE,
        FLOAT, INT, LONG, SHORT, BOOLEAN };
    String[] primitiveNames = new String[] { BYTE_NAME, CHAR_NAME, DOUBLE_NAME,
        FLOAT_NAME, INT_NAME, LONG_NAME, SHORT_NAME, BOOLEAN_NAME, };

    for (int i = 0; i < primitiveNames.length; i++) {
      IClass primitive = metaService
          .getClassByCanonicalName(primitiveCanonicalNames[i]);
      Assert.assertTrue(primitive.getId().getId() > 0);
      Assert.assertEquals(primitiveNames[i], primitive.getSimpleName());
      Assert.assertEquals(primitiveNames[i], primitive.getCompleteName());
      Assert.assertEquals(primitiveCanonicalNames[i],
          primitive.getCanonicalName());

      Assert.assertNull(primitive.getMetaId());
      Assert.assertNull(primitive.getAccess());
      Assert.assertEquals(ClassType.PRIMITIVE, primitive.getClassType());
      Assert.assertNull(primitive.getClassName());
      Assert.assertNull(primitive.getPackageName());
      Assert.assertNull(primitive.getSuperClass());
      Assert.assertNull(primitive.getInterfaces());
      Assert.assertNull(primitive.getComponentType());
    }
  }

  @Test
  public void getVoid() {
    String canonicalName = TypeConstants.VOID;
    String name = TypeConstants.VOID_NAME;
    IClass primitive = metaService.getClassByCanonicalName(canonicalName);
    Assert.assertTrue(primitive.getId().getId() > 0);
    Assert.assertEquals(name, primitive.getSimpleName());
    Assert.assertEquals(name, primitive.getCompleteName());
    Assert.assertEquals(canonicalName, primitive.getCanonicalName());

    Assert.assertNull(primitive.getMetaId());
    Assert.assertNull(primitive.getAccess());
    Assert.assertEquals(ClassType.VOID, primitive.getClassType());
    Assert.assertNull(primitive.getClassName());
    Assert.assertNull(primitive.getPackageName());
    Assert.assertNull(primitive.getSuperClass());
    Assert.assertNull(primitive.getInterfaces());
    Assert.assertNull(primitive.getComponentType());
  }
}
