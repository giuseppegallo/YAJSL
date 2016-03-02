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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Properties;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 * Extends the Properties class, adding new capabilities.
 *
 * @author Giuseppe Gallo
 */
public class ExtendedProperties extends Properties {

    /**
     * Exception thrown when a required property is missing.
     */
    public static class MissingPropertyException extends Exception {
        public MissingPropertyException(String name) {
            super("Missing property: [" + name + "]");
        }
    }

    /**
     * Exception thrown when the value for a property is invalid.
     */
    public static class InvalidPropertyValueException extends Exception {
        public InvalidPropertyValueException(String name, String value) {
            super("Invalid value for property: property=[" + name + "]; value=[" + value + "]");
        }
    }

    /**
     * Instantiates an ExtendedProperties object.
     */
    public ExtendedProperties() {
        super();
    }

    /**
     * Instantiates an ExtendedProperties object, initializing it with the properties provided as parameter.
     * 
     * @param properties  the properties to be used for initializing the object.
     */
    public ExtendedProperties(Properties properties) {
        super(properties);
    }

    /**
     * Instantiates an ExtendedProperties object, initializing it with the properties read from the input stream provided as parameter.
     * 
     * @param stream  the stream to be used for reading the properties.
     * @throws IOException  in case of any issue reading from the input stream
     */
    public ExtendedProperties(InputStream stream) throws IOException {
        super();
        load(stream);
    }

    /**
     * Instantiates an ExtendedProperties object, initializing it with the properties provided as parameter.
     * The properties provided are filtered so that only the ones starting with the given prefix are kept.
     * The prefix is removed from the property name.
     *
     * @param properties  the properties to be used for initializing the object.
     * @param prefix  the prefix to be used for filtering properties
     */
    public ExtendedProperties(Properties properties, String prefix) {
        super(filter(properties, prefix));
    }

    /**
     * Instantiates an ExtendedProperties object, initializing it with the properties read from the input stream provided as parameter.
     * The properties provided are filtered so that only the ones starting with the given prefix are kept.
     * The prefix is removed from the property name.
     * 
     * @param stream  the stream to be used for reading the properties.
     * @param prefix  the prefix to be used for filtering properties
     * @throws IOException  in case of any issue reading from the input stream
     */
    public ExtendedProperties(InputStream stream, String prefix) throws IOException {
        super();
        Properties props = new Properties();
        props.load(stream);
        putAll(props);
    }

    /**
     * Filters the given properties and returns a new properties object containing only
     * the ones starting with the given prefix.
     * The prefix is removed from the property name.
     *
     * @param properties  the properties to be filtered
     * @param prefix  the prefix to be used for filtering properties
     * @return  a new Properties object containing only the properties starting with the given prefix
     */
    public static Properties filter(Properties properties, String prefix) {
        if (properties == null || prefix == null) return null;

        Properties filtered = new Properties();
        int len = prefix.length();
        properties.stringPropertyNames().stream().filter((name) -> (name.startsWith(prefix))).forEach((name) -> {
            String newName = name.substring(len);
            filtered.setProperty(newName, properties.getProperty(name));
        });

        return filtered;
    }

    /**
     * Instantiates a new ExtendedProperties object, initializing it using the current properties.
     * The properties, however,  are filtered so that only the ones starting with the given prefix are kept.
     * The prefix is removed from the property name.
     *
     * @param prefix  the prefix to be used for filtering properties
     * @return  a new ExtendedProperties object, containing only the properties starting with the given prefix
     */
    public ExtendedProperties filter(String prefix) {
        return new ExtendedProperties(this, prefix);
    }

    /**
     * Parses the given string and returns the corresponding boolean value.
     * 
     * @param value  the string to be parsed
     * @return  true or false if the string can be parsed, null otherwise
     */
    private static Boolean parseBoolean(String value) {
        if (value == null) return null;

        if (value.equalsIgnoreCase("true") ||
            value.equalsIgnoreCase("1") ||
            value.equalsIgnoreCase("yes")) {
            return true;
        } else if (value.equalsIgnoreCase("false") ||
                   value.equalsIgnoreCase("0") ||
                   value.equalsIgnoreCase("no")) {
            return false;
        }

        return null;
    }

    /**
     * Returns the boolean value corresponding to the given property in a properties object.
     * If the property is not required and does not exist, the default value is returned.
     *
     * @param properties  the properties object to be used
     * @param name  the name of the property
     * @param required  if true and the property is missing, an exception is thrown; if false, the default value is used
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the boolean value corresponding to the given property in a properties object
     * @throws ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws ExtendedProperties.InvalidPropertyValueException  if the value for a property is invalid
     */
    public static Boolean getBooleanProperty(Properties properties, String name, boolean required, Boolean defaultValue) throws
            MissingPropertyException, InvalidPropertyValueException {
        String value = properties.getProperty(name);

        if (value == null) {
            if (required) {
                throw new MissingPropertyException(name);
            } else if (defaultValue != null) {
                return defaultValue;
            } else {
                return null;
            }
        }

        Boolean parsed = parseBoolean(value);
        if (parsed == null) {
            throw new InvalidPropertyValueException(name, value);
        }

        return parsed;
    }

    /**
     * Returns the boolean value corresponding to the given property (not reuired) in a properties object.
     * If the property does not exist, the default value is returned.
     *
     * @param properties  the properties object to be used
     * @param name  the name of the property
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the boolean value corresponding to the given property in a properties object
     */
    public static Boolean getBooleanPropertySilent(Properties properties, String name, Boolean defaultValue) {
        try {
            return getBooleanProperty(properties, name, false, defaultValue);
        } catch (MissingPropertyException | InvalidPropertyValueException ex) {
            Logger.getLogger(ExtendedProperties.class.getName()).log(Level.WARNING, null, ex);
            return defaultValue;
        }
    }

    /**
     * Returns the boolean value corresponding to the given property.
     * If the property is not required and does not exist, the default value is returned.
     *
     * @param name  the name of the property
     * @param required  if true and the property is missing, an exception is thrown; if false, the default value is used
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the boolean value corresponding to the given property
     * @throws ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws ExtendedProperties.InvalidPropertyValueException  if the value for a property is invalid
     */
    public Boolean getBooleanProperty(String name, boolean required, Boolean defaultValue) throws
            MissingPropertyException, InvalidPropertyValueException {
        return getBooleanProperty(this, name, required, defaultValue);
    }

    /**
     * Parses the given string and returns the corresponding boolean value.
     * 
     * @param value  the string to be parsed
     * @return  the correspoinding alignment if the string can be parsed, null otherwise
     */
    private static Integer parseAlignment(String value) {
        if (value == null) return null;

        if (value.equalsIgnoreCase("leading")) return JLabel.LEADING;
        else if (value.equalsIgnoreCase("left")) return JLabel.LEFT;
        else if (value.equalsIgnoreCase("center")) return JLabel.CENTER;
        else if (value.equalsIgnoreCase("right")) return JLabel.RIGHT;
        else if (value.equalsIgnoreCase("trailing")) return JLabel.TRAILING;

        return null;
    }

    /**
     * Returns the alignment corresponding to the given property in a properties object.
     * If the property is not required and does not exist, the default value is returned.
     *
     * @param properties  the properties object to be used
     * @param name  the name of the property
     * @param required  if true and the property is missing, an exception is thrown; if false, the default value is used
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the alignment corresponding to the given property in a properties object
     * @throws ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws ExtendedProperties.InvalidPropertyValueException  if the value for a property is invalid
     */
    public static Integer getAlignmentProperty(Properties properties, String name, boolean required, Integer defaultValue) throws
            MissingPropertyException, InvalidPropertyValueException {
        String value = properties.getProperty(name);

        if (value == null) {
            if (required) {
                throw new MissingPropertyException(name);
            } else if (defaultValue != null) {
                return defaultValue;
            } else {
                return null;
            }
        }

        Integer parsed = parseAlignment(value);
        if (parsed == null) {
            throw new InvalidPropertyValueException(name, value);
        }

        return parsed;
    }

    /**
     * Returns the alignment corresponding to the given property (not reuired) in a properties object.
     * If the property does not exist, the default value is returned.
     *
     * @param properties  the properties object to be used
     * @param name  the name of the property
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the boolean value corresponding to the given property in a properties object
     */
    public static Integer getAlignmentPropertySilent(Properties properties, String name, Integer defaultValue) {
        try {
            return getAlignmentProperty(properties, name, false, defaultValue);
        } catch (MissingPropertyException | InvalidPropertyValueException ex) {
            Logger.getLogger(ExtendedProperties.class.getName()).log(Level.WARNING, null, ex);
            return defaultValue;
        }
    }

    /**
     * Returns the alignment corresponding to the given property.
     * If the property is not required and does not exist, the default value is returned.
     *
     * @param name  the name of the property
     * @param required  if true and the property is missing, an exception is thrown; if false, the default value is used
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the boolean value corresponding to the given property
     * @throws ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws ExtendedProperties.InvalidPropertyValueException  if the value for a property is invalid
     */
    public Integer getAlignmentProperty(String name, boolean required, Integer defaultValue) throws
            MissingPropertyException, InvalidPropertyValueException {
        return getAlignmentProperty(this, name, required, defaultValue);
    }

    /**
     * Parses the given string and returns the corresponding double value.
     *
     * @param value  the string to be parsed
     * @return  the corresponding double value if the string can be parsed, null otherwise
     */
    private static Double parseDouble(String value) {
        if (value == null) return null;

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Returns the double value corresponding to the given property in a properties object.
     * If the property does not exist, the default value is returned.
     *
     * @param properties  the properties object to be used
     * @param name  the name of the property
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the double value corresponding to the given property in a properties object
     */
    public static Double getDoublePropertySilent(Properties properties, String name, Double defaultValue) {
        try {
            return getDoubleProperty(properties, name, false, defaultValue);
        } catch (MissingPropertyException | InvalidPropertyValueException ex) {
            Logger.getLogger(ExtendedProperties.class.getName()).log(Level.WARNING, null, ex);
            return defaultValue;
        }
    }

    /**
     * Returns the double value corresponding to the given property in a properties object.
     * If the property is not required and does not exist, the default value is returned.
     *
     * @param properties  the properties object to be used
     * @param name  the name of the property
     * @param required  if true and the property is missing, an exception is thrown; if false, the default value is used
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the double value corresponding to the given property in a properties object
     * @throws ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws ExtendedProperties.InvalidPropertyValueException  if the value for a property is invalid
     */
    public static Double getDoubleProperty(Properties properties, String name, boolean required, Double defaultValue) throws
            MissingPropertyException, InvalidPropertyValueException {
        String value = properties.getProperty(name);

        if (value == null) {
            if (required) {
                throw new MissingPropertyException(name);
            } else if (defaultValue != null) {
                return defaultValue;
            } else {
                return null;
            }
        }

        Double parsed = parseDouble(value);
        if (parsed == null) {
            throw new InvalidPropertyValueException(name, value);
        }

        return parsed;
    }

    /**
     * Returns the double value corresponding to the given property.
     * If the property is not required and does not exist, the default value is returned.
     *
     * @param name  the name of the property
     * @param required  if true and the property is missing, an exception is thrown; if false, the default value is used
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the double value corresponding to the given property
     * @throws ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws ExtendedProperties.InvalidPropertyValueException  if the value for a property is invalid
     */
    public Double getDoubleProperty(String name, boolean required, Double defaultValue) throws
            MissingPropertyException, InvalidPropertyValueException {
        return getDoubleProperty(this, name, required, defaultValue);
    }

    /**
     * Parses the given string and returns the corresponding integer value.
     *
     * @param value  the string to be parsed
     * @return  the corresponding integer value if the string can be parsed, null otherwise
     */
    private static Integer parseInteger(String value) {
        if (value == null) return null;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Returns the integer value corresponding to the given property in a properties object.
     * If the property is not required and does not exist, the default value is returned.
     *
     * @param properties  the properties object to be used
     * @param name  the name of the property
     * @param required  if true and the property is missing, an exception is thrown; if false, the default value is used
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the integer value corresponding to the given property in a properties object
     * @throws ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws ExtendedProperties.InvalidPropertyValueException  if the value for a property is invalid
     */
    public static Integer getIntegerProperty(Properties properties, String name, boolean required, Integer defaultValue) throws
            MissingPropertyException, InvalidPropertyValueException {
        String value = properties.getProperty(name);

        if (value == null) {
            if (required) {
                throw new MissingPropertyException(name);
            } else if (defaultValue != null) {
                return defaultValue;
            } else {
                return null;
            }
        }

        Integer parsed = parseInteger(value);
        if (parsed == null) {
            throw new InvalidPropertyValueException(name, value);
        }

        return parsed;
    }

    /**
     * Returns the integer value corresponding to the given property in a properties object.
     * If the property does not exist, the default value is returned.
     *
     * @param properties  the properties object to be used
     * @param name  the name of the property
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the integer value corresponding to the given property in a properties object
     */
    public static Integer getIntegerPropertySilent(Properties properties, String name, Integer defaultValue) {
        if (properties == null) return null;
        try {
            return getIntegerProperty(properties, name, false, defaultValue);
        } catch (MissingPropertyException | InvalidPropertyValueException ex) {
            Logger.getLogger(ExtendedProperties.class.getName()).log(Level.WARNING, null, ex);
            return defaultValue;
        }
    }

    /**
     * Returns the integer value corresponding to the given property.
     * If the property is not required and does not exist, the default value is returned.
     *
     * @param name  the name of the property
     * @param required  if true and the property is missing, an exception is thrown; if false, the default value is used
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the integer value corresponding to the given property
     * @throws ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws ExtendedProperties.InvalidPropertyValueException  if the value for a property is invalid
     */
    public Integer getIntegerProperty(String name, boolean required, Integer defaultValue) throws
            MissingPropertyException, InvalidPropertyValueException {
        return getIntegerProperty(this, name, required, defaultValue);
    }

    /**
     * Returns the integer value corresponding to the given property.
     * If the property does not exist, the default value is returned.
     *
     * @param name  the name of the property
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the integer value corresponding to the given property
     */
    public Integer getIntegerPropertySilent(String name, Integer defaultValue) {
        return getIntegerPropertySilent(this, name, defaultValue);
    }

    /**
     * Parses the given string and returns the corresponding long value.
     *
     * @param value  the string to be parsed
     * @return  the corresponding long value if the string can be parsed, null otherwise
     */
    private static Long parseLong(String value) {
        if (value == null) return null;

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Returns the long value corresponding to the given property in a properties object.
     * If the property is not required and does not exist, the default value is returned.
     *
     * @param properties  the properties object to be used
     * @param name  the name of the property
     * @param required  if true and the property is missing, an exception is thrown; if false, the default value is used
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the long value corresponding to the given property in a properties object
     * @throws ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws ExtendedProperties.InvalidPropertyValueException  if the value for a property is invalid
     */
    public static Long getLongProperty(Properties properties, String name, boolean required, Long defaultValue) throws
            MissingPropertyException, InvalidPropertyValueException {
        String value = properties.getProperty(name);

        if (value == null) {
            if (required) {
                throw new MissingPropertyException(name);
            } else if (defaultValue != null) {
                return defaultValue;
            } else {
                return null;
            }
        }

        Long parsed = parseLong(value);
        if (parsed == null) {
            throw new InvalidPropertyValueException(name, value);
        }

        return parsed;
    }

    /**
     * Returns the long value corresponding to the given property.
     * If the property is not required and does not exist, the default value is returned.
     *
     * @param name  the name of the property
     * @param required  if true and the property is missing, an exception is thrown; if false, the default value is used
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the long corresponding to the given property
     * @throws ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws ExtendedProperties.InvalidPropertyValueException  if the value for a property is invalid
     */
    public Long getLongProperty(String name, boolean required, Long defaultValue) throws
            MissingPropertyException, InvalidPropertyValueException {
        return getLongProperty(this, name, required, defaultValue);
    }

    /**
     * Parses the given string and returns the corresponding byte value.
     *
     * @param value  the string to be parsed
     * @return  the corresponding byte value if the string can be parsed, null otherwise
     */
    private static Byte parseByte(String value) {
        if (value == null) return null;

        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Returns the byte value corresponding to the given property in a properties object.
     * If the property is not required and does not exist, the default value is returned.
     *
     * @param properties  the properties object to be used
     * @param name  the name of the property
     * @param required  if true and the property is missing, an exception is thrown; if false, the default value is used
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the byte value corresponding to the given property in a properties object
     * @throws ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws ExtendedProperties.InvalidPropertyValueException  if the value for a property is invalid
     */
    public static Byte getByteProperty(Properties properties, String name, boolean required, Byte defaultValue) throws
            MissingPropertyException, InvalidPropertyValueException {
        String value = properties.getProperty(name);

        if (value == null) {
            if (required) {
                throw new MissingPropertyException(name);
            } else if (defaultValue != null) {
                return defaultValue;
            } else {
                return null;
            }
        }

        Byte parsed = parseByte(value);
        if (parsed == null) {
            throw new InvalidPropertyValueException(name, value);
        }

        return parsed;
    }

    /**
     * Returns the byte value corresponding to the given property.
     * If the property is not required and does not exist, the default value is returned.
     *
     * @param name  the name of the property
     * @param required  if true and the property is missing, an exception is thrown; if false, the default value is used
     * @param defaultValue  the default value to be returned if the property does not exist
     * @return  the byte corresponding to the given property
     * @throws ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws ExtendedProperties.InvalidPropertyValueException  if the value for a property is invalid
     */
    public Byte getByteProperty(String name, boolean required, Byte defaultValue) throws
            MissingPropertyException, InvalidPropertyValueException {
        return getByteProperty(this, name, required, defaultValue);
    }

    /**
     * Parses the given string and returns the corresponding color.
     *
     * @param value  the string to be parsed
     * @return  the corresponding color if the string can be parsed, null otherwise
     */
    private static Color parseColor(String value) {
        if (value == null) return null;

        try {
            return Color.decode(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Returns the color corresponding to the given property in a properties object.
     * If the property is not required and does not exist, the default color is returned.
     *
     * @param properties  the properties object to be used
     * @param name  the name of the property
     * @param required  if true and the property is missing, an exception is thrown; if false, the default color is used
     * @param defaultValue  the default color to be returned if the property does not exist
     * @return  the color corresponding to the given property in a properties object
     * @throws ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws ExtendedProperties.InvalidPropertyValueException  if the value for a property is invalid
     */
    public static Color getColorProperty(Properties properties, String name, boolean required, Color defaultValue) throws
            MissingPropertyException, InvalidPropertyValueException {
        String value = properties.getProperty(name);

        if (value == null) {
            if (required) {
                throw new MissingPropertyException(name);
            } else if (defaultValue != null) {
                return defaultValue;
            } else {
                return null;
            }
        }

        Color parsed = parseColor(value);
        if (parsed == null) {
            throw new InvalidPropertyValueException(name, value);
        }

        return parsed;
    }

    /**
     * Returns the color corresponding to the given property in a properties object.
     * If the property does not exist, the default color is returned.
     *
     * @param properties  the properties object to be used
     * @param name  the name of the property
     * @param defaultValue  the default color to be returned if the property does not exist
     * @return  the color corresponding to the given property in a properties object
     */
    public static Color getColorPropertySilent(Properties properties, String name, Color defaultValue) {
        String value = properties.getProperty(name);

        if (value == null) return defaultValue;
        
        Color parsed = parseColor(value);

        return parsed;
    }

    /**
     * Returns the color corresponding to the given property.
     * If the property is not required and does not exist, the default color is returned.
     *
     * @param name  the name of the property
     * @param required  if true and the property is missing, an exception is thrown; if false, the default value is used
     * @param defaultValue  the default color to be returned if the property does not exist
     * @return  the color corresponding to the given property
     * @throws ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws ExtendedProperties.InvalidPropertyValueException  if the value for a property is invalid
     */
    public Color getColorProperty(String name, boolean required, Color defaultValue) throws
            MissingPropertyException, InvalidPropertyValueException {
        return getColorProperty(this, name, required, defaultValue);
    }
    
    /**
     * Returns the byte array corresponding to the given property in a properties object.
     * If the property does not exist an exception is thrown.
     *
     * @param properties  the properties object to be used
     * @param name  the name of the property
     * @return  the byte array corresponding to the given property in a properties object
     * @throws YAJSL.Utils.ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws YAJSL.Utils.ExtendedProperties.InvalidPropertyValueException  if a property value is invalid
     */
    public static byte[] getHexByteArray(Properties properties, String name) throws
            MissingPropertyException, InvalidPropertyValueException {
        
        byte[] array;
        
        String value = properties.getProperty(name);
        if (value == null) throw new MissingPropertyException(name);
        
        int len = value.length();
        if (len == 0 || len % 2 != 0) throw new InvalidPropertyValueException(name, value);

        len /= 2;
        array = new byte[len];
        
        for (int i = 0; i < len; ++i) {
            String val = value.substring(i*2, i*2+2);
            try {
                array[i] = (byte)Integer.parseInt(val, 16);
            } catch (NumberFormatException ex) {
                throw new InvalidPropertyValueException(name, value);
            }
        }
        
        return array;
    }
    
    /**
     * Returns the byte array corresponding to the given property.
     * If the property does not exist an exception is thrown.
     *
     * @param name  the name of the property
     * @return  the byte array corresponding to the given property
     * @throws YAJSL.Utils.ExtendedProperties.MissingPropertyException  if a required property is missing
     * @throws YAJSL.Utils.ExtendedProperties.InvalidPropertyValueException  if a property value is invalid
     */
    public byte[] getHexByteArray(String name) throws
            MissingPropertyException, InvalidPropertyValueException {
        return getHexByteArray(this, name);
    }

    /**
     * Returns the value of a mandatory string property.
     * 
     * @param properties  the properties containing the entry
     * @param name  the name of the property to be returned
     * @return  the value of a mandatory string property
     * @throws ExtendedProperties.MissingPropertyException  if the property is missing
     */
    public static String getRequiredStringProperty(Properties properties, String name) throws MissingPropertyException {
        String text = properties.getProperty(name);
        if (text == null) {
            throw new MissingPropertyException(name);
        }
        return text;
    }
    
    /**
     * Returns the value of a mandatory string property.
     * 
     * @param name  the name of the property to be returned
     * @return  the value of a mandatory string property
     * @throws ExtendedProperties.MissingPropertyException  if the property is missing
     */
    public String getRequiredStringProperty(String name) throws MissingPropertyException {
        return getRequiredStringProperty(this, name);
    }
    
    /**
     * Extracts the properties starting with the given prefix and returns them sorted in alphabetical order.
     * 
     * @param properties  the properties
     * @param prefix  the prefix of the property names
     * @return  the properties starting with the given prefix, sorted in alphabetical order
     */
    public static Collection<String> getSortedKeys(Properties properties, String prefix) {
        TreeSet<String> keys = new TreeSet<>();
        if (properties == null) return keys;
        
        properties.stringPropertyNames().stream().filter((name) -> (name.startsWith(prefix))).forEach((name) -> {
            keys.add(name);
        });
        
        return keys;
    }
    
    /**
     * Extracts the properties starting with the given prefix and returns them sorted in alphabetical order.
     * 
     * @param prefix  the prefix of the property names
     * @return  the properties starting with the given prefix, sorted in alphabetical order
     */
    public Collection<String> getSortedKeys(String prefix) {
        return getSortedKeys(this, prefix);
    }
    
    /**
     * Reads all properties files in a directory whose name matches the given prefix and suffix and
     * returns the merged properties contained in the files.
     * 
     * @param dirName  the directory where to look for the properties files
     * @param prefix  the prefix for the name of the properties files
     * @param suffix  the suffix for the name of the properties files
     * @return  the properties read
     * @throws IOException  in case of any issue reading the files
     */
    public static Properties readPropertiesFiles(String dirName, String prefix, String suffix) throws IOException {
        File dir = new File((dirName == null || dirName.isEmpty()) ? "." : dirName);
        if (!dir.isDirectory()) return null;
        
        Properties props = new Properties();
        
        for (File file : dir.listFiles()) {
            String name = file.getName();
            if (!name.startsWith(prefix) || !name.endsWith(suffix)) continue;
            Properties p = new Properties();
            p.load(new FileInputStream(file));
            
            addProperties(props, p);
        }
        
        return props;
    }

    /**
     * Reads all properties files in a directory whose name matches the given prefix and suffix and
     * returns the merged properties contained in the files.
     * 
     * @param fileNames  the list of file names to be read
     * @return  the merged properties read
     * @throws IOException  in case of any issue reading the files
     */
    public static Properties readPropertiesFiles(Collection<String> fileNames) throws IOException {
        Properties props = new Properties();
        
        for (String fileName : fileNames) {
            addProperties(props, fileName);
        }
        
        return props;
    }

    /**
     * Adds a set of properties to an existing one (overwriting any already existing property).
     * 
     * @param properties  the existing properties
     * @param addition  the properties to be added
     * @return  the modified properties (this is the same reference to the existing properties passed)
     */
    public static Properties addProperties(Properties properties, Properties addition) {
        addition.entrySet().stream().forEach(e -> {
            String key = (String)e.getKey();
            String value = (String)e.getValue();
            properties.setProperty(key, value);
        });
        
        return properties;
    }

    /**
     * Adds a set of properties to an existing one (overwriting any already existing property).
     * 
     * @param properties  the existing properties
     * @param fileName  the name of the properties file to be added
     * @return  the modified properties (this is the same reference to the existing properties passed)
     * @throws IOException  in case of any issue reading the files
     */
    public static Properties addProperties(Properties properties, String fileName) throws IOException {
        File file = new File(fileName);
        Properties addition = new Properties();
        addition.load(new FileInputStream(file));
        
        return addProperties(properties, addition);
    }

    /**
     * Adds a set of properties to an existing one (overwriting any already existing property).
     * 
     * @param properties  the existing properties
     * @param urls  the list of URLs of the properties files to be added
     * @return  the modified properties (this is the same reference to the existing properties passed)
     * @throws IOException  in case of any issue reading the files
     */
    public static Properties addProperties(Properties properties, Collection<URL> urls) throws IOException {
        for (URL url : urls) {
            Properties p = new Properties();
            String protocol = url.getProtocol();
            if (protocol == null) continue;
            
            switch (protocol) {
                case "jar":{
                    String file = url.getFile();
                    int idx = file.lastIndexOf("!");
                    if (idx < 0) continue;
                    p.load(Localizer.class.getResourceAsStream(file.substring(idx + 1)));
                    break;
                    }
                case "file":{
                    String file = url.getFile();
                    p.load(new FileInputStream(file));
                    break;
                    }
                default:
                    continue;
            }
            
            addProperties(properties, p);
        }
        
        return properties;
    }
}
