package groupMiniApp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupController {
	/**
	 * Request specific group
	 * @return Group
	 * @author Daniel & Yaniv
	 */
	@RequestMapping(
			path = {"/groups/{groupName}"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public Group group(
			@PathVariable("groupName") String groupName){
		return new Group(groupName);
	}
	/**
	 * Request all groups
	 * @return List of Group
	 * @author Daniel & Yaniv
	 */
	@RequestMapping(
			path = {"/groups"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public Map<String, Group> group(){
		Map<String, Group> groups = new HashMap<>();
		groups.put("1", new Group("group 1"));
		groups.put("2", new Group("group 2"));
		
		return groups;
	}
	
}
