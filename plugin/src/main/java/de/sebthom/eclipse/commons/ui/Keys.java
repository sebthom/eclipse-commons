/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

/**
 * @author Sebastian Thomschke
 */
public abstract class Keys {

   public static void sendKeyDown(final int keyCode) {
      final var e = new Event();
      e.keyCode = keyCode;
      e.type = SWT.KeyDown;
      UI.getDisplay().post(e);
   }

   public static void sendKeyUp(final int keyCode) {
      final var e = new Event();
      e.keyCode = keyCode;
      e.type = SWT.KeyUp;
      UI.getDisplay().post(e);
   }
}
