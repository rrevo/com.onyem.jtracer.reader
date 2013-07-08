package com.onyem.jtracer.reader.meta;

import junit.framework.Assert;

import org.junit.Test;

public class SignatureTest extends AbstractMetaTest {

  @Override
  protected String getMetaPath() {
    return "/test-data/meta2.txt";
  }

  //<ic|0|50|33|org/world/Signature|<P:Ljava/lang/Object;>Ljava/lang/Object;|java/lang/Object||>
  //<im|2|0|1|aMethod|(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;|<Q:Ljava/lang/Object;>(TP;TQ;)TQ;||>
  @Test
  public void testMethodByMetaId() {
    final long methodMetaId = 2l;
    IMethod method = metaService.getMethodByMetaId(methodMetaId);
    {
      Assert.assertTrue(method.getId().getId() > 0);
      Assert.assertEquals(methodMetaId, method.getMetaId().longValue());
      Assert.assertEquals(1, method.getAccess().intValue());
      Assert.assertEquals("aMethod", method.getName());

      Assert.assertEquals(2, method.getParameters().size());
      MetaClassTest.assertClass(getClass(method.getParameters().get(0)));
      MetaClassTest.assertClass(getClass(method.getParameters().get(1)));
      MetaClassTest.assertClass(getClass(method.getReturn()));
      Assert.assertTrue(method.getExceptions().isEmpty());
      Assert.assertEquals(
          "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
          method.getCanonicalDescription());
      Assert.assertEquals("<Q:Ljava/lang/Object;>(TP;TQ;)TQ;",
          method.getCanonicalSignature());
    }
    {
      IClass clazz = getClass(method.getIClass());
      Assert.assertEquals("<P:Ljava/lang/Object;>Ljava/lang/Object;",
          clazz.getCanonicalSignature());
    }
  }
}
