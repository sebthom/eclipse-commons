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
import org.eclipse.jface.text.IDocument;

import de.sebthom.eclipse.commons.internal.EclipseCommonsPlugin;
import net.sf.jstuff.core.io.stream.CharSequenceInputStream;

/**
 * @author Sebastian Thomschke
 */
public final class DocumentInputStream extends CharSequenceInputStream {

   private static Charset getCharset(final IDocument document) {
      final ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
      if (bufferManager == null)
         return Charset.defaultCharset();
      final ITextFileBuffer buffer = bufferManager.getTextFileBuffer(document);
      if (buffer == null)
         return Charset.defaultCharset();
      try {
         final String charsetName = buffer.getEncoding();
         if (charsetName != null)
            return Charset.forName(charsetName);
      } catch (final Exception ex) {
         EclipseCommonsPlugin.log().error(ex);
      }
      return Charset.defaultCharset();
   }

   public DocumentInputStream(final IDocument doc) {
      super(doc::getChar, doc::getLength, getCharset(doc));
   }
}
