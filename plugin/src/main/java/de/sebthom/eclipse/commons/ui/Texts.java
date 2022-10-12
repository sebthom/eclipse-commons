/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.ref.MutableObservableRef;

/**
 * @author Sebastian Thomschke
 */
public abstract class Texts extends Controls {

   /**
    * two-way bind
    */
   @NonNullByDefault({}) // make <E>,<String> unspecified, i.e. allow @Nullable or @NonNull
   public static <E> void bind(final @NonNull Text widget, final @NonNull MutableObservableRef<E> model,
      final @NonNull Function<@NonNull String, E> widget2model, final Function<E, String> model2widget) {
      final var initialVal = model.get();
      if (initialVal != null) {
         widget.setText(model2widget.apply(initialVal));
      }

      widget.addModifyListener(ev -> model.set(widget2model.apply(widget.getText())));

      final Consumer<E> onModelChanged = newValue -> UI.run(() -> {
         final var oldTxt = widget.getText();
         final var newTxt = Strings.emptyIfNull(model2widget.apply(newValue));
         if (!Objects.equals(oldTxt, newTxt)) {
            widget.setText(newTxt);
         }
      });
      model.subscribe(onModelChanged);
      widget.addDisposeListener(ev -> model.unsubscribe(onModelChanged));
   }

   /**
    * two-way bind
    */
   @NonNullByDefault({}) // make <String> unspecified, i.e. allow @Nullable or @NonNull
   public static void bind(final @NonNull Text widget, final @NonNull MutableObservableRef<String> model) {
      final Consumer<String> onModelChanged = newValue -> UI.run(() -> {
         final var oldTxt = widget.getText();
         final var newTxt = Strings.emptyIfNull(newValue);
         if (!Objects.equals(oldTxt, newTxt)) {
            widget.setText(newTxt);
         }
      });
      model.subscribe(onModelChanged);

      final var initialTxt = model.get();
      widget.setText(Strings.emptyIfNull(initialTxt));
      widget.addModifyListener(ev -> model.set(widget.getText()));

      widget.addDisposeListener(ev -> model.unsubscribe(onModelChanged));
   }

   public static ModifyListener onModified(final Text text, final BiConsumer<Text, ModifyEvent> handler) {
      final ModifyListener listener = ev -> handler.accept(text, ev);
      text.addModifyListener(listener);
      return listener;
   }

   public static ModifyListener onModified(final Text text, final Consumer<String> handler) {
      final ModifyListener listener = ev -> handler.accept(text.getText());
      text.addModifyListener(listener);
      return listener;
   }

   public static ModifyListener onModified(final Text text, final Runnable handler) {
      final ModifyListener listener = ev -> handler.run();
      text.addModifyListener(listener);
      return listener;
   }
}
