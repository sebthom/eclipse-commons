/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RegistryToggleState;

import de.sebthom.eclipse.commons.ui.UI;

/**
 * @author Sebatian Thomschke
 */
public class ToggleCommand extends AbstractHandler {

   public static boolean isEnabled(final String commandId) {
      final var window = UI.getActiveWorkbenchWindow();
      if (window == null)
         return false;

      final var service = window.getService(ICommandService.class);
      if (service == null)
         return false;

      final var command = service.getCommand(commandId);
      return (Boolean) command.getState(RegistryToggleState.STATE_ID).getValue();
   }

   @Override
   public @Nullable Object execute(final ExecutionEvent event) throws ExecutionException {
      HandlerUtil.toggleCommandState(event.getCommand());
      return null;
   }
}
