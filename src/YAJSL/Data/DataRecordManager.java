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
 * The common interface for all Data Managers to be used for handling lists of DataRecord.
 * 
 * @param <T>  the type of DataRecord handled by this data manager.
 * @author Giuseppe Gallo
 */
public interface DataRecordManager<T extends DataRecord> {
    
    /**
     * Returns the object at position index in the list.
     * 
     * @param index  the index of the object to be returned.
     * @return  the object at position index in the list
     */
    public T get(int index);
    
    /**
     * Returns the position of the given object in the list (-1 = not found).
     * 
     * @param object  the object for which the position in the list needs to be returned.
     * @return  the position of the given object in the list (-1 = not found)
     */
    public int getIndex(T object);
    
    /**
     * Returns the number of objects in the list handled by this data manager.
     * 
     * @return  the number of objects in the list handled by this data manager
     */
    public int size();

    /**
     * Performs any action needed to effectively delete the object.
     * 
     * @param index  the index of the object to be deleted
     * @throws Exception  in case of any problem
     */
    public void delete(int index) throws Exception;
    
    /**
     * Returns the value of a field for the object of the given index, using the
     * getter method specified.
     * 
     * @param index  the index of the object for which the field must be returned
     * @param getter  the name of the getter method to be called to retrieve the value of the field
     * @return  the value of a field for the object of the given index
     * @throws Exception  in case of any problem 
     */
    public Object getField(int index, String getter) throws Exception;
    
    /**
     * Sets the value of a field for the object of the given index, using the
     * setter method specified.
     * 
     * @param index  the index of the object for which the field must be set
     * @param setter  the name of the setter method to be called to set the value of the field
     * @param type  the type of the argument for the setter method
     * @param value  the value to be set for the field
     * @throws Exception  in case of any problem 
     */
    public void setField(int index, String setter, Class<?> type, Object value) throws Exception;
}
