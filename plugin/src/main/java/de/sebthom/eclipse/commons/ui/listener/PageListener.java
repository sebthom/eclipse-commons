/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui.listener;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Sebastian Thomschke
 */
public interface PageListener extends IPageListener {

   @Override
   default void pageActivated(@Nullable final IWorkbenchPage page) {
   }

   @Override
   default void pageClosed(@Nullable final IWorkbenchPage page) {
   }

   @Override
   default void pageOpened(@Nullable final IWorkbenchPage page) {
   }

}
