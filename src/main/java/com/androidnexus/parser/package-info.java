/**
 * The parser layer of the Android Nexus application.
 *
 * <p><strong>Purpose:</strong>
 * Parsers are responsible for translating unstructured text output (stdout from ADB shell commands like
 * dumpsys, pm, ls) into structured Java model objects.
 *
 * <p><strong>Allowed Dependencies:</strong>
 * <ul>
 *   <li>{@code com.androidnexus.model} (Target data representations)</li>
 * </ul>
 *
 * <p><strong>Architectural Rules:</strong>
 * <ul>
 *   <li>Parsers MUST be completely stateless. They should only operate on input arguments (e.g. String output)
 *       and return mapped objects/lists. This ensures they are fully unit-testable without requiring device connections.</li>
 *   <li>Parsers MUST NOT depend on the adb package, controllers, or services.</li>
 * </ul>
 */
package com.androidnexus.parser;
