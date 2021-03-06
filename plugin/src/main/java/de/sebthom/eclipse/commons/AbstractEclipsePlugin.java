/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

import de.sebthom.eclipse.commons.logging.PluginLogger;
import de.sebthom.eclipse.commons.logging.StatusFactory;
import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.validation.Args;

/**
 * @author Sebastian Thomschke
 */
public abstract class AbstractEclipsePlugin extends AbstractUIPlugin {

   @NonNullByDefault({})
   protected BundleResources bundleResources;

   @NonNullByDefault({})
   protected PluginLogger logger;

   @NonNullByDefault({})
   protected IPersistentPreferenceStore preferenceStore;

   @NonNullByDefault({})
   protected StatusFactory statusFactory;

   protected final String pluginId = getBundle().getSymbolicName();

   public BundleResources getBundleResources() {
      if (bundleResources == null) {
         bundleResources = new BundleResources(this);
      }
      return bundleResources;
   }

   public ImageDescriptor getImageDescriptor(final String path) {
      return imageDescriptorFromPlugin(getBundle().getSymbolicName(), path);
   }

   public PluginLogger getLogger() {
      if (logger == null) {
         logger = new PluginLogger(this);
      }
      return logger;
   }

   public String getPluginId() {
      return pluginId;
   }

   @Override
   public IPersistentPreferenceStore getPreferenceStore() {
      if (preferenceStore == null) {
         preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, pluginId);
      }
      return preferenceStore;
   }

   @Nullable
   public Image getSharedImage(final String path) {
      Args.notNull("path", path);

      var image = getImageRegistry().get(path);
      if (image == null) {
         final var descriptor = getImageDescriptor(path);
         getImageRegistry().put(path, descriptor);
         image = getImageRegistry().get(path);
      }
      return image;
   }

   @Nullable
   public ImageDescriptor getSharedImageDescriptor(final String path) {
      Args.notNull("path", path);

      return imageDescriptorFromPlugin(getBundle().getSymbolicName(), path);
   }

   protected void registerImage(final ImageRegistry registry, final String path) {
      if (path.startsWith("platform:/plugin/")) {
         final var sourcePluginId = Strings.substringBetween(path, "platform:/plugin/", "/");
         final var imagePath = Strings.substringAfter(path, sourcePluginId);
         registry.put(path, imageDescriptorFromPlugin(sourcePluginId, imagePath));
         return;
      }

      final var url = getBundleResources().getURL(path);
      final var desc = ImageDescriptor.createFromURL(url);
      registry.put(path, desc);
   }

   public StatusFactory getStatusFactory() {
      if (statusFactory == null) {
         statusFactory = new StatusFactory(this);
      }
      return statusFactory;
   }

   @Override
   public void start(@NonNullByDefault({}) final BundleContext context) throws Exception {
      getLogger().info("starting...");
      super.start(context);
   }

   @Override
   public void stop(@NonNullByDefault({}) final BundleContext context) throws Exception {
      getLogger().info("stopping...");
      super.stop(context);
   }
}
