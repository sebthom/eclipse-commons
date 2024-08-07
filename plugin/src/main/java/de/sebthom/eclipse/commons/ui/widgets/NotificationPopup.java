/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.ui.widgets;

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

   private final String title;
   private final @Nullable String message;

   public NotificationPopup(final Shell parent, final @Nullable String message) {
      super(parent.getDisplay());
      setParentShell(parent);
      title = "";
      this.message = message;
   }

   public NotificationPopup(final Shell parent, final String title, final @Nullable String message) {
      super(parent.getDisplay());
      setParentShell(parent);
      this.title = title;
      this.message = message;
   }

   public NotificationPopup(final @Nullable String message) {
      this(UI.getShell(), message);
   }

   public NotificationPopup(final String title, final @Nullable String message) {
      this(UI.getShell(), title, message);
   }

   @Override
   protected void createContentArea(final Composite parent) {
      final var label = new StyledText(parent, SWT.WRAP);
      label.setEditable(false);
      label.setCaret(null);
      label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
      label.setText(message == null ? "null" : message);
   }

   @Override
   protected String getPopupShellTitle() {
      return Strings.isEmpty(title) ? super.getPopupShellTitle() : title;
   }
}
