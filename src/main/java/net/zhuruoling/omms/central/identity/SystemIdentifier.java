package net.zhuruoling.omms.central.identity;

import oshi.SystemInfo;

public class SystemIdentifier {
    String serialNumber;
    String hardwareUuid;

    public SystemIdentifier(String serialNumber, String hardwareUuid) {
        this.serialNumber = serialNumber;
        this.hardwareUuid = hardwareUuid;
    }

    public static SystemIdentifier create() {
        var computerSystem = new SystemInfo().getHardware().getComputerSystem();
        return new SystemIdentifier(computerSystem.getSerialNumber(), computerSystem.getHardwareUUID());
    }

    public boolean anyEquals(SystemIdentifier systemIdentifier) {
        return this.serialNumber.equals(systemIdentifier.serialNumber) || this.hardwareUuid.equals(systemIdentifier.hardwareUuid);
    }
}
