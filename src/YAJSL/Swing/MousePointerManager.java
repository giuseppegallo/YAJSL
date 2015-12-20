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

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;

/**
 * Represents a manager for the mouse pointer.
 * It allows the following:
 * <ul>
 *    <li>Automatically set the specific cursor (by default {@link Cursor.HAND_CURSOR})
 *        for a list of components when they are enabled</li>
 *    <li>Automatically set the specific cursor (by default {@link Cursor.DEFAULT_CURSOR})
 *        for a list of components when they are disabled</li>
 *    <li>Automatically set the specific cursor (by default {@link Cursor.WAIT_CURSOR})
 *        until the supplied thread completes its execution</li>
 * </ul>
 * 
 * @author Giuseppe Gallo
 */
public class MousePointerManager {
    /** The cursor to be used if the component is enabled */
    protected Cursor enabledCursor = null;

    /** The cursor to be used if the component is disabled */
    protected Cursor disabledCursor = null;
    
    /** The cursor to be used while waiting */
    protected Cursor waitCursor = null;

    /** The property change listeners for each components */
    protected HashMap<JComponent, PropertyChangeListener> listeners = new HashMap<>();
    
    
    /**
     * Default contructor.
     * Default cursors are used:
     * <ul>
     *    <li>Enabled cursor =  {@link Cursor.HAND_CURSOR}</li>
     *    <li>Disabled cursor = {@link Cursor.DEFAULT_CURSOR}</li>
     *    <li>Wait cursor = {@link Cursor.WAIT_CURSOR}</li>
     * </ul>
     */
    public MousePointerManager() {
        this(Cursor.HAND_CURSOR, Cursor.DEFAULT_CURSOR, Cursor.WAIT_CURSOR);
    }

    /**
     * Constructor which allows specifying custom cursors.
     *
     * @param enabledCursor    the integer value representing the cursor to be used
     *                         when the component is enabled
     * @param disabledCursor   the integer value representing the cursor to be used
     *                         when the component is disabled
     * @param waitCursor   the integer value representing the cursor to be used
     *                     when the component is waiting for a thread
     */
    public MousePointerManager(int enabledCursor, int disabledCursor, int waitCursor) {
        this.enabledCursor = Cursor.getPredefinedCursor(enabledCursor);
        this.disabledCursor = Cursor.getPredefinedCursor(disabledCursor);
        this.waitCursor = Cursor.getPredefinedCursor(waitCursor);
    }
    
    /**
     * Constructor which allows specifying custom cursors.
     *
     * @param enabledCursor    the cursor to be used when the component is enabled
     * @param disabledCursor   the cursor to be usedwhen the component is disabled
     * @param waitCursor   the cursor to be used when the component is waiting for a thread
     */
    public MousePointerManager(Cursor enabledCursor, Cursor disabledCursor, Cursor waitCursor) {
        this.enabledCursor = enabledCursor;
        this.disabledCursor = disabledCursor;
        this.waitCursor = waitCursor;
    }

    /**
     * Sets the cursor for the status Enabled.
     * 
     * @param enabledCursor  the cursor for the status Enabled
     */
    public void setEnabledCursor(Cursor enabledCursor) {
        this.enabledCursor = enabledCursor;
    }

    /**
     * Sets the cursor for the status Disabled.
     *
     * @param disabledCursor  the cursor for the status Disabled
     */
    public void setDisabledCursor(Cursor disabledCursor) {
        this.disabledCursor = disabledCursor;
    }

    /**
     * Sets the cursor for the status Wait.
     *
     * @param waitCursor  the cursor for the status Wait
     */
    public void setWaitCursor(Cursor waitCursor) {
        this.waitCursor = waitCursor;
    }

    /**
     * Returns the cursor for the status Enabled.
     * 
     * @return  the cursor for the status Enabled
     */
    public Cursor getEnabledCursor() {
        return enabledCursor;
    }

    /**
     * Returns the cursor for the status Disabled.
     *
     * @return  the cursor for the status Disabled
     */
    public Cursor getDisabledCursor() {
        return disabledCursor;
    }

    /**
     * Returns the cursor for the status Wait.
     *
     * @return  the cursor for the status Wait
     */
    public Cursor getWaitCursor() {
        return waitCursor;
    }
    
    /**
     * Adds a component to the ones managed by this MousePointerManager.
     * Does not do anything is the component is already managed by this MousePointerManager.
     *
     * @param comp   the component to be added
     */
    public void add(JComponent comp) {
        if (listeners.containsKey(comp)) return;
        
        if (comp.isEnabled()) {
            comp.setCursor(enabledCursor);
        }

        PropertyChangeListener listener = (PropertyChangeEvent evt) -> {
            JComponent src = (JComponent) evt.getSource();
            if (src.isEnabled()) {
                src.setCursor(enabledCursor);
            } else {
                src.setCursor(disabledCursor);
            }
        };

        listeners.put(comp, listener);
        comp.addPropertyChangeListener("enabled", listener);
    }

    /**
     * Removes a component from this MousePointerManager.
     * 
     * @param comp  the component to be removed
     */
    public void remove(JComponent comp) {
        PropertyChangeListener listener = listeners.get(comp);
        if (listener == null) return;
        
        comp.removePropertyChangeListener("enabled", listener);
        listeners.remove(comp);
    }

    /**
     * Migrates all component currently manager by this MousePointerManager to another MousePointerManager.
     * 
     * @param mpm  the new MousePointerManager
     */
    public void migrate(MousePointerManager mpm) {
        listeners.keySet().forEach(comp -> {
            remove(comp);
            mpm.add(comp);
        });
        
        listeners.clear();
    }
    
    /**
     * Refreshes the pointer for the given component, based on ints current
     * status (enabled / disabled).
     *
     * @param comp   the component for which the mouse pointer needs to be refreshed
     */
    public void refresh(JComponent comp) {
        if (comp.isEnabled()) {
            comp.setCursor(enabledCursor);
        } else {
            comp.setCursor(disabledCursor);
        }
    }

    /**
     * Starts the given thread, wait for its completion and in the meantime sets the
     * wait cursor for the supplied component.
     * The original mouse pointer is restored when the thread completes its execution.
     *
     * @param comp   the component for which the cursor has to be changed
     * @param thr   the thread to be executed
     */
    public void waitCursorForAction(JComponent comp, Thread thr) {
        waitCursorForAction(comp, thr, false);
    }

    /**
     * Starts the given thread, wait for its completion and in the meantime sets the
     * wait cursor for the supplied component.
     * The original mouse pointer is restored when the thread completes its execution.
     * It is also possible to disabled the component while the action is performed.
     *
     * @param comp   the component for which the cursor has to be changed
     * @param thr   the thread to be executed
     * @param disable   specifies whether disabling or not the component while the action
     *                  is performed
     */
    public void waitCursorForAction(JComponent comp, Thread thr, boolean disable) {
        Cursor orig = comp.getCursor();
        boolean status = comp.isEnabled();
        if (disable) comp.setEnabled(false);
        comp.setCursor(waitCursor);
        thr.start();
        try {
            thr.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(MousePointerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (disable) comp.setEnabled(status);
        comp.setCursor(orig);
    }
}
