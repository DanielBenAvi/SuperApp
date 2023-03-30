package superapp;

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
import objectBoundary.CreatedBy;
import objectBoundary.ObjectBoundary;
import objectBoundary.ObjectId;
import objectBoundary.UserID;

@RestController
public class ObjectBoundaryController {
	
	/**
	 * Request specific Object Boundary
	 * @return Object Boundary
	 * 
	 * @author Daniel & Yaniv
	 */
	@RequestMapping(
			path = {"/superapp/objects/{superapp}/{internalObjectId}"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary retriveObject(
			@PathVariable("superapp") String superapp,
			@PathVariable("internalObjectId") String internalObjectId){
		ObjectBoundary ob = new ObjectBoundary();
		// hardCode
		ob.setObjectId(new ObjectId());
		ob.getObjectId().setInternalObjectId(internalObjectId);
		ob.getObjectId().setSuperApp(superapp);
		ob.setType("dummyType");
		ob.setAlias("demo instance");
		ob.setActive(true);
		ob.setCreateTimeStamp(new Date());
		UserID ud = new UserID("jill@demo.org",superapp);
		CreatedBy cb = new CreatedBy();
		cb.setUserId(ud);
		ob.setCreatedBy(cb);
		ob.setObjectDetails(new Date());
		// hardCode
		return ob;
	}
	
	/**
	 * Request all Object Boundary
	 * @return List of Object Boundary
	 * 
	 * @author Daniel & Yaniv
	 */
	@RequestMapping(
			path = {"/superapp/objects"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public Map<String, ObjectBoundary> getAllObjects(){
		Map<String, ObjectBoundary> objectBoundaryHash = new HashMap<>();
//		hard code
		objectBoundaryHash.put("1", new ObjectBoundary());
		objectBoundaryHash.put("2", new ObjectBoundary());
		
		return objectBoundaryHash;
	}
	
	/**
	 * Create new Object
	 * @return Object Boundary
	 * @param objectBoundary as ObjectBoundary
	 * 
	 * @author Daniel & Yaniv
	 */
	@RequestMapping(
			path = {"/superapp/objects"},
			method = {RequestMethod.POST},
			produces = {MediaType.APPLICATION_JSON_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary createObject(@RequestBody ObjectBoundary objectBoundary) {
//		hard code
		objectBoundary.setObjectId(new ObjectId());
		objectBoundary.getObjectId().setSuperApp("2023b.liorAriely");
		System.err.println(objectBoundary.getObjectId().getInternalObjectId());
		return objectBoundary;
	}
	
	
	/**
	 * updateObject 
	 * @param superapp name of the superapp
	 * @param internalObjectId the object id
	 * @param updatedObject
	 */
	@RequestMapping(
			path = {"/superapp/objects/{superapp}/{internalObjectId}"},
			method = {RequestMethod.PUT},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateObject(
			@PathVariable("superapp") String superapp,
			@PathVariable("internalObjectId") String internalObjectId,
			@RequestBody ObjectBoundary updatedObject) {
		
		if (updatedObject.getObjectId() == null || 
				updatedObject.getObjectId().getInternalObjectId() == null) {
			System.err.println("ObjectId or internalObjectId is null");
		}
		else {
		System.err.println("Updating object #" + internalObjectId + " using: " + updatedObject);
		}
		
	}
	
}
