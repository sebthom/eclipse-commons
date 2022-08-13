/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui.widgets;

import static de.sebthom.eclipse.commons.util.NullAnalysisHelper.*;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.notifications.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import de.sebthom.eclipse.commons.ui.UI;
import net.sf.jstuff.core.Strings;

/**
 * @author Sebastian Thomschke
 */
public class NotificationPopup extends AbstractNotificationPopup {

   private final @Nullable String title;
   private final String message;

   public NotificationPopup(final Shell parent, final String message) {
      super(parent.getDisplay());
      setParentShell(parent);
      title = null;
      this.message = message;
   }

   public NotificationPopup(final Shell parent, final String title, final String message) {
      super(parent.getDisplay());
      setParentShell(parent);
      this.title = title;
      this.message = message;
   }

   public NotificationPopup(final String message) {
      this(castNonNull(UI.getShell()), message);
   }

   public NotificationPopup(final String title, final String message) {
      this(castNonNull(UI.getShell()), title, message);
   }

   @Override
   protected void createContentArea(@Nullable final Composite parent) {
      assert parent != null;
      final var label = new StyledText(parent, SWT.WRAP);
      label.setEditable(false);
      label.setCaret(null);
      label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
      label.setText(message == null ? "null" : message);
   }

   @Override
   protected String getPopupShellTitle() {
      return Strings.isEmpty(title) ? super.getPopupShellTitle() : castNonNull(title);
   }
}
