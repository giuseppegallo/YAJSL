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

import javax.swing.ImageIcon;

/**
 * Collection of useful methods for Swing applications using the ObjectStorage framework.
 * 
 * @author Giuseppe Gallo
 */
public class Utils {
    
    /** The default icon for adding something */
    public final static ImageIcon ICON_ADD = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/add.png"));
    
    /** The default icon for adding something (rollover) */
    public final static ImageIcon ICON_ADD_R = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/addR.png"));
    
    /** The default icon for adding something after the current one */
    public final static ImageIcon ICON_ADD_NEXT = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/addNext.png"));
    
    /** The default icon for adding something after the current one (rollover) */
    public final static ImageIcon ICON_ADD_NEXT_R = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/addNextR.png"));
    
    /** The default icon for clearing the current selection */
    public final static ImageIcon ICON_CLEAR_SELECTION = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/clearSelection.png"));
    
    /** The default icon for clearing the current selection (rollover) */
    public final static ImageIcon ICON_CLEAR_SELECTION_R = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/clearSelectionR.png"));
    
    /** The default icon for deleting something */
    public final static ImageIcon ICON_DELETE = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/delete.png"));
    
    /** The default icon for deleting something (rollover) */
    public final static ImageIcon ICON_DELETE_R = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/deleteR.png"));
    
    /** The default icon for duplicating something */
    public final static ImageIcon ICON_DUPLICATE = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/duplicate.png"));
    
    /** The default icon for duplicating something (rollover) */
    public final static ImageIcon ICON_DUPLICATE_R = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/duplicateR.png"));
    
    /** The default icon for editing something */
    public final static ImageIcon ICON_EDIT = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/edit.png"));
    
    /** The default icon for editing something (rollover) */
    public final static ImageIcon ICON_EDIT_R = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/editR.png"));
    
    /** The default icon for cancelling something */
    public final static ImageIcon ICON_CANCEL = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/cancel.png"));
    
    /** The default icon for cancelling something (rollover) */
    public final static ImageIcon ICON_CANCEL_R = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/cancelR.png"));
    
    /** The default icon for accepting something */
    public final static ImageIcon ICON_OK = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/ok.png"));
    
    /** The default icon for accepting something (rollover) */
    public final static ImageIcon ICON_OK_R = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/okR.png"));

    /** The default icon for selecting a path */
    public final static ImageIcon ICON_PATH = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/path.png"));
    
    /** The default icon for selecting a path (rollover) */
    public final static ImageIcon ICON_PATH_R = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/pathR.png"));

    /** The default icon for performing a search */
    public final static ImageIcon ICON_SEARCH = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/search.png"));
    
    /** The default icon for performing a search (rollover) */
    public final static ImageIcon ICON_SEARCH_R = new ImageIcon(Utils.class.getResource("/YAJSL/Swing/resources/searchR.png"));
}
