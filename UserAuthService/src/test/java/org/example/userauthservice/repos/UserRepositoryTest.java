package org.example.userauthservice.repos;

import org.example.userauthservice.models.Role;
import org.example.userauthservice.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testExistsByEmail_WithValidEmail_RunsSuccessfully(){
        // Arrange
        User user = User.builder().email("test@example.com").name("user").password("testing").role(Role.USER).build();
        user = userRepository.save(user);
        // Act
        Boolean exists = userRepository.existsByEmail(user.getEmail());
        // Assert
        assertTrue(exists);
    }

    @Test
    public void testExistsByEmail_WithInValidEmail_RunsSuccessfully(){
        // Arrange
        User user = User.builder().email("test@example.com").name("user").password("testing").role(Role.USER).build();
        user = userRepository.save(user);
        // Act
        Boolean exists = userRepository.existsByEmail("testing@gmail.com");
        // Assert
        assertFalse(exists);
    }

    @Test
    public void testFindByEmail_WithInvalidToken_RunsSuccessfully(){
        // Arrange
        User user = User.builder().email("test@example.com").name("user").password("testing").role(Role.USER).build();
        user = userRepository.save(user);
        // Act
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
        // Assert
        assertTrue(userOptional.isPresent());
        assertEquals(user, userOptional.get());
    }

    @Test
    public void testFindByEmail_WithInvalidEmail_RunsSuccessfully(){
        // Act
        Optional<User> userOptional = userRepository.findByEmail("test@gmail.com");
        // Assert
        assertTrue(userOptional.isEmpty());
    }

}
