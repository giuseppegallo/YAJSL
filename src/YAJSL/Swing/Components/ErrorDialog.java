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

import YAJSL.Swing.Application;
import YAJSL.Swing.MousePointerManager;
import YAJSL.Swing.Window;
import YAJSL.Utils.Localizer;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * A dialog for showing error messages.
 *
 * @author Giuseppe Gallo
 */
public class ErrorDialog extends javax.swing.JDialog {

    /** The property for specifying the text for the Description label */
    public final static String TEXT_LABEL_DESCRIPTION = "ErrorDialog.description.text";

    /** The property for specifying the text for the Details label */
    public final static String TEXT_LABEL_DETAILS = "ErrorDialog.details.text";

    /** The property for specifying the text for the Show Details button */
    public final static String TEXT_BUTTON_SHOW_DETAILS = "ErrorDialog.buttonShowDetails.text";

    /** The property for specifying the text for the Hide Details button */
    public final static String TEXT_BUTTON_HIDE_DETAILS = "ErrorDialog.buttonHideDetails.text";

    /** The property for specifying the text for the OK button */
    public final static String TEXT_BUTTON_OK = "ErrorDialog.buttonOK.text";

    /** The property for specifying the default title for the error dialog */
    public final static String DEFAULT_TITLE = "ErrorDialog.defaultTitle";

    /** The suffix for the property defining the localized title for the dialog */
    public final static String SUFFIX_ERROR_TITLE = ".title";

    /** The suffix for the property defining the localized description of the error */
    public final static String SUFFIX_ERROR_DESCR = ".descr";

    /** The name of the class to be used instead of ErrorDialog */
    public final static String CLASS_NAME = "ErrorDialog.class";

    /** The details of the error */
    private String textHideDetails = "Hide Details";

    /** The details of the error */
    private String textShowDetails = "Show Details";

    /** The height of the dialog with details shown */
    private final int heightDetails;

    /** The height of the dialog with no details shown */
    private final int heightNoDetails;


    /**
     * Creates a new ErrorDialog.
     *
     * @param parent  the parent frame
     * @param title  the title for the dialog
     * @param description  the error description
     * @param details  the details of the error
     * @param mpm  the mouse pointer manager to be used (null = allocate a new one)
     * @param loc  the localizer to be used (null = use default texts)
     */
    public ErrorDialog(Frame parent, String title, String description, String details, MousePointerManager mpm, Localizer loc) {
        super(parent, true);

        initComponents();

        // Set the mouse pointer manager
        mpm = (mpm == null) ? new MousePointerManager() : mpm;
        mpm.add(jButtonOK);
        mpm.add(jButtonDetails);
        mpm.add(jScrollPaneDescription.getVerticalScrollBar());
        mpm.add(jScrollPaneDetails.getVerticalScrollBar());

        // Set the text of the various components
        setTitle(title);

        if (loc != null) {
            jLabelDescription.setText(loc.getText(TEXT_LABEL_DESCRIPTION));
            jLabelDetails.setText(loc.getText(TEXT_LABEL_DETAILS));
            jButtonOK.setText(loc.getText(TEXT_BUTTON_OK));

            textShowDetails = loc.getText(TEXT_BUTTON_SHOW_DETAILS);
            textHideDetails = loc.getText(TEXT_BUTTON_HIDE_DETAILS);
            jButtonDetails.setText(textShowDetails);
        }

        jTextAreaDescription.setText(description);
        jTextAreaDescription.setCaretPosition(0);
        setTextAreaSize(jTextAreaDescription, description);

        jTextAreaDetails.setText(details);
        jTextAreaDetails.setCaretPosition(0);
        setTextAreaSize(jTextAreaDetails, details);

        // Hide unneeded components
        jPanelDetails.setVisible(false);
        if (details == null) {
            jButtonDetails.setVisible(false);
        }

        pack();

        heightNoDetails = getPreferredSize().height;
        heightDetails = heightNoDetails + 250;

        Dimension windowSize = getSize();
        setMinimumSize(windowSize);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int windowX = Math.max(0, (screenSize.width  - windowSize.width )/2);
        int windowY = Math.max(0, (screenSize.height - windowSize.height)/2);
        setLocation(windowX, windowY);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelIcon = new javax.swing.JLabel();
        jPanelMain = new javax.swing.JPanel();
        jPanelDescription = new javax.swing.JPanel();
        jLabelDescription = new javax.swing.JLabel();
        jScrollPaneDescription = new javax.swing.JScrollPane();
        jTextAreaDescription = new javax.swing.JTextArea();
        jPanelMiddle = new javax.swing.JPanel();
        jPanelDetails = new javax.swing.JPanel();
        jLabelDetails = new javax.swing.JLabel();
        jScrollPaneDetails = new javax.swing.JScrollPane();
        jTextAreaDetails = new javax.swing.JTextArea();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOK = new javax.swing.JButton();
        jButtonDetails = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);

        jLabelIcon.setIcon(UIManager.getIcon("OptionPane.errorIcon"));

        jLabelDescription.setFont(jLabelDescription.getFont().deriveFont(jLabelDescription.getFont().getStyle() | java.awt.Font.BOLD));
        jLabelDescription.setText("Description:");

        jTextAreaDescription.setEditable(false);
        jTextAreaDescription.setBackground(jPanelButtons.getBackground());
        jTextAreaDescription.setColumns(20);
        jTextAreaDescription.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jTextAreaDescription.setLineWrap(true);
        jTextAreaDescription.setRows(3);
        jScrollPaneDescription.setViewportView(jTextAreaDescription);

        javax.swing.GroupLayout jPanelDescriptionLayout = new javax.swing.GroupLayout(jPanelDescription);
        jPanelDescription.setLayout(jPanelDescriptionLayout);
        jPanelDescriptionLayout.setHorizontalGroup(
            jPanelDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDescriptionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneDescription)
                    .addGroup(jPanelDescriptionLayout.createSequentialGroup()
                        .addComponent(jLabelDescription)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelDescriptionLayout.setVerticalGroup(
            jPanelDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDescriptionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneDescription)
                .addContainerGap())
        );

        jPanelMiddle.setLayout(new javax.swing.BoxLayout(jPanelMiddle, javax.swing.BoxLayout.Y_AXIS));

        jLabelDetails.setFont(jLabelDetails.getFont().deriveFont(jLabelDetails.getFont().getStyle() | java.awt.Font.BOLD));
        jLabelDetails.setText("Details:");

        jTextAreaDetails.setEditable(false);
        jTextAreaDetails.setBackground(jPanelButtons.getBackground());
        jTextAreaDetails.setColumns(20);
        jTextAreaDetails.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        jTextAreaDetails.setLineWrap(true);
        jTextAreaDetails.setRows(3);
        jScrollPaneDetails.setViewportView(jTextAreaDetails);

        javax.swing.GroupLayout jPanelDetailsLayout = new javax.swing.GroupLayout(jPanelDetails);
        jPanelDetails.setLayout(jPanelDetailsLayout);
        jPanelDetailsLayout.setHorizontalGroup(
            jPanelDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneDetails)
                    .addGroup(jPanelDetailsLayout.createSequentialGroup()
                        .addComponent(jLabelDetails)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelDetailsLayout.setVerticalGroup(
            jPanelDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelDetails)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelMiddle.add(jPanelDetails);

        jButtonOK.setText("OK");
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jButtonDetails.setText("Show Details");
        jButtonDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetailsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelButtonsLayout = new javax.swing.GroupLayout(jPanelButtons);
        jPanelButtons.setLayout(jPanelButtonsLayout);
        jPanelButtonsLayout.setHorizontalGroup(
            jPanelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonsLayout.createSequentialGroup()
                .addContainerGap(302, Short.MAX_VALUE)
                .addComponent(jButtonDetails)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonOK)
                .addContainerGap())
        );
        jPanelButtonsLayout.setVerticalGroup(
            jPanelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOK)
                    .addComponent(jButtonDetails))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelMiddle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addComponent(jPanelDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelMiddle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelIcon)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        dispose();
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jButtonDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetailsActionPerformed
        boolean detailsShown = jPanelDetails.isVisible();
        jButtonDetails.setText((detailsShown) ? textShowDetails : textHideDetails);
        jPanelDetails.setVisible(!detailsShown);

        int sizeW = getWidth();
        int minH = (!detailsShown) ? heightDetails : heightNoDetails;

        setMinimumSize(new Dimension(getMinimumSize().width, minH));

        int newH = (detailsShown) ? minH : Math.max(getHeight(), minH);

        Dimension windowSize = new Dimension(sizeW, newH);
        setPreferredSize(windowSize);
        setSize(windowSize);

        // Recenter vertically
        Point p = getLocation();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int windowY = Math.max(0, (screenSize.height - windowSize.height)/2);
        setLocation(p.x, windowY);
    }//GEN-LAST:event_jButtonDetailsActionPerformed

    private void setTextAreaSize(JTextArea area, String text) {
        if (text == null) return;

        String[] lines = text.split("\n");
        int bigger = 0;

        for (int i = 0; i < lines.length; ++i) {
            int len = lines[i].length();
            if (bigger < len) bigger = len;
        }

        area.setColumns(bigger);
        area.setRows(lines.length);
        area.setMaximumSize(area.getPreferredScrollableViewportSize());
    }

    /**
     * Creates and shows a dialog notifying an error.
     *
     * @param parent  the parent frame
     * @param title  the title for the dialog
     * @param description  the error description
     * @param details  the details of the error
     * @param mpm  the mouse pointer manager to be used (null = allocate a new one)
     * @param loc  the localizer to be used (null = use default texts)
     * @param className  the name of the class to be used (null = ErrorDialog)
     */
    public static void showDialog(Frame parent, String title, String description, String details, MousePointerManager mpm, Localizer loc, String className) {
        JDialog dialog = null;

        if (className != null && !className.isEmpty()) {
            try {
                Class cl = Class.forName(className);
                dialog = (JDialog) cl.getConstructor(Frame.class, String.class, String.class, String.class, MousePointerManager.class, Localizer.class).
                    newInstance(parent, title, description, details, mpm, loc);
            } catch (
                ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException |
                IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ErrorDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (dialog == null) dialog = new ErrorDialog(parent, title, description, details, mpm, loc);

        dialog.setVisible(true);
    }

    /**
     * Creates and shows a dialog notifying an error.
     *
     * @param parent  the parent frame
     * @param title  the title for the dialog
     * @param description  the error description
     * @param ex  the exception to be shown as details
     * @param mpm  the mouse pointer manager to be used (null = allocate a new one)
     * @param loc  the localizer to be used (null = use default texts)
     * @param className  the name of the class to be used (null = ErrorDialog)
     */
    public static void showDialog(Frame parent, String title, String description, Exception ex, MousePointerManager mpm, Localizer loc, String className) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        showDialog(parent, title, description, ex.toString() + "\n\nSTACK TRACE:\n" + stackTrace, mpm, loc, className);
    }

    /**
     * Creates and shows a dialog notifying an error.
     *
     * @param app  the application in which the error occurred
     * @param title  the title for the dialog
     * @param description  the error description
     * @param details  the details of the error
     */
    public static void showDialog(Application app, String title, String description, String details) {
        String className = app.getProperties().getProperty(CLASS_NAME);
        Window window = app.getMainWindow();
        showDialog(window, title, description, details, (window == null) ? null : window.getMousePointerManager(), app.getLocalizer(), className);
    }

    /**
     * Creates and shows a dialog notifying an error.
     *
     * @param app  the application in which the error occurred
     * @param title  the title for the dialog
     * @param description  the error description
     * @param ex  the exception to be shown as details
     */
    public static void showDialog(Application app, String title, String description, Exception ex) {
        if (ex != null) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();

            showDialog(app, title, description, ex.toString() + "\n\nSTACK TRACE:\n" + stackTrace);
        } else {
            showDialog(app, title, description, (String)null);
        }
    }

    /**
     * Creates and shows a dialog notifying an error.
     *
     * @param app  the application in which the error occurred
     * @param prefix  the prefix of the properties defining the title and the description for the error
     * @param ex  the exception to be shown as details
     */
    public static void showDialog(Application app, String prefix, Exception ex) {
        Localizer loc = app.getLocalizer();
        showDialog(app, loc.getText(prefix + SUFFIX_ERROR_TITLE, loc.getText(DEFAULT_TITLE)), loc.getText(prefix + SUFFIX_ERROR_DESCR), ex);
    }

    public static void main(String[] argv) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ErrorDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        showDialog(
                new javax.swing.JFrame(),
                "Example error 1",
                "This is an error\ndescription spanning on multiple\n\nlines.5, to be precise.\nYes, 5.",
                "And now A LOT of lines for the details\n1.\n2.\n3.\n4.\n5.\n6.\n7.\n8.\n9.\n10.\n\nAND AGAIN...\n" +
                "And now A LOT of lines for the details\n1.\n2.\n3.\n4.\n5.\n6.\n7.\n8.\n9.\n10.\n\nAND AGAIN...\n" +
                "And now A LOT of lines for the details\n1.\n2.\n3.\n4.\n5.\n6.\n7.\n8.\n9.\n10.\n\nAND AGAIN...\n",
                null,
                null,
                null
        );

        showDialog(
                new javax.swing.JFrame(),
                "Example error 2",
                "This is an error\ndescription spanning on multiple\n\nlines.5, to be precise.\nYes, 5. And with no details.",
                (String)null,
                null,
                null,
                null
        );

        System.exit(0);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDetails;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JLabel jLabelDescription;
    private javax.swing.JLabel jLabelDetails;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelDescription;
    private javax.swing.JPanel jPanelDetails;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelMiddle;
    private javax.swing.JScrollPane jScrollPaneDescription;
    private javax.swing.JScrollPane jScrollPaneDetails;
    private javax.swing.JTextArea jTextAreaDescription;
    private javax.swing.JTextArea jTextAreaDetails;
    // End of variables declaration//GEN-END:variables
}
