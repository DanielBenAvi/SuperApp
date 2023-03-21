package demo;

import java.util.ArrayList;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	
	
	/**
	 * Request specific product
	 * @return User
	 * @author Ido & Yosef
	 */
	
	@GetMapping(
            path = {"/superapp/users/login/{superapp}/{email}"},
            produces = {MediaType.APPLICATION_JSON_VALUE})
	
	public UserBoundary user(@PathVariable("email") String email, @PathVariable("superapp") String superapp) {
		UserID userId = new UserID(superapp, email);
		
		UserDetails userDetails = new UserDetails("Ido", "052", new ArrayList<>(), "male", new ArrayList<>());
		
		return new UserBoundary(userId, "Student", "Dani", "avatar", userDetails);
	}
	
	// TODO : Post - path = /superapp/users
	// and Put - path = /superapp/users/{superapp}/{email}
}
