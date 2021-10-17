/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

/**
 * @author Sebastian Thomschke
 */
public abstract class Buttons extends Controls {

   public static void onSelected(final Button button, final Runnable handler) {
      button.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(final SelectionEvent ev) {
            handler.run();
         }
      });
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
