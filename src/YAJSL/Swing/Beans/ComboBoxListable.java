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
import YAJSL.Data.Listable;
import YAJSL.Swing.Application;
import YAJSL.Swing.RenderersAndEditors.ListCellRendererListable;
import YAJSL.Utils.ExtendedProperties;
import YAJSL.Utils.ExtendedProperties.InvalidPropertyValueException;
import YAJSL.Utils.ExtendedProperties.MissingPropertyException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;


/**
 * A JComboBox used for listing data objects.
 *
 * @param <T>  the type of Listable to be shown in this combo box
 * @author Giuseppe Gallo
 */
public class ComboBoxListable<T extends Listable> extends JComboBox implements DataListListener {

    /**
     * The table model used for the data object table.
     * @param <K>  the type of Listable handled by this table model
     */
    public static class ComboBoxModelListableData<K extends Listable> extends AbstractListModel implements ComboBoxModel {

        /** The elements in the list */
        protected ArrayList<K> elements = null;
        
        /** The data object currently selected */
        protected K selectedItem = null;

        /** True if the null value is allowed (it will be the first element in the list) */
        protected boolean allowNull = true;

        /** Internally used to add/subtract 1 from indexes/length because of the null value */
        private int nullDelta = 0;
        
        /**
         * Instantiates a new ComboBoxModelListableData.
         *
         * @param elements  the elements for this combo box
         * @param allowNull  if true, the null value is allowed (and automatically added to the list as first element)
         */
        protected ComboBoxModelListableData(Collection<K> elements, boolean allowNull) {
            super();
            this.elements = (elements == null) ? null : new ArrayList<>(elements);
            this.allowNull = allowNull;
            nullDelta = (allowNull) ? 1 : 0;
        }

        /**
         * Sets the list of elements to be used by this combo box model.
         *
         * @param elements  the elements for this combo box
         */
        public void setElements(Collection<K> elements) {
            this.elements = (elements == null) ? null : new ArrayList<>(elements);
        }

        /**
         * Returns the number of elements in the list.
         *
         * @return  the number of elements in the list
         */
        @Override
        public int getSize() {
            return (elements == null) ? 0 : elements.size() + nullDelta;
        }

        /**
         * Returns the element at the given index.
         *
         * @param index  the index of the element to be returned
         * @return  the element at the given index
         */
        @Override
        public K getElementAt(int index) {
            if (elements == null) return null;
            K value = (allowNull) ? (index == 0) ? null : elements.get(index - 1) : elements.get(index);
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
         * Returns the data object currently selected.
         *
         * @return  the the data object currently selected
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
            fireContentsChanged(this, nullDelta, elements.size() - 1 + nullDelta);
        }
    }

    /** The suffix used for the tooltip for the combo box in the message properties */
    public static final String PROPERTY_TOOLTIP_SUFFIX = ".tooltip";

    /** The suffix used for the getter method in the properties */
    public static final String PROPERTY_ALLOWNULL_SUFFIX = ".allowNull";

    /** True if the null value is allowed (it will be the first element in the list) */
    protected boolean nullAllowed = true;

    /** The combo box model used with this combo box */
    protected ComboBoxModelListableData<T> model = null;

    /**
     * Instantiates a ComboBoxListable.
     */
    public ComboBoxListable() {
        super(new ComboBoxModelListableData<>(null, true));
        model = (ComboBoxModelListableData<T>)getModel();
        setEditable(false);
        setRenderer(new ListCellRendererListable());
    }

    /**
     * Instantiates a ComboBoxListable.
     *
     * @param cm  the combo box model to be used
     */
    protected ComboBoxListable(ComboBoxModel cm) {
        super(cm);
        if (cm != null && cm instanceof ComboBoxModelListableData) {
            model = (ComboBoxModelListableData<T>)cm;
        }
        setEditable(false);
        setRenderer(new ListCellRendererListable());
    }

    /**
     * Instantiates a ComboBoxListable.
     * 
     * @param renderer  the renderer to be used (null = default)
     */
    public ComboBoxListable(ListCellRenderer renderer) {
        this();
        if (renderer != null) setRenderer(renderer);
    }

    /**
     * Instantiates a ComboBoxListable.
     *
     * @param cm  the combo box model to be used
     * @param renderer  the renderer to be used (null = default)
     */
    protected ComboBoxListable(ComboBoxModel cm, ListCellRenderer renderer) {
        this(cm);
        if (renderer != null) setRenderer(renderer);
    }

    /**
     * Initializes the ComboBoxListable.
     *
     * @param <T>  the type of data object handled by this combo box
     * @param elements  the elements for this combo box
     * @param tooltip  the tooltip to be used with the combo box
     * @param allowNull  if true, the null value is allowed (and automatically added to the list as first element)
     */
    public <T extends Listable> void init(Collection<T> elements, String tooltip, boolean allowNull) {
        ComboBoxModelListableData lm = new ComboBoxModelListableData(elements, allowNull);
        setModel(lm);
        setToolTipText(tooltip);
        setRenderer(new ListCellRendererListable());
    }

    /**
     * Initializes the ComboBoxListable.
     * 
     * @param <T>  the type of data object handled by this combo box
     * @param elements  the elements for this combo box
     * @param app  the application containing the properties
     * @param propertiesPrefix  the common prefix of all properties related to this combo box
     */
    public <T extends Listable> void init(Collection<T> elements, Application app, String propertiesPrefix) {

        String tooltip = app.getLocalizer().getText(propertiesPrefix + PROPERTY_TOOLTIP_SUFFIX);
        Properties properties = app.getProperties();

        Boolean allowNull = false;
        try {
            allowNull = ExtendedProperties.getBooleanProperty(properties, PROPERTY_ALLOWNULL_SUFFIX, false, null);
        } catch (MissingPropertyException | InvalidPropertyValueException ex) {
            Logger.getLogger(ComboBoxListable.class.getName()).log(Level.SEVERE, null, ex);
        }

        init(elements, tooltip, allowNull);
    }

    /**
     * Sets the list of elements to be used by this combo box.
     *
     * @param elements  the elements for this combo box
     */
    public void setElements(Collection<T> elements) {
        model.setElements(elements);
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
        if (model == null || !(model instanceof ComboBoxModelListableData)) return;
        super.setModel(model);
        this.model = (ComboBoxModelListableData<T>)model;
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
     * Allocates a ComboBoxListable.
     * 
     * @param <T>  the type of data object handled by this combo box
     * @param elements  the elements for this combo box
     * @param app  the application containing the properties
     * @param propertiesPrefix  the common prefix of all properties related to this combo box
     * @return  the instance of the ComboBoxListable allocated
     */
    public static <T extends Listable> ComboBoxListable<T> allocate(Collection<T> elements, Application app, String propertiesPrefix) {
        return allocate(elements, app, propertiesPrefix, null);
    }

    /**
     * Allocates a ComboBoxListable.
     *
     * @param <T>  the type of data object handled by this combo box
     * @param elements  the elements for this combo box
     * @param tooltip  the tooltip to be used with the combo box
     * @param allowNull  if true, the null value is allowed (and automatically added to the list as first element)
     * @return  the instance of the ComboBoxListable allocated
     */
    public static <T extends Listable> ComboBoxListable<T> allocate(Collection<T> elements, String tooltip, boolean allowNull) {
        return allocate(elements, tooltip, allowNull, null);
    }

    /**
     * Allocates a ComboBoxListable.
     * 
     * @param <T>  the type of data object handled by this combo box
     * @param elements  the elements for this combo box
     * @param app  the application containing the properties
     * @param propertiesPrefix  the common prefix of all properties related to this combo box
     * @param renderer  the renderer to be used (null = default)
     * @return  the instance of the ComboBoxListable allocated
     */
    public static <T extends Listable> ComboBoxListable<T> allocate(Collection<T> elements, Application app, String propertiesPrefix, ListCellRenderer renderer) {

        String tooltip = app.getLocalizer().getText(propertiesPrefix + PROPERTY_TOOLTIP_SUFFIX);
        Properties properties = app.getProperties();

        Boolean allowNull = false;
        try {
            allowNull = ExtendedProperties.getBooleanProperty(properties, PROPERTY_ALLOWNULL_SUFFIX, false, null);
        } catch (MissingPropertyException | InvalidPropertyValueException ex) {
            Logger.getLogger(ComboBoxListable.class.getName()).log(Level.SEVERE, null, ex);
        }

        return allocate(elements, tooltip, allowNull, renderer);
    }

    /**
     * Allocates a ComboBoxListable.
     *
     * @param <T>  the type of data object handled by this combo box
     * @param elements  the elements for this combo box
     * @param tooltip  the tooltip to be used with the combo box
     * @param allowNull  if true, the null value is allowed (and automatically added to the list as first element)
     * @param renderer  the renderer to be used (null = default)
     * @return  the instance of the ComboBoxListable allocated
     */
    public static <T extends Listable> ComboBoxListable<T> allocate(Collection<T> elements, String tooltip, boolean allowNull, ListCellRenderer renderer) {

        ComboBoxModelListableData lm = new ComboBoxModelListableData(elements, allowNull);
        ComboBoxListable<T> comboBox = new ComboBoxListable<>(lm, renderer);
        comboBox.setToolTipText(tooltip);

        return comboBox;
    }
}
