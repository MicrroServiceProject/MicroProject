package tn.esprit.backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.backend.entities.BlogComment;

import java.util.List;

public interface CommentRepository extends MongoRepository<BlogComment, String> {
    List<BlogComment> findByPostId(String postId);
}