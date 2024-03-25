package com.languageapp.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Change<T extends Serializable> implements Serializable {
    LocalDateTime dateTime;
    private T oldValue;
    private T newValue;
    private User user;
    private ChangeType type;

    public Change(T oldValue, T newValue, User user, LocalDateTime dateTime) {
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.user = user;
        this.dateTime = dateTime;
        this.type = ChangeType.EDIT;
        if (oldValue == null) {
            this.type = ChangeType.ADDITION;
        } else if (newValue == null) {
            this.type = ChangeType.REMOVAL;
        }
    }

    @Override
    public String toString() {
        return "Change{" +
                "dateTime=" + dateTime +
                ", oldValue=" + oldValue +
                ", newValue=" + newValue +
                ", user=" + user +
                ", type=" + type +
                '}';
    }

    public ChangeType getType() {
        return type;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public T getOldValue() {
        return oldValue;
    }

    public void setOldValue(T oldValue) {
        this.oldValue = oldValue;
    }

    public T getNewValue() {
        return newValue;
    }

    public void setNewValue(T newValue) {
        this.newValue = newValue;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
