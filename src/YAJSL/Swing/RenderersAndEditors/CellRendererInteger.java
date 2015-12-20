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

import java.util.Properties;

/**
 * A renderer for integer numbers.
 * <p>
 * The following properties can be used:
 * <li>{@code allowNull} - true if the renderer needs to allow null values; true is the default</li>
 * <li>{@code groupDigits} - true if the renderer needs to use digit grouping; false is the default</li>
 * <li>{@code min} - the minimum value allowed; by default there is no limit</li>
 * <li>{@code max} - the maximum value allowed; by default there is no limit</li>
 * <li>{@code fontSize} - the relative size of the font to be used for the renderer; by default the font is not changed</li>
 * <li>{@code positiveForeground} - the foreground color to be used for positive (and zero) values; by default the color is not changed</li>
 * <li>{@code negativeForeground} - the foreground color to be used for negative values; by default the color is not changed</li>
 * 
 * @author Giuseppe Gallo
 */
public class CellRendererInteger extends CellRendererNumber {
    
    /**
     * Allocates the editor using the given properties.
     * 
     * @param prop  the properties to be used by the editor
     */
    public CellRendererInteger(Properties prop) {
        super(prop);
        
        comp.setDecimalDigits(0);
    }
}
