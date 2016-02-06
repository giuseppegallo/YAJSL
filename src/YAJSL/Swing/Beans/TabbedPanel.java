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
package YAJSL.Swing.Beans;

import YAJSL.Swing.Beans.TabbedPanel.Tab;
import YAJSL.Swing.MousePointerManager;
import YAJSL.Utils.Localizer;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Represents a better tabbed pane.
 * 
 * @param <T>  the specific type of tab handled by the tabbed pane
 * 
 * @author Giuseppe Gallo
 */
public class TabbedPanel<T extends Tab> extends JTabbedPane implements Localizer.LocaleChangeListener {

    /**
     * The parent class for all tabs in this tabbed pane.
     */
    public abstract static class Tab extends JPanel implements Localizer.LocaleChangeListener {
        /**
         * Returns the "normal" icon for this tab.
         * 
         * @return  the "normal" icon for this tab
         */
        public abstract Icon getTabNormalIcon();
        
        /**
         * Returns the "selected" icon for this tab.
         * 
         * @return  the "selected" icon for this tab
         */
        public abstract Icon getTabSelectedIcon();
        
        /**
         * Returns the "rollover" (not selected) icon for this tab.
         * 
         * @return  the "rollover" (not selected) icon for this tab
         */
        public abstract Icon getTabRolloverIcon();
        
        /**
         * Returns the "rollover" (and selected) icon for this tab.
         * 
         * @return  the "rollover" (and selected) icon for this tab
         */
        public abstract Icon getTabRolloverSelectedIcon();
        
        /**
         * Returns the title (label) for this tab.
         * 
         * @return  the title (label) for this tab
         */
        public abstract String getTabLabel();
        
        /**
         * Returns the hint for this tab.
         * 
         * @return  the hint for this tab
         */
        public abstract String getTabHint();
        
        /**
         * Returns the unique name of this tab.
         * 
         * @return  the unique name of this tab
         */
        public abstract String getTabName();
    }
    
    /**
     * The event handler for the mouse and the selection.
     */
    private class TabbedPanelEventHandler implements MouseMotionListener, MouseListener, ChangeListener {
        /** The old tab on which the mouse was positioned */
        private T old = null;

        /** The old tab selected */
        private T oldSelected = null;

        /**
         * Updates the color of the text and the icon of the tabs (old and new) of the tabbed panel.
         * 
         * @param tab  the new tab on which the mouse hovers (null = none)
         */
        private void updateTabRollovers(T tab) {
            if (tab == old) return;

            if (old != null) {
                JLabel label = tabLabels.get(old);
                boolean selected = (getSelectedTab() == old);
                label.setFont((selected) ? labelSelectedFont : labelFont);
                label.setForeground((selected) ? labelSelectedColor : labelColor);
                label.setIcon((selected) ? old.getTabSelectedIcon() : old.getTabNormalIcon());
            }

            old = tab;

            if (old != null) {
                JLabel label = tabLabels.get(old);
                boolean selected = (getSelectedTab() == old);
                label.setFont((selected) ? labelRolloverSelectedFont : labelRolloverFont);
                label.setForeground((selected) ? labelSelectedColor : labelColor);
                label.setIcon((selected) ? old.getTabRolloverSelectedIcon() : old.getTabRolloverIcon());
            }
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            // Nothing to do
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            int idx = indexAtLocation(e.getX(), e.getY());
            updateTabRollovers((idx < 0) ? null : tabs.get(idx));
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // Nothing to do
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // Nothing to do
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // Nothing to do
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            int idx = indexAtLocation(e.getX(), e.getY());
            updateTabRollovers((idx < 0) ? null : tabs.get(idx));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            updateTabRollovers(null);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            T tab = getSelectedTab();
            
            if (oldSelected != null) {
                JLabel label = tabLabels.get(oldSelected);
                label.setFont(labelFont);
                label.setForeground(labelColor);
                label.setIcon(oldSelected.getTabNormalIcon());
            }
            
            oldSelected = tab;
            
            if (oldSelected != null) {
                JLabel label = tabLabels.get(oldSelected);
                label.setFont((old == oldSelected) ? labelRolloverSelectedFont : labelSelectedFont);
                label.setForeground((old == oldSelected) ? labelRolloverSelectedColor : labelSelectedColor);
                label.setIcon((old == oldSelected) ? oldSelected.getTabRolloverSelectedIcon() : oldSelected.getTabSelectedIcon());
            }
        }
    }

    /** The mouse pointer manager for the tabbed pane */
    protected MousePointerManager mpm = new MousePointerManager();
    
    /** The normal color of the labels (when not selected) */
    protected Color labelColor = Color.BLACK;

    /** The normal color of the labels (when selected) */
    protected Color labelSelectedColor = Color.BLUE;
    
    /** The color of the labels (when not selected) when the mouse hovers over them */
    protected Color labelRolloverColor = Color.BLACK;
    
    /** The color of the labels (when selected) when the mouse hovers over them */
    protected Color labelRolloverSelectedColor = Color.BLUE;
    
    /** The normal font of the labels (when not selected) */
    protected Font labelFont = getFont().deriveFont(Font.BOLD);
    
    /** The normal font of the labels (when selected) */
    protected Font labelSelectedFont = labelFont;
    
    /** The font of the labels (when not selected) when the mouse hovers over them */
    protected Font labelRolloverFont;
    
    /** The font of the labels (when selected) when the mouse hovers over them */
    protected Font labelRolloverSelectedFont;
    
    /** The tabs organized by name */
    protected HashMap<String, T> tabsByName = new HashMap<>();
    
    /** The tab instances (in the right order) */
    protected ArrayList<T> tabs = new ArrayList<>();
    
    /** The labels associated to each tab */
    protected HashMap<T, JLabel> tabLabels = new HashMap<>();
    
    /** The index for each tab */
    protected HashMap<T, Integer> tabIndexes = new HashMap<>();
    
    
    /**
     * Creates the new TabbedPane instance.
     */
    public TabbedPanel() {
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        labelRolloverFont = labelFont.deriveFont(attributes);
        labelRolloverSelectedFont = labelRolloverFont;
        
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setTabPlacement(JTabbedPane.LEFT);
        
        TabbedPanelEventHandler h = new TabbedPanelEventHandler();
        addMouseMotionListener(h);
        addMouseListener(h);
        addChangeListener(h);
    }

    /**
     * Returns the current mouse pointer manager for this tabbed pane.
     * 
     * @return  the current mouse pointer manager for this tabbed pane
     */
    public MousePointerManager getMousePointerManager() {
        return mpm;
    }
    
    /**
     * Sets the mouse pointer manager for this tabbed pane.
     * 
     * @param mpm  the mouse pointer manager to be used for this tabbed pane
     */
    public void setMousePointerManager(MousePointerManager mpm) {
        this.mpm.migrate(mpm);
        this.mpm = mpm;
    }

    /**
     * Returns the normal color of the labels (when not selected).
     * 
     * @return  the normal color of the labels (when not selected)
     */
    public Color getLabelColor() {
        return labelColor;
    }

    /**
     * Sets the normal color of the labels (when not selected).
     * 
     * @param color  the normal color of the labels (when not selected)
     */
    public void setLabelColor(Color color) {
        labelColor = color;
    }

    /**
     * Returns the normal color of the labels (when selected).
     * 
     * @return  the normal color of the labels (when selected)
     */
    public Color getLabelSelectedColor() {
        return labelSelectedColor;
    }

    /**
     * Sets the normal color of the labels (when selected).
     * 
     * @param color  the normal color of the labels (when selected)
     */
    public void setLabelSelectedColor(Color color) {
        labelSelectedColor = color;
    }

    /**
     * Returns the color of the labels (when not selected) when the mouse hovers over them.
     * 
     * @return  the color of the labels (when not selected) when the mouse hovers over them
     */
    public Color getLabelRolloverColor() {
        return labelRolloverColor;
    }

    /**
     * Sets the color of the labels (when not selected) when the mouse hovers over them.
     * 
     * @param color  the color of the labels (when not selected) when the mouse hovers over them
     */
    public void setLabelRolloverColor(Color color) {
        labelRolloverColor = color;
    }

    /**
     * Returns the color of the labels (when selected) when the mouse hovers over them.
     * 
     * @return  the color of the labels (when selected) when the mouse hovers over them
     */
    public Color getLabelRolloverSelectedColor() {
        return labelRolloverColor;
    }

    /**
     * Sets the color of the labels (when selected) when the mouse hovers over them.
     * 
     * @param color  the color of the labels (when selected) when the mouse hovers over them
     */
    public void setLabelRolloverSelectedColor(Color color) {
        labelRolloverSelectedColor = color;
    }

    /**
     * Returns the normal font of the labels (when not selected).
     * 
     * @return  the normal font of the labels (when not selected)
     */
    public Font getLabelFont() {
        return labelFont;
    }

    /**
     * Sets the normal font of the labels (when not selected).
     * 
     * @param font  the normal font of the labels (when not selected)
     */
    public void setLabelFont(Font font) {
        labelFont = font;
    }

    /**
     * Returns the normal font of the labels (when selected).
     * 
     * @return  the normal font of the labels (when selected)
     */
    public Font getLabelSelectedFont() {
        return labelSelectedFont;
    }

    /**
     * Sets the normal font of the labels (when selected).
     * 
     * @param font  the normal font of the labels (when selected)
     */
    public void setLabelSelectedFont(Font font) {
        labelSelectedFont = font;
    }

    /**
     * Returns the font of the labels (when not selected) when the mouse hovers over them.
     * 
     * @return  the font of the labels (when not selected) when the mouse hovers over them
     */
    public Font getLabelRolloverFont() {
        return labelRolloverFont;
    }

    /**
     * Sets the font of the labels (when not selected) when the mouse hovers over them.
     * 
     * @param font  the font of the labels (when not selected) when the mouse hovers over them
     */
    public void setLabelRolloverFont(Font font) {
        labelRolloverFont = font;
    }

    /**
     * Returns the font of the labels (when selected) when the mouse hovers over them.
     * 
     * @return  the font of the labels (when selected) when the mouse hovers over them
     */
    public Font getLabelRolloverSelectedFont() {
        return labelRolloverFont;
    }

    /**
     * Sets the font of the labels (when selected) when the mouse hovers over them.
     * 
     * @param font  the font of the labels (when selected) when the mouse hovers over them
     */
    public void setLabelRolloverSelectedFont(Font font) {
        labelRolloverSelectedFont = font;
    }

    @Override
    public void localeChanged(Locale newLocale) {
        tabs.forEach(t -> {
            t.localeChanged(newLocale);
            updateTabLabel(t);
        });
    }

    /**
     * Adds a tab to the tabbed pane.
     * 
     * @param tab  the tab to be added
     */
    public void addTab(T tab) {
        // Enclose the tab in a scroll pane
        JScrollPane scroll = new JScrollPane();
        scroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setViewportView(tab);
        
        // Create the label for the tab 
        JLabel label = new JLabel();
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        
        // Add the tab and the label to the data structures
        int idx = tabs.size();
        tabsByName.put(tab.getTabName(), tab);
        tabs.add(tab);
        tabIndexes.put(tab, idx);
        tabLabels.put(tab, label);
        
        // Add the tab to the tabbed pane
        addTab(null, null, scroll);
        
        // Initialize the label for the tab and sets it
        updateTabLabel(tab);
        setTabComponentAt(idx, label);
        
        // Add components to the mouse pointer manager
        mpm.add(label);
        mpm.add(scroll.getHorizontalScrollBar());
        mpm.add(scroll.getVerticalScrollBar());
    }

    /**
     * Updates the label for the given tab.
     * 
     * @param tab  the tab for which the label needs to be updated
     */
    protected void updateTabLabel(T tab) {
        JLabel label = tabLabels.get(tab);
        Integer idx = tabIndexes.get(tab);
        if (label == null || idx == null) return;
        
        boolean selected = (getSelectedTab() == tab);
        
        label.setFont((selected) ? labelSelectedFont : labelFont);
        label.setText(tab.getTabLabel());
        label.setForeground((selected) ? labelSelectedColor : labelColor);
        label.setIcon((selected) ? tab.getTabSelectedIcon() : tab.getTabNormalIcon());
        setToolTipTextAt(idx, tab.getTabHint());
    }

    /**
     * Sets the selected tab using the tab index.
     * 
     * @param idx  the index of the tab to be selected
     */
    public void setSelectedTab(int idx) {
        setSelectedIndex(idx);
    }

    /**
     * Sets the selected tab using the tab object.
     * 
     * @param tab  the tab object to be selected
     */
    public void setSelectedTab(T tab) {
        if (!tabIndexes.containsKey(tab)) return;
        setSelectedTab(tabIndexes.get(tab));
    }
    
    /**
     * Sets the selected tab by name.
     * 
     * @param name  the name of the tab to be selected
     */
    public void setSelectedTab(String name) {
        if (!tabsByName.containsKey(name)) return;
        setSelectedTab(tabsByName.get(name));
    }
    
    /**
     * Returns the selected tab.
     * 
     * @return  the selected tab.
     */
    public T getSelectedTab() {
        return tabs.get(getSelectedIndex());
    }
    
    /**
     * Returns the tab with the given name.
     * 
     * @param name  the name of the tab
     * @return  the tab with the given name
     */
    public T getTab(String name) {
        return tabsByName.get(name);
    }
    
    /**
     * Returns the list of all tabs.
     * 
     * @return  the list of all tabs
     */
    public Collection<T> getTabs() {
        return tabs;
    }
}
