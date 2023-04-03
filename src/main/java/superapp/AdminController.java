package superapp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
	/**
	 * Export all users
	 * @return Array of all users
	 * @author Omer&Lior
	 */
	@RequestMapping(
			path = {"/superapp/admin/users"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	
	public Map<String, Object> allusers(){
		// Hard coded for now, In the future intended to be "new User"
		Map<String, Object> allUsers = new HashMap<>();
		allUsers.put("User 1", "Omer Lande");
		allUsers.put("User 2", "Lior Ariely");
		return allUsers;
	}
	
	/**
	 * Export all history commands of specific miniapps 
	 * @return Array of history commands of specific miniapps 
	 * @author Omer&Lior
	 */ 
	@RequestMapping(
			path = {"/superapp/admin/miniapp/{miniAppName}"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	
	public Map<String, Object> specificMiniAppCommands(@PathVariable("miniAppName") String miniAppName){
		Map<String, Object> specificMiniAppCommands = new HashMap<>();
		specificMiniAppCommands.put(miniAppName, "Post event");
		return specificMiniAppCommands;
	}
	
	/**
	 * Export all history commands
	 * @return Array of history commands
	 * @author Omer&Lior
	 */ 
	@RequestMapping(
			path = {"/superapp/admin/miniapp"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	
	public Map<String, Object> miniAppCommands(){
		Map<String, Object> miniAppCommands = new HashMap<>();
		miniAppCommands.put("1", "Post event");
		miniAppCommands.put("2", "Match");
		miniAppCommands.put("3", "Modify location");
		return miniAppCommands;
	}
	
	
	// delete all the users in the superapp.
	@RequestMapping(
			path = {"/superapp/admin/users"},
			method = {RequestMethod.DELETE})
	public void deleteAllUsers() {
		// do nothing
		System.err.println("delete all users ");
	}
	
	//delete all the objects in the superapp.
	@RequestMapping(
			path = {"/superapp/admin/objects"},
			method = {RequestMethod.DELETE})
	public void deleteAllObjects() {
		// do nothing
		System.err.println("delete all objects ");
	}
	
	//delete all command history.
	@RequestMapping(
			path = {"/superapp/admin/miniapp"},
			method = {RequestMethod.DELETE})
	public void deleteAllcommnads() {
		// do nothing
		System.err.println("delete all commands ");
	}
}
