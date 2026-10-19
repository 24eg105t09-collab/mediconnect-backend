package com.mediconnect.hospital.dto;

import java.util.List;

public class ChatResponse {
    private String reply;
    private String intent;
    private List<String> suggestions;
    private List<AvailableSlotDto> slots;
    private Object data;

    public ChatResponse() {}

    public ChatResponse(String reply, String intent, List<String> suggestions, List<AvailableSlotDto> slots, Object data) {
        this.reply = reply;
        this.intent = intent;
        this.suggestions = suggestions;
        this.slots = slots;
        this.data = data;
    }

    public static ChatResponse text(String reply, List<String> suggestions) {
        return new ChatResponse(reply, "INFO", suggestions, null, null);
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }

    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }

    public List<AvailableSlotDto> getSlots() { return slots; }
    public void setSlots(List<AvailableSlotDto> slots) { this.slots = slots; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
