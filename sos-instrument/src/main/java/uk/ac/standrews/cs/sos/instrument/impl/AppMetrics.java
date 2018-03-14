/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module instrument.
 *
 * instrument is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * instrument is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with instrument. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
    private String message_2 = "n/a";
    private String message_3 = "n/a";
    private long userMeasure = -1;
    private long userMeasure_2 = -1;
    private long userMeasure_3 = -1;
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

    public String getMessage_2() {
        return message_2;
    }

    public void setMessage_2(String message_2) {
        this.message_2 = message_2;
    }

    public String getMessage_3() {
        return message_3;
    }

    public void setMessage_3(String message_3) {
        this.message_3 = message_3;
    }

    public long getUserMeasure() {
        return userMeasure;
    }

    public void setUserMeasure(long userMeasure) {
        this.userMeasure = userMeasure;
    }

    public long getUserMeasure_2() {
        return userMeasure_2;
    }

    public void setUserMeasure_2(long userMeasure_2) {
        this.userMeasure_2 = userMeasure_2;
    }

    public long getUserMeasure_3() {
        return userMeasure_3;
    }

    public void setUserMeasure_3(long userMeasure_3) {
        this.userMeasure_3 = userMeasure_3;
    }

    @Override
    public void setStatsType(StatsTYPE statsType) {
        this.statsTYPE = statsType;
    }

    @Override
    public void setSubType(StatsTYPE subtype) {
        this.subType = subtype;
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
        return "Timestamp (ms)"+TAB+"Time(UTC)"+TAB+
                "Message"+TAB+"Message_2"+TAB+"Message_3"+TAB+
                "User Measure"+TAB+"User Measure_2"+TAB+"User Measure_3"+TAB+
                "Class Name"+TAB+"Method Name";
    }

    @Override
    public String tsv() {
        return statsTYPE.toString() + TAB + subType.toString() + TAB +
                getNow().toEpochMilli() + TAB + getNow().toString() + TAB +
                getMessage() + TAB + getMessage_2() + TAB + getMessage_3() + TAB +
                getUserMeasure() + TAB + getUserMeasure_2() + TAB + getUserMeasure_3() + TAB +
                getStackTraceElement().getClassName() + TAB + getStackTraceElement().getMethodName();
    }

}
