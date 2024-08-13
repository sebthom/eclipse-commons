/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.prefs.fieldeditor;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * A pseudo field editor for displaying a horizontal separator.
 *
 * @author Sebastian Thomschke
 */
public class HorizontalSeparatorField extends FieldEditor {

   private @Nullable Label separator;
   private @Nullable GridData separatorLayoutData;

   public HorizontalSeparatorField(final Composite parent) {
      super("unused", "", parent);
   }

   @Override
   protected void adjustForNumColumns(final int numColumns) {
      final var labelLayoutData = separatorLayoutData;
      if (labelLayoutData != null) {
         labelLayoutData.horizontalSpan = numColumns;
      } else {
         separatorLayoutData = new GridData(GridData.FILL, GridData.CENTER, false, false, numColumns, 1);
      }
   }

   @Override
   protected void doFillIntoGrid(final Composite parent, final int numColumns) {
      adjustForNumColumns(numColumns);
      getLabelControl(parent).setLayoutData(separatorLayoutData);
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
   public Label getLabelControl(final Composite parent) {
      var separator = this.separator;
      if (separator == null || separator.isDisposed()) {
         separator = this.separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
      } else {
         checkParent(separator, parent);
      }
      return separator;
   }

   @Override
   public int getNumberOfControls() {
      return 1;
   }

}
