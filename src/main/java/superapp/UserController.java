package superapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import superapp.logic.UsersService;
import superapp.logic.boundaries.UserID;
import superapp.logic.boundaries.NewUserBoundary;
import superapp.logic.boundaries.UserBoundary;

@RestController
public class UserController {

	private UsersService usersService;

	@Autowired
	public void setUsersService(UsersService usersService){
		this.usersService = usersService;
	}


	/**
	 * login
	 *
	 * @return userBoundary
	 */
	@GetMapping(path = {"/superapp/users/login/{superapp}/{email}"},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public UserBoundary login(@PathVariable("email") String email, @PathVariable("superapp") String superapp) {
//		UserID userId = new UserID(superapp, email);
//		return new UserBoundary(userId, "Student", "Dani", "avatar");
		return this.usersService.login(superapp,email).orElseThrow(() -> new RuntimeException("Could not login: "+superapp+" "+email));
	}


	/**
	 * create new user
	 * @param newUser the new user boundary that is created
	 * @return userBoundary
	 */
	@PostMapping(path = {"/superapp/users"},
			produces = {MediaType.APPLICATION_JSON_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public UserBoundary createUser(@RequestBody NewUserBoundary newUser) {
		// convert NewUserBoundary to UserBoundary
		UserBoundary userBoundary= new UserBoundary();
		// crate a userID object
		userBoundary.setUserId(new UserID("${spring.application.name:defaultAppName}", newUser.getEmail()));
		userBoundary.setUsername(newUser.getUsername());
		userBoundary.setAvatar(newUser.getAvatar());
		userBoundary.setRole(newUser.getRole());

		return this.usersService.createUser(userBoundary);
//		return new UserBoundary(userId, newUser.getRole(),newUser.getUserName(),newUser.getAvatar());
	}

	/**
	 * update user
	 * @param userEmail the user mail
	 * @param superapp the suparapp name
	 * @param updatedUser the user boundary with changes
	 */
	@PutMapping(path = {"/superapp/users/{superapp}/{userEmail}"},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateUser(@PathVariable("userEmail") String userEmail, @PathVariable("superapp") String superapp,@RequestBody UserBoundary updatedUser) {

//		System.err.println("update user UrlMail: " + userEmail);
//		System.err.println("update user UrlSuperapp: " + superapp);
//		System.err.println("update user Info: " + updatedUser);

		this.usersService.updateUser(userEmail,superapp,updatedUser);

	}



}
