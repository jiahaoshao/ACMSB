package net.fangyi.acmsb.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.*;
import lombok.Data;

import java.lang.reflect.Type;
import java.util.List;

@Data
@Entity
public class ChatRequestCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int chatid;
    private int uid;
    @Lob
    private String messages; // Store messages as JSON string

    @Transient
    private static final Gson gson = new Gson();
    @Transient
    private static final Type messageListType = new TypeToken<List<Message>>() {}.getType();

    // Convert List<Message> to JSON string
    public void setMessages(List<Message> messages) {
        this.messages = gson.toJson(messages);
    }

    // Convert JSON string to List<Message>
    public List<Message> getMessages() {
        return gson.fromJson(this.messages, messageListType);
    }

    // Constructor to copy data from ChatRequest
    public ChatRequestCopy(ChatRequest chatRequest) {
        this.chatid = chatRequest.getChatid();
        this.uid = chatRequest.getUid();
        setMessages(chatRequest.getMessages());
    }

    // Default constructor
    public ChatRequestCopy() {}
}