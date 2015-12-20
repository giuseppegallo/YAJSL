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
 * A renderer for numbers.
 * <p>
 * The following properties can be used:
 * <li>{@code allowNull} - true if the renderer needs to allow null values; true is the default</li>
 * <li>{@code groupDigits} - true if the renderer needs to use digit grouping; false is the default</li>
 * <li>{@code min} - the minimum value allowed; by default there is no limit</li>
 * <li>{@code max} - the maximum value allowed; by default there is no limit</li>
 * <li>{@code decimalDigits} - the number of decimal digits shown by the renderer; 0 is the default</li>
 * <li>{@code fontSize} - the relative size of the font to be used for the renderer; by default the font is not changed</li>
 * <li>{@code positiveForeground} - the foreground color to be used for positive (and zero) values; by default the color is not changed</li>
 * <li>{@code negativeForeground} - the foreground color to be used for negative values; by default the color is not changed</li>
 * 
 * @author Giuseppe Gallo
 */
public class CellRendererNumberSpinner extends TableDataRecord.Renderer {

    /** The component actually as a renderer */
    protected NumberSpinner comp = new NumberSpinner();
    
    /** The foreground color to be used when the value of the cell is negative */
    protected Color negativeForeground = null;
    
    /** The foreground color to be used when the value of the cell is positive */
    protected Color positiveForeground = null;
    
    
    /**
     * Allocates the editor using the given properties.
     * 
     * @param prop  the properties to be used by the editor
     */
    public CellRendererNumberSpinner(Properties prop) {
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

        Integer decimalDigits = ExtendedProperties.getIntegerPropertySilent(prop, "decimalDigits", 0);
        if (decimalDigits != null) comp.setDecimalDigits(decimalDigits);
        
        int tmpSize = ExtendedProperties.getIntegerPropertySilent(prop, "fontSize", -1);
        if (tmpSize > 0) {
            comp.setFont(comp.getFont().deriveFont((float)tmpSize));
        }

        Boolean group = ExtendedProperties.getBooleanPropertySilent(prop, "groupDigits", false);
        if (group != null) comp.setGroupingUsed(group);
        
        Color color = ExtendedProperties.getColorPropertySilent(prop, "positiveForeground", null);
        if (color != null) positiveForeground = color;
        
        color = ExtendedProperties.getColorPropertySilent(prop, "negativeForeground", null);
        if (color != null) negativeForeground = color;
    }
    
    @Override
    protected JComponent getRendererComponent() {
        return comp;
    }

    @Override
    protected void setRendererValue(Object value) {
        Double number = 
                (value instanceof Long) ? Double.valueOf((Long)value) : 
                (value instanceof Integer) ? Double.valueOf((Integer)value) : 
                (value instanceof Short) ? Double.valueOf((Short)value) : 
                (value instanceof Byte) ? Double.valueOf((Byte)value) : 
                (value instanceof Float) ? Double.valueOf((Float)value) : 
                (Double)value;
        
        comp.setValue(number);
    }

    @Override
    protected void setRendererEditable(boolean editable) {
        // Nothing to do
    }

    @Override
    protected void setRendererForeground(Color color) {
        Double value = comp.getValue();
        if (value == null) value = 0d;
        
        if (positiveForeground != null && value >= 0) color = positiveForeground;
        else if (negativeForeground != null && value < 0) color = negativeForeground;
        
        comp.setForeground(color);
    }

    @Override
    protected void setRendererBackground(Color color) {
        comp.setBackground(color);
    }

    @Override
    protected void setRendererFont(Font font) {
        comp.setFont(font);
    }

    @Override
    protected void updateLocalizedAttributes(Localizer loc, String prefix) {
        comp.setLocale(loc.getLocale());
    }

    @Override
    protected void setMousePointerManager(MousePointerManager mpm) {
        mpm.add(comp);
    }
}
