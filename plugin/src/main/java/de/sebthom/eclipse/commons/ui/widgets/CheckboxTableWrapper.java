/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.ui.widgets;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.widgets.Composite;

import de.sebthom.eclipse.commons.ui.UI;
import net.sf.jstuff.core.ref.MutableObservableRef;

/**
 * @author Sebastian Thomschke
 */
public class CheckboxTableWrapper<E> extends TableWrapper<E> {

   public CheckboxTableWrapper(final CheckboxTableViewer viewer) {
      super(viewer);
   }

   public CheckboxTableWrapper(final Composite parent, final int style, final @Nullable Object layoutData) {
      super(CheckboxTableViewer.newCheckList(parent, style));
      getTable().setLayoutData(layoutData);
   }

   public CheckboxTableWrapper<E> bindCheckedItems(final MutableObservableRef<List<E>> model) {
      final Consumer<List<E>> onModelChanged = newValue -> UI.run(() -> setCheckedItems(newValue));
      model.subscribe(onModelChanged);

      setCheckedItems(model.get());
      onCheckedItemsChanged(model::set);

      getTable().addDisposeListener(ev -> model.unsubscribe(onModelChanged));
      return this;
   }

   public CheckboxTableWrapper<E> clearCheckedItems() {
      getViewer().setCheckedElements(ArrayUtils.EMPTY_OBJECT_ARRAY);
      return this;
   }

   @Override
   public CheckboxTableWrapper<E> clearSelection() {
      super.clearSelection();
      return this;
   }

   @SuppressWarnings("unchecked")
   public List<E> getCheckedItems() {
      return (List<E>) List.of(getViewer().getCheckedElements());
   }

   @Override
   public CheckboxTableViewer getViewer() {
      return (CheckboxTableViewer) super.getViewer();
   }

   public CheckboxTableWrapper<E> onCheckedItemsChanged(final Consumer<List<E>> listener) {
      getViewer().addCheckStateListener(event -> listener.accept(getCheckedItems()));
      return this;
   }

   public CheckboxTableWrapper<E> setCheckedItems(@SuppressWarnings("unchecked") final E @Nullable... items) {
      final var currentSelection = getCheckedItems();
      if (Objects.equals(items, currentSelection))
         return this;

      getViewer().setCheckedElements(items == null ? ArrayUtils.EMPTY_OBJECT_ARRAY : items);
      return this;
   }

   public CheckboxTableWrapper<E> setCheckedItems(final @Nullable List<E> items) {
      final var currentSelection = getCheckedItems();
      if (Objects.equals(items, currentSelection))
         return this;

      getViewer().setCheckedElements(items == null ? ArrayUtils.EMPTY_OBJECT_ARRAY : items.toArray());
      return this;
   }

   @Override
   public CheckboxTableWrapper<E> setEnabled(final boolean enabled) {
      super.setEnabled(enabled);
      return this;
   }

   @Override
   public CheckboxTableWrapper<E> setItems(final Collection<E> items) {
      super.setItems(items);
      return this;
   }

   @Override
   public CheckboxTableWrapper<E> setItems(final Collection<E> items, final Comparator<E> comparator) {
      super.setItems(items, comparator);
      return this;
   }

   @Override
   public CheckboxTableWrapper<E> setItems(@SuppressWarnings("unchecked") final E... items) {
      super.setItems(items);
      return this;
   }

   @Override
   public CheckboxTableWrapper<E> setItems(final E[] items, final Comparator<E> comparator) {
      super.setItems(items, comparator);
      return this;
   }

   @Override
   public CheckboxTableWrapper<E> setLabelComparator(final @Nullable Comparator<? super String> comparator) {
      super.setLabelComparator(comparator);
      return this;
   }

   @Override
   public CheckboxTableWrapper<E> setLabelProvider(final Function<E, String> provider) {
      super.setLabelProvider(provider);
      return this;
   }

   @Override
   public CheckboxTableWrapper<E> setSelection(final @Nullable List<E> items) {
      super.setSelection(items);
      return this;
   }
}
