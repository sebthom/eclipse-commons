/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Sebastian Thomschke
 */
public abstract class Colors {

   private static final Display DISPLAY = UI.getDisplay();

   public static final Color WHITE = get(SWT.COLOR_WHITE);
   public static final Color BLACK = get(SWT.COLOR_BLACK);

   public static final Color RED = get(SWT.COLOR_RED);
   public static final Color DARK_RED = get(SWT.COLOR_DARK_RED);

   public static final Color GREEN = get(SWT.COLOR_GREEN);
   public static final Color DARK_GREEN = get(SWT.COLOR_DARK_GREEN);

   public static final Color YELLOW = get(SWT.COLOR_YELLOW);
   public static final Color DARK_YELLOW = get(SWT.COLOR_DARK_YELLOW);

   public static final Color BLUE = get(SWT.COLOR_BLUE);
   public static final Color DARK_BLUE = get(SWT.COLOR_DARK_BLUE);

   public static final Color MAGENTA = get(SWT.COLOR_MAGENTA);
   public static final Color DARK_MAGENTA = get(SWT.COLOR_DARK_MAGENTA);

   public static final Color CYAN = get(SWT.COLOR_CYAN);
   public static final Color DARK_CYAN = get(SWT.COLOR_DARK_CYAN);

   public static final Color GRAY = get(SWT.COLOR_GRAY);
   public static final Color DARK_GRAY = get(SWT.COLOR_DARK_GRAY);

   /**
    * @param colorId see color constants in {@link SWT}
    */
   public static Color get(final int colorId) {
      return DISPLAY.getSystemColor(colorId);
   }
}
