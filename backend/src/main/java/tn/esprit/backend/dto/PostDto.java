package tn.esprit.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDto {
    private String id;
    private String title;
    private String content;
    private String imageUrl;
    private String category;
    private LocalDateTime createdAt;
    private String authorId;
    private String authorUsername;
    private Integer likes;
    private Integer views;
    private Boolean isLiked;
    private Boolean isFavorite;
    private List<CommentDto> comments;
}