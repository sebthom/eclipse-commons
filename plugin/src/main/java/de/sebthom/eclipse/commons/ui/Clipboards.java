/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.ui;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

/**
 * @author Sebastian Thomschke
 */
public abstract class Clipboards {

   private static final Transfer[] TEXT_TRANSFER = new Transfer[] {TextTransfer.getInstance()};

   private static @Nullable Clipboard clipboard;

   public static synchronized @Nullable Clipboard getClipboard() {
      if (clipboard == null) {
         clipboard = new Clipboard(UI.getDisplay());
      }
      return clipboard;
   }

   public synchronized void copyToClipboard(final String text) {
      getClipboard();

      if (clipboard != null) {
         clipboard.setContents(new Object[] {text}, TEXT_TRANSFER);
      }
   }
}
