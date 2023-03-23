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
public class GroupController {
	
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
		groups.put("1", new ObjectBoundary());
		groups.put("2", new ObjectBoundary());
		
		return groups;
	}
//	public Group[] group(int size){
//		return IntStream.range(0, size) // Stream<Integer>
//				.map(x->x+1) // Stream<Integer>
//				.mapToObj(id->new Group("Message #" + id)) // Stream<Message>
//				.toList() // List<Message>
//				.toArray(new Group[0]); // Message[]
//	}
	
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
		objectBoundary.setCreateTimeStamp(new Date());
		objectBoundary.setObjectId(new ObjectId());
		System.err.println(objectBoundary.getObjectId().getInternalObjectId());
		return objectBoundary;
	}
	
	@RequestMapping(
			path = {"/superapp/objects/{superapp}/{internalObjectId}"},
			method = {RequestMethod.PUT},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateGroup(
			@PathVariable("superapp") String superapp,
			@PathVariable("internalObjectId") String internalObjectId,
			@RequestBody ObjectBoundary updatedGropu) {
		System.err.println("updating group #" + internalObjectId + " using: " + updatedGropu);
	}
	
}
