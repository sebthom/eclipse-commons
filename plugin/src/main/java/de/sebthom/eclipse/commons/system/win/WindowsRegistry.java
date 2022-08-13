/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.system.win;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEY;

import net.sf.jstuff.core.validation.Args;

/**
 * @author Sebastian Thomschke
 */
public class WindowsRegistry {

   private static final String KEY_PATH_SEPARATOR = "\\";

   public static final WindowsRegistry HKEY_CLASSES_ROOT = new WindowsRegistry(WinReg.HKEY_CLASSES_ROOT);
   public static final WindowsRegistry HKEY_CURRENT_USER = new WindowsRegistry(WinReg.HKEY_CURRENT_USER);
   public static final WindowsRegistry HKEY_LOCAL_MACHINE = new WindowsRegistry(WinReg.HKEY_LOCAL_MACHINE);

   public static void assertSupported() {
      if (!isSupported())
         throw new UnsupportedOperationException(WindowsRegistry.class.getSimpleName() + " is not supported on " + SystemUtils.OS_NAME);
   }

   public static boolean isSupported() {
      return SystemUtils.IS_OS_WINDOWS;
   }

   public static String join(final String... keyPathElements) {
      Args.notNull("keyPathElements", keyPathElements);

      return String.join(KEY_PATH_SEPARATOR, keyPathElements);
   }

   private final HKEY root;

   public WindowsRegistry(final HKEY root) {
      Args.notNull("root", root);

      this.root = root;
   }

   public boolean createKey(final String keyPath) {
      Args.notEmpty("keyPath", keyPath);

      assertSupported();

      if (hasKey(keyPath))
         return false;

      return Advapi32Util.registryCreateKey(root, keyPath);
   }

   public boolean createKey(final String keyParentPath, final String keyName) {
      Args.notEmpty("keyParentPath", keyParentPath);
      Args.notEmpty("keyName", keyName);

      return createKey(keyParentPath + KEY_PATH_SEPARATOR + keyName);
   }

   public boolean deleteKey(final String keyPath, final boolean recursive) {
      Args.notEmpty("keyPath", keyPath);

      assertSupported();

      if (!hasKey(keyPath))
         return false;

      if (recursive) {
         for (final var child : getKeys(keyPath)) {
            deleteKey(keyPath, child, recursive);
         }
      }
      Advapi32Util.registryDeleteKey(root, keyPath);
      return true;
   }

   public boolean deleteKey(final String keyParentPath, final String keyName, final boolean recursive) {
      Args.notEmpty("keyParentPath", keyParentPath);
      Args.notEmpty("keyName", keyName);

      return deleteKey(keyParentPath + KEY_PATH_SEPARATOR + keyName, recursive);
   }

   public List<String> getKeys(final String keyPath) {
      Args.notEmpty("keyPath", keyPath);

      assertSupported();

      if (!hasKey(keyPath))
         return Collections.emptyList();

      return Arrays.asList(Advapi32Util.registryGetKeys(root, keyPath));
   }

   public List<String> getKeys(final String keyParentPath, final String keyName) {
      Args.notEmpty("keyParentPath", keyParentPath);
      Args.notEmpty("keyName", keyName);

      return getKeys(keyParentPath + KEY_PATH_SEPARATOR + keyName);
   }

   public HKEY getRoot() {
      return root;
   }

   /**
    * Get a registry REG_SZ value.
    *
    * @return null if not present
    */
   @Nullable
   public String getStringValue(final String keyPath, @Nullable final String valueName) {
      Args.notEmpty("keyPath", keyPath);

      assertSupported();

      if (Advapi32Util.registryValueExists(root, keyPath, valueName))
         return Advapi32Util.registryGetStringValue(root, keyPath, valueName);
      return null;
   }

   /**
    * Get a registry REG_SZ value.
    *
    * @return null if not present
    */
   @Nullable
   public String getStringValue(final String keyParentPath, final String keyName, @Nullable final String valueName) {
      Args.notEmpty("keyParentPath", keyParentPath);
      Args.notEmpty("keyName", keyName);

      return getStringValue(keyParentPath + KEY_PATH_SEPARATOR + keyName, valueName);
   }

   public boolean hasKey(final String keyPath) {
      Args.notEmpty("keyPath", keyPath);

      assertSupported();

      return Advapi32Util.registryKeyExists(root, keyPath);
   }

   public boolean hasKey(final String keyParentPath, final String keyName) {
      Args.notEmpty("keyParentPath", keyParentPath);
      Args.notEmpty("keyName", keyName);

      return Advapi32Util.registryKeyExists(root, keyParentPath + KEY_PATH_SEPARATOR + keyName);
   }

   /**
    * Set a registry REG_SZ value.
    */
   public void setStringValue(final String keyPath, @Nullable final String valueName, final Object value) {
      Args.notEmpty("keyPath", keyPath);

      assertSupported();

      Advapi32Util.registrySetStringValue(root, keyPath, valueName, value.toString());
   }

   /**
    * Set a registry REG_SZ value.
    */
   public void setStringValue(final String keyParentPath, final String keyName, @Nullable final String valueName, final Object value) {
      Args.notEmpty("keyParentPath", keyParentPath);
      Args.notEmpty("keyName", keyName);

      setStringValue(keyParentPath + KEY_PATH_SEPARATOR + keyName, valueName, value);
   }
}
