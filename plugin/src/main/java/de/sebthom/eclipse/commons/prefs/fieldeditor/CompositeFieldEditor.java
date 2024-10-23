/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.prefs.fieldeditor;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.sebthom.eclipse.commons.ui.UI;

/**
 * @author Sebastian Thomschke
 */
public abstract class CompositeFieldEditor<T extends Control> extends FieldEditor {

   private boolean isValid;
   protected @Nullable T widget;

   /**
    * @param preferenceKey the name of the preference this field editor works on
    * @param labelText the label text of the field editor
    * @param parent the parent of the field editor's control
    */
   protected CompositeFieldEditor(final String preferenceKey, final String labelText, final Composite parent) {
      super(preferenceKey, labelText, parent);
   }

   @Override
   protected void adjustForNumColumns(final int numColumns) {
      if (widget != null && widget.getLayoutData() instanceof final GridData gd) {
         gd.horizontalSpan = numColumns - 1;
      }
   }

   protected abstract T createWidget(Composite parent);

   @Override
   protected void doFillIntoGrid(final Composite parent, final int numColumns) {
      getLabelControl(parent).setLayoutData(new GridData());

      final var widget = this.widget = getOrCreateWidget(parent);
      final var gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.verticalAlignment = GridData.FILL;
      gd.horizontalSpan = numColumns - 1;
      gd.grabExcessHorizontalSpace = true;
      widget.setLayoutData(gd);
   }

   @Override
   protected void refreshValidState() {
      final var isValidOld = isValid;
      isValid = isValid();
      if (isValid != isValidOld) {
         fireStateChanged(IS_VALID, isValidOld, isValid);
      }
   }

   /**
    * Informs this field editor's listener, if it has one, about a change to the value ({@link #VALUE} property) provided
    * that the old and new values are different.
    */
   protected boolean fireValueChanged(final Object oldValue, final Object newValue, final boolean isDefaultValue) {
      setPresentsDefaultValue(isDefaultValue);
      refreshValidState();

      if (!Objects.equals(newValue, oldValue)) {
         fireValueChanged(VALUE, oldValue, newValue);
         return true;
      }
      return false;
   }

   @Override
   public int getNumberOfControls() {
      return 2;
   }

   private T getOrCreateWidget(final Composite parent) {
      var widget = this.widget;
      if (widget == null) {
         widget = this.widget = createWidget(parent);
         widget.setFont(parent.getFont());
         widget.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(final ControlEvent e) {
               UI.runAsync(((Composite) e.widget)::layout); // workaround for redraw issues of fields inside the group
            }
         });
         widget.addDisposeListener(event -> this.widget = null);
      } else {
         checkParent(widget, parent);
      }
      return widget;
   }

   /**
    * Returns this field editor's widget control.
    *
    * @return the widget control
    */
   public T getWidget() {
      if (widget != null)
         return widget;
      throw new IllegalStateException("widget is null");

   }

   @Override
   public void setEnabled(final boolean enabled, final Composite parent) {
      super.setEnabled(enabled, parent);
      final var widget = this.widget;
      if (widget != null) {
         widget.setEnabled(enabled);
      }
   }

   @Override
   public void setFocus() {
      final var widget = this.widget;
      if (widget != null && !widget.isDisposed()) {
         widget.setFocus();
      }
   }
}
