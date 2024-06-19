/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.prefs.fieldeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import de.sebthom.eclipse.commons.ui.UI;

/**
 * @author Sebastian Thomschke
 */
public class GroupFieldEditor extends FieldEditor {

   private static final int NUM_COLUMNS = 2;

   private final Group fieldGroup;
   private final GridData fieldGroupLayoutData;
   private final List<FieldEditor> editors;

   public GroupFieldEditor(final String title, final Composite parent, final Function<Composite, List<FieldEditor>> fieldEditorsFactory) {
      fieldGroup = new Group(parent, SWT.DEFAULT);
      fieldGroup.setText(title);
      fieldGroupLayoutData = new GridData( //
         GridData.FILL, GridData.CENTER, //
         true, false, //
         parent.getLayout() instanceof final GridLayout gl ? gl.numColumns : 1, 1);
      fieldGroup.setLayoutData(fieldGroupLayoutData);
      fieldGroup.addControlListener(new ControlAdapter() {
         @Override
         public void controlResized(final ControlEvent e) {
            UI.runAsync(fieldGroup::layout); // workaround for redraw issues of fields inside the group
         }
      });

      editors = new ArrayList<>(fieldEditorsFactory.apply(fieldGroup));
      editors.forEach(e -> {
         e.fillIntoGrid(fieldGroup, NUM_COLUMNS);
         e.setPropertyChangeListener(p -> fireValueChanged(p.getProperty(), p.getOldValue(), p.getNewValue()));
      });

      // needs to be set after field editors were added
      fieldGroup.setLayout(GridLayoutFactory.swtDefaults().numColumns(NUM_COLUMNS).create());
   }

   @Override
   protected void adjustForNumColumns(final int numColumns) {
      fieldGroupLayoutData.verticalSpan = numColumns;
   }

   @Override
   protected void doFillIntoGrid(final Composite parent, final int numColumns) {
      adjustForNumColumns(numColumns);
   }

   @Override
   protected void doLoad() {
      editors.forEach(FieldEditor::load);
   }

   @Override
   protected void doLoadDefault() {
      editors.forEach(FieldEditor::loadDefault);
   }

   @Override
   protected void doStore() {
      editors.forEach(FieldEditor::store);
   }

   @Override
   public int getNumberOfControls() {
      return 1;
   }

   @Override
   public void setEnabled(final boolean enabled, final Composite parent) {
      editors.forEach(e -> e.setEnabled(enabled, fieldGroup));
   }

   @Override
   public void setFocus() {
      editors.stream().findFirst().ifPresent(FieldEditor::setFocus);
   }

   @Override
   public void setPreferenceStore(final @Nullable IPreferenceStore store) {
      super.setPreferenceStore(store);
      editors.forEach(e -> e.setPreferenceStore(store));
   }

   @Override
   public boolean isValid() {
      return editors.stream().allMatch(FieldEditor::isValid);
   }
}
