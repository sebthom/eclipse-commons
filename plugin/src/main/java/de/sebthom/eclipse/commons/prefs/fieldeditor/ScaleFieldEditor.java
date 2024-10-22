/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.prefs.fieldeditor;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;

/**
 * @author Sebastian Thomschke
 */
public abstract class ScaleFieldEditor extends CompositeFieldEditor<Scale> {

   /**
    * Old integer value.
    */
   private int oldValue;

   /**
    * Creates a spinner field editor.
    *
    * @param preferenceKey the name of the preference this field editor works on
    * @param labelText the label text of the field editor
    * @param parent the parent of the field editor's control
    */
   protected ScaleFieldEditor(final String preferenceKey, final String labelText, final Composite parent) {
      super(preferenceKey, labelText, parent);
   }

   protected abstract Scale createScale(Composite parent);

   @Override
   protected final Scale createWidget(final Composite parent) {
      final var widget = createScale(parent);
      widget.addSelectionListener(widgetSelectedAdapter(e -> {
         final var value = widget.getSelection();
         fireValueChanged(oldValue, value, false);
         oldValue = value;
         widget.setToolTipText(Integer.toString(value));
      }));
      return widget;
   }

   @Override
   protected void doLoad() {
      final var widget = this.widget;
      if (widget != null) {
         final int value = getPreferenceStore().getInt(getPreferenceName());
         widget.setSelection(value);
         widget.setToolTipText(Integer.toString(value));
         oldValue = value;
      }
   }

   @Override
   protected void doLoadDefault() {
      final var widget = this.widget;
      if (widget != null) {
         final int value = getPreferenceStore().getDefaultInt(getPreferenceName());
         widget.setSelection(value);
         widget.setToolTipText(Integer.toString(value));
         fireValueChanged(oldValue, value, true);
      }
   }

   @Override
   protected void doStore() {
      final var widget = this.widget;
      if (widget != null) {
         getPreferenceStore().setValue(getPreferenceName(), widget.getSelection());
      }
   }
}
