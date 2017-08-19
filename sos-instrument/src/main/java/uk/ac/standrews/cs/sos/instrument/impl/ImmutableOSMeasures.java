package uk.ac.standrews.cs.sos.instrument.impl;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.MemoryStats;
import com.jezhumble.javasysmon.OsProcess;
import com.jezhumble.javasysmon.ProcessInfo;
import uk.ac.standrews.cs.sos.instrument.Metrics;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ImmutableOSMeasures implements Metrics {

    private String osName;
    private int noCPUs;
    private long cpuHZ;

    private long memTotalBytes;
    private long memFreeBytes;

    private int processPID;
    private String processName;
    private long residentBytes;
    private long totalBytes;
    private long sysUptime;
    private long userUptime;

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public int getNoCPUs() {
        return noCPUs;
    }

    public void setNoCPUs(int noCPUs) {
        this.noCPUs = noCPUs;
    }

    public long getCpuHZ() {
        return cpuHZ;
    }

    public void setCpuHZ(long cpuHZ) {
        this.cpuHZ = cpuHZ;
    }

    public long getMemTotalBytes() {
        return memTotalBytes;
    }

    public void setMemTotalBytes(long memTotalBytes) {
        this.memTotalBytes = memTotalBytes;
    }

    public long getMemFreeBytes() {
        return memFreeBytes;
    }

    public void setMemFreeBytes(long memFreeBytes) {
        this.memFreeBytes = memFreeBytes;
    }

    public int getProcessPID() {
        return processPID;
    }

    public void setProcessPID(int processPID) {
        this.processPID = processPID;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

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

    public static ImmutableOSMeasures measure() {
        JavaSysMon monitor = new JavaSysMon();

        ImmutableOSMeasures osMeasures = new ImmutableOSMeasures();

        osMeasures.setOsName(monitor.osName());
        osMeasures.setNoCPUs(monitor.numCpus());
        osMeasures.setCpuHZ(monitor.cpuFrequencyInHz());

        MemoryStats memoryStats = monitor.swap();
        osMeasures.setMemTotalBytes(memoryStats.getTotalBytes());
        osMeasures.setMemFreeBytes(memoryStats.getFreeBytes());

        osMeasures.setProcessPID(monitor.currentPid());

        OsProcess process = monitor.processTree().find(monitor.currentPid());
        ProcessInfo processInfo = process.processInfo();
        osMeasures.setProcessName(processInfo.getName());
        osMeasures.setResidentBytes(processInfo.getResidentBytes());
        osMeasures.setTotalBytes(processInfo.getTotalBytes());
        osMeasures.setUserUptime(processInfo.getUserMillis());
        osMeasures.setSysUptime(processInfo.getSystemMillis());

        return osMeasures;
    }

    @Override
    public String toString() {

        return "OSName: " + getOsName() + "\n" +
                "NoCPUs: " + getNoCPUs() + "\n" +
                "CPU Hz: " + getCpuHZ() + "\n" +
                "Free OS Memory: " + getMemFreeBytes() + "/" + getMemTotalBytes() + "\n" +
                "Process ID/Name: " + getProcessPID() + "/" + getProcessName() + "\n" +
                "Resident/Total Bytes: " + getResidentBytes() + "/" + getTotalBytes() + "\n" +
                "User/Sys Uptime Milliseconds: " + getUserUptime() + "/" + getSysUptime() + "\n\n";
    }

    @Override
    public String csvHeader() {
        return "OS,No CPUs,CPU Hz,Mem Free Bytes,Mem Total Bytes,PID,Process Name,Resident Bytes,Total Bytes,User Uptime,System Uptime";
    }

    @Override
    public String csv() {

        return getOsName() + COMMA + getNoCPUs() + COMMA + getCpuHZ() + COMMA + getMemFreeBytes() + COMMA + getMemTotalBytes() + COMMA +
                getProcessPID() + COMMA + getProcessName() + COMMA + getResidentBytes() + COMMA + getTotalBytes() + COMMA +
                getUserUptime() + COMMA + getSysUptime();
    }

    @Override
    public String tsvHeader() {
        return "OS"+TAB+"No CPUs"+TAB+"CPU Hz"+TAB+"Mem Free Bytes"+TAB+"Mem Total Bytes"+TAB+"PID"+TAB+"Process Name"+TAB+"Resident Bytes"+TAB+"Total Bytes"+TAB+"User Uptime"+TAB+"System Uptime";
    }

    @Override
    public String tsv() {
        return getOsName() + TAB + getNoCPUs() + TAB + getCpuHZ() + TAB + getMemFreeBytes() + TAB + getMemTotalBytes() + TAB +
                getProcessPID() + TAB + getProcessName() + TAB + getResidentBytes() + TAB + getTotalBytes() + TAB +
                getUserUptime() + TAB + getSysUptime();
    }
}
