/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.List;

import net.sf.jstuff.core.validation.Args;

/**
 * @author Sebastian Thomschke
 */
public abstract class Lists extends Controls {

   @NonNull
   public static SelectionListener onSelectionChanged(@NonNull final List list, @NonNull final Consumer<SelectionEvent> handler) {
      Args.notNull("list", list);
      Args.notNull("handler", handler);

      final var listener = new SelectionAdapter() {
         @Override
         public void widgetSelected(final SelectionEvent ev) {
            handler.accept(ev);
         }
      };
      list.addSelectionListener(listener);
      return listener;
   }

   @NonNull
   public static SelectionListener onSelectionChanged(@NonNull final List list, @NonNull final BiConsumer<List, SelectionEvent> handler) {
      Args.notNull("list", list);
      Args.notNull("handler", handler);

      final var listener = new SelectionAdapter() {
         @Override
         public void widgetSelected(final SelectionEvent ev) {
            handler.accept(list, ev);
         }
      };
      list.addSelectionListener(listener);
      return listener;
   }

   @NonNull
   public static SelectionListener onSelectionChanged(@NonNull final List list, @NonNull final Runnable handler) {
      Args.notNull("list", list);
      Args.notNull("handler", handler);

      final var listener = new SelectionAdapter() {
         @Override
         public void widgetSelected(final SelectionEvent ev) {
            handler.run();
         }
      };
      list.addSelectionListener(listener);
      return listener;
   }
}
