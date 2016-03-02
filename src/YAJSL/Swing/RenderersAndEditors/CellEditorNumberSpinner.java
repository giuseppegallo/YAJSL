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
package YAJSL.Swing.RenderersAndEditors;

import YAJSL.Swing.Beans.NumberSpinner;
import YAJSL.Swing.Beans.TableDataRecord;
import YAJSL.Swing.MousePointerManager;
import YAJSL.Utils.ExtendedProperties;
import YAJSL.Utils.Localizer;
import java.awt.Color;
import java.awt.Font;
import java.util.Properties;
import javax.swing.JComponent;


/**
 * An editor for editing numbers using a spinner.
 * <p>
 * The following properties can be used:
 * <ul>
 * <li>{@code allowNull} - true if the editor needs to allow null values; true is the default</li>
 * <li>{@code min} - the minimum value allowed; by default there is no limit</li>
 * <li>{@code max} - the maximum value allowed; by default there is no limit</li>
 * <li>{@code increment} - the increment between two consecutive values; 1 by default</li>
 * <li>{@code decimalDigits} - the number of decimal digits shown by the editor; 2 is the default</li>
 * <li>{@code fontSize} - the relative size of the font to be used for the editor; by default the font is not changed</li>
 * </ul>
 * 
 * @author Giuseppe Gallo
 */
public class CellEditorNumberSpinner extends TableDataRecord.Editor {

    /** The component actually as an editor */
    protected NumberSpinner comp = new NumberSpinner();
    
    
    /**
     * Allocates the editor using the given properties.
     * 
     * @param prop  the properties to be used by the editor
     */
    public CellEditorNumberSpinner(Properties prop) {
        super(prop);
        
        comp.setOpaque(true);
        comp.setValue(null);
        comp.setEnabled(true);
        comp.setBorder(null);
        
        Boolean allowNull = ExtendedProperties.getBooleanPropertySilent(prop, "allowNull", true);
        if (allowNull != null) comp.setNullAllowed(allowNull);

        Double min = ExtendedProperties.getDoublePropertySilent(prop, "min", Double.NEGATIVE_INFINITY);
        if (min != null) comp.setMinimumValue(min);

        Double max = ExtendedProperties.getDoublePropertySilent(prop, "max", Double.POSITIVE_INFINITY);
        if (max != null) comp.setMaximumValue(max);

        Double increment = ExtendedProperties.getDoublePropertySilent(prop, "increment", 1d);
        if (increment != null) comp.setIncrement(increment);

        Integer decimalDigits = ExtendedProperties.getIntegerPropertySilent(prop, "decimalDigits", 2);
        if (decimalDigits != null) comp.setDecimalDigits(decimalDigits);
        
        int tmpSize = ExtendedProperties.getIntegerPropertySilent(prop, "fontSize", -1);
        if (tmpSize > 0) {
            comp.setFont(comp.getFont().deriveFont((float)tmpSize));
        }
        
        comp.addActionListener(e -> { stopCellEditing(); });
    }
    
    @Override
    protected JComponent getEditorComponent() {
        return comp;
    }

    @Override
    protected void setEditorForeground(Color color) {
        comp.setForeground(color);
    }

    @Override
    protected void setEditorBackground(Color color) {
        comp.setBackground(color);
    }

    @Override
    protected void setEditorFont(Font font) {
        comp.setFont(font);
    }

    @Override
    protected void updateLocalizedAttributes(Localizer loc, String prefix) {
        comp.setLocale(loc.getLocale());
    }

    @Override
    public Object getCellEditorValue() {
        return comp.getValue();
    }

    @Override
    protected void setEditorValue(Object value) {
        Double val =
                (value instanceof Double) ? (Double)value :
                (value instanceof Integer) ? Double.valueOf((Integer)value) :
                (value instanceof Long) ? Double.valueOf((Long)value) :
                (value instanceof Short) ? Double.valueOf((Short)value) :
                (value instanceof Byte) ? Double.valueOf((Byte)value) :
                (value instanceof Float) ? Double.valueOf((Float)value) :
                null;
        comp.setValue(val);
    }

    @Override
    protected void setMousePointerManager(MousePointerManager mpm) {
        mpm.add(comp);
    }
}
