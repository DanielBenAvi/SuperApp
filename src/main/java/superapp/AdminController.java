package superapp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import superapp.logic.ObjectsService;
import superapp.logic.UsersService;
import superapp.logic.boundaries.UserBoundary;

@RestController
public class AdminController {

	private UsersService usersService;
	private ObjectsService objectsService;


	@Autowired
	public void setUsersService(UsersService usersService){
		this.usersService = usersService;
	}

	@Autowired
	public void setObjectsService(ObjectsService objectsService) {
		this.objectsService = objectsService;
	}


	/**
	 * This method exports all users
	 *
	 * @return UserBoundary[]
	 */
	@RequestMapping(
			path = {"/superapp/admin/users"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	
	public UserBoundary[] allUsers(){

		return usersService.getAllUsers().toArray(new UserBoundary[0]);
	}
	

	/**
	 * This method exports all history commands of specific miniapps
	 *
	 * @param miniAppName String
	 * @return
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
	 * @return
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
	
	

	/**
	 * Delete all users
	 */
	@RequestMapping(
			path = {"/superapp/admin/users"},
			method = {RequestMethod.DELETE})
	public void deleteAllUsers() {
		usersService.deleteAllUsers();
		System.err.println("delete all users");
	}


	/**
	 * This method delete all objects of superapp.
	 */
	@RequestMapping(
			path = {"/superapp/admin/objects"},
			method = {RequestMethod.DELETE})
	public void deleteAllObjects() {

		this.objectsService.deleteAllObjects();
		System.err.println("delete all superapp objects");
	}
	
	//delete all command history.
	@RequestMapping(
			path = {"/superapp/admin/miniapp"},
			method = {RequestMethod.DELETE})
	public void deleteAllCommands() {
		// do nothing
		System.err.println("delete all commands");
	}
}
