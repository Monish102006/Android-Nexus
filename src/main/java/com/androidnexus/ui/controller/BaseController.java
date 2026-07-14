package com.androidnexus.ui.controller;

/**
 * Base view controller that provides references back to the main shell.
 *
 * Allows mounted sub-views to trigger navigation shifts or update status bar states.
 */
public abstract class BaseController {

    protected ShellController shellController;

    public void setShellController(ShellController shellController) {
        this.shellController = shellController;
    }
}
