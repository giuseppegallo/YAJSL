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

import YAJSL.Data.Listable;
import YAJSL.Swing.Beans.TableDataRecord;
import YAJSL.Utils.ExtendedProperties;
import YAJSL.Utils.Localizer;
import java.awt.Color;
import java.awt.Font;
import java.util.Properties;
import javax.swing.JComponent;
import javax.swing.JLabel;


/**
 * A renderer for showing strings.
 * <p>
 * The following properties can be used:
 * <ul>
 * <li>{@code alignmentH} - (Optional) The horizontal alignment [leading (default), left, center, right, trailing]</li>
 * </ul>
 * 
 * @author Giuseppe Gallo
 */
public class CellRendererString extends TableDataRecord.Renderer {

    /** The component actually used as a renderer */
    protected JLabel comp = new JLabel();
    
    /**
     * Allocates the renderer using the given properties.
     * 
     * @param prop  the properties to be used by the renderer
     */
    public CellRendererString(Properties prop) {
        super(prop);
        
        comp.setOpaque(true);
        comp.setIconTextGap(0);
        
        int alignment = ExtendedProperties.getAlignmentPropertySilent(prop, "alignmentH", JLabel.LEADING);
        comp.setHorizontalAlignment(alignment);
    }
    
    @Override
    protected JComponent getRendererComponent() {
        return comp;
    }

    @Override
    protected void setRendererValue(Object value) {
        String text =
                (value == null) ? "" :
                (value instanceof String) ? (String)value :
                (value instanceof Listable) ? ((Listable)value).getListableText() :
                value.toString();
        
        comp.setText(text);
    }

    @Override
    protected void setRendererEditable(boolean editable) {
        // Nothing to do
    }

    @Override
    protected void setRendererForeground(Color color) {
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
        // Nothing to do
    }
}
