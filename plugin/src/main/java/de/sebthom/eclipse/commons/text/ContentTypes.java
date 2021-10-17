/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.text;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;

import net.sf.jstuff.core.validation.Args;

/**
 * @author Sebastian Thomschke
 */
public abstract class ContentTypes {

   private static final IContentType[] EMPTY = {};

   @NonNull
   public static IContentType[] getContentTypes(@Nullable final IDocument doc) {
      if (doc == null)
         return EMPTY;

      final var buffer = FileBuffers.getTextFileBufferManager().getTextFileBuffer(doc);
      return getContentTypes(buffer);
   }

   @NonNull
   public static IContentType[] getContentTypes(@Nullable final ITextFileBuffer buff) {
      if (buff == null)
         return EMPTY;

      final var fileName = buff.getLocation().lastSegment();
      if (fileName == null)
         return EMPTY;

      return Platform.getContentTypeManager().findContentTypesFor(fileName);
   }

   @NonNull
   public static IContentType[] getContentTypes(@NonNull final IEditorInput input) {
      Args.notNull("input", input);

      return Platform.getContentTypeManager().findContentTypesFor(input.getName());
   }
}
