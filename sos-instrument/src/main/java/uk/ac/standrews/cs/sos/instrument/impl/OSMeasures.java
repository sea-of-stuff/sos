package uk.ac.standrews.cs.sos.instrument.impl;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.OsProcess;
import com.jezhumble.javasysmon.ProcessInfo;
import uk.ac.standrews.cs.sos.instrument.Measure;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class OSMeasures implements Measure {

    private long residentBytes;
    private long totalBytes;
    private long sysUptime;
    private long userUptime;

    public long getResidentBytes() {
        return residentBytes;
    }

    public void setResidentBytes(long residentBytes) {
        this.residentBytes = residentBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public long getSysUptime() {
        return sysUptime;
    }

    public void setSysUptime(long sysUptime) {
        this.sysUptime = sysUptime;
    }

    public long getUserUptime() {
        return userUptime;
    }

    public void setUserUptime(long userUptime) {
        this.userUptime = userUptime;
    }

    public static OSMeasures measure() {
        JavaSysMon monitor = new JavaSysMon();

        OSMeasures osMeasures = new OSMeasures();

        OsProcess process = monitor.processTree().find(monitor.currentPid());
        ProcessInfo processInfo = process.processInfo();
        osMeasures.setResidentBytes(processInfo.getResidentBytes());
        osMeasures.setTotalBytes(processInfo.getTotalBytes());
        osMeasures.setUserUptime(processInfo.getUserMillis());
        osMeasures.setSysUptime(processInfo.getSystemMillis());

        return osMeasures;
    }

    @Override
    public String toString() {

        return "Resident/Total Bytes: " + getResidentBytes() + "/" + getTotalBytes() + "\n" +
                "User/Sys Uptime Milliseconds: " + getUserUptime() + "/" + getSysUptime() + "\n\n";
    }

    @Override
    public String csvHeader() {
        return "Resident Bytes,Total Bytes,User Uptime,System Uptime";
    }

    @Override
    public String csv() {

        return getResidentBytes() + COMMA + getTotalBytes() + COMMA + getUserUptime() + COMMA + getSysUptime();
    }

    @Override
    public String tsvHeader() {
        return "Resident Bytes"+TAB+"Total Bytes"+TAB+"User Uptime"+TAB+"System Uptime";
    }

    @Override
    public String tsv() {
        return getResidentBytes() + TAB + getTotalBytes() + TAB + getUserUptime() + TAB + getSysUptime();
    }
}
