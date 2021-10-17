/*
 * Copyright 2021 by Sebastian Thomschke and contributors
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

   public static final Color WHITE = DISPLAY.getSystemColor(SWT.COLOR_WHITE);
   public static final Color BLACK = DISPLAY.getSystemColor(SWT.COLOR_BLACK);

   public static final Color RED = DISPLAY.getSystemColor(SWT.COLOR_RED);
   public static final Color DARK_RED = DISPLAY.getSystemColor(SWT.COLOR_DARK_RED);

   public static final Color GREEN = DISPLAY.getSystemColor(SWT.COLOR_GREEN);
   public static final Color DARK_GREEN = DISPLAY.getSystemColor(SWT.COLOR_DARK_GREEN);

   public static final Color YELLOW = DISPLAY.getSystemColor(SWT.COLOR_YELLOW);
   public static final Color DARK_YELLOW = DISPLAY.getSystemColor(SWT.COLOR_DARK_YELLOW);

   public static final Color BLUE = DISPLAY.getSystemColor(SWT.COLOR_BLUE);
   public static final Color DARK_BLUE = DISPLAY.getSystemColor(SWT.COLOR_DARK_BLUE);

   public static final Color MAGENTA = DISPLAY.getSystemColor(SWT.COLOR_MAGENTA);
   public static final Color DARK_MAGENTA = DISPLAY.getSystemColor(SWT.COLOR_DARK_MAGENTA);

   public static final Color CYAN = DISPLAY.getSystemColor(SWT.COLOR_CYAN);
   public static final Color DARK_CYAN = DISPLAY.getSystemColor(SWT.COLOR_DARK_CYAN);

   public static final Color GRAY = DISPLAY.getSystemColor(SWT.COLOR_GRAY);
   public static final Color DARK_GRAY = DISPLAY.getSystemColor(SWT.COLOR_DARK_GRAY);
}
