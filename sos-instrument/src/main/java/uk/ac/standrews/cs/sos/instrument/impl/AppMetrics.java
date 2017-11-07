package uk.ac.standrews.cs.sos.instrument.impl;

import uk.ac.standrews.cs.sos.instrument.Metrics;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;

import java.time.Instant;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AppMetrics implements Metrics {

    private Instant now;
    private String message = "n/a";
    private long userMeasure = -1;
    private StackTraceElement stackTraceElement;
    private StatsTYPE statsTYPE;
    private StatsTYPE subType;

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
    public String tsvHeader() {
        return "Timestamp (ms)"+TAB+"Time(UTC)"+TAB+"Message"+TAB+"User Measure"+TAB+"Class Name"+TAB+"Method Name";
    }

    @Override
    public String tsv() {
        return statsTYPE.toString() + TAB + subType.toString() + TAB +
                getNow().toEpochMilli() + TAB + getNow().toString() + TAB + getMessage() + TAB + getUserMeasure() + TAB +
                getStackTraceElement().getClassName() + TAB + getStackTraceElement().getMethodName();
    }

    @Override
    public void setStatsType(StatsTYPE statsType) {
        this.statsTYPE = statsType;
    }

    @Override
    public void setSubType(StatsTYPE subtype) {
        this.subType = subtype;
    }

}
