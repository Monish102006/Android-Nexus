/**
 * The service layer of the Android Nexus application.
 *
 * <p><strong>Purpose:</strong>
 * Services contain the core business logic of the application. They orchestrate command
 * invocations, manage data operations, map high-level requests to specific shell parameters,
 * and delegate data format parsing to designated parser classes.
 *
 * <p><strong>Allowed Dependencies:</strong>
 * <ul>
 *   <li>{@code com.androidnexus.model} (Data model representations)</li>
 *   <li>{@code com.androidnexus.adb} (ADB shell and process execution)</li>
 *   <li>{@code com.androidnexus.exception} (Error handling hierarchy)</li>
 *   <li>{@code com.androidnexus.parser} (Dedicated output parsing)</li>
 *   <li>{@code com.androidnexus.utils} (Utility constants and formatting)</li>
 * </ul>
 *
 * <p><strong>Architectural Rules:</strong>
 * <ul>
 *   <li><strong>Rule 5:</strong> Never parse inside Services. Raw text parsing (e.g. from dumpsys, ls, pm list)
 *       MUST be delegated to dedicated classes in the {@code com.androidnexus.parser} package.</li>
 *   <li>Services must communicate with the device strictly via the {@code CommandExecutor} in the adb package.</li>
 * </ul>
 */
package com.androidnexus.service;
