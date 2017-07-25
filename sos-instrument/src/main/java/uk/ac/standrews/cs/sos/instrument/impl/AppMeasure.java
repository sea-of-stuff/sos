package uk.ac.standrews.cs.sos.instrument.impl;

import uk.ac.standrews.cs.sos.instrument.Measure;

import java.time.Instant;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AppMeasure implements Measure {

    private Instant now;
    private String message;
    private StackTraceElement stackTraceElement;

    public Instant getNow() {
        return now;
    }

    public void setNow(Instant now) {
        this.now = now;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public StackTraceElement getStackTraceElement() {
        return stackTraceElement;
    }

    public void setStackTraceElement(StackTraceElement stackTraceElement) {
        this.stackTraceElement = stackTraceElement;
    }

    public static AppMeasure measure(String message) {

        AppMeasure appMeasure = new AppMeasure();
        appMeasure.setNow(Instant.now());
        appMeasure.setMessage(message);
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        appMeasure.setStackTraceElement(stackTraceElements[3]);

        return appMeasure;
    }

    @Override
    public String toString() {

        return "TODO";
    }

    @Override
    public String csvHeader() {
        return "Timestamp (ms),Time(UTC),Message,Class Name,Method Name";
    }

    @Override
    public String csv() {
        return getNow().toEpochMilli() + COMMA + getNow().toString() + COMMA + getMessage() + COMMA +
                getStackTraceElement().getClassName() + COMMA + getStackTraceElement().getMethodName();
    }
}
