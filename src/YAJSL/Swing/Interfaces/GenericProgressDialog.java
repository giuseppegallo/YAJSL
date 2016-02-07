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
package YAJSL.Swing.Interfaces;

import java.beans.PropertyChangeListener;

/**
 * The interface to be implemented by any Progress Dialog.
 */
public interface GenericProgressDialog extends PropertyChangeListener {

    /**
     * Sets the location of the dialog.
     */
    public void setLocation();


    /**
     * Sets the value for the progress.
     *
     * @param progress  the value to be set (0 - 100)
     */
    public void setProgress(int progress);

    /**
     * Sets if the progress is indeterminate.
     *
     * @param indeterminate  true if the progress is indeterminate
     */
    public void setIndeterminate(boolean indeterminate);

    /**
     * Returns true if the progress is indeterminate.
     *
     * @return  true if the progress is indeterminate.
     */
    public boolean isIndeterminate();
}
