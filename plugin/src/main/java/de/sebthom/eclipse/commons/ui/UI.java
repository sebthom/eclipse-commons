/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
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

   @Nullable
   public static IProject getActiveProject() {
      final var window = getActiveWorkbenchWindow();
      if (window == null)
         return null;
      return getActiveProject(window);
   }

   @Nullable
   public static IProject getActiveProject(final IWorkbenchPartReference partRef) {
      try {
         if (partRef instanceof IEditorReference) {
            final var editorRef = (IEditorReference) partRef;
            final var editorInput = editorRef.getEditorInput();
            if (editorInput instanceof FileEditorInput) {
               final var fileEditorInput = (FileEditorInput) editorInput;
               return fileEditorInput.getFile().getProject();
            }
         }
      } catch (final CoreException ex) {
         EclipseCommonsPlugin.log().error(ex);
      }
      return null;
   }

   @Nullable
   @SuppressWarnings("unused") // annotation-based null-analysis false positive
   public static IProject getActiveProject(final IWorkbenchWindow window) {
      try {
         final var sel = window.getSelectionService().getSelection();
         if (sel instanceof IStructuredSelection) {
            final var structSel = (IStructuredSelection) sel;
            final var elem1 = structSel.getFirstElement();
            if (elem1 instanceof IResource)
               return ((IResource) elem1).getProject();
            return null;
         }

         final var activePage = window.getActivePage();
         if (activePage != null) {
            final var activeEditor = activePage.getActiveEditor();
            if (activeEditor != null) {
               final var input = activeEditor.getEditorInput();
               final var project = input.getAdapter(IProject.class);
               if (project != null)
                  return project;

               final IResource resource = input.getAdapter(IResource.class);
               if (resource == null)
                  return null;
               return resource.getProject();
            }
         }
      } catch (final Exception ex) {
         EclipseCommonsPlugin.log().error(ex);
      }
      return null;
   }

   @Nullable
   public static IProject getActiveProjectWithNature(final IWorkbenchPartReference partRef, @Nullable final String natureId) {
      final var project = getActiveProject(partRef);
      if (project == null)
         return null;

      return Projects.hasNature(project, natureId) ? project : null;
   }

   @Nullable
   public static IProject getActiveProjectWithNature(final IWorkbenchWindow window, @Nullable final String natureId) {
      final var project = getActiveProject(window);
      if (project == null)
         return null;

      return Projects.hasNature(project, natureId) ? project : null;
   }

   @Nullable
   public static IWorkbenchPage getActiveWorkbenchPage() {
      final var window = getActiveWorkbenchWindow();
      return window == null ? null : window.getActivePage();
   }

   @Nullable
   public static IWorkbenchWindow getActiveWorkbenchWindow() {
      final var workbench = PlatformUI.getWorkbench();
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
      var display = PlatformUI.getWorkbench().getDisplay();
      if (display != null)
         return display;

      display = Display.getCurrent();
      if (display != null)
         return display;

      return Display.getDefault();
   }

   /**
    * @return the current shell
    */
   @Nullable
   public static Shell getShell() {
      final var window = getActiveWorkbenchWindow();
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
         final var page = getActiveWorkbenchPage();
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
