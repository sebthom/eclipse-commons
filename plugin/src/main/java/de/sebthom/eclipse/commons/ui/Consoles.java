/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors.
 * SPDX-FileContributor: Sebastian Thomschke
 * SPDX-License-Identifier: EPL-2.0
 * SPDX-ArtifactOfProjectHomePage: https://github.com/sebthom/eclipse-commons
 */
package de.sebthom.eclipse.commons.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.osgi.framework.Version;

/**
 * @author Sebastian Thomschke
 */
public abstract class Consoles {

   /**
    * See https://www.eclipse.org/eclipse/news/4.25/platform.php#debug-ansi-support
    */
   private static final Version ECLIPSE_PLATFORM_VERSION_WITH_ANSI_SUPPORT = new Version(4, 25, 0);

   public static void closeConsole(final IConsole console) {
      final var mgr = findConsoleManager();
      if (mgr == null)
         return;
      mgr.removeConsoles(new IConsole[] {console});
   }

   public static int closeConsoles(final Predicate<IConsole> filter) {
      final var mgr = findConsoleManager();
      if (mgr == null)
         return 0;

      final var consoles = Arrays.stream(mgr.getConsoles()).filter(filter).toArray(IConsole[]::new);
      mgr.removeConsoles(consoles);
      return consoles.length;
   }

   public static @Nullable IConsole findConsole(final Predicate<IConsole> filter) {
      final var mgr = findConsoleManager();
      if (mgr == null)
         return null;
      for (final var console : mgr.getConsoles()) {
         if (filter.test(console))
            return console;
      }
      return null;
   }

   public static @Nullable IConsoleManager findConsoleManager() {
      final var consolePlugin = ConsolePlugin.getDefault();
      if (consolePlugin == null)
         return null;
      return consolePlugin.getConsoleManager();
   }

   public static List<IConsole> findConsoles(final Predicate<IConsole> filter) {
      final var mgr = findConsoleManager();
      if (mgr == null)
         return Collections.emptyList();
      return Arrays.stream(mgr.getConsoles()).filter(filter).toList();
   }

   public static boolean isAnsiColorsSupported() {
      return Platform.getBundle("org.eclipse.platform").getVersion().compareTo(ECLIPSE_PLATFORM_VERSION_WITH_ANSI_SUPPORT) >= 0 //
            || Platform.getBundle("net.mihai-nita.ansicon.plugin") != null;
   }

   /**
    * Opens the console view and displays the selected console.
    */
   public static void showConsole(final IConsole console) {
      final var mgr = findConsoleManager();
      if (mgr == null)
         return;

      if (!ArrayUtils.contains(mgr.getConsoles(), console)) {
         mgr.addConsoles(new IConsole[] {console});
      }
      mgr.showConsoleView(console);
   }

   /**
    * Opens the console view and displays the selected console.
    */
   public static boolean showConsole(final Predicate<IConsole> filter) {
      final var mgr = findConsoleManager();
      if (mgr == null)
         return false;

      for (final var console : mgr.getConsoles()) {
         if (filter.test(console)) {
            mgr.showConsoleView(console);
            return true;
         }
      }
      return false;
   }

   /**
    * Opens the console view and displays the selected consoles.
    */
   public static int showConsoles(final Predicate<IConsole> filter) {
      final var mgr = findConsoleManager();
      if (mgr == null)
         return 0;

      var count = 0;
      for (final var console : mgr.getConsoles()) {
         if (filter.test(console)) {
            mgr.showConsoleView(console);
            count++;
         }
      }
      return count;
   }
}
