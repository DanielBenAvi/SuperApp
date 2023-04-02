package superapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;
import superapp.logic.ObjectsService;
import superapp.logic.boundaries.SuperAppObjectBoundary;


@RestController
public class ObjectBoundaryController {

	private ObjectsService objectsService;

	@Autowired
	public void setObjectsService(ObjectsService objectsService) {
		this.objectsService = objectsService;
	}

	/**
	 * Request specific Object Boundary
	 * @return Object Boundary
	 * @author Daniel & Yaniv
	 */
	@GetMapping(path = {"/superapp/objects/{superapp}/{internalObjectId}"},
				produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary getObject(@PathVariable("superapp") String superapp,
											@PathVariable("internalObjectId") String internalObjectId) {

		return objectsService.getSpecificObject(superapp, internalObjectId);
	}
	
	/**
	 * Request all Object Boundary
	 * @return List of Object Boundary
	 * 
	 * @author Daniel & Yaniv
	 */
	@GetMapping(path = {"/superapp/objects"},
				produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary[] getAllObjects() {

		return this.objectsService.getAllObjects()
				.toArray(new SuperAppObjectBoundary[0]);
	}
	
	/**
	 * Create new Object
	 * @return Object Boundary
	 * @param superAppObjectBoundary as ObjectBoundary
	 * @author Daniel & Yaniv
	 */
	@PostMapping(
			path = {"/superapp/objects"},
			produces = {MediaType.APPLICATION_JSON_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary createObject(@RequestBody SuperAppObjectBoundary superAppObjectBoundary) {

		// TODO : question about path (user ID), Ido & Yosef
		return this.objectsService.createObject(superAppObjectBoundary);
	}
	
	
	/**
	 * updateObject 
	 * @param superapp name of the superapp
	 * @param internalObjectId the object id
	 * @param updatedObject
	 */
	@PutMapping(
			path = {"/superapp/objects/{superapp}/{internalObjectId}"},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateObject(@PathVariable("superapp") String superapp,
							 @PathVariable("internalObjectId") String internalObjectId,
							 @RequestBody SuperAppObjectBoundary updatedObject) {

		this.objectsService.updateObject(superapp, internalObjectId, updatedObject);
	}


	// just for testing
//	@DeleteMapping(path = {"/superapp/admin/objects"})
//	public void deleteAllObjects() {
//		// do nothing
//		this.objectsService.deleteAllObjects();
//		System.err.println("all objects deleted ");
//	}
}
