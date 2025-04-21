package com.esprit.microservice.user.services;

import com.esprit.microservice.user.entities.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IUserService {
    // CRUD operations
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);

    // Authentication operations
    User register(User user);
    User login(String username, String password);

    // Ajoutez cette méthode dans UserServiceImp
    // Ajoutez cette méthode dans UserServiceImp
    User loginWithGoogle(String googleToken);

    // Méthode pour obtenir les statistiques des utilisateurs par rôle
    Map<String, Long> getUserStatisticsByRole();

    // Méthode pour obtenir le nombre total d'utilisateurs
    long getTotalUsersCount();
}
