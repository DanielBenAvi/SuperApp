package superapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;

import jakarta.annotation.PostConstruct;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import superapp.data.UserRole;
import superapp.logic.boundaries.NewUserBoundary;
import superapp.logic.boundaries.UserBoundary;
import superapp.logic.boundaries.UserID;

import java.util.HashMap;
import java.util.Map;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTestSet {
    private String springApplicationName;
    private RestTemplate restTemplate;
    private String baseUrl;
    private int port;

    @Value("${spring.application.name:defaultAppName}")
    public void setApplicationName(String springApplicationName) {
        this.springApplicationName = springApplicationName;
    }


    @LocalServerPort
    public void setPort(int port) {
        this.port = port;
    }

    @PostConstruct
    public void setup() {
        this.restTemplate = new RestTemplate();
        this.baseUrl = "http://localhost:" + this.port;
    }

    @AfterEach
    public void tearDown() {
        this.restTemplate.delete(this.baseUrl + "/superapp/admin/users");
    }

    /**
     * POST:
     * Helper method to create a user
     * the path is "/superapp/users"
     *
     * @param email    - the email of the user
     * @param role     - the role of the user
     * @param username - the username of the user
     * @param avatar   - the avatar of the user
     * @return the user that was created
     */
    public UserBoundary help_PostUserBoundary(String email, String role, String username, String avatar) {
        NewUserBoundary user = new NewUserBoundary();
        user.setEmail(email).setRole(role).setUsername(username).setAvatar(avatar);
        return this.restTemplate
                .postForObject(
                        this.baseUrl + "/superapp/users"
                        , user
                        , UserBoundary.class);
    }


    /**
     * GET:
     * Helper method to get a user
     * the path is "/superapp/users/login/{superapp}/{email}"
     *
     * @param email - the email of the user
     * @return the user that was created
     */
    public UserBoundary help_GetUserBoundary(String email) {
        return this.restTemplate
                .getForObject(
                        this.baseUrl + "/superapp/users/login/{superapp}/{email}"
                        , UserBoundary.class
                        , springApplicationName
                        , email);
    }

    /**
     * PUT
     * Helper method to update a user
     * the path is "/superapp/users/{superapp}/{email}"
     *
     * @param userBoundary - the user to update
     * @param email        - the email of the user we want to update
     */
    public void help_PutUserBoundary(UserBoundary userBoundary, String email) {
        this.restTemplate.put(
                this.baseUrl + "/superapp/users/{superapp}/{email}"
                , userBoundary, springApplicationName
                , email);
    }

    /**
     * DELETE
     * Helper method to delete a user
     * the path is "/superapp/admin/users"
     */
    public void help_DeleteUsersBoundary() {
        this.restTemplate.delete(this.baseUrl + "/superapp/admin/users");
    }

    /**
     * GET_ALL
     * Helper method to get all users
     * the path is "/superapp/admin/users
     *
     * @return all users - UserBoundary[]
     */
    public UserBoundary[] help_GetAllUsersBoundary() {
        return this.restTemplate.getForObject(this.baseUrl + "/superapp/admin/users", UserBoundary[].class);
    }


    @Test
    @DisplayName("Successful login user")
    public void successfulLoginUser() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        help_PostUserBoundary(email, role, username, avatar);

        // WHEN
        // A GET request is made to the path "/superapp/users/login/{superapp}/{email}"
        UserBoundary userBoundary = help_GetUserBoundary(email);

        // THEN
        // the server returns status code 2xx
        // userBoundary is equal to expectedUser
        UserBoundary expectedUser = new UserBoundary()
                .setUserId(new UserID()
                        .setEmail(email)
                        .setSuperapp(springApplicationName))
                .setRole(role).setUsername(username)
                .setAvatar(avatar);
        assertThat(userBoundary)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("Unsuccessful login user - No user is in the database")
    public void unsuccessfulLoginUser() {
        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is not registered

        // when
        // A GET request is made to the path "/superapp/users/login/{superapp}/{email}"

        // then
        // the server returns status code 4xx

        assertThatThrownBy(() ->
                this.restTemplate
                        .getForObject(
                                this.baseUrl + "/superapp/users/login/{superapp}/{email}"
                                , UserBoundary.class, springApplicationName
                                , "demo@gmail.com"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Successfully create a user - ADMIN while the database is empty")
    public void successfulCreateADMIN() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the database is empty

        // when
        // A POST request is made to the path "/superapp/users"
        help_PostUserBoundary(email, role, username, avatar);

        // then
        // the server returns status code 2xx
        // userBoundary is equal to the user that was created
        UserBoundary expectedUser = new UserBoundary()
                .setUserId(new UserID()
                        .setEmail(email)
                        .setSuperapp(springApplicationName))
                .setRole(role)
                .setUsername(username)
                .setAvatar(avatar);

        assertThat(help_GetUserBoundary(email)).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("Successfully create a user - PLAYER while the database is empty")
    public void successfulCreateMINIAPP_USER() {
        String email = "demo@gmail.com";
        String role = UserRole.MINIAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the database is empty

        // when
        // A POST request is made to the path "/superapp/users"
        help_PostUserBoundary(email, role, username, avatar);

        // then
        // the server returns status code 2xx
        // userBoundary is equal to the user that was created
        UserBoundary expectedUser = new UserBoundary()
                .setUserId(new UserID()
                        .setEmail(email)
                        .setSuperapp(springApplicationName))
                .setRole(role).setUsername(username)
                .setAvatar(avatar);

        assertThat(help_GetUserBoundary(email)).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("Successfully create a user - SUPERAPP_USER while the database is empty")
    public void successfulCreateSUPERAPP_USER() {
        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"
        help_PostUserBoundary(email, role, username, avatar);

        // then
        // the server returns status code 2xx
        // userBoundary is equal to the user that was created
        UserBoundary expectedUser = new UserBoundary().setUserId(new UserID().setEmail(email).setSuperapp(springApplicationName)).setRole(role).setUsername(username).setAvatar(avatar);

        assertThat(help_GetUserBoundary(email)).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }


    @Test
    @DisplayName("Unsuccessful create a user - email is null")
    public void unsuccessfulCreateUser_email() {
        String email = null;
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"

        // then
        // the server returns status code 400
        assertThatThrownBy(() ->
                help_PostUserBoundary(email, role, username, avatar))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Unsuccessful create a user - email is not valid")
    public void unsuccessfulCreateUser_email_notValid() {
        String email = "notValidEmail";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"

        // then
        // the server returns status code 400
        assertThatThrownBy(() ->
                help_PostUserBoundary(email, role, username, avatar))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Unsuccessful create a user - email is empty")
    public void unsuccessfulCreateUser_email_empty() {
        String email = "";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"

        // then
        // the server returns status code 400
        assertThatThrownBy(() ->
                help_PostUserBoundary(email, role, username, avatar))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Unsuccessful create a user - role is null")
    public void unsuccessfulCreateUser_role() {
        String email = "demo@gmail.com";
        String role = null;
        String username = "demo_user";
        String avatar = "demo_avatar";
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"

        // then
        // the server returns status code 400
        assertThatThrownBy(() ->
                help_PostUserBoundary(email, role, username, avatar))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Unsuccessful create a user - role is not valid")
    public void unsuccessfulCreateUser_role_notValid() {
        String email = "demo@gmail.com";
        String role = "notValidRole";
        String username = "demo_user";
        String avatar = "demo_avatar";
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"

        // then
        // the server returns status code 400
        assertThatThrownBy(() ->
                help_PostUserBoundary(email, role, username, avatar))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Unsuccessful create a user - role is empty")
    public void unsuccessfulCreateUser_role_empty() {
        String email = "demo@gmail.com";
        String role = "";
        String username = "demo_user";
        String avatar = "demo_avatar";
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"

        // then
        // the server returns status code 400
        assertThatThrownBy(() ->
                help_PostUserBoundary(email, role, username, avatar))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Unsuccessful create a user - username is null")
    public void unsuccessfulCreateUser_username_null() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = null;
        String avatar = "demo_avatar";
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"

        // then
        // the server returns status code 400
        assertThatThrownBy(() ->
                help_PostUserBoundary(email, role, username, avatar))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Unsuccessful create a user - username is empty")
    public void unsuccessfulCreateUser_username_empty() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "";
        String avatar = "demo_avatar";
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"

        // then
        // the server returns status code 400
        assertThatThrownBy(() ->
                help_PostUserBoundary(email, role, username, avatar))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Unsuccessful create a user - avatar is null")
    public void unsuccessfulCreateUser_avatar() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = null;
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"

        // then
        // the server returns status code 400
        assertThatThrownBy(() ->
                help_PostUserBoundary(email, role, username, avatar))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Unsuccessful create a user - avatar is empty")
    public void unsuccessfulCreateUser_avatar_isEmpty() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "";
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"

        // then
        // the server returns status code 400
        assertThatThrownBy(() ->
                help_PostUserBoundary(email, role, username, avatar))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }


    @Test
    @DisplayName("Unsuccessful create a user - user is already exists")
    public void unsuccessfulCreateUser_alreadyExists() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";

        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. a user with the same email already exists in the database
        help_PostUserBoundary(email, role, username, avatar);

        // when
        // A POST request is made to the path "/superapp/users"

        // then
        // the server returns status code 4xx
        assertThatThrownBy(() ->
                help_PostUserBoundary(email, role, username, avatar))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Successfully update a user - role")
    public void successfulUpdateUser_role() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";

        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        help_PostUserBoundary(email, role, username, avatar);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        // with the new role from SUPERAPP_USER to ADMIN
        String updatedRole = UserRole.SUPERAPP_USER.toString();
        help_PutUserBoundary(new UserBoundary().setRole(updatedRole), email);


        // then
        // the server returns status code 2xx
        // the user's role is updated
        UserBoundary expectedUser = new UserBoundary()
                .setUserId(new UserID()
                        .setSuperapp(springApplicationName)
                        .setEmail(email))
                .setRole(updatedRole)
                .setUsername(username)
                .setAvatar(avatar);

        assertThat(help_GetUserBoundary(email)).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("unsuccessfully update a user - role is null")
    public void unsuccessfulUpdateUser_role() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";

        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        UserBoundary expectedUser = help_PostUserBoundary(email, role, username, avatar);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        // role changed to null
        String updatedRole = null;
        help_PutUserBoundary(new UserBoundary().setRole(updatedRole), email);


        // then
        // the user's role is not updated
        assertThat(help_GetUserBoundary(email)).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("unsuccessfully update a user - role is empty")
    public void successfulUpdateUser_role_empty() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";

        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        UserBoundary expectedUser = help_PostUserBoundary(email, role, username, avatar);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        // with the new role from ADMIN to empty
        String updatedRole = "";


        // then
        // the server returns status code 4xx
        // the user's role is updated
        assertThatThrownBy(() ->
                help_PutUserBoundary(new UserBoundary().setRole(updatedRole), email))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Successfully update a user - username")
    public void successfulUpdateUser_username() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";

        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        help_PostUserBoundary(email, role, username, avatar);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        // with the new username from demo_user to demo_user_updated
        String updatedUsername = "new_demo_user";
        help_PutUserBoundary(new UserBoundary().setUsername(updatedUsername), email);


        // then
        // the server returns status code 2xx
        // the user's role is updated
        UserBoundary expectedUser = new UserBoundary()
                .setUserId(new UserID()
                        .setSuperapp(springApplicationName)
                        .setEmail(email))
                .setRole(role)
                .setUsername(updatedUsername)
                .setAvatar(avatar);

        assertThat(help_GetUserBoundary(email)).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("unsuccessfully update a user - username is null")
    public void unsuccessfulUpdateUser_username_null() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";

        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        UserBoundary expectedUser = help_PostUserBoundary(email, role, username, avatar);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        //username changed to null
        String updatedUsername = null;
        help_PutUserBoundary(new UserBoundary().setUsername(updatedUsername), email);


        // then
        // the user's role is not updated
        assertThat(help_GetUserBoundary(email)).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("unsuccessfully update a user - username is empty")
    public void successfulUpdateUser_username_empty() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";

        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        UserBoundary expectedUser = help_PostUserBoundary(email, role, username, avatar);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        // with the new username from demo_user to demo_user_updated
        String updatedUsername = "";


        // then
        // the server returns status code 4xx
        // the user's role is updated
        assertThatThrownBy(() ->
                help_PutUserBoundary(new UserBoundary().setUsername(updatedUsername), email))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }


    @Test
    @DisplayName("Successfully update a user - avatar")
    public void successfulUpdateUser_avatar() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";

        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        help_PostUserBoundary(email, role, username, avatar);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        // with the new avatar from demo_avatar to new_demo_avatar
        String updatedAvatar = "new_demo_avatar";
        help_PutUserBoundary(new UserBoundary().setAvatar(updatedAvatar), email);


        // then
        // the server returns status code 2xx
        // the user's avatar is updated
        UserBoundary expectedUser = new UserBoundary()
                .setUserId(new UserID()
                        .setSuperapp(springApplicationName)
                        .setEmail(email))
                .setRole(role)
                .setUsername(username)
                .setAvatar(updatedAvatar);

        assertThat(help_GetUserBoundary(email)).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("unsuccessfully update a user - avatar is null")
    public void unsuccessfulUpdateUser_avatar_null() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";

        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        UserBoundary expectedUser = help_PostUserBoundary(email, role, username, avatar);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        // avatar changed to null
        String updatedAvatar = null;
        help_PutUserBoundary(new UserBoundary().setAvatar(updatedAvatar), email);


        // then
        // the user's avatar is not updated
        assertThat(help_GetUserBoundary(email)).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }


    @Test
    @DisplayName("unsuccessfully update a user - avatar is empty")
    public void unsuccessfulUpdateUser_avatar_empty() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";

        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        UserBoundary expectedUser = help_PostUserBoundary(email, role, username, avatar);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        // with the new avatar from demo_avatar to empty
        String updatedAvatar = "";


        // then
        // the server returns status code 4xx
        assertThatThrownBy(() ->
                help_PutUserBoundary(new UserBoundary().setAvatar(updatedAvatar), email))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }


    @Test
    @DisplayName("unsuccessfully update a user - email")
    public void unsuccessfulUpdateUser_email() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";

        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        UserBoundary expectedUser = help_PostUserBoundary(email, role, username, avatar);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        // avatar email to null
        String updatedEmail = null;
        help_PutUserBoundary(new UserBoundary().setUserId(new UserID().setEmail(updatedEmail)), email);


        // then
        // the user's email is not updated
        assertThat(help_GetUserBoundary(email)).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }


    @Test
    @DisplayName("unsuccessfully update a user - superapp")
    public void unsuccessfulUpdateUser_superapp() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";

        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        UserBoundary expectedUser = help_PostUserBoundary(email, role, username, avatar);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        // avatar email to null
        String updatedSuperAppName = null;
        help_PutUserBoundary(new UserBoundary().setUserId(new UserID().setSuperapp(updatedSuperAppName)), email);


        // then
        // the user's email is not updated
        assertThat(help_GetUserBoundary(email)).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("successfully delete all users - 2 users in the database")
    public void successfulDeleteAllUsers() {
        String email1 = "demo1@gmail.com";
        String email2 = "demo2@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        help_PostUserBoundary(email1, role, username, avatar);
        help_PostUserBoundary(email2, role, username, avatar);

        // when
        // A DELETE request is made to the path "/superapp/admin/users"
        help_DeleteUsersBoundary();

        // then
        // the server returns status code 2xx
        // get all users
        UserBoundary[] users = help_GetAllUsersBoundary();
        assertThat(users).isEmpty();

    }

    @Test
    @DisplayName("successfully delete all users - 0 users in the database")
    public void successfulDeleteAllUsers_empty() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. there are no users in the database

        // when
        // A DELETE request is made to the path "/superapp/admin/users"
        help_DeleteUsersBoundary();

        // then
        // the server returns status code 2xx
        // get all users
        UserBoundary[] users = help_GetAllUsersBoundary();
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("successfully get all users - 2 users in the database")
    public void successfulGetAllUsers() {
        String email1 = "demo1@gmail.com";
        String email2 = "demo2@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        help_PostUserBoundary(email1, role, username, avatar);
        help_PostUserBoundary(email2, role, username, avatar);

        // when
        // A GET request is made to the path "/superapp/admin/users"
        UserBoundary[] users = help_GetAllUsersBoundary();

        // then
        // the server returns status code 2xx
        // users are returned -> 2 users
        assertThat(users).hasSize(2);
    }

    @Test
    @DisplayName("successfully get all users - 0 users in the database")
    public void successfulGetAllUsers_empty() {
        String url = this.baseUrl + "/superapp/admin/users";
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. there are no users in the database

        // when
        // A GET request is made to the path "/superapp/admin/users"
        UserBoundary[] users = help_GetAllUsersBoundary();

        // then
        // the server returns status code 2xx
        // users are returned -> 2 users
        assertThat(users).isEmpty();
    }


    @Test
    @DisplayName("successfully crate a user - json is valid")
    public void successfulCreateUser_jsonValid() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";

        // given
        // 1. the server is up and running
        // 2. the database is up and running
        Map<String, Object> invalidUser = new HashMap<>();
        invalidUser.put("email", email);
        invalidUser.put("role", role);
        invalidUser.put("username", username);
        invalidUser.put("avatar", avatar);

        // when
        // A POST request is made to the path "/superapp/users"
        // with a valid user JSON
        this.restTemplate.postForObject(this.baseUrl + "/superapp/users", invalidUser, UserBoundary.class);

        // then
        // the server returns status code 2xx
        // the user is stored in the database
        UserBoundary expectedUser = new UserBoundary()
                .setUserId(new UserID()
                        .setSuperapp(springApplicationName)
                        .setEmail(email))
                .setRole(role)
                .setUsername(username)
                .setAvatar(avatar);

        assertThat(help_GetUserBoundary(email)).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);

    }

    @Test
    @DisplayName("unsuccessfully crate a user - json is not valid")
    public void unsuccessfulCreateUser_jsonNotValid() {
        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";

        // given
        // 1. the server is up and running
        // 2. the database is up and running
        Map<String, Object> invalidUser = new HashMap<>();
        invalidUser.put("email", email);
        invalidUser.put("invalid-role", role);
        invalidUser.put("username", username);
        invalidUser.put("avatar", avatar);


        // when
        // A POST request is made to the path "/superapp/users"
        // with invalid user
        // then
        // the server returns status code 4xx
        assertThatThrownBy(() -> this.restTemplate.postForObject(this.baseUrl + "/superapp/users", invalidUser, UserBoundary.class))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError")
                .isEqualTo(true);
    }


}
