/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.localization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.osgi.util.NLS;

import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Fields;
import net.sf.jstuff.core.reflection.Members;

/**
 * @author Sebastian Thomschke
 */
@NonNullByDefault
public final class MessagesInitializer {

   private static final Logger LOG = Logger.create();

   /**
    * Alternative to {@link NLS#initializeMessages(String, Class)} which does not support default field values.
    *
    * @param messageBundleName the base name of a fully qualified message properties file.
    * @param messagesClass the class where the constants will exist
    *
    * @see NLS#initializeMessages(String, Class)
    */
   public static void initializeMessages(final String messageBundleName, final Class<?> messagesClass) {
      /*
       * calculate names of message properties files based on current locale
       */
      final List<String> messagePropFiles = new ArrayList<>(4);
      {
         final var root = messageBundleName.replace('.', '/');
         var locale = Locale.getDefault().toString();
         while (true) {
            messagePropFiles.add(root + '_' + locale + ".properties");
            if (locale.lastIndexOf('_') == -1) {
               break;
            }
            locale = StringUtils.substringBeforeLast(locale, "_");
         }
         messagePropFiles.add(root + ".properties");
         Collections.reverse(messagePropFiles);
      }

      /*
       * load values from all .properties files
       */
      final var messageProps = new Properties();
      for (final String variant : messagePropFiles) {
         try (var input = messagesClass.getClassLoader().getResourceAsStream(variant)) {
            if (input != null) {
               messageProps.load(input);
            }
         } catch (final IOException ex) {
            LOG.error(ex, "Error loading %s", variant);
         }
      }

      /*
       * load plugin.xml as String to later check for references to message properties
       */
      var pluginXml = "";
      try (var is = messagesClass.getClassLoader().getResourceAsStream("/plugin.xml")) {
         pluginXml = IOUtils.toString(is);
      } catch (final IOException ex) {
         LOG.error(ex);
      }

      /*
       * apply message properties to fields
       */
      for (final var entry : messageProps.entrySet()) {
         final var fieldName = entry.getKey().toString();
         final var field = Fields.find(messagesClass, fieldName);
         if (field == null) {
            if (!pluginXml.contains("\"%" + fieldName + "\"")) {
               LOG.warn("Unused message property: %s", fieldName);
            }
            continue;
         }

         try {
            Fields.write(null, field, entry.getValue());
         } catch (final Exception ex) {
            LOG.error(ex);
         }
      }

      /*
       * ensure all fields are set
       */
      for (final var field : messagesClass.getFields()) {
         if (!Members.isStatic(field)) {
            continue;
         }
         if (Fields.read(null, field) == null) {
            LOG.error("Uninizialied message property: %s", field.getName());
         }
      }
   }

   private MessagesInitializer() {
   }
}
