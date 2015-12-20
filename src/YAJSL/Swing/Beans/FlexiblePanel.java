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
/*
 * NOTE: This component is widely based on the tutorial available here: http://zetcode.com/tutorials/javaswingtutorial/resizablecomponent/
 */
package YAJSL.Swing.Beans;

import YAJSL.Swing.Interfaces.Gridded;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * Represents a JPanel panel which can be moved and resized.
 * <p>
 * NOTE: This component is widely based on the tutorial available here: http://zetcode.com/tutorials/javaswingtutorial/resizablecomponent/
 */
public class FlexiblePanel extends javax.swing.JPanel {

    /**
     * Represents the border for the flexible panel.
     */
    public static class FlexibleBorder implements Border {

        /** The stroke used for drawing solid lines with width 1 */
        public final static Stroke SOLID_STROKE_1 = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

        /** The stroke used for drawing solid lines with width 1 */
        public final static Stroke SOLID_STROKE_2 = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

        /** The stroke used for drawing very thin lines with width 1 */
        public final static Stroke HAIRY_STROKE_1 = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{1f}, 0f);

        /** The stroke used for drawing dotted lines with width 1 */
        public final static Stroke DOTTED_STROKE_1 = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{2f}, 0f);

        /** The stroke used for drawing dotted lines with width 2 */
        public final static Stroke DOTTED_STROKE_2 = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2f, new float[]{2f}, 0f);

        /** The stroke used for drawing dashed lines with width 1 */
        public final static Stroke DASHED_STROKE_1 = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{4f}, 0f);
    
        /** The stroke used for drawing dashed lines with width 2 */
        public final static Stroke DASHED_STROKE_2 = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2f, new float[]{4f}, 0f);
    
        
        /**
         * The information about the handles.
         */
        protected class HandleInfo {
            protected int location = -1;
            protected int cursor = -1;
            
            protected HandleInfo(int location, int cursor) {
                this.location = location;
                this.cursor = cursor;
            }
            
            protected Rectangle getRectangle(int x, int y, int width, int height) {
                int half = handlesSize/2;
                switch (location) {
                    case SwingConstants.NORTH:
                        return new Rectangle(x + width/2 - half, y, handlesSize, handlesSize);
                    
                    case SwingConstants.SOUTH:
                        return new Rectangle(x + width/2 - half, y + height - handlesSize - 1, handlesSize, handlesSize);
                    
                    case SwingConstants.WEST:
                        return new Rectangle(x, y + height/2 - half, handlesSize, handlesSize);
                    
                    case SwingConstants.EAST:
                        return new Rectangle(x + width - handlesSize - 1, y + height/2 - half, handlesSize, handlesSize);
                    
                    case SwingConstants.NORTH_WEST:
                        return new Rectangle(x, y, handlesSize, handlesSize);
                    
                    case SwingConstants.NORTH_EAST:
                        return new Rectangle(x + width - handlesSize - 1, y, handlesSize, handlesSize);
                    
                    case SwingConstants.SOUTH_WEST:
                        return new Rectangle(x, y + height - handlesSize - 1, handlesSize, handlesSize);
                    
                    case SwingConstants.SOUTH_EAST:
                        return new Rectangle(x + width - handlesSize - 1, y + height - handlesSize - 1, handlesSize, handlesSize);
                }
                return null;
            }
        }
        
        /** The color used for drawing the border */
        protected Color color = Color.BLACK;

        /** The color used for drawing the handles */
        protected Color handlesColor = Color.WHITE;
        
        /** The stroke used for painting the border */
        protected Stroke stroke = SOLID_STROKE_1;
        
        /** The stroke used for painting the handles */
        protected Stroke handlesStroke = SOLID_STROKE_1;
        
        /** The size of the handles */
        protected int handlesSize = 6;
        
        /** The inset in addition to half-size of the handle */
        protected int inset = 1;
        
        /** The information about the handles */
        protected final HandleInfo[] handles = {
            new HandleInfo(SwingConstants.NORTH, Cursor.N_RESIZE_CURSOR),
            new HandleInfo(SwingConstants.SOUTH, Cursor.S_RESIZE_CURSOR),
            new HandleInfo(SwingConstants.WEST, Cursor.W_RESIZE_CURSOR),
            new HandleInfo(SwingConstants.EAST, Cursor.E_RESIZE_CURSOR),
            new HandleInfo(SwingConstants.NORTH_WEST, Cursor.NW_RESIZE_CURSOR),
            new HandleInfo(SwingConstants.NORTH_EAST, Cursor.NE_RESIZE_CURSOR),
            new HandleInfo(SwingConstants.SOUTH_WEST, Cursor.SW_RESIZE_CURSOR),
            new HandleInfo(SwingConstants.SOUTH_EAST, Cursor.SE_RESIZE_CURSOR),
        };

        
        /**
         * Sets the color used for drawing the border.
         * 
         * @param color  the color to be used for drawing the border
         */
        public void setColor(Color color) {
            this.color = color;
        }
        
        /**
         * Returns the color used for drawing the border.
         * 
         * @return  the color used for drawing the border
         */
        public Color getColor() {
            return color;
        }
        
        /**
         * Sets the stroke to be used to paint the border.
         * 
         * @param stroke  the stroke to be used to paint the border
         */
        public void setStroke(Stroke stroke) {
            this.stroke = stroke;
        }
        
        /**
         * Returns the stroke used to paint the border.
         * 
         * @return  the stroke used to paint the border
         */
        public Stroke getStroke() {
            return stroke;
        }
        
        /**
         * Sets the stroke to be used to paint the handles.
         * 
         * @param handlesStroke  the stroke to be used to paint the handles
         */
        public void setHandlesStroke(Stroke handlesStroke) {
            this.handlesStroke = handlesStroke;
        }
        
        /**
         * Returns the stroke used to paint the handles.
         * 
         * @return  the stroke used to paint the handles
         */
        public Stroke getHandlesStroke() {
            return handlesStroke;
        }
        
        /**
         * Sets the color used for drawing the handles.
         * 
         * @param color  the color to be used for drawing the handles
         */
        public void setHandlesColor(Color color) {
            this.handlesColor = color;
        }
        
        /**
         * Returns the color used for drawing the handles.
         * 
         * @return  the color used for drawing the handles
         */
        public Color getHandlesColor() {
            return handlesColor;
        }
        
        /**
         * Sets the size of the handles.
         * 
         * @param size  the size of the handles
         */
        public void setHandlesSize(int size) {
            this.handlesSize = size;
        }
        
        /**
         * Returns the size of the handles.
         * 
         * @return  the size of the handles
         */
        public int getHandlesSize() {
            return handlesSize;
        }
        
        /**
         * Sets the inset in addition to half-size of the handle.
         * 
         * @param inset  the inset in addition to half-size of the handle
         */
        public void setInset(int inset) {
            this.inset = inset;
        }
        
        /**
         * Returns the inset in addition to half-size of the handle.
         * 
         * @return  the inset in addition to half-size of the handle
         */
        public int getInset() {
            return inset;
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(handlesSize/2 + inset, handlesSize/2 + inset, handlesSize/2 + inset + 1, handlesSize/2 + inset + 1);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D)g;
            
            int half = handlesSize/2;
            
            g2.setColor(color);
            g2.setStroke(stroke);
            g2.drawRect(x + half, y + half, width - handlesSize - 1, height - handlesSize - 1);
            
            g2.setStroke(handlesStroke);
            if (c.hasFocus()) {
                for (HandleInfo h : handles) {
                    Rectangle r = h.getRectangle(x, y, width, height);
                    g2.setColor(handlesColor);
                    g2.fillRect(r.x, r.y, r.width, r.height);
                    g2.setColor(color);
                    g2.drawRect(r.x, r.y, r.width, r.height);
                }
            }
        }
        
        /**
         * Returns the cursor to be used depending on whether the mouse is hovering over the handles or not.
         * 
         * @param e  the mouse event
         * @return  the cursor to be used depending on whether the mouse is hovering over the handles or not
         */
        public int getCursor(MouseEvent e) {
            Component c = e.getComponent();
            int width = c.getWidth();
            int height = c.getHeight();
            
            for (HandleInfo h : handles) {
                if (h.getRectangle(0, 0, width, height).contains(e.getPoint())) return h.cursor;
            }
            
            return Cursor.DEFAULT_CURSOR;
        }
    }
    
    /**
     * Creates new form FlexiblePanel
     */
    public FlexiblePanel() {
        super.setOpaque(false);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBorder(new FlexibleBorder());
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        dragCursor = ((FlexibleBorder)getBorder()).getCursor(evt);
        dragStart = evt.getLocationOnScreen();
        
        if (getParent() instanceof JLayeredPane) {
            ((JLayeredPane)getParent()).moveToFront(this);
        }
        
        requestFocus();
        repaint();
    }//GEN-LAST:event_formMousePressed

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited
        setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_formMouseExited

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        if (hasFocus()) {
            setCursor(Cursor.getPredefinedCursor(((FlexibleBorder)getBorder()).getCursor(evt)));
        }
    }//GEN-LAST:event_formMouseMoved

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        dragStart = null;
    }//GEN-LAST:event_formMouseReleased

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        if (dragStart == null) return;
        
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        Point loc = evt.getLocationOnScreen();
        int cx = loc.x;
        int cy = loc.y;
        int dx = cx - dragStart.x;
        int dy = cy - dragStart.y;
        
        Dimension min = getMinimumSize();
        
        switch (dragCursor) {
            case Cursor.N_RESIZE_CURSOR:
                if (h - dy < min.height) dy = h - min.height;
                if (dy != 0) {
                    setBounds(getAlignedBounds(x, y + dy, w, h - dy));
                    resize();
                }
                break;
                
            case Cursor.S_RESIZE_CURSOR:
                if (h + dy < min.height) dy = min.height - h;
                if (dy != 0) {
                    setBounds(getAlignedBounds(x, y, w, h + dy));
                    resize();
                }
                break;
                
            case Cursor.W_RESIZE_CURSOR:
                if (w - dx < min.width) dx = w - min.width;
                if (dx != 0) {
                    setBounds(getAlignedBounds(x + dx, y, w - dx, h));
                    resize();
                }
                break;
                
            case Cursor.E_RESIZE_CURSOR:
                if (w + dx < min.width) dx = min.width - w;
                if (dx != 0) {
                    setBounds(getAlignedBounds(x, y, w + dx, h));
                    resize();
                }
                break;

            case Cursor.NE_RESIZE_CURSOR:
                if (h - dy < min.height) dy = h - min.height;
                if (w + dx < min.width) dx = min.width - w;
                if (dy != 0 || dx != 0) {
                    setBounds(getAlignedBounds(x, y + dy, w + dx, h - dy));
                    resize();
                }
                break;
                
            case Cursor.NW_RESIZE_CURSOR:
                if (h - dy < min.height) dy = h - min.height;
                if (w - dx < min.width) dx = w - min.width;
                if (dy != 0 || dx != 0) {
                    setBounds(getAlignedBounds(x + dx, y + dy, w - dx, h - dy));
                    resize();
                }
                break;
                
            case Cursor.SE_RESIZE_CURSOR:
                if (h + dy < min.height) dy = min.height - h;
                if (w + dx < min.width) dx = min.width - w;
                if (dy != 0 || dx != 0) {
                    setBounds(getAlignedBounds(x, y, w + dx, h + dy));
                    resize();
                }
                break;
                
            case Cursor.SW_RESIZE_CURSOR:
                if (h + dy < min.height) dy = min.height - h;
                if (w - dx < min.width) dx = w - min.width;
                if (dy != 0 || dx != 0) {
                    setBounds(getAlignedBounds(x + dx, y, w - dx, h + dy));
                    resize();
                }
                break;
                
            case Cursor.DEFAULT_CURSOR:
            case Cursor.MOVE_CURSOR:
                setBounds(getAlignedBounds(x + dx, y + dy, w, h));
                resize();
                dragCursor = Cursor.MOVE_CURSOR;
                break;
                
        }
        
        setCursor(Cursor.getPredefinedCursor(dragCursor));
        dragStart = getAlignedPoint(loc);
    }//GEN-LAST:event_formMouseDragged

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost
        repaint();
    }//GEN-LAST:event_formFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    /**
     * Ensures the component is repainted according to the new size.
     */
    private void resize() {
        JComponent parent = (JComponent)getParent();
        if (parent != null) {
            parent.revalidate();
        } else {
            repaint();
        }
    }

    /**
     * Returns the bounds aligned with the grid of the parent (if it is a Gridded panel).
     * 
     * @param x  the horizontal position of the upper-left corner
     * @param y  the vertical position of the upper-left corner
     * @param w  the width
     * @param h  the height
     * @return  the bounds aligned with the grid of the parent (if it is a Gridded panel)
     */
    public Rectangle getAlignedBounds(int x, int y, int w, int h) {
        Component parent = getParent();
        if (parent == null || !(parent instanceof Gridded)) {
            return new Rectangle(x, y, w, h);
        } else {
            Point start = ((Gridded)parent).getGridPoint(new Point(x, y));
            Point end = ((Gridded)parent).getGridPoint(new Point(x + w - 1, y + h - 1));
            return new Rectangle(start.x, start.y, end.x - start.x + 1, end.y - start.y + 1);
        }
    }

    public Point getAlignedPoint(Point p) {
        Component parent = getParent();
        if (parent == null || !(parent instanceof Gridded)) {
            return p;
        } else {
            return ((Gridded)parent).getGridPoint(p);
        }
    }
    
    @Override
    public void setBorder(Border border) {
        if (!(border instanceof FlexibleBorder)) return;
        super.setBorder(border);
    }

    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(false);
    }
    
    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int handleSize = ((FlexibleBorder)getBorder()).getHandlesSize();
        int half = handleSize/2;
        
        Rectangle clip = g.getClipBounds();
        Rectangle draw = clip.intersection(new Rectangle(half, half, getWidth() - handleSize - 1, getHeight() - handleSize - 1));
        
        if (draw.isEmpty()) return;
        
        g.setColor(getBackground());
        g.fillRect(draw.x, draw.y, draw.width, draw.height);
    }
    
    /**
     * Sets the color used for drawing the border.
     * 
     * @param color  the color to be used for drawing the border
     */
    public void setBorderColor(Color color) {
        ((FlexibleBorder)getBorder()).setColor(color);
    }

    /**
     * Returns the color used for drawing the border.
     * 
     * @return  the color used for drawing the border
     */
    public Color getBorderColor() {
        return ((FlexibleBorder)getBorder()).getColor();
    }

    /**
     * Sets the stroke to be used to paint the border.
     * 
     * @param stroke  the stroke to be used to paint the border
     */
    public void setBorderStroke(Stroke stroke) {
        ((FlexibleBorder)getBorder()).setStroke(stroke);
    }

    /**
     * Returns the stroke used to paint the border.
     * 
     * @return  the stroke used to paint the border
     */
    public Stroke getBorderStroke() {
        return ((FlexibleBorder)getBorder()).getStroke();
    }

    /**
     * Sets the stroke to be used to paint the handles.
     * 
     * @param handlesStroke  the stroke to be used to paint the handles
     */
    public void setHandlesStroke(Stroke handlesStroke) {
        ((FlexibleBorder)getBorder()).setHandlesStroke(handlesStroke);
    }

    /**
     * Returns the stroke used to paint the handles.
     * 
     * @return  the stroke used to paint the handles
     */
    public Stroke getHandlesStroke() {
        return ((FlexibleBorder)getBorder()).getHandlesStroke();
    }

    /**
     * Sets the color used for drawing the handles.
     * 
     * @param color  the color to be used for drawing the handles
     */
    public void setHandlesColor(Color color) {
        ((FlexibleBorder)getBorder()).setHandlesColor(color);
    }

    /**
     * Returns the color used for drawing the handles.
     * 
     * @return  the color used for drawing the handles
     */
    public Color getHandlesColor() {
        return ((FlexibleBorder)getBorder()).getHandlesColor();
    }

    /**
     * Sets the size of the handles.
     * 
     * @param size  the size of the handles
     */
    public void setHandlesSize(int size) {
        ((FlexibleBorder)getBorder()).setHandlesSize(size);
    }

    /**
     * Returns the size of the handles.
     * 
     * @return  the size of the handles
     */
    public int getHandlesSize() {
        return ((FlexibleBorder)getBorder()).getHandlesSize();
    }
        
    /**
     * Sets the inset in addition to half-size of the handle.
     * 
     * @param inset  the inset in addition to half-size of the handle
     */
    public void setInset(int inset) {
        ((FlexibleBorder)getBorder()).setInset(inset);
    }

    /**
     * Returns the inset in addition to half-size of the handle.
     * 
     * @return  the inset in addition to half-size of the handle
     */
    public int getInset() {
        return ((FlexibleBorder)getBorder()).getInset();
    }
    
    /** The starting point for the dragging */
    private Point dragStart = null;
    
    /** The cursor identifying the handle being dragged */
    private int dragCursor = -1;
}
