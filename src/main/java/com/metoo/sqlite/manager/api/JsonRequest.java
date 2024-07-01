package com.metoo.sqlite.manager.api;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-29 21:13
 */
public class JsonRequest {

    private String taskuuid;
    private String ip;

    public String getTaskuuid() {
        return taskuuid;
    }

    public void setTaskuuid(String taskuuid) {
        this.taskuuid = taskuuid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
