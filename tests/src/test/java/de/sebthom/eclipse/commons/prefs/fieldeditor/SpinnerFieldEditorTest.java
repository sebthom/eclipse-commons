/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.prefs.fieldeditor;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Sebastian Thomschke
 */
class SpinnerFieldEditorTest {

   private static final String PREFKEY = "spinnerValue";

   private Shell shell = lateNonNull();
   private SpinnerFieldEditor editor = lateNonNull();

   private static final int INITIAL_MIN = 5;
   private static final int INITIAL_MAX = 50;
   private static final int INITIAL_INC = 3;
   private static final int INITIAL_PAGE_INC = 9;

   @BeforeEach
   void setUp() {
      shell = new Shell(SWT.NONE);
      editor = new SpinnerFieldEditor(PREFKEY, "Test Spinner", shell) {
         @Override
         protected Spinner createSpinner(final Composite parent) {
            final var spinner = new Spinner(parent, SWT.HORIZONTAL);
            spinner.setMinimum(INITIAL_MIN);
            spinner.setMaximum(INITIAL_MAX);
            spinner.setMinimum(INITIAL_MIN);
            spinner.setIncrement(INITIAL_INC);
            spinner.setPageIncrement(INITIAL_PAGE_INC);
            return spinner;
         }
      };
   }

   @AfterEach
   void tearDown() {
      if (asNullable(shell) != null && !shell.isDisposed()) {
         shell.dispose();
      }
   }

   @Test
   void testConstructorArgs() {
      assertThat(editor.getWidget().getMinimum()).isEqualTo(INITIAL_MIN);
      assertThat(editor.getWidget().getMaximum()).isEqualTo(INITIAL_MAX);
      assertThat(editor.getWidget().getIncrement()).isEqualTo(INITIAL_INC);
      assertThat(editor.getWidget().getPageIncrement()).isEqualTo(INITIAL_PAGE_INC);
   }

   @Test
   void testGetterAndSetter() {
      final Spinner widget = editor.getWidget();

      assertThat(widget).isNotNull();

      assertThat(editor.getLabelControl(shell).isEnabled()).isTrue();
      assertThat(widget.isEnabled()).isTrue();
      editor.setEnabled(false, shell);
      assertThat(editor.getLabelControl(shell).isEnabled()).isFalse();
      assertThat(widget.isEnabled()).isFalse();
   }

   @Test
   void testLoad() {
      final Spinner widget = editor.getWidget();
      assertThat(widget.getSelection()).isEqualTo(INITIAL_MIN);

      final var prefStoreWithDefault = new PreferenceStore();
      prefStoreWithDefault.setDefault(PREFKEY, INITIAL_MIN + 1);

      editor.setPreferenceStore(prefStoreWithDefault);

      editor.load();
      assertThat(widget.getSelection()).isEqualTo(INITIAL_MIN + 1);

      final var prefStoreWithDefaultAndValue = new PreferenceStore();
      prefStoreWithDefaultAndValue.setDefault(PREFKEY, INITIAL_MIN + 1);
      prefStoreWithDefaultAndValue.setValue(PREFKEY, INITIAL_MIN + 2);

      editor.setPreferenceStore(prefStoreWithDefaultAndValue);

      editor.load();
      assertThat(widget.getSelection()).isEqualTo(INITIAL_MIN + 2);
   }

   @Test
   void testLoadDefault() {
      final Spinner widget = editor.getWidget();
      assertThat(widget.getSelection()).isEqualTo(INITIAL_MIN);

      final var prefStoreWithDefaultAndValue = new PreferenceStore();
      prefStoreWithDefaultAndValue.setDefault(PREFKEY, INITIAL_MIN + 1);
      prefStoreWithDefaultAndValue.setValue(PREFKEY, INITIAL_MIN + 2);

      editor.setPreferenceStore(prefStoreWithDefaultAndValue);

      editor.load();
      assertThat(widget.getSelection()).isEqualTo(INITIAL_MIN + 2);

      editor.loadDefault();
      assertThat(widget.getSelection()).isEqualTo(INITIAL_MIN + 1);
   }

   @Test
   void testSetValueInWidget() {
      final Spinner widget = editor.getWidget();
      assertThat(widget.getSelection()).isEqualTo(INITIAL_MIN);

      final int min = 100;
      final int max = 103;

      editor.getWidget().setMinimum(min);
      editor.getWidget().setMaximum(max);
      editor.getWidget().setMinimum(min);

      // By setting the minimum bound the selection changes accordingly
      assertThat(widget.getSelection()).isEqualTo(min);

      widget.setSelection(min - 1); // outside lower bound
      assertThat(widget.getSelection()).isEqualTo(min);

      widget.setSelection(min);
      assertThat(widget.getSelection()).isEqualTo(min);

      widget.setSelection(min + 1);
      assertThat(widget.getSelection()).isEqualTo(min + 1);

      widget.setSelection(max);
      assertThat(widget.getSelection()).isEqualTo(max);

      widget.setSelection(max + 1); // outside upper bound
      assertThat(widget.getSelection()).isEqualTo(max);
   }
}
