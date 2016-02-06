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

import YAJSL.Data.DataListListener;
import YAJSL.Data.Listable;
import YAJSL.Swing.Beans.ComboBoxListable;
import YAJSL.Swing.Beans.TableDataRecord;
import YAJSL.Swing.MousePointerManager;
import YAJSL.Utils.ExtendedProperties;
import YAJSL.Utils.Localizer;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Properties;
import javax.swing.JComponent;

/**
 * An editor allowing to select Listable objects using a combo box.
 * <p>
 * The following properties can be used:
 * <li>{@code allowNull} - true if the editor needs to allow null values; true is the default</li>
 * 
 * @author Giuseppe Gallo
 */
public class CellEditorComboBoxListable extends TableDataRecord.Editor implements DataListListener {

    /** The component actually as an editor */
    protected ComboBoxListable comp = new ComboBoxListable();
    
    /**
     * Allocates the editor using the given properties.
     * 
     * @param prop  the properties to be used by the editor
     */
    public CellEditorComboBoxListable(Properties prop) {
        super(prop);
        
        Boolean allowNull = ExtendedProperties.getBooleanPropertySilent(prop, "allowNull", true);
        if (allowNull != null) comp.setNullAllowed(allowNull);

        comp.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // Nothing to do
            }

            @Override
            public void focusLost(FocusEvent e) {
                stopCellEditing();
            }
        });
        
        comp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_TAB || code == KeyEvent.VK_ENTER) {
                    stopCellEditing();
                } else if (code == KeyEvent.VK_ESCAPE) {
                    cancelCellEditing();
                }
            }
        });
        
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
        // Nothing to do
    }

    @Override
    public Object getCellEditorValue() {
        return comp.getSelectedObject();
    }

    @Override
    protected void setDataList(Collection<? extends Listable> data) {
        cancelCellEditing();
        comp.setElements(data);
    }
    
    @Override
    public void listChanged(Class<?> type) {
        cancelCellEditing();
    }

    @Override
    public void objectAdded(int index, Class<?> type, Object object) {
        cancelCellEditing();
    }

    @Override
    public void objectRemoved(int index, Class<?> type) {
        cancelCellEditing();
    }

    @Override
    public void objectChanged(int index, Class<?> type, Object object) {
        cancelCellEditing();
    }

    @Override
    protected void setEditorValue(Object value) {
        comp.setSelectedObject((value instanceof Listable) ? (Listable)value : null);
    }

    @Override
    protected void setMousePointerManager(MousePointerManager mpm) {
        mpm.add(comp);
    }
}
