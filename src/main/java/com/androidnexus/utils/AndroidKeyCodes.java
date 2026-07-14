package com.androidnexus.utils;

/**
 * Android keyevent codes used with {@code adb shell input keyevent <code>}.
 *
 * Why this class exists:
 * ---------------------
 * Without it, key codes are scattered as magic numbers ("3", "4", "26")
 * across NavigationController, PowerController, and AudioController.
 * A typo in a magic number (like sending "3" for volumeUp instead of "24")
 * produces no compile error and no runtime error — it just silently
 * presses the wrong button on the phone.
 *
 * Named constants make the intent self-documenting and make typos
 * impossible (the compiler catches misspelled constant names).
 *
 * Reference: Android AOSP KeyEvent.java
 * https://developer.android.com/reference/android/view/KeyEvent
 */
public final class AndroidKeyCodes {

    private AndroidKeyCodes() {
        // Utility class — no instantiation
    }

    // ── Navigation ──────────────────────────────────────────────────────
    /** KEYCODE_HOME — Go to home screen */
    public static final String HOME = "3";

    /** KEYCODE_BACK — Navigate back */
    public static final String BACK = "4";

    /** KEYCODE_APP_SWITCH — Open recent apps / task switcher */
    public static final String APP_SWITCH = "187";

    // ── Power ───────────────────────────────────────────────────────────
    /** KEYCODE_POWER — Toggle screen on/off, long-press for power menu */
    public static final String POWER = "26";

    // ── Volume ──────────────────────────────────────────────────────────
    /** KEYCODE_VOLUME_UP — Increase media/ringer volume */
    public static final String VOLUME_UP = "24";

    /** KEYCODE_VOLUME_DOWN — Decrease media/ringer volume */
    public static final String VOLUME_DOWN = "25";

    /** KEYCODE_VOLUME_MUTE — Toggle mute */
    public static final String VOLUME_MUTE = "164";

    // ── Media ───────────────────────────────────────────────────────────
    /** KEYCODE_MEDIA_PLAY_PAUSE */
    public static final String MEDIA_PLAY_PAUSE = "85";

    /** KEYCODE_MEDIA_NEXT */
    public static final String MEDIA_NEXT = "87";

    /** KEYCODE_MEDIA_PREVIOUS */
    public static final String MEDIA_PREVIOUS = "88";

    // ── Camera ──────────────────────────────────────────────────────────
    /** KEYCODE_CAMERA — Launch camera app / take photo */
    public static final String CAMERA = "27";

    // ── Miscellaneous ───────────────────────────────────────────────────
    /** KEYCODE_ENTER */
    public static final String ENTER = "66";

    /** KEYCODE_DEL — Backspace / delete */
    public static final String DELETE = "67";

    /** KEYCODE_MENU — Legacy menu button */
    public static final String MENU = "82";

    /** KEYCODE_SEARCH */
    public static final String SEARCH = "84";

    /** KEYCODE_WAKEUP — Wake the device without toggling screen */
    public static final String WAKEUP = "224";

    /** KEYCODE_SLEEP — Put device to sleep without toggling screen */
    public static final String SLEEP = "223";
}
