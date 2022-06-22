/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.prefs.fieldeditor;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * A pseudo field editor for displaying a label.
 *
 * @author Sebastian Thomschke
 */
@NonNullByDefault
public class LabelFieldEditor extends FieldEditor {

   @Nullable
   private GridData labelLayoutData;

   public LabelFieldEditor(@Nullable final String value, final Composite parent) {
      super("unused", value, parent);
   }

   @Override
   protected void adjustForNumColumns(final int numColumns) {
      final var labelLayoutData = this.labelLayoutData;
      if (labelLayoutData != null) {
         labelLayoutData.horizontalSpan = numColumns;
      }
   }

   @Override
   protected void doFillIntoGrid(@Nullable final Composite parent, final int numColumns) {
      labelLayoutData = new GridData(GridData.FILL, GridData.CENTER, false, false, numColumns, 1);
      getLabelControl(parent).setLayoutData(labelLayoutData);
   }

   @Override
   protected void doLoad() {
      // ignored
   }

   @Override
   protected void doLoadDefault() {
      // ignored
   }

   @Override
   protected void doStore() {
      // ignored
   }

   @Override
   public int getNumberOfControls() {
      return 1;
   }
}
