/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import de.sebthom.eclipse.commons.internal.EclipseCommonsPlugin;
import net.sf.jstuff.core.Strings;

/**
 * @author Sebastian Thomschke
 */
@NonNullByDefault
public abstract class Projects {

   @Nullable
   public static IProject adapt(@Nullable final Object adaptable) {
      final var project = Adapters.adapt(adaptable, IProject.class);
      if (project != null)
         return project;
      @SuppressWarnings("unused")
      final var resource = Adapters.adapt(adaptable, IResource.class);
      if (resource != null)
         return resource.getProject();
      return null;
   }

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
   public static IProject getOpenProjectWithNature(@Nullable final String projectName, @Nullable final String natureId) {
      if (Strings.isEmpty(projectName) || Strings.isBlank(natureId))
         return null;

      final var project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
      return project.exists() && project.isOpen() && hasNature(project, natureId) ? project : null;
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
}
