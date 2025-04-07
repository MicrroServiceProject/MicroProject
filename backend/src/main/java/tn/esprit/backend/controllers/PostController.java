package tn.esprit.backend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backend.dto.PostDto;
import tn.esprit.backend.services.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        logger.info("Requête pour récupérer tous les posts");
        List<PostDto> posts = postService.getAllPosts();
        logger.info("Posts récupérés avec succès, nombre : {}", posts.size());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable String id) {
        logger.info("Requête pour récupérer le post avec ID : {}", id);
        PostDto post = postService.getPostById(id);
        postService.incrementViews(id);
        logger.info("Post récupéré avec succès : {}", id);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        logger.info("Requête pour créer un nouveau post : {}", postDto);
        PostDto createdPost = postService.createPost(postDto);
        logger.info("Post créé avec succès : {}", createdPost.getId());
        return ResponseEntity.ok(createdPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable String id, @RequestBody PostDto postDto) {
        logger.info("Requête pour mettre à jour le post avec ID : {}", id);
        PostDto updatedPost = postService.updatePost(id, postDto);
        logger.info("Post mis à jour avec succès : {}", id);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String id) {
        logger.info("Requête pour supprimer le post avec ID : {}", id);
        postService.deletePost(id);
        logger.info("Post supprimé avec succès : {}", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> toggleLike(@PathVariable String id) {
        logger.info("Requête pour toggle like sur le post avec ID : {}", id);
        postService.toggleLike(id);
        logger.info("Like toggled avec succès pour le post : {}", id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> toggleFavorite(@PathVariable String id) {
        logger.info("Requête pour toggle favorite sur le post avec ID : {}", id);
        postService.toggleFavorite(id);
        logger.info("Favorite toggled avec succès pour le post : {}", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDto>> searchPosts(@RequestParam String query) {
        logger.info("Requête pour rechercher des posts avec la requête : {}", query);
        List<PostDto> searchResults = postService.searchPosts(query);
        logger.info("Recherche terminée, nombre de résultats : {}", searchResults.size());
        return ResponseEntity.ok(searchResults);
    }
}