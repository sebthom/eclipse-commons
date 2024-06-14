/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.ui.listener;

import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @author Sebastian Thomschke
 */
public interface WindowListener extends IWindowListener {

   @Override
   default void windowActivated(final IWorkbenchWindow window) {
   }

   @Override
   default void windowDeactivated(final IWorkbenchWindow window) {
   }

   @Override
   default void windowClosed(final IWorkbenchWindow window) {
   }

   @Override
   default void windowOpened(final IWorkbenchWindow window) {
   }
}
