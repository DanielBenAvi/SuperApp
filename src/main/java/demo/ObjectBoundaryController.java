package demo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import groupMiniApp.Group;
import objectBoundary.ObjectBoundary;
import objectBoundary.ObjectId;

@RestController
public class ObjectBoundaryController {
	
	/**
	 * Request specific group
	 * @return Group
	 * 
	 * @author Daniel & Yaniv
	 */
	@RequestMapping(
			path = {"/superapp/objects/{superapp}/{internalObjectId}"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary group(
			@PathVariable("superapp") String superapp,
			@PathVariable("internalObjectId") String internalObjectId){
		ObjectBoundary ob = new ObjectBoundary();
//		ob.setObjectId(new ObjectId());
		return ob;
	}
	
	/**
	 * Request all groups
	 * @return List of Group
	 * 
	 * @author Daniel & Yaniv
	 */
	@RequestMapping(
			path = {"/superapp/objects"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public Map<String, ObjectBoundary> getAllGroup(){
		Map<String, ObjectBoundary> groups = new HashMap<>();
//		hard code
		groups.put("1", new ObjectBoundary());
		groups.put("2", new ObjectBoundary());
		
		return groups;
	}
	
	/**
	 * Create new Group
	 * @return Object Boundary
	 * @param objectBoundary as ObjectBoundary
	 * @param superapp as String
	 * 
	 * @author Daniel & Yaniv
	 */
	@RequestMapping(
			path = {"/superapp/objects/{superapp}"},
			method = {RequestMethod.POST},
			produces = {MediaType.APPLICATION_JSON_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary createGroup(
			@PathVariable("superapp") String superapp, 
			@RequestBody ObjectBoundary objectBoundary) {
//		hard code
		objectBoundary.setCreateTimeStamp(new Date());
		objectBoundary.setObjectId(new ObjectId());
		System.err.println(objectBoundary.getObjectId().getInternalObjectId());
		return objectBoundary;
	}
	
	
	/**
	 * updateGroup 
	 * @param superapp name of the superapp
	 * @param internalObjectId the object id
	 * @param updatedGrop
	 */
	@RequestMapping(
			path = {"/superapp/objects/{superapp}/{internalObjectId}"},
			method = {RequestMethod.PUT},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateGroup(
			@PathVariable("superapp") String superapp,
			@PathVariable("internalObjectId") String internalObjectId,
			@RequestBody ObjectBoundary updatedGroup) {
		System.err.println("Updating group #" + internalObjectId + " using: " + updatedGroup);
	}
	
}
