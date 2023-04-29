package superapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import superapp.logic.MiniAppCommandService;
import superapp.logic.ObjectsService;
import superapp.logic.UsersService;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.UserBoundary;

@RestController
public class AdminController {

	private UsersService usersService;
	private ObjectsService objectsService;
	private MiniAppCommandService miniAppCommandService;


	@Autowired
	public void setUsersService(UsersService usersService){
		this.usersService = usersService;
	}

	@Autowired
	public void setObjectsService(ObjectsService objectsService) {
		this.objectsService = objectsService;
	}

	@Autowired
	public void setMiniAppCmdService(MiniAppCommandService miniAppCmdService) {
		this.miniAppCommandService = miniAppCmdService;
	}
	
	/**
	 * This method exports all users
	 *
	 * @return UserBoundary[]
	 */
	@GetMapping(path = {"/superapp/admin/users"},
				produces = {MediaType.APPLICATION_JSON_VALUE})
	public UserBoundary[] allUsers(){

		return this.usersService.getAllUsers().toArray(new UserBoundary[0]);
	}
	

	/**
	 * This method exports all history commands of specific miniapps
	 *
	 * @param miniAppName String
	 * @return MiniAppCommandBoundary[]
	 */
	@GetMapping(path = {"/superapp/admin/miniapp/{miniAppName}"},
				produces = {MediaType.APPLICATION_JSON_VALUE})
	public MiniAppCommandBoundary[] specificMiniAppCommands(@PathVariable("miniAppName") String miniAppName){

		return this.miniAppCommandService.getAllMiniAppCommands(miniAppName).toArray(new MiniAppCommandBoundary[0]);
	}
	
	/**
	 * Export all history commands
	 * @return MiniAppCommandBoundary[]
	 */ 
	@GetMapping(path = {"/superapp/admin/miniapp"},
				produces = {MediaType.APPLICATION_JSON_VALUE})
	public MiniAppCommandBoundary[] miniAppCommands(){

		return this.miniAppCommandService.getAllCommands().toArray(new MiniAppCommandBoundary[0]);
	}

	/**
	 * This method delete all users.
	 */
	@DeleteMapping(path = {"/superapp/admin/users"})
	public void deleteAllUsers() {

		this.usersService.deleteAllUsers();
		System.err.println("All users deleted");
	}

	/**
	 * This method delete all objects of superapp.
	 */
	@DeleteMapping(path = {"/superapp/admin/objects"})
	public void deleteAllObjects() {

		this.objectsService.deleteAllObjects();
		System.err.println("All superapp objects deleted");
	}

	/**
	 * This method delete all commands history.
	 */
	@DeleteMapping(path = {"/superapp/admin/miniapp"})
	public void deleteAllCommands() {

		this.miniAppCommandService.deleteAllCommands();
		System.err.println("All commands history deleted");
	}

}
