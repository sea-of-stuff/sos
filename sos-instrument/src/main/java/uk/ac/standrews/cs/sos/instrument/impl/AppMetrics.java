package uk.ac.standrews.cs.sos.instrument.impl;

import uk.ac.standrews.cs.sos.instrument.Metrics;

import java.time.Instant;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AppMetrics implements Metrics {

    private Instant now;
    private String message = "n/a";
    private long userMeasure = -1;
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

    public long getUserMeasure() {
        return userMeasure;
    }

    public void setUserMeasure(long userMeasure) {
        this.userMeasure = userMeasure;
    }

    public StackTraceElement getStackTraceElement() {
        return stackTraceElement;
    }

    public void setStackTraceElement(StackTraceElement stackTraceElement) {
        this.stackTraceElement = stackTraceElement;
    }

    public static AppMetrics measure(String message) {

        AppMetrics appMeasure = new AppMetrics();
        appMeasure.setNow(Instant.now());
        appMeasure.setMessage(message);
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        appMeasure.setStackTraceElement(stackTraceElements[3]);

        return appMeasure;
    }

    @Override
    public String toString() {

        return "Timestamp / Time (UTC): " + getNow().toEpochMilli() + " / " + getNow().toString() + "\n" +
                "Message: " + getMessage() + "\n" +
                "User Measure: " + getMessage() + "\n" +
                "Class / Method name: " + getStackTraceElement().getClassName() + " / " + getStackTraceElement().getMethodName();
    }

    @Override
    public String csvHeader() {
        return "Timestamp (ms),Time(UTC),Message,User Measure,Class Name,Method Name";
    }

    @Override
    public String csv() {
        return getNow().toEpochMilli() + COMMA + getNow().toString() + COMMA + getMessage() + COMMA +
                getStackTraceElement().getClassName() + COMMA + getStackTraceElement().getMethodName();
    }

    @Override
    public String tsvHeader() {
        return "Timestamp (ms)"+TAB+"Time(UTC)"+TAB+"Message"+TAB+"User Measure"+TAB+"Class Name"+TAB+"Method Name";
    }

    @Override
    public String tsv() {
        return getNow().toEpochMilli() + TAB + getNow().toString() + TAB + getMessage() + TAB + getUserMeasure() + TAB +
                getStackTraceElement().getClassName() + TAB + getStackTraceElement().getMethodName();
    }

}
