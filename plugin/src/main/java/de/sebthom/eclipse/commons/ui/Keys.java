/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
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
