package superapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
	
	public MiniAppCommandBoundary[] specificMiniAppCommands(@PathVariable("miniAppName") String miniAppName){
		MiniAppCommandBoundary[] cmds = new MiniAppCommandBoundary[miniAppCommandService.getAllCommands().size()];
		//return miniAppCommandService.getAllMiniAppCommands(miniAppName).toArray(new MiniAppCommandBoundary[0]);
		return miniAppCommandService.getAllMiniAppCommands(miniAppName).toArray(cmds);
	}
	
	/**
	 * Export all history commands
	 * @return
	 */ 
	@RequestMapping(
			path = {"/superapp/admin/miniapp"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	
	public MiniAppCommandBoundary[] miniAppCommands(){
		MiniAppCommandBoundary[] cmds = new MiniAppCommandBoundary[miniAppCommandService.getAllCommands().size()];
		//return miniAppCommandService.getAllCommands().toArray(new MiniAppCommandBoundary[0]);
		return miniAppCommandService.getAllCommands().toArray(cmds);
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
		this.miniAppCommandService.deleteAllCommands();
		System.err.println("delete all commands");
	}
}
