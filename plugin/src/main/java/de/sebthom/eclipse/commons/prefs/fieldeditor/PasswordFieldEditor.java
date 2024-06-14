/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.prefs.fieldeditor;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Sebastian Thomschke
 */
public class PasswordFieldEditor extends StringFieldEditor {

   public PasswordFieldEditor(final String name, final String labelText, final Composite parent) {
      super(name, labelText, parent);
   }

   public PasswordFieldEditor(final String name, final String labelText, final int width, final Composite parent) {
      super(name, labelText, width, parent);
   }

   public PasswordFieldEditor(final String name, final String labelText, final int width, final int strategy, final Composite parent) {
      super(name, labelText, width, strategy, parent);
   }

   public PasswordFieldEditor(final String name, final String labelText, final int widthInChars, final int heigthInChars,
         final int strategy, final Composite parent) {
      super(name, labelText, widthInChars, heigthInChars, strategy, parent);
   }

   @Override
   protected void doFillIntoGrid(final Composite parent, final int numColumns) {
      super.doFillIntoGrid(parent, numColumns);
      getTextControl().setEchoChar('*');
   }
}
