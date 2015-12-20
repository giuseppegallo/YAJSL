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
import YAJSL.Data.DataRecordSortedListManager;
import YAJSL.Swing.Application;
import YAJSL.Swing.Components.ErrorDialog;
import static YAJSL.Swing.Components.ErrorDialog.SUFFIX_ERROR_DESCR;
import static YAJSL.Swing.Components.ErrorDialog.SUFFIX_ERROR_TITLE;
import YAJSL.Swing.MousePointerManager;
import YAJSL.Utils.ColorUtils;
import YAJSL.Utils.ExtendedProperties;
import YAJSL.Utils.Localizer;
import YAJSL.Utils.Localizer.LocaleChangeListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultRowSorter;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import static javax.swing.JTable.AUTO_RESIZE_OFF;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import YAJSL.Data.DataRecord;
import YAJSL.Data.DataRecordManager;


/**
 * Represents a table for handling DataRecord objects.
 * 
 * @param <T>  the type of objects handled by this table
 * @author Giuseppe Gallo
 */
public class TableDataRecord<T extends DataRecord> extends JTable implements DataListListener, LocaleChangeListener {

    /**
     * The interface to be implemented by any class used for storing the state of a table.
     */
    public static interface TableState {
        /**
         * Returns the state of the column with the given name (null of not found).
         * 
         * @param name  the name of the column to be returned
         * @return  the state of the column with the given name (null of not found)
         */
        public ColumnState get(String name);

        /**
         * Saves the state of a column.
         * 
         * @param name  the name of the column
         * @param state  the state of the column
         */
        public void set(String name, ColumnState state);
    
    }
    
    /**
     * The interface to be implemented by any listener for mouse clicks in a table.
     */
    public static interface TableClickListener {

        /**
         * Notifies the listening class that a button has been clicked in a table.
         * 
         * @param table  the table
         * @param object  the underlying object on which the mouse was clicked
         * @param column  the internal name of the column on which the mouse was clicked
         * @param button  the mouse button clicked
         * @param count  the click count
         */
        public void mouseClickedInTable(TableDataRecord table, DataRecord object, String column, int button, int count);
    }
    
    /**
     * A class for storing JTable column states.
     * 
     * @author Giuseppe Gallo
     */
    public static class ColumnState  {

        /** The order (relative position) of the column */
        private int order = 0;

        /** The width of the window */
        private int width = 0;


        /**
         * Allocates a ColumnState.
         * 
         * @param order  the order (relative position) of the column
         * @param width  the width of the column
         */
        public ColumnState(int order, int width) {
            this.order = order;
            this.width = width;
        }
        
        /**
         * Returns the order (relative position) of the column.
         * 
         * @return  the order (relative position) of the column
         */
        public int getOrder() {
            return order;
        }

        /**
         * Sets the order (relative position) of the column.
         *
         * @param order  the order (relative position) of the column
         */
        public void setOrder(int order) {
            this.order = order;
        }

        /**
         * Returns the width of the column.
         * 
         * @return  the width of the column
         */
        public int getWidth() {
            return width;
        }

        /**
         * Sets the width of the column.
         *
         * @param width  the width of the column
         */
        public void setWidth(int width) {
            this.width = width;
        }
    }
    
    /**
     * The exception thrown when the column name is duplicated.
     */
    public static class DuplicatedColumnException extends Exception {
        /**
         * Allocates a DuplicatedColumnException.
         * 
         * @param column  the name of the duplicated column
         */
        public DuplicatedColumnException(String column) {
            super("Duplicated column [" + column +"]");
        }
    }
    
    
    /**
     * The exception thrown when an object cannot be allocated.
     */
    public static class AllocationException extends Exception {
        /**
         * Allocates a generic AllocationException.
         * 
         * @param className  the name of the class that cannot be allocated
         */
        public AllocationException(String className) {
            super("Unable to instantiate or find class [" + className +"]");
        }

        /**
         * Allocates an AllocationException in case the class is not valid.
         * 
         * @param className  the name of the class that cannot be allocated
         * @param parent  the required parent class
         */
        public AllocationException(String className, Class parent) {
            super("Class [" + className +"] must be a subclass of [" + parent.getName() + "]");
        }
    }
    

    /**
     * The interface to be implemented by any class listening for changes
 to the selected object in a TableDataRecord table.
     */
    public static interface SelectionListener {
        /**
         * Notifies a selection change.
         * 
         * @param sel  the object currently selected (null = none)
         * @param source  the table for which the selection changed
         */
        public void selectionChanged(DataRecord sel, TableDataRecord source);
    }
    
    /**
     * Represents the common class for any table cell renderer for this type of table.
     */
    public static abstract class Renderer implements TableCellRenderer {
        
        /** The thikness of the border for the renderer */
        public final static String PROP_BORDER_THICKNESS = "border.thickness";
        
        /** The thickness of the border for the renderer */
        protected final int borderThickness;
        
        
        /**
         * Allocates a new renderer.
         * 
         * @param properties  the properties for this renderer
         */
        protected Renderer(Properties properties) {
            borderThickness = ExtendedProperties.getIntegerPropertySilent(properties, PROP_BORDER_THICKNESS, 2);
        }
        
        /**
         * Allocates a new renderer.
         * 
         * @param properties  the properties to be used for allocating the renderer
         * @param dataType  the type of data handled by the renderer
         * @return  the renderer instance allocated
         * 
         * @throws ExtendedProperties.MissingPropertyException  in case the property specifying the class name is missing
         * @throws AllocationException  in case of any other issue
         */
        public static Renderer allocate(Properties properties, Class dataType) throws AllocationException, ExtendedProperties.MissingPropertyException {
            if (properties == null) properties = new Properties();
            
            String className = properties.getProperty(Column.PROP_CLASS);
            
            if (className == null) {
                if (dataType == String.class) className = "YAJSL.Swing.RenderersAndEditors.CellRendererString";
                else if (dataType == Boolean.class || dataType == boolean.class) className = "YAJSL.Swing.RenderersAndEditors.CellRendererBoolean";
                else if (
                        dataType == Long.class || dataType == long.class ||
                        dataType == Integer.class || dataType == int.class ||
                        dataType == Short.class || dataType == short.class ||
                        dataType == Byte.class || dataType == byte.class
                        ) className = "YAJSL.Swing.RenderersAndEditors.CellRendererInteger";
                else if (
                        dataType == Float.class || dataType == float.class ||
                        dataType == Double.class || dataType == double.class
                        ) className = "YAJSL.Swing.RenderersAndEditors.CellRendererNumber";
                else if (Color.class.isAssignableFrom(dataType)) className = "YAJSL.Swing.RenderersAndEditors.CellRendererColor";
                else if (Icon.class.isAssignableFrom(dataType)) className = "YAJSL.Swing.RenderersAndEditors.CellRendererIcon";
                else if (Listable.class.isAssignableFrom(dataType)) className = "YAJSL.Swing.RenderersAndEditors.CellRendererListable";

                if (className != null) properties.setProperty(Column.PROP_CLASS, className);
                
            } else if (className.isEmpty()) {
                properties.clear();
            }
            
            return (properties.isEmpty()) ? null : (Renderer) allocateFromString(properties, Renderer.class);
        }

        @Override
        public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (!(table instanceof TableDataRecord)) return getRendererComponent();
            
            TableDataRecord tab = (TableDataRecord)table;
            DataRecordManager dm = tab.getModel().dm;
            int index = table.convertRowIndexToModel(row);
            Column col = tab.getModel().getColumn(table.convertColumnIndexToModel(column));
            boolean isOdd = (row % 2 != 0);
            
            try {
                updateBackground(tab, dm, index, col, isOdd, isSelected);
                updateForeground(tab, dm, index, col, isSelected);
                updateBorderColor(dm, index, col);
                updateFont(dm, index, col);
                
            } catch (Exception ex) {
                Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            setRendererValue(value);
            
            return getRendererComponent();
        }
        
        /**
         * Updates the background color of the renderer.
         * 
         * @param table  the table in which this renderer is used
         * @param dm  the data manager used by the table
         * @param index  the actual index of the underlying DataRecord object
         * @param col  the information about the column
         * @param isOdd  true if the index of the row shown is odd (in the table)
         * @param isSelected  true if the row is selected
         * 
         * @throw Exception  in case of any issue
         */
        private void updateBackground(TableDataRecord table, DataRecordManager dm, int index, Column col, boolean isOdd, boolean isSelected) throws Exception {
            Color color = null;
            
            if (col.methodBackground != null) color = (Color)dm.getField(index, col.methodBackground);
            if (color == null) color = (isOdd) ? col.backgroundOdd : col.backgroundEven;
            if (color == null) color = table.getBackground();
            
            boolean editable = col.isCellEditable(dm, index);
            if (!editable && col.nonEditableShading > 0) color = ColorUtils.applyShading(color, col.nonEditableShading);
            
            if (isSelected) color = ColorUtils.blend(table.selectionBackground, color, 0.2f);
            
            setRendererEditable(editable);
            setRendererBackground(color);
        }
        
        /**
         * Updates the foreground color of the renderer.
         * 
         * @param table  the table in which this renderer is used
         * @param dm  the data manager used by the table
         * @param index  the actual index of the underlying DataRecord object
         * @param col  the information about the column
         * @param isSelected  true if the row is selected
         * 
         * @throw Exception  in case of any issue
         */
        private void updateForeground(TableDataRecord table, DataRecordManager dm, int index, Column col, boolean isSelected) throws Exception {
            Color color = null;
            
            if (isSelected) {
                color = table.selectionForeground;
            } else {
                if (col.methodForeground != null) color = (Color)dm.getField(index, col.methodForeground);
                if (color == null) color = table.getForeground();
            }
            
            setRendererForeground(color);
        }
        
        /**
         * Updates the color of the border of the renderer.
         * 
         * @param dm  the data manager used by the table
         * @param index  the actual index of the underlying DataRecord object
         * @param col  the information about the column
         * 
         * @throw Exception  in case of any issue
         */
        private void updateBorderColor(DataRecordManager dm, int index, Column col) throws Exception {
            Color color = null;
            
            if (col.mandatory) {
                Object value = dm.getField(index, col.methodGet);
                if (value == null || ((value instanceof String) && ((String)value).isEmpty())) {
                    color = col.borderMissing;
                }
            }
            
            if (col.methodValidate != null) {
                boolean valid = (boolean)dm.getField(index, col.methodValidate);
                if (!valid) color = col.borderInvalid;
            }
            
            if (color == null) color = (col.methodBorder == null) ? null : (Color)dm.getField(index, col.methodBorder);
            
            setRendererBorderColor(color);
        }
        
        /**
         * Updates the font of the renderer.
         * 
         * @param dm  the data manager used by the table
         * @param index  the actual index of the underlying DataRecord object
         * @param col  the information about the column
         * 
         * @throw Exception  in case of any issue
         */
        private void updateFont(DataRecordManager dm, int index, Column col) throws Exception {
            Font font = (col.methodFont == null) ? null : (Font)dm.getField(index, col.methodFont);
            if (font != null) setRendererFont(font);
        }
        
        /**
         * Returns the height of the component used by the renderer (0 if not relevant).
         * The default implementation returns 0.
         * 
         * @return  the height of the component used by the renderer (0 if not relevant)
         */
        protected int getRendererHeight() {
            return 0;
        }
        
        /**
         * Sets the color of the border of the component.
         * 
         * @param color  the color of the border of the component
         */
        protected void setRendererBorderColor(Color color) {
            if (borderThickness <= 0) return;

            JComponent comp = getRendererComponent();
            
            if (color == null) {
                comp.setBorder(null);
                return;
            }

            Border border = javax.swing.BorderFactory.createLineBorder(color, borderThickness);
            comp.setBorder(border);
        }

        /**
         * Sets the mouse pointer manager for the renderer.
         * Nothing is done by default.
         * 
         * @param mpm  the mouse pointer manager
         */
        protected void setMousePointerManager(MousePointerManager mpm) {
            // Nothing to do
        }
        
        /**
         * Returns the component used by this renderer.
         * 
         * @return  the component used by this renderer
         */
        protected abstract JComponent getRendererComponent();
        
        /**
         * Sets the value to be shown by the renderer.
         * 
         * @param value  the value to be shown by the renderer
         */
        protected abstract void setRendererValue(Object value);
        
        /**
         * Adjusts the component attributes depending on the cell being editable or not.
         * 
         * @param editable  true if the cell is editable
         */
        protected abstract void setRendererEditable(boolean editable);
        
        /**
         * Sets the foreground color of the component.
         * 
         * @param color  the foreground color
         */
        protected abstract void setRendererForeground(Color color);
        
        /**
         * Sets the background color of the component.
         * 
         * @param color  the background color
         */
        protected abstract void setRendererBackground(Color color);
        
        /**
         * Sets the font of the component.
         * 
         * @param font  the font
         */
        protected abstract void setRendererFont(Font font);
        
        /**
         * Updates anything which is localized.
         * 
         * @param loc  the localizer to be used
         * @param prefix  the prefix to be used for identifying the properties
         */
        protected abstract void updateLocalizedAttributes(Localizer loc, String prefix);
    }
    
    
    /**
     * Represents the common class for any table cell editor for this type of table.
     */
    public static abstract class Editor extends AbstractCellEditor implements TableCellEditor {
        
        /** The thikness of the border for the editor */
        public final static String PROP_BORDER_THICKNESS = "border.thickness";
        
        /** The thickness of the border for the editor */
        protected final int borderThickness;
        
        
        /**
         * Allocates a new editor.
         * 
         * @param properties  the properties for this editor
         */
        protected Editor(Properties properties) {
            borderThickness = ExtendedProperties.getIntegerPropertySilent(properties, PROP_BORDER_THICKNESS, 0);
        }
        
        /**
         * Allocates a new editor.
         * 
         * @param properties  the properties to be used for allocating the editor
         * @param dataType  the type of data handled by the editor
         * @return  the editor instance allocated
         * 
         * @throws ExtendedProperties.MissingPropertyException  in case the property specifying the class name is missing
         * @throws AllocationException  in case of any other issue
         */
        public static Editor allocate(Properties properties, Class dataType) throws AllocationException, ExtendedProperties.MissingPropertyException {
            if (properties == null) properties = new Properties();
            
            String className = properties.getProperty(Column.PROP_CLASS);
            
            if (className == null) {
                if (dataType == String.class) className = null;
                else if (dataType == Boolean.class || dataType == boolean.class) className = "YAJSL.Swing.RenderersAndEditors.CellEditorBoolean";
                else if (
                        dataType == Long.class || dataType == long.class ||
                        dataType == Integer.class || dataType == int.class ||
                        dataType == Short.class || dataType == short.class ||
                        dataType == Byte.class || dataType == byte.class
                        ) className = "YAJSL.Swing.RenderersAndEditors.CellEditorInteger";
                else if (
                        dataType == Float.class || dataType == float.class ||
                        dataType == Double.class || dataType == double.class
                        ) className = "YAJSL.Swing.RenderersAndEditors.CellEditorNumber";
                else if (DataRecord.class.isAssignableFrom(dataType)) className = "YAJSL.Swing.RenderersAndEditors.CellEditorComboBoxDataRecord";
                else if (Listable.class.isAssignableFrom(dataType)) className = "YAJSL.Swing.RenderersAndEditors.CellEditorComboBoxListable";

                if (className != null) properties.setProperty(Column.PROP_CLASS, className);
                
            } else if (className.isEmpty()) {
                properties.clear();
            }
            
            return (properties.isEmpty()) ? null : (Editor) allocateFromString(properties, Editor.class);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (!(table instanceof TableDataRecord)) return getEditorComponent();
            
            TableDataRecord tab = (TableDataRecord)table;
            DataRecordManager dm = tab.getModel().dm;
            int index = table.convertRowIndexToModel(row);
            Column col = tab.getModel().getColumn(table.convertColumnIndexToModel(column));
            boolean isOdd = (row % 2 != 0);
            
            try {
                updateBackground(tab, dm, index, col, isOdd, isSelected);
                updateForeground(tab, dm, index, col, isSelected);
                updateBorderColor(dm, index, col);
                updateFont(dm, index, col);
                
            } catch (Exception ex) {
                Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            setEditorValue(value);
            
            return getEditorComponent();
        }

        /**
         * Updates the background color of the editor.
         * 
         * @param table  the table in which this editor is used
         * @param dm  the data manager used by the table
         * @param index  the actual index of the underlying DataRecord object
         * @param col  the information about the column
         * @param isOdd  true if the index of the row shown is odd (in the table)
         * @param isSelected  true if the row is selected
         * 
         * @throw Exception  in case of any issue
         */
        private void updateBackground(TableDataRecord table, DataRecordManager dm, int index, Column col, boolean isOdd, boolean isSelected) throws Exception {
            Color color = null;
            
            if (col.methodBackground != null) color = (Color)dm.getField(index, col.methodBackground);
            if (color == null) color = (isOdd) ? col.backgroundOdd : col.backgroundEven;
            if (color == null) color = table.getBackground();
            
            if (isSelected) color = ColorUtils.blend(table.selectionBackground, color);
            
            setEditorBackground(color);
        }
        
        /**
         * Updates the foreground color of the editor.
         * 
         * @param table  the table in which this editor is used
         * @param dm  the data manager used by the table
         * @param index  the actual index of the underlying DataRecord object
         * @param col  the information about the column
         * @param isSelected  true if the row is selected
         * 
         * @throw Exception  in case of any issue
         */
        private void updateForeground(TableDataRecord table, DataRecordManager dm, int index, Column col, boolean isSelected) throws Exception {
            Color color = null;
            
            if (isSelected) {
                color = table.selectionForeground;
            } else {
                if (col.methodForeground != null) color = (Color)dm.getField(index, col.methodForeground);
                if (color == null) color = table.getForeground();
            }
            
            setEditorForeground(color);
        }
        
        /**
         * Updates the color of the border of the editor.
         * 
         * @param dm  the data manager used by the table
         * @param index  the actual index of the underlying DataRecord object
         * @param col  the information about the column
         * 
         * @throw Exception  in case of any issue
         */
        private void updateBorderColor(DataRecordManager dm, int index, Column col) throws Exception {
            Color color = (col.methodBorder == null) ? null : (Color)dm.getField(index, col.methodBorder);
            if (color != null) setEditorBorderColor(color);
        }
        
        /**
         * Updates the font of the editor.
         * 
         * @param dm  the data manager used by the table
         * @param index  the actual index of the underlying DataRecord object
         * @param col  the information about the column
         * 
         * @throw Exception  in case of any issue
         */
        private void updateFont(DataRecordManager dm, int index, Column col) throws Exception {
            Font font = (col.methodFont == null) ? null : (Font)dm.getField(index, col.methodFont);
            if (font != null) setEditorFont(font);
        }
        
        /**
         * Sets the color of the border of the component.
         * 
         * @param color  the color of the border of the component
         */
        protected void setEditorBorderColor(Color color) {
            if (borderThickness <= 0) return;

            JComponent comp = getEditorComponent();
            
            if (color == null) {
                comp.setBorder(null);
                return;
            }

            Border border = javax.swing.BorderFactory.createLineBorder(color, borderThickness);
            comp.setBorder(border);
        }
        
        /**
         * Sets the data manager for the editor.
         * Nothing is done by default.
         * 
         * @param dm  the data manager
         */
        protected void setDataManager(DataRecordSortedListManager<? extends DataRecord> dm) {
            // Nothing to do
        }
        
        /**
         * Sets the data for the editor.
         * Nothing is done by default.
         * 
         * @param data  the data for the editor
         */
        protected void setDataList(Collection<? extends Listable> data) {
            // Nothing to do
        }
        
        /**
         * Sets the mouse pointer manager for the editor.
         * Nothing is done by default.
         * 
         * @param mpm  the mouse pointer manager
         */
        protected void setMousePointerManager(MousePointerManager mpm) {
            // Nothing to do
        }
        
        /**
         * Sets the value to be shown by the editor.
         * 
         * @param value  the value to be shown by the editor
         */
        protected abstract void setEditorValue(Object value);
        
        /**
         * Returns the component used by this editor.
         * 
         * @return  the component used by this editor
         */
        protected abstract JComponent getEditorComponent();
        
        /**
         * Sets the foreground color of the component.
         * 
         * @param color  the foreground color
         */
        protected abstract void setEditorForeground(Color color);
        
        /**
         * Sets the background color of the component.
         * 
         * @param color  the background color
         */
        protected abstract void setEditorBackground(Color color);
        
        /**
         * Sets the font of the component.
         * 
         * @param font  the font
         */
        protected abstract void setEditorFont(Font font);
        
        /**
         * Updates anything which is localized.
         * 
         * @param loc  the localizer to be used
         * @param prefix  the prefix to be used for identifying the properties
         */
        protected abstract void updateLocalizedAttributes(Localizer loc, String prefix);
    }
    
    
    /**
     * Represents a column for the table.
     */
    public static class Column {
        
        /** The property defining the attributes of the renderer for the columns */
        public static final String PROP_RENDERER = "renderer.";
        
        /** The property defining the attributes of the editor for the columns */
        public static final String PROP_EDITOR = "editor.";
        
        /** The property defining the class of various objects */
        public static final String PROP_CLASS = "class";
        
        /** The property defining the prefix for localizing the columns */
        public static final String PROP_LOCALIZATION_PREFIX = "localizationPrefix";
        
        /** The property defining the unique name of the columns */
        public static final String PROP_NAME = "name";
        
        /** The property defining the preferred width of the columns */
        public static final String PROP_WIDTH = "width";
        
        /** The property defining the data type of the columns */
        public static final String PROP_DATATYPE = "dataType";
        
        /** The property defining if the contents of the column can be edited */
        public static final String PROP_EDITABLE = "editable";
        
        /** The property defining if the column can be sorted */
        public static final String PROP_SORTABLE = "sortable";
        
        /** The property defining if the column contains mandatory data */
        public static final String PROP_MANDATORY = "mandatory";
        
        /** The property defining the color of the border shown in case of invalid data */
        public static final String PROP_BORDER_INVALID = "border.invalid";
        
        /** The property defining the color of the border shown in case of missing mandatory data */
        public static final String PROP_BORDER_MISSING = "border.missing";
        
        /** The property defining the background color for odd rows */
        public static final String PROP_BACKGROUND_ODD = "background.odd";
        
        /** The property defining the background color for even rows */
        public static final String PROP_BACKGROUND_EVEN = "background.even";
        
        /** The property defining the shading for non-editable cells */
        public static final String PROP_SHADING_NONEDITABLE = "shading.nonEditable";
        
        /** The property defining the name of the getter method */
        public static final String PROP_METHOD_GET = "method.get";
        
        /** The property defining the name of the setter method */
        public static final String PROP_METHOD_SET = "method.set";
        
        /** The property defining the name of the method for determining if a cell is editable */
        public static final String PROP_METHOD_EDITABLE = "method.editable";
        
        /** The property defining the name of the method for determining the foreground color of a cell */
        public static final String PROP_METHOD_FOREGROUND = "method.foreground";
        
        /** The property defining the name of the method for determining the background color of a cell */
        public static final String PROP_METHOD_BACKGROUND = "method.background";
        
        /** The property defining the name of the method for determining the color of the border of a cell */
        public static final String PROP_METHOD_BORDER = "method.border";
        
        /** The property defining the name of the method for determining the font of a cell */
        public static final String PROP_METHOD_FONT = "method.font";
        
        /** The property defining the name of the method for validating the value of a cell */
        public static final String PROP_METHOD_VALIDATE = "method.validate";
        
        /** The property defining the header alignment for the columns */
        public static final String PROP_HEADER_ALIGNMENT = "header.alignment";
        
        /** The property for getting the localized title for the columns */
        public static final String PROP_HEADER_TITLE = "header.title";
        
        /** The property for getting the localized tooltip for the columns */
        public static final String PROP_HEADER_HINT = "header.hint";
        

        /** The map of all columns organized by name */
        private static final HashMap<String, Column> columns = new HashMap<>();
        
        
        /** The cell editor to be used for this column */
        private final Editor editor;

        /** The cell renderer to be used for this column */
        private final Renderer renderer;
        
        /** The name of the column */
        private final String name;
        
        /** The type of data in this column */
        private final Class dataType;
        
        /** True if the contents of the column can be edited */
        private final boolean editable;
        
        /** True if the column can be sorted */
        private final boolean sortable;
        
        /** True if the contents of the column are mandatory */
        private final boolean mandatory;
        
        /** The border color shown for missing mandatory data */
        private final Color borderMissing;
        
        /** The border color shown for invalid data */
        private final Color borderInvalid;
        
        /** The background color for odd rows */
        private final Color backgroundOdd;
        
        /** The background color for even rows */
        private final Color backgroundEven;
        
        /** The shading for non-editable cells */
        private final int nonEditableShading;
        
        /** The name of the getter method */
        private final String methodGet;
        
        /** The name of the setter method */
        private final String methodSet;
        
        /** The name of the method for determining if a cell is editable */
        private final String methodEditable;
        
        /** The name of the method for determining the foreground color of a cell */
        private final String methodForeground;
        
        /** The name of the method for determining the background color of a cell */
        private final String methodBackground;
        
        /** The name of the method for determining the color of the border of a cell */
        private final String methodBorder;
        
        /** The name of the method for determining the font of a cell */
        private final String methodFont;
        
        /** The name of the method for validating the value of a cell */
        private final String methodValidate;
        
        /** The preferred width of the column */
        private final Integer width;
        
        /** The prefix for localizing the column */
        private final String localizationPrefix;
        
        /** The localizer to be used for this column */
        private final Localizer loc;
      
        /** The tooltip for this column */
        private String tooltip;
        
        /** The label for this column */
        private String label;
        
        /** The alignment of the text in the header of this column */
        public int headerAlignment;
        
        
        /**
         * Allocates a new Column.
         * 
         * @param loc  the localizer to be used for this column
         * @param colProperties  the properties to be used for allocating the column (already filtered)
         * @param tabProperties  the common properties for the table
         * 
         * @throws ExtendedProperties.MissingPropertyException  in case a required property is missing
         * @throws ExtendedProperties.InvalidPropertyValueException  in case the value of a property is invalid
         * @throws DuplicatedColumnException  in case the column name is duplicated
         * @throws AllocationException  in case of any issue allocating the renderer or editor
         */
        private Column(Localizer loc, Properties colProperties, Properties tabProperties) throws
                ExtendedProperties.MissingPropertyException, ExtendedProperties.InvalidPropertyValueException,
                DuplicatedColumnException, AllocationException {
            
            this.loc = loc;

            name = ExtendedProperties.getRequiredStringProperty(colProperties, PROP_NAME);
            if (columns.containsKey(name)) throw new DuplicatedColumnException(name);
            
            String typeName = colProperties.getProperty(PROP_DATATYPE);
            try {
                dataType =
                        (typeName == null) ? String.class :
                        (typeName.equals("int")) ? int.class :
                        (typeName.equals("double")) ? double.class :
                        (typeName.equals("boolean")) ? boolean.class :
                        (typeName.equals("long")) ? long.class :
                        (typeName.equals("float")) ? float.class :
                        (typeName.equals("short")) ? short.class :
                        (typeName.equals("byte")) ? byte.class :
                        (typeName.equals("char")) ? char.class :
                        Class.forName(typeName);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Column.class.getName()).log(Level.SEVERE, null, ex);
                throw new AllocationException(typeName);
            }
            
            localizationPrefix = ExtendedProperties.getRequiredStringProperty(colProperties, PROP_LOCALIZATION_PREFIX);
            editable = ExtendedProperties.getBooleanPropertySilent(colProperties, PROP_EDITABLE, false);
            sortable = ExtendedProperties.getBooleanPropertySilent(colProperties, PROP_SORTABLE, true);
            mandatory = ExtendedProperties.getBooleanPropertySilent(colProperties, PROP_MANDATORY, false);
            width = ExtendedProperties.getIntegerPropertySilent(colProperties, PROP_WIDTH, null);
            
            int defaultHeaderAlignment = ExtendedProperties.getAlignmentPropertySilent(tabProperties, PROP_HEADER_ALIGNMENT, JLabel.LEADING);
            headerAlignment = ExtendedProperties.getAlignmentPropertySilent(colProperties, PROP_HEADER_ALIGNMENT, defaultHeaderAlignment);
            
            methodGet = ExtendedProperties.getRequiredStringProperty(colProperties, PROP_METHOD_GET);
            renderer = Renderer.allocate(ExtendedProperties.filter(colProperties, PROP_RENDERER), dataType);
            
            if (editable) {
                editor = Editor.allocate(ExtendedProperties.filter(colProperties, PROP_EDITOR), dataType);
                methodSet = ExtendedProperties.getRequiredStringProperty(colProperties, PROP_METHOD_SET);
                methodEditable = colProperties.getProperty(PROP_METHOD_EDITABLE);
            } else {
                editor = null;
                methodSet = null;
                methodEditable = null;
            }
            
            methodForeground = colProperties.getProperty(PROP_METHOD_FOREGROUND);
            methodBackground = colProperties.getProperty(PROP_METHOD_BACKGROUND);
            methodBorder = colProperties.getProperty(PROP_METHOD_BORDER);
            methodFont = colProperties.getProperty(PROP_METHOD_FONT);
            methodValidate = colProperties.getProperty(PROP_METHOD_VALIDATE);
            
            Color defaultBorder = ExtendedProperties.getColorPropertySilent(tabProperties, PROP_BORDER_MISSING, Color.RED);
            borderMissing = ExtendedProperties.getColorPropertySilent(colProperties, PROP_BORDER_MISSING, defaultBorder);
            
            defaultBorder = ExtendedProperties.getColorPropertySilent(tabProperties, PROP_BORDER_INVALID, Color.MAGENTA);
            borderInvalid = ExtendedProperties.getColorPropertySilent(colProperties, PROP_BORDER_INVALID, defaultBorder);
            
            Color defaultBackground = ExtendedProperties.getColorPropertySilent(tabProperties, PROP_BACKGROUND_ODD, null);
            backgroundOdd = ExtendedProperties.getColorPropertySilent(colProperties, PROP_BACKGROUND_ODD, defaultBackground);
            
            defaultBackground = ExtendedProperties.getColorPropertySilent(tabProperties, PROP_BACKGROUND_EVEN, null);
            backgroundEven = ExtendedProperties.getColorPropertySilent(colProperties, PROP_BACKGROUND_EVEN, defaultBackground);
            
            int defaultShading = ExtendedProperties.getIntegerPropertySilent(tabProperties, PROP_SHADING_NONEDITABLE, 0);
            nonEditableShading = ExtendedProperties.getIntegerPropertySilent(colProperties, PROP_SHADING_NONEDITABLE, defaultShading);
            
            updateLocalizedAttributes();
        }
        
        /**
         * Allocates a new Column.
         * 
         * @param loc  the localizer to be used for this column
         * @param colProperties  the properties to be used for allocating the column (already filtered)
         * @param tabProperties  the common properties for the table
         * @return  the column allocated
         * 
         * @throws ExtendedProperties.MissingPropertyException  in case a required property is missing
         * @throws ExtendedProperties.InvalidPropertyValueException  in case the value of a property is invalid
         * @throws DuplicatedColumnException  in case the column name is duplicated
         * @throws AllocationException  in case of any issue allocating the renderer or editor
         */
        public static Column allocate(Localizer loc, Properties colProperties, Properties tabProperties) throws
                ExtendedProperties.MissingPropertyException, ExtendedProperties.InvalidPropertyValueException,
                DuplicatedColumnException, AllocationException {
            
            Column col = new Column(loc, colProperties, tabProperties);
            columns.put(col.name, col);
            return col;
        }
        
        /**
         * Returns the column with the given name (null if not found).
         * 
         * @param name  the name of the column to be retrieved
         * @return  the column with the given name (null if not found)
         */
        public static Column find(String name) {
            return columns.get(name);
        }
        
        /**
         * Sets the tooltip for this column.
         * 
         * @param tooltip  the tooltip for this column
         */
        public void setTooltip(String tooltip) {
            this.tooltip = tooltip;
        }
        
        /**
         * Returns the tooltip for this column.
         * 
         * @return  the tooltip for this column
         */
        public String getTooltip() {
            return tooltip;
        }
        
        /**
         * Sets the label for this column.
         * 
         * @param label  the label for this column
         */
        public void setLabel(String label) {
            this.label = label;
        }
        
        /**
         * Returns the label for this column.
         * 
         * @return  the label for this column
         */
        public String getLabel() {
            return label;
        }
        
        /**
         * Returns true if the cell in this column at the given row can be edited.
         * 
         * @param dm  the data manager to be used
         * @param row  the row number
         * @return  true if the cell in this column at the given row can be edited
         * @throws Exception  in case of any issue
         */
        public boolean isCellEditable(DataRecordManager dm, int row) throws Exception {
            if (!editable || methodSet == null) return false;
            if (methodEditable == null) return true;
            
            Boolean edit = (Boolean)dm.getField(row, methodEditable);
            return (edit == null) ? false : edit;
        }

        /**
         * Returns the background color for the cell in this column at the given row.
         * 
         * @param dm  the data manager to be used
         * @param row  the row number
         * @param isOdd  true if the line is odd
         * @return  the background color for the cell in this column at the given row
         * @throws Exception  in case of any issue
         */
        public Color getBackground(DataRecordManager dm, int row, boolean isOdd) throws Exception {
            if (methodBackground != null) {
                Color color = (Color)dm.getField(row, methodBackground);
                if (color != null) return color;
            }
            
            return (isOdd) ? backgroundOdd : backgroundEven;
        }

        /**
         * Updates the localized attributes of the column.
         */
        public final void updateLocalizedAttributes() {
            label = loc.getText(localizationPrefix + PROP_HEADER_TITLE);
            tooltip = loc.getText(localizationPrefix + PROP_HEADER_HINT);
            
            if (renderer != null) renderer.updateLocalizedAttributes(loc, localizationPrefix);
            if (editable && editor != null) editor.updateLocalizedAttributes(loc, localizationPrefix);
        }
    }
    
    
    /**
     * The table model used for the table.
     * 
     * @param <K>  the type of DataRecord handled by this table model
     */
    public class Model<K extends T> extends javax.swing.table.AbstractTableModel {
        
        /** The property prefix identifying the columns in the table */
        public final static String PROP_COLUMN = "column.";

        /** The property identifying the unique prefix for all columns in the table */
        public final static String PROP_UNIQUE_PREFIX = "uniquePrefix";

        
        /** The data manager used by this table model */
        private final DataRecordManager<K> dm;

        /** The application  instance */
        private final Application app;
        
        /** The columns in the table */
        private ArrayList<Column> columns = null;
        
        
        /**
         * Allocates the table model.
         * 
         * @param dm  the data manager to be used for this table model
         * @param props  the properties to be used for initializing the columns
         * @param app  the application instance
         * @param store  the store in which the state of the column are saved
         */
        public Model(DataRecordManager<K> dm, Properties props, Application app, TableState store) {
            this.app = app;
            this.dm = dm;
            
            if (dm == null || props == null) return;
            
            Properties tabProp = new Properties();
            TreeMap<String, Properties> cols = new TreeMap<>();
            
            prepareProperties(props, store, tabProp, cols);
            
            columns = new ArrayList(cols.size());
            
            Localizer loc = app.getLocalizer();
            
            try {
                for (String col : cols.keySet()) {
                    columns.add(Column.allocate(loc, cols.get(col), tabProp));
                }
            } catch (ExtendedProperties.MissingPropertyException | ExtendedProperties.InvalidPropertyValueException |
                     DuplicatedColumnException | AllocationException ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                columns = null;
            }
        }
        
        /**
         * Splits the properties at table level and column level.
         * 
         * @param props  the cumulative set of properties
         * @param store  the store in which the state of the column are saved
         * @param tabProp  the properties at table level
         * @param cols  the properties at column level (for each column)
         */
        private void prepareProperties(Properties props, TableState store, Properties tabProp, TreeMap<String, Properties> cols) {
            String uniquePrefix;
            try {
                uniquePrefix = ExtendedProperties.getRequiredStringProperty(props, PROP_UNIQUE_PREFIX);
            } catch (ExtendedProperties.MissingPropertyException ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            
            HashMap<String, String> keys = new HashMap<>();
            HashMap<String, Integer> widths = new HashMap<>();
            
            for (String key: props.stringPropertyNames()) {
                String val = props.getProperty(key);
                
                if (!key.startsWith(PROP_COLUMN)) {
                    tabProp.setProperty(key, val);
                    continue;
                }
                
                String[] tokens = key.split("\\.", 4);
                if (tokens.length < 4) {
                    Logger.getLogger(Model.class.getName()).log(Level.SEVERE, "Invalid property name [{0}]", key);
                    continue;
                }
                
                String order = tokens[1];
                String name = tokens[2];
                String unique = uniquePrefix + "." + name;
                
                String col = keys.get(unique);
                if (col == null) {
                    String pos = "999999999";
                    if (store != null) {
                        ColumnState state = (ColumnState) store.get(unique);
                        if (state != null) {
                            int colPos = state.getOrder();
                            int width = state.getWidth();
                            pos = String.format("%09d", colPos);
                            
                            widths.put(unique, width);
                        }
                    }
                    col = pos + "." + order + "." + name;
                    keys.put(unique, col);
                }
                
                Properties p = cols.get(col);
                if (p == null) {
                    p = new Properties();
                    p.setProperty(Column.PROP_NAME, unique);
                    p.setProperty(Column.PROP_LOCALIZATION_PREFIX, unique + ".");
                    
                    Integer width = widths.get(unique);
                    if (width != null) p.setProperty(Column.PROP_WIDTH, Integer.toString(width));
                    cols.put(col, p);
                }
                
                String newKey = key.substring(PROP_COLUMN.length() + order.length() + name.length() + 2);
                if (!p.containsKey(newKey)) p.setProperty(newKey, val);
            }
        }
        
        @Override
        public int getRowCount() {
            return (dm == null) ? 0 : dm.size();
        }

        @Override
        public int getColumnCount() {
            return (columns == null) ? 0 : columns.size();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columns.get(columnIndex).dataType;
        }

        @Override
        public String getColumnName(int column) {
            return columns.get(column).label;
        }
        
        /**
         * Returns the information about the column with the given index.
         * 
         * @param column  the index of the column to be returned
         * @return  the information about the column with the given index
         */
        public Column getColumn(int column) {
            return columns.get(column);
        }

        /**
         * Returns the label of the given column.
         * 
         * @param column  the index of the column
         * @return  the label of the given column
         */
        public String getColumnLabel(int column) {
            return columns.get(column).label;
        }

        /**
         * Returns the tooltip for the given column.
         * 
         * @param column  the index of the column
         * @return  the tooltip for the given column
         */
        public String getColumnTooltip(int column) {
            return columns.get(column).tooltip;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            try {
                Column col = columns.get(columnIndex);
                return col.isCellEditable(dm, rowIndex);
            } catch (Exception ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                showErrorDialog(app, "TableDataRecord.error.isCellEditable", rowIndex, columnIndex, ex);
                return false;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            try {
                Column col = columns.get(columnIndex);
                return dm.getField(rowIndex, col.methodGet);
            } catch (Exception ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                showErrorDialog(app, "TableDataRecord.error.getValueAt", rowIndex, columnIndex, ex);
                return null;
            }
        }
        
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            try {
                Column col = columns.get(columnIndex);
                dm.setField(rowIndex, col.methodSet, col.dataType, value);
                fireTableRowsUpdated(rowIndex, rowIndex);
                // TODO: update the cell height?
            } catch (Exception ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                showErrorDialog(app, "TableDataRecord.error.setValueAt", rowIndex, columnIndex, ex);
            }
        }

        /**
         * Deletes the object with the given index.
         *
         * @param index  the index of the onject to be deleted
         * @throws Exception  in case of any problem when deleting the object
         */
        public void delete(int index) throws Exception {
            int size = dm.size();
            if (index < 0 || index >= size) return;
            
            dm.delete(index);
        }

        /**
         * Returns the object corresponding to the given row in the table (not in the model).
         * 
         * @param row  the index of the row in the table (not in the model)
         * @return  the object corresponding to the given row in the table (not in the model)
         */
        public K get(int row) {
            return dm.get(convertRowIndexToModel(row));
        }
        
        /**
         * Returns the horizontal aligment for the text in the header of the table for the given column.
         * 
         * @param  column  the index of the column for which the alignment needs to be returned
         * @return  the horizontal aligment for the text in the header of the table for the given column
         */
        public int getHeaderHorizontalAlignment(int column) {
            return columns.get(column).headerAlignment;
        }
    
        /**
         * Shows an error dialog related to an error occurring for a specific cell.
         * 
         * @param app  the application instance
         * @param prefix  the prefix to be used to retrieve the error title and description
         * @param rowIndex  the index of the row
         * @param columnIndex  the index of the column
         * @param ex  the exception
         */
        private void showErrorDialog(Application app, String prefix, int rowIndex, int columnIndex, Exception ex) {
            Localizer loc = app.getLocalizer();

            String title = loc.getText(prefix + SUFFIX_ERROR_TITLE).
                    replace("__COL__", Integer.toString(columnIndex)).
                    replace("__ROW__", Integer.toString(rowIndex));
            String descr = loc.getText(prefix + SUFFIX_ERROR_DESCR).
                    replace("__COL__", Integer.toString(columnIndex)).
                    replace("__ROW__", Integer.toString(rowIndex));

            ErrorDialog.showDialog(app, title, descr, ex);
        }

        /**
         * Updates the localized attributes of the columns.
         */
        public final void updateLocalizedAttributes() {
            int i = 0;
            TableColumnModel cmod = getTableHeader().getColumnModel();
            for (Column col : columns) {
                col.updateLocalizedAttributes();
                cmod.getColumn(convertColumnIndexToView(i)).setHeaderValue(col.label);
                ++i;
            }
        }
    }

    
    /**
     * A renderer which overrides the text alignment in the headed of the columns.
     */
    public static class HeaderRenderer implements TableCellRenderer {
        
        private final TableCellRenderer orig;
        
        public HeaderRenderer(TableCellRenderer orig) {
            this.orig = orig;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component comp = orig.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (comp instanceof JLabel) {
                ((JLabel)comp).setHorizontalAlignment(((TableDataRecord)table).getModel().getHeaderHorizontalAlignment(column));
            }
            return comp;
        }
    }
    
    /** An array of Class, internally used for reflection, containing the Properties class */
    private final static Class[] PROPERTIES_CLASS_ARRAY = { Properties.class };
    
    /** The property defining the default row height for the table */
    protected final String PROP_ROW_HEIGHT = "rowHeight";
    
    /** The mapping from column names to column index */
    protected final HashMap<String, Integer> colNameToIndex = new HashMap<>();
    
    /** The control panel used with this table */
    protected TableControlPanel controlPanel = null;

    /** The list of objects listening for changes to the selected object in the table */
    protected final LinkedList<SelectionListener> listeners = new LinkedList<>();
    
    /** The list of objects listening for mouse clicks in the table */
    protected final LinkedList<TableClickListenerInfo> clickListeners = new LinkedList<>();
    
    
    /**
     * Allocates an object from the class name and the properties.
     *
     * @param properties  the properties to be used for initializing the object
     * @param type  the type of class required
     * @return  the object allocated from the class name and the properties
     * @throws ExtendedProperties.MissingPropertyException  in case the property for the class is missing
     * @throws AllocationException  in case of any other issue
     */
    private static Object allocateFromString(Properties properties, Class type) throws AllocationException, ExtendedProperties.MissingPropertyException {
        String className = ExtendedProperties.getRequiredStringProperty(properties, Column.PROP_CLASS);
        
        try {
            Class cl = Class.forName(className);
            if (type != null && !type.isAssignableFrom(cl)) throw new AllocationException(className, type);
            return cl.getConstructor(PROPERTIES_CLASS_ARRAY).newInstance(new Object[]{ properties });
            
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                 NoSuchMethodException | SecurityException | ClassNotFoundException ex) {
            Logger.getLogger(TableDataRecord.class.getName()).log(Level.SEVERE, null, ex);
            throw new AllocationException(className);
        }
    }

    
    /**
     * Helper class for storing the information of the listeners for mouse clicks on the table.
     */
    private static class TableClickListenerInfo {
        
        /** The listening object */
        private final TableClickListener listener;
        
        /** The internal name of the column for which clicks have to be detected */
        private final String column;
        
        /** The mouse button for which clicks have to be detected */
        private final int button;
        
        /** The number of clicks to be detected */
        private final int clicks;
        
        
        /**
         * Adds a listener for changes to the selected object in the table.
         * 
         * @param listener  the listener to be added
         * @param column  the internal name of the column for which clicks have to be detected
         * @param button  the mouse button for which clicks have to be detected
         * @param clicks  the number of clicks to be detected
         */
        public TableClickListenerInfo(TableClickListener listener, String column, int button, int clicks) {
            this.listener = listener;
            this.column = column;
            this.button = button;
            this.clicks = clicks;
        }
        
        /**
         * Checks if the mouse click parameters match with the ones for this info.
         * 
         * @param column  the internal name of the column for which the click has been detected
         * @param button  the mouse button for which the click has been detected
         * @param clicks  the number of clicks detected
         * 
         * @return  true if the parameters detected match
         */
        public boolean check(String column, int button, int clicks) {
            return this.column.equals(column) && button == this.button && clicks == this.clicks;
        }
        
        /**
         * Notifies the listening object.
         * 
         * @param table  the table in which the mouse was clicked
         * @param object  the underlying object on which the mouse was clicked
         */
        public void notifyListener(TableDataRecord table, DataRecord object) {
            listener.mouseClickedInTable(table, object, column, button, button);
        }
    }

    /** The default comparator for long values */
    public static Comparator<Long> COMPARATOR_LONG = new Comparator<Long>() {
        @Override
        public int compare(Long o1, Long o2) {
            return Long.compare(o1, o2);
        }
    };
    
    /** The default comparator for int values */
    public static Comparator<Integer> COMPARATOR_INT = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return Integer.compare(o1, o2);
        }
    };
    
    /** The default comparator for short values */
    public static Comparator<Short> COMPARATOR_SHORT = new Comparator<Short>() {
        @Override
        public int compare(Short o1, Short o2) {
            return Short.compare(o1, o2);
        }
    };
    
    /** The default comparator for byte values */
    public static Comparator<Byte> COMPARATOR_BYTE = new Comparator<Byte>() {
        @Override
        public int compare(Byte o1, Byte o2) {
            return Byte.compare(o1, o2);
        }
    };
    
    /** The default comparator for char values */
    public static Comparator<Character> COMPARATOR_CHAR = new Comparator<Character>() {
        @Override
        public int compare(Character o1, Character o2) {
            return Character.compare(o1, o2);
        }
    };
    
    /** The default comparator for boolean values */
    public static Comparator<Boolean> COMPARATOR_BOOLEAN = new Comparator<Boolean>() {
        @Override
        public int compare(Boolean o1, Boolean o2) {
            return Boolean.compare(o1, o2);
        }
    };
    
    /** The default comparator for double values */
    public static Comparator<Double> COMPARATOR_DOUBLE = new Comparator<Double>() {
        @Override
        public int compare(Double o1, Double o2) {
            return Double.compare(o1, o2);
        }
    };
    
    /** The default comparator for float values */
    public static Comparator<Float> COMPARATOR_FLOAT = new Comparator<Float>() {
        @Override
        public int compare(Float o1, Float o2) {
            return Float.compare(o1, o2);
        }
    };
    
    /** The default comparator for comparable values */
    public static Comparator<Comparable> COMPARATOR_COMPARABLE = new Comparator<Comparable>() {
        @Override
        public int compare(Comparable o1, Comparable o2) {
            return (o1 == null && o2 == null) ? 0 : (o1 == null) ? -1 : (o2 == null) ? 1 : o1.compareTo(o2);
        }
    };
    
    
    /**
     * Allocates a new instance of the table.
     * This needs to be used only for preview purposes.
     */
    public TableDataRecord() {
        super(0, 0);
        setAutoCreateRowSorter(false);
        setModel(new Model<>(null, null, null, null));
        initHeader();
        initDefaults();
    }
    
    /**
     * Allocates a new instance of the table.
     * 
     * @param dm  the data manager for handling the data
     * @param props  the properties for initializing the table
     * @param app  the application instance
     * @param store  the store in which the state of the column are saved
     */
    public TableDataRecord(DataRecordManager<T> dm, Properties props, Application app, TableState store) {
        super(0, 0);
        setAutoCreateRowSorter(false);
        setModel(new Model<>(dm, props, app, store));
        initColumns();
        initHeader();
        initDefaults();
        initComparators();
        initDefaultRowHeight(props);
        initListeners();
    }
    
    /**
     * Allocates a new instance of the table.<p>
     * 
     * The following properties are expected (please ignore the double quotes):
     *<pre>
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_UNIQUE_PREFIX} = The unique prefix for all columns in the table [string]
     * &lt;prefix&gt;.{@value #PROP_ROW_HEIGHT} = (Optional) the default row height for the table [positive integer]
     * &lt;prefix&gt;.{@value TableDataRecord.Column.#PROP_HEADER_ALIGNMENT} = (Optional) The default header alignment ["leading" (default), "left", "center", "right", "trailing"]
     * &lt;prefix&gt;.{@value TableDataRecord.Column.#PROP_BORDER_MISSING} = (Optional) The default color of the border to be shown in case of missing mandatory data (red by default) [0xRRGGBB]
     * &lt;prefix&gt;.{@value TableDataRecord.Column.#PROP_BORDER_INVALID} = (Optional) The default color of the border to be shown in case of invalid data (magenta by default) [0xRRGGBB]
     * &lt;prefix&gt;.{@value TableDataRecord.Column.#PROP_BACKGROUND_ODD} = (Optional) The default background color for the odd rows [0xRRGGBB]
     * &lt;prefix&gt;.{@value TableDataRecord.Column.#PROP_BACKGROUND_EVEN} = (Optional) The default background color for the even rows [0xRRGGBB]
     * &lt;prefix&gt;.{@value TableDataRecord.Column.#PROP_SHADING_NONEDITABLE} = (Optional) The default shading factor for non-editable cells [positive integer]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_DATATYPE} = (Optional) The type of data handle by the column (assumed String by default) [string]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_EDITABLE} = (Optional) Defines if the column is editable (false by default) [boolean]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_SORTABLE} = (Optional) Defines if the column is sortable (true by default) [boolean]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_MANDATORY} = (Optional) Defines if the column contains mandatory data (false by default) [boolean]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_WIDTH} = (Optional) The preferred width of the column [positive integer]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_HEADER_ALIGNMENT} = (Optional) The header alignment ["leading" (default), "left", "center", "right", "trailing"]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_BORDER_MISSING} = (Optional) The color of the border to be shown in case of missing mandatory data (red by default) [0xRRGGBB]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_BORDER_INVALID} = (Optional) The color of the border to be shown in case of invalid data (magenta by default) [0xRRGGBB]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_BACKGROUND_ODD} = (Optional) The background color for the odd rows [0xRRGGBB]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_BACKGROUND_EVEN} = (Optional) The background color for the even rows [0xRRGGBB]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_SHADING_NONEDITABLE} = (Optional) The shading factor for non-editable cells [positive integer]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_METHOD_GET} = The name of the method for retrieving the value of the cell [string]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_METHOD_SET} = (Mandatory for editable cells) The name of the method for setting the value of the cell [string]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_METHOD_EDITABLE} = (Optional) The name of the method for determining if a cell is editable [string]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_METHOD_VALIDATE} = (Optional) The name of the method for determining if the value of a cell is valid [string]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_METHOD_FOREGROUND} = (Optional) The name of the method for determining the foreground color of a cell [string]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_METHOD_BACKGROUND} = (Optional) The name of the method for determining the background color of a cell [string]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_METHOD_BORDER} = (Optional) The name of the method for determining the color of the border of a cell [string]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_METHOD_FONT} = (Optional) The name of the method for determining the font of a cell [string]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_RENDERER}{@value TableDataRecord.Column.#PROP_CLASS} = (Mandatory if the default renderer is changed) The class name of the renderer to be used [string]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_RENDERER}{@value TableDataRecord.Renderer.#PROP_BORDER_THICKNESS} = (Optional) The thickness of the border (2 by default) [integer]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_RENDERER}* = any property needed by the renderer
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_EDITOR}{@value TableDataRecord.Column.#PROP_CLASS} = (Mandatory if the default editor is changed) The class name of the editor to be used [string]
     * &lt;prefix&gt;.{@value TableDataRecord.Model.#PROP_COLUMN}&lt;order&gt;.&lt;name&gt;.{@value TableDataRecord.Column.#PROP_EDITOR}* = any property needed by the editor
     *</pre>
     * <p>
     * The unique name of each column is determined by concatenating the unique prefix for the table and the {@code <name>} of the column (separated by ".").<p>
     * The localization property files need to contain the following properties for each column:
     *<pre>
     * &lt;unique_name&gt;.{@value TableDataRecord.Column.#PROP_HEADER_TITLE} = The localized title for the column
     * &lt;unique_name&gt;.{@value TableDataRecord.Column.#PROP_HEADER_HINT} = The localized tooltip for the column
     *</pre>
     * 
     * @param dm  the data manager for handling the data
     * @param props  the global properties
     * @param prefix  the prefix of the properties needed for the table ("." is automatically appended)
     * @param app  the application instance
     * @param store  the store in which the state of the column are saved
     * @return  the table allocated
     */
    public static TableDataRecord<? extends DataRecord> allocate(DataRecordManager<? extends DataRecord> dm, Properties props, String prefix, Application app, TableState store) {
        if (dm == null || props == null || app == null) {
            return new TableDataRecord<>();
        } else {
            Properties settings = ExtendedProperties.filter(props, "TableDataRecord.");
            Properties filtered = ExtendedProperties.filter(props, prefix + ".");
            ExtendedProperties.addProperties(settings, filtered);
            
            return new TableDataRecord<>(dm, settings, app, store);
        }
    }

    /**
     * Initializes the default attributes of the table.
     */
    private void initDefaults() {
        setAutoResizeMode(AUTO_RESIZE_OFF);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        Dimension size = new Dimension(100, 100);
        setPreferredScrollableViewportSize(size);
        
        setRowSelectionAllowed(true);
        setColumnSelectionAllowed(false);
        
        setRowSorter(new TableRowSorter<>(getModel()));
    }
    
    /**
     * Initializes the default comparators for the comlumns in the table.
     * NOTE: this is a workaround because assigning the class to the column doe not seem to work anymore.
     */
    private void initComparators() {
        Model mod = getModel();
        TableRowSorter sorter = (TableRowSorter)getRowSorter();
        
        for (int i = 0; i < mod.getColumnCount(); ++i) {
            Column data = mod.getColumn(i);
            
            if (!data.sortable) {
                sorter.setSortable(i, false);
                continue;
            }
            
            Comparator comp =
                    (data.dataType == long.class) ? COMPARATOR_LONG :
                    (data.dataType == int.class) ? COMPARATOR_INT :
                    (data.dataType == short.class) ? COMPARATOR_SHORT :
                    (data.dataType == byte.class) ? COMPARATOR_BYTE :
                    (data.dataType == char.class) ? COMPARATOR_CHAR :
                    (data.dataType == boolean.class) ? COMPARATOR_BOOLEAN :
                    (data.dataType == double.class) ? COMPARATOR_DOUBLE :
                    (data.dataType == float.class) ? COMPARATOR_FLOAT :
                    (data.dataType.isAssignableFrom(Comparable.class)) ? COMPARATOR_COMPARABLE :
                    null;
            
            if (comp != null) sorter.setComparator(i, comp);
        }
    }
    
    /**
     * Initializes the header of the table.
     */
    private void initHeader() {
        JTableHeader h = getTableHeader();
        h.setDefaultRenderer(new HeaderRenderer(h.getDefaultRenderer()));
    }
    
    /**
     * Initializes all columns.
     */
    private void initColumns() {
        Model mod = getModel();
        for (int i = 0; i < mod.getColumnCount(); ++i) {
            Column data = mod.getColumn(i);
            TableColumn col = getColumnModel().getColumn(i);
            
            colNameToIndex.put(data.name, i);

            if (data.renderer != null) col.setCellRenderer(data.renderer);
            if (data.editor != null) col.setCellEditor(data.editor);
            if (data.width != null) col.setPreferredWidth(data.width);
        }
    }
    
    /**
     * Initializes the default row height using the properties.
     * 
     * @param properties  the properties to be used for initializing the default row height
     */
    private void initDefaultRowHeight(Properties properties) {
        Integer height = ExtendedProperties.getIntegerPropertySilent(properties, PROP_ROW_HEIGHT, null);
        if (height != null) setRowHeight(height);
    }
    
    private void initListeners() {
        ListSelectionModel selm = getSelectionModel();
        
        selm.addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) return;
            notifySelectionChange();
        });
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableClicked(evt);
            }
        });        
    }
    
    /**
     * Creates and returns the default table header.
     * 
     * @return  returns the default table header
     */
    @Override
    protected JTableHeader createDefaultTableHeader() {
        JTableHeader h = new JTableHeader(columnModel) {
            @Override
            public String getToolTipText(MouseEvent e) {
                java.awt.Point p = e.getPoint();
                int index = columnModel.getColumnIndexAtX(p.x);
                if (index < 0) return null;
                int realIndex = columnModel.getColumn(index).getModelIndex();
                if (realIndex < 0) return null;
                
                return getModel().getColumnTooltip(realIndex);
            }
        };

        h.setFont(h.getFont().deriveFont(Font.BOLD));
        
        return h;
    }

    @Override
    public Model getModel() {
        TableModel mod = super.getModel();
        return (mod instanceof Model) ? (Model)mod : null;
    }
    
    /**
     * Enables or disables the table (and its header).
     * 
     * @param enabled  if true the table (and its header) will be enabled, if false the table (and its header) will be disabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (tableHeader != null) {
            tableHeader.setEnabled(enabled);
        }
    }

    /**
     * Returns the cell editor used for the column with the given index.
     *
     * @param index  the index of the column
     * @return  the cell editor used for the column with the given index
     */
    public TableCellEditor getEditorForColumn(int index) {
        return getColumnModel().getColumn(convertColumnIndexToView(index)).getCellEditor();
    }
    
    /**
     * Returns the cell renderer used for the column with the given index.
     *
     * @param index  the index of the column
     * @return  the cell renderer used for the column with the given index
     */
    public TableCellRenderer getRendererForColumn(int index) {
        return getColumnModel().getColumn(convertColumnIndexToView(index)).getCellRenderer();
    }
    
    /**
     * Returns the cell editor used for the column with the given index.
     *
     * @param column  the name of the column
     * @return  the cell editor used for the column with the given index
     */
    public TableCellEditor getEditorForColumn(String column) {
        Integer index = getColumnIndex(column);
        return (index == null) ? null : getColumnModel().getColumn(index).getCellEditor();
    }
    
    /**
     * Returns the cell renderer used for the column with the given index.
     *
     * @param column  the name of the column
     * @return  the cell renderer used for the column with the given index
     */
    public TableCellRenderer getRendererForColumn(String column) {
        Integer index = getColumnIndex(column);
        return (index == null) ? null : getColumnModel().getColumn(index).getCellRenderer();
    }
    
    /**
     * Sets the data manager for the editor used for the given column.
     * 
     * @param column  the name of the column
     * @param dm  the data manager
     */
    public void setDataManagerForColumn(String column, DataRecordSortedListManager<? extends DataRecord> dm) {
        TableCellEditor editor = getEditorForColumn(column);
        if (editor != null && (editor instanceof Editor)) ((Editor)editor).setDataManager(dm);
    }
    
    /**
     * Returns the index of the column with the given name (or null if not found).
     * 
     * @param column  the name of the column
     * @return  the index of the column with the given name (or null if not found)
     */
    public Integer getColumnIndex(String column) {
        return colNameToIndex.get(column);
    }
    
    /**
     * Stops cell editing.<p>
     * Nothing is done if no cell is being edited.
     */
    public void stopEditing() {
        if (getEditingRow() >= 0 && getEditingColumn() >= 0) {
            TableCellEditor editor = getEditorForColumn(getEditingColumn());
            if (editor != null) editor.stopCellEditing();
        }
    }
    
    /**
     * Cancels cell editing.<p>
     * Nothing is done if no cell is being edited.
     */
    public void cancelEditing() {
        if (getEditingRow() >= 0 && getEditingColumn() >= 0) {
            TableCellEditor editor = getEditorForColumn(getEditingColumn());
            if (editor != null) editor.cancelCellEditing();
        }
    }
    
    @Override
    public void localeChanged(Locale newLocale) {
        updateLocalizedAttributes();
    }
    
    /**
     * Updates the localized attributes of the table.
     */
    public final void updateLocalizedAttributes() {
        getModel().updateLocalizedAttributes();
        tableHeader.repaint();
    }

    /**
     * Adds a listener for changes to the selected object in the table.
     * 
     * @param l  the listener to be added
     */
    public final void addSelectionListener(SelectionListener l) {
        listeners.add(l);
    }
    
    /**
     * Removes a listener for changes to the selected object in the table.
     * 
     * @param l  the listener to be removed
     */
    public final void removeSelectionListener(SelectionListener l) {
        listeners.remove(l);
    }

    /**
     * Notifies to all listeners a change in the selected object.
     */
    private void notifySelectionChange() {
        listeners.stream().forEach((l) -> {
            l.selectionChanged(getSelectedObject(), this);
        });
    }

    /**
     * Adds a listener for changes to the selected object in the table.
     * 
     * @param l  the listener to be added
     * @param column  the internal name of the column for which clicks have to be detected
     * @param button  the mouse button for which clicks have to be detected
     * @param clicks  the number of clicks
     */
    public final void addTableClickListener(TableClickListener l, String column, int button, int clicks) {
        clickListeners.add(new TableClickListenerInfo(l, column, button, clicks));
    }
    
    /**
     * Rects to a mouse click in the table.
     * 
     * @param evt  the mouse event
     */
    public final void tableClicked(java.awt.event.MouseEvent evt) {

        if (evt.isConsumed()) return;
        
        Point p = evt.getPoint();
        T obj = getObjectAtPoint(p);
        String col = getNameAtPoint(p);
        
        int clicks = evt.getClickCount();
        int button = evt.getButton();
        
        if (obj == null || col == null) return;
        
        boolean consumed = false;
        
        for (TableClickListenerInfo i : clickListeners) {
            if (i.check(col, button, clicks)) {
                i.notifyListener(this, obj);
                consumed = true;
            }
        }
        
        if (consumed) evt.consume();
    }
    
    /**
     * Sets the control panel to be used with this table.
     *
     * @param controlPanel  the control panel to be used with this table
     */
    public void setControlPanel(TableControlPanel controlPanel) {
        if (this.controlPanel == controlPanel) return;

        if (this.controlPanel != null) {
            removeSelectionListener(this.controlPanel);
        }

        this.controlPanel = controlPanel;
        controlPanel.setTable(this);
    }

    /**
     * Returns the object currently selected (or null if nothing is selected).
     *
     * @return  the object currently selected (or null if nothing is selected)
     */
    public T getSelectedObject() {
        return getSelectedObject(true);
    }

    /**
     * Returns the object currently selected (or null if nothing is selected).
     *
     * @param stopEditing  if true, stops editing before returning the object selected
     * @return  the object currently selected (or null if nothing is selected)
     */
    public T getSelectedObject(boolean stopEditing) {
        if (stopEditing) stopEditing();
        int index = getSelectionModel().getMinSelectionIndex();
        if (index == -1) return null;

        return (T)getModel().get(index);
    }

    /**
     * Returns the underlying object for the given row.
     * 
     * @param row  the number of the row
     * @return  the underlying object for the given row
     */
    public T getObjectAtRow(int row) {
        return (row < 0) ? null : (T)getModel().get(row);
    }
    
    /**
     * Returns the underlying object for the row shown at the given point.
     * 
     * @param point  the point on the screen
     * @return  the underlying object for the row shown at the given point
     */
    public T getObjectAtPoint(Point point) {
        return getObjectAtRow(rowAtPoint(point));
    }

    /**
     * Returns the internal name of the column with the given index.
     * 
     * @param col  the index of the column
     * @return  the internal name of the column with the given index
     */
    public String getNameForColumn(int col) {
        if (col < 0) return null;
        
        Column c = (Column)getModel().columns.get(convertColumnIndexToModel(col));
        return (c == null) ? null : c.name;
    }

    /**
     * Returns the internal name of the column at the given point.
     * 
     * @param point  the point
     * @return  the internal name of the column at the given point
     */
    public String getNameAtPoint(Point point) {
        return getNameForColumn(columnAtPoint(point));
    }
    
    /**
     * Returns the object currently being edited (or null if nothing is being edited).
     *
     * @return  the object currently being edite (or null if nothing is being edited)
     */
    public T getEditingObject() {
        if (!isEditing()) return null;
        int index = getEditingRow();
        if (index == -1) return null;
        
        return (T)getModel().get(index);
    }
    
    /**
     * Selects the row with the given index.
     * 
     * @param row  the index of the row to be selected
     */
    public void setSelectedRow(int row) {
        if (row < 0) {
            clearSelection();
            return;
        }
        
        getSelectionModel().setSelectionInterval(row, row);
        scrollRectToVisible(getCellRect(row, 0, true));
    }
    
    /**
     * Selects the given object.<p>
     * Nothing is done if null is passed.
     * 
     * @param object  the object to be selected
     */
    public void setSelectedObject(T object) {
        if (object == null) {
            clearSelection();
            return;
        }
        
        int index = getModel().dm.getIndex(object);
        if (index == -1) {
            clearSelection();
            return;
        }

        RowSorter rs = getRowSorter();
        if (rs != null) {
            index = rs.convertRowIndexToView(index);
        }
        
        setSelectedRow(index);
    }
    
    /**
     * Deletes the object currently selected.
     *
     * @throws Exception  in case of any problem when deleting the object
     */
    public void deleteSelectedObject() throws Exception {
        stopEditing();
        int index = getSelectionModel().getMinSelectionIndex();
        if (index == -1) return;

        RowSorter rs = getRowSorter();
        if (rs != null) {
            index = rs.convertRowIndexToModel(index);
        }

        getModel().delete(index);
        updateRecordCount();
    }

    /**
     * Adds an object to the table and stores it.
     *
     * @param obj  the object to be added
     * @throws Exception  in case of any problem adding the object
     */
    public void addObject(T obj) throws Exception {
        stopEditing();
        
        Model mod = getModel();
        int index = mod.getRowCount();
        obj.store();
        mod.fireTableRowsInserted(index, index);

        RowSorter rs = getRowSorter();
        if (rs != null) {
            index = rs.convertRowIndexToView(index);
        }

        getSelectionModel().setSelectionInterval(index, index);
        scrollRectToVisible(getCellRect(index, 0, true));
        setEditingRow(index);
        setEditingColumn(0);
        updateRecordCount();
    }

    /**
     * Notifies the record index update to the control panel
     */
    public void updateRecordCount() {
        if (controlPanel != null) {
            controlPanel.recordCountChanged(getModel().getRowCount());
        }
    }

    /**
     * Sorts the rows of the table by column.
     * 
     * @param column  the index of the column to be used for sorting the table
     * @param order  the order to be used for sorting (ascending, descending or unsorted)
     */
    public void sortByColumn(int column, SortOrder order) {
        RowSorter rs = getRowSorter();
        if (rs == null || !(rs instanceof DefaultRowSorter)) return;
        
   	DefaultRowSorter sorter = (DefaultRowSorter)rs;
    	ArrayList list = new ArrayList();
    	list.add(new RowSorter.SortKey(column, order));
    	sorter.setSortKeys(list);
    	sorter.sort();        
    }
    
    /**
     * Sorts the rows of the table by column(s).
     * 
     * @param columns  the index of the columns to be used for sorting the table
     * @param order  the order to be used for sorting (ascending, descending or unsorted)
     */
    public void sortByColumns(int[] columns, SortOrder[] order) {
        RowSorter rs = getRowSorter();
        if (rs == null || !(rs instanceof DefaultRowSorter)) return;
        
   	DefaultRowSorter sorter = (DefaultRowSorter)rs;
    	ArrayList list = new ArrayList();
        for (int i = 0; i < columns.length; ++i) {
            list.add(new RowSorter.SortKey(columns[i], order[i]));
        }
    	sorter.setSortKeys(list);
    	sorter.sort();        
    }
    
    /**
     * Sorts the rows of the table by column.
     * 
     * @param column  the name of the column to be used for sorting the table
     * @param order  the order to be used for sorting (ascending, descending or unsorted)
     */
    public void sortByColumn(String column, SortOrder order) {
        Integer index = getColumnIndex(column);
        if (index == null) return;
        
        sortByColumn(index, order);
    }
    
    /**
     * Sorts the rows of the table by column(s).
     * 
     * @param columns  the name of the columns to be used for sorting the table
     * @param order  the order to be used for sorting (ascending, descending or unsorted)
     */
    public void sortByColumns(String[] columns, SortOrder[] order) {
        ArrayList<Integer> indexes = new ArrayList<>(columns.length);
        
        for (String col : columns) {
            Integer index = getColumnIndex(col);
            if (index == null) continue;
            
            indexes.add(index);
        }
        
        if (indexes.isEmpty()) return;
        int array[] = new int[indexes.size()];
        
        for (int i = 0; i < array.length; ++i) {
            array[i] = indexes.get(i);
        }
        
        sortByColumns(array, order);
    }

    /**
     * Sets the comparator for a column.
     * 
     * @param column  the index of the column
     * @param comparator  the comparator
     */
    public void setComparator(int column, Comparator comparator) {
        RowSorter rs = getRowSorter();
        if (rs == null || !(rs instanceof DefaultRowSorter)) return;

        DefaultRowSorter sorter = (DefaultRowSorter)rs;
        
        sorter.setComparator(column, comparator);
    }
    
    /**
     * Sets the comparator for a column.
     * 
     * @param column  the name of the column
     * @param comparator  the comparator
     */
    public void setComparator(String column, Comparator comparator) {
        Integer index = getColumnIndex(column);
        if (index == null) return;
        
        setComparator(index, comparator);
    }
    
    /**
     * Sets the comparator for several columns.
     * 
     * @param columns  the index of the columns
     * @param comparators  the comparators
     */
    public void setComparator(int[] columns, Comparator[] comparators) {
        RowSorter rs = getRowSorter();
        if (rs == null || !(rs instanceof DefaultRowSorter)) return;

        DefaultRowSorter sorter = (DefaultRowSorter)rs;
        
        for (int i = 0; i < columns.length; ++i) {
            sorter.setComparator(columns[i], comparators[i]);
        }
    }
    
    /**
     * Sets the comparator for several columns.
     * 
     * @param columns  the name of the columns
     * @param comparators  the comparators
     */
    public void setComparator(String[] columns, Comparator[] comparators) {
        RowSorter rs = getRowSorter();
        if (rs == null || !(rs instanceof DefaultRowSorter)) return;

        DefaultRowSorter sorter = (DefaultRowSorter)rs;
        
        for (int i = 0; i < columns.length; ++i) {
            String col = columns[i];
            Comparator comp = comparators[i];
            
            Integer index = getColumnIndex(col);
            if (index == null) continue;
            
            sorter.setComparator(index, comp);
        }
    }

    /**
     * Sets if a column is sortable.
     * 
     * @param column  the index of the column
     * @param sortable  the flag indicating if the column is sortable
     */
    public void setSortable(int column, boolean sortable) {
        RowSorter rs = getRowSorter();
        if (rs == null || !(rs instanceof DefaultRowSorter)) return;

        DefaultRowSorter sorter = (DefaultRowSorter)rs;
        
        sorter.setSortable(column, sortable);
    }
    
    /**
     * Sets if a column is sortable.
     * 
     * @param column  the name of the column
     * @param sortable  the flag indicating if the column is sortable
     */
    public void setSortable(String column, boolean sortable) {
        Integer index = getColumnIndex(column);
        if (index == null) return;
        
        setSortable(index, sortable);
    }
    
    /**
     * Sets if several columns are sortable.
     * 
     * @param columns  the index of the columns
     * @param sortable  the flag indicating if the columns are sortable
     */
    public void setSortable(int[] columns, boolean[] sortable) {
        RowSorter rs = getRowSorter();
        if (rs == null || !(rs instanceof DefaultRowSorter)) return;

        DefaultRowSorter sorter = (DefaultRowSorter)rs;
        
        for (int i = 0; i < columns.length; ++i) {
            sorter.setSortable(columns[i], sortable[i]);
        }
    }
    
    /**
     * Sets if several columns are sortable.
     * 
     * @param columns  the name of the columns
     * @param sortable  the flag indicating if the columns are sortable
     */
    public void setSortable(String[] columns, boolean[] sortable) {
        RowSorter rs = getRowSorter();
        if (rs == null || !(rs instanceof DefaultRowSorter)) return;

        DefaultRowSorter sorter = (DefaultRowSorter)rs;
        
        for (int i = 0; i < columns.length; ++i) {
            String col = columns[i];
            
            Integer index = getColumnIndex(col);
            if (index == null) continue;
            
            sorter.setSortable(index, sortable[i]);
        }
    }
    
    /**
     * Sets the mouse pointer manager for the table and all renderers and editors.
     * 
     * @param mpm  the mouse pointer manager
     */
    public void setMousePointerManager(MousePointerManager mpm) {
        ArrayList<Column> cols = getModel().columns;
        
        for (Column col : cols) {
            if (col.renderer != null) col.renderer.setMousePointerManager(mpm);
            if (col.editor != null) col.editor.setMousePointerManager(mpm);
        }
    }
    
    /**
     * Saves the state of the columns.
     * 
     * @param store  the store in which the state of the column have to be saved
     * @throws Exception  in case of any issue
     */
    public void saveState(TableState store) throws Exception {
        ArrayList<Column> cols = getModel().columns;
        
        int idx = 0;
        for (Column col : cols) {
            int pos = convertColumnIndexToView(idx);
            int width = getColumnModel().getColumn(pos).getWidth();
            store.set(col.name, new ColumnState(pos, width));
            ++idx;
        }
    }
    
    @Override
    public void listChanged(Class<?> type) {
        getModel().fireTableDataChanged();
        updateRecordCount();
    }

    @Override
    public void objectAdded(int index, Class<?> type, Object object) {
        getModel().fireTableRowsInserted(index, index);
        updateRecordCount();
    }

    @Override
    public void objectRemoved(int index, Class<?> type) {
        getModel().fireTableRowsDeleted(index, index);
        updateRecordCount();
    }

    @Override
    public void objectChanged(int index, Class<?> type, Object object) {
        getModel().fireTableRowsUpdated(index, index);
    }

    /**
     * Notifies the change of the object currently selected.
     *
     * @param type  the class of the object changed
     */
    public void selectedRowChanged(Class type) {
        int index = getSelectionModel().getMinSelectionIndex();
        if (index == -1) return;

        objectChanged(index, type, getModel().get(index));
    }
}
