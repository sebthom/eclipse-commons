/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
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

   public static @Nullable IDocument getActiveDocument() {
      return getDocument(getActiveTextEditor());
   }

   public static @Nullable IEditorPart getActiveEditor() {
      final var activePage = UI.getActiveWorkbenchPage();
      if (activePage == null)
         return null;
      return activePage.getActiveEditor();
   }

   public static @Nullable IFile getActiveFile() {
      return getFile(getActiveEditor());
   }

   public static @Nullable ITextEditor getActiveTextEditor() {
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

   public static @Nullable String getActiveTextSelection() {
      final var editor = getActiveTextEditor();
      if (editor == null)
         return null;
      if (editor.getSelectionProvider().getSelection() instanceof final ITextSelection sel)
         return sel.getText();
      return null;
   }

   public static @Nullable IAnnotationModel getAnnotationModel(final @Nullable ITextEditor editor) {
      if (editor == null)
         return null;
      final var docProvider = editor.getDocumentProvider();
      if (docProvider == null)
         return null;
      return docProvider.getAnnotationModel(editor.getEditorInput());
   }

   public static @Nullable IDocument getDocument(final @Nullable ITextEditor editor) {
      if (editor == null)
         return null;
      final var docProvider = editor.getDocumentProvider();
      if (docProvider == null)
         return null;
      return docProvider.getDocument(editor.getEditorInput());
   }

   public static @Nullable IFile getFile(final @Nullable IEditorPart editor) {
      if (editor == null)
         return null;
      final var input = editor.getEditorInput();
      if (input == null)
         return null;
      return input.getAdapter(IFile.class);
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
