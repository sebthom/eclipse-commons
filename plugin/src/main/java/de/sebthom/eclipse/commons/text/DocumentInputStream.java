/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.text;

import java.nio.charset.Charset;

import org.eclipse.jface.text.IDocument;

import net.sf.jstuff.core.io.stream.CharSequenceInputStream;

/**
 * @author Sebastian Thomschke
 */
public final class DocumentInputStream extends CharSequenceInputStream {

   private static Charset getCharset(final IDocument document) {
      final var cs = Documents.getCharset(document);
      return cs == null ? Charset.defaultCharset() : cs;
   }

   public DocumentInputStream(final IDocument doc) {
      super(doc::getChar, doc::getLength, getCharset(doc));
   }
}
