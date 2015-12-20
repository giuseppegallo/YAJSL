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

import YAJSL.Swing.MousePointerManager;
import YAJSL.Utils.Localizer;
import YAJSL.Utils.Localizer.LocaleChangeListener;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

/**
 * A generic dialog.
 * 
 * @author Giuseppe Gallo
 */
public abstract class GenericDialog extends javax.swing.JDialog implements LocaleChangeListener {

    /** The suffix for the property defining the localized title for the dialog */
    public final static String SUFFIX_TITLE = ".title";
    
    /** The localizer used by this dialog */
    protected Localizer loc;
    
    /** The mouse pointer manager used by this dialog */
    protected MousePointerManager mpm;
    
    
    /**
     * Creates a new Dialog.
     * 
     * @param parent  the parent frame
     * @param prefix  the prefix for the messages for the dialog
     * @param mpm  the mouse pointer manager to be used (null = allocate a new one)
     * @param loc  the localizer to be used (null = use default texts)
     */
    public GenericDialog(Frame parent, String prefix, MousePointerManager mpm, Localizer loc) {
        super(parent, true);
        this.loc = loc;
        this.mpm = (mpm == null) ? new MousePointerManager() : mpm;

        setTitle((loc == null) ? "" : loc.getText(prefix + SUFFIX_TITLE));
    }

    /**
     * Creates a new Dialog.
     * 
     * @param parent  the parent frame
     * @param mpm  the mouse pointer manager to be used (null = allocate a new one)
     * @param title  the title for the dialog
     */
    public GenericDialog(Frame parent, MousePointerManager mpm, String title) {
        super(parent, true);
        this.loc = null;
        this.mpm = (mpm == null) ? new MousePointerManager() : mpm;

        setTitle(title);
    }

    /**
     * Centers the dielog on the screen.
     */
    public void center() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = getSize();
        int windowX = Math.max(0, (screenSize.width  - windowSize.width )/2);
        int windowY = Math.max(0, (screenSize.height - windowSize.height)/2);
        setLocation(windowX, windowY);
    }
}
