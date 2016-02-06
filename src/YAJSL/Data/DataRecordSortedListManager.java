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

import YAJSL.Utils.ReflectionUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * An implementation of the DataRecordManager interface which uses an
 array list as data source.
 * 
 * @param <T>  the type of DataRecord handled by this data manager.
 * @author Giuseppe Gallo
 */
public class DataRecordSortedListManager<T extends DataRecord> implements DataRecordManager<T>, DataListListener {

    /** An empty array of Objects, to be used for invoking methods with reflection */
    private final static Object[] EMPTY_OBJECT_ARRAY = new Object[]{};
    
    /** An empty array of Classes, to be used for invoking methods with reflection */
    private final static Class[] EMPTY_CLASS_ARRAY = new Class[]{};
    
    /** The data source used by this data manager */
    private final ArrayList<T> data;
    
    /** The map of known getter methods */
    private final static HashMap<String, Method> getters = new HashMap<>();
    
    /** The map of known setter methods */
    private final static HashMap<String, Method> setters = new HashMap<>();
    
    /** The comparator to be used for sorting data */
    private final Comparator<T> comparator;
    
    /** The sorted data */
    private ArrayList<T> sorted;
    
    /** The list of listeners for data change */
    private LinkedList<DataListListener> listeners = null;

    
    /**
     * Instantiates a DataRecordManager using the given list as source of data.
     * 
     * @param data  the list of data to be used as data source
     * @param comparator  the comparator to be used for sorting data 
     */
    public DataRecordSortedListManager(ArrayList<T> data, Comparator<T> comparator) {
        this.data = data;
        this.comparator = comparator;
        
        if (data == null) {
            sorted = new ArrayList<>(0);
        } else {
            sorted = new ArrayList<>(data);
            Collections.sort(sorted, comparator);
        }
    }
    
    /**
     * Returns the object at position index in the list.
     * 
     * @param index  the index of the object to be returned.
     * @return  the object at position index in the list
     */
    @Override
    public T get(int index) {
        return sorted.get(index);
    }
    
    /**
     * Returns the position of the given object in the list (-1 = not found).
     * 
     * @param object  the object for which the position in the list needs to be returned.
     * @return  the position of the given object in the list (-1 = not found)
     */
    @Override
    public int getIndex(T object) {
        return (sorted == null) ? -1 : sorted.indexOf(object);
    }
    
    /**
     * Returns the number of objects in the list handled by this data manager.
     * 
     * @return  the number of objects in the list handled by this data manager
     */
    @Override
    public int size() {
        return sorted.size();
    }

    /**
     * Notifies a generic change in the list of objects.
     * 
     * @param type  the class of the objects in the list
     */
    @Override
    public void listChanged(Class type) {
        sorted = new ArrayList<>(data);
        Collections.sort(sorted, comparator);
        
        if (listeners == null) return;
        listeners.stream().forEach(l -> l.listChanged(type));
    }

    /**
     * Notifies the addition of an object to a list (class-specific).
     *
     * @param index  the position in the list of the object added
     * @param type  the class of the object added
     * @param object  the object added
     */
    @Override
    public void objectAdded(int index, Class type, Object object) {
        sorted.add(data.get(index));
        Collections.sort(sorted, comparator);
        
        if (listeners == null) return;
        listeners.stream().forEach(l -> l.listChanged(type));
    }

    /**
     * Notifies the removal of an object from a list (class-specific).
     *
     * @param index  the position in the list of the object removed
     * @param type  the class of the object removed
     */
    @Override
    public void objectRemoved(int index, Class type) {
        listChanged(type);
    }

    /**
     * Notifies the change of an object in a list (class-specific).
     *
     * @param index  the position in the list of the object changed
     * @param type  the class of the object changed
     * @param object  the object added
     */
    @Override
    public void objectChanged(int index, Class type, Object object) {
        Collections.sort(sorted, comparator);
        
        if (listeners == null) return;
        listeners.stream().forEach(l -> l.listChanged(type));
    }

    /**
     * Adds a listener for the list of objects of the given class.
     *
     * @param l  the listener to be added
     */
    public void addDataListListener(DataListListener l) {
        if (listeners == null) listeners = new LinkedList<>();
        listeners.add(l);
    }

    /**
     * Removes a listener for the list of objects of the given class.
     *
     * @param l  the listener to be removed
     */
    public void removeDataListListener(DataListListener l) {
        listeners.remove(l);
    }

    /**
     * Returns the value of a field for the object of the given index, using the
     * getter method specified.
     * 
     * @param index  the index of the object for which the field must be returned
     * @param getter  the name of the getter method to be called to retrieve the value of the field
     * @return  the value of a field for the object of the given index
     * @throws Exception  in case of any problem 
     */
    @Override
    public Object getField(int index, String getter) throws Exception {
        T obj = sorted.get(index);
        Method method = getters.get(getter);
        if (method == null) {
            method = ReflectionUtils.getMethod(obj.getClass(), getter, EMPTY_CLASS_ARRAY);
            getters.put(getter, method);
        }
        return method.invoke(obj, EMPTY_OBJECT_ARRAY);
    }
    
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
    @Override
    public void setField(int index, String setter, Class type, Object value) throws Exception {
        T obj = sorted.get(index);
        Method method = setters.get(setter);
        if (method == null) {
            method = ReflectionUtils.getMethod(obj.getClass(), setter, new Class[]{type});
            setters.put(setter, method);
        }
        method.invoke(obj, new Object[]{value});
        obj.store();
    }

    /**
     * Performs any action needed to effectively delete the object.
     * 
     * @param index  the index of the object to be deleted
     * @throws Exception  in case of any problem
     */
    @Override
    public void delete(int index) throws Exception {
        sorted.get(index).delete();
    }    
}
