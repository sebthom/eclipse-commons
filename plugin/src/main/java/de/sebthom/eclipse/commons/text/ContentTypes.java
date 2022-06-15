/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.text;

import static org.eclipse.core.filebuffers.FileBuffers.*;
import static org.eclipse.core.runtime.Platform.*;

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;

/**
 * @author Sebastian Thomschke
 */
public abstract class ContentTypes {

   private static final IContentType[] EMPTY = {};

   @Nullable
   public static IContentType findById(final String contentTypeId) {
      return getContentTypeManager().getContentType(contentTypeId);
   }

   public static IContentType[] getAll(@Nullable final IDocument doc) {
      if (doc == null)
         return EMPTY;

      return getAll(getTextFileBufferManager().getTextFileBuffer(doc));
   }

   public static IContentType[] getAll(final IEditorInput input) {
      return getContentTypeManager().findContentTypesFor(input.getName());
   }

   public static IContentType[] getAll(@Nullable final ITextFileBuffer buff) {
      if (buff == null)
         return EMPTY;

      final var fileName = buff.getLocation().lastSegment();
      if (fileName == null)
         return EMPTY;

      return getContentTypeManager().findContentTypesFor(fileName);
   }
}
