package demo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
		//Hard coded for now, In the future intended to be "new User"
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
	
	public Map<String, Object> specificMiniAppCommands(){
		Map<String, Object> specificMiniAppCommands = new HashMap<>();
		specificMiniAppCommands.put("1", "Post event");
		specificMiniAppCommands.put("2", "Modify location");
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
		for(int i=0; i<specificMiniAppCommands().size(); i++)
		{
			miniAppCommands.put("1", specificMiniAppCommands().get(i).toString());
		}
		return miniAppCommands;
	}
	
	
}
