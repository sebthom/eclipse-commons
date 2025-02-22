/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.ui;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Sebastian Thomschke
 */
public abstract class Controls {

   protected abstract static class CompositeBuilder<SELF extends CompositeBuilder<SELF, T>, T extends Composite> extends
         ControlBuilder<SELF, T> {

      protected CompositeBuilder(final T composite) {
         super(composite);
      }

      /**
       * @see Composite#setBackgroundMode(int)
       */
      public SELF setBackgroundMode(final int backgroundMode) {
         widget.setBackgroundMode(backgroundMode);
         return self();
      }

      /**
       * @see Composite#setLayout(Layout)
       */
      public SELF setLayout(final @Nullable Layout layout) {
         widget.setLayout(layout);
         return self();
      }
   }

   protected abstract static class ControlBuilder<SELF extends ControlBuilder<SELF, T>, T extends Control> {
      protected T widget;

      protected ControlBuilder(final T control) {
         widget = control;
      }

      public T get() {
         return widget;
      }

      public SELF onFocused(final BiConsumer<T, FocusEvent> handler) {
         Controls.onFocused(widget, handler);
         return self();
      }

      public SELF onFocused(final Consumer<FocusEvent> handler) {
         Controls.onFocused(widget, handler);
         return self();
      }

      public SELF onFocused(final Runnable handler) {
         Controls.onFocused(widget, handler);
         return self();
      }

      public SELF onKeyPressed(final BiConsumer<T, KeyEvent> handler) {
         Controls.onKeyPressed(widget, handler);
         return self();
      }

      public SELF onKeyPressed(final Consumer<KeyEvent> handler) {
         Controls.onKeyPressed(widget, handler);
         return self();
      }

      @SuppressWarnings("unchecked")
      protected SELF self() {
         return (SELF) this;
      }

      /**
       * @see Control#setEnabled(boolean)
       */
      public SELF setEnabled(final boolean enabled) {
         widget.setEnabled(enabled);
         return self();
      }

      /**
       * @see Control#setLayoutData(Object)
       */
      public SELF setLayoutData(final @Nullable Object layoutData) {
         widget.setLayoutData(layoutData);
         return self();
      }
   }

   public static boolean hasStyle(final Widget widget, final int style) {
      return (widget.getStyle() & style) == style;
   }

   public static <C extends Control> FocusListener onFocused(final C control, final BiConsumer<C, FocusEvent> handler) {
      final var listener = new FocusAdapter() {
         @Override
         @NonNullByDefault({})
         public void focusGained(final FocusEvent ev) {
            handler.accept(control, ev);
         }
      };
      control.addFocusListener(listener);
      return listener;
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

   public static <C extends Control> KeyListener onKeyPressed(final C control, final BiConsumer<C, KeyEvent> handler) {
      final var listener = new KeyAdapter() {
         @Override
         @NonNullByDefault({})
         public void keyPressed(final KeyEvent ev) {
            handler.accept(control, ev);
         }
      };
      control.addKeyListener(listener);
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
