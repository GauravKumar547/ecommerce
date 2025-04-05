package org.example.userauthservice.repos;

import org.example.userauthservice.models.Role;
import org.example.userauthservice.models.Session;
import org.example.userauthservice.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SessionRepositoryTest {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindAllByUserId_WithExistingSession_RunsSuccessfully() {
        // Arrange
        User user = User.builder().name("User").email("test@gmail.com").password("testing").role(Role.USER).build();
        user = userRepository.save(user);

        Session session = Session.builder().user(user).token("testToken").build();
        session = sessionRepository.save(session);

        // Act
        List<Session> sessions = sessionRepository.findAllByUserId(user.getId());

        // Assert
        assertFalse(sessions.isEmpty());
        assertEquals(1, sessions.size());
        assertEquals(session, sessions.getFirst());
        assertEquals(session, sessions.getLast());

    }
    @Test
    public void testFindAllByUserId_WithNonExistingSession_RunsSuccessfully() {
        // Arrange
        User user = User.builder().name("User").email("test@gmail.com").password("testing").role(Role.USER).build();
        user = userRepository.save(user);


        User user1 = User.builder().name("User1").email("t2est@gmail.com").password("testing1").role(Role.ADMIN).build();
        user1 = userRepository.save(user1);

        Session session = Session.builder().user(user).token("testToken").build();
        sessionRepository.save(session);

        // Act
        List<Session> sessions = sessionRepository.findAllByUserId(user1.getId());

        // Assert
        assertTrue(sessions.isEmpty());
    }

    @Test
    public void testFindByTokenAndUserId_WithValidUserIdAndToken_RunsSuccessfully(){
        // Arrange
        String token = "testToken";
        User user = User.builder().name("user").email("test@gmail.com").password("testing").role(Role.USER).build();
        user = userRepository.save(user);

        Session session = Session.builder().user(user).token(token).build();
        session = sessionRepository.save(session);
        // Act
        Optional<Session> foundSession = sessionRepository.findByTokenAndUserId(token, user.getId());
        // Assert
        assertTrue(foundSession.isPresent());
        assertEquals(session, foundSession.get());
        assertEquals(user, foundSession.get().getUser());
    }

    @Test
    public void testFindByTokenAndUserId_WithNonExistingSessionForUserButValidToken_RunsSuccessfully(){
        // Arrange
        String token = "testToken";
        User user = User.builder().name("user").email("test@gmail.com").password("testing").role(Role.USER).build();
        user = userRepository.save(user);

        User user1 = User.builder().name("user1").email("t1est@gmail.com").password("testing").role(Role.USER).build();
        user1 = userRepository.save(user1);

        Session session = Session.builder().user(user).token(token).build();
        session = sessionRepository.save(session);

        // Act
            Optional<Session> foundSession = sessionRepository.findByTokenAndUserId(token, user1.getId());
        // Assert
        assertFalse(foundSession.isPresent());
        assertEquals(Optional.empty(), foundSession);
    }
}
