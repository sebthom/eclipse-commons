/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.annotation.Nullable;

import de.sebthom.eclipse.commons.internal.EclipseCommonsPlugin;
import net.sf.jstuff.core.Strings;

/**
 * @author Sebastian Thomschke
 */
public abstract class Projects {

   public static @Nullable IProject adapt(final @Nullable Object adaptable) {
      final var project = Adapters.adapt(adaptable, IProject.class);
      if (project != null)
         return project;
      final var resource = Adapters.adapt(adaptable, IResource.class);
      if (resource != null)
         return resource.getProject();
      return null;
   }

   /**
    * Finds the project containing a resource represented by the given resourcePath
    */
   @SuppressWarnings("deprecation")
   public static @Nullable IProject findProjectContaining(final @Nullable IPath resourcePath) {
      if (resourcePath == null)
         return null;

      for (final var container : ResourcesPlugin.getWorkspace().getRoot().findContainersForLocation(resourcePath)) {
         if (container instanceof final IProject project)
            return project;
      }
      return null;
   }

   /**
    * Finds the project containing a resource represented by the given URI
    */
   public static @Nullable IProject findProjectContaining(final @Nullable URI resourceURI) {
      if (resourceURI == null)
         return null;

      for (final var container : ResourcesPlugin.getWorkspace().getRoot().findContainersForLocationURI(resourceURI)) {
         if (container instanceof final IProject project)
            return project;
      }
      return null;
   }

   public static List<IProject> getOpenProjectsWithNature(final @Nullable String natureId) {
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

   public static @Nullable IProject getOpenProjectWithNature(final @Nullable String projectName, final @Nullable String natureId) {
      if (Strings.isEmpty(projectName) || Strings.isBlank(natureId))
         return null;

      final var project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
      return project.exists() && project.isOpen() && hasNature(project, natureId) ? project : null;
   }

   public static boolean hasNature(@Nullable final IProject project, final @Nullable String natureId) {
      if (project == null //
         || Strings.isBlank(natureId) //
         || !project.exists() //
         || !project.isOpen() // natures of closed projects cannot be queried
      )
         return false;

      try {
         return project.hasNature(natureId);
      } catch (final CoreException ex) {
         EclipseCommonsPlugin.log().debug(ex);
         return false;
      }
   }
}
