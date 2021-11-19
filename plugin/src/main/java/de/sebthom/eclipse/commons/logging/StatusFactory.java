/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.logging;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

import net.sf.jstuff.core.Strings;

/**
 * @author Sebastian Thomschke
 */
public class StatusFactory {

   private final String pluginId;

   public StatusFactory(final Bundle bundle) {
      pluginId = bundle.getSymbolicName();
   }

   public StatusFactory(final Plugin plugin) {
      pluginId = plugin.getBundle().getSymbolicName();
   }

   public IStatus createError(@Nullable final String msg, final Object... msgArgs) {
      return createStatus(IStatus.ERROR, msg, msgArgs);
   }

   public IStatus createError(final Throwable ex) {
      return createStatus(IStatus.ERROR, ex, ex.getMessage());
   }

   public IStatus createError(final Throwable ex, @Nullable final String msg, final Object... msgArgs) {
      return createStatus(IStatus.ERROR, ex, msg, msgArgs);
   }

   public IStatus createInfo(@Nullable final String msg, final Object... msgArgs) {
      return createStatus(IStatus.INFO, msg, msgArgs);
   }

   public IStatus createInfo(final Throwable ex) {
      return createStatus(IStatus.INFO, ex, ex.getMessage());
   }

   public IStatus createInfo(final Throwable ex, @Nullable final String msg, final Object... msgArgs) {
      return createStatus(IStatus.INFO, ex, msg, msgArgs);
   }

   public IStatus createStatus(final int severity, @Nullable final String msg, final Object... msgArgs) {
      if (msgArgs == null || msgArgs.length == 0)
         return new Status(severity, pluginId, msg);
      return new Status(severity, pluginId, NLS.bind(msg, msgArgs));
   }

   public IStatus createStatus(final int severity, @Nullable final Throwable ex, @Nullable final String msg, final Object... msgArgs) {
      final var statusMsg = Strings.isNotEmpty(msg) //
         ? NLS.bind(msg, msgArgs) //
         : ex == null //
            ? null //
            : Strings.isEmpty(ex.getMessage()) //
               ? ex.getClass().getName() //
               : ex.getMessage();

      if (statusMsg == null)
         throw new IllegalArgumentException("[ex] or [msg] must be specified");

      return new Status(severity, pluginId, severity, statusMsg, ex);
   }

   public IStatus createWarning(@Nullable final String msg, final Object... msgArgs) {
      return createStatus(IStatus.WARNING, msg, msgArgs);
   }

   public IStatus createWarning(final Throwable ex) {
      return createStatus(IStatus.WARNING, ex, ex.getMessage());
   }

   public IStatus createWarning(final Throwable ex, @Nullable final String msg, final Object... msgArgs) {
      return createStatus(IStatus.WARNING, ex, msg, msgArgs);
   }
}
