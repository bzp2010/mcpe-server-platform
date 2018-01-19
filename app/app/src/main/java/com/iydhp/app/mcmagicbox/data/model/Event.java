package com.iydhp.app.mcmagicbox.data.model;

import android.os.Bundle;

/**
 * EventBus消息数据
 */
public class Event {

    public enum EventId{
        LOGIN_FINISH,
        LOGOUT,
        PROFILE_CHANGE
    }

    private EventId eventId;
    private Bundle eventData;

    public Event(EventId eventId, Bundle eventData) {
        this.eventId = eventId;
        this.eventData = eventData;
    }

    public EventId getEventId() {
        return eventId;
    }

    public void setEventId(EventId eventId) {
        this.eventId = eventId;
    }

    public Bundle getEventData() {
        return eventData;
    }

    public void setEventData(Bundle eventData) {
        this.eventData = eventData;
    }
}
