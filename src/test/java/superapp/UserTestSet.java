package superapp;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import superapp.data.UserRole;
import superapp.logic.boundaries.NewUserBoundary;
import superapp.logic.boundaries.UserBoundary;
import superapp.logic.boundaries.UserID;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;


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

    @Test
    public void successfulLoginUser() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        NewUserBoundary user = new NewUserBoundary();
        user.setEmail("demo@gmail.com").setRole("MINIAPP_USER").setUsername("demo_user").setAvatar("demo_avatar");
        this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user, UserBoundary.class);


        // when
        // A GET request is made to the path "/superapp/users/login/{superapp}/{email}"
        UserBoundary userBoundary = this.restTemplate.getForObject(this.baseUrl + "/superapp/users/login/{superapp}/{email}", UserBoundary.class, springApplicationName, user.getEmail());

        // then
        // the server returns status code 2xx
        UserBoundary expectedUser = new UserBoundary().setUserId(new UserID().setEmail("demo@gmail.com").setSuperapp(springApplicationName)).setRole("MINIAPP_USER").setUsername("demo_user").setAvatar("demo_avatar");

        // userBoundary is equal to expectedUser
        assertThat(userBoundary).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    public void unsuccessfulLoginUser() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running


        // when
        // A GET request is made to the path "/superapp/users/login/{superapp}/{email}"
        UserBoundary userBoundary;
        try {
            userBoundary = this.restTemplate.getForObject(this.baseUrl + "/superapp/users/login/{superapp}/{email}", UserBoundary.class, springApplicationName, "demo@gmail.com");
        } catch (RestClientException e) {
            userBoundary = null;
            System.out.println("Exception: " + e.getMessage());
        }

        // then
        // the server returns status code 4xx
        // TODO: check if the server returns status code 4xx
        assertThat(userBoundary).isNull();
    }

    @Test
    public void successfulCreateADMIN() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"
        NewUserBoundary user = new NewUserBoundary();
        user.setEmail("demo@gmail.com").setRole(UserRole.ADMIN.toString()).setUsername("demo_user").setAvatar("demo_avatar");
        UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user, UserBoundary.class);

        // then
        // the server returns status code 2xx
        // userBoundary is equal to the user that was created
        UserBoundary expectedUser = new UserBoundary().setUserId(new UserID().setEmail("demo@gmail.com").setSuperapp(springApplicationName)).setRole(UserRole.ADMIN.toString()).setUsername("demo_user").setAvatar("demo_avatar");

        assertThat(userBoundary).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    public void successfulCreateMINIAPP_USER() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"
        NewUserBoundary user = new NewUserBoundary();
        user.setEmail("demo@gmail.com").setRole(UserRole.MINIAPP_USER.toString()).setUsername("demo_user").setAvatar("demo_avatar");
        UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user, UserBoundary.class);

        // then
        // the server returns status code 2xx
        // userBoundary is equal to the user that was created
        UserBoundary expectedUser = new UserBoundary().setUserId(new UserID().setEmail("demo@gmail.com").setSuperapp(springApplicationName)).setRole(UserRole.MINIAPP_USER.toString()).setUsername("demo_user").setAvatar("demo_avatar");

        assertThat(userBoundary).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    public void successfulCreateSUPERAPP_USER() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"
        NewUserBoundary user = new NewUserBoundary();
        user.setEmail("demo@gmail.com").setRole(UserRole.SUPERAPP_USER.toString()).setUsername("demo_user").setAvatar("demo_avatar");
        UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user, UserBoundary.class);

        // then
        // the server returns status code 2xx
        // userBoundary is equal to the user that was created
        UserBoundary expectedUser = new UserBoundary().setUserId(new UserID().setEmail("demo@gmail.com").setSuperapp(springApplicationName)).setRole(UserRole.SUPERAPP_USER.toString()).setUsername("demo_user").setAvatar("demo_avatar");

        assertThat(userBoundary).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }


    @Test
    public void unsuccessfulCreateUser_email() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"
        try {

            NewUserBoundary user = new NewUserBoundary();
            user.setEmail(null).setRole(UserRole.ADMIN.toString()).setUsername("demo_user").setAvatar("demo_avatar");
            UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user, UserBoundary.class);
        } catch (RestClientException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        // then
        // the server returns status code 5xx

    }

    @Test
    public void unsuccessfulCreateUser_role() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"
        try {

            NewUserBoundary user = new NewUserBoundary();
            user.setEmail(null).setRole(UserRole.ADMIN.toString()).setUsername("demo_user").setAvatar("demo_avatar");
            UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user, UserBoundary.class);
        } catch (RestClientException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        // then
        // the server returns status code 5xx

    }

    @Test
    public void unsuccessfulCreateUser_username() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"
        try {

            NewUserBoundary user = new NewUserBoundary();
            user.setEmail(null).setRole(UserRole.ADMIN.toString()).setUsername("demo_user").setAvatar("demo_avatar");
            UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user, UserBoundary.class);
        } catch (RestClientException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        // then
        // the server returns status code 5xx
    }

    @Test
    public void unsuccessfulCreateUser_avatar() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running

        // when
        // A POST request is made to the path "/superapp/users"
        try {

            NewUserBoundary user = new NewUserBoundary();
            user.setEmail(null).setRole(UserRole.ADMIN.toString()).setUsername("demo_user").setAvatar("demo_avatar");
            UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user, UserBoundary.class);
        } catch (RestClientException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        // then
        // the server returns status code 5xx
    }


    @Test
    public void unsuccessfulCreateUser_alreadyExists() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. a user with the same email already exists in the database
        NewUserBoundary user1 = new NewUserBoundary();
        user1.setEmail("demo@gmail.com").setRole(UserRole.SUPERAPP_USER.toString()).setUsername("demo_user").setAvatar("demo_avatar");
        UserBoundary userBoundary1 = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user1, UserBoundary.class);

        // when
        // A POST request is made to the path "/superapp/users"
        try {

            NewUserBoundary user2 = new NewUserBoundary();
            user2.setEmail("demo@gmail.com").setRole(UserRole.SUPERAPP_USER.toString()).setUsername("demo_user").setAvatar("demo_avatar");
            UserBoundary userBoundary2 = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user2, UserBoundary.class);
        } catch (RestClientException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        // then
        // the server returns status code 4xx
        // the user is not created
    }

    @Test
    public void successfulUpdateUser_role() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        NewUserBoundary user = new NewUserBoundary();
        user.setEmail("demo@gmail.com").setRole(UserRole.SUPERAPP_USER.toString()).setUsername("demo_user").setAvatar("demo_avatar");
        UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user, UserBoundary.class);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        // with the new role from SUPERAPP_USER to ADMIN
        userBoundary.setRole(UserRole.ADMIN.toString());
        UserBoundary updatedUser = new UserBoundary().setRole(UserRole.ADMIN.toString());
        this.restTemplate.put(this.baseUrl + "/superapp/users/{superapp}/{email}", updatedUser, springApplicationName, user.getEmail());


        // then
        // the server returns status code 2xx
        // the user's role is updated
        UserBoundary expectedUser = this.restTemplate.getForObject(this.baseUrl + "/superapp/users/login/{superapp}/{email}", UserBoundary.class, springApplicationName, user.getEmail());

        assertThat(userBoundary).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    public void successfulUpdateUser_username() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        NewUserBoundary user = new NewUserBoundary();
        user.setEmail("demo@gmail.com").setRole(UserRole.SUPERAPP_USER.toString()).setUsername("demo_user").setAvatar("demo_avatar");
        UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user, UserBoundary.class);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        // with the new username from demo_user to demo_user_updated
        UserBoundary updatedUser = new UserBoundary().setUsername("demo_user_updated");
        this.restTemplate.put(this.baseUrl + "/superapp/users/{superapp}/{email}", updatedUser, springApplicationName, user.getEmail());

        userBoundary.setUsername("demo_user_updated");

        // then
        // the server returns status code 2xx
        // the user's role is updated
        UserBoundary expectedUser = this.restTemplate.getForObject(this.baseUrl + "/superapp/users/login/{superapp}/{email}", UserBoundary.class, springApplicationName, user.getEmail());

        assertThat(userBoundary).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    public void successfulUpdateUser_avatar() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        NewUserBoundary user = new NewUserBoundary();
        user.setEmail("demo@gmail.com").setRole(UserRole.SUPERAPP_USER.toString()).setUsername("demo_user").setAvatar("demo_avatar");
        UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user, UserBoundary.class);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        // with the new avatar from demo_avatar to demo_avatar_updated
        UserBoundary updatedUser = new UserBoundary().setAvatar("demo_avatar_updated");
        this.restTemplate.put(this.baseUrl + "/superapp/users/{superapp}/{email}", updatedUser, springApplicationName, user.getEmail());

        userBoundary.setAvatar("demo_avatar_updated");

        // then
        // the server returns status code 2xx
        // the user's role is updated
        UserBoundary expectedUser = this.restTemplate.getForObject(this.baseUrl + "/superapp/users/login/{superapp}/{email}", UserBoundary.class, springApplicationName, user.getEmail());

        assertThat(userBoundary).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }


    @Test
    public void unsuccessfulUpdateUser_email() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        NewUserBoundary user = new NewUserBoundary();
        user.setEmail("demo@gmail.com").setRole(UserRole.SUPERAPP_USER.toString()).setUsername("demo_user").setAvatar("demo_avatar");
        UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user, UserBoundary.class);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        // with the new avatar from demo_avatar to demo_avatar_updated
        UserBoundary updatedUser = new UserBoundary().setUserId(new UserID().setEmail("newdemo@gmail.com"));
        this.restTemplate.put(this.baseUrl + "/superapp/users/{superapp}/{email}", updatedUser, springApplicationName, user.getEmail());

        // then
        // the server returns status code 2xx
        // the user's role is updated
        UserBoundary expectedUser = this.restTemplate.getForObject(this.baseUrl + "/superapp/users/login/{superapp}/{email}", UserBoundary.class, springApplicationName, user.getEmail());

        assertThat(userBoundary).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }


    @Test
    public void unsuccessfulUpdateUser_superapp() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        NewUserBoundary user = new NewUserBoundary();
        user.setEmail("demo@gmail.com").setRole(UserRole.SUPERAPP_USER.toString()).setUsername("demo_user").setAvatar("demo_avatar");
        UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user, UserBoundary.class);

        // when
        // A PUT request is made to the path "/superapp/users/{superapp}/{email}"
        // with the new avatar from demo_avatar to demo_avatar_updated
        UserBoundary updatedUser = new UserBoundary().setUserId(new UserID().setSuperapp("Superapp_updated"));
        this.restTemplate.put(this.baseUrl + "/superapp/users/{superapp}/{email}", updatedUser, springApplicationName, user.getEmail());

        // then
        // the server returns status code 2xx
        // the user's role is updated
        UserBoundary expectedUser = this.restTemplate.getForObject(this.baseUrl + "/superapp/users/login/{superapp}/{email}", UserBoundary.class, springApplicationName, user.getEmail());

        assertThat(userBoundary).isNotNull().usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    public void successfulDeleteAllUsers() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        NewUserBoundary user1 = new NewUserBoundary();
        user1.setEmail("demo1@gmail.com").setRole(UserRole.SUPERAPP_USER.toString()).setUsername("demo_user").setAvatar("demo_avatar");
        this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user1, UserBoundary.class);

        NewUserBoundary user2 = new NewUserBoundary();
        user2.setEmail("demo2@gmail.com").setRole(UserRole.SUPERAPP_USER.toString()).setUsername("demo_user").setAvatar("demo_avatar");
        this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user2, UserBoundary.class);

        // when
        // A DELETE request is made to the path "/superapp/admin/users"
        this.restTemplate.delete(this.baseUrl + "/superapp/admin/users");

        // then
        // the server returns status code 2xx
        // get all users
        UserBoundary[] users = this.restTemplate.getForObject(this.baseUrl + "/superapp/admin/users", UserBoundary[].class);
        assertThat(users).isEmpty();

    }

    @Test
    public void successfulDeleteAllUsers_empty() {
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. there are no users in the database

        // when
        // A DELETE request is made to the path "/superapp/admin/users"
        this.restTemplate.delete(this.baseUrl + "/superapp/admin/users");

        // then
        // the server returns status code 2xx
        // get all users
        UserBoundary[] users = this.restTemplate.getForObject(this.baseUrl + "/superapp/admin/users", UserBoundary[].class);
        assertThat(users).isEmpty();
    }

    @Test
    public void successfulGetAllUsers() {

        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. the user is registered
        NewUserBoundary user1 = new NewUserBoundary();
        user1.setEmail("demo1@gmail.com").setRole(UserRole.SUPERAPP_USER.toString()).setUsername("demo_user").setAvatar("demo_avatar");
        this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user1, UserBoundary.class);

        NewUserBoundary user2 = new NewUserBoundary();
        user2.setEmail("demo2@gmail.com").setRole(UserRole.SUPERAPP_USER.toString()).setUsername("demo_user").setAvatar("demo_avatar");
        this.restTemplate.postForObject(this.baseUrl + "/superapp/users", user2, UserBoundary.class);

        // when
        // A GET request is made to the path "/superapp/admin/users"
        UserBoundary[] users = this.restTemplate.getForObject(this.baseUrl + "/superapp/admin/users", UserBoundary[].class);

        // then
        // the server returns status code 2xx
        // users are returned -> 2 users
        assertThat(users).hasSize(2);
    }

    @Test
    public void successfulGetAllUsers_empty() {
        String url = this.baseUrl + "/superapp/admin/users";
        // given
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. there are no users in the database

        // when
        // A GET request is made to the path "/superapp/admin/users"
        UserBoundary[] users = this.restTemplate.getForObject(url, UserBoundary[].class);

        // then
        // the server returns status code 2xx
        assertThat(users).isEmpty();
        assertThat(this.restTemplate.getForEntity(url, UserBoundary[].class).getStatusCode()).isEqualTo(HttpStatus.OK);

    }
}
