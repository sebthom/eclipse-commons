/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import de.sebthom.eclipse.commons.internal.EclipseCommonsPlugin;
import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.validation.Args;

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

   public static final void addBuilder(final IProject project, final String builderId, @Nullable final IProgressMonitor monitor)
      throws CoreException {
      if (hasBuilder(project, builderId))
         return;

      final var projectCfg = project.getDescription();
      final var builder = projectCfg.newCommand();
      builder.setBuilderName(builderId);
      projectCfg.setBuildSpec(ArrayUtils.add(projectCfg.getBuildSpec(), builder));
      project.setDescription(projectCfg, monitor);
   }

   public static void addNature(final IProject project, final String natureId, @Nullable final IProgressMonitor monitor)
      throws CoreException {
      Args.notEmpty("natureId", natureId);

      if (project.hasNature(natureId))
         return;

      final var projectCfg = project.getDescription();
      final var newNatureIds = ArrayUtils.add(projectCfg.getNatureIds(), natureId);
      final var status = ResourcesPlugin.getWorkspace().validateNatureSet(newNatureIds);
      if (status.getCode() != IStatus.OK)
         throw new CoreException(status);
      projectCfg.setNatureIds(newNatureIds);
      project.setDescription(projectCfg, monitor);
   }

   public static List<IProject> findOpenProjectsWithNature(final @Nullable String natureId) {
      if (natureId == null || natureId.length() == 0)
         return Collections.emptyList();

      return findProjects(project -> project.isOpen() && hasNature(project, natureId));
   }

   public static @Nullable IProject findOpenProjectWithNature(final @Nullable String projectName, final @Nullable String natureId) {
      if (projectName == null || projectName.isEmpty() || Strings.isBlank(natureId))
         return null;

      final var project = getProject(projectName);
      return project.exists() && project.isOpen() && hasNature(project, natureId) ? project : null;
   }

   /**
    * Finds the project containing a resource represented by the given resourcePath
    */
   @SuppressWarnings("deprecation")
   public static @Nullable IProject findProjectOfResource(final @Nullable IPath resourcePath) {
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
   public static @Nullable IProject findProjectOfResource(final @Nullable URI resourceURI) {
      if (resourceURI == null)
         return null;

      for (final var container : ResourcesPlugin.getWorkspace().getRoot().findContainersForLocationURI(resourceURI)) {
         if (container instanceof final IProject project)
            return project;
      }
      return null;
   }

   public static List<IProject> findProjects(final Predicate<IProject> filter) {
      final var projects = new ArrayList<IProject>();
      for (final var project : getProjects()) {
         if (filter.test(project)) {
            projects.add(project);
         }
      }
      return projects;
   }

   /**
    * @see IWorkspaceRoot#getProject(String)
    */
   public static IProject getProject(final String projectName) {
      return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
   }

   /**
    * @see IWorkspaceRoot#getProjects()
    */
   public static @NonNull IProject[] getProjects() {
      return ResourcesPlugin.getWorkspace().getRoot().getProjects();
   }

   public static final boolean hasBuilder(final IProject project, final String builderId) throws CoreException {
      for (final var builder : project.getDescription().getBuildSpec()) {
         if (builderId.equals(builder.getBuilderName()))
            return true;
      }
      return false;
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

   public static void removeBuilder(final IProject project, final String builderId, @Nullable final IProgressMonitor monitor)
      throws CoreException {
      if (hasBuilder(project, builderId))
         return;

      final var projectCfg = project.getDescription();
      projectCfg.setBuildSpec(ArrayUtils.filter(builder -> !builderId.equals(builder.getBuilderName()), projectCfg.getBuildSpec()));
      project.setDescription(projectCfg, monitor);
   }

   public static void removeNature(final IProject project, final String natureId, @Nullable final IProgressMonitor monitor)
      throws CoreException {
      Args.notEmpty("natureId", natureId);

      if (!project.hasNature(natureId))
         return;

      final var projectCfg = project.getDescription();
      final var newNatureIds = ArrayUtils.removeElement(projectCfg.getNatureIds(), natureId);
      projectCfg.setNatureIds(newNatureIds);
      project.setDescription(projectCfg, monitor);
   }
}
