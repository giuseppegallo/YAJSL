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
package YAJSL.Swing.Components;

import YAJSL.Swing.Beans.TabbedPanel;
import YAJSL.Swing.Application;
import YAJSL.Swing.MousePointerManager;
import YAJSL.Utils.ExtendedProperties;
import YAJSL.Utils.Localizer;
import java.awt.Dimension;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Properties;
import java.util.TreeSet;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * Represents a generic application tab in a tabbed panel.
 * 
 * @author Giuseppe Gallo
 */
public abstract class TabPanel extends TabbedPanel.Tab {

    /**
     * The generic exception to be raised when a tab panel cannot be created.
     */
    public static class TabPanelCreationException extends Exception {
        public TabPanelCreationException(String msg) {
            super(msg);
        }
        
        public TabPanelCreationException(String msg, Exception ex) {
            super(msg, ex);
        }
    }

    
    /** The property for the name of the class for the tab panel */
    public final static String PROP_CLASS = "class";
    
    /** The property for the resource containing the properties for the tab panel */
    public final static String PROP_PROPS = "properties";

    /** The property for the unique name of the tab panel */
    public final static String PROP_NAME = "name";

    /** The property for the resource containing the icon for the tab (when not selected) */
    public final static String PROP_ICON_NORMAL = "icon.normal";

    /** The property for the resource containing the icon for the tab (when selected) */
    public final static String PROP_ICON_SELECTED = "icon.selected";
    
    /** The property for the resource containing the icon for the tab (when not selected), when the mouse hover on it */
    public final static String PROP_ICON_ROLLOVER = "icon.rollover";

    /** The property for the resource containing the icon for the tab (when selected), when the mouse hover on it */
    public final static String PROP_ICON_ROLLOVERSELECTED = "icon.rolloverSelected";

    /** The property for the unique name of the tab panel */
    public final static String LOC_LABEL = "label";

    /** The property for the unique name of the tab panel */
    public final static String LOC_HINT = "hint";

    
    /** The icon to be shown on the tab (when not selected) */
    protected Icon tabIcon = null;
    
    /** The icon to be shown on the tab (when selected) */
    protected Icon tabIconSelected = null;
    
    /** The icon to be shown on the tab (when not selected), when the mouse hovers on it */
    protected Icon tabIconRollover = null;
    
    /** The icon to be shown on the tab (when selected), when the mouse hovers on it */
    protected Icon tabIconRolloverSelected = null;
    
    /** The title to be shown on the tab */
    protected String tabLabel = null;
    
    /** The hint to be shown on the tab */
    protected String tabHint = null;
    
    /** The unique name of the tab panel */
    protected String tabName = null;
    
    /** The localizer to be used with this tab panel */
    protected Localizer loc = null;
    
    /** The application this tab panel is part of */
    protected Application app = null;
    
    /** The properties for this panel */
    protected ExtendedProperties properties = null;

    /** The MousePointerManager for this tab panel */
    protected MousePointerManager mpm = null;
    
    
    /**
     * Allocates a new TabPanel instance.
     */
    public TabPanel() {}
    
    /**
     * Allocates a new TabPanel instance.
     * 
     * @param app  the application the tab panel is part of
     * @param props  the properties to be used for allocating this tab panel
     * @param mpm  the MousePointerManager instance to be used with this panel (a new one is created if null)
     * @throws TabPanelCreationException  in case any issue
     */
    public TabPanel(Properties props, Application app, MousePointerManager mpm) throws TabPanelCreationException {
        properties = new ExtendedProperties(props);
        this.app = app;
        this.loc = (app == null) ? null : app.getLocalizer();
        this.mpm = (mpm == null) ? new MousePointerManager() : mpm;
        
        try {
            tabName = ExtendedProperties.getRequiredStringProperty(props, PROP_NAME);
            
            tabIcon = loadIcon(PROP_ICON_NORMAL);
            tabIconSelected = loadIcon(PROP_ICON_SELECTED);
            tabIconRollover = loadIcon(PROP_ICON_ROLLOVER);
            tabIconRolloverSelected = loadIcon(PROP_ICON_ROLLOVERSELECTED);

            loadTabTexts();
            
        } catch (ExtendedProperties.MissingPropertyException ex) {
            throw new TabPanelCreationException("Unable to create tab panel [" + ((tabName == null) ? "<unknown>" : tabName) + "]", ex);
        }
    }
    
    /**
     * Loads the icon specified by the given property.
     * 
     * @param property  the property name identifying the icon
     * @return  the icon loaded
     */
    private Icon loadIcon(String property) {
        String resource = properties.getProperty(property);
        return (resource == null) ? null : new ImageIcon(getClass().getResource(resource));
    }
    
    /**
     * Retrieves the title and hint for the tab using the localizer.
     */
    private void loadTabTexts() {
        if (loc == null) return;
        
        tabLabel = loc.getText(tabName + "." + LOC_LABEL);
        tabHint = loc.getText(tabName + "." + LOC_HINT);
    }
    
    @Override
    public Icon getTabNormalIcon() {
        return tabIcon;
    }

    @Override
    public Icon getTabSelectedIcon() {
        return tabIconSelected;
    }

    @Override
    public Icon getTabRolloverIcon() {
        return tabIconRollover;
    }

    @Override
    public Icon getTabRolloverSelectedIcon() {
        return tabIconRolloverSelected;
    }

    @Override
    public String getTabLabel() {
        return tabLabel;
    }

    @Override
    public String getTabHint() {
        return tabHint;
    }

    @Override
    public String getTabName() {
        return tabName;
    }

    @Override
    public void localeChanged(Locale newLocale) {
        loadTabTexts();
        initLocale();
    }
    
    /**
     * Allocates a new tab panel.
     * 
     * @param app  the application the tab panel is part of
     * @param properties  the properties to be used for allocating the tab panel
     * @param mpm  the MousePointerManager instance to be used with the panel (a new one is created if null)
     * @return  the tab panel instance
     * @throws TabPanelCreationException  in case any issue
     */
    public static TabPanel allocate(Application app, Properties properties, MousePointerManager mpm) throws TabPanelCreationException {
        
        TabPanel panel;
        
        try {
            String className = ExtendedProperties.getRequiredStringProperty(properties, PROP_CLASS);
            
            // Prepare the properties for the panel
            Properties props;
            String propResource = properties.getProperty(PROP_PROPS);
            
            if (propResource != null) {
                props = new Properties();
                props.load(TabPanel.class.getResourceAsStream(propResource));
            } else {
                props = properties;
            }
            
            ExtendedProperties.addProperties(props, app.getProperties());

            // Instantiate the panel
            Class<?> cl = Class.forName(className);
            Object instance = cl.getConstructor(
                    new Class[]{ Properties.class, Application.class, MousePointerManager.class })
                    .newInstance(new Object[]{ props, app, mpm });
            if (!(instance instanceof TabPanel)) {
                throw new ClassNotFoundException("Panels must be subclasses of TabPanel");
            }
            
            panel = (TabPanel)instance;
            
        } catch (ExtendedProperties.MissingPropertyException | ClassNotFoundException | NoSuchMethodException |
                 SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException |
                InvocationTargetException | IOException ex) {
            throw new TabPanelCreationException("Unable to create tab panel", ex);
        }
        
        return panel;
    }

    /**
     * Initializes the tabs in a tabbed panel from properties.
     * 
     * @param props  the properties to be used for initializing the tabs
     * @param app  the application instance
     * @param panel  the tabbed panel
     * @throws YAJSL.Swing.Components.TabPanel.TabPanelCreationException  in case of any issue
     */
    public static void initTabs(Properties props, Application app, TabbedPanel panel) throws TabPanelCreationException {
        Properties filtered = ExtendedProperties.filter(props, "tab.");
        TreeSet<String> keys = new TreeSet<>(filtered.stringPropertyNames());
        
        for (String key : keys) {
            if (!key.endsWith(PROP_CLASS)) continue;
            String prefix = key.substring(0, key.lastIndexOf(".") + 1);
            
            TabPanel tab = allocate(app, ExtendedProperties.filter(filtered, prefix), panel.getMousePointerManager());
            panel.addTab(tab);
        }
    }
    
    /**
     * Sets the preferred horizontal size of a component.
     * 
     * @param comp  the component
     * @param size  the preferred horizontal size
     */
    public static void setPreferredHorizontalSize(JComponent comp, int size) {
        Dimension d = comp.getPreferredSize();
        d.width = size;
        comp.setPreferredSize(d);
    }
    
    /**
     * Loads the data for all tabs in a panel.
     * 
     * @param panel  the panel
     * @throws Exception  in case of any issue
     */
    public static void loadData(TabbedPanel<TabbedPanel.Tab> panel) throws Exception {
        for (TabbedPanel.Tab t : panel.getTabs()) {
            if (!(t instanceof TabPanel)) continue;
            ((TabPanel)t).loadData();
        }
    }
    
    /**
     * Saves the data for all tabs in a panel.
     * 
     * @param panel  the panel
     * @throws Exception  in case of any issue
     */
    public static void saveData(TabbedPanel<TabbedPanel.Tab> panel) throws Exception {
        for (TabbedPanel.Tab t : panel.getTabs()) {
            if (!(t instanceof TabPanel)) continue;
            ((TabPanel)t).saveData();
        }
    }
    
    /**
     * Initializes (and re-initializes when needed) anything depending on the locale.
     */
    public abstract void initLocale();
    
    /**
     * Loads the data needed by the tab.
     * 
     * @throws Exception  in case of any issue
     */
    public abstract void loadData() throws Exception;
    
    /**
     * Saves the data used by the tab.
     * 
     * @throws Exception  in case of any issue
     */
    public abstract void saveData() throws Exception;
}
