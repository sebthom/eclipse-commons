/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.text;

import static org.eclipse.core.filebuffers.FileBuffers.getTextFileBufferManager;
import static org.eclipse.core.runtime.Platform.getContentTypeManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;

import net.sf.jstuff.core.functional.ThrowingSupplier;

/**
 * @author Sebastian Thomschke
 */
public abstract class ContentTypes {
   public static @Nullable IContentType findById(final String contentTypeId) {
      return getContentTypeManager().getContentType(contentTypeId);
   }

   public static List<IContentType> of(final @Nullable IDocument doc) {
      if (doc == null)
         return Collections.emptyList();

      final var buff = getTextFileBufferManager().getTextFileBuffer(doc);
      if (buff == null)
         return of(null, () -> new ByteArrayInputStream(doc.get().getBytes()));

      return of(buff);
   }

   public static List<IContentType> of(final IEditorInput input) {
      if (input instanceof final IFileEditorInput fInput)
         return of(fInput.getName(), () -> fInput.getFile().getContents());
      if (input instanceof final IPathEditorInput pInput)
         return of(pInput.getPath().toFile().toPath());
      return List.of(getContentTypeManager().findContentTypesFor(input.getName()));
   }

   public static List<IContentType> of(final IFile file) {
      return of(file.getName(), file::getContents);
   }

   public static List<IContentType> of(final ITextFileBuffer buff) {
      final String fileName = buff.getLocation().lastSegment();
      final var result = new ArrayList<>( //
         of(fileName, () -> new ByteArrayInputStream(buff.getDocument().get().getBytes())) //
      );

      try {
         final IContentType primaryContentType = buff.getContentType();
         if (primaryContentType != null) {
            switch (result.indexOf(primaryContentType)) {
               case -1:
                  result.add(0, primaryContentType);
                  break;
               case 0: // nothing to do, already at first position
                  break;
               default:
                  result.remove(primaryContentType);
                  result.add(0, primaryContentType);
            }
         }
      } catch (final CoreException e) {
         // ignore
      }
      return result;
   }

   public static List<IContentType> of(final Path path) {
      return of(path.getFileName().toString(), () -> Files.newInputStream(path));
   }

   private static List<IContentType> of(final @Nullable String fileName,
         final ThrowingSupplier<@Nullable InputStream, Exception> inputStreamFactory) {
      try (var in = inputStreamFactory.get()) {
         if (in != null)
            return List.of(getContentTypeManager().findContentTypesFor(in, fileName));
      } catch (final Exception ex) {
         // ignore
      }
      if (fileName != null)
         return List.of(getContentTypeManager().findContentTypesFor(fileName));
      return Collections.emptyList();
   }
}
