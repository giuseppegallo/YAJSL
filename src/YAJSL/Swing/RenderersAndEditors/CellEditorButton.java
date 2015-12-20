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

import YAJSL.Swing.Beans.TableDataRecord;
import YAJSL.Swing.MousePointerManager;
import YAJSL.Utils.ExtendedProperties;
import YAJSL.Utils.Localizer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JComponent;

/**
 * An editor for using buttons.
 * <p>
 * The following properties can be used:
 * <li>{@code textSize} - (Optional) The size of the text in points [int]</li>
 * <li>{@code bold} - (Optional) True if the text needs to be shown in bold (false by default) [boolean]</li>
 * 
 * @author Giuseppe Gallo
 */
public class CellEditorButton extends TableDataRecord.Editor {

    /** The component actually used as an editor */
    protected JButton comp = new JButton();
    
    
    /**
     * Allocates the editor using the given properties.
     * 
     * @param prop  the properties to be used by the editor
     */
    public CellEditorButton(Properties prop) {
        super(prop);
        
        comp.setMargin(new Insets(1, 1, 1, 1));
        
        comp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopCellEditing();
            }
        });
        
        Integer textSize = ExtendedProperties.getIntegerPropertySilent(prop, "textSize", null);
        boolean bold = ExtendedProperties.getBooleanPropertySilent(prop, "bold", false);
        
        if (textSize != null) {
            comp.setFont(comp.getFont().deriveFont(textSize));            
        }
        
        if (bold) {
            comp.setFont(comp.getFont().deriveFont(comp.getFont().getStyle() | java.awt.Font.BOLD));
        }
    }
    
    @Override
    protected void setEditorValue(Object value) {
        // Nothing to do
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
        // Nothing to do
    }

    @Override
    protected void setEditorFont(Font font) {
        comp.setFont(font);
    }

    @Override
    protected void updateLocalizedAttributes(Localizer loc, String prefix) {
        comp.setText(loc.getText(prefix + "text"));
        comp.setToolTipText(loc.getText(prefix + "hint"));
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }
    
    @Override
    public void setMousePointerManager(MousePointerManager mpm) {
        mpm.add(comp);
    }
}
