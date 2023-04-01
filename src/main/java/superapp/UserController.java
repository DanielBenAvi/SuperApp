package superapp;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import superapp.logic.boundaries.UserID;
import superapp.logic.boundaries.NewUserBoundary;
import superapp.logic.boundaries.UserBoundary;

@RestController
public class UserController {


	/**

	 * Request specific product

	 * @return User

	 * @author Ido & Yosef

	 */

	@GetMapping(path = {"/superapp/users/login/{superapp}/{email}"},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public UserBoundary login(@PathVariable("email") String email, @PathVariable("superapp") String superapp) {
		UserID userId = new UserID(superapp, email);
		return new UserBoundary(userId, "Student", "Dani", "avatar");
	}




	@PostMapping(path = {"/superapp/users"},
			produces = {MediaType.APPLICATION_JSON_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public UserBoundary createUser(@RequestBody NewUserBoundary newUser) {
		UserID userId = new UserID("SocialHive", newUser.getEmail());
		return new UserBoundary(userId, newUser.getRole(),newUser.getUserName(),newUser.getAvatar());
	}


	@PutMapping(path = {"/superapp/users/{superapp}/{userEmail}"},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateUser(@PathVariable("userEmail") String userEmail, @PathVariable("superapp") String superapp,@RequestBody UserBoundary updatedUser) {

		// doNothing
		System.err.println("update user UrlMail: " + userEmail);
		System.err.println("update user UrlSuperapp: " + superapp);
		System.err.println("update user Info: " + updatedUser);

	}



}
