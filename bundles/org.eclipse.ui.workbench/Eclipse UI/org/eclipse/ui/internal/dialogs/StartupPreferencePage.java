/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.dialogs;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.internal.IHelpContextIds;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.util.PrefUtil;
import org.osgi.framework.Constants;

/**
 * The Startup preference page.
 */
public class StartupPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	private Table pluginsList;
	private Workbench workbench;
	
	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
        WorkbenchHelp.setHelp(parent, IHelpContextIds.STARTUP_PREFERENCE_PAGE);

        Composite composite = createComposite(parent);

        createEarlyStartupSelection(composite);

        return composite;
    }

	protected Composite createComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);
        GridData data = new GridData(GridData.FILL_BOTH
                | GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
        composite.setLayoutData(data);
        composite.setFont(parent.getFont());

        return composite;
    }

	protected void createEarlyStartupSelection(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(WorkbenchMessages
                .getString("StartupPreferencePage.label")); //$NON-NLS-1$
        label.setFont(parent.getFont());
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        label.setLayoutData(data);
        pluginsList = new Table(parent, SWT.BORDER | SWT.CHECK | SWT.H_SCROLL
                | SWT.V_SCROLL);
        data = new GridData(GridData.FILL_BOTH);
        pluginsList.setFont(parent.getFont());
        pluginsList.setLayoutData(data);
        populatePluginsList();
    }
	
	private void populatePluginsList() {
		String descriptors[] = workbench.getEarlyActivatedPlugins();
		IPreferenceStore store = workbench.getPreferenceStore();
		String pref = store.getString(IPreferenceConstants.PLUGINS_NOT_ACTIVATED_ON_STARTUP);
		if(pref == null)
			pref = new String();
		for (int i = 0; i < descriptors.length; i++) {
			String desc = descriptors[i];
			TableItem item = new TableItem(pluginsList,SWT.NONE);
			item.setText((String) Platform.getBundle(desc).getHeaders().get(Constants.BUNDLE_NAME));
			item.setData(desc);
			String id = desc + IPreferenceConstants.SEPARATOR;
			item.setChecked(pref.indexOf(id) < 0);
		}
	}
	/**
	 * @see IWorkbenchPreferencePage
	 */
	public void init(IWorkbench workbench) {
		this.workbench = (Workbench) workbench;
	}
	/**
	 * @see PreferencePage
	 */
	protected void performDefaults() {
		TableItem items[] = pluginsList.getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].setChecked(true);
		}
	}
	/**
	 * @see PreferencePage
	 */
	public boolean performOk() {
		StringBuffer preference = new StringBuffer();
		TableItem items[] = pluginsList.getItems();
		for (int i = 0; i < items.length; i++) {
			if(!items[i].getChecked()) {
				preference.append((String)items[i].getData());
				preference.append(IPreferenceConstants.SEPARATOR);
			}
		}
		String pref = preference.toString();
		IPreferenceStore store = workbench.getPreferenceStore();
		store.putValue(IPreferenceConstants.PLUGINS_NOT_ACTIVATED_ON_STARTUP,pref);
		PrefUtil.savePrefs();
		return true;
	}
}
