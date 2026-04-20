package com.example.guardrailService.Dtos;

import com.example.guardrailService.entities.AuthorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PostRequestDto {
    @NotNull(message = "Author ID is required")
    private Long authorId;

    @NotBlank(message = "Author type is required")
    private AuthorType authorType;

    @NotBlank(message = "Content cannot be empty")
    private String content;

}
