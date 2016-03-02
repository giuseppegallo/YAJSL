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

import YAJSL.Swing.Components.ProgressBarDialog;
import YAJSL.Swing.Interfaces.GenericProgressDialog;
import YAJSL.Swing.Interfaces.TaskErrorHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 * A task to be performed.
 *
 * @author Giuseppe Gallo (ggallo76@gmail.com)
 */
public class Task extends SwingWorker<Void, Exception> {

    /**
     * An activity (sub-task).
     *
     * @author Giuseppe Gallo (ggallo76@gmail.com)
     */
    public abstract static class Activity {

        /** The task to be notified */
        private Task task = null;


        /**
         * Sets the task to be notified.
         *
         * @param task  the task to be notified
         */
        public final void setTask(Task task) {
            this.task = task;
        }

        /**
         * Increments the progress.
         *
         * @param steps  the number of steps by which the progress needs to be incremented
         */
        public final void progress(int steps) {
            if (task != null) {
                task.progress(steps);
            }
        }

        /**
         * Returns the total number of steps for this activity (negative if unkown).
         *
         * @return  the total number of steps for this activity
         */
        public abstract int getSteps();

        /**
         * Performs the activity.
         *
         * @throws Exception  in case of any issue
         */
        public abstract void executeSteps() throws Exception;
    }


    /** The dialog used for showing the progress */
    private final GenericProgressDialog dialog;

    /** The parent frame */
    private final java.awt.Container parent;

    /** The activities (sub-tasks) to be performed */
    private final Activity[] activities;

    /** The error handler used for all activties */
    private final TaskErrorHandler activityHandler;

    /** The error handler used for the entire task (at the end) */
    private final TaskErrorHandler taskHandler;

    /** The total number of steps to be performed */
    private int totalSteps = 0;

    /** The current progress */
    private int steps = 0;

    /** True if the number of steps cannot be determined */
    private boolean indeterminate = false;

    /** The first unhandled exception thrown by an activity (null = none) */
    private Exception exception = null;


    /**
     * Allocates a new Task (which also handles the progress dialog).
     *
     * @param title  the title for the progress dialog
     * @param activities  the activities (sub-tasks) to be performed
     * @param dialog  the dialog to be used for showing the progress (null = none)
     * @param activityHandler  the error handler to be used for all activities (null = no handler)
     * @param taskHandler  the error handler to be used at the end of the entire task (null = no handler)
     */
    public Task(String title, Activity[] activities, GenericProgressDialog dialog, TaskErrorHandler activityHandler, TaskErrorHandler taskHandler) {
        super();

        this.parent = (dialog == null) ? null : dialog.getParent();
        this.activities = activities;
        this.activityHandler = activityHandler;
        this.taskHandler = taskHandler;

        for (Activity a : activities) {
            int actSteps = a.getSteps();
            if (actSteps < 0) indeterminate = true;

            totalSteps += actSteps;
        }

        if (totalSteps == 0) totalSteps = 1;

        setProgress(0);

        if (dialog == null) {
            dialog = new ProgressBarDialog(null);
        } else {
            dialog.setProgress(0);
            dialog.setIndeterminate(indeterminate);
            dialog.setTitle(title);
            dialog.setLocation();
            dialog.setVisible(true);
        }
        this.dialog = dialog;

        if (parent != null) parent.setEnabled(false);
        addPropertyChangeListener(dialog);
    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            for (Activity a : activities) {
                a.setTask(this);
                try {
                    a.executeSteps();
                } catch (Exception ex) {
                    if (activityHandler != null) activityHandler.handleError(ex);
                    else throw ex;
                }
            }
        } catch (Exception ex) {
            exception = ex;
        }

        return null;
    }

    @Override
    protected void done() {
        dialog.setVisible(false);
        dialog.dispose();

        if (parent != null) {
            parent.setEnabled(true);
            parent.setVisible(true);
        }

        if (taskHandler == null) return;

        try {
            taskHandler.handleError(exception);
        } catch (Exception ex) {
            // NOTE: in this case the handler should not raise an exception, as it would be ignored
            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Increments the progress.
     *
     * @param steps  the number of steps by which the progress needs to be incremented
     */
    public final void progress(int steps) {
        if (indeterminate) return;

        this.steps += steps;
        int value = (int) Math.floor(100.0 * this.steps / totalSteps);
        if (value >= 100 && getState() != SwingWorker.StateValue.DONE) {
            value = 99;
        }

        setProgress(value);
    }

    /**
     * Returns the first unhandled exception thrown by an activity for this task (null = none).
     *
     * @return  the first unhandled exception thrown by an activity for this task (null = none)
     */
    public final Exception getError() {
        return exception;
    }

    /**
     * In case there were any unhandled exceptions thrown by the activities for this task,
     * re-throws the first one.
     *
     * @throws Exception  in case of any exception thrown by the activities for this task
     */
    public void checkError() throws Exception {
        if (exception != null) throw exception;
    }
}
