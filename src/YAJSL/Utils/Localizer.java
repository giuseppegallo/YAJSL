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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.Icon;
import javax.swing.ImageIcon;


/**
 * Helper class for localizing applications.
 * 
 * @author Giuseppe Gallo
 */
public class Localizer {

    /**
     * The interface to be implemented by classes listening for locale changes.
     */
    public interface LocaleChangeListener {
        
        /**
         * Reacts to a locale change.
         * 
         * @param newLocale  the new locale
         */
        public void localeChanged(Locale newLocale);
    }

    /**
     * Represents a supported locale.
     */
    public static class Language {
        /** The property for getting the name of the language */
        public static final String PROP_NAME = "name";

        /** The property for getting the icon for the language */
        public static final String PROP_ICON = "icon";


        /** The locale corresponding to this language */
        protected final Locale locale;

        /** The name to be shown for this language */
        protected final String name;

        /** The icon to be shown for this language */
        protected final Icon icon;


        /**
         * Creates a new Language instance.
         *
         * @param locale  the locale corresponding to the language
         * @param name  the name to be shown for the language
         * @param icon  the icon to be shown for the language
         */
        public Language(Locale locale, String name, Icon icon) {
            this.locale = locale;
            this.name = name;
            this.icon = icon;
        }

        /**
         * Creates a new Language instance from properties.
         *
         * @param locale  the locale corresponding to the language
         * @param props  the properties containing the additional information
         */
        public Language(Locale locale, Properties props) {
            this.locale = locale;

            String langName = props.getProperty(PROP_NAME);
            this.name = (langName != null) ? langName : locale.getDisplayLanguage();

            String fileName = props.getProperty(PROP_ICON);
            if (fileName == null) {
                this.icon = null;
                return;
            }

            // Attempt to read the icon from a file, if it exists
            File file = new File(fileName);
            if (file.exists()) {
                this.icon = new ImageIcon(fileName);
                return;
            }

            // Otherwise assume it is a resource
            this.icon = new ImageIcon(getClass().getResource(fileName));
        }

        /**
         * Returns the locale corresponding to this language.
         * 
         * @return  the locale corresponding to this language
         */
        public Locale getLocale() {
            return locale;
        }
        
        /**
         * Returns the name to be shown for this language.
         * 
         * @return  the name to be shown for this language
         */
        public String getName() {
            return name;
        }
        
        /**
         * Returns the icon to be shown for this language.
         * 
         * @return  the icon to be shown for this language
         */
        public Icon getIcon() {
            return icon;
        }
    }

    
    /** The locale currently set */
    protected Locale locale = null;

    /** The default locale to be used in case the requested resource is not available in the current locale */
    protected final Locale defaultLoc;

    /** The localized strings for the current locale */
    protected Properties strings = null;
    
    /** The map of properties organized by locale */
    protected HashMap<Locale, Properties> properties = new HashMap<>();

    /** The listeners for any locale change */
    protected HashSet<LocaleChangeListener> listeners = new HashSet<>();

    /** The comparator for locales */
    protected final Comparator<Locale> LOCALE_COMPARATOR = (Locale o1, Locale o2) -> o1.toLanguageTag().compareTo(o2.toLanguageTag());
    
    /** The list of locales handled by this Localizer */
    protected final ArrayList<Locale> locales;

    /** The list of languages handled by this Localizer */
    protected final ArrayList<Language> languages;
    
    
    /**
     * Instantiates a Localizer.
     * 
     * @param resources  the resources for the properties files
     * @param filePath  the path in the file system where the properties files are
     * @param prefix  the prefix of all properties files to be retrieved
     * @param defaultLoc  the default locale (if null, the default JVM locale is used)
     */
    public Localizer(String[] resources, String filePath, String prefix, Locale defaultLoc) {
        this.defaultLoc = (defaultLoc != null) ? defaultLoc : Locale.getDefault();
        locale = defaultLoc;

        HashMap<Locale, LinkedList<URL>> urls = new HashMap<>();

        // Load the properties from the resources
        if (resources != null) {
            for (int i = 0; i < resources.length; ++i) {
                URL res = Localizer.class.getResource(resources[i]);
                if (res == null) continue;
                
                String name = res.getFile();
                int locStart = name.lastIndexOf(".", name.length() - 13);
                if (locStart < 0) continue;
                
                // Determine the locale from the properties file name
                String localeStr = name.substring(locStart + 1, name.length() - 11);
                Locale loc = Locale.forLanguageTag(localeStr);
                if (loc == null) continue;

                // Append the properties file to the list of files for the locale
                LinkedList<URL> files = urls.get(loc);
                if (files == null) {
                    files = new LinkedList<>();
                    urls.put(loc, files);
                }

                files.add(res);
            }
        }
        
        // Load the properties from the file path
        if (filePath != null) {
            File dir = new File(filePath);

            if (dir.isDirectory()) {
                for (File file : dir.listFiles()) {
                    String name = file.getName();

                    // Only consider properties files starting with the given prefix
                    if (!name.endsWith(".properties") || !name.startsWith(prefix)) continue;

                    // Only consider files having name like "<prefix><something>.<locale>.properties"
                    int locStart = name.lastIndexOf(".", name.length() - 13);
                    if (locStart < 0) continue;

                    // Determine the locale from the properties file name
                    String localeStr = name.substring(locStart + 1, name.length() - 11);
                    Locale loc = Locale.forLanguageTag(localeStr);
                    if (loc == null) continue;

                    URL res;
                    try {
                        res = file.toURI().toURL();
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(Localizer.class.getName()).log(Level.SEVERE, null, ex);
                        continue;
                    }

                    // Append the properties file to the list of files for the locale
                    LinkedList<URL> files = urls.get(loc);
                    if (files == null) {
                        files = new LinkedList<>();
                        urls.put(loc, files);
                    }

                    files.add(res);
                }
            }
        }
        
        properties = new HashMap<>();
        urls.keySet().forEach(loc -> {
            Properties props = new Properties();
            
            if (loc != this.defaultLoc && this.defaultLoc != null) {
                LinkedList<URL> files = urls.get(this.defaultLoc);
                if (files != null) try {
                    ExtendedProperties.addProperties(props, files);
                } catch (IOException ex) {
                    Logger.getLogger(Localizer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            LinkedList<URL> files = urls.get(loc);
            if (files != null) {
                try {
                    ExtendedProperties.addProperties(props, files);
                } catch (IOException ex) {
                    Logger.getLogger(Localizer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            properties.put(loc, props);
        });
        
        locales = new ArrayList<>(properties.size());
        locales.addAll(properties.keySet().stream().sorted(LOCALE_COMPARATOR).collect(Collectors.toList()));
        
        if (locales == null) {
            languages = null;
        } else {
            languages = new ArrayList<>(locales.size());
            
            for (Locale loc : locales) {
                Language l = new Language(loc, properties.get(loc));
                languages.add(l);
            }
        }
    }

    /**
     * Returns the text corresponding to the given key for the current locale (if it exists)
     * or alternatively for the default locale.
     * 
     * @param key  the key to be used for retrieving the text
     * @return  the text corresponding to the given key for the current locale (if it exists)
     *          or alternatively for the default locale. If nothing can be found, returns the key itself.
     */
    public String getText(String key) {
        if (strings == null) return key;
        
        return strings.getProperty(key, key);
    }
    
    /**
     * Returns the text corresponding to the given key for the current locale (if it exists)
     * or alternatively for the default locale.
     * 
     * @param key  the key to be used for retrieving the text
     * @param defValue  the default value to be returned in case the key cannot be found anywhere
     * @return  the text corresponding to the given key for the current locale (if it exists)
     *          or alternatively for the default locale. If nothing can be found, returns the default value.
     */
    public String getText(String key, String defValue) {
        if (strings == null) return key;
        
        return strings.getProperty(key, defValue);
    }
    
    /**
     * Adds a listener for the changes to the current locale.
     * 
     * @param listener  the listener to be added
     */
    public void addListener(LocaleChangeListener listener) {
        if (!listeners.contains(listener)) listeners.add(listener);
    }
    
    /**
     * Removes a listener for the changes to the current locale.
     * Nothing happens if the listener is not there.
     * 
     * @param listener  the listener to be removed
     */
    public void removeListener(LocaleChangeListener listener) {
        if (listeners.contains(listener)) listeners.remove(listener);
    }
    
    /**
     * Notifies a local change to all listeners.
     */
    protected void notifyListeners() {
        listeners.forEach(l -> l.localeChanged(locale));
    }
    
    /**
     * Sets the current locale and notifies all listeners.
     * 
     * @param locale  the locale to be set as current
     */
    public void setLocale(Locale locale) {
        setLocale(locale, false);
    }

    /**
     * Sets the current locale and notifies all listeners.
     * 
     * @param locale  the locale to be set as current
     * @param force  it true, the locale change is performed even if the new locale is the same as the old one
     */
    public void setLocale(Locale locale, boolean force) {
        // Don't do anything unless the locale has actually changed or the locale change is forced
        if (!force && locale.equals(this.locale)) return;

        this.locale = locale;
        strings = properties.get(locale);
        notifyListeners();
    }

    /**
     * Returns the current locale.
     * 
     * @return  the current locale
     */
    public Locale getLocale() {
        return locale;
    }
    
    /**
     * Returns the list of supported locales.
     * 
     * @return  the list of supported locales
     */
    public ArrayList<Locale> getLocales() {
        return locales;
    }
    
    /**
     * Returns the list of supported languages.
     * 
     * @return  the list of supported languages
     */
    public ArrayList<Language> getLanguages() {
        return languages;
    }
}
