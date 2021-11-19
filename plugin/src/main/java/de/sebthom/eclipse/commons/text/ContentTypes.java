/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.text;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;

/**
 * @author Sebastian Thomschke
 */
public abstract class ContentTypes {

   private static final IContentType[] EMPTY = {};

   public static IContentType[] getContentTypes(@Nullable final IDocument doc) {
      if (doc == null)
         return EMPTY;

      final var buffer = FileBuffers.getTextFileBufferManager().getTextFileBuffer(doc);
      return getContentTypes(buffer);
   }

   public static IContentType[] getContentTypes(@Nullable final ITextFileBuffer buff) {
      if (buff == null)
         return EMPTY;

      final var fileName = buff.getLocation().lastSegment();
      if (fileName == null)
         return EMPTY;

      return Platform.getContentTypeManager().findContentTypesFor(fileName);
   }

   public static IContentType[] getContentTypes(final IEditorInput input) {
      return Platform.getContentTypeManager().findContentTypesFor(input.getName());
   }
}
