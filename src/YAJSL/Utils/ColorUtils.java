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
package YAJSL.Utils;

import java.awt.Color;

/**
 * Collection of color-related utility functions.
 * 
 * @author Giuseppe Gallo
 */
public class ColorUtils {
    
    /**
     * Blends two colors mixing them in equal parts.
     * 
     * @param c1  the first color to be mixed
     * @param c2  the second color to be mixed
     * @return  the mixed color
     */
    public static Color blend(Color c1, Color c2) {
        return blend(c1, c2, 0.5f);
    }
    
    /**
     * Blends two colors mixing them according to the given factor.<br>
     * <ul>
     * <li>factor = 0 ==&gt; only the first color is used</li>
     * <li>factor = 1 ==&gt; only the second color is used</li>
     * </ul>
     * 
     * @param c1  the first color to be mixed
     * @param c2  the second color to be mixed
     * @param factor  the factor to be used for mixing colors<br>
     *                <ul>
     *                <li>0 ==&gt; only the first color is used</li>
     *                <li>1 ==&gt; only the second color is used</li>
     *                </ul>
     * @return  the mixed color
     */
    public static Color blend(Color c1, Color c2, float factor) {
        if (factor > 1f) factor = 1f;
        
        int r1 = c1.getRed();
        int g1 = c1.getGreen();
        int b1 = c1.getBlue();
        int r2 = c2.getRed();
        int g2 = c2.getGreen();
        int b2 = c2.getBlue();
        
        float ifactor = 1 - factor;
        
        int r = Math.round(r1*ifactor + r2*factor);
        int g = Math.round(g1*ifactor + g2*factor);
        int b = Math.round(b1*ifactor + b2*factor);
        
        if (r < 0) r = 0; else if (r > 255) r = 255;
        if (g < 0) g = 0; else if (g > 255) g = 255;
        if (b < 0) b = 0; else if (b > 255) b = 255;
        
        return new Color(r, g, b);
    }
    
    /**
     * Returns a lighter or darker version of a color.
     * 
     * @param c  the color to be changed
     * @param factor  the factor to be used for darkening/lighting the color (0 = pure black)
     * @return  the new color
     */
    public static Color changeLight(Color c, float factor) {
        int r = Math.round(c.getRed() * factor);
        int g = Math.round(c.getGreen() * factor);
        int b = Math.round(c.getBlue() * factor);
        
        if (r < 0) r = 0; else if (r > 255) r = 255;
        if (g < 0) g = 0; else if (g > 255) g = 255;
        if (b < 0) b = 0; else if (b > 255) b = 255;
        
        return new Color(r, g, b);
    }

    /**
     * Creates a new version of a color, setting the transparency according to the alpha value passed in input.
     * 
     * @param c  the original color
     * @param alpha  the alpha value
     * @return  a new version of the original color with increased transparency
     */
    public static Color setAlpha(Color c, float alpha) {
        if (c == null) return null;
        float[] comp = c.getComponents(null);
        comp[3] *= alpha;
        return new Color(comp[0], comp[1], comp[2], comp[3]);
    }
    
    /**
     * Applies a shading to a color.
     * 
     * @param col  the color
     * @param shading  the shading factor
     * @return  the shaded color
     */
    public static Color applyShading(Color col, int shading) {
        if (shading <= 0) return col;
        
        float[] hsb = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
        hsb[2] *= (100f-shading)/100f;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }
}
