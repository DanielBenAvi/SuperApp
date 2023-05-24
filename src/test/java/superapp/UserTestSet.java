package superapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;
import superapp.data.UserRole;
import superapp.logic.boundaries.UserBoundary;
import superapp.logic.boundaries.UserId;

import java.util.HashMap;
import java.util.Map;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTestSet extends BaseTestSet{
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
                .setUserId(new UserId()
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
                .setUserId(new UserId()
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
                .setUserId(new UserId()
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
        UserBoundary expectedUser = new UserBoundary().setUserId(new UserId().setEmail(email).setSuperapp(springApplicationName)).setRole(role).setUsername(username).setAvatar(avatar);

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
                .setUserId(new UserId()
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
                .setUserId(new UserId()
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
                .setUserId(new UserId()
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
        help_PutUserBoundary(new UserBoundary().setUserId(new UserId().setEmail(updatedEmail)), email);


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
        help_PutUserBoundary(new UserBoundary().setUserId(new UserId().setSuperapp(updatedSuperAppName)), email);


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
        help_DeleteUsersBoundary(email1);

        //TODO - to check that after the deleting we get 2xx status code

        // then
        // the server returns status code 2xx
        // get all users (by creating 1 ADMIN user and see that there is only 1 user in the database)
        help_PostUserBoundary(email1, role, username, avatar);
        UserBoundary[] users = help_GetAllUsersBoundary(email1);
        assertThat(users).hasSize(1);
    }

//    @Test
//    @DisplayName("successfully delete all users - 0 users in the database")
//    public void successfulDeleteAllUsers_empty() {
//        // given
//        // 1. the server is up and running
//        // 2. the database is up and running
//        // 3. there are no users in the database
//
//        // when
//        // A DELETE request is made to the path "/superapp/admin/users"
//        help_DeleteUsersBoundary();
//
//        // then
//        // the server returns status code 2xx
//        // get all users
//        UserBoundary[] users = help_GetAllUsersBoundary();
//        assertThat(users).isEmpty();
//    }

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
        UserBoundary[] users = help_GetAllUsersBoundary(email1);

        // then
        // the server returns status code 2xx
        // users are returned -> 2 users
        assertThat(users).hasSize(2);
    }

//    @Test
//    @DisplayName("successfully get all users - 0 users in the database")
//    public void successfulGetAllUsers_empty() {
//        String url = this.baseUrl + "/superapp/admin/users";
//        // given
//        // 1. the server is up and running
//        // 2. the database is up and running
//        // 3. there are no users in the database
//
//        // when
//        // A GET request is made to the path "/superapp/admin/users"
//        UserBoundary[] users = help_GetAllUsersBoundary();
//
//        // then
//        // the server returns status code 2xx
//        // users are returned -> 2 users
//        assertThat(users).isEmpty();
//    }


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
                .setUserId(new UserId()
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
