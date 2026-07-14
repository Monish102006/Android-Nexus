/**
 * The controller layer of the Android Nexus application.
 *
 * <p><strong>Purpose:</strong>
 * Controllers act as the entry points (facades) for the presentation layer (Main CLI,
 * JavaFX Dashboard, AI Assistant). They are responsible for:
 * <ul>
 *   <li>Validating parameters and inputs at the boundary.</li>
 *   <li>Delegating business logic to appropriate services.</li>
 *   <li>Translating lower-level exceptions when needed or coordinating workflows.</li>
 * </ul>
 *
 * <p><strong>Allowed Dependencies:</strong>
 * <ul>
 *   <li>{@code com.androidnexus.model} (Data representation objects)</li>
 *   <li>{@code com.androidnexus.exception} (Error handling types)</li>
 *   <li>{@code com.androidnexus.service} (Direct service delegation)</li>
 * </ul>
 *
 * <p><strong>Architectural Rules:</strong>
 * <ul>
 *   <li>Controllers MUST NOT execute ADB process commands directly. All shell or subprocess executions
 *       must be delegated to the service/ADB layers.</li>
 *   <li>Methods must remain stateless static facades until Module 8 (JavaFX Dashboard integration).</li>
 * </ul>
 */
package com.androidnexus.controller;
