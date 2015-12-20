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

import java.awt.Dimension;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Represents the common interface for all windows in my GUI framework.
 * 
 * @author Giuseppe Gallo
 */
public abstract class Window extends JFrame {

    /**
     * Listener for window events used to intercept windows closure and 
     * perform shutdown operations.
     */
    private class CustomWindowListener implements WindowListener {
        /** The main window */
        private Window window = null;

        /**
         * Allocates the listener.
         * 
         * @param w  the window
         */
        public CustomWindowListener(Window w) {
            window = w;
        }
        
        /**
         * Reacts to the window closure event.
         * 
         * @param e  the window event
         */
        @Override
        public void windowClosed(WindowEvent e) {
            window.shutdown();
        }

        /**
         * Reacts to the window closing event.
         * 
         * @param e  the window event
         */
        @Override
        public void windowClosing(WindowEvent e) {
            window.dispose();
        }

        /**
         * Reacts to the "window opened" event.
         * Nothing is done in this case.
         * 
         * @param e  the window event
         */
        @Override
        public void windowOpened(WindowEvent e) { /* Nothing to do */ }

        /**
         * Reacts to the "window iconified" event.
         * Nothing is done in this case.
         * 
         * @param e  the window event
         */
        @Override
        public void windowIconified(WindowEvent e) { /* Nothing to do */ }

        /**
         * Reacts to the "window deiconified" event.
         * Nothing is done in this case.
         * 
         * @param e  the window event
         */
        @Override
        public void windowDeiconified(WindowEvent e) { /* Nothing to do */ }

        /**
         * Reacts to the "window activated" event.
         * Nothing is done in this case.
         * 
         * @param e  the window event
         */
        @Override
        public void windowActivated(WindowEvent e) { /* Nothing to do */ }

        /**
         * Reacts to the "window deactivated" event.
         * Nothing is done in this case.
         * 
         * @param e  the window event
         */
        @Override
        public void windowDeactivated(WindowEvent e) { /* Nothing to do */ }
    }
    
    /** The manager for the mouse pointer */
    protected MousePointerManager mpm = new MousePointerManager();

    /** The application instance */
    protected Application app = null;


    /**
     * Instantiates the window.
     *
     * @param app  the application instance
     */
    public Window(Application app) {
        this.app = app;
        addWindowListener(new CustomWindowListener(this));
    }

    /**
     * Performs post-initialization settings.
     * In particular:
     * <ul>
     *    <li>Initialize the mouse pointer manager</li>
     *    <li>Sets the title of the window</li>
     * </ul>
     * It MUST be called by extended classes after components initialization.
     */
    protected void postInit() {
        initMousePointer();
        setTitle(getWindowTitle());
    }

    /**
     * Adds a component to the list of components managed by the mouse
     * pointer manager facility offered by this form.
     *
     * @param comp  the component to be added to the mouse pointer manager
     */
    public void addMousePointerManagerComponent(JComponent comp) {
        mpm.add(comp);
    }
    
    /**
     * Returns the mouse pointer manager used by this window.
     * 
     * @return  the mouse pointer manager used by this window
     */
    public MousePointerManager getMousePointerManager() {
        return mpm;
    }

    /**
     * Resizes and repositions this window.
     * 
     * @param maximized  if true, the window is maximized
     * @param width  the new width of the window
     * @param height  the new height of the window
     */
    public void resize(boolean maximized, int width, int height) {
        setSize(width, height);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = getSize();
        int windowX = Math.max(0, (screenSize.width  - windowSize.width )/2);
        int windowY = Math.max(0, (screenSize.height - windowSize.height)/2);
        setLocation(windowX, windowY);
        if (maximized) {
            setExtendedState(MAXIMIZED_BOTH);
        }
    }
    
    /**
     * Add the components to the mouse pointer manager supplied by the
     * parent class.
     * To be implemented by each subclass.
     */
    protected abstract void initMousePointer();

    /**
     * Returns the title for this window.
     *
     * @return  the title of this window
     */
    protected abstract String getWindowTitle();
    
    /**
     * Performs any shutdown action.
     */
    protected void shutdown() {
        // Nothing to do by default
    }
}
