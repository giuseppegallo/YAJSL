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
package YAJSL.Swing;

import YAJSL.Swing.Components.ErrorDialog;
import YAJSL.Utils.ExtendedProperties;
import YAJSL.Utils.Localizer;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Represents a generic Java Swing application.
 * 
 * @author Giuseppe Gallo
 */
public abstract class Application {

    public static class PreparationException extends Exception {
        private PreparationException(String text) {
            super(text);
        }
    }
    
    public static class SingleInstanceException extends PreparationException {
        private SingleInstanceException(Class c) {
            super("Only 1 instance is allowed for class " + c.getName());
        }
    }
    
    /** The property defining the path to "external" languages. No external languages are loaded unless defined. */
    protected final static String PROP_LANGUANGES_PATH = "languages.path";
    
    /** The file name prefix of the "external" language properties files */
    protected final static String PROP_LANGUANGES_PREFIX = "languages.prefix";
    
    /** The property defining the tag for the default locale. The JVM default locale is used unless defined. */
    protected final static String PROP_LANGUANGES_DEFAULT = "languages.default";
    
    /** The property defining if the current locale needs to be automatically set to the default locale */
    protected final static String PROP_LANGUANGES_INIT = "languages.init";
    
    /** The property prefix for the list of "internal" languages (defined in a resource) */
    protected final static String PROP_LANGUANGES_LIST = "languages.language.";
    
    /** The property defining the class of the main window for this application */
    protected final static String PROP_MAIN_WINDOW_CLASS = "mainWindow.class";
    
    /** The property defining the name of the external file containing any additional properties (optional) */
    protected final static String PROP_EXTERNAL_FILE = "properties.external";
    
    /** The property defining the name of the log file */
    protected final static String PROP_LOG_FILE = "log.file";

    /** The property for the error when instantiating the main window */
    protected final static String MSG_MAIN_WINDOW_ERROR = "mainWindow.error.instantiate";
    
    
    /** The instance of this application */
    protected static Application INSTANCE = null;

    
    /** The localization helper for the application */
    protected Localizer localizer = null;
    
    /** The main properties for this application */
    protected Properties properties = null;
    
    /** The main window for this instance */
    protected Window mainWindow = null;

    /**
     * Instantiates the application.
     */
    protected Application() {
        setLookAndFeel();
    }
    
    /**
     * Initializes the application.
     * 
     * @throws Exception  in case of any issue 
     */
    public void init() throws Exception {
        properties = loadProperties();
        if (properties == null) {
            throw new PreparationException("No properties have been loaded");
        }
        
        initLog();
        initLanguages();
    }

    /**
     * Initializes the log.
     */
    protected void initLog() {
        // Set logging
        String logFileName = properties.getProperty(PROP_LOG_FILE);
        if (logFileName != null) {
            try {
                Logger rootLogger = Logger.getLogger("");
                FileHandler handler = new FileHandler(logFileName, true);
                handler.setFormatter(new SimpleFormatter());
                rootLogger.addHandler(handler);
            } catch (IOException | SecurityException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Initializes the localization.
     */
    private void initLanguages() {
        String extLanguagesPath = properties.getProperty(PROP_LANGUANGES_PATH);
        
        // Determine the default locale
        String defaultLocaleTag = properties.getProperty(PROP_LANGUANGES_DEFAULT);
        Locale defaultLocale = null;
        if (defaultLocaleTag != null) {
            defaultLocale = Locale.forLanguageTag(defaultLocaleTag);
        }
        
        // Determine the "internal" languages
        Collection<String> keys = ExtendedProperties.getSortedKeys(properties, PROP_LANGUANGES_LIST);
        final String[] langs = new String[keys.size()];
        int i = 0;
        for (String key : keys) {
            langs[i++] = properties.getProperty(key);
        }
        
        // Initialize the localization helper
        String prefix = properties.getProperty(PROP_LANGUANGES_PREFIX, "");
        localizer = new Localizer(langs, extLanguagesPath, prefix, defaultLocale);
        
        // Set the current locale needs to the default locale if requested
        if (ExtendedProperties.getBooleanPropertySilent(properties, PROP_LANGUANGES_INIT, false)) 
            localizer.setLocale(localizer.getLocale(), true);
    }
    
    /**
     * Returns the localization helper for this application.
     * 
     * @return  the localization helper for this application
     */
    public Localizer getLocalizer() {
        return localizer;
    }
    
    /**
     * Returns the only allowed instance of the application.
     * 
     * @return  the only allowed instance of the application
     */
    public static Application getInstance() {
        return INSTANCE;
    }
    
    /**
     * Returns the instance of the main window for this application.
     *
     * @return  the instance of the main window for this application
     */
    public Window getMainWindow() {
        return mainWindow;
    }
    
    /**
     * Returns the properties for this application instance.
     * 
     * @return  the properties for this application instance
     */
    public Properties getProperties() {
        return properties;
    }
    
    /**
     * Instantiates and shows the main window for this application.
     */
    protected final void showMainWindow() {
        final String thisClassName = this.getClass().getName();
        
        java.awt.EventQueue.invokeLater(() -> {
            try {
                String className = ExtendedProperties.getRequiredStringProperty(properties, PROP_MAIN_WINDOW_CLASS);
                Class cl = Class.forName(className);
                java.lang.reflect.Constructor constructor = cl.getConstructor(new Class[]{Application.class});
                mainWindow = (Window) constructor.newInstance(new Object[]{INSTANCE});
                
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException |
                    IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                    ExtendedProperties.MissingPropertyException ex) {
                Logger.getLogger(thisClassName).log(Level.SEVERE, null, ex);
                
                ErrorDialog.showDialog(
                        null,
                        localizer.getText(MSG_MAIN_WINDOW_ERROR + ErrorDialog.SUFFIX_ERROR_TITLE),
                        localizer.getText(MSG_MAIN_WINDOW_ERROR + ErrorDialog.SUFFIX_ERROR_DESCR),
                        ex, null, localizer,
                        properties.getProperty(ErrorDialog.CLASS_NAME)
                    );
                
                System.exit(1);
            }
            
            preShow();
            
            mainWindow.setTitle(mainWindow.getWindowTitle());
            mainWindow.setVisible(true);
        });
    }
    
    /**
     * Sets the look and feel.
     */
    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    /**
     * Loads the properties from a resource (that can optionally include an external file).
     * 
     * @param resource  the resource containing the properties.
     * @returns  the properties loaded
     * @throws IOException  in case of any issue loading the properties
     */
    public static Properties loadPropertiesFromResource(String resource) throws IOException {
        Properties props = new ExtendedProperties(Application.class.getResourceAsStream(resource));
        String external = props.getProperty(PROP_EXTERNAL_FILE);
        
        if (external != null) {
            ExtendedProperties.addProperties(props, external);
        }
        
        return props;
    }
    
    /**
     * Instantiates and initializes the application.
     * 
     * @param c  the actual class of the application to be instantiated
     * @param args  the command line arguments
     * @return  the application instance
     * @throws Exception  in case of any issue
     */
    public static Application launch(Class<? extends Application> c, String[] args) throws Exception {
        if (INSTANCE != null) {
            throw new SingleInstanceException(Application.class);
        }
        
        Constructor<?> con = c.getConstructor(new Class[] {});
        Application app = (Application) con.newInstance(new Object[] {});

        INSTANCE = app;
        
        app.init();
        app.startup();
        app.showMainWindow();
        
        return app;
    }
    
    /**
     * Loads the main properties for this application.
     * 
     * In addition to the custom properties used by any subclass, the following
     * ones are used:
     * <li>{@link #PROP_LANGUANGES_PATH} = {@value #PROP_LANGUANGES_PATH}</li>
     * <li>{@link #PROP_LANGUANGES_PREFIX} = {@value #PROP_LANGUANGES_PREFIX}</li>
     * <li>{@link #PROP_LANGUANGES_DEFAULT} = {@value #PROP_LANGUANGES_DEFAULT}</li>
     * <li>{@link #PROP_LANGUANGES_INIT} = {@value #PROP_LANGUANGES_INIT}</li>
     * <li>{@link #PROP_LANGUANGES_LIST} = {@value #PROP_LANGUANGES_LIST}</li>
     * <li>{@link #PROP_MAIN_WINDOW_CLASS} = {@value #PROP_MAIN_WINDOW_CLASS}</li>
     * <li>{@link #PROP_EXTERNAL_FILE} = {@value AbstractMethodErrorPROP_EXTERNAL_FILE}</li>
     * 
     * @return  the main properties for this application
     */
    public abstract Properties loadProperties() throws Exception;
    
    /**
     * Executes the startup logic for the application.
     * 
     * @throws Exception  in case of any issue
     */
    public abstract void startup() throws Exception;
    
    /**
     * Executes the shutdown logic for the application.
     * 
     * @throws Exception  in case of any issue
     */
    public abstract void shutdown() throws Exception;
    
    /**
     * Executes the logic needed just before making main window visible.
     */
    public abstract void preShow();
}
