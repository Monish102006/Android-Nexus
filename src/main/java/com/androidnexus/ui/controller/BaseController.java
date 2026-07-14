package com.androidnexus.ui.controller;

/**
 * Base view controller that provides references back to the main shell.
 *
 * Exposes lifecycle hooks like {@code cleanup()} to manage resources
 * when the view is detached.
 */
public abstract class BaseController {

    protected ShellController shellController;

    public void setShellController(ShellController shellController) {
        this.shellController = shellController;
    }

    /**
     * Called when the user navigates away from this view.
     * Override to stop background sync, timers, or daemon threads.
     */
    public void cleanup() {
        // Default empty implementation
    }

    /**
     * Called when the user types in the global search bar of the Shell.
     * Override to filter active tables, lists, or trigger remote search.
     */
    public void onGlobalSearch(String query) {
        // Default empty implementation
    }
}
