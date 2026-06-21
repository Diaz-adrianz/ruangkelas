
package id.adrianz.ruangkelas.model;

import java.beans.Transient;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "documents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Class clazz;

    private String title;

    private String fileName;

    private String fileType;

    private String filePath;

    private Long fileSize;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Transient
    public String formatFileSize() {
        if (this.fileSize < 1024) {
            return this.fileSize + " B";
        }
        if (this.fileSize < 1024 * 1024) {
            return String.format("%.1f KB", this.fileSize / 1024.0);
        }
        return String.format("%.1f MB", this.fileSize / (1024.0 * 1024.0));
    }
}