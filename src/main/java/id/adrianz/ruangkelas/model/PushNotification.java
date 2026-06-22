package id.adrianz.ruangkelas.model;

import jakarta.persistence.Entity;

@Entity
public class PushNotification extends Notification {

    // Field 'id' dihapus karena sudah otomatis diwarisi dari class induk (Notification)

    private String title;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}