/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.ui.listener;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

/**
 * @author Sebastian Thomschke
 */
public interface WorkbenchListener extends IWorkbenchListener {

   @Override
   default boolean preShutdown(final IWorkbench workbench, final boolean forced) {
      return true;
   }

   @Override
   default void postShutdown(final IWorkbench workbench) {
   }

}
