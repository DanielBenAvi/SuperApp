package superapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import superapp.logic.mongo.NotFoundException;
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
	 * This method is for user login.
	 *
	 * @return UserBoundary
	 */
	@GetMapping(path = {"/superapp/users/login/{superapp}/{email}"},
				produces = {MediaType.APPLICATION_JSON_VALUE})
	public UserBoundary login(@PathVariable("email") String email, @PathVariable("superapp") String superapp) {

		return this.usersService.login(superapp,email)
				.orElseThrow(() -> new NotFoundException("User with id " + superapp + "_" + email + " not found"));
	}


	/**
	 * This method create-register new user.
	 * @param newUser NewUserBoundary
	 * @return UserBoundary
	 */
	@PostMapping(path = {"/superapp/users"},
				produces = {MediaType.APPLICATION_JSON_VALUE},
				consumes = {MediaType.APPLICATION_JSON_VALUE})
	public UserBoundary createUser(@RequestBody NewUserBoundary newUser) {

		// create UserBoundary from NewUserBoundary
		UserBoundary userBoundary = new UserBoundary()
									.setUserId(new UserID(null, newUser.getEmail())) // superapp name will update in the service.
									.setUsername(newUser.getUsername())
									.setAvatar(newUser.getAvatar())
									.setRole(newUser.getRole());

		return this.usersService.createUser(userBoundary);
	}

	/**
	 * This method update user details.
	 *
	 * @param userEmail the user mail
	 * @param superapp the suparapp name
	 * @param updatedUser the user boundary with changes
	 */
	@PutMapping(path = {"/superapp/users/{superapp}/{userEmail}"},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateUser(@PathVariable("userEmail") String userEmail,
						   @PathVariable("superapp") String superapp,
						   @RequestBody UserBoundary updatedUser) {

		this.usersService.updateUser(userEmail, superapp, updatedUser);
	}

}
