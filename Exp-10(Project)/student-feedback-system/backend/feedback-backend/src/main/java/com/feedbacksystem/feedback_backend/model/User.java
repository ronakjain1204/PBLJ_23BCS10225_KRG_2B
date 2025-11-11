// package com.feedbacksystem.feedback_backend.model;
package com.feedbacksystem.feedback_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represents the User document in the MongoDB 'users' collection.
 */
@Data // From Lombok: Generates getters, setters, toString, equals, and hashCode
@Builder // From Lombok: Provides a builder pattern for creating objects
@NoArgsConstructor // From Lombok: Generates a no-argument constructor
@AllArgsConstructor // From Lombok: Generates a constructor with all arguments
@Document(collection = "users") // Tells Spring Data this is a MongoDB document
public class User {

    @Id // Marks this field as the primary key (_id)
    private String id;

    private String name;

    @Indexed(unique = true) // Creates a unique index on email, as planned
    private String email;

    private String password;

    private Role role; // Uses the Role enum we just created

    private LocalDateTime createdAt;
}