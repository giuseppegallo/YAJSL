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

import YAJSL.Data.DataListListener;
import YAJSL.Data.DataRecordSortedListManager;
import YAJSL.Swing.Application;
import static YAJSL.Swing.Beans.ComboBoxListable.PROPERTY_TOOLTIP_SUFFIX;
import YAJSL.Swing.RenderersAndEditors.ListCellRendererListable;
import YAJSL.Utils.ExtendedProperties;
import java.util.Properties;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;
import YAJSL.Data.DataRecord;
import YAJSL.Data.DataRecordManager;
import java.awt.Dimension;


/**
 * A JComboBox used for listing data objectes.
 *
 * @param <T>  the type of DataRecord to be shown in this combo box
 * @author Giuseppe Gallo
 */
public class ComboBoxDataRecord<T extends DataRecord> extends JComboBox implements DataListListener {

    /**
     * The table model used for the data object table.
     * @param <K>  the type of DataRecord handled by this table model
     */
    public static class ComboBoxModelDataRecord<K extends DataRecord> extends AbstractListModel implements ComboBoxModel {

        /** The data manager used by this combo box model */
        protected DataRecordManager<K> dm;

        /** The data object currently selected */
        protected K selectedItem = null;

        /** True if the null value is allowed (it will be the first element in the list) */
        protected boolean allowNull = true;

        /** Internally used to add/subtract 1 from indexes/length because of the null value */
        private int nullDelta = 0;
        
        /**
         * Instantiates a new ComboBoxModelDataRecord.
         *
         * @param dm  the data manager for this combo box model
         * @param allowNull  if true, the null value is allowed (and automatically added to the list as first element)
         */
        protected ComboBoxModelDataRecord(DataRecordManager<K> dm, boolean allowNull) {
            super();
            this.dm = dm;
            this.allowNull = allowNull;
            nullDelta = (allowNull) ? 1 : 0;
        }

        /**
         * Sets the data manager to be used by this combo box model.
         *
         * @param dm  the data source to be used by this combo box model
         */
        public void setDataManager(DataRecordManager<K> dm) {
            this.dm = dm;
            listChanged();
        }

        /**
         * Returns the number of elements in the list.
         *
         * @return  the number of elements in the list
         */
        @Override
        public int getSize() {
            return (dm == null) ? 0 : dm.size() + nullDelta;
        }

        /**
         * Returns the element at the given index.
         *
         * @param index  the index of the element to be returned
         * @return  the element at the given index
         */
        @Override
        public K getElementAt(int index) {
            if (dm == null) return null;
            K value = (allowNull) ? (index < nullDelta) ? null : dm.get(index - nullDelta) : dm.get(index);
            return value;
        }

        /**
         * Sets the object currently selected.
         * Nothing is done if the object is not contained in the list.
         * 
         * @param anItem  the object to be selected
         */
        @Override
        public void setSelectedItem(Object anItem) {
            if (anItem == null && allowNull) {
                selectedItem = null;
                return;
            }

            selectedItem = (K)anItem;
        }

        /**
         * Returns the object currently selected.
         *
         * @return  the object currently selected
         */
        @Override
        public K getSelectedItem() {
            return selectedItem;
        }

        /**
         * Fires the events related to the addition of an object at the given index.
         *
         * @param index  the index at which the object has been added
         */
        public void fireItemAdded(int index) {
            fireIntervalAdded(this, index + nullDelta, index + nullDelta);
        }

        /**
         * Fires the events related to the removal of an object at the given index.
         *
         * @param index  the index from which the object has been removed
         */
        public void fireItemRemoved(int index) {
            fireIntervalRemoved(this, index + nullDelta, index + nullDelta);
        }

        /**
         * Fires the events related to the change of an object at the given index.
         *
         * @param index  the index at which the object has been changed
         */
        public void fireItemChanged(int index) {
            fireContentsChanged(this, index + nullDelta, index + nullDelta);
        }
        
        /**
         * Refreshes the list of objects.
         */
        public void listChanged() {
            if (dm == null) return;
            fireContentsChanged(this, nullDelta, dm.size() - 1 + nullDelta);
        }
    }

    /** True if the null value is allowed (it will be the first element in the list) */
    protected boolean nullAllowed = true;

    /** The combo box model used with this combo box */
    protected ComboBoxModelDataRecord<T> model = null;

    /** True while laying out the combo box */
    private boolean layingOut = false;


    /**
     * Instantiates a ComboBoxDataRecord.
     */
    public ComboBoxDataRecord() {
        super(new ComboBoxModelDataRecord(null, true));
        model = (ComboBoxModelDataRecord<T>)getModel();
        setEditable(false);
        setRenderer(new ListCellRendererListable());
    }

    /**
     * Instantiates a ComboBoxDataRecord.
     *
     * @param cm  the combo box model to be used
     */
    protected ComboBoxDataRecord(ComboBoxModel cm) {
        super(cm);
        if (cm != null && cm instanceof ComboBoxModelDataRecord) {
            model = (ComboBoxModelDataRecord<T>)cm;
        }
        setEditable(false);
        setRenderer(new ListCellRendererListable());
    }

    /**
     * Instantiates a ComboBoxDataRecord.
     * 
     * @param renderer  the renderer to be used (null = default)
     */
    public ComboBoxDataRecord(ListCellRenderer renderer) {
        this();
        if (renderer != null) setRenderer(renderer);
    }

    /**
     * Instantiates a ComboBoxDataRecord.
     *
     * @param cm  the combo box model to be used
     * @param renderer  the renderer to be used (null = default)
     */
    protected ComboBoxDataRecord(ComboBoxModel cm, ListCellRenderer renderer) {
        this(cm);
        if (renderer != null) setRenderer(renderer);
    }

    /**
     * Initializes the ComboBoxDataRecord.
     *
     * @param <T>  the type of data object handled by this combo box
     * @param dm  the data manager to be used with this combo box
     * @param tooltip  the tooltip to be used with the combo box
     * @param allowNull  if true, the null value is allowed (and automatically added to the list as first element)
     */
    public <T extends DataRecord> void init(DataRecordManager<T> dm, String tooltip, boolean allowNull) {
        ComboBoxModelDataRecord lm = new ComboBoxModelDataRecord(dm, allowNull);
        setModel(lm);
        setToolTipText(tooltip);
        setRenderer(new ListCellRendererListable());
        
        if (dm instanceof DataRecordSortedListManager) {
            ((DataRecordSortedListManager)dm).addDataListListener(this);
        }
    }

    /**
     * Initializes the ComboBoxDataRecord.
     * 
     * @param <T>  the type of data object handled by this combo box
     * @param dm  the data manager to be used with this combo box
     * @param app  the application containing the properties
     * @param propertiesPrefix  the common prefix of all properties related to this combo box
     */
    public <T extends DataRecord> void init(DataRecordManager<T> dm, Application app, String propertiesPrefix) {

        String tooltip = app.getLocalizer().getText(propertiesPrefix + PROPERTY_TOOLTIP_SUFFIX);
        Properties properties = app.getProperties();

        Boolean allowNull = ExtendedProperties.getBooleanPropertySilent(properties, ".allowNull", false);

        init(dm, tooltip, allowNull);
    }

    /**
     * Sets the data manager to be used by this combo box.
     *
     * @param dm  the data manager to be used by this combo box
     */
    public void setDataManager(DataRecordManager<T> dm) {
        model.setDataManager(dm);
        setModel(model);
    }

    /**
     * Returns the data object currently selected.
     *
     * @return  the data object currently selected
     */
    public T getSelectedObject() {
        return (T)model.getSelectedItem();
    }

    /**
     * Selects the given data object in the combo box.
     *
     * @param obj  the data object to be selected
     */
    public void setSelectedObject(T obj) {
        model.setSelectedItem(obj);
        selectedItemChanged();
    }

    /**
     * Returns true if the null value (empty text) is allowed.
     *
     * @return  true if the null value (empty text) is allowed
     */
    public boolean isNullAllowed() {
        return nullAllowed;
    }

    /**
     * If true, the null value (empty text) is allowed.
     *
     * @param nullAllowed  if true, the null value (empty text) is allowed
     */
    public void setNullAllowed(boolean nullAllowed) {
        if (model != null && model.getSize() > 0 && this.nullAllowed && !nullAllowed && getSelectedObject() == null) {
            setSelectedIndex(model.getSize() - 1);
        }

        this.nullAllowed = nullAllowed;
        
        if (model != null) {
            model.allowNull = nullAllowed;
            model.nullDelta = (nullAllowed) ? 1 : 0;
        }
    }

    @Override
    public void setModel(ComboBoxModel model) {
        if (model == null || !(model instanceof ComboBoxModelDataRecord)) return;
        super.setModel(model);
        this.model = (ComboBoxModelDataRecord<T>)model;
    }

    /**
     * Notifies a generic change in the list of objects.
     * 
     * @param type  the class of the objects in the list
     */
    @Override
    public void listChanged(Class type) {
        model.listChanged();
    }

    /**
     * Notifies the addition of a object to a list (class-specific).
     *
     * @param type  the class of the object added
     * @param object  the object added
     */
    @Override
    public void objectAdded(int index, Class type, Object object) {
        model.fireItemAdded(index);
    }

    /**
     * Notifies the removal of a object from a list (class-specific).
     *
     * @param type  the class of the object removed
     */
    @Override
    public void objectRemoved(int index, Class type) {
        model.fireItemRemoved(index);
    }

    /**
     * Notifies the change of a object in a list (class-specific).
     *
     * @param type  the class of the object changed
     * @param object  the object changed
     */
    @Override
    public void objectChanged(int index, Class type, Object object) {
        model.fireItemChanged(index);
    }

    /**
     * Returns the maximum width of the elements in the list.
     *
     * @return  the maximum width of the elements in the list
     */
    protected int getMaxElementWidth() {
        int max = 0;

        ListCellRenderer r = getRenderer();
        if (!(r instanceof ListCellRendererListable)) return max;

        for (int i = 0; i < model.getSize(); ++i) {
            int width = ((ListCellRendererListable)r).getElementWidth(model.getElementAt(i), getGraphics().getFontMetrics());
            if (max < width) max = width;
        }

        return max;
    }

    @Override
    public void doLayout(){
        try{
            layingOut = true;
            super.doLayout();
        } finally {
            layingOut = false;
        }
    }

    @Override
    public Dimension getSize(){
        Dimension dim = super.getSize();

        if (!layingOut) {
            dim.width = Math.max(getMaxElementWidth() + 25, dim.width);
        }

        return dim;
    }

    /**
     * Allocates a ComboBoxDataRecord.
     * 
     * @param <T>  the type of data object handled by this combo box
     * @param dm  the data manager to be used with this combo box
     * @param app  the application containing the properties
     * @param propertiesPrefix  the common prefix of all properties related to this combo box
     * @return  the instance of the ComboBoxDataRecord allocated
     */
    public static <T extends DataRecord> ComboBoxDataRecord<T> allocate(DataRecordManager<T> dm, Application app, String propertiesPrefix) {
        return allocate(dm, app, propertiesPrefix, null);
    }

    /**
     * Allocates a ComboBoxDataRecord.
     *
     * @param <T>  the type of data object handled by this combo box
     * @param dm  the dm manager to be used with this combo box
     * @param tooltip  the tooltip to be used with the combo box
     * @param allowNull  if true, the null value is allowed (and automatically added to the list as first element)
     * @return  the instance of the ComboBoxDataRecord allocated
     */
    public static <T extends DataRecord> ComboBoxDataRecord<T> allocate(DataRecordManager<T> dm, String tooltip, boolean allowNull) {
        return allocate(dm, tooltip, allowNull, null);
    }

    /**
     * Allocates a ComboBoxDataRecord.
     * 
     * @param <T>  the type of data object handled by this combo box
     * @param dm  the data manager to be used with this combo box
     * @param app  the application containing the properties
     * @param propertiesPrefix  the common prefix of all properties related to this combo box
     * @param renderer  the renderer to be used (null = default)
     * @return  the instance of the ComboBoxDataRecord allocated
     */
    public static <T extends DataRecord> ComboBoxDataRecord<T> allocate(DataRecordManager<T> dm, Application app, String propertiesPrefix, ListCellRenderer renderer) {

        String tooltip = app.getLocalizer().getText(propertiesPrefix + PROPERTY_TOOLTIP_SUFFIX);
        Properties properties = app.getProperties();

        Boolean allowNull = ExtendedProperties.getBooleanPropertySilent(properties, ".allowNull", false);

        return allocate(dm, tooltip, allowNull, renderer);
    }

    /**
     * Allocates a ComboBoxDataRecord.
     *
     * @param <T>  the type of data object handled by this combo box
     * @param dm  the dm manager to be used with this combo box
     * @param tooltip  the tooltip to be used with the combo box
     * @param allowNull  if true, the null value is allowed (and automatically added to the list as first element)
     * @param renderer  the renderer to be used (null = default)
     * @return  the instance of the ComboBoxDataRecord allocated
     */
    public static <T extends DataRecord> ComboBoxDataRecord<T> allocate(DataRecordManager<T> dm, String tooltip, boolean allowNull, ListCellRenderer renderer) {

        ComboBoxModelDataRecord lm = new ComboBoxModelDataRecord(dm, allowNull);
        ComboBoxDataRecord<T> comboBox = new ComboBoxDataRecord<>(lm, renderer);
        comboBox.setToolTipText(tooltip);

        return comboBox;
    }
}
