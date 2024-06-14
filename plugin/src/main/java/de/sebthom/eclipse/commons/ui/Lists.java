/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.ui;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.List;

/**
 * @author Sebastian Thomschke
 */
public abstract class Lists extends Controls {

   public static SelectionListener onSelectionChanged(final List list, final Consumer<SelectionEvent> handler) {
      final var listener = new SelectionAdapter() {
         @Override
         @NonNullByDefault({})
         public void widgetSelected(final SelectionEvent ev) {
            handler.accept(ev);
         }
      };
      list.addSelectionListener(listener);
      return listener;
   }

   public static SelectionListener onSelectionChanged(final List list, final BiConsumer<List, SelectionEvent> handler) {
      final var listener = new SelectionAdapter() {
         @Override
         @NonNullByDefault({})
         public void widgetSelected(final SelectionEvent ev) {
            handler.accept(list, ev);
         }
      };
      list.addSelectionListener(listener);
      return listener;
   }

   public static SelectionListener onSelectionChanged(final List list, final Runnable handler) {
      final var listener = new SelectionAdapter() {
         @Override
         @NonNullByDefault({})
         public void widgetSelected(final SelectionEvent ev) {
            handler.run();
         }
      };
      list.addSelectionListener(listener);
      return listener;
   }
}
