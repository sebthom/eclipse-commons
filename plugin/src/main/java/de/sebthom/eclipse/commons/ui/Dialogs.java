/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Sebastian Thomschke
 */
public abstract class Dialogs {

   public static boolean openOkCancel(final String title, final String msg) {
      return UI.run(() -> MessageDialog.openConfirm(UI.getShell(), title, msg));
   }

   /**
    * @returns
    *          <li>{@link IDialogConstants#YES_ID} (2) - user clicked "Yes"
    *          <li>{@link IDialogConstants#NO_ID} (3) - user clicked "No"
    *          <li>{@link IDialogConstants#CANCEL_ID} (1) - user clicked "Cancel"
    */
   public static int openYesNoCancel(final String title, final String msg) {
      final var rc = UI.run(() -> {
         final var dialog = new MessageDialog( //
            UI.getShell(), //
            title, //
            null, //
            msg, //
            MessageDialog.QUESTION, //
            0, //
            IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL);
         return dialog.open();
      });

      return switch (rc) {
         case 0 -> IDialogConstants.YES_ID;
         case 1 -> IDialogConstants.NO_ID;
         default -> IDialogConstants.CANCEL_ID;
      };
   }

   public static void showError(@Nullable final String title, @Nullable final String msg, final Object... msgArgs) {
      UI.run(() -> MessageDialog.openError(UI.getShell(), title, ArrayUtils.isEmpty(msgArgs) ? msg : NLS.bind(msg, msgArgs)));
   }

   public static void showWarning(@Nullable final String title, @Nullable final String msg, final Object... msgArgs) {
      UI.run(() -> MessageDialog.openWarning(UI.getShell(), title, ArrayUtils.isEmpty(msgArgs) ? msg : NLS.bind(msg, msgArgs)));
   }

   public static void showStatus(@Nullable final String title, final IStatus status, final boolean log) {
      UI.run(() -> {
         final var statusAdapter = new StatusAdapter(status);
         statusAdapter.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, title);
         var style = StatusManager.SHOW | StatusManager.BLOCK;
         if (log) {
            style = style | StatusManager.LOG;
         }
         StatusManager.getManager().handle(statusAdapter, style);
      });
   }
}
