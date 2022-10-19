/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Sebastian Thomschke
 */
public abstract class Controls {

   public static boolean hasStyle(final Widget widget, final int style) {
      return (widget.getStyle() & style) == style;
   }

   public static FocusListener onFocused(final Control control, final Consumer<FocusEvent> handler) {
      final var listener = new FocusAdapter() {

         @Override
         @NonNullByDefault({})
         public void focusGained(final FocusEvent ev) {
            handler.accept(ev);
         }
      };
      control.addFocusListener(listener);
      return listener;
   }

   public static FocusListener onFocused(final Control control, final Runnable handler) {

      final var listener = new FocusAdapter() {
         @Override
         @NonNullByDefault({})
         public void focusGained(final FocusEvent ev) {
            handler.run();
         }
      };
      control.addFocusListener(listener);
      return listener;
   }

   public static KeyListener onKeyPressed(final Control control, final Consumer<KeyEvent> handler) {
      final var listener = new KeyAdapter() {
         @Override
         @NonNullByDefault({})
         public void keyPressed(final KeyEvent ev) {
            handler.accept(ev);
         }
      };
      control.addKeyListener(listener);
      return listener;
   }

}
