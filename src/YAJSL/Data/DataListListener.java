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
package YAJSL.Data;

/**
 * Interface to be implemented by all classes listening for changes in the lists of objects.
 *
 * @author Giuseppe Gallo
 */
public interface DataListListener {
    /**
     * Notifies a generic change in the list of objects.
     * 
     * @param type  the class of the objects in the list
     */
    public void listChanged(Class<?> type);

    /**
     * Notifies the addition of an object to a list (class-specific).
     *
     * @param index  the position in the list of the object added
     * @param type  the class of the object added
     * @param object  the object added 
     */
    public void objectAdded(int index, Class<?> type, Object object);

    /**
     * Notifies the removal of an object from a list (class-specific).
     *
     * @param index  the position in the list of the object removed
     * @param type  the class of the object removed
     */
    public void objectRemoved(int index, Class<?> type);

    /**
     * Notifies the change of an object in a list (class-specific).
     *
     * @param index  the position in the list of the object changed
     * @param type  the class of the object changed
     * @param object  the object changed
     */
    public void objectChanged(int index, Class<?> type, Object object);
}
