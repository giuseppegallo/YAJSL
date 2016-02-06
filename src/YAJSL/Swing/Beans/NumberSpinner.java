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

import YAJSL.Swing.MousePointerManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import javax.swing.AbstractSpinnerModel;
import javax.swing.JSpinner;

/**
 * A better spinner for numbers.
 * 
 * @author Giuseppe Gallo
 */
public class NumberSpinner extends JSpinner {

    /** The component actually used as an editor */
    protected NumberField editor = new NumberField();
    
    /** The size of each increment */
    protected double increment = 1;

    /** The buttons used for incrementing/decrementing the value */
    protected ArrayList<javax.swing.plaf.basic.BasicArrowButton> buttons = new ArrayList<>(2);

    /** The list of all registered action listeners */
    protected LinkedList<ActionListener> listeners = new LinkedList<>();
    
    
    /**
     * The actual model used by the spinner.
     */
    protected class Model extends AbstractSpinnerModel {

        @Override
        public Object getValue() {
            return editor.getValue();
        }

        @Override
        public void setValue(Object value) {
            Double val =
                    (value instanceof Double) ? (Double)value :
                    (value instanceof Integer) ? Double.valueOf((Integer)value) :
                    (value instanceof Long) ? Double.valueOf((Long)value) :
                    (value instanceof Short) ? Double.valueOf((Short)value) :
                    (value instanceof Byte) ? Double.valueOf((Byte)value) :
                    (value instanceof Float) ? Double.valueOf((Float)value) :
                    null;
                    
            editor.setValue(val);
            notifyListeners(null);
        }

        @Override
        public Object getNextValue() {
            Double value = editor.getValue();
            editor.setValue((value == null) ? 0 : value + increment);
            return editor.getValue();
        }

        @Override
        public Object getPreviousValue() {
            Double value = editor.getValue();
            editor.setValue((value == null) ? 0 : value - increment);
            return editor.getValue();
        }
    }
    
    /**
     * Allocates a new instance.
     */
    public NumberSpinner() {
        super();
        setEditor(editor);
        setModel(new Model());
        
        for (Component c : getComponents()) {
            if (c instanceof javax.swing.plaf.basic.BasicArrowButton) buttons.add((javax.swing.plaf.basic.BasicArrowButton)c);
        }
        
        editor.addActionListener((ActionEvent e) -> { notifyListeners(e); });
    }
    
    /**
     * Returns the size of each increment.
     * 
     * @return  the size of each increment
     */
    public double getIncrement() {
        return increment;
    }
    
    /**
     * Sets the size of each increment.
     * 
     * @param increment  the size of each increment
     */
    public void setIncrement(double increment) {
        this.increment = increment;
    }

    /**
     * Sets the locale to be used for this number field.
     *
     * @param locale  the locale to be used for this number field
     */
    @Override
    public void setLocale(Locale locale) {
        super.setLocale(locale);
        editor.setLocale(locale);
    }

    /**
     * Returns the numerical value currently stored in this number field.
     *
     * @return  the numerical value currently stored in this number field
     */
    @Override
    public Double getValue() {
        Object value = super.getValue();
        
        Double val =
                (value instanceof Double) ? (Double)value :
                (value instanceof Integer) ? Double.valueOf((Integer)value) :
                (value instanceof Long) ? Double.valueOf((Long)value) :
                (value instanceof Short) ? Double.valueOf((Short)value) :
                (value instanceof Byte) ? Double.valueOf((Byte)value) :
                (value instanceof Float) ? Double.valueOf((Float)value) :
                null;
        
        return val;
    }

    /**
     * Sets the current numerical value for this number field.
     *
     * @param value  the numerical value to be set for this number field
     */
    public void setValue(Double value) {
        super.setValue(value);
    }

    /**
     * Returns the number of decimal digits to be shown.
     *
     * @return  the number of decimal digits to be shown
     */
    public int getDecimalDigits() {
        return editor.getDecimalDigits();
    }

    /**
     * Sets the number of decimal digits to be shown.
     *
     * @param decimalDigits  the number of decimal digits to be shown
     */
    public void setDecimalDigits(int decimalDigits) {
        editor.setDecimalDigits(decimalDigits);
    }

    /**
     * Returns the minimum allowed value for this number field.
     *
     * @return  the minimum allowed value for this number field
     */
    public double getMinimumValue() {
        return editor.getMinimumValue();
    }

    /**
     * Sets the minimum allowed value for this number field.
     *
     * @param minimumValue  the minimum allowed value for this number field
     */
    public void setMinimumValue(double minimumValue) {
        editor.setMinimumValue(minimumValue);
    }

    /**
     * Returns the maximum allowed value for this number field.
     *
     * @return  the maximum allowed value for this number field
     */
    public double getMaximumValue() {
        return editor.getMaximumValue();
    }

    /**
     * Sets the maximum allowed value for this number field.
     *
     * @param maximumValue  the maximum allowed value for this number field
     */
    public void setMaximumValue(double maximumValue) {
        editor.setMaximumValue(maximumValue);
    }

    /**
     * Returns true if the null value (empty text) is allowed.
     * 
     * @return  true if the null value (empty text) is allowed
     */
    public boolean isNullAllowed() {
        return editor.isNullAllowed();
    }

    /**
     * If true, the null value (empty text) is allowed.
     *
     * @param nullAllowed  if true, the null value (empty text) is allowed
     */
    public void setNullAllowed(boolean nullAllowed) {
        editor.setNullAllowed(nullAllowed);
    }

    /**
     * Returns whether or not grouping is used to show the number.
     * 
     * @return  true if grouping is used to show the number
     */
    public boolean isGroupingUsed() {
        return editor.isGroupingUsed();
    }
    
    /**
     * Sets whether or not grouping will be used to show the number.
     * 
     * @param group  if true, digits will be grouped
     */
    public void setGroupingUsed(boolean group) {
        editor.setGroupingUsed(group);
    }
    
    @Override
    public Color getForeground() {
        return (editor == null) ? super.getForeground() : editor.getForeground();
    }
    
    @Override
    public void setForeground(Color color) {
        super.setForeground(color);
        if (editor != null) {
            editor.setForeground(color);
            editor.setCaretColor(color);
        }
    }
    
    @Override
    public Color getBackground() {
        return (editor == null) ? super.getBackground() : editor.getBackground();
    }
    
    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        if (editor != null) editor.setBackground(color);
    }

    /**
     * Sets the mouse pointer manager for the buttons of the spinner.
     * 
     * @param mpm  the mouse pointer manager
     */
    public void setMousePointerManager(MousePointerManager mpm) {
        buttons.forEach(b -> { mpm.add(b); });
    }

    @Override
    public Font getFont() {
        return (editor == null) ? super.getFont() : editor.getFont();
    }
    
    @Override
    public void setFont(Font font) {
        if (editor != null) editor.setFont(font);
    }
    
    /**
     * Adds an action listener.
     * 
     * @param l  the action listener
     */
    public void addActionListener(ActionListener l) {
        listeners.add(l);
    }
    
    /**
     * Removes an action listener.
     * 
     * @param l  the action listener
     */
    public void removeActionListener(ActionListener l) {
        listeners.remove(l);
    }
    
    /**
     * Notifies all action listeners.
     * 
     * @param e  the event containing the details about the action
     */
    public final void notifyListeners(ActionEvent e) {
        listeners.forEach(l -> { l.actionPerformed(e); });
    }
}
