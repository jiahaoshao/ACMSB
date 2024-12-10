package net.fangyi.acmsb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class Message {
    private String role;
    private String content;

    // Constructors
    public Message() {}

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

}
