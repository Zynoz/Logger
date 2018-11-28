package com.logger;

/**
 * Append types that can be used.
 */
public enum AppendType {
    /**
     * Appends new log output to previous log output.
     */
    APPEND,
    /**
     * Overwrites the log output from previous log output.
     */
    OVERWRITE,
    /**
     * Renames the old log file and creates a new log file.
     */
    NEW,
    /**
     * Logs output to console but not to file.
     */
    CONSOLE,
}
