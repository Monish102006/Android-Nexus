/**
 * Low-level Android Debug Bridge (ADB) execution wrapper.
 *
 * <p><strong>Purpose:</strong>
 * This package acts as the direct boundary to the operating system shell and command-line execution.
 * It manages:
 * <ul>
 *   <li>Spawning OS processes for ADB client commands.</li>
 *   <li>Enforcing execution timeouts to prevent process hangs.</li>
 *   <li>Reading stdout and stderr concurrently to prevent buffer deadlocks.</li>
 *   <li>Basic status verification (DeviceDetector, AdbManager).</li>
 * </ul>
 *
 * <p><strong>Allowed Dependencies:</strong>
 * <ul>
 *   <li>{@code com.androidnexus.exception} (ADB exceptions)</li>
 * </ul>
 *
 * <p><strong>Architectural Rules:</strong>
 * <ul>
 *   <li>This is the ONLY package allowed to call {@link java.lang.ProcessBuilder} or manage {@link java.lang.Process} instances.</li>
 *   <li>All command returns should wrap values inside {@code CommandResult} objects to provide explicit exit code and error stream context.</li>
 * </ul>
 */
package com.androidnexus.adb;
