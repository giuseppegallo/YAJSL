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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A file chooser dialog with several options.
 * 
 * @author Giuseppe Gallo
 */
public class FileChooser extends JFileChooser {
    
    /**
     * Preview component for images to be used as Accessory with a JFileChooser.
     * 
     * @author Giuseppe Gallo
     */
    public static class ImagePreview extends JPanel implements PropertyChangeListener {
        
        /** The label used for displaying the image preview */
        private final JLabel label;
        
        /** The margin to be left around the image */
        private final int border;
        
        /** The scaling options for the image */
        private final int scaleOptions;

        
        /**
         * Allocates an image preview instance.
         * 
         * @param sizeX  the horizontal size of the image preview
         * @param sizeY  the vertical size of the image preview
         * @param border  the margin to be left around the image
         * @param scaleOptions  the scaling options for the image
         */
        protected ImagePreview(int sizeX, int sizeY, int border, int scaleOptions) {
            this.scaleOptions = scaleOptions;
            this.border = border;

            // Prepare components
            setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            setLayout(new BorderLayout(5,5));

            label = new JLabel();
            label.setPreferredSize(new Dimension(sizeX, sizeY));
            label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            label.setAlignmentX(0.5F);
            label.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
            label.setFont(label.getFont().deriveFont(label.getFont().getSize()+149f));
            label.setForeground(Color.GRAY);

            add(label, BorderLayout.CENTER);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                File file = (File) evt.getNewValue();

                try {
                    if (file == null || (!file.isFile())) throw new IOException();
                    BufferedImage img = ImageIO.read(file);
                    if (img == null) throw new IOException();

                    double imgSizeX = img.getWidth();
                    double imgSizeY = img.getHeight();
                    double labSizeX = label.getWidth() - border;
                    double labSizeY = label.getHeight() - border;
                    double imgRatio = imgSizeX/imgSizeY;
                    double labRatio = labSizeX/labSizeY;
                    double scale = (imgRatio > labRatio) ? imgSizeX/labSizeX : imgSizeY/labSizeY;

                    Icon icon = new ImageIcon(img.getScaledInstance((int) Math.round(imgSizeX/scale), (int) Math.round(imgSizeY/scale), scaleOptions));
                    label.setText(null);
                    label.setIcon(icon);
                    label.setDisabledIcon(icon);
                    label.repaint();
                } catch (IOException e) {
                    label.setIcon(null);
                    label.setDisabledIcon(null);
                    label.setText("?");
                }
            }
        }
    }
    
    /**
     * Allocates a FileChooser instance.
     */
    private FileChooser() {
        super();
    }

    /**
     * Allocates a file chooser for selecting images (with preview).
     * 
     * @param sizeX  the horizontal size of the image preview
     * @param sizeY  the vertical size of the image preview
     * @param border  the margin to be left around the image
     * @param scaleOptions  the scaling options for the image
     * 
     * @return the file chooser allocated
     */
    public static FileChooser allocateImageChooser(int sizeX, int sizeY, int border, int scaleOptions) {
        FileChooser fc = new FileChooser();
        
        ImagePreview accessory = new ImagePreview(sizeX, sizeY, border, scaleOptions);
        
        fc.setAccessory(accessory);
        fc.addPropertyChangeListener(accessory);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        
        return fc;
    }
    
    /**
     * Allocates a file chooser for selecting images (with preview),
     * using default options.
     * 
     * @return the file chooser allocated
     */
    public static FileChooser allocateImageChooser() {
        return allocateImageChooser(200, 200, 2, Image.SCALE_SMOOTH);
    }
    
    /**
     * Allocates a file chooser for selecting directories.
     * 
     * @return the file chooser allocated
     */
    public static FileChooser allocateDirChooser() {
        FileChooser fc = new FileChooser();
        
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(false);
        
        return fc;
    }
    
    /**
     * Allocates a file chooser for selecting files only.
     * 
     * @return the file chooser allocated
     */
    public static FileChooser allocateFileChooser() {
        FileChooser fc = new FileChooser();
        
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        
        return fc;
    }
}
