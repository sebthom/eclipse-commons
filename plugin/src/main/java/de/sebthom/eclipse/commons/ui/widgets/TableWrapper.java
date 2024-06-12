/*
 * Copyright 2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import de.sebthom.eclipse.commons.ui.UI;
import net.sf.jstuff.core.functional.TriConsumer;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.ref.MutableObservableRef;

/**
 * @author Sebastian Thomschke
 */
public class TableWrapper<E> {

   private static final Logger LOG = Logger.create();
   private static final IStructuredSelection EMPTY_SELECTION = new StructuredSelection();

   private Table table;
   private TableViewer viewer;

   private CopyOnWriteArrayList<TriConsumer<TableWrapper<E>, Collection<E>, Collection<E>>> itemsChangedListener = new CopyOnWriteArrayList<>();

   public TableWrapper(final TableViewer viewer) {
      table = viewer.getTable();
      this.viewer = viewer;
   }

   public TableWrapper(final Table table) {
      this.table = table;
      viewer = new TableViewer(table);
   }

   public TableWrapper(final Composite parent, final int style, final @Nullable Object layoutData) {
      table = new Table(parent, style);
      table.setLayoutData(layoutData);
      viewer = new TableViewer(table);
   }

   public TableWrapper<E> bind(final MutableObservableRef<List<E>> model) {
      final Consumer<List<E>> onModelChanged = newValue -> UI.run(() -> setSelection(newValue));
      model.subscribe(onModelChanged);

      setSelection(model.get());
      onSelectionChanged(model::set);

      table.addDisposeListener(ev -> model.unsubscribe(onModelChanged));
      return this;
   }

   public TableWrapper<E> clearSelection() {
      viewer.setSelection(EMPTY_SELECTION);
      return this;
   }

   public Table getTable() {
      return table;
   }

   public TableViewer getViewer() {
      return viewer;
   }

   @SuppressWarnings("unchecked")
   public E getItemAt(final int index) {
      return (E) viewer.getElementAt(index);
   }

   public int getItemCount() {
      return table.getItemCount();
   }

   @SuppressWarnings("unchecked")
   public Collection<E> getItems() {
      final var input = (Collection<E>) viewer.getInput();
      return input == null ? Collections.emptyList() : input;
   }

   public @Nullable List<E> getSelection() {
      return ((IStructuredSelection) viewer.getSelection()).toList();
   }

   public boolean isEnabled() {
      return table.isEnabled();
   }

   public TableWrapper<E> onItemsChanged(final TriConsumer<TableWrapper<E>, Collection<E>, Collection<E>> listener) {
      itemsChangedListener.add(listener);
      return this;
   }

   public TableWrapper<E> onSelectionChanged(final Consumer<List<E>> listener) {
      viewer.addSelectionChangedListener(event -> listener.accept(event.getStructuredSelection().toList()));
      return this;
   }

   public TableWrapper<E> setEnabled(final boolean enabled) {
      table.setEnabled(enabled);
      return this;
   }

   public TableWrapper<E> setItems(final Collection<E> items) {
      viewer.setContentProvider((IStructuredContentProvider) input -> {
         final var coll = (Collection<?>) input;
         return coll == null ? ArrayUtils.EMPTY_OBJECT_ARRAY : coll.toArray(Object[]::new);
      });
      final var old = getItems();
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

   public TableWrapper<E> setItems(final Collection<E> items, final Comparator<E> comparator) {
      viewer.setContentProvider((IStructuredContentProvider) input -> {
         if (input == null)
            return ArrayUtils.EMPTY_OBJECT_ARRAY;

         @SuppressWarnings("unchecked")
         final var list = new ArrayList<>((Collection<E>) input);
         list.sort(comparator);
         return list.toArray(Object[]::new);
      });
      final var old = getItems();
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

   public TableWrapper<E> setItems(@SuppressWarnings("unchecked") final E... items) {
      return setItems(Arrays.asList(items));
   }

   public TableWrapper<E> setItems(final E[] items, final Comparator<E> comparator) {
      return setItems(Arrays.asList(items), comparator);
   }

   public TableWrapper<E> setLabelComparator(final @Nullable Comparator<? super String> comparator) {
      if (comparator == null) {
         viewer.setComparator(null);
      } else {
         viewer.setComparator(new ViewerComparator(comparator));
      }
      return this;
   }

   public TableWrapper<E> setLabelProvider(final Function<E, String> provider) {
      viewer.setLabelProvider(new LabelProvider() {
         @Override
         @SuppressWarnings("unchecked")
         public @Nullable String getText(final Object item) {
            return provider.apply((E) item);
         }
      });
      return this;
   }

   public TableWrapper<E> setSelection(final @Nullable List<E> items) {
      final var currentSelection = getSelection();
      if (Objects.equals(items, currentSelection))
         return this;

      viewer.setSelection(items == null ? EMPTY_SELECTION : new StructuredSelection(items), true);
      return this;
   }
}
