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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.SwingConstants;

/**
 *
 * @author Giuseppe Gallo
 */
public class ImagePanel extends javax.swing.JPanel {

    /** The image shown */
    protected BufferedImage image = null;

    /** The scaled image shown */
    protected Image scaledImage = null;

    /** The previous scale of the image */
    protected float prevScale = 0;

    /** The scaled width of the image */
    protected int scaledSizeX = 1;
    
    /** The scaled height of the image */
    protected int scaledSizeY = 1;
    
    /** True if the image is valid */
    protected boolean valid = true;

    /** The rendering hints used for diplaying the image */
    protected Map<RenderingHints.Key,Object> renderingHints = null;

    /** The title of the image */
    protected String title = null;

    /** The font of the title of the image */
    protected Font titleFont = null;

    /** The color of the title of the image */
    protected Color titleColor = Color.BLACK;

    /** The horizontal alignment for the title of the image */
    protected int horizontalTitleAlignment = SwingConstants.CENTER;

    /** The vertical alignment for the title of the image */
    protected int verticalTitleAlignment = SwingConstants.CENTER;
    
    /** The size of the upper curtain (in %) */
    protected float upperCurtainSize = 0f;

    /** The size of the lower curtain (in %) */
    protected float lowerCurtainSize = 0f;

    /** The color of the curtains */
    protected Color curtainColor = Color.DARK_GRAY;

    /** The opacity of the curtains */
    protected float curtainOpacity = 0.5f;

    /** True if the curtains are shown */
    protected boolean curtainShown = false;

    /** The color to be used when the image is invalid */
    protected Color invalidColor = Color.GRAY;

    /** True if the zoom circle is enabled. The zoom circle displays at 100% the area around the mouse cursor. */
    protected boolean zoomEnabled = false;

    /** The position of the mouse cursor */
    protected Point mouseXY = null;

    /** The size of the zoom circle. The zoom circle displays at 100% the area around the mouse cursor. */
    protected float zoomCircleSize = 0.5f;

    /** The color of the outline of the zoom circle. The zoom circle displays at 100% the area around the mouse cursor. */
    protected Color zoomCircleColor = Color.BLACK;

    /** The size of the outline of the zoom circle. The zoom circle displays at 100% the area around the mouse cursor. */
    protected int zoomCircleLineSize = 4;
    
    
    /**
     * Default constructor
     */
    public ImagePanel() {
        initComponents();

        // Init rendering hints
        renderingHints = new HashMap<>();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelTitle = new javax.swing.JLabel();

        jLabelTitle.setFont(titleFont);
        jLabelTitle.setForeground(titleColor);
        jLabelTitle.setHorizontalAlignment(horizontalTitleAlignment);
        jLabelTitle.setText(title);
        jLabelTitle.setVerticalAlignment(verticalTitleAlignment);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelTitle;
    // End of variables declaration//GEN-END:variables

    /**
     * If the zoon is enabled, displays the zoom circle showing at 100%
     * zoom the area around the specified point.
     *
     * @param zoomXY  the center of the area to be zoomed
     */
    public void zoomAt(Point zoomXY) {
        mouseXY = zoomXY;
        if (zoomEnabled) repaint();
    }

    /**
     * Returns the rendering hints used for displaying the image.
     *
     * @return   the rendering hints used for displaying the image
     */
    public Map<?,?> getRenderingHints() {
        return renderingHints;
    }

    /**
     * Sets the rendering hints used for displaying the image.
     *
     * @param hints   the rendering hints to be used for displaying the image
     */
    public void setRenderingHints(Map<RenderingHints.Key,Object> hints) {
        renderingHints = hints;
    }

    /**
     * Sets a rendering hint used for displaying the image.
     *
     * @param key   the rendering key to be set
     * @param hint  the rendering hint to be set
     */
    public void setRenderingHint(RenderingHints.Key key, Object hint) {
        renderingHints.put(key, hint);
    }

    /**
     * Returns the title of the image.
     *
     * @return   the title of the image
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the image.
     *
     * @param title   the title for the image
     */
    public void setTitle(String title) {
        this.title = title;
        if (jLabelTitle != null) jLabelTitle.setText(title);
        repaint();
    }

    /**
     * Returns the horizontal alignment of the image title.
     *
     * @return   the horizontal alignment of the image title
     */
    public int getHorizontalTitleAlignment() {
        return horizontalTitleAlignment;
    }

    /**
     * Sets the horizontal alignment of the image title.
     *
     * @param horizontalTitleAlignment   the horizontal alignment for the image title
     */
    public void setHorizontalTitleAlignment(int horizontalTitleAlignment) {
        this.horizontalTitleAlignment = horizontalTitleAlignment;
        if (jLabelTitle != null) jLabelTitle.setHorizontalAlignment(horizontalTitleAlignment);
        if (title != null) repaint();
    }

    /**
     * Returns the vertical alignment of the image title.
     *
     * @return   the vertical alignment of the image title
     */
    public int getVerticalTitleAlignment() {
        return verticalTitleAlignment;
    }

    /**
     * Sets the vertical alignment of the image title.
     *
     * @param verticalTitleAlignment   the vertical alignment for the image title
     */
    public void setVerticalTitleAlignment(int verticalTitleAlignment) {
        this.verticalTitleAlignment = verticalTitleAlignment;
        if (jLabelTitle != null) jLabelTitle.setVerticalAlignment(verticalTitleAlignment);
        if (title != null) repaint();
    }

    /**
     * Returns the color of the image title
     *
     * @return   the color of the image title
     */
    public Color getTitleColor() {
        return titleColor;
    }

    /**
     * Sets the color of the image title.
     *
     * @param titleColor   the color for the image title
     */
    public void setTitleColor(Color titleColor) {
        this.titleColor = titleColor;
        if (jLabelTitle != null) jLabelTitle.setForeground(titleColor);
        if (title != null) repaint();
    }

    /**
     * Returns the font of the image title.
     *
     * @return   the font of the image title
     */
    public Font getTitleFont() {
        return titleFont;
    }

    /**
     * Sets the font of the image title.
     *
     * @param titleFont   the font for the image title
     */
    public void setTitleFont(Font titleFont) {
        this.titleFont = titleFont;
        if (jLabelTitle != null) jLabelTitle.setFont(titleFont);
        if (title != null) repaint();
    }

    /**
     * Returns the color of the cross shown when an invalid image is loaded.
     *
     * @return   the color of the cross shown when an invalid image is loaded
     */
    public Color getInvalidColor() {
        return invalidColor;
    }

    /**
     * Sets the color of the cross shown when an invalid image is loaded.
     *
     * @param invalidColor   the color for the cross shown when an invalid image is loaded
     */
    public void setInvalidColor(Color invalidColor) {
        this.invalidColor = invalidColor;
        if (!valid) repaint();
    }

    /**
     * Returns the color of the "curtains" used to restrict the view to a particular
     * section of the image.
     *
     * @return   the color of the "curtains" used to restrict the view to a particular
     *           section of the image
     */
    public Color getCurtainColor() {
        return curtainColor;
    }

    /**
     * Sets the color of the "curtains" used to restrict the view to a particular
     * section of the image.
     *
     * @param curtainColor   the color for the "curtains" used to restrict the view
     *                       to a particular section of the image
     */
    public void setCurtainColor(Color curtainColor) {
        this.curtainColor = curtainColor;
        if (curtainShown) repaint();
    }

    /**
     * Returns the opacity (from 0 = transparent to 1 = opaque) of the "curtains"
     * used to restrict the view to a particular section of the image.
     *
     * @return   the opacity (from 0 = transparent to 1 = opaque) of the "curtains"
     *           used to restrict the view to a particular section of the image
     */
    public float getCurtainOpacity() {
        return curtainOpacity;
    }

    /**
     * Sets the opacity (from 0 = transparent to 1 = opaque) of the "curtains"
     * used to restrict the view to a particular section of the image.
     *
     * @param curtainOpacity   the opacity (from 0 = transparent to 1 = opaque)
     *                         for the "curtains" used to restrict the view to a
     *                         particular section of the image
     */
    public void setCurtainOpacity(float curtainOpacity) {
        this.curtainOpacity = curtainOpacity;
        if (curtainShown) repaint();
    }

    /**
     * Returns the size (in % of the image height) of the lower "curtain".
     *
     * @return   the size (in % of the image height) of the lower "curtain"
     */
    public float getLowerCurtainSize() {
        return lowerCurtainSize;
    }

    /**
     * Sets the size (in % of the image height) of the lower "curtain".
     *
     * @param lowerCurtainSize   the size (in % of the image height) for
     *                           the lower "curtain"
     */
    public void setLowerCurtainSize(float lowerCurtainSize) {
        this.lowerCurtainSize = lowerCurtainSize;
        if (curtainShown) repaint();
    }

    /**
     * Returns the size (in % of the image height) of the upper "curtain".
     *
     * @return   the size (in % of the image height) of the upper "curtain"
     */
    public float getUpperCurtainSize() {
        return upperCurtainSize;
    }

    /**
     * Sets the size (in % of the image height) of the upper "curtain".
     * 
     * @param upperCurtainSize  the size (in % of the image height) of the upper "curtain".
     */
    public void setUpperCurtainSize(float upperCurtainSize) {
        this.upperCurtainSize = upperCurtainSize;
        if (curtainShown) repaint();
    }

    /**
     * Returns true if the curtains are shown.
     * 
     * @return  true if the curtains are shown
     */
    public boolean isCurtainShown() {
        return curtainShown;
    }

    /**
     * Sets if the curtains need to be shown.
     * 
     * @param curtainShown  true if the curtains need to be shown
     */
    public void setCurtainShown(boolean curtainShown) {
        this.curtainShown = curtainShown;
        repaint();
    }

    /**
     * Returns true if the zoom circle is enabled.
     * 
     * @return  true if the zoom circle is enabled
     */
    public boolean isZoomEnabled() {
        return zoomEnabled;
    }

    /**
     * Sets if the zoom circle is enabled.
     * 
     * @param zoomEnabled  true if the zoom circle needs to be enabled
     */
    public void setZoomEnabled(boolean zoomEnabled) {
        this.zoomEnabled = zoomEnabled;
        repaint();
    }

    /**
     * Returns the color of the outline of the zoom circle. The zoom circle displays at 100% the area around the mouse cursor.
     * 
     * @return  the color of the outline of the zoom circle
     */
    public Color getZoomCircleColor() {
        return zoomCircleColor;
    }

    /**
     * Sets the color of the outline of the zoom circle. The zoom circle displays at 100% the area around the mouse cursor.
     * 
     * @param zoomCircleColor  the color of the outline of the zoom circle
     */
    public void setZoomCircleColor(Color zoomCircleColor) {
        this.zoomCircleColor = zoomCircleColor;
        if (zoomEnabled) repaint();
    }

    /**
     * Returns the size of the zoom circle. The zoom circle displays at 100% the area around the mouse cursor.
     * 
     * @return  the size of the zoom circle
     */
    public float getZoomCircleSize() {
        return zoomCircleSize;
    }

    /**
     * Sets the size of the zoom circle. The zoom circle displays at 100% the area around the mouse cursor.
     * 
     * @param zoomCircleSize  the size of the zoom circle
     */
    public void setZoomCircleSize(float zoomCircleSize) {
        this.zoomCircleSize = zoomCircleSize;
        if (zoomEnabled) repaint();
    }

    /**
     * Calculates the size of the scaled image.
     */
    protected void calculateScale() {
        int cmpSizeX = getSize().width - getInsets().left - getInsets().right;
        int cmpSizeY = getSize().height - getInsets().top - getInsets().bottom;
        
        if (image != null) {
            int imgSizeX = image.getWidth();
            int imgSizeY = image.getHeight();

            float cmpRatio = (float)cmpSizeX/(float)cmpSizeY;
            float imgRatio = (float)imgSizeX/(float)imgSizeY;

            float scale;
            if (cmpRatio > imgRatio) {
                scale = (float)cmpSizeY/(float)imgSizeY;
            } else {
                scale = (float)cmpSizeX/(float)imgSizeX;
            }

            scaledSizeX = (int)Math.floor(imgSizeX*scale);
            scaledSizeY = (int)Math.floor(imgSizeY*scale);
        } else {
            prevScale = 0;
            scaledSizeX = cmpSizeX;
            scaledSizeY = cmpSizeY;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        int cmpSizeX = getSize().width - getInsets().left - getInsets().right;
        int cmpSizeY = getSize().height - getInsets().top - getInsets().bottom;
        int imgSizeX;
        int imgSizeY;
        scaledSizeX = cmpSizeX;
        scaledSizeY = cmpSizeY;
        float scale = 1;

        if (image != null) {
            imgSizeX = image.getWidth();
            imgSizeY = image.getHeight();
            int posX = getInsets().left;
            int posY = getInsets().top;

            float cmpRatio = (float)cmpSizeX/(float)cmpSizeY;
            float imgRatio = (float)imgSizeX/(float)imgSizeY;

            if (cmpRatio > imgRatio) {
                scale = (float)cmpSizeY/(float)imgSizeY;
                posX += (int)Math.floor((cmpSizeX - imgSizeX*scale)/2);
            } else {
                scale = (float)cmpSizeX/(float)imgSizeX;
                posY += (int)Math.floor((cmpSizeY - imgSizeY*scale)/2);
            }

            scaledSizeX = (int)Math.floor(imgSizeX*scale);
            scaledSizeY = (int)Math.floor(imgSizeY*scale);

            if (scale != prevScale || scaledImage == null) {
                scaledImage = image.getScaledInstance(scaledSizeX, scaledSizeY, Image.SCALE_SMOOTH);
                prevScale = scale;
            }
            
            g2d.setRenderingHints(renderingHints);
            g2d.drawImage(scaledImage, posX, posY, null);
        }

        if (!valid) {
            g2d.setColor(invalidColor);
            g2d.setStroke(new BasicStroke(Math.min(cmpSizeX, cmpSizeY)/20));
            g2d.setClip(getInsets().left, getInsets().top, cmpSizeX, cmpSizeY);
            g2d.drawLine(getInsets().left, getInsets().top, getInsets().left + cmpSizeX, getInsets().top + cmpSizeY);
            g2d.drawLine(getInsets().left, getInsets().top + cmpSizeY, getInsets().left + cmpSizeX, getInsets().top);
        }

        if (curtainShown) {
            g2d.setColor(curtainColor);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, curtainOpacity));
            int border = (cmpSizeY - scaledSizeY)/2;
            int ucSize = (int)Math.floor(upperCurtainSize*scaledSizeY) + border;
            int lcSize = (int)Math.floor(lowerCurtainSize*scaledSizeY) + border;
            g2d.fillRect(getInsets().left, getInsets().top, cmpSizeX, ucSize);
            g2d.fillRect(getInsets().left, getInsets().top + cmpSizeY - lcSize, cmpSizeX, lcSize);
            g2d.setComposite(AlphaComposite.SrcAtop);
        }

        if (image != null && zoomEnabled && mouseXY != null) {
            int circleSize = (int)Math.floor(Math.min(cmpSizeX, cmpSizeY)*this.zoomCircleSize);
            g2d.setColor(zoomCircleColor);
            g2d.setStroke(new BasicStroke(zoomCircleLineSize));

            int minX = (cmpSizeX - scaledSizeX)/2;
            int minY = (cmpSizeY - scaledSizeY)/2;
            int maxX = cmpSizeX - minX;
            int maxY = cmpSizeY - minY;

            int mx = (mouseXY.x < minX) ? minX : (mouseXY.x > maxX) ? maxX : mouseXY.x;
            int my = (mouseXY.y < minY) ? minY : (mouseXY.y > maxY) ? maxY : mouseXY.y;

            // Calculate where to show the circle depending on the mouse position
            int cx = getInsets().left + ((mx >= getSize().width/2) ? zoomCircleLineSize : cmpSizeX - circleSize - zoomCircleLineSize);
            int cy = getInsets().top + ((my >= getSize().height/2) ? zoomCircleLineSize : cmpSizeY - circleSize - zoomCircleLineSize);

            // Draw the circle
            Ellipse2D.Float circle = new Ellipse2D.Float(cx, cy, circleSize, circleSize);
            g2d.draw(circle);
            g2d.setColor(getBackground());
            g2d.fill(circle);

            // Draw the zoomed image inside the circle
            int imgX = (int)Math.floor((mx - minX)/scale);
            int imgY = (int)Math.floor((my - minY)/scale);
            g2d.clip(circle);
            g2d.drawImage(image, AffineTransform.getTranslateInstance(circle.getCenterX() - imgX, circle.getCenterY() - imgY), null);
        }
    }

    /**
     * Loads the image from the given file name.
     * 
     * @param fileName  the name of the file from which the image needs to be loaded
     */
    public void loadImage(String fileName) {
        loadImage((fileName == null) ? null : new File(fileName));
    }

    /**
     * Sets the image to be shown.
     * 
     * @param img  the image to be shown
     */
    public void setImage(BufferedImage img) {
        prevScale = 0;
        scaledImage = null;
        image = img;
        repaint();
    }

    /**
     * Loads the image from the given file.
     * 
     * @param file  the file from which the image needs to be loaded
     */
    public void loadImage(File file) {
        if (file == null) {
            image = null;
            valid = true;
        } else {
            try {
                image = ImageIO.read(file);
                valid = true;
            } catch (IOException ex) {
                image = null;
                valid = false;
            }
        }
        scaledImage = null;
        prevScale = 0;
        repaint();
    }
    
    /**
     * Returns the aspect ratio of this component.
     * 
     * @return  the aspect ratio of this component
     */
    public float getRatio() {
        float cmpSizeX = getSize().width - getInsets().left - getInsets().right;
        float cmpSizeY = getSize().height - getInsets().top - getInsets().bottom;
        
        return (cmpSizeY == 0) ? 0 : cmpSizeX/cmpSizeY;
    }
}
