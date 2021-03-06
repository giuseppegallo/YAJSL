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
import YAJSL.Utils.Localizer;
import java.awt.Color;
import java.awt.Font;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;


/**
 * A renderer for showing icons.
 * 
 * @author Giuseppe Gallo
 */
public class CellRendererIcon extends TableDataRecord.Renderer {

    /** The component actually as a renderer */
    protected JLabel comp = new JLabel();
    
    /**
     * Allocates the renderer using the given properties.
     * 
     * @param prop  the properties to be used by the renderer
     */
    public CellRendererIcon(Properties prop) {
        super(prop);
        comp.setOpaque(true);
        comp.setIconTextGap(0);
    }
    
    @Override
    protected JComponent getRendererComponent() {
        return comp;
    }

    @Override
    protected void setRendererValue(Object value) {
        Icon icon =
                (value == null) ? null :
                (value instanceof Icon) ? (Icon)value :
                (value instanceof Listable) ? ((Listable)value).getListableIcon() :
                null;
        
        comp.setIcon(icon);
    }

    @Override
    protected void setRendererEditable(boolean editable) {
        // Nothing to do
    }

    @Override
    protected void setRendererForeground(Color color) {
        // Nothing to do
    }

    @Override
    protected void setRendererBackground(Color color) {
        comp.setBackground(color);
    }

    @Override
    protected void setRendererFont(Font font) {
        // Nothing to do
    }

    @Override
    protected void updateLocalizedAttributes(Localizer loc, String prefix) {
        // Nothing to do
    }
}
