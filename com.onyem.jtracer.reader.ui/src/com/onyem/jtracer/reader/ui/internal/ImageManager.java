package com.onyem.jtracer.reader.ui.internal;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;

import com.onyem.jtracer.reader.ui.Activator;
import com.onyem.jtracer.reader.ui.IImageManager;

public class ImageManager implements IImageManager {

  private static final ImageRegistry IMAGE_REGISTRY = JFaceResources
      .getImageRegistry();

  ImageManager() {
    String[] imageNames = new String[] { METHOD_ENTRY, METHOD_EXIT,
        EXCEPTION_CATCH, EXCEPTION_THROW, WARNING, LOGO };

    for (String imageName : imageNames) {
      URL url = Platform.getBundle(Activator.PLUGIN_ID).getEntry(
          "/icons/" + imageName + ".gif");
      ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
      IMAGE_REGISTRY.put(imageName, imageDescriptor);
    }
  }

  @Override
  public Image getImage(String name) {
    return IMAGE_REGISTRY.get(name);
  }
}
