/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolItem;

import net.sf.jstuff.core.validation.Args;

/**
 * @author Sebastian Thomschke
 */
public abstract class Buttons extends Controls {

   @NonNull
   public static SelectionListener onSelected(@NonNull final Button button, @NonNull final Runnable handler) {
      Args.notNull("button", button);
      Args.notNull("handler", handler);

      final var listener = new SelectionAdapter() {
         @Override
         public void widgetSelected(final SelectionEvent ev) {
            handler.run();
         }
      };
      button.addSelectionListener(listener);
      return listener;
   }

   @NonNull
   public static SelectionListener onSelected(@NonNull final ToolItem button, @NonNull final Runnable handler) {
      Args.notNull("button", button);
      Args.notNull("handler", handler);

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
         if (radio != child && (child.getStyle() & SWT.RADIO) != 0 && child instanceof Button) {
            ((Button) child).setSelection(false);
         }
      }
      radio.setSelection(true);
   }
}
