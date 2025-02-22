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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

/**
 * @author Sebastian Thomschke
 */
public abstract class Links extends Controls {

   public static final class LinkBuilder extends ControlBuilder<LinkBuilder, Link> {

      LinkBuilder(final Composite parent, final int style) {
         super(new Link(parent, style));
      }

      public LinkBuilder onSelected(final BiConsumer<Link, SelectionEvent> handler) {
         Links.onSelected(widget, handler);
         return this;
      }

      public LinkBuilder onSelected(final Consumer<SelectionEvent> handler) {
         Links.onSelected(widget, handler);
         return this;
      }

      public LinkBuilder onSelected(final Runnable handler) {
         Links.onSelected(widget, handler);
         return this;
      }

      public LinkBuilder setText(final String text) {
         widget.setText(text);
         return this;
      }
   }

   public static LinkBuilder create(final Composite parent) {
      return create(parent, SWT.NONE);
   }

   public static LinkBuilder create(final Composite parent, final int style) {
      return new LinkBuilder(parent, style);
   }

   public static LinkBuilder create(final String text, final Composite parent) {
      return create(parent).setText(text);
   }

   public static LinkBuilder create(final String text, final Composite parent, final int style) {
      return create(parent, style).setText(text);
   }

   public static SelectionListener onSelected(final Link link, final BiConsumer<Link, SelectionEvent> handler) {
      final var listener = new SelectionAdapter() {
         @Override
         public void widgetSelected(final SelectionEvent ev) {
            handler.accept(link, ev);
         }
      };
      link.addSelectionListener(listener);
      return listener;
   }

   public static SelectionListener onSelected(final Link link, final Consumer<SelectionEvent> handler) {
      final var listener = widgetSelectedAdapter(handler);
      link.addSelectionListener(listener);
      return listener;
   }

   public static SelectionListener onSelected(final Link link, final Runnable handler) {
      final var listener = new SelectionAdapter() {
         @Override
         public void widgetSelected(final SelectionEvent ev) {
            handler.run();
         }
      };
      link.addSelectionListener(listener);
      return listener;
   }

}
