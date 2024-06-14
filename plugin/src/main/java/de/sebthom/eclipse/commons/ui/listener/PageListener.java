/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.ui.listener;

import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Sebastian Thomschke
 */
public interface PageListener extends IPageListener {

   @Override
   default void pageActivated(final IWorkbenchPage page) {
   }

   @Override
   default void pageClosed(final IWorkbenchPage page) {
   }

   @Override
   default void pageOpened(final IWorkbenchPage page) {
   }

}
