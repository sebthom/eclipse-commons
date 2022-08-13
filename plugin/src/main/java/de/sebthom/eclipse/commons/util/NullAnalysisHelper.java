/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.util;

import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Sebastian Thomschke
 */
public final class NullAnalysisHelper {

   /**
    * Casts non-null value marked as {@link Nullable} to {@link NonNull}.
    * <p>
    * Only use if you are sure the value is non-null but annotation-based null analysis was not able to determine it.
    * <p>
    * This method is not meant for non-null input validation.
    *
    * @throws AssertionError if JVM assertions are enabled and the given value is null
    */
   @NonNull
   public static <T> T castNonNull(@Nullable final T value) {
      assert value != null;
      return value;
   }

   /**
    * Casts a non-null value as {@link Nullable}.
    */
   @Nullable
   public static <T> T castNullable(final T value) {
      return value;
   }

   public static <T> T defaultIfNull(@Nullable final T object, final T defaultValue) {
      if (object == null)
         return defaultValue;
      return object;
   }

   public static <T> T defaultIfNull(@Nullable final T object, final Supplier<T> defaultValue) {
      if (object == null)
         return defaultValue.get();
      return object;
   }

   private NullAnalysisHelper() {
   }
}
