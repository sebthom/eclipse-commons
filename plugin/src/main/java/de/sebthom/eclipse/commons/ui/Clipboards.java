/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

/**
 * @author Sebastian Thomschke
 */
public abstract class Clipboards {

   private static Clipboard clipboard;
   private static Transfer[] textTransfer = new Transfer[] {TextTransfer.getInstance()};

   @NonNull
   public static synchronized Clipboard getClipboard() {
      if (clipboard == null) {
         clipboard = new Clipboard(UI.getDisplay());
      }
      return clipboard;
   }

   public synchronized void copyToClipboard(final String text) {
      getClipboard();

      clipboard.setContents(new Object[] {text}, textTransfer);
   }
}
