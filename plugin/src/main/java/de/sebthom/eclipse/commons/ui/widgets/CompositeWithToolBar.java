/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui.widgets;

import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Sebastian Thomschke
 */
public abstract class CompositeWithToolBar extends Composite {

   private ViewForm viewForm;
   private Composite contentArea;

   protected CompositeWithToolBar(final Composite parent, final int style) {
      super(parent, style);

      setLayout(new FillLayout());

      viewForm = new ViewForm(this, SWT.NONE);
      viewForm.setBorderVisible(false);

      final var toolbarMgr = new ToolBarManager(SWT.HORIZONTAL | SWT.FLAT);
      createToolBarActions(toolbarMgr);

      if (toolbarMgr.getItems().length > 0) {
         final var cbm = new CoolBarManager();
         cbm.add(toolbarMgr);

         final var cb = cbm.createControl(viewForm);
         viewForm.setTopLeft(cb);
      }

      contentArea = new Composite(viewForm, SWT.NONE);
      contentArea.setLayout(new FillLayout());

      createContent(contentArea);

      viewForm.setContent(contentArea);
   }

   protected CompositeWithToolBar(final IManagedForm managedForm, final Composite parent, final int style) {
      this(parent, style);

      final var formToolkit = managedForm.getToolkit();
      formToolkit.adapt(this);
      formToolkit.adapt(viewForm);
      formToolkit.adapt(contentArea);
   }

   protected abstract void createContent(Composite parent);

   /**
    * the toolbar will only be created if at least one action is added to the {@link IToolBarManager} within this method
    */
   protected abstract void createToolBarActions(IToolBarManager mgr);
}
