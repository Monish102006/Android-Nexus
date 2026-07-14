package com.androidnexus.model;

/**
 * Represents the installation category of an Android application.
 *
 * Categories:
 *   - SYSTEM: Applications pre-installed by the manufacturer or carrier in the system partition
 *   - USER: Third-party applications installed by the user (e.g. from Google Play Store or sideloaded)
 */
public enum ApplicationType {
    SYSTEM,
    USER
}
