/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.ui;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import de.sebthom.eclipse.commons.internal.EclipseCommonsPlugin;
import de.sebthom.eclipse.commons.resources.Resources;

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

   public static @Nullable IFile getFile(final @Nullable IEditorInput input) {
      if (input == null)
         return null;
      if (input instanceof final IFileEditorInput fileInput)
         return fileInput.getFile();
      return input.getAdapter(IFile.class);
   }

   /**
    * Use {@link #getFilePath(IEditorPart)} to get the file input more reliably.
    *
    * @return null if editor is not using {@link IFileEditorInput} and if file is outside of workspace
    */
   public static @Nullable IFile getFile(final @Nullable IEditorPart editor) {
      if (editor == null)
         return null;
      return getFile(editor.getEditorInput());
   }

   public static @Nullable Path getFilePath(final @Nullable IEditorInput input) {
      if (input == null)
         return null;
      if (input instanceof final IFileEditorInput fileInput)
         return Resources.toAbsolutePath(fileInput.getFile());
      if (input instanceof final IPathEditorInput uriInput)
         return Resources.toAbsolutePath(uriInput.getPath());
      if (input instanceof final IURIEditorInput uriInput) {
         final URI uri = uriInput.getURI();
         if ("file".equals(uri.getScheme()))
            return Paths.get(uriInput.getURI());
      }
      return input.getAdapter(Path.class);
   }

   public static @Nullable Path getFilePath(final @Nullable IEditorPart editor) {
      if (editor == null)
         return null;
      return getFilePath(editor.getEditorInput());
   }

   public static String getText(final @Nullable ITextEditor editor) {
      if (editor == null)
         return "";
      final var doc = getDocument(editor);
      if (doc == null)
         return "";
      return doc.get();
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
            || replacement.equals(sel.getText()))
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
