/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNull;

import org.eclipse.core.runtime.Platform;
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
      var bundleResources = this.bundleResources;
      if (bundleResources == null) {
         bundleResources = this.bundleResources = new BundleResources(this);
      }
      return bundleResources;
   }

   public @Nullable ImageDescriptor getImageDescriptor(final String path) {
      return imageDescriptorFromPlugin(pluginId, path);
   }

   public PluginLogger getLogger() {
      var logger = this.logger;
      if (logger == null) {
         logger = this.logger = new PluginLogger(this);
      }
      return logger;
   }

   public String getPluginId() {
      return pluginId;
   }

   @Override
   public IPersistentPreferenceStore getPreferenceStore() {
      var preferenceStore = this.preferenceStore;
      if (preferenceStore == null) {
         preferenceStore = this.preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, pluginId) {
            @Override
            public boolean getBoolean(final String name) {
               return Platform.getPreferencesService().getBoolean(pluginId, name, getDefaultBoolean(name),
                  null /* = search in all available scopes */);
            }

            @Override
            public double getDouble(final String name) {
               return Platform.getPreferencesService().getDouble(pluginId, name, getDefaultDouble(name),
                  null /* = search in all available scopes */);
            }

            @Override
            public float getFloat(final String name) {
               return Platform.getPreferencesService().getFloat(pluginId, name, getDefaultFloat(name),
                  null /* = search in all available scopes */);
            }

            @Override
            public int getInt(final String name) {
               return Platform.getPreferencesService().getInt(pluginId, name, getDefaultInt(name),
                  null /* = search in all available scopes */);
            }

            @Override
            public long getLong(final String name) {
               return Platform.getPreferencesService().getLong(pluginId, name, getDefaultLong(name),
                  null /* = search in all available scopes */);
            }

            @Override
            public String getString(final String name) {
               return asNonNull(Platform.getPreferencesService().getString(pluginId, name, getDefaultString(name),
                  null /* = search in all available scopes */));
            }
         };
      }
      return preferenceStore;
   }

   public @Nullable Image getSharedImage(final String path) {
      Args.notNull("path", path);

      var image = getImageRegistry().get(path);
      if (image == null) {
         final var descriptor = getImageDescriptor(path);
         getImageRegistry().put(path, descriptor);
         image = getImageRegistry().get(path);
      }
      return image;
   }

   public @Nullable ImageDescriptor getSharedImageDescriptor(final String path) {
      Args.notNull("path", path);

      return imageDescriptorFromPlugin(pluginId, path);
   }

   public StatusFactory getStatusFactory() {
      var statusFactory = this.statusFactory;
      if (statusFactory == null) {
         statusFactory = this.statusFactory = new StatusFactory(this);
      }
      return statusFactory;
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

   @Override
   public void start(final BundleContext context) throws Exception {
      getLogger().info("starting...");
      super.start(context);
   }

   @Override
   public void stop(final BundleContext context) throws Exception {
      getLogger().info("stopping...");
      super.stop(context);
   }
}
