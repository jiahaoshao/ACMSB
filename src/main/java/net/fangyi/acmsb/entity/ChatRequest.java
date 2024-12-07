package net.fangyi.acmsb.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class ChatRequest {
    private int chatid;
    private int uid;
    private List<Message> messages;

    // 无参构造器
    public ChatRequest() {}

    // 带参构造器
    public ChatRequest(List<Message> messages) {
        this.messages = messages;
    }

}
