/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Text;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.ref.ObservableRef;
import net.sf.jstuff.core.validation.Args;

/**
 * @author Sebastian Thomschke
 */
public abstract class Texts extends Controls {

   /**
    * two-way bind
    */
   public static <E> void bind(@NonNull final Text widget, @NonNull final ObservableRef<E> model,
      @NonNull final Function<String, E> widget2model, @NonNull final Function<E, String> model2widget) {
      Args.notNull("widget", widget);
      Args.notNull("model", model);
      Args.notNull("widget2model", widget2model);
      Args.notNull("model2widget", model2widget);

      final var initialVal = model.get();
      if (initialVal != null) {
         widget.setText(model2widget.apply(initialVal));
      }

      widget.addModifyListener(ev -> model.set(widget2model.apply(widget.getText())));

      final Consumer<E> onModelChanged = newValue -> UI.run(() -> {
         final var oldTxt = widget.getText();
         var newTxt = model2widget.apply(newValue);
         if (newTxt == null) {
            newTxt = "";
         }
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
   public static void bind(@NonNull final Text widget, @NonNull final ObservableRef<String> model) {
      Args.notNull("widget", widget);
      Args.notNull("model", model);

      final var initialTxt = model.get();
      if (Strings.isNotEmpty(initialTxt)) {
         widget.setText(initialTxt);
      }

      widget.addModifyListener(ev -> model.set(widget.getText()));

      final Consumer<String> onModelChanged = newValue -> UI.run(() -> {
         final var oldTxt = widget.getText();
         final var newTxt = newValue == null ? "" : newValue;
         if (!Objects.equals(oldTxt, newTxt)) {
            widget.setText(newTxt);
         }
      });
      model.subscribe(onModelChanged);
      widget.addDisposeListener(ev -> model.unsubscribe(onModelChanged));
   }

   public static void onModified(@NonNull final Text text, @NonNull final BiConsumer<Text, ModifyEvent> handler) {
      Args.notNull("text", text);
      Args.notNull("handler", handler);

      text.addModifyListener(ev -> handler.accept(text, ev));
   }

   public static void onModified(@NonNull final Text text, @NonNull final Runnable handler) {
      Args.notNull("text", text);
      Args.notNull("handler", handler);

      text.addModifyListener(ev -> handler.run());
   }
}
