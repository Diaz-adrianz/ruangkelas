package id.adrianz.ruangkelas.model;

import jakarta.persistence.Entity;

@Entity
public class EmailNotification extends Notification {

    private String email;
    private String subject;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
}