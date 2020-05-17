package org.gbcraft.tyrodetector.config;

import java.util.List;

/**
 * 邮件配置文件实例
 */
public class EmailConfig {
    private final Boolean ssl;
    private final Boolean tls;
    private final List<String> receiver; // 收件人列表
    private final String hostname;
    private final Integer port;
    private final String sender;
    private final String password;
    private final String servername;
    private final Integer age;
    private final Integer time;

    public EmailConfig() {
        this.ssl = Boolean.parseBoolean(ConfigReader.getEmailParam("ssl"));
        this.tls = Boolean.parseBoolean(ConfigReader.getEmailParam("tls"));
        this.receiver = ConfigReader.getEmailList("receiver");
        this.hostname = ConfigReader.getEmailParam("hostname");
        this.port = Integer.valueOf(ConfigReader.getEmailParam("port"));
        this.sender = ConfigReader.getEmailParam("sender");
        this.password = ConfigReader.getEmailParam("password");
        this.servername = ConfigReader.getEmailParam("servername");
        this.age = Integer.valueOf(ConfigReader.getEmailParam("age"));
        this.time = Integer.valueOf(ConfigReader.getEmailParam("time"));
    }

    public List<String> getReceiver() {
        return receiver;
    }

    public String getHostname() {
        return hostname;
    }

    public Integer getPort() {
        return port;
    }

    public String getSender() {
        return sender;
    }

    public String getPassword() {
        return password;
    }

    public Boolean isSsl() {
        return ssl;
    }

    public Boolean isTls() {
        return tls;
    }

    public String getServername() {
        return servername;
    }

    public Integer getAge() {
        return age;
    }

    public Integer getTime() {
        return time;
    }
}
