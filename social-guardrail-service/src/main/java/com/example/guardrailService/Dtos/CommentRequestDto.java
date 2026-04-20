package com.example.guardrailService.Dtos;

import com.example.guardrailService.entities.AuthorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequestDto {
    @NotNull(message = "Author ID is required")
    private Long authorId;

    @NotNull(message = "Author type is required")
    private AuthorType authorType;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    private Long parentCommentId;
}
