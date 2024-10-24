/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.text;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Sebastian Thomschke
 */
class DocumentInputStreamTest {

   private static final char UNICODE_REPLACEMENT_CHAR = '\uFFFD';

   private static final String TEST_ASCII = "Hello, World!";

   private static final String EMOJI = "😊";
   private static final int EMOJI_BYTES_LEN = EMOJI.getBytes(UTF_8).length;
   private static final String JAPANESE = "こんにちは";
   private static final String TEST_UNICODE = EMOJI + JAPANESE;
   private static final int TEST_UNICODE_BYTES_LEN = TEST_UNICODE.getBytes(UTF_8).length;

   private final IDocumentProvider documentProvider = new FileDocumentProvider();
   private IDocument document = lateNonNull();

   @BeforeEach
   void setUp() throws CoreException {
      final IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(getClass().getName() + System.currentTimeMillis());
      p.create(null);
      p.open(null);

      final IFile testFile = p.getFile("testfile");
      testFile.create(new ByteArrayInputStream(TEST_UNICODE.getBytes()), true, null);
      final var editorInput = new FileEditorInput(testFile);
      documentProvider.connect(editorInput);
      document = asNonNull(documentProvider.getDocument(editorInput));
   }

   @Test
   void testAvailable() throws IOException {
      document.set(TEST_ASCII);
      try (var is = new DocumentInputStream(document)) {
         assertEquals(UTF_8, is.getCharset());
         assertEquals(TEST_ASCII.length(), is.available());
         final var buffer = new byte[4];
         is.read(buffer);
         assertEquals(TEST_ASCII.length() - 4, is.available());
         is.readAllBytes();
         assertEquals(0, is.available());
      }

      document.set(TEST_UNICODE);
      try (var is = new DocumentInputStream(document)) {
         assertEquals(UTF_8, is.getCharset());
         assertTrue(is.available() > 0);
         is.read(new byte[10]);
         assertTrue(is.available() > 0);
         is.readAllBytes();
         assertEquals(0, is.available());
      }
   }

   @Test
   void testEndOfStream() throws IOException {
      try (var is = new DocumentInputStream(document)) {
         assertEquals(UTF_8, is.getCharset());
         is.skip(Long.MAX_VALUE);
         assertEquals(-1, is.read());
      }
   }

   @Test
   void testReadEachByte() throws IOException {
      try (var is = new DocumentInputStream(document)) {
         assertEquals(UTF_8, is.getCharset());
         final var bytesRead = new ArrayList<Byte>();
         int b;
         while ((b = is.read()) != -1) {
            bytesRead.add((byte) b);
         }

         final var byteArray = new byte[bytesRead.size()];
         for (int i = 0; i < bytesRead.size(); i++) {
            byteArray[i] = bytesRead.get(i);
         }
         assertEquals(TEST_UNICODE, new String(byteArray, UTF_8));
      }
   }

   @Test
   void testReadIntoByteArray() throws IOException {
      final var buffer = new byte[1024]; // Buffer to read a portion of the text

      try (var is = new DocumentInputStream(document)) {
         assertEquals(UTF_8, is.getCharset());
         final int bytesRead = is.read(buffer, 0, buffer.length);

         assertEquals(TEST_UNICODE, new String(buffer, 0, bytesRead, UTF_8));
      }
   }

   @Test
   void testSkip() throws IOException {
      try (var is = new DocumentInputStream(document)) {
         assertEquals(UTF_8, is.getCharset());
         // skip emoji
         final long skipped = is.skip(EMOJI_BYTES_LEN);
         assertEquals(EMOJI_BYTES_LEN, skipped);

         final var japanese = new byte[TEST_UNICODE_BYTES_LEN];
         final int bytesRead = is.read(japanese);

         assertEquals(JAPANESE, new String(japanese, 0, bytesRead, UTF_8));
      }
   }

   @Test
   void testHighSurrogateAtEndOfInput() throws IOException {
      document.set(new String(new char[] {'A', '\uD800'})); // valid char followed by an isolated high surrogate
      try (var is = new DocumentInputStream(document)) {
         assertEquals(UTF_8, is.getCharset());
         final byte[] result = is.readAllBytes();
         final var output = new String(result, UTF_8);

         // the high surrogate at the end should be replaced by the Unicode replacement char
         assertEquals("A" + UNICODE_REPLACEMENT_CHAR, output);
      }
   }

   @Test
   void testHighSurrogateWithoutLowSurrogate() throws IOException {
      document.set(new String(new char[] {'\uD800', 'A'})); // \uD800 is a high surrogate, followed by 'A'
      try (var is = new DocumentInputStream(document)) {
         assertEquals(UTF_8, is.getCharset());
         final byte[] result = is.readAllBytes();
         final var output = new String(result, UTF_8);

         // the invalid surrogate pair should be replaced by the Unicode replacement char
         assertEquals(UNICODE_REPLACEMENT_CHAR + "A", output);
      }
   }
}
