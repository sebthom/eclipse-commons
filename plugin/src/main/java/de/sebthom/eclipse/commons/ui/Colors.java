/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import net.sf.jstuff.core.validation.Args;

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

   public static RGB[] createLinearGradient(final RGB from, final RGB to, final int steps) {
      if (steps < 2) {
         Args.min("steps", steps, 2);
      }

      @SuppressWarnings("null")
      final RGB[] gradient = new RGB[steps];
      gradient[0] = from;

      // Calculate the difference and step increment for each color component
      final float stepR = (float) (to.red - from.red) / (steps - 1);
      final float stepG = (float) (to.green - from.green) / (steps - 1);
      final float stepB = (float) (to.blue - from.blue) / (steps - 1);

      for (int i = 1; i < steps; i++) {
         int newRed = Math.round(from.red + stepR * i);
         int newGreen = Math.round(from.green + stepG * i);
         int newBlue = Math.round(from.blue + stepB * i);

         // Clamp color values to the valid range [0, 255]
         newRed = Math.max(0, Math.min(255, newRed));
         newGreen = Math.max(0, Math.min(255, newGreen));
         newBlue = Math.max(0, Math.min(255, newBlue));

         gradient[i] = new RGB(newRed, newGreen, newBlue);
      }

      return gradient;
   }
}
