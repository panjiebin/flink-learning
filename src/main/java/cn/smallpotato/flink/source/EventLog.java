package cn.smallpotato.flink.source;

import java.util.Map;

/**
 * @author small potato
 */
public class EventLog {
    private long guid;
    private String sessionId;
    private String eventId;
    private long timestamp;
    private Map<String, String> eventInfo;

    public long getGuid() {
        return guid;
    }

    public void setGuid(long guid) {
        this.guid = guid;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(Map<String, String> eventInfo) {
        this.eventInfo = eventInfo;
    }
}
