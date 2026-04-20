package com.example.guardrailService.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private Long authorId;

    @Enumerated(EnumType.STRING)
    private AuthorType authorType;

    private Long parentCommentId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int depthLevel;

    @CreationTimestamp
    @Column(nullable = false , updatable = false)
    private LocalDateTime createdAt;
}
