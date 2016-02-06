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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A text field which can only accept numbers.
 *
 * @author Giuseppe Gallo
 */
public class NumberField extends JTextField {

    /**
     * The key listener used for the number fields
     */
    private class NumberKeyListener implements KeyListener {

        /**
         * Reacts to the keyTyped event.
         * If the character typed is not allowed, the event is consumed immediately and thus ignored.
         *
         * @param e  the event fired
         */
        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (Character.isISOControl(c) || Character.isDigit(c)) return;

            int selStart = getSelectionStart();
            if (c == '-' && allowNegative && selStart == 0) return;

            int selEnd = getSelectionEnd();
            String text = getText();
            int decSepPos = text.substring(selStart, selEnd).indexOf(decimalSeparator);
            boolean decSepExists = text.indexOf(decimalSeparator) >= 0;
            if (c == decimalSeparator &&
                decimalDigits > 0 &&
                ((decSepPos >= 0 && selStart > 0) || !decSepExists)) return;

            e.consume();
            beep();
        }

        /**
         * Reacts to the keyPressed event.
         * Nothing is done.
         *
         * @param e  the event fired
         */
        @Override
        public void keyPressed(KeyEvent e) { /* Nothing to do */ }

        /**
         * Reacts to the keyReleased event.
         * Nothing is done.
         *
         * @param e  the event fired
         */
        @Override
        public void keyReleased(KeyEvent e) { /* Nothing to do */ }
    }

    /**
     * The document listener used for the number fields
     */
    private class NumberDocumentListener implements DocumentListener {

        /**
         * Reacts to text addition.
         *
         * @param e  the event fired
         */
        @Override
        public void insertUpdate(DocumentEvent e) {
            updated(e);
        }

        /**
         * Reacts to text removal.
         *
         * @param e  the event fired
         */
        @Override
        public void removeUpdate(DocumentEvent e) {
            updated(e);
        }

        /**
         * Reacts to a property change.
         * Nothing is done.
         *
         * @param e  the event fired
         */
        @Override
        public void changedUpdate(DocumentEvent e) { /* Nothing to do */ }

        /**
         * Reacts to the text update.
         *
         * @param e  the event fired
         */
        private void updated(DocumentEvent e) {
            String oldText = getText();

            try {
                value = (nullAllowed && oldText.isEmpty()) ? null : format.parse(oldText).doubleValue();
                if (value != null && value < minimumValue) value = minimumValue;
                if (value != null && value > maximumValue) value = maximumValue;
            } catch (ParseException ex) {
                // Nothing to do
            }
        }
    }

    /**
     * The focus listener used for the number fields
     */
    private class NumberFocusListener implements FocusListener {

        /**
         * Reacts to the focus gained event.
         *
         * @param e  the event fired
         */
        @Override
        public void focusGained(FocusEvent e) {
            updateText();
        }

        /**
         * Reacts to the focus lost event.
         *
         * @param e  the event fired
         */
        @Override
        public void focusLost(FocusEvent e) {
            updateText();
        }

        /**
         * Updates the text based on the current value.
         */
        private void updateText() {
            String oldText = getText();
            String newText = (value == null) ? "" : format.format(value);
            if (!newText.equals(oldText)) {
                setText(newText);
            }
        }
    }

    /**
     * The action listener used for the number fields
     */
    private class NumberActionListener implements ActionListener {

        /**
         * Reacts to an action performed event.
         *
         * @param e  the event fired
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            updateText();
        }

        /**
         * Updates the text based on the current value.
         */
        private void updateText() {
            String oldText = getText();
            String newText = (value == null) ? "" : format.format(value);
            if (!newText.equals(oldText)) {
                setText(newText);
            }
        }
    }

    /** If true, null (empty text) is allowed */
    protected boolean nullAllowed = true;

    /** The numerical value stored in this number field */
    protected Double value = (nullAllowed) ? null : 0D;

    /** The minimum allowed value */
    protected double minimumValue = Double.NEGATIVE_INFINITY;

    /** The maximum allowed value */
    protected double maximumValue = Double.POSITIVE_INFINITY;
    
    /** The number of decimal digits to be shown */
    protected int decimalDigits = 0;

    /** The number format used by this number field */
    protected NumberFormat format;

    /** The toolkit to be used for emitting a beep */
    protected static final Toolkit toolkit = Toolkit.getDefaultToolkit();

    /** The decimal separator character */
    protected char decimalSeparator;

    /** True if negative values are allowed */
    protected boolean allowNegative = minimumValue < 0D;


    /**
     * Emits a beep.
     */
    private static void beep() {
        toolkit.beep();
    }

    /**
     * Allocates a new NumberField.
     */
    public NumberField() {
        super();
        configureFormatter();
        setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        setValueText(value);
        addKeyListener(new NumberKeyListener());
        getDocument().addDocumentListener(new NumberDocumentListener());
        addFocusListener(new NumberFocusListener());
        addActionListener(new NumberActionListener());
    }

    /**
     * Configures the text formatter and gets the character to be used as decimal separator.
     */
    private void configureFormatter() {
        format = NumberFormat.getNumberInstance(getLocale());

        // Determine the decimal separator character
        format.setMinimumFractionDigits(1);
        format.setMaximumFractionDigits(1);
        decimalSeparator = format.format(0D).charAt(1);

        // Configure the text formatter
        format.setMinimumFractionDigits(decimalDigits);
        format.setMaximumFractionDigits(decimalDigits);
        format.setGroupingUsed(false);
    }

    /**
     * Sets the locale to be used for this number field.
     *
     * @param locale  the locale to be used for this number field
     */
    @Override
    public void setLocale(Locale locale) {
        super.setLocale(locale);
        configureFormatter();
        setValueText(value);
    }

    /**
     * Returns the numerical value currently stored in this number field.
     *
     * @return  the numerical value currently stored in this number field
     */
    public Double getValue() {
        return value;
    }

    /**
     * Sets the current numerical value for this number field.
     *
     * @param value  the numerical value to be set for this number field
     */
    public void setValue(Double value) {
        if (value != null && value < minimumValue) value = minimumValue;
        if (value != null && value > maximumValue) value = maximumValue;
        this.value = value;
        setValueText(value);
    }

    /**
     * Returns the number of decimal digits to be shown.
     *
     * @return  the number of decimal digits to be shown
     */
    public int getDecimalDigits() {
        return decimalDigits;
    }

    /**
     * Sets the number of decimal digits to be shown.
     *
     * @param decimalDigits  the number of decimal digits to be shown
     */
    public void setDecimalDigits(int decimalDigits) {
        if (decimalDigits < 0) decimalDigits = 0;
        this.decimalDigits = decimalDigits;

        format.setMinimumFractionDigits(decimalDigits);
        format.setMaximumFractionDigits(decimalDigits);

        setValueText(value);
    }

    /**
     * Returns the minimum allowed value for this number field.
     *
     * @return  the minimum allowed value for this number field
     */
    public double getMinimumValue() {
        return minimumValue;
    }

    /**
     * Sets the minimum allowed value for this number field.
     *
     * @param minimumValue  the minimum allowed value for this number field
     */
    public void setMinimumValue(double minimumValue) {
        if (maximumValue < minimumValue) maximumValue = minimumValue;
        this.minimumValue = minimumValue;
        allowNegative = minimumValue < 0D;
        setValue(value);
    }

    /**
     * Returns the maximum allowed value for this number field.
     *
     * @return  the maximum allowed value for this number field
     */
    public double getMaximumValue() {
        return maximumValue;
    }

    /**
     * Sets the maximum allowed value for this number field.
     *
     * @param maximumValue  the maximum allowed value for this number field
     */
    public void setMaximumValue(double maximumValue) {
        if (minimumValue > maximumValue) minimumValue = maximumValue;
        this.maximumValue = maximumValue;
        setValue(value);
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
        if (this.nullAllowed && !nullAllowed && value == null) {
            setValue(0D);
        }
        this.nullAllowed = nullAllowed;
    }

    /**
     * Sets the edit box text according to the value and the correct formatting.
     *
     * @param value  the value to be used
     */
    private void setValueText(Double value) {
        setText((value == null) ? "" : format.format(value));
    }
    
    /**
     * Returns whether or not grouping is used to show the number.
     * 
     * @return  true if grouping is used to show the number
     */
    public boolean isGroupingUsed() {
        return format.isGroupingUsed();
    }
    
    /**
     * Sets whether or not grouping will be used to show the number.
     * 
     * @param group  if true, digits will be grouped
     */
    public void setGroupingUsed(boolean group) {
        format.setGroupingUsed(group);
    }
}
