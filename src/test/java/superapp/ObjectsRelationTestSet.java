package superapp;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import superapp.data.UserRole;
import superapp.logic.boundaries.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ObjectsRelationTestSet extends BaseTestSet {

    // GIVEN for all tests in this test set
    // 1. the server is up and running
    // 2. the database (local) is up and running


    @Test
    @DisplayName("Invalid url for get all children")
    public void test_1() {

        // GIVEN
        // 3. db contains an object with child object

        Location location = new Location(10.200, 10.200);

        String parentEmail = "demoParent@gmail.com";
        CreatedBy createdByParent = new CreatedBy()
                .setUserId(new UserID(this.springApplicationName, parentEmail));

        Map<String, Object> objectDetailsParent = new HashMap<>();
        objectDetailsParent.put("details", "String object demo parent");

        SuperAppObjectBoundary postedObjectParent = this.help_PostObjectBoundary(null,"Event","demo parent",
                null,true, location, createdByParent, objectDetailsParent);

        String childEmail = "demoChild@gmail.com";
        CreatedBy createdByChild = new CreatedBy()
                .setUserId(new UserID(this.springApplicationName, childEmail));

        Map<String, Object> objectDetailsChild = new HashMap<>();
        objectDetailsParent.put("details", "String object demo child");

        SuperAppObjectBoundary postedObjectChild = this.help_PostObjectBoundary(null,"Event","demo",
                null,true, location, createdByChild, objectDetailsChild);


        this.help_PutRelationBetweenObjects(this.springApplicationName
                                        , postedObjectParent.getObjectId().getInternalObjectId()
                                        , postedObjectChild.getObjectId());


        SuperAppObjectBoundary[] parents = this.help_GetRelationParents(this.springApplicationName
                , postedObjectChild.getObjectId().getInternalObjectId());

        assertThat(parents)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(postedObjectParent);

        SuperAppObjectBoundary[] children = this.help_GetRelationChildren(this.springApplicationName
                , postedObjectParent.getObjectId().getInternalObjectId());

        assertThat(children)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(postedObjectChild);
        // WHEN
        // A GET request is made to the path
        // "superapp/objects/2023b.LiorAriely/"here id of exist object that has children"/childrens"


        // THEN
        // The server response with status 404 not found AND not return any array of object boundary



    }

    @Test
    @DisplayName("invalid url for get parent")
    public void test_2() {

        // GIVEN
        // 3. db contains child object with parent object


        // WHEN
        // A GET request is made to the path
        // "superapp/objects/2023b.LiorAriely/"here id of exist object that has parent"/parent"

        // THEN
        // The server response with status 404 not found AND not return any array of object boundary


    }

    @Test
    @DisplayName("invalid for put  (bind) relation between child and parent")
    public void test_3() {

        // GIVEN
        // 3. db contains 2 objects without relation


        // WHEN
        // A PUT request is made to the path
        // "superapp/object/2023b.LiorAriely/here id of exist object/children"
        // AND pass SuperAppObjectIdBoundary
        // {
        //    "superapp":"2023b.LiorAriely",
        //    "internalObjectId":"123456789"
        // }

        // THEN
        // The server response with status 404 not found AND relation between objects not updated/saved.

    }

    @Test
    @DisplayName("bind child to parent")
    public void test_4() {

        // GIVEN
        // 3. db contains 2 objects without relation

        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/here id of exist object-target parent/children"
        // AND pass SuperAppObjectIdBoundary
        // {
        //    "superapp":"2023b.LiorAriely",
        //    "internalObjectId":"123456789"
        // }

        // THEN
        // The server response with status 2xx OK AND relation between objects updated and saved in db.


    }

    @Test
    @DisplayName("bind child that has parent to another parent")
    public void test_5() {

        // GIVEN
        // 3. db contains child object
        // "objectId": {
        //        "superapp": "2023b.LiorAriely",
        //        "internalObjectId": "c2759119-f06f-4f3c-a8ca-9db9b16301c1"
        // }
        // that has parent object
        // "objectId": {
        //        "superapp": "2023b.LiorAriely",
        //        "internalObjectId": "aaaaaa-bbbb-ccccc-1111-000000000000"
        // }
        // AND object without any relations
        // "objectId": {
        //        "superapp": "2023b.LiorAriely",
        //        "internalObjectId": "bbbbbb-bbbb-ccccc-1111-11111111111111"
        // }


        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/bbbbbb-bbbb-ccccc-1111-11111111111111/children"
        // AND pass SuperAppObjectIdBoundary
        // {
        //    "superapp":"2023b.LiorAriely",
        //    "internalObjectId":"c2759119-f06f-4f3c-a8ca-9db9b16301c1"
        // }

        // THEN
        // The server response with status 400 bad request AND relation between objects not updated/saved.

    }

    @Test
    @DisplayName("bind child to non existing parent")
    public void test_6() {

        // GIVEN
        // 3. db contain only one object
        // "objectId": {
        //        "superapp": "2023b.LiorAriely",
        //        "internalObjectId": "c2759119-f06f-4f3c-a8ca-9db9b16301c1"
        // }


        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/bbbbbbb-222222/children"
        // AND pass SuperAppObjectIdBoundary
        // {
        //    "superapp":"2023b.LiorAriely",
        //    "internalObjectId":"c2759119-f06f-4f3c-a8ca-9db9b16301c1"
        // }

        // THEN
        // The server response with status 404 not found AND relation between objects not updated/saved.


    }

    @Test
    @DisplayName("bind non existing child to parent")
    public void test_7() {

        // GIVEN
        // 3. db contain only one object
        // "objectId": {
        //        "superapp": "2023b.LiorAriely",
        //        "internalObjectId": "c2759119-f06f-4f3c-a8ca-9db9b16301c1"
        // }


        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/c2759119-f06f-4f3c-a8ca-9db9b16301c1/children"
        // AND
        // pass SuperAppObjectIdBoundary
        // {
        //    "superapp":"2023b.LiorAriely",
        //    "internalObjectId":"123456789"
        // }


        // THEN
        // "The server response with status 404 not found AND relation between objects not updated/saved."

    }

    @Test
    @DisplayName("bind object to itself")
    public void test_8() {

        // GIVEN
        // 3. db contain only one object
        // "objectId": {
        //        "superapp": "2023b.LiorAriely",
        //        "internalObjectId": "c2759119-f06f-4f3c-a8ca-9db9b16301c1"
        // }


        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/c2759119-f06f-4f3c-a8ca-9db9b16301c1/children"
        // AND
        // pass SuperAppObjectIdBoundary
        // {
        //    "superapp":"2023b.LiorAriely",
        //    "internalObjectId":"c2759119-f06f-4f3c-a8ca-9db9b16301c1"
        // }


        // THEN
        // "The server response with status 409 conflict AND relation between objects not updated/saved."


    }

    @Test
    @DisplayName("bind object relations with SuperAppObjectIdBoundary null")
    public void test_9() {

        // GIVEN
        // 3. db contains 2 objects without relation


        // when
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/parent-target-id/children"
        // AND pass SuperAppObjectIdBoundary : null

        // THEN
        // The server response with status 400 bad request AND relation between objects not updated/saved.

    }

    @Test
    @DisplayName("bind object relations with invalid attributes of SuperAppObjectIdBoundary ")
    public void test_10() {

        // GIVEN
        // 3. db contains 2 objects without relation


        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/parent-target-id/children"
        // AND
        // pass SuperAppObjectIdBoundary
        // {
        //    "superapp": null,
        //    "internalObjectId":"1234567"
        // }

        // THEN
        // The server response with status 400 bad request AND relation between objects not updated/saved.

    }

    @Test
    @DisplayName("bind object relations with invalid attributes of SuperAppObjectIdBoundary ")
    public void test_11() {

        // GIVEN
        // 3. db contains 2 objects without relation


        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/parent-target-id/children"
        // AND
        // pass SuperAppObjectIdBoundary
        // {
        //    "superapp":"2023b-LiorAriely",
        //    "internalObjectId":"123456789"
        // }

        // THEN
        // The server response with status 400 bad request AND relation between objects not updated/saved.

    }

    @Test
    @DisplayName("bind object relations with invalid attributes of SuperAppObjectIdBoundary ")
    public void test_12() {

        // GIVEN
        // 3. db contains 2 objects without relation


        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/parent-target-id/children"
        // AND
        // pass SuperAppObjectIdBoundary
        // {
        //    "superapp":"2023bLiorAriely",
        //    "internalObjectId":""
        // }

        // THEN
        // The server response with status 400 bad request AND relation between objects not updated/saved.

    }

    @Test
    @DisplayName("bind object relations with invalid attributes of SuperAppObjectIdBoundary ")
    public void test_13() {

        // GIVEN
        // 3. db contains 2 objects without relation

        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/parent-target-id/children"
        // AND
        // pass SuperAppObjectIdBoundary
        // {
        //    "superapp":"2023b.LiorAriely",
        //    "internalObjectId":null
        // }


        // THEN
        // The server response with status 400 bad request AND relation between objects not updated/saved.

    }

    @Test
    @DisplayName("get all children of some parent ")
    public void test_14() {

        // GIVEN
        // 3. db contains parent object
        // "objectId": {
        //        "superapp": "2023b.LiorAriely",
        //        "internalObjectId": "c2759119-f06f-4f3c-a8ca-9db9b16301c1"
        // }
        // that has 2 children objects:
        // "objectId": {
        //        "superapp": "2023b.LiorAriely",
        //        "internalObjectId": "aaaaaa-bbbb-ccccc-1111-000000000000"
        // }.
        // "objectId": {
        //        "superapp": "2023b.LiorAriely",
        //        "internalObjectId": "bbbbbb-bbbb-ccccc-1111-222222222222"
        // }


        // WHEN
        // A GET request is made to the path
        // "superapp/objects/2023b.LiorAriely/c2759119-f06f-4f3c-a8ca-9db9b16301c1/children"


        // THEN
        // The server response with status 200 OK AND return array of children objects

    }

    @Test
    @DisplayName("get all children of parent that does not have  children")
    public void test_15() {

        // GIVEN
        // 3. db contain only one object
        // "objectId": {
        //        "superapp": "2023b.LiorAriely",
        //        "internalObjectId": "c2759119-f06f-4f3c-a8ca-9db9b16301c1"
        // }


        // WHEN
        // A GET request is made to the path
        // "superapp/objects/2023b.LiorAriely/c2759119-f06f-4f3c-a8ca-9db9b16301c1/children"


        // THEN
        // The server response with status 200 OK AND return array of children objects

    }

    @Test
    @DisplayName("get parent of child")
    public void test_16() {

        // GIVEN
        // 3. db contains parent object
        // "objectId": {
        //        "superapp": "2023b.LiorAriely",
        //        "internalObjectId": "c2759119-f06f-4f3c-a8ca-9db9b16301c1"
        // }
        // that has 2 children objects:
        // "objectId": {
        //        "superapp": "2023b.LiorAriely",
        //        "internalObjectId": "aaaaaa-bbbb-ccccc-1111-000000000000"
        // }.
        // "objectId": {
        //        "superapp": "2023b.LiorAriely",
        //        "internalObjectId": "bbbbbb-bbbb-ccccc-1111-222222222222"
        // }


        // WHEN
        // A GET request is made to the path
        // "superapp/objects/2023b.LiorAriely/aaaaaa-bbbb-ccccc-1111-000000000000/parents"

        // THEN
        // The server response with status 200 OK AND return array with one parent object

    }

    @Test
    @DisplayName("get parent that does not have children")
    public void test_17() {

        // GIVEN
        // 3. db contain an object
        // "objectId": { "superapp": "2023b.LiorAriely",
        //               "internalObjectId": "178" }

        // WHEN
        // A GET request is made to the path
        // "superapp/objects/2023b.LiorAriely/178/parents"


        // THEN
        // The server response with status 200 OK and return empty array

    }

}
