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

import java.io.File;

/**
 * A collection of useful methods for files and paths.
 * 
 * @author Giuseppe Gallo
 */
public class FileUtils {

    /**
     * Returns the relative path of a file, compared to the path of a parent one.
     * Returns null if the parent is not actually a parent directory of the file.
     * 
     * @param file  the file
     * @param parent  the parent directory
     * @return the relative path of a file, compared to the path of a parent one (or null in case of any issue).
     */
    public static String getSubPath(File file, File parent) {
        String dir = file.getAbsolutePath();
        
        if (!parent.isDirectory()) return null;
        
        String parentDir = parent.getAbsolutePath();
        if (!dir.startsWith(parentDir)) return null;
        
        return (parentDir.equals(dir)) ? "" : dir.substring(parentDir.length() + 1);
    }
}
