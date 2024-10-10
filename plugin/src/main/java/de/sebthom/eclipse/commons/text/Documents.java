/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.text;

import java.nio.charset.Charset;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.IDocument;

import de.sebthom.eclipse.commons.internal.EclipseCommonsPlugin;
import de.sebthom.eclipse.commons.resources.Resources;

/**
 * @author Sebastian Thomschke
 */
public abstract class Documents {

   public static @Nullable Charset getCharset(final @Nullable IDocument document) {
      if (document == null)
         return null;
      final ITextFileBuffer buf = getTextFileBuffer(document);
      if (buf == null)
         return null;
      return getCharset(buf);
   }

   private static @Nullable Charset getCharset(final ITextFileBuffer textFileBuffer) {
      try {
         final String charsetName = textFileBuffer.getEncoding();
         if (charsetName != null)
            return Charset.forName(charsetName);
      } catch (final Exception ex) {
         EclipseCommonsPlugin.log().error(ex);
      }
      return null;
   }

   public static @Nullable IFile getFile(final @Nullable IDocument document) {
      if (document == null)
         return null;
      final var path = getPath(document);
      if (path == null)
         return null;
      return Resources.getWorkspaceRoot().getFile(path);
   }

   public static @Nullable IPath getPath(final @Nullable IDocument document) {
      if (document == null)
         return null;
      final var buf = getTextFileBuffer(document);
      if (buf == null)
         return null;
      return buf.getLocation();
   }

   public static @Nullable ITextFileBuffer getTextFileBuffer(final @Nullable IDocument document) {
      if (document == null)
         return null;
      final ITextFileBufferManager bufMgr = FileBuffers.getTextFileBufferManager();
      if (bufMgr == null)
         return null;
      return bufMgr.getTextFileBuffer(document);
   }
}
