/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.ui.widgets;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import de.sebthom.eclipse.commons.ui.Controls;
import de.sebthom.eclipse.commons.ui.UI;
import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.functional.TriConsumer;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.ref.MutableObservableRef;

/**
 * @author Sebastian Thomschke
 */
public class ComboWrapper<E> {

   private static final Logger LOG = Logger.create();
   private static final IStructuredSelection EMPTY_SELECTION = new StructuredSelection();

   private final Combo combo;
   private @Nullable Comparator<E> itemComparator;
   private final ComboViewer viewer;

   private final CopyOnWriteArrayList<TriConsumer<ComboWrapper<E>, Collection<E>, Collection<E>>> itemsChangedListener = new CopyOnWriteArrayList<>();

   public ComboWrapper(final Combo combo) {
      this.combo = combo;
      viewer = createComboViewer();
      if (!Controls.hasStyle(combo, SWT.READ_ONLY)) {
         configureAutoComplete();
      }
   }

   public ComboWrapper(final Composite parent, final @Nullable Object layoutData) {
      combo = new Combo(parent, SWT.NONE);
      combo.setLayoutData(layoutData);
      viewer = createComboViewer();
      configureAutoComplete();
   }

   private ComboViewer createComboViewer() {
      final var viewer = new ComboViewer(combo);
      viewer.setContentProvider((IStructuredContentProvider) input -> {
         if (input == null)
            return ArrayUtils.EMPTY_OBJECT_ARRAY;

         @SuppressWarnings("unchecked")
         final var items = (Collection<E>) input;

         final var comparator = itemComparator;
         if (comparator == null)
            return asNonNull(items.toArray());

         @SuppressWarnings("unchecked")
         final var sorted = new ArrayList<>((Collection<E>) input);
         sorted.sort(comparator);
         return asNonNull(sorted.toArray());
      });
      return viewer;
   }

   private void configureAutoComplete() {
      combo.addFocusListener(new FocusAdapter() {
         @Override
         public void focusGained(final FocusEvent e) {
            // automatically open drop-down list if focused and no item was selected
            UI.getDisplay().asyncExec(() -> combo.setListVisible(true));
            // -> delaying it with asyncExec to prevent focus loss which happens for some reason
         }

         @SuppressWarnings("unchecked")
         @Override
         public void focusLost(final FocusEvent e) {
            final String text = combo.getText();
            final String[] items = combo.getItems();

            for (final String item : items) {
               if (text.equals(item))
                  return;
            }
            for (int i = 0; i < items.length; i++) {
               if (text.equalsIgnoreCase(items[i])) {
                  setSelection((E) viewer.getElementAt(i));
                  return;
               }
            }
            for (int i = 0; i < items.length; i++) {
               if (Strings.startsWithIgnoreCase(items[i], text)) {
                  setSelection((E) viewer.getElementAt(i));
               }
            }
            clearSelection();
         }
      });

      combo.addKeyListener(new KeyListener() {
         private int prevItem = 0;
         private Point prevTextSelection = new Point(0, 0);

         @Override
         public void keyPressed(final KeyEvent keyEvent) {
            prevItem = combo.getSelectionIndex();
            prevTextSelection = combo.getSelection();
            if (keyEvent.keyCode == SWT.BS) {
               combo.setSelection(new Point(Math.max(0, prevTextSelection.x - 1), prevTextSelection.y));
            }
         }

         @SuppressWarnings("unchecked")
         @Override
         public void keyReleased(final KeyEvent keyEvent) {
            final String text = combo.getText();

            if (text.isEmpty()) {
               clearSelection();
               return;
            }

            final String[] items = combo.getItems();

            int matchingItem = -1;
            if (matchingItem == -1) {
               for (int i = 0; i < items.length; i++) {
                  if (Strings.startsWithIgnoreCase(items[i], text)) {
                     matchingItem = i;
                     break;
                  }
               }
            }

            if (matchingItem > -1) {
               final var pt = combo.getSelection();
               setSelection((E) viewer.getElementAt(matchingItem));
               combo.setSelection(new Point(pt.x, items[matchingItem].length()));
            } else {
               setSelection((E) viewer.getElementAt(prevItem));
               combo.setSelection(prevTextSelection);
            }
         }
      });
   }

   public ComboWrapper<E> bind(final MutableObservableRef<@Nullable E> model) {
      final Consumer<@Nullable E> onModelChanged = newValue -> UI.run(() -> setSelection(newValue));
      model.subscribe(onModelChanged);

      setSelection(model.get());
      onSelectionChanged(model::set);

      combo.addDisposeListener(ev -> model.unsubscribe(onModelChanged));
      return this;
   }

   public ComboWrapper<E> bind(final MutableObservableRef<@NonNull E> model, final @NonNull E defaultValue) {
      final Consumer<@NonNull E> onModelChanged = newValue -> UI.run(() -> setSelection(newValue));
      model.subscribe(onModelChanged);

      setSelection(model.get());
      onSelectionChanged((final @Nullable E selection) -> model.set(selection == null ? defaultValue : selection));

      combo.addDisposeListener(ev -> model.unsubscribe(onModelChanged));
      return this;
   }

   public ComboWrapper<E> clearSelection() {
      viewer.setSelection(EMPTY_SELECTION);
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
      final var input = (Collection<E>) viewer.getInput();
      return input == null ? Collections.emptyList() : input;
   }

   @SuppressWarnings("unchecked")
   public @Nullable E getSelection() {
      return (E) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
   }

   public boolean isEnabled() {
      return combo.isEnabled();
   }

   public ComboWrapper<E> onItemsChanged(final TriConsumer<ComboWrapper<E>, Collection<E>, Collection<E>> listener) {
      itemsChangedListener.add(listener);
      return this;
   }

   @SuppressWarnings("unchecked")
   public ComboWrapper<E> onSelectionChanged(final Consumer<E> listener) {
      viewer.addSelectionChangedListener(event -> {
         final var elem = (E) event.getStructuredSelection().getFirstElement();
         listener.accept(elem);
      });
      return this;
   }

   public ComboWrapper<E> setEnabled(final boolean enabled) {
      combo.setEnabled(enabled);
      return this;
   }

   public void setItemComparator(final @Nullable Comparator<E> itemComparator) {
      this.itemComparator = itemComparator;
   }

   public synchronized ComboWrapper<E> setItems(final Collection<@NonNull E> items) {
      final var selection = getSelection();

      final var old = getItems();
      viewer.setInput(items);
      for (final var l : itemsChangedListener) {
         try {
            l.accept(this, old, items);
         } catch (final Exception ex) {
            LOG.error(ex);
         }
      }
      if (selection != null && items.contains(selection)) {
         setSelection(selection);
      }
      return this;
   }

   @SafeVarargs
   public final synchronized ComboWrapper<E> setItems(final E... items) {
      return setItems(Arrays.asList(items));
   }

   public ComboWrapper<E> setLabelComparator(final @Nullable Comparator<? super String> comparator) {
      viewer.setComparator(comparator == null ? null : new ViewerComparator(comparator));
      return this;
   }

   public ComboWrapper<E> setLabelProvider(final Function<@NonNull E, String> provider) {
      viewer.setLabelProvider(new LabelProvider() {
         @SuppressWarnings("unchecked")
         @Override
         public @Nullable String getText(final @Nullable Object item) {
            if (item == null)
               return "";
            return provider.apply((E) item);
         }
      });
      return this;
   }

   public synchronized ComboWrapper<E> setSelection(final @Nullable E item) {
      return setSelection(item, false);
   }

   public synchronized ComboWrapper<E> setSelection(final @Nullable E item, final boolean reveal) {
      final var currentSelection = getSelection();
      if (currentSelection == item)
         return this;

      if (item == null) {
         viewer.setSelection(EMPTY_SELECTION);
         return this;
      }

      final var items = getItems();
      if (items.isEmpty()) {
         setItems(item);
      }

      viewer.setSelection(new StructuredSelection(item), reveal);
      return this;
   }
}
