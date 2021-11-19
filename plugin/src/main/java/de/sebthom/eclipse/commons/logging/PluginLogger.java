/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.logging;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

import de.sebthom.eclipse.commons.AbstractEclipsePlugin;
import net.sf.jstuff.core.logging.Logger;

/**
 * @author Sebastian Thomschke
 */
public class PluginLogger {

   private final Logger fileLog;
   private final ILog uiLog;
   private StatusFactory statusFactory;

   public PluginLogger(final AbstractEclipsePlugin plugin) {
      fileLog = Logger.create(plugin.getPluginId());
      uiLog = Platform.getLog(plugin.getBundle());
      statusFactory = plugin.getStatusFactory();
   }

   public PluginLogger(final Bundle bundle) {
      fileLog = Logger.create(bundle.getSymbolicName());
      uiLog = Platform.getLog(bundle);
      statusFactory = new StatusFactory(bundle);
   }

   public PluginLogger(final Plugin plugin) {
      final var bundle = plugin.getBundle();
      fileLog = Logger.create(bundle.getSymbolicName());
      uiLog = Platform.getLog(bundle);
      statusFactory = new StatusFactory(bundle);
   }

   public void addLogListener(final ILogListener listener) {
      uiLog.addLogListener(listener);
   }

   public void debug(@Nullable final String msg, final Object... msgArgs) {
      fileLog.info(NLS.bind(msg, msgArgs));
   }

   public void debug(final Throwable ex) {
      fileLog.info(ex);
   }

   public void debug(final Throwable ex, @Nullable final String msg, final Object... msgArgs) {
      fileLog.info(ex, NLS.bind(msg, msgArgs));
   }

   public IStatus error(@Nullable final Object msg) {
      final var status = statusFactory.createError(msg == null ? null : msg.toString());
      uiLog.log(status);
      return status;
   }

   public IStatus error(@Nullable final String msg, final Object... msgArgs) {
      final var status = statusFactory.createError(msg, msgArgs);
      uiLog.log(status);
      return status;
   }

   public IStatus error(final Throwable ex) {
      final var status = statusFactory.createError(ex);
      uiLog.log(status);
      return status;
   }

   public IStatus error(final Throwable ex, @Nullable final String msg, final Object... msgArgs) {
      final var status = statusFactory.createError(ex, msg, msgArgs);
      uiLog.log(status);
      return status;
   }

   public Bundle getBundle() {
      return uiLog.getBundle();
   }

   public IStatus info(@Nullable final Object msg) {
      final var status = statusFactory.createInfo(msg == null ? null : msg.toString());
      uiLog.log(status);
      return status;
   }

   public IStatus info(@Nullable final String msg, final Object... msgArgs) {
      final var status = statusFactory.createInfo(msg, msgArgs);
      uiLog.log(status);
      return status;
   }

   public IStatus info(final Throwable ex, @Nullable final String msg, final Object... msgArgs) {
      final var status = statusFactory.createInfo(ex, msg, msgArgs);
      uiLog.log(status);
      return status;
   }

   public void log(final IStatus status) {
      uiLog.log(status);
   }

   public void removeLogListener(final ILogListener listener) {
      uiLog.removeLogListener(listener);
   }

   public IStatus warn(@Nullable final Object msg) {
      final var status = statusFactory.createWarning(msg == null ? null : msg.toString());
      uiLog.log(status);
      return status;
   }

   public IStatus warn(@Nullable final String msg, final Object... msgArgs) {
      final var status = statusFactory.createWarning(msg, msgArgs);
      uiLog.log(status);
      return status;
   }

   public IStatus warn(final Throwable ex) {
      final var status = statusFactory.createWarning(ex);
      uiLog.log(status);
      return status;
   }

   public IStatus warn(final Throwable ex, @Nullable final String msg, final Object... msgArgs) {
      final var status = statusFactory.createWarning(ex, msg, msgArgs);
      uiLog.log(status);
      return status;
   }
}
