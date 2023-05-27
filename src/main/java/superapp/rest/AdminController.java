package superapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import superapp.logic.*;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.UserBoundary;

@RestController
public class AdminController {

	private UserServiceWithPaging usersService;
	private ObjectsServiceWithPaging objectsService;
	private MiniAppCommandWithPaging miniAppCommandService;


	@Autowired
	public void setUsersService(UserServiceWithPaging usersService){
		this.usersService = usersService;
	}

	@Autowired
	public void setObjectsService(ObjectsServiceWithPaging objectsService) {
		this.objectsService = objectsService;
	}

	@Autowired
	public void setMiniAppCmdService(MiniAppCommandWithPaging miniAppCmdService) {
		this.miniAppCommandService = miniAppCmdService;
	}
	
	/**
	 * This method exports all users
	 *
	 * @return UserBoundary[]
	 */
	@GetMapping(path = {"/superapp/admin/users"},
				produces = {MediaType.APPLICATION_JSON_VALUE})
	public UserBoundary[] allUsers(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
		    @RequestParam(name = "userEmail", required = true) String userEmail,
		    @RequestParam(name = "size", required = false, defaultValue = "15") int size,
		    @RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		return this.usersService.getAllUsers(userSuperapp, userEmail, size, page).toArray(new UserBoundary[0]);
	}
	

	/**
	 * This method exports all history commands of specific miniapps
	 *
	 * @param miniAppName String
	 * @return MiniAppCommandBoundary[]
	 */
	@GetMapping(path = {"/superapp/admin/miniapp/{miniAppName}"},
				produces = {MediaType.APPLICATION_JSON_VALUE})
	public MiniAppCommandBoundary[] specificMiniAppCommands(
			@PathVariable("miniAppName") String miniAppName,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page){

		return this.miniAppCommandService.getAllMiniAppCommands(miniAppName,userSuperapp,userEmail,size,page)
				.toArray(new MiniAppCommandBoundary[0]);
	}
	
	/**
	 * Export all history commands
	 * @return MiniAppCommandBoundary[]
	 */ 
	@GetMapping(path = {"/superapp/admin/miniapp"},
				produces = {MediaType.APPLICATION_JSON_VALUE})
	public MiniAppCommandBoundary[] miniAppCommands(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page){

		return this.miniAppCommandService.getAllCommands(userSuperapp,userEmail,size,page).toArray(new MiniAppCommandBoundary[0]);
	}

	/**
	 * This method delete all users.
	 */
	@DeleteMapping(path = {"/superapp/admin/users"})
	public void deleteAllUsers(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail) {

		this.usersService.deleteAllUsers(userSuperapp, userEmail);
		System.err.println("All users deleted");
	}

	/**
	 * This method delete all objects of superapp.
	 */
	@DeleteMapping(path = {"/superapp/admin/objects"})
	public void deleteAllObjects(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail) {

		this.objectsService.deleteAllObjects(userSuperapp, userEmail);
		System.err.println("All superapp objects deleted");
	}



	/**
	 * This method delete all commands history.
	 */
	@DeleteMapping(path = {"/superapp/admin/miniapp"})
	public void deleteAllCommands(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail) {

		this.miniAppCommandService.deleteAllCommands(userSuperapp, userEmail);
		System.err.println("All commands history deleted");

	}

}
