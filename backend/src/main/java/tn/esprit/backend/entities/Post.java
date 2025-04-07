package tn.esprit.backend.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "posts")
@Data
public class Post {
    @Id
    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private Category category;

    private LocalDateTime createdAt;

    @DBRef
    private User author;

    @DBRef
    private List<BlogComment> comments; // Changed from Comment to BlogComment

    private Integer likes = 0;
    private Integer views = 0;
    private Boolean isLiked = false;
    private Boolean isFavorite = false;
}