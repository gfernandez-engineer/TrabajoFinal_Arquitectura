package com.tecsup.lms.course.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private String instructor;

    @Builder.Default
    private Boolean published = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.published == null) {
            this.published = false;
        }
    }

    public void publish() {
        this.published = true;
    }

    public boolean isPublished() {
        return Boolean.TRUE.equals(this.published);
    }
}
