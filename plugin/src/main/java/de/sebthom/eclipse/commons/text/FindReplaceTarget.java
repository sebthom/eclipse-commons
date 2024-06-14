/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.text;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IFindReplaceTargetExtension;
import org.eclipse.jface.text.IFindReplaceTargetExtension3;
import org.eclipse.jface.text.IFindReplaceTargetExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.IFindReplaceTargetExtension2;
import org.eclipse.ui.texteditor.ITextEditor;

import de.sebthom.eclipse.commons.ui.Editors;

/**
 * @author Sebastian Thomschke
 */
public class FindReplaceTarget implements IFindReplaceTarget, IFindReplaceTargetExtension, IFindReplaceTargetExtension2,
      IFindReplaceTargetExtension3, IFindReplaceTargetExtension4 {

   private static @Nullable FindReplaceTarget lastFindReplaceTarget;
   private static @Nullable ITextEditor lastEditor;

   public static synchronized @Nullable FindReplaceTarget get() {
      final ITextEditor editor = Editors.getActiveTextEditor();
      if (editor == null)
         return null;

      return get(editor);
   }

   public static synchronized @Nullable FindReplaceTarget get(final ITextEditor editor) {
      if (editor == lastEditor)
         return lastFindReplaceTarget;

      final var findReplaceTarget = editor.getAdapter(IFindReplaceTarget.class);
      if (findReplaceTarget == null)
         return null;

      lastFindReplaceTarget = new FindReplaceTarget(findReplaceTarget);
      return lastFindReplaceTarget;
   }

   protected final IFindReplaceTarget target;
   protected final IFindReplaceTargetExtension targetExt;
   protected final IFindReplaceTargetExtension2 targetExt2;
   protected final IFindReplaceTargetExtension3 targetExt3;
   protected final IFindReplaceTargetExtension4 targetExt4;

   protected FindReplaceTarget(final IFindReplaceTarget target) {
      this.target = target;
      targetExt = (IFindReplaceTargetExtension) target;
      targetExt2 = (IFindReplaceTargetExtension2) target;
      targetExt3 = (IFindReplaceTargetExtension3) target;
      targetExt4 = (IFindReplaceTargetExtension4) target;
   }

   @Override
   public void beginSession() {
      targetExt.beginSession();
   }

   @Override
   public boolean canPerformFind() {
      return target.canPerformFind();
   }

   @Override
   public void endSession() {
      targetExt.endSession();
   }

   @Override
   public int findAndSelect(final int widgetOffset, final String findString, final boolean searchForward, final boolean caseSensitive,
         final boolean wholeWord) {
      return target.findAndSelect(widgetOffset, findString, searchForward, caseSensitive, wholeWord);
   }

   @Override
   public int findAndSelect(final int offset, final String findString, final boolean searchForward, final boolean caseSensitive,
         final boolean wholeWord, final boolean regExSearch) {
      return targetExt3.findAndSelect(offset, findString, searchForward, caseSensitive, wholeWord, regExSearch);
   }

   @Override
   public Point getLineSelection() {
      return targetExt.getLineSelection();
   }

   @Override
   public @Nullable IRegion getScope() {
      return targetExt.getScope();
   }

   @Override
   public Point getSelection() {
      return target.getSelection();
   }

   @Override
   public String getSelectionText() {
      return target.getSelectionText();
   }

   @Override
   public boolean isEditable() {
      return target.isEditable();
   }

   @Override
   public void replaceSelection(final String text) {
      target.replaceSelection(text);
   }

   @Override
   public void replaceSelection(final String text, final boolean regExReplace) {
      targetExt3.replaceSelection(text, regExReplace);
   }

   @Override
   public void setReplaceAllMode(final boolean replaceAll) {
      targetExt.setReplaceAllMode(replaceAll);
   }

   @Override
   public void setScope(final @Nullable IRegion scope) {
      targetExt.setScope(scope);
   }

   @Override
   public void setScopeHighlightColor(final @Nullable Color color) {
      targetExt.setScopeHighlightColor(color);
   }

   @Override
   public void setSelection(final int offset, final int length) {
      targetExt.setSelection(offset, length);
   }

   @Override
   public void setSelection(final IRegion[] ranges) {
      targetExt4.setSelection(ranges);
   }

   @Override
   public boolean validateTargetState() {
      return targetExt2.validateTargetState();
   }
}
