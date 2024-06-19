/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.prefs.fieldeditor;

import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * An extended version of {@link IntegerFieldEditor} that ensures only digits can be entered.
 *
 * @author Sebastian Thomschke
 */
public class IntFieldEditor extends IntegerFieldEditor {

   protected IntFieldEditor() {
   }

   public IntFieldEditor(final String name, final String labelText, final Composite parent, final int textLimit) {
      super(name, labelText, parent, textLimit);
   }

   public IntFieldEditor(final String name, final String labelText, final Composite parent) {
      super(name, labelText, parent);
   }

   @Override
   protected Text createTextWidget(final Composite parent) {
      final var text = super.createTextWidget(parent);
      text.addVerifyListener(e -> {
         if (!e.text.matches("\\d*")) {
            e.doit = false;
         }
      });
      return text;
   }
}
