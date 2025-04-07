package tn.esprit.backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.backend.entities.Post;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByTitleContainingIgnoreCase(String title);
}