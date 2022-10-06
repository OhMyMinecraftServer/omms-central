package net.zhuruoling.controller;

import net.zhuruoling.util.Util;

public class Controller {
    private String name;

    private String executable;

    private String type;

    private String launchParams;

    private String workingDir;

    private int password;

    private int rmiPort;

    public Controller() {
    }

    public Controller(String name, String executable, String type, String launchCommand, String workingDir) {
        this.name = name;
        this.executable = executable;
        this.type = type;
        this.launchParams = launchCommand;
        this.workingDir = workingDir;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Controller{" +
                "name='" + name + '\'' +
                ", executable='" + executable + '\'' +
                ", type='" + type + '\'' +
                ", launchCommand='" + launchParams + '\'' +
                ", workingDir='" + workingDir + '\'' +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLaunchParams() {
        return launchParams;
    }

    public void setLaunchParams(String launchParams) {
        this.launchParams = launchParams;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }

    public int getRmiPort() {
        return rmiPort;
    }

    public void setRmiPort(int rmiPort) {
        this.rmiPort = rmiPort;
    }

    public String toJson() {
        return Util.gson.toJson(this);
    }
}
