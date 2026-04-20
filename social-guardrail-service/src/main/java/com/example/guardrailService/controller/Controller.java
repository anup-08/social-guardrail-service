package com.example.guardrailService.controller;

import com.example.guardrailService.Dtos.CommentRequestDto;
import com.example.guardrailService.Dtos.PostRequestDto;
import com.example.guardrailService.Dtos.PostResponseDto;
import com.example.guardrailService.service.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class Controller {
    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostRequestDto responseDto) {
        PostResponseDto postResponseDto = postService.createPost(responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postResponseDto);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<String> addCommentToPost(@PathVariable Long postId, @Valid @RequestBody CommentRequestDto requestDto) {
         String result = postService.addCommentToPost(postId, requestDto);
         return ResponseEntity.ok(result);
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable Long postId) {
        String result = postService.likePost(postId);
        return ResponseEntity.ok(result);
    }

}
