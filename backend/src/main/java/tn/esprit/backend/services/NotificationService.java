package tn.esprit.backend.services;

import org.springframework.stereotype.Service;
import tn.esprit.backend.dto.NotificationDto;
import tn.esprit.backend.entities.Notification;
import tn.esprit.backend.entities.User;
import tn.esprit.backend.repositories.NotificationRepository;
import tn.esprit.backend.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<NotificationDto> getAllNotifications() {
        return notificationRepository.findAll().stream().map(notification -> {
            NotificationDto dto = new NotificationDto();
            dto.setId(notification.getId());
            dto.setMessage(notification.getMessage());
            dto.setCreatedAt(notification.getCreatedAt());
            dto.setRead(notification.isRead());
            return dto;
        }).collect(Collectors.toList());
    }

    // Overloaded method without User parameter
    public NotificationDto addNotification(NotificationDto notificationDto) {
        return addNotification(notificationDto, null); // Call the main method with null user
    }

    // Main method with User parameter
    public NotificationDto addNotification(NotificationDto notificationDto, User user) {
        Notification notification = new Notification();
        notification.setMessage(notificationDto.getMessage());
        notification.setCreatedAt(notificationDto.getCreatedAt());
        notification.setRead(false);
        notification.setUser(user); // Can be null
        notification = notificationRepository.save(notification);

        NotificationDto result = new NotificationDto();
        result.setId(notification.getId());
        result.setMessage(notification.getMessage());
        result.setCreatedAt(notification.getCreatedAt());
        result.setRead(notification.isRead());
        return result;
    }

    public void markAsRead(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + id));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}