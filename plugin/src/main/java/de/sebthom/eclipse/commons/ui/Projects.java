/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.EditorReference;
import org.eclipse.ui.part.FileEditorInput;

import de.sebthom.eclipse.commons.internal.EclipseCommonsPlugin;
import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.validation.Args;

/**
 * @author Sebastian Thomschke
 */
@SuppressWarnings("restriction")
public final class Projects {

   @Nullable
   public static IProject getActiveProject(@NonNull final IWorkbenchPartReference partRef) {
      Args.notNull("partRef", partRef);

      try {
         if (partRef instanceof EditorReference) {
            final var editorRef = (EditorReference) partRef;
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
   public static IProject getActiveProject(@NonNull final IWorkbenchWindow window) {
      Args.notNull("window", window);

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
   public static IProject getActiveProjectWithNature(@NonNull final IWorkbenchPartReference partRef, @Nullable final String natureId) {
      Args.notNull("partRef", partRef);

      final var project = getActiveProject(partRef);
      if (project == null)
         return null;

      return hasNature(project, natureId) ? project : null;
   }

   @Nullable
   public static IProject getActiveProjectWithNature(@NonNull final IWorkbenchWindow window, @Nullable final String natureId) {
      Args.notNull("window", window);

      final var project = getActiveProject(window);
      if (project == null)
         return null;

      return hasNature(project, natureId) ? project : null;
   }

   @NonNull
   public static List<IProject> getOpenProjectsWithNature(@Nullable final String natureId) {
      if (natureId == null || natureId.length() == 0)
         return Collections.emptyList();

      final var projects = new ArrayList<IProject>();
      for (final var project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
         if (project.isOpen() && hasNature(project, natureId)) {
            projects.add(project);
         }
      }
      return projects;
   }

   @Nullable
   public static IProject getProject(@Nullable final String projectName, @Nullable final String natureId) {
      if (Strings.isEmpty(projectName) || Strings.isBlank(natureId))
         return null;

      final var project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
      if (!project.exists())
         return null;

      return hasNature(project, natureId) ? project : null;
   }

   public static boolean hasNature(@Nullable final IProject project, @Nullable final String natureId) {
      if (project == null || Strings.isBlank(natureId))
         return false;

      if (!project.exists())
         return false;

      // natures of closed projects cannot be queried
      if (!project.isOpen())
         return false;

      try {
         return project.hasNature(natureId);
      } catch (final CoreException ex) {
         EclipseCommonsPlugin.log().debug(ex);
         return false;
      }
   }

   private Projects() {
   }
}
