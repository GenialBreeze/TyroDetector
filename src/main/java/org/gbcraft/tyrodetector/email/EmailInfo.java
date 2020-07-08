package org.gbcraft.tyrodetector.email;

/**
 * 邮件实例
 */
public class EmailInfo {
    private String title; // 邮件标题
    private String content; // 邮件正文
    private Integer age; // 邮件生命周期，在添加一次内容后+1

    public EmailInfo(String content) {
        this.content = content;
        this.age = 1;
    }

    public EmailInfo(String title, String content) {
        this.title = title;
        this.content = content;
        this.age = 1;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void appendContent(String content) {
        this.content += "\n" + content;
        this.age++;
    }

    public Integer getAge() {
        return this.age;
    }
}
