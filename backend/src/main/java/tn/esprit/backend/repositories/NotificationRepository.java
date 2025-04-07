package tn.esprit.backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.backend.entities.Notification;

public interface NotificationRepository extends MongoRepository<Notification, String> {
}