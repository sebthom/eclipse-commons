/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.prefs.fieldeditor;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNull;

import java.util.ArrayList;
import java.util.function.Function;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Sebastian Thomschke
 */
public class EnumFieldEditor<T extends Enum<T>> extends ComboFieldEditor {

   @SuppressWarnings("null")
   private static <T extends Enum<T>> String[][] toEntryNamesAndValues(final Class<T> enumClass,
         final Function<T, String> enumLabelProvider) {
      final var pairs = new ArrayList<String[]>();
      for (final var e : asNonNull(enumClass.getEnumConstants())) {
         pairs.add(new String[] {enumLabelProvider.apply(e), e.name()});
      }
      return pairs.toArray(String[][]::new);
   }

   public EnumFieldEditor(final String name, final String labelText, final Class<T> enumClass, final Composite parent) {
      this(name, labelText, enumClass, (Function<T, String>) Enum::name, parent);
   }

   public EnumFieldEditor(final String name, final String labelText, final Class<T> enumClass, final Function<T, String> enumLabelProvider,
         final Composite parent) {
      super(name, labelText, toEntryNamesAndValues(enumClass, enumLabelProvider), parent);
   }
}
