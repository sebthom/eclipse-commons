/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.osgi.framework.Bundle;

import net.sf.jstuff.core.validation.Args;

/**
 * @author Sebastian Thomschke
 */
public class BundleResources {

   private final Bundle bundle;

   @NonNullByDefault({})
   private String[] searchPaths;

   public BundleResources(final Bundle bundle) {
      this(bundle, "");
   }

   public BundleResources(final Bundle bundle, final String... searchPaths) {
      this.bundle = bundle;
      initSearchPaths(searchPaths);
   }

   public BundleResources(final Plugin plugin) {
      this(plugin, "");
   }

   public BundleResources(final Plugin plugin, final String... searchPaths) {
      bundle = plugin.getBundle();
      initSearchPaths(searchPaths);
   }

   /**
    * @throws IllegalArgumentException if given resourcePath cannot be found
    */
   public File extract(final String resourcePath) throws IOException {
      Args.notBlank("resourcePath", resourcePath);

      final var url = FileLocator.toFileURL(getURL(resourcePath)); // extract the file
      try {
         return URIUtil.toFile(URIUtil.toURI(url));
      } catch (final URISyntaxException ex) {
         throw new IOException(ex);
      }
   }

   /**
    * @throws IllegalArgumentException if given resourcePath cannot be found
    */
   public InputStream getAsStream(final String resourcePath) throws IOException {
      final var url = getURL(resourcePath);
      final var con = url.openConnection();
      return con.getInputStream(); // responsibility of caller to close stream when done
   }

   /**
    * @throws IllegalArgumentException if given resourcePath cannot be found
    */
   public String getAsString(final String resourcePath) throws IOException {
      try (var stream = getAsStream(resourcePath)) {
         final var writer = new StringWriter();
         IOUtils.copy(stream, writer, StandardCharsets.UTF_8);
         return writer.toString();
      }
   }

   /**
    * @throws IllegalArgumentException if given resourcePath cannot be found
    */
   public URL getURL(final String resourcePath) {
      Args.notBlank("resourcePath", resourcePath);

      for (final var searchPath : searchPaths) {
         final var url = FileLocator.find(bundle, new Path(searchPath + resourcePath), null);
         if (url != null)
            return url;
      }

      throw new IllegalArgumentException("Resource not found: " + resourcePath);
   }

   private void initSearchPaths(final String... searchPaths) {
      final var paths = new LinkedHashSet<String>();

      for (var p : searchPaths) {
         if (p.length() == 0) {
            paths.add("");
            continue;
         }
         p = p.replace('\\', '/');
         if (!p.endsWith("/")) {
            p = p + '/';
         }
         paths.add(p);
      }
      paths.add(""); // add root path to end in case it is not in the list

      this.searchPaths = paths.toArray(String[]::new);
   }
}
