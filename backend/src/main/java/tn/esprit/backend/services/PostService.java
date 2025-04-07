package tn.esprit.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tn.esprit.backend.dto.CommentDto;
import tn.esprit.backend.dto.NotificationDto;
import tn.esprit.backend.dto.PostDto;
import tn.esprit.backend.entities.Category;
import tn.esprit.backend.entities.BlogComment;
import tn.esprit.backend.entities.Post;
import tn.esprit.backend.entities.User;
import tn.esprit.backend.exceptions.ResourceNotFoundException;
import tn.esprit.backend.repositories.PostRepository;
import tn.esprit.backend.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public PostService(PostRepository postRepository, UserRepository userRepository, NotificationService notificationService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public PostDto createPost(PostDto postDto) {
        logger.info("Création d'un nouveau post : {}", postDto);
        try {
            Post post = new Post();
            post.setTitle(postDto.getTitle());
            post.setContent(postDto.getContent());
            post.setImageUrl(postDto.getImageUrl());
            // Convertir la chaîne category en enum
            post.setCategory(Category.valueOf(postDto.getCategory().toUpperCase().replace(" ", "_")));
            post.setCreatedAt(LocalDateTime.now());
            post.setLikes(postDto.getLikes() != null ? postDto.getLikes() : 0);
            post.setViews(postDto.getViews() != null ? postDto.getViews() : 0);
            post.setIsLiked(postDto.getIsLiked() != null ? postDto.getIsLiked() : false);
            post.setIsFavorite(postDto.getIsFavorite() != null ? postDto.getIsFavorite() : false);

            // Try to find the user by authorId first
            User author = null;
            if (postDto.getAuthorId() != null) {
                author = userRepository.findById(postDto.getAuthorId()).orElse(null);
            }
            // If authorId is invalid or not found, fall back to authorUsername
            if (author == null && postDto.getAuthorUsername() != null) {
                author = userRepository.findByUsername(postDto.getAuthorUsername());
            }
            // If author is still not found, throw an exception
            if (author == null) {
                throw new ResourceNotFoundException("Author not found with ID: " + postDto.getAuthorId() + " or username: " + postDto.getAuthorUsername());
            }
            post.setAuthor(author);

            Post savedPost = postRepository.save(post);
            logger.info("Post créé avec succès : {}", savedPost);

            // If the author is an admin, notify all non-admin users
            if (author.isAdmin()) {
                List<User> nonAdminUsers = userRepository.findAll().stream()
                        .filter(user -> !user.isAdmin())
                        .collect(Collectors.toList());
                for (User user : nonAdminUsers) {
                    NotificationDto notificationDto = new NotificationDto();
                    notificationDto.setMessage("Nouvel article créé : " + savedPost.getTitle());
                    notificationDto.setCreatedAt(LocalDateTime.now().toString());
                    notificationService.addNotification(notificationDto, user);
                }
            }

            return mapToDto(savedPost);
        } catch (IllegalArgumentException e) {
            logger.error("Catégorie invalide : {}", postDto.getCategory(), e);
            throw new IllegalArgumentException("Invalid category: " + postDto.getCategory());
        } catch (Exception e) {
            logger.error("Erreur lors de la création du post : {}", postDto, e);
            throw e;
        }
    }

    public List<PostDto> getAllPosts() {
        logger.info("Récupération de tous les posts");
        return postRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PostDto getPostById(String id) {
        logger.info("Récupération du post avec ID : {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));
        return mapToDto(post);
    }

    public List<PostDto> searchPosts(String query) {
        logger.info("Recherche de posts avec la requête : {}", query);
        return postRepository.findByTitleContainingIgnoreCase(query).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PostDto updatePost(String id, PostDto postDto) {
        logger.info("Mise à jour du post avec ID : {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setImageUrl(postDto.getImageUrl());
        post.setCategory(Category.valueOf(postDto.getCategory().toUpperCase().replace(" ", "_")));
        post.setLikes(postDto.getLikes());
        post.setViews(postDto.getViews());
        post.setIsLiked(postDto.getIsLiked());
        post.setIsFavorite(postDto.getIsFavorite());

        Post updatedPost = postRepository.save(post);
        logger.info("Post mis à jour avec succès : {}", updatedPost);
        return mapToDto(updatedPost);
    }

    public void deletePost(String id) {
        logger.info("Suppression du post avec ID : {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));
        postRepository.delete(post);
        logger.info("Post supprimé avec succès : {}", id);
    }

    public void toggleLike(String postId) {
        logger.info("Toggle like pour le post avec ID : {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));
        if (post.getIsLiked()) {
            post.setLikes(post.getLikes() - 1);
            post.setIsLiked(false);
        } else {
            post.setLikes(post.getLikes() + 1);
            post.setIsLiked(true);
        }
        postRepository.save(post);
        logger.info("Like toggled pour le post : {}", post);
    }

    public void toggleFavorite(String postId) {
        logger.info("Toggle favorite pour le post avec ID : {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));
        post.setIsFavorite(!post.getIsFavorite());
        postRepository.save(post);
        logger.info("Favorite toggled pour le post : {}", post);
    }

    public void incrementViews(String postId) {
        logger.info("Incrémentation des vues pour le post avec ID : {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));
        post.setViews(post.getViews() + 1);
        postRepository.save(post);
        logger.info("Vues incrémentées pour le post : {}", post);
    }

    private PostDto mapToDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setImageUrl(post.getImageUrl());
        // Convertir l'enum Category en chaîne pour le frontend
        postDto.setCategory(post.getCategory().toString().replace("_", " ").toLowerCase());
        postDto.setCreatedAt(post.getCreatedAt());
        postDto.setAuthorId(post.getAuthor() != null ? post.getAuthor().getId() : null);
        postDto.setAuthorUsername(post.getAuthor() != null ? post.getAuthor().getUsername() : "Anonyme");
        postDto.setLikes(post.getLikes());
        postDto.setViews(post.getViews());
        postDto.setIsLiked(post.getIsLiked());
        postDto.setIsFavorite(post.getIsFavorite());
        postDto.setComments(post.getComments() != null ? post.getComments().stream().map(this::mapCommentToDto).collect(Collectors.toList()) : List.of());
        return postDto;
    }

    private CommentDto mapCommentToDto(BlogComment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        commentDto.setCreatedAt(comment.getCreatedAt());
        commentDto.setPostId(comment.getPost() != null ? comment.getPost().getId() : null);
        commentDto.setAuthorId(comment.getAuthor() != null ? comment.getAuthor().getId() : null);
        commentDto.setAuthorUsername(comment.getAuthor() != null ? comment.getAuthor().getUsername() : "Anonyme");
        return commentDto;
    }
}