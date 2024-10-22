/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.ui;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolItem;

import net.sf.jstuff.core.ref.MutableObservableRef;

/**
 * @author Sebastian Thomschke
 */
public abstract class Buttons extends Controls {

   /**
    * two-way bind
    */
   public static void bind(final Button button, final MutableObservableRef<Boolean> model) {
      final Consumer<Boolean> onModelChanged = newValue -> UI.run(() -> {
         final boolean select = newValue;
         if (button.getSelection() != select) {
            button.setSelection(select);
         }
      });
      model.subscribe(onModelChanged);

      button.setSelection(model.get());
      button.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(final SelectionEvent ev) {
            model.set(button.getSelection());
         }
      });

      button.addDisposeListener(ev -> model.unsubscribe(onModelChanged));
   }

   public static SelectionListener onSelected(final Button button, final BiConsumer<Button, SelectionEvent> handler) {
      final var listener = new SelectionAdapter() {
         @Override
         @NonNullByDefault({})
         public void widgetSelected(final SelectionEvent ev) {
            handler.accept(button, ev);
         }
      };
      button.addSelectionListener(listener);
      return listener;
   }

   public static SelectionListener onSelected(final Button button, final Consumer<SelectionEvent> handler) {
      final var listener = widgetSelectedAdapter(handler);
      button.addSelectionListener(listener);
      return listener;
   }

   public static SelectionListener onSelected(final Button button, final Runnable handler) {
      final var listener = new SelectionAdapter() {
         @Override
         public void widgetSelected(final SelectionEvent ev) {
            handler.run();
         }
      };
      button.addSelectionListener(listener);
      return listener;
   }

   public static SelectionListener onSelected(final ToolItem button, final BiConsumer<ToolItem, SelectionEvent> handler) {
      final var listener = new SelectionAdapter() {
         @Override
         @NonNullByDefault({})
         public void widgetSelected(final SelectionEvent ev) {
            handler.accept(button, ev);
         }
      };
      button.addSelectionListener(listener);
      return listener;
   }

   public static SelectionListener onSelected(final ToolItem button, final Consumer<SelectionEvent> handler) {
      final var listener = widgetSelectedAdapter(handler);
      button.addSelectionListener(listener);
      return listener;
   }

   public static SelectionListener onSelected(final ToolItem button, final Runnable handler) {
      final var listener = new SelectionAdapter() {
         @Override
         public void widgetSelected(final SelectionEvent ev) {
            handler.run();
         }
      };
      button.addSelectionListener(listener);
      return listener;
   }

   /**
    * https://stackoverflow.com/questions/5835618/swt-set-radio-buttons-programmatically
    */
   public static void selectRadio(final Button radio) {
      for (final Control child : radio.getParent().getChildren()) {
         if (radio != child && (child.getStyle() & SWT.RADIO) != 0 && child instanceof final Button button) {
            button.setSelection(false);
         }
      }
      radio.setSelection(true);
   }
}
