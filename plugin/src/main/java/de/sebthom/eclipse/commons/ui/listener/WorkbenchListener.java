/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui.listener;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

/**
 * @author Sebastian Thomschke
 */
public interface WorkbenchListener extends IWorkbenchListener {

   @Override
   default boolean preShutdown(@Nullable final IWorkbench workbench, final boolean forced) {
      return true;
   }

   @Override
   default void postShutdown(@Nullable final IWorkbench workbench) {
   }

}
