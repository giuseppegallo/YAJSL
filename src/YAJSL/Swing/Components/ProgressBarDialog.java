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

import YAJSL.Swing.Interfaces.GenericProgressDialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;

/**
 * A dialog for showing the progress of a task.
 */
@SuppressWarnings("serial")
public class ProgressBarDialog extends javax.swing.JDialog implements GenericProgressDialog {

    /**
     * Creates a progress dialog.
     *
     * @param parent  the parent window
     */
    public ProgressBarDialog(java.awt.Frame parent) {
        this(parent, true);
    }

    /**
     * Creates a progress dialog, optionally calling the pack() method.
     *
     * @param parent  the parent window
     * @param pack  if true, calls pack()
     */
    protected ProgressBarDialog(java.awt.Frame parent, boolean pack) {
        super(parent, false);
        initComponents();
        if (pack) pack();
    }

    /**
     * Centers the dialog on the parent.
     */
    @Override
    public void setLocation() {
        java.awt.Container parent = getParent();

        Dimension parentSize = (parent == null) ? Toolkit.getDefaultToolkit().getScreenSize() : parent.getSize();
        Dimension windowSize = getSize();

        int windowX = Math.max(0, (parentSize.width  - windowSize.width )/2);
        int windowY = Math.max(0, (parentSize.height - windowSize.height)/2);

        setLocation(windowX, windowY);
    }

    @Override
    public final void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            if (!isIndeterminate()) setProgress(progress);
        }
    }

    /**
     * Sets the value in the progress bar.
     *
     * @param progress  the value to be set (0 - 100)
     */
    @Override
    public void setProgress(int progress) {
        jProgressBar.setValue(progress);
    }

    /**
     * Sets if the progress bar is indeterminate.
     *
     * @param indeterminate  true if the progress bar is indeterminate
     */
    @Override
    public void setIndeterminate(boolean indeterminate) {
        jProgressBar.setIndeterminate(indeterminate);
    }

    /**
     * Returns true if the progress bar is indeterminate.
     * 
     * @return  true if the progress bar is indeterminate.
     */
    @Override
    public boolean isIndeterminate() {
        return jProgressBar.isIndeterminate();
    }

    @Override
    public final void pack() {
        super.pack();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProgressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        jProgressBar.setPreferredSize(new java.awt.Dimension(146, 24));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar jProgressBar;
    // End of variables declaration//GEN-END:variables
}
