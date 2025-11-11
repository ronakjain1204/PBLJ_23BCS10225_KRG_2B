package com.feedbacksystem.feedback_backend.repository;

import com.feedbacksystem.feedback_backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User documents.
 * Provides CRUD operations and custom queries.
 */
@Repository // Tells Spring this is a repository bean
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Finds a user by their email address.
     * This was part of the original plan.
     *
     * @param email The email to search for.
     * @return An Optional containing the User if found, or empty if not.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given email already exists.
     *
     * @param email The email to check.
     * @return true if a user with this email exists, false otherwise.
     */
    Boolean existsByEmail(String email);

}