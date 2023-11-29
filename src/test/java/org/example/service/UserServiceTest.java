package org.example.service;

import org.example.dto.User;
import org.example.paramresolver.UserServiceParamResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple App.
 */
@ExtendWith({UserServiceParamResolver.class})
public class UserServiceTest {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User DIMA = User.of(2, "Dima", "234");
    private UserService userService;

    UserServiceTest(TestInfo testInfo) {
        System.out.println("");

    }

    @BeforeEach
    void prepare(UserService userService) {
        System.out.println("Before each: " + this);
        this.userService = userService;
    }

    @Test
    void UsersIfNoUserAddedTest() {
        List<User> users = userService.getAll();
        assertTrue(users.isEmpty(), () -> "user should be empty");
    }

    @Test
    void userSizeIfUserAddedTest() {
        userService.add(IVAN);
        userService.add(DIMA);

        List<User> users = userService.getAll();
        assertEquals(2, users.size());
    }

    @Test
    @Tag("login")
    void loginSuccessIfUserExists() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

        assertTrue(maybeUser.isPresent());
        maybeUser.ifPresent(user -> assertEquals(IVAN, user));
    }

    @Test
    @Tag("login")
    void loginFailedIfPasswordDoesNotCorrect() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), "dummy");

        assertTrue(maybeUser.isEmpty());
    }

    @Test
    @Tag("login")
    void loginFailedIfUserDoesNotExist() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login("dummy", IVAN.getPassword());

        assertTrue(maybeUser.isEmpty());
    }

    @Test
    @Tag("login")
    void throwExceptionIfUsernameOrPasswordIsNull() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "123")),
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login("ivan", null))
        );
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForLoginTest")
    void loginParameterizedTest(String username, String password, Optional<User> user) {
        userService.add(IVAN, DIMA);

        var mayBeUser = userService.login(username, password);
        assertEquals(mayBeUser, user);
    }

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Dima", "234", Optional.of(DIMA)),
                Arguments.of("Dima", "dummy", Optional.empty()),
                Arguments.of("dummy", "123", Optional.empty())
        );
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each: " + this);
    }
}
