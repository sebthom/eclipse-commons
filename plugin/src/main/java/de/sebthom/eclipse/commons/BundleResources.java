/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.URIUtil;
import org.osgi.framework.Bundle;

import net.sf.jstuff.core.validation.Args;

/**
 * @author Sebastian Thomschke
 */
public class BundleResources {

   private final Bundle bundle;
   private final String[] searchPaths;

   public BundleResources(final Bundle bundle) {
      this(bundle, "");
   }

   public BundleResources(final Bundle bundle, final String... searchPaths) {
      this.bundle = bundle;
      this.searchPaths = initSearchPaths(searchPaths);
   }

   public BundleResources(final Plugin plugin) {
      this(plugin, "");
   }

   public BundleResources(final Plugin plugin, final String... searchPaths) {
      this(plugin.getBundle(), searchPaths);
   }

   /**
    * @throws IllegalArgumentException if given resourcePath cannot be found
    */
   public File extract(final String resourcePath) throws IOException {
      Args.notBlank("resourcePath", resourcePath);

      try {
         final URL url = FileLocator.toFileURL(getURL(resourcePath)); // extracts the file
         return URIUtil.toFile(URIUtil.toURI(url));
      } catch (final IOException ex) {
         throw ex;
      } catch (final Exception ex) {
         throw new IOException(ex);
      }
   }

   /**
    * @throws IllegalArgumentException if given resourcePath cannot be found
    */
   public InputStream getAsStream(final String resourcePath) throws IOException {
      final URL url = getURL(resourcePath);
      final URLConnection con = url.openConnection();
      return con.getInputStream(); // responsibility of caller to close stream when done
   }

   /**
    * @throws IllegalArgumentException if given resourcePath cannot be found
    */
   public String getAsString(final String resourcePath) throws IOException {
      try (InputStream stream = getAsStream(resourcePath)) {
         final var writer = new StringWriter();
         IOUtils.copy(stream, writer, StandardCharsets.UTF_8);
         return writer.toString();
      }
   }

   /**
    * @throws IllegalArgumentException if given resourcePath is blank or cannot be found
    */
   public URL getURL(final String resourcePath) {
      Args.notBlank("resourcePath", resourcePath);

      for (final String searchPath : searchPaths) {
         final var url = FileLocator.find(bundle, new Path(searchPath + resourcePath), null);
         if (url != null)
            return url;
      }

      throw new IllegalArgumentException("Resource not found: " + resourcePath);
   }

   private String[] initSearchPaths(final String... searchPaths) {
      final var paths = new LinkedHashSet<String>();

      for (String searchPath : searchPaths) {
         if (searchPath.length() == 0) {
            paths.add("");
            continue;
         }
         searchPath = searchPath.replace('\\', '/');
         if (!searchPath.endsWith("/")) {
            searchPath = searchPath + '/';
         }
         paths.add(searchPath);
      }
      paths.add(""); // add root path to end in case it is not in the list

      return paths.toArray(String[]::new);
   }
}
