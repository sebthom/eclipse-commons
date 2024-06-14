/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
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
      this(plugin.getBundle());
   }

   public void addLogListener(final ILogListener listener) {
      uiLog.addLogListener(listener);
   }

   public void debug(final @Nullable String msg, final @Nullable Object... msgArgs) {
      fileLog.info(NLS.bind(msg, msgArgs));
   }

   public void debug(final Throwable ex) {
      fileLog.info(ex);
   }

   public void debug(final Throwable ex, final @Nullable String msg, final @Nullable Object... msgArgs) {
      fileLog.info(ex, NLS.bind(msg, msgArgs));
   }

   public IStatus error(final @Nullable Object msg) {
      final var status = statusFactory.createError(msg == null ? null : msg.toString());
      uiLog.log(status);
      return status;
   }

   public IStatus error(final @Nullable String msg, final @Nullable Object... msgArgs) {
      final var status = statusFactory.createError(msg, msgArgs);
      uiLog.log(status);
      return status;
   }

   public IStatus error(final Throwable ex) {
      final var status = statusFactory.createError(ex);
      uiLog.log(status);
      return status;
   }

   public IStatus error(final @Nullable Throwable ex, final @Nullable String msg, final @Nullable Object... msgArgs) {
      final var status = statusFactory.createError(ex, msg, msgArgs);
      uiLog.log(status);
      return status;
   }

   public Bundle getBundle() {
      return uiLog.getBundle();
   }

   public IStatus info(final @Nullable Object msg) {
      final var status = statusFactory.createInfo(msg == null ? null : msg.toString());
      uiLog.log(status);
      return status;
   }

   public IStatus info(final @Nullable String msg, final @Nullable Object... msgArgs) {
      final var status = statusFactory.createInfo(msg, msgArgs);
      uiLog.log(status);
      return status;
   }

   public IStatus info(final Throwable ex, final @Nullable String msg, final @Nullable Object... msgArgs) {
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

   public IStatus warn(final @Nullable Object msg) {
      final var status = statusFactory.createWarning(msg == null ? null : msg.toString());
      uiLog.log(status);
      return status;
   }

   public IStatus warn(final @Nullable String msg, final @Nullable Object... msgArgs) {
      final var status = statusFactory.createWarning(msg, msgArgs);
      uiLog.log(status);
      return status;
   }

   public IStatus warn(final Throwable ex) {
      final var status = statusFactory.createWarning(ex);
      uiLog.log(status);
      return status;
   }

   public IStatus warn(final Throwable ex, final @Nullable String msg, final @Nullable Object... msgArgs) {
      final var status = statusFactory.createWarning(ex, msg, msgArgs);
      uiLog.log(status);
      return status;
   }
}
