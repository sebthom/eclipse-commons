/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui.listener;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @author Sebastian Thomschke
 */
public interface WindowListener extends IWindowListener {

   @Override
   default void windowActivated(@Nullable final IWorkbenchWindow window) {
   }

   @Override
   default void windowDeactivated(@Nullable final IWorkbenchWindow window) {
   }

   @Override
   default void windowClosed(@Nullable final IWorkbenchWindow window) {
   }

   @Override
   default void windowOpened(@Nullable final IWorkbenchWindow window) {
   }
}
