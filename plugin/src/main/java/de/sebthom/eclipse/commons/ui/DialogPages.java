/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;

/**
 * @author Sebastian Thomschke
 */
public abstract class DialogPages {

   public static void setMessage(final DialogPage page, final IStatus status) {
      final var msg = status.getMessage();
      switch (status.getSeverity()) {
         case IStatus.ERROR:
            page.setMessage(null);
            page.setErrorMessage(msg);
            break;

         case IStatus.WARNING:
            page.setMessage(msg, IMessageProvider.WARNING);
            page.setErrorMessage(null);
            break;

         case IStatus.INFO:
            page.setMessage(msg, IMessageProvider.INFORMATION);
            page.setErrorMessage(null);
            break;

         case IStatus.OK:
         default:
            page.setMessage(msg, IMessageProvider.NONE);
            page.setErrorMessage(null);
      }
   }
}
