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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reflection-related utilities.
 * 
 * @author Giuseppe Gallo
 */
public class ReflectionUtils {
    /**
     * Returns all the fields in the given class, regardless of the access modifier
     * and including the ones inherited by any superclass.
     *
     * @param c  the class for which the fields needs to be returned
     * @return  the list with the name of each field and the corresponding Field information
     */
    public static List<Field> getAllFields(Class c) {
        List<Field> fields = new LinkedList<>();

        while (c != null) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
            c = c.getSuperclass();
        }

        return fields;
    }

    /**
     * Returns all the methods in the given class, regardless of the access modifier
     * and including the ones inherited by any superclass.
     *
     * @param c  the class for which the methods needs to be returned
     * @return  the list with the name of each method and the corresponding Method information
     */
    public static List<Method> getAllMethods(Class c) {
        List<Method> methods = new LinkedList<>();

        while (c != null) {
            methods.addAll(Arrays.asList(c.getDeclaredMethods()));
            c = c.getSuperclass();
        }

        return methods;
    }

    /**
     * Returns the field with the given name in the given class, regardless of the access modifier
     * and including in the search also the ones inherited by any superclass.
     *
     * @param c  the class for which the field need to be returned
     * @param name  the name of the field to be returned
     * @return  the Field information for the field (or null if not found)
     */
    public static Field getField(Class c, String name) {
        while (c != null) {
            try {
                Field field = c.getDeclaredField(name);
                if (field != null) return field;
            } catch (NoSuchFieldException ex) {
                /* Nothing to do */
            }
            c = c.getSuperclass();
        }

        return null;
    }

    /**
     * Returns the method with the given name and parameters in the given class,
     * regardless of the access modifier and including in the search also the ones
     * inherited by any superclass.
     *
     * @param c  the class for which the method need to be returned
     * @param name  the name of the method to be returned
     * @param params  the parameters of the method
     * @return  the Method information for the method (or null if not found)
     */
    public static Method getMethod(Class c, String name, Class[] params) {
        if (name == null) return null;
        
        Class orig = c;
        while (c != null) {
            try {
                Method method = c.getDeclaredMethod(name, params);
                if (method != null) return method;
            } catch (NoSuchMethodException ex) {
                /* Nothing to do */
            }
            c = c.getSuperclass();
        }

        Logger.getLogger(ReflectionUtils.class.getName()).log(Level.WARNING, "Method \"{0}\" not found for class: {1}", new Object[]{name, orig.getName()});
        return null;
    }
}
