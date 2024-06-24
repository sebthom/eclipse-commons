/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Sebastian Thomschke
 */
public abstract class Resources {

   public static IWorkspace getWorkspace() {
      return ResourcesPlugin.getWorkspace();
   }

   public static IWorkspaceRoot getWorkspaceRoot() {
      return ResourcesPlugin.getWorkspace().getRoot();
   }

   /**
    * Returns the uncached last modified timestamp from the file system via {@link File#lastModified()}.
    *
    * @return 0 if the resource does not exist.
    */
   public static long lastModified(final @Nullable IPath path) {
      if (path == null)
         return 0;
      return path.toFile().lastModified();
   }

   /**
    * Returns the uncached last modified timestamp from the file system via {@link File#lastModified()}.
    * This value may differ (i.e. be more accurate) than {@link IResource#getModificationStamp()}
    *
    * @return 0 if the resource does not exist.
    */
   public static long lastModified(final @Nullable IResource res) {
      if (res == null)
         return 0;
      return lastModified(res.getLocation());
   }

   public static BufferedReader newBufferedReader(final IFile file) throws CoreException {
      Charset cs = StandardCharsets.UTF_8;
      try {
         cs = Charset.forName(file.getCharset());
      } catch (final Exception ex) {
         // ignore
      }
      return new BufferedReader(new InputStreamReader(file.getContents(true), cs));
   }

   public static byte[] readBytes(final IFile file) throws IOException, CoreException {
      try (InputStream is = file.getContents(true)) {
         return IOUtils.toByteArray(is);
      }
   }

   public static String readString(final IFile file) throws IOException, CoreException {
      try (BufferedReader reader = newBufferedReader(file)) {
         return IOUtils.toString(reader);
      }
   }

   public static File toAbsoluteFile(final IResource file) {
      final var path = file.getLocation();
      if (path == null)
         throw new IllegalArgumentException("[file.rawLocation] is null");
      return path.makeAbsolute().toFile();
   }

   public static Path toAbsolutePath(final IPath path) {
      return path.toFile().toPath().toAbsolutePath();
   }

   public static Path toAbsolutePath(final IResource file) {
      return toAbsoluteFile(file).toPath();
   }

   public static Path toNormalizedPath(final IPath path) {
      return path.toFile().toPath().normalize();
   }

   public static File toProjectRelativeFile(final IResource file) {
      return file.getProjectRelativePath().toFile();
   }

   public static Path toProjectRelativePath(final IResource file) {
      return file.getProjectRelativePath().toFile().toPath();
   }
}
