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
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Represents a rendered for rendering a text and an icon
 * 
 * @author Giuseppe Gallo
 */
public class ListCellRendererListable extends JLabel implements ListCellRenderer {

    public ListCellRendererListable() {
        setOpaque(true);
    }
    
    /**
     * Returns a component that has been configured to display the specified value.
     * 
     * @param list  the JList we're painting
     * @param value  the value returned by list.getModel().getElementAt(index)
     * @param index  the cells index
     * @param isSelected  true if the specified cell was selected
     * @param cellHasFocus  true if the specified cell has the focus
     * 
     * @return  a component whose paint() method will render the specified value
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Icon icon = null;
        String text = null;
        
        if (value == null) {
            text = " ";
            
        } else if (value instanceof Listable) {
            icon = ((Listable)value).getListableIcon();
            text = ((Listable)value).getListableText();
        
        } else if (value instanceof String) {
            text = (String)value;
        }
        
        if (isSelected) {
            setOpaque(true);
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setOpaque(false);
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setFont(list.getFont());
        setIcon(icon);
        setText(text);
        
        return this;
    }
}
