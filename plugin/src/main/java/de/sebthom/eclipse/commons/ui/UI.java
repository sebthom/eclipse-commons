/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Sebastian Thomschke
 */
public abstract class UI {

   public static void center(final Shell shell) {
      final Rectangle parentBounds;
      if (shell.getParent() == null) {
         parentBounds = shell.getDisplay().getBounds();
      } else {
         parentBounds = shell.getParent().getBounds();
      }
      final var shellBounds = shell.getBounds();
      final var x = (parentBounds.width - shellBounds.width) / 2 + parentBounds.x;
      final var y = (parentBounds.height - shellBounds.height) / 2 + parentBounds.y;
      shell.setLocation(new Point(x, y));
   }

   public static Display getDisplay() {
      var display = PlatformUI.getWorkbench().getDisplay();
      if (display != null)
         return display;

      display = Display.getCurrent();
      if (display != null)
         return display;

      return Display.getDefault();
   }

   @Nullable
   public static Shell getShell() {
      final var window = UI.getWorkbenchWindow();
      return window == null ? null : window.getShell();
   }

   @Nullable
   public static IStatusLineManager getStatusLine(final IWorkbenchWindow window) {
      final var page = window.getActivePage();

      final var editorSite = page.getActiveEditor().getEditorSite();
      if (editorSite != null)
         return editorSite.getActionBars().getStatusLineManager();

      final var part = page.getActivePart();
      if (part instanceof IViewSite)
         return ((IViewSite) part).getActionBars().getStatusLineManager();

      return null;
   }

   @Nullable
   public static IWorkbenchPage getWorkbenchPage() {
      final var window = getWorkbenchWindow();
      return window == null ? null : window.getActivePage();
   }

   @Nullable
   public static IWorkbenchWindow getWorkbenchWindow() {
      final var workbench = PlatformUI.getWorkbench();
      final var window = workbench.getActiveWorkbenchWindow();
      if (window == null) {
         final var windows = workbench.getWorkbenchWindows();
         return windows.length == 0 ? null : windows[0];
      }
      return window;
   }

   public static boolean isUIThread() {
      return Display.getCurrent() != null;
   }

   public static void maximize(final IWorkbenchPart part) {
      final var site = part.getSite();
      final var handlerService = site.getService(IHandlerService.class);
      try {
         handlerService.executeCommand(IWorkbenchCommandConstants.WINDOW_MAXIMIZE_ACTIVE_VIEW_OR_EDITOR, null);
      } catch (final Exception ex) {
         ex.printStackTrace();
      }
   }

   public static void maximize(final IWorkbenchWindow window) {
      final var handlerService = window.getWorkbench().getService(IHandlerService.class);
      try {
         handlerService.executeCommand(IWorkbenchCommandConstants.WINDOW_MAXIMIZE_ACTIVE_VIEW_OR_EDITOR, null);
      } catch (final Exception ex) {
         ex.printStackTrace();
      }
   }

   @Nullable
   public static IViewPart openProgressView() {
      try {
         final var page = getWorkbenchPage();
         if (page != null)
            return page.showView(IProgressConstants.PROGRESS_VIEW_ID);
      } catch (final Exception ex) {
         ex.printStackTrace();
      }
      return null;
   }

   /**
    * Runs the given runnable asynchronous on the UI thread
    */
   public static void run(final Runnable runnable) {
      if (isUIThread()) {
         runnable.run();
      } else {
         getDisplay().asyncExec(runnable);
      }
   }
}
