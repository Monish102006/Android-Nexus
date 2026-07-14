/**
 * The data models of the Android Nexus application.
 *
 * <p><strong>Purpose:</strong>
 * This package contains pure data representations (POJOs, records, and enums) that capture
 * device metrics, package installations, active notifications, and file metadata.
 *
 * <p><strong>Allowed Dependencies:</strong>
 * <ul>
 *   <li>None. Models should be clean domain objects containing only attributes, getters,
 *       setters, and toString/equals/hashCode methods.</li>
 * </ul>
 *
 * <p><strong>Architectural Rules:</strong>
 * <ul>
 *   <li>Models MUST NOT contain business logic, controller hooks, or execute shell commands.</li>
 * </ul>
 */
package com.androidnexus.model;
