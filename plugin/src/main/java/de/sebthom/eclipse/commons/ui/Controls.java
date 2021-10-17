/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;

import net.sf.jstuff.core.validation.Args;

/**
 * @author Sebastian Thomschke
 */
public abstract class Controls {

   public static void onFocused(@NonNull final Control control, @NonNull final Runnable handler) {
      Args.notNull("control", control);
      Args.notNull("handler", handler);

      control.addFocusListener(new FocusAdapter() {
         @Override
         public void focusGained(final FocusEvent e) {
            handler.run();
         }
      });
   }

   public static void onKeyPressed(@NonNull final Control control, @NonNull final Consumer<KeyEvent> handler) {
      Args.notNull("control", control);
      Args.notNull("handler", handler);

      control.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(final KeyEvent ev) {
            handler.accept(ev);
         }
      });
   }
}
