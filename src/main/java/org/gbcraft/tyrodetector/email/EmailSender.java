package org.gbcraft.tyrodetector.email;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.SimpleEmail;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.config.EmailConfig;

/**
 * 邮件发送者
 */
public class EmailSender {

    /**
     * 发送一封邮件
     *
     * @param title   邮件标题
     * @param content 邮件内容
     */
    public void send(String title, String content) {
        EmailConfig config = TyroDetector.getPlugin().getEmailConfig();
        SimpleEmail email = new SimpleEmail();
        email.setHostName(config.getHostname());
        try {
            email.setSSLOnConnect(config.isSsl());
            email.setStartTLSEnabled(config.isTls());
            email.setSmtpPort(config.getPort());
            // 收件人邮箱
            email.addTo(config.getReceiver().toArray(new String[0]));

            // 发件人邮箱
            email.setFrom(config.getSender());
            email.setAuthentication(config.getSender(), config.getPassword());
            // 邮件主题
            String servername = config.getServername();
            if (StringUtils.isNotBlank(servername)) {
                title = servername + " - " + title;
            }
            email.setSubject(title);
            // 邮件内容
            email.setMsg(content);

            // 发送邮件
            email.send();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
