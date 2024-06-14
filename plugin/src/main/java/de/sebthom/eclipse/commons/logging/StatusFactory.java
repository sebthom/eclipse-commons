/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.logging;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

import de.sebthom.eclipse.commons.AbstractEclipsePlugin;
import net.sf.jstuff.core.Strings;

/**
 * @author Sebastian Thomschke
 */
public class StatusFactory {

   private final String pluginId;

   public StatusFactory(final AbstractEclipsePlugin plugin) {
      pluginId = plugin.getPluginId();
   }

   public StatusFactory(final Bundle bundle) {
      pluginId = bundle.getSymbolicName();
   }

   public StatusFactory(final Plugin plugin) {
      this(plugin.getBundle());
   }

   public IStatus createError(final @Nullable String msg, final @Nullable Object... msgArgs) {
      return createStatus(IStatus.ERROR, msg, msgArgs);
   }

   public IStatus createError(final Throwable ex) {
      return createStatus(IStatus.ERROR, ex, ex.getMessage());
   }

   public IStatus createError(final @Nullable Throwable ex, final @Nullable String msg, final @Nullable Object... msgArgs) {
      return createStatus(IStatus.ERROR, ex, msg, msgArgs);
   }

   public IStatus createInfo(final @Nullable String msg, final @Nullable Object... msgArgs) {
      return createStatus(IStatus.INFO, msg, msgArgs);
   }

   public IStatus createInfo(final Throwable ex) {
      return createStatus(IStatus.INFO, ex, ex.getMessage());
   }

   public IStatus createInfo(final Throwable ex, final @Nullable String msg, final @Nullable Object... msgArgs) {
      return createStatus(IStatus.INFO, ex, msg, msgArgs);
   }

   public IStatus createStatus(final int severity, final @Nullable String msg, final @Nullable Object... msgArgs) {
      if (msgArgs.length == 0)
         return new Status(severity, pluginId, msg);
      return new Status(severity, pluginId, interpolateMessage(msg, msgArgs));
   }

   public IStatus createStatus(final int severity, final @Nullable Throwable ex, final @Nullable String msg,
         final @Nullable Object... msgArgs) {
      final var statusMsg = Strings.isNotEmpty(msg) //
            ? interpolateMessage(msg, msgArgs) //
            : ex == null //
                  ? null //
                  : Strings.isEmpty(ex.getMessage()) //
                        ? ex.getClass().getName() //
                        : ex.getMessage();

      if (statusMsg == null)
         throw new IllegalArgumentException("[ex] or [msg] must be specified");

      return new Status(severity, pluginId, severity, statusMsg, ex);
   }

   public IStatus createWarning(final @Nullable String msg, final @Nullable Object... msgArgs) {
      return createStatus(IStatus.WARNING, msg, msgArgs);
   }

   public IStatus createWarning(final Throwable ex) {
      return createStatus(IStatus.WARNING, ex, ex.getMessage());
   }

   public IStatus createWarning(final Throwable ex, final @Nullable String msg, final @Nullable Object... msgArgs) {
      return createStatus(IStatus.WARNING, ex, msg, msgArgs);
   }

   private @Nullable String interpolateMessage(final @Nullable String msg, final @Nullable Object... msgArgs) {
      if (msgArgs.length == 0 || Strings.isEmpty(msg))
         return msg;
      return NLS.bind(msg, msgArgs);
   }
}
