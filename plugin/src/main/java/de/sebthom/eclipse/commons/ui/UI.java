/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.progress.IProgressConstants;

import de.sebthom.eclipse.commons.internal.EclipseCommonsPlugin;
import de.sebthom.eclipse.commons.resources.Projects;
import net.sf.jstuff.core.functional.ThrowingSupplier;

/**
 * @author Sebastian Thomschke
 */
public abstract class UI {

   public static void center(final Shell shell) {
      final Rectangle parentBounds;
      final var parent = shell.getParent();
      if (parent == null) {
         parentBounds = shell.getDisplay().getBounds();
      } else {
         parentBounds = parent.getBounds();
      }
      final var shellBounds = shell.getBounds();
      final var x = (parentBounds.width - shellBounds.width) / 2 + parentBounds.x;
      final var y = (parentBounds.height - shellBounds.height) / 2 + parentBounds.y;
      shell.setLocation(new Point(x, y));
   }

   public static @Nullable IProject getActiveProject() {
      final var window = getActiveWorkbenchWindow();
      if (window == null)
         return null;
      return getActiveProject(window);
   }

   public static @Nullable IProject getActiveProject(final IWorkbenchPartReference partRef) {
      try {
         if (partRef instanceof final IEditorReference editorRef) {
            final var editorInput = editorRef.getEditorInput();
            if (editorInput instanceof final FileEditorInput fileEditorInput)
               return fileEditorInput.getFile().getProject();
         }
      } catch (final Exception ex) {
         EclipseCommonsPlugin.log().error(ex);
      }
      return null;
   }

   public static @Nullable IProject getActiveProject(final IWorkbenchWindow window) {
      try {
         final var sel = window.getSelectionService().getSelection();
         if (sel instanceof final IStructuredSelection structSel) {
            final var elem1 = structSel.getFirstElement();
            if (elem1 instanceof final IResource res)
               return res.getProject();
            return null;
         }

         final var activePage = window.getActivePage();
         if (activePage != null) {
            final var activeEditor = activePage.getActiveEditor();
            if (activeEditor != null) {
               final var input = activeEditor.getEditorInput();
               return Projects.adapt(input);
            }
         }
      } catch (final Exception ex) {
         EclipseCommonsPlugin.log().error(ex);
      }
      return null;
   }

   public static @Nullable IProject getActiveProjectWithNature(final IWorkbenchPartReference partRef, final @Nullable String natureId) {
      final var project = getActiveProject(partRef);
      if (project == null)
         return null;

      return Projects.hasNature(project, natureId) ? project : null;
   }

   public static @Nullable IProject getActiveProjectWithNature(final IWorkbenchWindow window, final @Nullable String natureId) {
      final var project = getActiveProject(window);
      if (project == null)
         return null;

      return Projects.hasNature(project, natureId) ? project : null;
   }

   public static @Nullable IWorkbenchPage getActiveWorkbenchPage() {
      final var window = getActiveWorkbenchWindow();
      return window == null ? null : window.getActivePage();
   }

   public static @Nullable IWorkbenchWindow getActiveWorkbenchWindow() {
      if (!isWorkbenchRunning())
         return null;

      final var workbench = getWorkbench();
      final var window = workbench.getActiveWorkbenchWindow();
      if (window == null) {
         final var windows = workbench.getWorkbenchWindows();
         return windows.length == 0 ? null : windows[0];
      }
      return window;
   }

   /**
    * @return the current display
    */
   public static Display getDisplay() {
      if (isWorkbenchRunning())
         return getWorkbench().getDisplay();

      final var display = Display.getCurrent();
      if (display != null)
         return display;

      return Display.getDefault();
   }

   /**
    * @return the current shell
    * @throws IllegalStateException if no shell is available
    */
   public static Shell getShell() {
      final var window = getActiveWorkbenchWindow();
      Shell shell = window == null ? null : window.getShell();
      if (shell == null) {
         shell = getDisplay().getActiveShell();
      }
      if (shell == null)
         throw new IllegalStateException("No open shell found!");
      return shell;
   }

   public static @Nullable IStatusLineManager getStatusLine(final IWorkbenchWindow window) {
      final var page = window.getActivePage();

      final var editorSite = page.getActiveEditor().getEditorSite();
      if (editorSite != null)
         return editorSite.getActionBars().getStatusLineManager();

      final var part = page.getActivePart();
      if (part instanceof final IViewSite view)
         return view.getActionBars().getStatusLineManager();

      return null;
   }

   /**
    * @throws IllegalStateException if {@link PlatformUI#isWorkbenchRunning()} is false
    *
    * @see PlatformUI#getWorkbench()
    */
   public static IWorkbench getWorkbench() {
      return PlatformUI.getWorkbench();
   }

   public static boolean isUIThread() {
      return Display.getCurrent() != null;
   }

   public static boolean isWorkbenchRunning() {
      try {
         return PlatformUI.isWorkbenchRunning();
      } catch (final LinkageError ex) {
         return false;
      }
   }

   public static void maximize(final IWorkbenchPart part) {
      final var site = part.getSite();
      if (site == null)
         return;
      try {
         final var handlerService = site.getService(IHandlerService.class);
         if (handlerService == null)
            return;
         handlerService.executeCommand(IWorkbenchCommandConstants.WINDOW_MAXIMIZE_ACTIVE_VIEW_OR_EDITOR, null);
      } catch (final Exception ex) {
         EclipseCommonsPlugin.log().error(ex);
      }
   }

   public static void maximize(final IWorkbenchWindow window) {
      try {
         final var handlerService = window.getWorkbench().getService(IHandlerService.class);
         if (handlerService == null)
            return;
         handlerService.executeCommand(IWorkbenchCommandConstants.WINDOW_MAXIMIZE_ACTIVE_VIEW_OR_EDITOR, null);
      } catch (final Exception ex) {
         EclipseCommonsPlugin.log().error(ex);
      }
   }

   public static @Nullable IViewPart openProgressView() {
      return openView(IProgressConstants.PROGRESS_VIEW_ID);
   }

   @SuppressWarnings("unchecked")
   public static @Nullable <T extends IViewPart> T openView(final String viewId) {
      try {
         final var page = getActiveWorkbenchPage();
         if (page != null)
            return (T) page.showView(viewId);
      } catch (final Exception ex) {
         EclipseCommonsPlugin.log().error(ex);
      }
      return null;
   }

   /**
    * Runs the given runnable synchronously on the UI thread
    *
    * @throws SWTException if the {@link Display} has been disposed
    */
   public static void run(final Runnable runnable) {
      if (isUIThread()) {
         runnable.run();
      } else {
         getDisplay().syncExec(runnable);
      }
   }

   /**
    * Runs the given runnable synchronously on the UI thread
    *
    * @throws EXCEPTION if executing the runnable failed
    * @throws SWTException if the {@link Display} has been disposed
    */
   @SuppressWarnings("unchecked")
   public static <RETURN_VALUE, EXCEPTION extends Exception> RETURN_VALUE run(final ThrowingSupplier<RETURN_VALUE, EXCEPTION> runnable)
         throws EXCEPTION {
      if (isUIThread())
         return runnable.get();

      final var resultRef = new AtomicReference<RETURN_VALUE>();
      final var exRef = new AtomicReference<@Nullable Exception>();
      getDisplay().syncExec(() -> {
         try {
            resultRef.set(runnable.getOrThrow());
         } catch (final Exception ex) {
            exRef.set(ex);
         }
      });

      final Exception ex = exRef.get();
      if (ex != null) {
         if (ex instanceof final RuntimeException rex)
            throw rex;
         throw (EXCEPTION) ex;
      }
      return resultRef.get();
   }

   /**
    * Runs the given runnable asynchronously on the UI thread
    *
    * @throws SWTException if the {@link Display} has been disposed
    */
   public static void runAsync(final Runnable runnable) {
      getDisplay().asyncExec(runnable);
   }

   /**
    * Runs the given runnable on the UI thread after the specified number of milliseconds have elapsed.
    *
    * @throws SWTException if the {@link Display} has been disposed
    */
   public static void runDelayed(final int delayMS, final Runnable runnable) {
      getDisplay().timerExec(delayMS, runnable);
   }
}
