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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * Represents a generic view.
 * 
 * @author Giuseppe Gallo
 * @param <P>  the type of data of the parent objects
 * @param <C>  the type of data handled by this view
 */
public class View<P extends View.ViewParent, C extends View.ViewChild> implements DataListListener, DataRecordManager<C> {
    
    /**
     * The interface to be implemented by any parent class for the view.
     * 
     * @param <T>  the type of children
     */
    public static interface ViewParent<T extends DataRecord> extends DataRecord {

        /**
         * Removes the given child from the list in this parent.
         * 
         * @param child  the child to be removed
         * @throws Exception  in case of any issue
         */
        public void removeChild(T child) throws Exception;
        
        /**
         * Notifies a change in this parent object.
         * 
         * @throws Exception  in case of any issue
         */
        public void notifyChange() throws Exception;
        
        /**
         * Returns the children for this parent.
         * 
         * @param view  the view requesting the objects
         * @return  the children for this parent
         */
        public Collection<T> getChildren(View view);
    }
    
    /**
     * The interface to be implemented by any child class for the view.
     */
    public static interface ViewChild extends DataRecord {

        /**
         * Returns true if the object has been modified and not yet stored.
         * 
         * @return  true if the object has been modified and not yet stored
         */
        public boolean isModified();
        
        /**
         * Returns the parent for the object.
         * 
         * @param view  the view requesting the objects
         * @return  the parent for the object
         */
        public ViewParent getParent(View view);
    }
    
    /** The list of listeners for data change */
    protected LinkedList<DataListListener> listeners = null;
    
    /** An empty array of Objects, to be used for invoking methods with reflection */
    protected final static Object[] EMPTY_OBJECT_ARRAY = new Object[]{};
    
    /** An empty array of Classes, to be used for invoking methods with reflection */
    @SuppressWarnings({"rawtypes"})
    protected final static Class[] EMPTY_CLASS_ARRAY = new Class<?>[]{};
    
    /** The map of known getter methods */
    protected final HashMap<String, Method> getters = new HashMap<>();
    
    /** The map of known setter methods */
    protected final HashMap<String, Method> setters = new HashMap<>();

    /** A copy of the objects used for fast access by index */
    protected ArrayList<C> objects = null;

    /** The parent object */
    protected ViewParent<C> parent = null;
    
    /** The type of objects handled by this view */
    protected final Class type;
    
    /** If true, stores the objects modified in setField */
    private boolean storeEnabled = true;

    
    /**
     * Allocates a new View instance.
     * 
     * @param type  the type of objects handled by this view
     */
    public View(Class type) {
        this.type = type;
    }

    /**
     * Returns the type of objects handled by this view.
     * 
     * @return  the type of objects handled by this view
     */
    public Class getType() {
        return type;
    }
    
    /**
     * Sets whether to store or not the objects modified during setField.
     * 
     * @param enabled  if true, stores the objects modified in setField
     */
    public void setStoreEnabled(boolean enabled) {
        storeEnabled = enabled;
    }
    
    /**
     * Returns true if the objects modified in setField are stored.
     * 
     * @return  true if the objects modified in setField are stored
     */
    public boolean isStoreEnabled() {
        return storeEnabled;
    }
    
    /**
     * Returns the parent object used for getting/setting the children.
     * 
     * @return  the parent object used for getting/setting the children
     */
    public P getParent() {
        return (P)parent;
    }
    
    /**
     * Sets the parent object to be used for getting/setting the children.
     * 
     * @param parent  the parent object to be used for getting/setting the children
     */
    public void setParent(P parent) {
        if (this.parent == parent) return;
        this.parent = parent;
        
        if (parent != null) {
            Collection<C> c = parent.getChildren(this);
            objects = (c == null) ? null : new ArrayList<>(c);
        } else {
            objects = null;
        }
        
        notifyListChanged(this.type);
    }
    
    /**
     * Returns the object at position index in the list.
     * 
     * @param index  the index of the object to be returned.
     * @return  the object at position index in the list
     */
    @Override
    public C get(int index) {
        return (objects == null) ? null : objects.get(index);
    }

    /**
     * Returns the position of the given object in the list (-1 = not found).
     * 
     * @param object  the object for which the position in the list needs to be returned.
     * @return  the position of the given object in the list (-1 = not found)
     */
    @Override
    public int getIndex(C object) {
        return (objects == null) ? -1 : objects.indexOf(object);
    }

    /**
     * Returns the number of objects in the list handled by this data manager.
     * 
     * @return  the number of objects in the list handled by this data manager
     */
    @Override
    public int size() {
        return (objects == null) ? 0 : objects.size();
    }

    /**
     * Performs any action needed to effectively delete the object.
     * 
     * @param index  the index of the object to be deleted
     * @throws Exception  in case of any problem
     */
    @Override
    public void delete(int index) throws Exception {
        C r = get(index);
        objectRemoved(index, this.type);
        objects.remove(index);
        parent.removeChild(r);
        parent.store();
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
        C i = objects.get(index);
        
        // Try invoking the method on the CounterEntry class
        Method method = getters.get(getter);
        if (method == null) {
            method = ReflectionUtils.getMethod(type, getter, EMPTY_CLASS_ARRAY);
            getters.put(getter, method);
        }
        return method.invoke(i, EMPTY_OBJECT_ARRAY);
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
    public void setField(int index, String setter, Class<?> type, Object value) throws Exception {
        C i = objects.get(index);
        
        // Try invoking the method on the CounterEntry class
        Method method = setters.get(setter);
        if (method == null) {
            method = ReflectionUtils.getMethod(this.type, setter, new Class<?>[]{type});
            setters.put(setter, method);
        }
        method.invoke(i, new Object[]{value});
        if (!i.isModified()) return;
        
        if (storeEnabled) {
            i.store();
        } else {
            objectChanged(index, type, value);
        }
        
        parent.notifyChange();
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
     * Notifies a generic change in the list of objects.
     * 
     * @param type  the class of the objects in the list
     */
    public void notifyListChanged(Class<?> type) {
        if (listeners == null) return;
        for (DataListListener l : listeners) {
            l.listChanged(type);
        }        
    }

    /**
     * Notifies a change in a specific object.
     * 
     * @param object  the object changed
     */
    public void notifyObjectChanged(C object) {
        int index = getIndex(object);
        if (listeners == null) return;
        for (DataListListener l : listeners) {
            l.objectChanged(index, type, object);
        }        
    }
    
    /**
     * Notifies the change of an object in a list (class-specific).
     *
     * @param index  the position in the list of the object changed
     * @param type  the class of the object changed
     * @param object  the object changed
     */
    @Override
    public void objectChanged(int index, Class<?> type, Object object) {
        if (type == this.type && object != null && ((ViewChild)object).getParent(this) == parent) {
            notifyObjectChanged((C)object);
        } else if (object == parent) {
            Collection<C> c = parent.getChildren(this);
            objects = (c == null) ? null : new ArrayList<>(c);
            notifyListChanged(type);
        }
    }

    /**
     * Notifies a generic change in the list of objects.
     * 
     * @param type  the class of the objects in the list
     */
    @Override
    public void listChanged(Class<?> type) {
        if (type == this.type && parent != null) {
            notifyListChanged(type);
        }
    }

    /**
     * Notifies the addition of an object to a list (class-specific).
     *
     * @param index  the position in the list of the object added
     * @param type  the class of the object added
     * @param object  the object added
     */
    @Override
    public void objectAdded(int index, Class<?> type, Object object) {
        if (type == this.type && object != null && parent != null && ((ViewChild)object).getParent(this) == parent) {
            objectChanged(0, parent.getClass(), parent);
        }
    }

    /**
     * Notifies the removal of an object from a list (class-specific).
     *
     * @param index  the position in the list of the object removed
     * @param type  the class of the object removed
     */
    @Override
    public void objectRemoved(int index, Class<?> type) {
        if (type == this.type && parent != null) {
            objectChanged(0, parent.getClass(), parent);
        }
    }
}
