/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Sebastian Thomschke
 */
public abstract class Tables {

   public static void autoResizeColumns(final TableViewer tableViewer) {
      autoResizeColumns(tableViewer.getTable());
   }

   public static void autoResizeColumns(final Table table) {
      for (final TableColumn column : table.getColumns()) {
         column.pack();
      }
   }

   public static void setLastColumnAutoExpand(final Table table, final int minWidth) {
      table.addControlListener(new ControlAdapter() {
         final Set<TableColumn> monitoredColumns = new HashSet<>();

         @Override
         public synchronized void controlResized(@NonNullByDefault({}) final ControlEvent event) {
            final var cols = table.getColumns();
            if (cols.length == 0)
               return;

            if (cols.length == 1) {
               cols[0].setWidth(Math.max(minWidth, table.getClientArea().width));
               return;
            }

            var totalWidthOfOtherColumns = 0;
            for (var i = 0; i < cols.length - 1; i++) {
               final var col = cols[i];
               if (!monitoredColumns.contains(col)) {
                  col.addControlListener(this);
                  monitoredColumns.add(col);
               }
               totalWidthOfOtherColumns += cols[i].getWidth();
            }
            cols[cols.length - 1].setWidth(Math.max(minWidth, table.getClientArea().width - totalWidthOfOtherColumns));
         }
      });
   }
}
