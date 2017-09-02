package com.xahaolan.emanage.http.bean;

import java.io.Serializable;

/**
 * Created by aiodiy on 2016/12/23.
 */

public class RepMsg implements Serializable {
    private String from;//发送账号
    private String to; //接收账号
    /*type = 0  系统消息
            type = 1  添加好友
            type = 2  删除好友
            type = 3  建立群组
            type = 4  退出群组
            type = 5  邀请加群
            type = 6  聊天消息*/
    private int type; //发送类型
    private String content; //发送内容
    private int timestamp; //int64 时间戳

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
