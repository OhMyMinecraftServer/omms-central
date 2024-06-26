package icu.takeneko.omms.central.system.info;

import java.util.List;

public class ProcessorInfo {
    private int physicalCPUCount;
    private int logicalProcessorCount;
    private String processorName;
    private double cpuLoadAvg;
    private String processorId;
    private double cpuTemp;
    private List<Long> cpuFreqs;

    public double getCpuTemp() {
        return cpuTemp;
    }

    public void setCpuTemp(double cpuTemp) {
        this.cpuTemp = cpuTemp;
    }

    public int getPhysicalCPUCount() {
        return physicalCPUCount;
    }

    public void setPhysicalCPUCount(int physicalCPUCount) {
        this.physicalCPUCount = physicalCPUCount;
    }

    public int getLogicalProcessorCount() {
        return logicalProcessorCount;
    }

    public void setLogicalProcessorCount(int logicalCoreCount) {
        this.logicalProcessorCount = logicalCoreCount;
    }

    public double getCpuLoadAvg() {
        return cpuLoadAvg;
    }

    public void setCpuLoadAvg(double cpuLoadAvg) {
        this.cpuLoadAvg = cpuLoadAvg;
    }

    public String getProcessorName() {
        return processorName;
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }


    public String getProcessorId() {
        return processorId;
    }

    public void setProcessorId(String processorId) {
        this.processorId = processorId;
    }

    public List<Long> getCpuFreqs() {
        return cpuFreqs;
    }

    public void setCpuFreqs(List<Long> cpuFreqs) {
        this.cpuFreqs = cpuFreqs;
    }

    @Override
    public String toString() {
        return "ProcessorInfo{" +
                "physicalCPUCount=" + physicalCPUCount +
                ", logicalProcessorCount=" + logicalProcessorCount +
                ", processorName='" + processorName + '\'' +
                ", cpuLoadAvg=" + cpuLoadAvg +
                ", processorId='" + processorId + '\'' +
                ", cpuTemp=" + cpuTemp +
                '}';
    }
}
