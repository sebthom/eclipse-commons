/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.ui.widgets;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Sebastian Thomschke
 */
@NonNullByDefault(org.eclipse.jdt.annotation.DefaultLocation.PARAMETER)
public abstract class CompositeWithToolBar extends Composite {

   private ViewForm viewForm;
   private Composite contentArea;
   private FormToolkit formToolkit;

   protected CompositeWithToolBar(final Composite parent, final int style) {
      super(parent, style);

      createContent();
   }

   protected CompositeWithToolBar(final IManagedForm managedForm, final Composite parent, final int style) {
      super(parent, style);

      formToolkit = managedForm.getToolkit();

      createContent();

      formToolkit.adapt(this);
      formToolkit.adapt(viewForm);
      formToolkit.adapt(contentArea);
   }

   private void createContent() {
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

   protected abstract void createContent(Composite parent);

   /**
    * the toolbar will only be created if at least one action is added to the {@link IToolBarManager} within this method
    */
   protected abstract void createToolBarActions(IToolBarManager mgr);
}
