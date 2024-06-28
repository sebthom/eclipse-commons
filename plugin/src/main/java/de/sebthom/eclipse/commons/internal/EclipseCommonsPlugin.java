/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.internal;

import org.eclipse.jdt.annotation.Nullable;
import org.osgi.framework.BundleContext;

import de.sebthom.eclipse.commons.AbstractEclipsePlugin;
import de.sebthom.eclipse.commons.logging.PluginLogger;
import net.sf.jstuff.core.validation.Assert;

/**
 * @author Sebastian Thomschke
 */
public class EclipseCommonsPlugin extends AbstractEclipsePlugin {

   private static volatile @Nullable EclipseCommonsPlugin instance;

   /**
    * @return the shared instance
    */
   public static EclipseCommonsPlugin get() {
      return Assert.notNull(instance, "Default plugin instance is still null.");
   }

   public static PluginLogger log() {
      return get().getLogger();
   }

   @Override
   public void start(final BundleContext context) throws Exception {
      super.start(context);
      instance = this;

   }

   @Override
   public void stop(final BundleContext context) throws Exception {
      instance = null;
      super.stop(context);
   }
}
