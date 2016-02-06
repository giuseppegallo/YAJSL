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

/**
 * String-related utilities.
 * 
 * @author Giuseppe Gallo
 */
public class StringUtils {
    /**
     * Returns the number of times the given substring occurs in the given string.
     * <p>
     * Please note that matches can overlap.
     * For instance for this function "XX" occurs 2 times in "ABCXXXDE", at character 4 and 5.
     * 
     * @param string  the string in which the substring needs to be found
     * @param substr  the substring
     * @return  the number of times the given substring occurs in the given string
     */
    public static int count(String string, String substr) {
        int count = 0;
        int idx = 0;
        
        while ((idx = string.indexOf(substr, idx)) >= 0) {
            ++count;
            ++idx;
        }
        
        return count;
    }
    
    /**
     * Reverses the given string.
     * 
     * @param string  the string to be reversed
     * @return  the reversed string
     */
    public static String reverse(String string) {
        return (string == null) ? null : new StringBuilder(string).reverse().toString();
    }
    
    /**
     * Compares two strings and accepts also null values (considered before everything).
     * 
     * @param s1  the first string to be compared
     * @param s2  the second string to be compared
     * @return  the result of the comparison (-1 if s1 &lt; s2, 0 if s1 = s2, 1 if s1 &gt; s2)
     */
    public static int compareWithNull(String s1, String s2) {
        return (s1 == null && s2 == null) ? 0 : (s1 == null) ? -1 : (s2 == null) ? 1 : s1.compareTo(s2); 
    }
    
    /**
     * Returns the string obtained by repeating the given token several times.
     * 
     * @param token  the token to be repeated
     * @param times  the number of repetitions
     * @return  the string obtained by repeating the given token several times
     */
    public static String repeat(String token, int times) {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < times; ++i) {
            sb.append(token);
        }
        
        return sb.toString();
    }
}
