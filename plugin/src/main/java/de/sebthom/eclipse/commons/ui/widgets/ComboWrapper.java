/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import de.sebthom.eclipse.commons.ui.UI;
import net.sf.jstuff.core.functional.TriConsumer;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.ref.MutableObservableRef;

/**
 * @author Sebastian Thomschke
 */
@NonNullByDefault
public class ComboWrapper<E> {
   private static final Logger LOG = Logger.create();
   private static final IStructuredSelection EMPTY_SELECTION = new StructuredSelection();

   private Combo combo;
   private ComboViewer viewer;

   private CopyOnWriteArrayList<TriConsumer<ComboWrapper<E>, Collection<E>, Collection<E>>> itemsChangedListener = new CopyOnWriteArrayList<>();

   public ComboWrapper(final Combo combo) {
      this.combo = combo;
      this.viewer = new ComboViewer(combo);
   }

   public ComboWrapper(final Composite parent, final int style, final Object layoutData) {
      combo = new Combo(parent, style);
      combo.setLayoutData(layoutData);
      this.viewer = new ComboViewer(combo);
   }

   public ComboWrapper<E> clearSelection() {
      combo.clearSelection();
      return this;
   }

   public Combo getCombo() {
      return combo;
   }

   @SuppressWarnings("unchecked")
   public E getItemAt(final int index) {
      return (E) viewer.getElementAt(index);
   }

   public int getItemCount() {
      return combo.getItemCount();
   }

   @SuppressWarnings("unchecked")
   public Collection<E> getItems() {
      return (Collection<E>) viewer.getInput();
   }

   @SuppressWarnings("unchecked")
   public E getSelection() {
      return (E) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
   }

   public boolean isEnabled() {
      return combo.isEnabled();
   }

   public ComboWrapper<E> bind(final MutableObservableRef<@Nullable E> model) {
      final Consumer<@Nullable E> onModelChanged = newValue -> UI.run(() -> setSelection(newValue));
      model.subscribe(onModelChanged);

      setSelection(model.get());
      onSelectionChanged(model::set);

      combo.addDisposeListener(ev -> model.unsubscribe(onModelChanged));
      return this;
   }

   public ComboWrapper<E> onItemsChanged(final TriConsumer<ComboWrapper<E>, Collection<E>, Collection<E>> listener) {
      itemsChangedListener.add(listener);
      return this;
   }

   @SuppressWarnings("unchecked")
   public ComboWrapper<E> onSelectionChanged(final Consumer<E> listener) {
      viewer.addSelectionChangedListener(event -> listener.accept((E) event.getStructuredSelection().getFirstElement()));
      return this;
   }

   public ComboWrapper<E> setEnabled(final boolean enabled) {
      combo.setEnabled(enabled);
      return this;
   }

   public ComboWrapper<E> setItems(final Collection<E> items) {
      viewer.setContentProvider((IStructuredContentProvider) input -> {
         final var coll = (Collection<?>) input;
         return coll.toArray(Object[]::new);
      });
      @SuppressWarnings("unchecked")
      final var old = (Collection<E>) viewer.getInput();
      viewer.setInput(items);
      for (final var l : itemsChangedListener) {
         try {
            l.accept(this, old, items);
         } catch (final Exception ex) {
            LOG.error(ex);
         }
      }
      return this;
   }

   public ComboWrapper<E> setItems(final Collection<E> items, final Comparator<E> comparator) {
      viewer.setContentProvider((IStructuredContentProvider) input -> {
         @SuppressWarnings("unchecked")
         final var list = new ArrayList<>((Collection<E>) input);
         list.sort(comparator);
         return list.toArray(Object[]::new);
      });
      @SuppressWarnings("unchecked")
      final var old = (Collection<E>) viewer.getInput();
      viewer.setInput(items);
      for (final var l : itemsChangedListener) {
         try {
            l.accept(this, old, items);
         } catch (final Exception ex) {
            LOG.error(ex);
         }
      }
      return this;
   }

   public ComboWrapper<E> setItems(@SuppressWarnings("unchecked") final E... items) {
      return setItems(Arrays.asList(items));
   }

   public ComboWrapper<E> setItems(final E[] items, final Comparator<E> comparator) {
      return setItems(Arrays.asList(items), comparator);
   }

   public ComboWrapper<E> setLabelComparator(@Nullable final Comparator<? super String> comparator) {
      if (comparator == null) {
         viewer.setComparator(null);
      } else {
         viewer.setComparator(new ViewerComparator(comparator));
      }
      return this;
   }

   public ComboWrapper<E> setLabelProvider(final Function<E, String> provider) {
      viewer.setLabelProvider(new LabelProvider() {
         @SuppressWarnings("unchecked")
         @Override
         public String getText(@Nullable final Object item) {
            return provider.apply((E) item);
         }
      });
      return this;
   }

   public ComboWrapper<E> setSelection(@Nullable final E item) {
      final var currentSelection = getSelection();
      if (currentSelection == item)
         return this;

      viewer.setSelection(item == null ? EMPTY_SELECTION : new StructuredSelection(item));
      return this;
   }

   public ComboWrapper<E> setSelection(@Nullable final E item, final boolean reveal) {
      final var currentSelection = getSelection();
      if (currentSelection == item)
         return this;

      viewer.setSelection(item == null ? EMPTY_SELECTION : new StructuredSelection(item), reveal);
      return this;
   }
}
