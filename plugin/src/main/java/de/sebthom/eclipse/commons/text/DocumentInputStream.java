/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

/**
 * @author Sebastian Thomschke
 */
public class DocumentInputStream extends InputStream {

   private final IDocument doc;
   private int nextPos = 0;

   public DocumentInputStream(final IDocument document) {
      doc = document;
   }

   @Override
   public int available() throws IOException {
      return Math.max(0, doc.getLength() - nextPos);
   }

   public IDocument getDocument() {
      return doc;
   }

   @Override
   public int read() throws IOException {
      try {
         if (nextPos < doc.getLength())
            return doc.getChar(nextPos++) & 0xFF;
      } catch (final BadLocationException ex) {
         // ignore
      }
      return -1;
   }

   @Override
   public int read(@NonNullByDefault({}) final byte[] buff, final int buffOffset, final int len) throws IOException {
      Objects.checkFromIndexSize(buffOffset, len, buff.length);

      if (len == 0)
         return 0;

      final var docLen = doc.getLength();
      if (nextPos >= docLen)
         return -1;

      var bytesRead = -1;
      try {
         buff[buffOffset] = (byte) doc.getChar(nextPos++);
         bytesRead = 1;

         while (bytesRead < len) {
            if (nextPos >= docLen) {
               break;
            }

            buff[buffOffset + bytesRead++] = (byte) doc.getChar(nextPos++);
         }
      } catch (final BadLocationException ex) {
         // ignore
      }
      return bytesRead;
   }

   @Override
   public long skip(long n) throws IOException {
      if (n < 1)
         return 0;
      n = Math.min(n, available());
      nextPos += n;
      return n;
   }
}
