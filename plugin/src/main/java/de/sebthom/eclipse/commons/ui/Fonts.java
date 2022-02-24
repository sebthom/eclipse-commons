/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;

/**
 * @author Sebastian Thomschke
 */
public abstract class Fonts {

   public static final Font DEFAULT_FONT_BOLD = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
   public static final Styler DEFAULT_FONT_BOLD_STYLER = new Styler() {
      @Override
      @NonNullByDefault({})
      public void applyStyles(final TextStyle textStyle) {
         textStyle.font = DEFAULT_FONT_BOLD;
      }
   };

}
