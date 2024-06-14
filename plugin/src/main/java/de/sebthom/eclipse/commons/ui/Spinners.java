/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.ui;

import java.util.function.Consumer;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Spinner;

import net.sf.jstuff.core.ref.MutableObservableRef;
import net.sf.jstuff.core.validation.Args;

/**
 * @author Sebastian Thomschke
 */
public abstract class Spinners {

   /**
    * two-way bind
    */
   public static void bind(final Spinner spinner, final MutableObservableRef<Integer> model) {
      final Consumer<Integer> onModelChanged = newValue -> UI.run(() -> {
         final int selection = newValue;
         if (spinner.getSelection() != selection) {
            spinner.setSelection(selection);
         }
      });
      model.subscribe(onModelChanged);

      spinner.setSelection(model.get());
      spinner.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(final SelectionEvent ev) {
            model.set(spinner.getSelection());
         }
      });

      spinner.addDisposeListener(ev -> model.unsubscribe(onModelChanged));
   }

   public static SelectionListener onSelected(final Spinner spinner, final Runnable handler) {
      Args.notNull("spinner", spinner);
      Args.notNull("handler", handler);

      final var listener = new SelectionAdapter() {
         @Override
         public void widgetSelected(final SelectionEvent ev) {
            handler.run();
         }
      };
      spinner.addSelectionListener(listener);
      return listener;
   }
}
