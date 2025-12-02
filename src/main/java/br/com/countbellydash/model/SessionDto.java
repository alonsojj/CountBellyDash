package br.com.countbellydash.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SessionDto {

    @JsonProperty("user_id")
    private String userId;

    private SessionClientDto client;

    private int state;

    @JsonProperty("previous_state")
    private int previousState;

    @JsonProperty("chat_history")
    private List<ChatMessageDto> chatHistory;

    @JsonProperty("ia_suggestion")
    private String iaSuggestion;

    @JsonProperty("last_activity_time")
    private String lastActivityTime;

    @JsonProperty("whatsapp_instance")
    private String whatsappInstance;

    // getters e setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public SessionClientDto getClient() {
        return client;
    }

    public void setClient(SessionClientDto client) {
        this.client = client;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getPreviousState() {
        return previousState;
    }

    public void setPreviousState(int previousState) {
        this.previousState = previousState;
    }

    public List<ChatMessageDto> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(List<ChatMessageDto> chatHistory) {
        this.chatHistory = chatHistory;
    }

    public String getIaSuggestion() {
        return iaSuggestion;
    }

    public void setIaSuggestion(String iaSuggestion) {
        this.iaSuggestion = iaSuggestion;
    }

    public String getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(String lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public String getWhatsappInstance() {
        return whatsappInstance;
    }

    public void setWhatsappInstance(String whatsappInstance) {
        this.whatsappInstance = whatsappInstance;
    }
}

class ChatMessageDto {
    private String role;
    private String content;

    // getters e setters

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
