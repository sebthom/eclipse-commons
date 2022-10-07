/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import de.sebthom.eclipse.commons.internal.EclipseCommonsPlugin;

/**
 * @author Sebastian Thomschke
 */
public abstract class Editors {

   @Nullable
   public static IDocument getActiveDocument() {
      return getDocument(getActiveTextEditor());
   }

   @Nullable
   public static IEditorPart getActiveEditor() {
      final var activePage = UI.getActiveWorkbenchPage();
      if (activePage == null)
         return null;
      return activePage.getActiveEditor();
   }

   @Nullable
   public static ITextEditor getActiveTextEditor() {
      final var editor = getActiveEditor();
      if (editor == null)
         return null;

      if (editor instanceof final ITextEditor textEditor)
         return textEditor;

      if (editor instanceof final MultiPageEditorPart pagePart) {
         final var page = pagePart.getSelectedPage();
         if (page instanceof final ITextEditor textEditor)
            return textEditor;
      }

      return Adapters.adapt(editor, ITextEditor.class);
   }

   @Nullable
   public static IAnnotationModel getAnnotationModel(@Nullable final ITextEditor editor) {
      if (editor == null)
         return null;
      final var docProvider = editor.getDocumentProvider();
      if (docProvider == null)
         return null;
      return docProvider.getAnnotationModel(editor.getEditorInput());
   }

   @Nullable
   public static IDocument getDocument(@Nullable final ITextEditor editor) {
      if (editor == null)
         return null;
      final var docProvider = editor.getDocumentProvider();
      if (docProvider == null)
         return null;
      return docProvider.getDocument(editor.getEditorInput());
   }

   public static boolean replaceCurrentSelection(final String replacement, final boolean selectReplacement) {
      final var editor = getActiveTextEditor();
      if (editor == null)
         return false;

      final var doc = getDocument(editor);
      if (doc == null)
         return false;

      final var selProvider = editor.getSelectionProvider();
      final var sel = (TextSelection) selProvider.getSelection();

      if (sel.isEmpty() //
         || sel.getLength() == 0 //
         || sel.getText().equals(replacement))
         return false;

      try {
         doc.replace(sel.getOffset(), sel.getLength(), replacement);
         if (selectReplacement) {
            selProvider.setSelection(new TextSelection(sel.getOffset(), replacement.length()));
         } else {
            selProvider.setSelection(new TextSelection(sel.getOffset() + replacement.length(), 0));
         }
         return true;
      } catch (final BadLocationException ex) {
         EclipseCommonsPlugin.log().error(ex);
      }
      return false;
   }
}
