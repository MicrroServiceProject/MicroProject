package tn.esprit.backend.services;

import org.springframework.stereotype.Service;
import tn.esprit.backend.dto.CommentDto;
import tn.esprit.backend.entities.BlogComment;
import tn.esprit.backend.entities.Post;
import tn.esprit.backend.entities.User;
import tn.esprit.backend.repositories.CommentRepository;
import tn.esprit.backend.repositories.PostRepository;
import tn.esprit.backend.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository, EmailService emailService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public CommentDto addComment(CommentDto commentDto) {
        System.out.println("Adding comment with DTO: " + commentDto); // Debug log
        System.out.println("Author ID: " + commentDto.getAuthorId()); // Debug log

        BlogComment comment = new BlogComment();
        comment.setContent(commentDto.getContent());
        comment.setCreatedAt(commentDto.getCreatedAt());

        // Set the post
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + commentDto.getPostId()));
        comment.setPost(post);

        // Set the author
        String authorId = commentDto.getAuthorId();
        System.out.println("Looking for user with ID: " + authorId); // Debug log
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + authorId));
        comment.setAuthor(author);

        comment = commentRepository.save(comment);
        System.out.println("Saved comment: " + comment); // Debug log

        // Send email to admin
        List<User> admins = userRepository.findAll().stream()
                .filter(User::isAdmin)
                .collect(Collectors.toList());
        for (User admin : admins) {
            emailService.sendCommentNotification(
                    admin.getEmail(),
                    admin.getUsername(),
                    post.getTitle(),
                    commentDto.getContent(),
                    post.getLikes(),
                    post.getViews()
            );
        }

        CommentDto result = new CommentDto();
        result.setId(comment.getId());
        result.setContent(comment.getContent());
        result.setPostId(comment.getPost().getId());
        result.setAuthorId(comment.getAuthor().getId());
        result.setAuthorUsername(commentDto.getAuthorUsername());
        result.setCreatedAt(comment.getCreatedAt());
        return result;
    }

    public List<CommentDto> getCommentsByPostId(String postId) {
        return commentRepository.findByPostId(postId).stream().map(comment -> {
            CommentDto dto = new CommentDto();
            dto.setId(comment.getId());
            dto.setContent(comment.getContent());
            dto.setPostId(comment.getPost().getId());
            dto.setAuthorId(comment.getAuthor().getId());
            dto.setAuthorUsername(comment.getAuthor().getUsername());
            dto.setCreatedAt(comment.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }
}