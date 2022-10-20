/*
 * Copyright 2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Sebastian Thomschke
 */
public abstract class Resources {

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

   public static BufferedReader newBufferedReader(final IFile file) throws CoreException {
      return new BufferedReader(new InputStreamReader(file.getContents(true)));
   }
}
