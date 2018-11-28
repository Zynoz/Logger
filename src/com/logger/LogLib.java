package com.logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LogLib is a logging library that helps you format your error/warning/debug/info messages and outputs them to the console or writes them to a log file.
 * This logging tool is NOT optimized for performance.
 * @version 1.1
 */
public class LogLib extends Thread {

    private Path path;
    private AppendType appendType;
    private String dateTimeFormat;
    private Path fileName;

    /**
     * Constructor for LogLib without specific path for log file and AppendType.
     * Log file will be save in the users home directory.
     */
    public LogLib() {
        setup();
    }

    /**
     * Constructor for LogLib without specific file path. Log file will be save in the users home directory.
     * @param appendType What should happen with the old file?
     */
    public LogLib(AppendType appendType) {
        setAppendType(appendType);
        setup();
    }

    /**
     * Constructor for LogLib with specific file path.
     * @param path Path where log file will be stored.
     */
    public LogLib(Path path) {
        setPath(path);
        setup();
    }

    /**
     * Constructor for LogLib with specific file path. Log file will be stored in the given path.
     * @param path Path where log file will be stored.
     * @param fileName Name of the log file. File name has to have a file extension.
     * @param appendType What should happen with the old file?
     */
    public LogLib(Path path, String fileName , AppendType appendType) {
        setPath(path);
        setAppendType(appendType);
        setFileName(fileName);
        setup();
    }

    /**
     * Constructor for LogLib with specific file path. Log file will be stored in the given path.
     * @param path Path where log file will be stored.
     * @param fileName Name of the log file. File name has to have a file extension.
     * @param appendType What should happen with the old file?
     * @param dateTimeFormat How the date and time should be formatted.
     */
    public LogLib(Path path, String fileName , AppendType appendType, String dateTimeFormat) {
        setPath(path);
        setAppendType(appendType);
        setFileName(fileName);
        setDateTimeFormat(dateTimeFormat);
        setup();
    }

    /**
     * Creates an error message.
     * @param message The error message.
     */
    public void e(String message) {
        formatMessage(message, ErrorType.ERROR);
    }

    /**
     * Creates a warning message.
     * @param message The warning message.
     */
    public void w(String message) {
        formatMessage(message, ErrorType.WARNING);
    }

    /**
     * Creates a debug message.
     * @param message The debug message.
     */
    public void d(String message) {
        formatMessage(message, ErrorType.DEBUG);
    }

    /**
     * Creates an info message.
     * @param message The info message.
     */
    public void i(String message) {
        formatMessage(message, ErrorType.INFO);
    }

    /**
     * Sets up the the files to write to.
     */
    private void setup() {
        File file = new File(String.valueOf(getFile()));
        File test = new File(getPath() + "/logfile_old.txt");
        switch (getAppendType()) {
            case OVERWRITE:
                file.delete();
                System.out.println("OVERWRITE");
                break;
            case NEW:
                file.renameTo(test);
                file = new File(String.valueOf(getFile()));
                file.delete();
                break;
        }
        if (getAppendType() != AppendType.CONSOLE) {
            try {
                file.createNewFile();
                printHeader();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Prints a header containing time and date to the file before logging the errors.
     */
    private void printHeader() {
        System.out.println("log file: " + getFile());
        String newEntry = "--------------------NEW RUN: " + new SimpleDateFormat(getDateTimeFormat()).format(new Date()) + "--------------------" + System.lineSeparator();
        try {
            Files.write(getFile(), newEntry.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param fileName File name to set (including file extension).
     */
    private void setFileName(String fileName) {
        if (fileName != null) {
            this.fileName = Paths.get(fileName);
        }
    }

    /**
     * @return The file path.
     */
    private Path getFile() {
        if (fileName == null) {
            return Paths.get(String.valueOf(getPath()), "/", "log.txt");
        } else {
            return Paths.get(String.valueOf(getPath()), "/", String.valueOf(fileName));
        }
    }

    /**
     * @param path Path to store the log file to.
     */
    private void setPath(Path path) {
        this.path = path;
    }

    /**
     * @return Path where log file is stored.
     */
    private Path getPath() {
        if (path == null) {
            path = Paths.get(System.getProperty("user.home"));
        }
        return path;
    }

    /**
     * @param appendType What should happen with the old file?
     */
    private void setAppendType(AppendType appendType) {
        this.appendType = appendType;
    }

    /**
     * @return Append type.
     */
    private AppendType getAppendType() {
        if (appendType == null) {
            appendType = AppendType.APPEND;
        }
        return appendType;
    }

    /**
     * @param dateTimeFormat Date time format to set.
     */
    private void setDateTimeFormat(String dateTimeFormat) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimeFormat);
            simpleDateFormat.format(new Date());
        } catch (Exception e) {
            throw new IllegalArgumentException("date time format is not valid.");
        }
        this.dateTimeFormat = dateTimeFormat;
    }

    /**
     * @return current date time format.
     */
    private String getDateTimeFormat() {
        if (dateTimeFormat == null) {
            return "dd.MM HH:mm:ss";
        }
        return dateTimeFormat;
    }

    /**
     * Formats message and writes it to console/file.
     * @param message The message to write.
     * @param errorType The error type.
     */
    private void formatMessage(String message, ErrorType errorType) {
        Throwable throwable = new Throwable();
        StackTraceElement[] elements = throwable.getStackTrace();

        String callerMethodName = elements[elements.length - 1].getMethodName();
        String callerClassName = elements[elements.length - 1].getClassName();
        int lineNumber = elements[elements.length - 1].getLineNumber();
        elements[elements.length - 1].isNativeMethod();

        String timeStamp = new SimpleDateFormat(getDateTimeFormat()).format(new Date());

        String error = String.format("%s: [%s] in method %s from class %s in line %s: %s.%n", errorType, timeStamp, callerMethodName, callerClassName, lineNumber, message);

        if (getAppendType() == AppendType.CONSOLE) {
            System.out.println(error);
        } else {
            append(error);
        }
    }

    /**
     * Adds error line to log file.
     * @param error the error to print.
     */
    private void append(String error) {
        try {
            Files.write(Paths.get(String.valueOf(getFile())), error.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}