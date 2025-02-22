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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolItem;

import net.sf.jstuff.core.ref.MutableObservableRef;

/**
 * @author Sebastian Thomschke
 */
public abstract class Buttons extends Controls {

   public static final class ButtonBuilder extends ControlBuilder<ButtonBuilder, Button> {

      private ButtonBuilder(final Composite parent, final int style) {
         super(new Button(parent, style));
      }

      /**
       * @see Button#setSelection(boolean)
       */
      public ButtonBuilder setSelection(final boolean selected) {
         widget.setSelection(selected);
         return this;
      }

      public ButtonBuilder onSelected(final BiConsumer<Button, SelectionEvent> handler) {
         Buttons.onSelected(widget, handler);
         return this;
      }

      public ButtonBuilder onSelected(final Consumer<SelectionEvent> handler) {
         Buttons.onSelected(widget, handler);
         return this;
      }

      public ButtonBuilder onSelected(final Runnable handler) {
         Buttons.onSelected(widget, handler);
         return this;
      }

      /**
       * @see Button#setAlignment(int)
       */
      public ButtonBuilder setAlignment(final int alignment) {
         widget.setAlignment(alignment);
         return this;
      }

      /**
       * @see Button#setText(String)
       */
      public ButtonBuilder setText(final String text) {
         widget.setText(text);
         return this;
      }
   }

   public static ButtonBuilder create(final Composite parent) {
      return create(parent, SWT.NONE);
   }

   public static ButtonBuilder create(final Composite parent, final int style) {
      return new ButtonBuilder(parent, style);
   }

   public static ButtonBuilder create(final String text, final Composite parent) {
      return create(parent).setText(text);
   }

   public static ButtonBuilder create(final String text, final Composite parent, final int style) {
      return create(parent, style).setText(text);
   }

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
