/* 
 * YAJSL - Yet Another Java Swing Library
 *
 * Copyright (c) 2013 Giuseppe Gallo
 *
 * LICENSED UNDER:
 *
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2013 Giuseppe Gallo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package YAJSL.Swing.Beans;

import YAJSL.Data.RecordCountListener;
import YAJSL.Swing.Beans.TableControlPanel.ControlPanelActionEvent.ActionType;
import YAJSL.Swing.Utils;
import YAJSL.Swing.MousePointerManager;
import YAJSL.Utils.ExtendedProperties;
import YAJSL.Utils.Localizer;
import java.awt.Cursor;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.JComponent;
import YAJSL.Data.DataRecord;

/**
 * Represents a control panel to be used with tables.
 * 
 * @author Giuseppe Gallo
 */
public class TableControlPanel extends javax.swing.JPanel implements RecordCountListener, TableDataRecord.SelectionListener {

    /**
     * The event corresponding to an action in the control panel.
     */
    public static class ControlPanelActionEvent extends java.util.EventObject {

        /**
         * The types of actions allowed.
         */
        public static enum ActionType {
            CLEAR_SELECTION, SEARCH, ADD, DUPLICATE, EDIT, DELETE, OK, CANCEL
        }

        /** The type of action for this event */
        ActionType action;

        /**
         * Instantiates a ControlPanelActionEvent.
         *
         * @param source  the object originating the event
         * @param action  the type of action
         */
        public ControlPanelActionEvent(Object source, ActionType action) {
            super(source);
            this.action = action;
        }
    }

    /**
     * The common interface for all objects listening for a ControlPanelActionEvent.
     */
    public interface ControlPanelActionListener extends EventListener {
        /**
         * Reacts to a CLEAR_SELECTION action.
         * @param evt  the event corresponding to the action
         */
        public void actionClearSelection(ControlPanelActionEvent evt);

        /**
         * Reacts to a SEARCH action.
         * @param evt  the event corresponding to the action
         */
        public void actionSearch(ControlPanelActionEvent evt);

        /**
         * Reacts to an ADD action.
         * @param evt  the event corresponding to the action
         */
        public void actionAdd(ControlPanelActionEvent evt);

        /**
         * Reacts to an DUPLICATE action.
         * @param evt  the event corresponding to the action
         */
        public void actionDuplicate(ControlPanelActionEvent evt);

        /**
         * Reacts to an EDIT action.
         * @param evt  the event corresponding to the action
         */
        public void actionEdit(ControlPanelActionEvent evt);

        /**
         * Reacts to a DELETE action.
         * @param evt  the event corresponding to the action
         */
        public void actionDelete(ControlPanelActionEvent evt);

        /**
         * Reacts to an OK action.
         * @param evt  the event corresponding to the action
         */
        public void actionOK(ControlPanelActionEvent evt);

        /**
         * Reacts to a CANCEL action.
         * @param evt  the event corresponding to the action
         */
        public void actionCancel(ControlPanelActionEvent evt);
    }


    /** The property suffix for the tooltip */
    public static final String PROPERTY_SUFFIX_TOOLTIP = ".tooltip";
    
    /** The property suffix for the text */
    public static final String PROPERTY_SUFFIX_TEXT = ".text";

    /** The property prefix for the ClearSelection button */
    public static final String PROPERTY_PREFIX_CLEAR_SELECTION = "button.clearSelection";

    /** The property prefix for the Search button */
    public static final String PROPERTY_PREFIX_SEARCH = "button.search";

    /** The property prefix for the Add button */
    public static final String PROPERTY_PREFIX_ADD = "button.add";

    /** The property prefix for the Add button */
    public static final String PROPERTY_PREFIX_DUPLICATE = "button.duplicate";

    /** The property prefix for the Edit button */
    public static final String PROPERTY_PREFIX_EDIT = "button.edit";

    /** The property prefix for the Delete button */
    public static final String PROPERTY_PREFIX_DELETE = "button.delete";

    /** The property prefix for the OK button */
    public static final String PROPERTY_PREFIX_OK = "button.ok";

    /** The property prefix for the Cancel button */
    public static final String PROPERTY_PREFIX_CANCEL = "button.cancel";

    /** The property prefix for the RecordCount label */
    public static final String PROPERTY_PREFIX_RECORDCOUNT = "label.recordCount";

    /** The default tooltip for the ClearSelection button */
    public static final String DEFAULT_TOOLTIP_CLEAR_SELECTION = "Clear the selection (unselect all)";

    /** The default tooltip for the Search button */
    public static final String DEFAULT_TOOLTIP_SEARCH = "Search and selects a record";

    /** The default tooltip for the Add button */
    public static final String DEFAULT_TOOLTIP_ADD = "Add a new record";

    /** The default tooltip for the Duplicate button */
    public static final String DEFAULT_TOOLTIP_DUPLICATE = "Duplicate the record currently selected";

    /** The default tooltip for the Edit button */
    public static final String DEFAULT_TOOLTIP_EDIT = "Edit the record currently selected";

    /** The default tooltip for the Delete button */
    public static final String DEFAULT_TOOLTIP_DELETE = "Delete the record currently selected";

    /** The default tooltip for the OK button */
    public static final String DEFAULT_TOOLTIP_OK = "Confirm the modifications to the record";

    /** The default tooltip for the Cancel button */
    public static final String DEFAULT_TOOLTIP_CANCEL = "Cancel the modifications to the record";

    /** If true, shows the count of records in the corresponding table */
    protected boolean recordCountShown = true;

    /** If true, shows the ClearSelection button */
    protected boolean buttonClearSelectionShown = true;

    /** If true, shows the Search button */
    protected boolean buttonSearchShown = true;

    /** If true, shows the Add button */
    protected boolean buttonAddShown = true;

    /** If true, shows the Duplicate button */
    protected boolean buttonDuplicateShown = true;

    /** If true, shows the Edit button */
    protected boolean buttonEditShown = true;

    /** If true, shows the Delete button */
    protected boolean buttonDeleteShown = true;

    /** If true, shows the OK button */
    protected boolean buttonOkShown = true;

    /** If true, shows the Cancel button */
    protected boolean buttonCancelShown = true;

    /** If true, the ClearSelection button is enabled */
    protected boolean buttonClearSelectionEnabled = true;

    /** If true, the Search button is enabled */
    protected boolean buttonSearchEnabled = true;

    /** If true, the Add button is enabled */
    protected boolean buttonAddEnabled = true;

    /** If true, the Duplicate button is enabled */
    protected boolean buttonDuplicateEnabled = true;

    /** If true, the Edit button is enabled */
    protected boolean buttonEditEnabled = true;

    /** If true, the Delete button is enabled */
    protected boolean buttonDeleteEnabled = true;

    /** If true, the OK button is enabled */
    protected boolean buttonOkEnabled = true;

    /** If true, the Cancel button is enabled */
    protected boolean buttonCancelEnabled = true;

    /** The MousePointerManager to be used with this control panel */
    protected MousePointerManager mpm = new MousePointerManager();

    /** The listeners for a ControlPanelActionEvent */
    protected LinkedList<ControlPanelActionListener> controlPanelActionListeners = new LinkedList<>();

    /** The table controlled by this control panel */
    protected TableDataRecord table = null;
    
    /** The icon used for the button ClearSelection */
    protected Icon iconClearSelection = Utils.ICON_CLEAR_SELECTION;
    
    /** The icon used for the button Search */
    protected Icon iconSearch = Utils.ICON_SEARCH;
    
    /** The icon used for the button Add */
    protected Icon iconAdd = Utils.ICON_ADD;
    
    /** The icon used for the button Duplicate */
    protected Icon iconDuplicate = Utils.ICON_DUPLICATE;
    
    /** The icon used for the button Edit */
    protected Icon iconEdit = Utils.ICON_EDIT;
    
    /** The icon used for the button Delete */
    protected Icon iconDelete = Utils.ICON_DELETE;
    
    /** The icon used for the button OK */
    protected Icon iconOK = Utils.ICON_OK;
    
    /** The icon used for the button Cancel */
    protected Icon iconCancel = Utils.ICON_CANCEL;

    /** The rollover icon used for the button ClearSelection */
    protected Icon iconClearSelectionR = Utils.ICON_CLEAR_SELECTION_R;
    
    /** The rollover icon used for the button Search */
    protected Icon iconSearchR = Utils.ICON_SEARCH_R;
    
    /** The rollover icon used for the button Add */
    protected Icon iconAddR = Utils.ICON_ADD_R;
    
    /** The rollover icon used for the button Duplicate */
    protected Icon iconDuplicateR = Utils.ICON_DUPLICATE_R;
    
    /** The rollover icon used for the button Edit */
    protected Icon iconEditR = Utils.ICON_EDIT_R;
    
    /** The rollover icon used for the button Delete */
    protected Icon iconDeleteR = Utils.ICON_DELETE_R;
    
    /** The rollover icon used for the button OK */
    protected Icon iconOKR = Utils.ICON_OK_R;
    
    /** The rollover icon used for the button Cancel */
    protected Icon iconCancelR = Utils.ICON_CANCEL_R;

    /** The list of components to be selected/deselected based on the table selection */
    protected final LinkedList<JComponent> managed = new LinkedList<>();
    
    /**
     * Creates a new panel TableControlPanel
     */
    public TableControlPanel() {
        super();
        initComponents();
        postInit();

        jButtonClearSelection.setToolTipText(DEFAULT_TOOLTIP_CLEAR_SELECTION);
        jButtonSearch.setToolTipText(DEFAULT_TOOLTIP_SEARCH);
        jButtonAdd.setToolTipText(DEFAULT_TOOLTIP_ADD);
        jButtonDuplicate.setToolTipText(DEFAULT_TOOLTIP_DUPLICATE);
        jButtonEdit.setToolTipText(DEFAULT_TOOLTIP_EDIT);
        jButtonDelete.setToolTipText(DEFAULT_TOOLTIP_DELETE);
        jButtonOK.setToolTipText(DEFAULT_TOOLTIP_OK);
        jButtonCancel.setToolTipText(DEFAULT_TOOLTIP_CANCEL);

        unselectRecord();
    }

    /**
     * Creates a new panel TableControlPanel
     * 
     * @param properties  the properties to be used for initializing this control panel
     */
    public TableControlPanel(Properties properties) {
        super();
        initComponents();
        postInit();
        initMousePointerManager();
        setLocalizedTexts(properties);
        unselectRecord();
    }

    /**
     * Sets the texts (labels and tooltips) according to the properties passed.
     * 
     * @param properties the properties to be used for updating labels and tooltips
     * @param prefix  the prefix used for filtering the properties (the "." is automatically appended).
     */
    public final void setLocalizedTexts(Properties properties, String prefix) {
        setLocalizedTexts(ExtendedProperties.filter(properties, prefix + "."));
    }
    
    /**
     * Sets the texts (labels and tooltips) according to the properties passed.
     * 
     * @param properties the properties to be used for updating labels and tooltips
     */
    public final void setLocalizedTexts(Properties properties) {
        setTooltip(jButtonClearSelection, properties, PROPERTY_PREFIX_CLEAR_SELECTION, DEFAULT_TOOLTIP_CLEAR_SELECTION);
        setTooltip(jButtonSearch, properties, PROPERTY_PREFIX_SEARCH, DEFAULT_TOOLTIP_SEARCH);
        setTooltip(jButtonAdd, properties, PROPERTY_PREFIX_ADD, DEFAULT_TOOLTIP_ADD);
        setTooltip(jButtonDuplicate, properties, PROPERTY_PREFIX_DUPLICATE, DEFAULT_TOOLTIP_DUPLICATE);
        setTooltip(jButtonEdit, properties, PROPERTY_PREFIX_EDIT, DEFAULT_TOOLTIP_EDIT);
        setTooltip(jButtonDelete, properties, PROPERTY_PREFIX_DELETE, DEFAULT_TOOLTIP_DELETE);
        setTooltip(jButtonOK, properties, PROPERTY_PREFIX_OK, DEFAULT_TOOLTIP_OK);
        setTooltip(jButtonCancel, properties, PROPERTY_PREFIX_CANCEL, DEFAULT_TOOLTIP_CANCEL);

        String text = properties.getProperty(PROPERTY_PREFIX_RECORDCOUNT + PROPERTY_SUFFIX_TEXT);
        jLabelRecords.setText((text == null) ? "# Records: " : (text + " "));
    }
    
    /**
     * Sets the texts (labels and tooltips) according to the localizer passed.
     * 
     * @param loc the localizer to be used for updating labels and tooltips
     * @param prefix  the prefix used for filtering the properties (the "." is automatically appended).
     */
    public final void setLocalizedTexts(Localizer loc, String prefix) {
        prefix += ".";
        
        setTooltip(jButtonClearSelection, loc, prefix + PROPERTY_PREFIX_CLEAR_SELECTION, DEFAULT_TOOLTIP_CLEAR_SELECTION);
        setTooltip(jButtonSearch, loc, prefix + PROPERTY_PREFIX_SEARCH, DEFAULT_TOOLTIP_SEARCH);
        setTooltip(jButtonAdd, loc, prefix + PROPERTY_PREFIX_ADD, DEFAULT_TOOLTIP_ADD);
        setTooltip(jButtonDuplicate, loc, prefix + PROPERTY_PREFIX_DUPLICATE, DEFAULT_TOOLTIP_DUPLICATE);
        setTooltip(jButtonEdit, loc, prefix + PROPERTY_PREFIX_EDIT, DEFAULT_TOOLTIP_EDIT);
        setTooltip(jButtonDelete, loc, prefix + PROPERTY_PREFIX_DELETE, DEFAULT_TOOLTIP_DELETE);
        setTooltip(jButtonOK, loc, prefix + PROPERTY_PREFIX_OK, DEFAULT_TOOLTIP_OK);
        setTooltip(jButtonCancel, loc, prefix + PROPERTY_PREFIX_CANCEL, DEFAULT_TOOLTIP_CANCEL);

        String text = loc.getText(prefix + PROPERTY_PREFIX_RECORDCOUNT + PROPERTY_SUFFIX_TEXT, null);
        jLabelRecords.setText((text == null) ? "# Records: " : (text + " "));
    }
    
    /**
     * Performs the post-initializations, which sets the icon of the buttons.
     */
    public final void postInit() {
        jButtonClearSelection.setIcon(iconClearSelection);
        jButtonSearch.setIcon(iconSearch);
        jButtonAdd.setIcon(iconAdd);
        jButtonDuplicate.setIcon(iconDuplicate);
        jButtonEdit.setIcon(iconEdit);
        jButtonDelete.setIcon(iconDelete);
        jButtonOK.setIcon(iconOK);
        jButtonCancel.setIcon(iconCancel);
        
        jButtonClearSelection.setRolloverIcon(iconClearSelectionR);
        jButtonSearch.setRolloverIcon(iconSearchR);
        jButtonAdd.setRolloverIcon(iconAddR);
        jButtonDuplicate.setRolloverIcon(iconDuplicateR);
        jButtonEdit.setRolloverIcon(iconEditR);
        jButtonDelete.setRolloverIcon(iconDeleteR);
        jButtonOK.setRolloverIcon(iconOKR);
        jButtonCancel.setRolloverIcon(iconCancelR);
    }

    /**
     * Initializes the internal MousePointerManager by adding componentes to it.
     */
    private void initMousePointerManager() {
        mpm.add(jButtonClearSelection);
        mpm.add(jButtonSearch);
        mpm.add(jButtonAdd);
        mpm.add(jButtonDuplicate);
        mpm.add(jButtonEdit);
        mpm.add(jButtonDelete);
        mpm.add(jButtonOK);
        mpm.add(jButtonCancel);
    }

    /**
     * Sets the mouse pointer manager for this control panel.
     * 
     * @param mpm  the mouse pointer manager for this control panel
     */
    public void setMousePointerManager(MousePointerManager mpm) {
        this.mpm = null;
        this.mpm = mpm;
        initMousePointerManager();
    }
    
    /**
     * Returns true if the record count is currently shown in this control panel
     * 
     * @return  true if the record count is currently shown in this control panel
     */
    public boolean isRecordCountShown() {
        return recordCountShown;
    }

    /**
     * Sets whether the record count needs to be shown or not in this control panel
     *
     * @param recordCountShown  true if the record count needs to be shown in this control panel
     */
    public void setRecordCountShown(boolean recordCountShown) {
        if (this.recordCountShown != recordCountShown) {
            this.recordCountShown = recordCountShown;
            jLabelRecords.setVisible(recordCountShown);
            jLabelRecordsNo.setVisible(recordCountShown);
            jLabelSpacer.setVisible(recordCountShown);
            updateSeparatorsStatus();
        }
    }

    /**
     * Returns true if the Clear Selection button is currently shown in this control panel
     *
     * @return  true if the Clear Selection button is currently shown in this control panel
     */
    public boolean isButtonClearSelectionShown() {
        return buttonClearSelectionShown;
    }

    /**
     * Sets whether the Clear Selection button needs to be shown or not in this control panel
     *
     * @param shown  true if the Clear Selection button needs to be shown or not in this control panel
     */
    public void setButtonClearSelectionShown(boolean shown) {
        if (buttonClearSelectionShown != shown) {
            buttonClearSelectionShown = shown;
            jButtonClearSelection.setVisible(shown);
            updateSeparatorsStatus();
        }
    }

    /**
     * Returns true if the Search button is currently shown in this control panel
     *
     * @return  true if the Search button is currently shown in this control panel
     */
    public boolean isButtonSearchShown() {
        return buttonSearchShown;
    }

    /**
     * Sets whether the Search button needs to be shown or not in this control panel
     *
     * @param shown  true if the Search button needs to be shown or not in this control panel
     */
    public void setButtonSearchShown(boolean shown) {
        if (buttonSearchShown != shown) {
            buttonSearchShown = shown;
            jButtonSearch.setVisible(shown);
            updateSeparatorsStatus();
        }
    }

    /**
     * Returns true if the Add button is currently shown in this control panel
     *
     * @return  true if the Add button is currently shown in this control panel
     */
    public boolean isButtonAddShown() {
        return buttonAddShown;
    }

    /**
     * Sets whether the Add button needs to be shown or not in this control panel
     *
     * @param shown  true if the Add button needs to be shown or not in this control panel
     */
    public void setButtonAddShown(boolean shown) {
        if (buttonAddShown != shown) {
            buttonAddShown = shown;
            jButtonAdd.setVisible(shown);
            updateSeparatorsStatus();
        }
    }

    /**
     * Returns true if the Duplicate button is currently shown in this control panel
     *
     * @return  true if the Duplicate button is currently shown in this control panel
     */
    public boolean isButtonDuplicateShown() {
        return buttonDuplicateShown;
    }

    /**
     * Sets whether the Duplicate button needs to be shown or not in this control panel
     *
     * @param shown  true if the Duplicate button needs to be shown or not in this control panel
     */
    public void setButtonDuplicateShown(boolean shown) {
        if (buttonDuplicateShown != shown) {
            buttonDuplicateShown = shown;
            jButtonDuplicate.setVisible(shown);
            updateSeparatorsStatus();
        }
    }

    /**
     * Returns true if the Edit button is currently shown in this control panel
     *
     * @return  true if the Edit button is currently shown in this control panel
     */
    public boolean isButtonEditShown() {
        return buttonEditShown;
    }

    /**
     * Sets whether the Edit button needs to be shown or not in this control panel
     *
     * @param shown  true if the Edit button needs to be shown or not in this control panel
     */
    public void setButtonEditShown(boolean shown) {
        if (buttonEditShown != shown) {
            buttonEditShown = shown;
            jButtonEdit.setVisible(shown);
            updateSeparatorsStatus();
        }
    }

    /**
     * Returns true if the Delete button is currently shown in this control panel
     *
     * @return  true if the Delete button is currently shown in this control panel
     */
    public boolean isButtonDeleteShown() {
        return buttonDeleteShown;
    }

    /**
     * Sets whether the Delete button needs to be shown or not in this control panel
     *
     * @param shown  true if the Delete button needs to be shown or not in this control panel
     */
    public void setButtonDeleteShown(boolean shown) {
        if (buttonDeleteShown != shown) {
            buttonDeleteShown = shown;
            jButtonDelete.setVisible(shown);
            updateSeparatorsStatus();
        }
    }

    /**
     * Returns true if the OK button is currently shown in this control panel
     *
     * @return  true if the OK button is currently shown in this control panel
     */
    public boolean isButtonOkShown() {
        return buttonOkShown;
    }

    /**
     * Sets whether the OK button needs to be shown or not in this control panel
     *
     * @param shown  true if the OK button needs to be shown or not in this control panel
     */
    public void setButtonOkShown(boolean shown) {
        if (buttonOkShown != shown) {
            buttonOkShown = shown;
            jButtonOK.setVisible(shown);
            updateSeparatorsStatus();
        }
    }

    /**
     * Returns true if the Cancel button is currently shown in this control panel
     *
     * @return  true if the Cancel button is currently shown in this control panel
     */
    public boolean isButtonCancelShown() {
        return buttonCancelShown;
    }

    /**
     * Sets whether the Cancel button needs to be shown or not in this control panel
     *
     * @param shown  true if the Cancel button needs to be shown or not in this control panel
     */
    public void setButtonCancelShown(boolean shown) {
        if (buttonCancelShown != shown) {
            buttonCancelShown = shown;
            jButtonCancel.setVisible(shown);
            updateSeparatorsStatus();
        }
    }

    /**
     * Returns true if the button ClearSelection is enabled.
     * 
     * @return  true if the button ClearSelection is enabled
     */
    public boolean isButtonClearSelectionEnabled() {
        return buttonClearSelectionEnabled;
    }

    /**
     * Sets if the button ClearSelection is enabled.
     * 
     * @param buttonClearSelectionEnabled  true if the button ClearSelection is enabled
     */
    public void setButtonClearSelectionEnabled(boolean buttonClearSelectionEnabled) {
        this.buttonClearSelectionEnabled = buttonClearSelectionEnabled;
        setEnabled(isEnabled());
    }

    /**
     * Returns true if the button Search is enabled.
     * 
     * @return  true if the button Search is enabled
     */
    public boolean isButtonSearchEnabled() {
        return buttonSearchEnabled;
    }

    /**
     * Sets if the button Search is enabled.
     * 
     * @param buttonSearchEnabled  true if the button Search is enabled
     */
    public void setButtonSearchEnabled(boolean buttonSearchEnabled) {
        this.buttonSearchEnabled = buttonSearchEnabled;
        setEnabled(isEnabled());
    }

    /**
     * Returns true if the button Add is enabled.
     * 
     * @return  true if the button Add is enabled
     */
    public boolean isButtonAddEnabled() {
        return buttonAddEnabled;
    }

    /**
     * Sets if the button Add is enabled.
     * 
     * @param buttonAddEnabled  true if the button Add is enabled
     */
    public void setButtonAddEnabled(boolean buttonAddEnabled) {
        this.buttonAddEnabled = buttonAddEnabled;
        setEnabled(isEnabled());
    }

    /**
     * Returns true if the button Duplicate is enabled.
     * 
     * @return  true if the button Duplicate is enabled
     */
    public boolean isButtonDuplicateEnabled() {
        return buttonDuplicateEnabled;
    }

    /**
     * Sets if the button Duplicate is enabled.
     * 
     * @param buttonDuplicateEnabled  true if the button Duplicate is enabled
     */
    public void setButtonDuplicateEnabled(boolean buttonDuplicateEnabled) {
        this.buttonDuplicateEnabled = buttonDuplicateEnabled;
        setEnabled(isEnabled());
    }

    /**
     * Returns true if the button Edit is enabled.
     * 
     * @return  true if the button Edit is enabled
     */
    public boolean isButtonEditEnabled() {
        return buttonEditEnabled;
    }

    /**
     * Sets if the button Edit is enabled.
     * 
     * @param buttonEditEnabled  true if the button Edit is enabled
     */
    public void setButtonEditEnabled(boolean buttonEditEnabled) {
        this.buttonEditEnabled = buttonEditEnabled;
        setEnabled(isEnabled());
    }

    /**
     * Returns true if the button Delete is enabled.
     * 
     * @return  true if the button Delete is enabled
     */
    public boolean isButtonDeleteEnabled() {
        return buttonDeleteEnabled;
    }

    /**
     * Sets if the button Delete is enabled.
     * 
     * @param buttonDeleteEnabled  true if the button Delete is enabled
     */
    public void setButtonDeleteEnabled(boolean buttonDeleteEnabled) {
        this.buttonDeleteEnabled = buttonDeleteEnabled;
        setEnabled(isEnabled());
    }

    /**
     * Returns true if the button Ok is enabled.
     * 
     * @return  true if the button Ok is enabled
     */
    public boolean isButtonOkEnabled() {
        return buttonOkEnabled;
    }

    /**
     * Sets if the button Ok is enabled.
     * 
     * @param buttonOkEnabled  true if the button Ok is enabled
     */
    public void setButtonOkEnabled(boolean buttonOkEnabled) {
        this.buttonOkEnabled = buttonOkEnabled;
        setEnabled(isEnabled());
    }

    /**
     * Returns true if the button Cancel is enabled.
     * 
     * @return  true if the button Cancel is enabled
     */
    public boolean isButtonCancelEnabled() {
        return buttonCancelEnabled;
    }

    /**
     * Sets if the button Cancel is enabled.
     * 
     * @param buttonCancelEnabled  true if the button Cancel is enabled
     */
    public void setButtonCancelEnabled(boolean buttonCancelEnabled) {
        this.buttonCancelEnabled = buttonCancelEnabled;
        setEnabled(isEnabled());
    }

    /**
     * Sets the tooltip text for the given component.
     *
     * @param comp  the component for which the tooltip text needs to be set
     * @param properties  the properties to be used to retrieve the tooltip text
     * @param prefix  the prefix for all properties to be used to retrieve the tooltip text
     * @param defaultText  the default tooltip text to be used in case the property cannot be found
     */
    private static void setTooltip(JComponent comp, Properties properties, String prefix, String defaultText) {
        String text = properties.getProperty(prefix + PROPERTY_SUFFIX_TOOLTIP, defaultText);
        comp.setToolTipText(text);
    }

    /**
     * Sets the tooltip text for the given component.
     *
     * @param comp  the component for which the tooltip text needs to be set
     * @param loc  the localizer to be used to retrieve the tooltip text
     * @param prefix  the prefix for all properties to be used to retrieve the tooltip text
     * @param defaultText  the default tooltip text to be used in case the property cannot be found
     */
    private static void setTooltip(JComponent comp, Localizer loc, String prefix, String defaultText) {
        String text = loc.getText(prefix + PROPERTY_SUFFIX_TOOLTIP, defaultText);
        comp.setToolTipText(text);
    }

    /**
     * Add a listener for a ControlPanelActionEvent
     *
     * @param l  the listener for a ControlPanelActionEvent to be added
     */
    public void addControlPanelActionListener(ControlPanelActionListener l) {
        controlPanelActionListeners.add(l);
    }

    /**
     * Removes a listener for a ControlPanelActionEvent
     *
     * @param l  the listener for a ControlPanelActionEvent to be removed
     */
    public void removeControlPanelActionListener(ControlPanelActionListener l) {
        controlPanelActionListeners.remove(l);
    }

    /**
     * Sets the table controlled by this control panel.
     * 
     * @param table  the table controlled by this control panel
     */
    public void setTable(TableDataRecord table) {
        this.table = table;

        if (table == null) {
            recordCountChanged(0);
            return;
        }

        table.addSelectionListener(this);
        recordCountChanged(table.getModel().getRowCount());
    }

    /**
     * Returns the table controlled by this control panel.
     * 
     * @return  the table controlled by this control panel
     */
    public TableDataRecord getTable() {
        return table;
    }

    /**
     * Notifies a change in the record count.
     *
     * @param newCount  the new recound count
     */
    @Override
    public void recordCountChanged(long newCount) {
        jLabelRecordsNo.setText(" " + Long.toString(newCount) + " ");
    }


    @Override
    public void selectionChanged(DataRecord sel, TableDataRecord source) {
        if (sel == null) {
            unselectRecord();
        } else {
            selectRecord();
        }
    }

    protected void updateSeparatorsStatus() {
        // The toolbar is like this: A | BCD | EF | G
        
        boolean a = buttonClearSelectionShown || buttonSearchShown;
        boolean bcd = buttonAddShown || buttonDuplicateShown || buttonEditShown || buttonDeleteShown;
        boolean ef = buttonOkShown || buttonCancelShown;
        boolean g = recordCountShown;

        jSeparatorCS.setVisible(a && (bcd || ef || g));
        jSeparatorAED.setVisible(bcd && (ef || g));
        jSeparatorOC.setVisible(ef && g);
    }

    /**
     * Enables the buttons when a record is selected.
     */
    protected void selectRecord() {
        jButtonClearSelection.setEnabled(buttonClearSelectionEnabled);
        jButtonSearch.setEnabled(buttonSearchEnabled);
        jButtonAdd.setEnabled(buttonAddEnabled);
        jButtonDuplicate.setEnabled(buttonDuplicateEnabled);
        jButtonEdit.setEnabled(buttonEditEnabled);
        jButtonDelete.setEnabled(buttonDeleteEnabled);
        jButtonOK.setEnabled(buttonOkEnabled);
        jButtonCancel.setEnabled(buttonCancelEnabled);
        
        managed.forEach(c -> { c.setEnabled(true);} );
    }

    /**
     * Disables the buttons when a record is deselected.
     */
    protected final void unselectRecord() {
        jButtonClearSelection.setEnabled(false);
        jButtonSearch.setEnabled(buttonSearchEnabled);
        jButtonAdd.setEnabled(buttonAddEnabled);
        jButtonDuplicate.setEnabled(false);
        jButtonEdit.setEnabled(false);
        jButtonDelete.setEnabled(false);
        jButtonOK.setEnabled(false);
        jButtonCancel.setEnabled(false);
        
        managed.forEach(c -> { c.setEnabled(false);} );
    }

    /**
     * Sets the cursor for the status Enabled of the buttons.
     *
     * @param enabledCursor  the cursor for the status Enabled of the buttons
     */
    public void setEnabledCursor(Cursor enabledCursor) {
        mpm.setEnabledCursor(enabledCursor);
    }

    /**
     * Sets the cursor for the status Disabled of the buttons.
     *
     * @param disabledCursor  the cursor for the status Disabled of the buttons
     */
    public void setDisabledCursor(Cursor disabledCursor) {
        mpm.setDisabledCursor(disabledCursor);
    }

    /**
     * Sets the cursor for the status Wait of the buttons.
     *
     * @param waitCursor  the cursor for the status Wait of the buttons
     */
    public void setWaitCursor(Cursor waitCursor) {
        mpm.setWaitCursor(waitCursor);
    }

    /**
     * Returns the cursor for the status Enabled of the buttons.
     *
     * @return  the cursor for the status Enabled of the buttons
     */
    public Cursor getEnabledCursor() {
        return mpm.getEnabledCursor();
    }

    /**
     * Returns the cursor for the status Disabled of the buttons.
     *
     * @return  the cursor for the status Disabled of the buttons
     */
    public Cursor getDisabledCursor() {
        return mpm.getDisabledCursor();
    }

    /**
     * Returns the cursor for the status Wait of the buttons.
     *
     * @return  the cursor for the status Wait of the buttons
     */
    public Cursor getWaitCursor() {
        return mpm.getWaitCursor();
    }

    /**
     * Returns the icon for the button "Add".
     * 
     * @return  the icon for the button "Add"
     */
    public Icon getIconAdd() {
        return iconAdd;
    }

    /**
     * Sets the icon for the button "Add".
     * 
     * @param iconAdd  the icon for the button "Add"
     */
    public void setIconAdd(Icon iconAdd) {
        this.iconAdd = iconAdd;
        jButtonAdd.setIcon(iconAdd);
    }

    /**
     * Returns the icon for the button "Cancel".
     * 
     * @return  the icon for the button "Cancel"
     */
    public Icon getIconCancel() {
        return iconCancel;
    }

    /**
     * Sets the icon for the button "Cancel".
     * 
     * @param iconCancel  the icon for the button "Cancel"
     */
    public void setIconCancel(Icon iconCancel) {
        this.iconCancel = iconCancel;
        jButtonCancel.setIcon(iconCancel);
    }

    /**
     * Returns the icon for the button "ClearSelection".
     * 
     * @return  the icon for the button "ClearSelection"
     */
    public Icon getIconClearSelection() {
        return iconClearSelection;
    }

    /**
     * Sets the icon for the button "ClearSelection".
     * 
     * @param iconClearSelection  the icon for the button "ClearSelection"
     */
    public void setIconClearSelection(Icon iconClearSelection) {
        this.iconClearSelection = iconClearSelection;
        jButtonClearSelection.setIcon(iconClearSelection);
    }

    /**
     * Returns the icon for the button "Delete".
     * 
     * @return  the icon for the button "Delete"
     */
    public Icon getIconDelete() {
        return iconDelete;
    }

    /**
     * Sets the icon for the button "Delete".
     * 
     * @param iconDelete  the icon for the button "Delete"
     */
    public void setIconDelete(Icon iconDelete) {
        this.iconDelete = iconDelete;
        jButtonDelete.setIcon(iconDelete);
    }

    /**
     * Returns the icon for the button "Duplicate".
     * 
     * @return  the icon for the button "Duplicate"
     */
    public Icon getIconDuplicate() {
        return iconDuplicate;
    }

    /**
     * Sets the icon for the button "Duplicate".
     * 
     * @param iconDuplicate  the icon for the button "Duplicate"
     */
    public void setIconDuplicate(Icon iconDuplicate) {
        this.iconDuplicate = iconDuplicate;
        jButtonDuplicate.setIcon(iconDuplicate);
    }

    /**
     * Returns the icon for the button "Edit".
     * 
     * @return  the icon for the button "Edit"
     */
    public Icon getIconEdit() {
        return iconEdit;
    }

    /**
     * Sets the icon for the button "Edit".
     * 
     * @param iconEdit  the icon for the button "Edit"
     */
    public void setIconEdit(Icon iconEdit) {
        this.iconEdit = iconEdit;
        jButtonEdit.setIcon(iconEdit);
    }

    /**
     * Returns the icon for the button "OK".
     * 
     * @return  the icon for the button "OK"
     */
    public Icon getIconOK() {
        return iconOK;
    }

    /**
     * Sets the icon for the button "OK".
     * 
     * @param iconOK  the icon for the button "OK"
     */
    public void setIconOK(Icon iconOK) {
        this.iconOK = iconOK;
        jButtonOK.setIcon(iconOK);
    }

    /**
     * Returns the icon for the button "Search".
     * 
     * @return  the icon for the button "Search"
     */
    public Icon getIconSearch() {
        return iconSearch;
    }

    /**
     * Sets the icon for the button "Search".
     * 
     * @param iconSearch  the icon for the button "Search"
     */
    public void setIconSearch(Icon iconSearch) {
        this.iconSearch = iconSearch;
        jButtonSearch.setIcon(iconSearch);
    }
    
    /**
     * Returns the rollover icon for the button "Add".
     * 
     * @return  the rollover icon for the button "Add"
     */
    public Icon getIconAddR() {
        return iconAddR;
    }

    /**
     * Sets the rollover icon for the button "Add".
     * 
     * @param iconAddR  the rollover icon for the button "Add"
     */
    public void setIconAddR(Icon iconAddR) {
        this.iconAddR = iconAddR;
        jButtonAdd.setRolloverIcon(iconAddR);
    }

    /**
     * Returns the rollover icon for the button "Cancel".
     * 
     * @return  the rollover icon for the button "Cancel"
     */
    public Icon getIconCancelR() {
        return iconCancelR;
    }

    /**
     * Sets the rollover icon for the button "Cancel".
     * 
     * @param iconCancelR  the rollover icon for the button "Cancel"
     */
    public void setIconCancelR(Icon iconCancelR) {
        this.iconCancelR = iconCancelR;
        jButtonCancel.setRolloverIcon(iconCancelR);
    }

    /**
     * Returns the rollover icon for the button "ClearSelection".
     * 
     * @return  the rollover icon for the button "ClearSelection"
     */
    public Icon getIconClearSelectionR() {
        return iconClearSelectionR;
    }

    /**
     * Sets the rollover icon for the button "ClearSelection".
     * 
     * @param iconClearSelectionR  the rollover icon for the button "ClearSelection"
     */
    public void setIconClearSelectionR(Icon iconClearSelectionR) {
        this.iconClearSelectionR = iconClearSelectionR;
        jButtonClearSelection.setRolloverIcon(iconClearSelectionR);
    }

    /**
     * Returns the rollover icon for the button "Delete".
     * 
     * @return  the rollover icon for the button "Delete"
     */
    public Icon getIconDeleteR() {
        return iconDeleteR;
    }

    /**
     * Sets the rollover icon for the button "Delete".
     * 
     * @param iconDeleteR  the rollover icon for the button "Delete"
     */
    public void setIconDeleteR(Icon iconDeleteR) {
        this.iconDeleteR = iconDeleteR;
        jButtonDelete.setRolloverIcon(iconDeleteR);
    }

    /**
     * Returns the rollover icon for the button "Duplicate".
     * 
     * @return  the rollover icon for the button "Duplicate"
     */
    public Icon getIconDuplicateR() {
        return iconDuplicateR;
    }

    /**
     * Sets the rollover icon for the button "Duplicate".
     * 
     * @param iconDuplicateR  the rollover icon for the button "Duplicate"
     */
    public void setIconDuplicateR(Icon iconDuplicateR) {
        this.iconDuplicateR = iconDuplicateR;
        jButtonDuplicate.setRolloverIcon(iconDuplicateR);
    }

    /**
     * Returns the rollover icon for the button "Edit".
     * 
     * @return  the rollover icon for the button "Edit"
     */
    public Icon getIconEditR() {
        return iconEditR;
    }

    /**
     * Sets the rollover icon for the button "Edit".
     * 
     * @param iconEditR  the rollover icon for the button "Edit"
     */
    public void setIconEditR(Icon iconEditR) {
        this.iconEditR = iconEditR;
        jButtonEdit.setRolloverIcon(iconEditR);
    }

    /**
     * Returns the rollover icon for the button "OK".
     * 
     * @return  the rollover icon for the button "OK"
     */
    public Icon getIconOKR() {
        return iconOKR;
    }

    /**
     * Sets the rollover icon for the button "OK".
     * 
     * @param iconOKR  the rollover icon for the button "OK"
     */
    public void setIconOKR(Icon iconOKR) {
        this.iconOKR = iconOKR;
        jButtonOK.setRolloverIcon(iconOKR);
    }

    /**
     * Returns the rollover icon for the button "Search".
     * 
     * @return  the rollover icon for the button "Search"
     */
    public Icon getIconSearchR() {
        return iconSearchR;
    }

    /**
     * Sets the rollover icon for the button "Search".
     * 
     * @param iconSearchR  the rollover icon for the button "Search"
     */
    public void setIconSearchR(Icon iconSearchR) {
        this.iconSearchR = iconSearchR;
        jButtonSearch.setRolloverIcon(iconSearchR);
    }
    
    /**
     * Adds an externally-defined component to the control panel, before the count of records.
     * The component will not be automatically selected/deselected based on the table selection.
     * 
     * @param comp  the component to be added (null = separator)
     */
    public void addComponent(JComponent comp) {
        addComponent(comp, false);
    }
    
    /**
     * Adds an externally-defined component to the control panel, before the count of records.
     * 
     * @param comp  the component to be added (null = separator)
     * @param isManaged  if true, the component is automatically enabled/disabled based on the table selection
     */
    public void addComponent(JComponent comp, boolean isManaged) {
        if (comp == null) {
            comp = new javax.swing.JToolBar.Separator();
            isManaged = false;
        }
        
        int idx = jToolBarMain.getComponentIndex(jSeparatorOC);
        jToolBarMain.add(comp, idx);
        jToolBarMain.validate();
        
        if (isManaged) managed.add(comp);
    }
    
    /**
     * Adds an externally-defined component to the control panel, at the end of it.
     * The component will not be automatically selected/deselected based on the table selection.
     * 
     * @param comp  the component to be added (null = separator)
     */
    public void addComponentAtTheEnd(JComponent comp) {
        addComponentAtTheEnd(comp, false);
    }
    
    /**
     * Adds an externally-defined component to the control panel, at the end of it.
     * 
     * @param comp  the component to be added (null = separator)
     * @param isManaged  if true, the component is automatically enabled/disabled based on the table selection
     */
    public void addComponentAtTheEnd(JComponent comp, boolean isManaged) {
        if (comp == null) {
            comp = new javax.swing.JToolBar.Separator();
            isManaged = false;
        }
        
        jToolBarMain.add(comp);
        
        if (isManaged) managed.add(comp);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            if (table != null) {
                if (table.getSelectedRowCount() == 0) {
                    unselectRecord();
                } else {
                    selectRecord();
                }
            }
            jLabelRecords.setEnabled(enabled);
            jLabelRecordsNo.setEnabled(enabled);
        } else {
            jButtonAdd.setEnabled(enabled && buttonAddEnabled);
            jButtonCancel.setEnabled(enabled && buttonCancelEnabled);
            jButtonClearSelection.setEnabled(enabled && buttonClearSelectionEnabled);
            jButtonDelete.setEnabled(enabled && buttonDeleteEnabled);
            jButtonDuplicate.setEnabled(enabled && buttonDuplicateEnabled);
            jButtonEdit.setEnabled(enabled && buttonEditEnabled);
            jButtonOK.setEnabled(enabled && buttonOkEnabled);
            jButtonSearch.setEnabled(enabled && buttonSearchEnabled);
            jLabelRecords.setEnabled(enabled);
            jLabelRecordsNo.setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBarMain = new javax.swing.JToolBar();
        jButtonClearSelection = new javax.swing.JButton();
        jButtonSearch = new javax.swing.JButton();
        jSeparatorCS = new javax.swing.JToolBar.Separator();
        jButtonAdd = new javax.swing.JButton();
        jButtonDuplicate = new javax.swing.JButton();
        jButtonEdit = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jSeparatorAED = new javax.swing.JToolBar.Separator();
        jButtonOK = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jSeparatorOC = new javax.swing.JToolBar.Separator();
        jLabelRecords = new javax.swing.JLabel();
        jLabelRecordsNo = new javax.swing.JLabel();
        jLabelSpacer = new javax.swing.JLabel();

        jToolBarMain.setBorder(null);
        jToolBarMain.setFloatable(false);
        jToolBarMain.setRollover(true);

        jButtonClearSelection.setIcon(getIconClearSelection());
        jButtonClearSelection.setFocusable(false);
        jButtonClearSelection.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonClearSelection.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonClearSelection.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonClearSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearSelectionActionPerformed(evt);
            }
        });
        jToolBarMain.add(jButtonClearSelection);

        jButtonSearch.setIcon(getIconSearch());
        jButtonSearch.setFocusable(false);
        jButtonSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSearch.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });
        jToolBarMain.add(jButtonSearch);
        jToolBarMain.add(jSeparatorCS);

        jButtonAdd.setIcon(getIconAdd());
        jButtonAdd.setFocusable(false);
        jButtonAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAdd.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });
        jToolBarMain.add(jButtonAdd);

        jButtonDuplicate.setIcon(getIconDuplicate());
        jButtonDuplicate.setFocusable(false);
        jButtonDuplicate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDuplicate.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonDuplicate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDuplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDuplicateActionPerformed(evt);
            }
        });
        jToolBarMain.add(jButtonDuplicate);

        jButtonEdit.setIcon(getIconEdit());
        jButtonEdit.setFocusable(false);
        jButtonEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonEdit.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditActionPerformed(evt);
            }
        });
        jToolBarMain.add(jButtonEdit);

        jButtonDelete.setIcon(getIconDelete());
        jButtonDelete.setFocusable(false);
        jButtonDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDelete.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        jToolBarMain.add(jButtonDelete);
        jToolBarMain.add(jSeparatorAED);

        jButtonOK.setIcon(getIconOK());
        jButtonOK.setFocusable(false);
        jButtonOK.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonOK.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonOK.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });
        jToolBarMain.add(jButtonOK);

        jButtonCancel.setIcon(getIconCancel());
        jButtonCancel.setFocusable(false);
        jButtonCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonCancel.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jToolBarMain.add(jButtonCancel);
        jToolBarMain.add(jSeparatorOC);

        jLabelRecords.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelRecords.setText("# Records: ");
        jToolBarMain.add(jLabelRecords);

        jLabelRecordsNo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelRecordsNo.setText(" 0 ");
        jLabelRecordsNo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jToolBarMain.add(jLabelRecordsNo);

        jLabelSpacer.setText("  ");
        jToolBarMain.add(jLabelSpacer);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBarMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBarMain, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonClearSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearSelectionActionPerformed
        ControlPanelActionEvent e = new ControlPanelActionEvent(this, ActionType.CLEAR_SELECTION);
        controlPanelActionListeners.forEach((l) -> {
            l.actionClearSelection(e);
        });
    }//GEN-LAST:event_jButtonClearSelectionActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        ControlPanelActionEvent e = new ControlPanelActionEvent(this, ActionType.ADD);
        controlPanelActionListeners.forEach((l) -> {
            l.actionAdd(e);
        });
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditActionPerformed
        ControlPanelActionEvent e = new ControlPanelActionEvent(this, ActionType.EDIT);
        controlPanelActionListeners.forEach((l) -> {
            l.actionEdit(e);
        });
    }//GEN-LAST:event_jButtonEditActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        ControlPanelActionEvent e = new ControlPanelActionEvent(this, ActionType.DELETE);
        controlPanelActionListeners.forEach((l) -> {
            l.actionDelete(e);
        });
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        ControlPanelActionEvent e = new ControlPanelActionEvent(this, ActionType.OK);
        controlPanelActionListeners.forEach((l) -> {
            l.actionOK(e);
        });
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        ControlPanelActionEvent e = new ControlPanelActionEvent(this, ActionType.CANCEL);
        controlPanelActionListeners.forEach((l) -> {
            l.actionCancel(e);
        });
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchActionPerformed
        ControlPanelActionEvent e = new ControlPanelActionEvent(this, ActionType.SEARCH);
        controlPanelActionListeners.forEach((l) -> {
            l.actionSearch(e);
        });
    }//GEN-LAST:event_jButtonSearchActionPerformed

    private void jButtonDuplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDuplicateActionPerformed
        ControlPanelActionEvent e = new ControlPanelActionEvent(this, ActionType.DUPLICATE);
        controlPanelActionListeners.forEach((l) -> {
            l.actionDuplicate(e);
        });
    }//GEN-LAST:event_jButtonDuplicateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonClearSelection;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonDuplicate;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JLabel jLabelRecords;
    private javax.swing.JLabel jLabelRecordsNo;
    private javax.swing.JLabel jLabelSpacer;
    private javax.swing.JToolBar.Separator jSeparatorAED;
    private javax.swing.JToolBar.Separator jSeparatorCS;
    private javax.swing.JToolBar.Separator jSeparatorOC;
    private javax.swing.JToolBar jToolBarMain;
    // End of variables declaration//GEN-END:variables
}
