package com.example.guardrailService.service;

import com.example.guardrailService.Dtos.CommentRequestDto;
import com.example.guardrailService.Dtos.PostRequestDto;
import com.example.guardrailService.Dtos.PostResponseDto;
import com.example.guardrailService.entities.AuthorType;
import com.example.guardrailService.entities.Comment;
import com.example.guardrailService.entities.Post;
import com.example.guardrailService.exceptions.NotFound;
import com.example.guardrailService.repository.CommentRepository;
import com.example.guardrailService.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

@org.springframework.stereotype.Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final StringRedisTemplate redisTemplate;

    public PostResponseDto createPost(PostRequestDto requestDto) {
        Post post = new Post();
        post.setAuthorId(requestDto.getAuthorId());
        post.setAuthorType(requestDto.getAuthorType());
        post.setContent(requestDto.getContent());

        return mapToResponseDto(postRepository.save(post));
    }

    public String addCommentToPost(Long postId , CommentRequestDto requestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFound("Post not found with id: " + postId));

        int depth;

        if (requestDto.getParentCommentId() != null) {
            Comment parent = commentRepository.findById(requestDto.getParentCommentId())
                    .orElseThrow(() -> new NotFound("Parent comment not found"));

            if(!parent.getPostId().equals(postId)){
                throw new IllegalArgumentException("Invalid parent comment for this post");
            }
            depth = parent.getDepthLevel() + 1;
        } else {
            depth = 1;
        }
        if (depth > 20) {
            throw new IllegalArgumentException("Max depth level exceeded");
        }

        if(requestDto.getAuthorType() == AuthorType.BOT){
            String key = "post:"+ postId +":bot_count";
            Long botCount = redisTemplate.opsForValue().increment(key);
            if(botCount > 100) {
                redisTemplate.opsForValue().decrement(key);
                throw new RuntimeException("Bot comment limit exceeded for this post");
            }
        }

        if (requestDto.getAuthorType() == AuthorType.BOT) {
            String cooldownKey = "cooldown:bot_" + requestDto.getAuthorId() + ":human_" + post.getAuthorId();

            if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
                throw new RuntimeException("Cooldown active");
            }

            redisTemplate.opsForValue()
                    .set(cooldownKey, "1", Duration.ofMinutes(10));
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setParentCommentId(requestDto.getParentCommentId());
        comment.setAuthorId(requestDto.getAuthorId());
        comment.setAuthorType(requestDto.getAuthorType());
        comment.setContent(requestDto.getContent());
        comment.setDepthLevel(depth);

        commentRepository.save(comment);

        updateVirality(postId, requestDto.getAuthorType());

        if (requestDto.getAuthorType() == AuthorType.BOT) {
            handleNotification(post.getAuthorId(), "Bot replied to your post");
        }

        return "Comment added successfully";
    }

    public String likePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        redisTemplate.opsForValue()
                .increment("post:" + postId + ":virality_score", 20);


        return "Liked";
    }

    private void updateVirality(Long postId , AuthorType type){
        String key = "post:"+ postId +":virality_score";
        int score = (type == AuthorType.BOT) ? 1 : 50;
        redisTemplate.opsForValue().increment(key,score);
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

    private void handleNotification(Long userId, String message) {

        String cooldownKey = "notif:user:" + userId;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            redisTemplate.opsForList()
                    .rightPush("user:" + userId + ":pending_notifs", message);

            redisTemplate.opsForSet().add("users_with_notifications", userId.toString());

        } else {
            System.out.println("Notification Sent to User " + userId);

            redisTemplate.opsForValue()
                    .set(cooldownKey, "1", Duration.ofMinutes(15));
        }
    }
}
