/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Sebastian Thomschke
 */
public abstract class Editors {

   @Nullable
   public static IEditorPart getActiveEditor() {
      final var activePage = UI.getWorkbenchPage();
      if (activePage == null)
         return null;
      return activePage.getActiveEditor();
   }

   @Nullable
   @SuppressWarnings("unused") // annotation-based null-analysis false positive
   public static ITextEditor getActiveTextEditor() {
      final var editor = getActiveEditor();
      if (editor == null)
         return null;

      if (editor instanceof ITextEditor)
         return (ITextEditor) editor;

      final Object adapter = editor.getAdapter(ITextEditor.class);
      if (adapter != null)
         return (ITextEditor) adapter;

      return null;
   }

   @Nullable
   public static IAnnotationModel getAnnotationModel(final ITextEditor editor) {
      if (editor == null)
         return null;
      final var docProvider = editor.getDocumentProvider();
      if (docProvider == null)
         return null;
      return docProvider.getAnnotationModel(editor.getEditorInput());
   }

   @Nullable
   public static IDocument getDocument(final ITextEditor editor) {
      if (editor == null)
         return null;
      final var docProvider = editor.getDocumentProvider();
      if (docProvider == null)
         return null;
      return docProvider.getDocument(editor.getEditorInput());
   }
}
