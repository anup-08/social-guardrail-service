package com.example.guardrailService.service;

import com.example.guardrailService.Dtos.CommentRequestDto;
import com.example.guardrailService.Dtos.PostRequestDto;
import com.example.guardrailService.Dtos.PostResponseDto;
import com.example.guardrailService.entities.Comment;
import com.example.guardrailService.entities.Post;
import com.example.guardrailService.repository.CommentRepository;
import com.example.guardrailService.repository.PostRepository;
import lombok.AllArgsConstructor;

@org.springframework.stereotype.Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostResponseDto createPost(PostRequestDto requestDto) {
        Post post = new Post();
        post.setAuthorId(requestDto.getAuthorId());
        post.setAuthorType(requestDto.getAuthorType());
        post.setContent(requestDto.getContent());

        return mapToResponseDto(postRepository.save(post));
    }

    public String addCommentToPost(Long postId , CommentRequestDto requestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        int depth;

        if (requestDto.getParentCommentId() != null) {
            Comment parent = commentRepository.findById(requestDto.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));

            if(!parent.getPostId().equals(postId)){
                throw new RuntimeException("Invalid parent comment for this post");
            }
            depth = parent.getDepthLevel() + 1;
        } else {
            depth = 1;
        }
        if (depth > 20) {
            throw new RuntimeException("Max depth level exceeded");
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setParentCommentId(requestDto.getParentCommentId());
        comment.setAuthorId(requestDto.getAuthorId());
        comment.setAuthorType(requestDto.getAuthorType());
        comment.setContent(requestDto.getContent());
        comment.setDepthLevel(depth);

        commentRepository.save(comment);
        return "Comment added successfully";
    }

    public String likePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return "Liked";
    }

    private PostResponseDto mapToResponseDto(Post post) {
        PostResponseDto responseDto = new PostResponseDto();
        responseDto.setId(post.getId());
        responseDto.setAuthorId(post.getAuthorId());
        responseDto.setAuthorType(String.valueOf(post.getAuthorType()));
        responseDto.setContent(post.getContent());
        responseDto.setCreatedAt(post.getCreatedAt());
        return responseDto;
    }
}
