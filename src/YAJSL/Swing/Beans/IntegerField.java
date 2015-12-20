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


/**
 * A text field which can only accept integers.
 *
 * @author Giuseppe Gallo
 */
public class IntegerField extends NumberField {

    /**
     * Allocates a new IntegerField.
     */
    public IntegerField() {
        super();
        decimalDigits = 0;
        maximumValue = (maximumValue < 0) ? Math.ceil(maximumValue) : Math.floor(maximumValue);
        minimumValue = (minimumValue < 0) ? Math.ceil(minimumValue) : Math.floor(minimumValue);
    }
    
    @Override
    public void setMaximumValue(double maximumValue) {
        throw new UnsupportedOperationException("Not allowed.");
    }

    @Override
    public void setMinimumValue(double minimumValue) {
        throw new UnsupportedOperationException("Not allowed.");
    }

    @Override
    public void setDecimalDigits(int decimalDigits) {
        throw new UnsupportedOperationException("Not allowed.");
    }

    @Override
    public void setValue(Double value) {
        throw new UnsupportedOperationException("Not allowed.");
    }

    @Override
    public Double getValue() {
        throw new UnsupportedOperationException("Not allowed.");
    }

    /**
     * Sets the current numerical value for this number field.
     *
     * @param value  the numerical value to be set for this number field
     */
    public void setValue(Integer value) {
        super.setValue(value.doubleValue());
    }

    /**
     * Sets the maximum allowed value for this number field.
     *
     * @param maximumValue  the maximum allowed value for this number field
     */
    public void setMaximumValue(int maximumValue) {
        super.setMaximumValue(maximumValue);
    }

    /**
     * Sets the minimum allowed value for this number field.
     *
     * @param minimumValue  the minimum allowed value for this number field
     */
    public void setMinimumValue(int minimumValue) {
        super.setMinimumValue(minimumValue);
    }

    /**
     * Returns the numerical (integer) value currently stored in this number field.
     *
     * @return  the numerical (integer) value currently stored in this number field
     */
    public Integer getInteger() {
        return (value == null) ? null : value.intValue();
    }
}
