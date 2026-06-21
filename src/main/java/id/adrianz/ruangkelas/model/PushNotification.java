package id.adrianz.ruangkelas.model;

import jakarta.persistence.Entity;

@Entity
public class PushNotification extends Notification {

    private String title;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}