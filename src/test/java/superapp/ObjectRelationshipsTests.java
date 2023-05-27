package superapp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import superapp.data.UserRole;
import superapp.logic.boundaries.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ObjectRelationshipsTests extends BaseTestSet {

    // GIVEN for all tests in this test set
    // 1. the server is up and running
    // 2. the database (local) is up and running

    /**
     * help method to create Object and do PUT
     *
     * @param email String
     * @param applicationName String
     * @param alias String
     * @return SuperAppObjectBoundary
     */
    private SuperAppObjectBoundary createObjectAndPostHelper(String email, String applicationName, String alias) {

        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details_1", "String object demo");

        return  this.help_PostObjectBoundary(null
                ,"Event"
                , alias
                ,null
                ,true
                , new Location(10.200, 10.200)
                , new CreatedBy().setUserId(new UserId(applicationName, email))
                , objectDetails
        );

    }

    /**
     * help method to check that relation not updated after exception
     * method do double-check: parent and children.
     *
     * @param internalObjectId String
     */
    private void assertAfterRelationNotShallBeUpdated(String internalObjectId, String userSuperapp, String userEmail,
                                                      int size, int page) {

        SuperAppObjectBoundary[] parentResult = this.getRelationParents(internalObjectId, userSuperapp, userEmail
        , size, page);
        assertThat(parentResult)
                .isNotNull()
                .isEmpty();

        SuperAppObjectBoundary[] childrenResult = this.getRelationChildren(internalObjectId, userSuperapp, userEmail
                , size, page);

        assertThat(childrenResult)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Invalid url for GET children")
    public void invalidGetChildrenUrlTest() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 3. db contains an object with child object

        // parent object
        SuperAppObjectBoundary postedObjectParent
                = createObjectAndPostHelper(email, this.springApplicationName,"demo parent");

        // child object
        SuperAppObjectBoundary postedObjectChild
                = createObjectAndPostHelper(email, this.springApplicationName,"demo child");

        // create relation
        this.putRelationBetweenObjects(postedObjectParent.getObjectId().getInternalObjectId()
                                        , postedObjectChild.getObjectId(),springApplicationName, email);

        // WHEN
        // A GET request is made to the path
        // "superapp/objects/{superapp}/{internalObjectId}/childrens
        // ?userSuperapp={superapp}&userEmail={email}&size={size}&page={page}"

        // THEN
        // The server response with status 404 not found AND not return any array of object boundary
        assertThatThrownBy(() ->
                this.restTemplate.getForObject(this.baseUrl + "/superapp/objects/{superapp}/{internalObjectId}/childrens" +
                                "?userSuperapp={superapp}&userEmail={email}&size={size}&page={page}"
                                , SuperAppObjectBoundary[].class
                                , this.springApplicationName
                                , postedObjectParent.getObjectId().getInternalObjectId()
                                , springApplicationName
                                , email, 10, 0
                ))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());

    }

    @Test
    @DisplayName("Invalid url for GET parent")
    public void invalidGetParentsUrlTest() {



        // GIVEN
        // 3. db contains child object with parent object

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // parent object
        SuperAppObjectBoundary postedObjectParent
                = createObjectAndPostHelper(email , this.springApplicationName, "demo parent");

        // child object
        SuperAppObjectBoundary postedObjectChild
                = createObjectAndPostHelper(email , this.springApplicationName, "demo child");

        // create relation
        this.putRelationBetweenObjects(postedObjectParent.getObjectId().getInternalObjectId()
                                        , postedObjectChild.getObjectId(), springApplicationName, email);

        // WHEN
        // A GET request is made to the path
        // "superapp/objects/{superapp}/{internalObjectId}/parent
        // ?userSuperapp={superapp}&userEmail={email}&size={size}&page={page}"

        // THEN
        // The server response with status 404 not found AND not return any array of object boundary

        assertThatThrownBy(() ->
                this.restTemplate.getForObject(this.baseUrl + "/superapp/objects/{superapp}/{internalObjectId}/parent" +
                                "?userSuperapp={superapp}&userEmail={email}&size={size}&page={page}"
                        , SuperAppObjectBoundary[].class
                        , this.springApplicationName
                        , postedObjectChild.getObjectId().getInternalObjectId()
                        , springApplicationName
                        , email, 10, 0
                ))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Invalid url for PUT (bind) relation between child and parent")
    public void invalidPutRelationUrlTest () {

        // GIVEN
        // 3. db contains 2 objects without relation

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // parent object
        SuperAppObjectBoundary postedObjectParent
                = createObjectAndPostHelper(email, this.springApplicationName,"demo parent");

        // child object
        SuperAppObjectBoundary postedObjectChild
                = createObjectAndPostHelper(email, this.springApplicationName,"demo child");

        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/{superapp}/{internalObjectId}/childrens
        //  ?userSuperapp={superapp}&userEmail={email}&size={size}&page={page}"

        // THEN
        // The server response with status 404 not found AND relation between objects not updated/saved.

        assertThatThrownBy(() ->
                        this.restTemplate.put(
                                this.baseUrl + "/superapp/object/{superapp}/{internalObjectId}/children" +
                                        "?userSuperapp={superapp}&userEmail={email}&size={size}&page={page}"
                                , postedObjectChild.getObjectId()
                                , this.springApplicationName
                                , postedObjectParent.getObjectId().getInternalObjectId()
                                , springApplicationName
                                , email, 10, 0)
                )
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());

        assertAfterRelationNotShallBeUpdated(postedObjectParent.getObjectId().getInternalObjectId(), springApplicationName,
                email , 10 , 0);
        assertAfterRelationNotShallBeUpdated(postedObjectChild.getObjectId().getInternalObjectId(), springApplicationName,
                email , 10 , 0);

    }

    @Test
    @DisplayName("Successfully bind relation between object child to parent")
    public void bindRelationSuccessfullyTest_1() {

        // GIVEN
        // 3. db contains 3 objects without relation

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // parent object
        SuperAppObjectBoundary postedObjectParent
                = createObjectAndPostHelper(email, this.springApplicationName, "demo parent");

        // child object
        SuperAppObjectBoundary postedObjectChild
                = createObjectAndPostHelper(email, this.springApplicationName, "demo child");

        // third object
        SuperAppObjectBoundary postedObject
                = createObjectAndPostHelper(email, this.springApplicationName, "demo");

        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/{superapp}/{internalObjectId}/childrens
        //  ?userSuperapp={superapp}&userEmail={email}&size={size}&page={page}"
        // AND pass ObjectId of postedObjectChild

        this.putRelationBetweenObjects(postedObjectParent.getObjectId().getInternalObjectId()
                                        , postedObjectChild.getObjectId(),springApplicationName, email);

        // THEN
        // The server response with status 2xx OK AND relation between 2 objects updated and saved in db.

        SuperAppObjectBoundary[] parentOfChild
                = this.getRelationParents(postedObjectChild.getObjectId().getInternalObjectId(),springApplicationName, email
        , 10 , 0);

        assertThat(parentOfChild)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(postedObjectParent);

        SuperAppObjectBoundary[] childOfParent
                = this.getRelationChildren(postedObjectParent.getObjectId().getInternalObjectId(), springApplicationName, email
        , 10, 0);

        assertThat(childOfParent)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(postedObjectChild);

        assertAfterRelationNotShallBeUpdated(postedObject.getObjectId().getInternalObjectId(), springApplicationName, email
        , 10 , 0);

    }

    @Test
    @DisplayName("Successfully bind relation between 2 objects to parent object")
    public void bindRelationSuccessfullyTest_2() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 3. db contains 3 objects without relation
        // parent object
        SuperAppObjectBoundary postedObjectParent
                = createObjectAndPostHelper(email, this.springApplicationName, "demo parent");

        // child 1 object
        SuperAppObjectBoundary postedObjectChild_1
                = createObjectAndPostHelper(email, this.springApplicationName, "demo child 1");


        // child 3 object
        SuperAppObjectBoundary postedObjectChild_2
                = createObjectAndPostHelper(email, this.springApplicationName, "demo child 2");

        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/postedObjectParent-internalObjectId/children"
        // AND pass ObjectIds of postedObjectChild_1 and postedObjectChild_2

        this.putRelationBetweenObjects(postedObjectParent.getObjectId().getInternalObjectId()
                , postedObjectChild_1.getObjectId(), springApplicationName, email);
        this.putRelationBetweenObjects(postedObjectParent.getObjectId().getInternalObjectId()
                , postedObjectChild_2.getObjectId(), springApplicationName, email);

        // THEN
        // The server response with status 2xx OK AND relation between 3 objects updated and saved in db.
        // the relation will be parented with 2 children.

        SuperAppObjectBoundary[] childrenOfParent
                = this.getRelationChildren(postedObjectParent.getObjectId().getInternalObjectId()
                , springApplicationName, email , 10 ,0);

        assertThat(childrenOfParent)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2);


        SuperAppObjectBoundary[] parentsOfChild_1
                = this.getRelationParents(postedObjectChild_1.getObjectId().getInternalObjectId()
                , springApplicationName, email , 10 ,0);

        assertThat(parentsOfChild_1)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(postedObjectParent);

        SuperAppObjectBoundary[] parentsOfChild_2
                = this.getRelationParents(postedObjectChild_2.getObjectId().getInternalObjectId()
                , springApplicationName, email , 10 ,0);

        assertThat(parentsOfChild_2)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(postedObjectParent);

    }

    @Test
    @DisplayName("Bind child that has parent to another parent")
    public void bindRelationToObjThatHasParentTest() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 3. db contains 3 objects: 1 parent, 1 child, 1 object without relation.

        // parent object
        SuperAppObjectBoundary postedObjectParent
                = createObjectAndPostHelper(email, this.springApplicationName,"demo parent");

        // child object
        SuperAppObjectBoundary postedObjectChild
                = createObjectAndPostHelper(email, this.springApplicationName,"demo child");

        // create relation
        this.putRelationBetweenObjects(postedObjectParent.getObjectId().getInternalObjectId()
                                        , postedObjectChild.getObjectId(), springApplicationName, email);

        // other parent object
        SuperAppObjectBoundary postedObjectOtherParent
                = createObjectAndPostHelper(email, this.springApplicationName,"demo other parent");

        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/postedObjectOtherParent-internalObjectId/children"
        // AND pass ObjectId of postedObjectChild

        // THEN
        // The server response with status 400 bad request AND relation between objects not updated/saved.

        assertThatThrownBy(() ->
                this.putRelationBetweenObjects(postedObjectOtherParent.getObjectId().getInternalObjectId()
                                                , postedObjectChild.getObjectId(), springApplicationName, email
                ))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertAfterRelationNotShallBeUpdated(postedObjectOtherParent.getObjectId().getInternalObjectId()
                , springApplicationName, email, 10 , 0);


    }

    @Test
    @DisplayName("Bind relation child to non existing object")
    public void bindRelationToNonExistObjectTest() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 3. db contain only one object

        SuperAppObjectBoundary postedObject
                = createObjectAndPostHelper(email, this.springApplicationName, "demo");

        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/c2759119-f06f-4f3c-a8ca-9db9b16301c1/children"
        // AND pass ObjectId of postedObject

        // THEN
        // The server response with status 404 not found AND relation between objects not updated/saved.
        assertThatThrownBy(() ->
                this.putRelationBetweenObjects("c2759119-f06f-4f3c-a8ca-9db9b16301c1"
                                            , postedObject.getObjectId(), springApplicationName, email
                ))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());

        assertAfterRelationNotShallBeUpdated(postedObject.getObjectId().getInternalObjectId(), springApplicationName, email
        ,10, 0);

    }

    @Test
    @DisplayName("Bind relation to non existing objectId")
    public void bindNonExistObjectIdTest() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 3. db contain only one object

        SuperAppObjectBoundary postedObject
                = createObjectAndPostHelper(email, this.springApplicationName, "demo");

        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/postedObject-internalObjectId/children"
        // AND pass ObjectId
        // {
        //    "superapp":"2023b.LiorAriely",
        //    "internalObjectId":"c2759119-f06f-4f3c-a8ca-9db9b16301c1"
        // }

        // THEN
        // "The server response with status 404 not found AND relation between objects not updated/saved."

        ObjectId objectId = new ObjectId()
                .setSuperapp(this.springApplicationName)
                .setInternalObjectId("c2759119-f06f-4f3c-a8ca-9db9b16301c1");

        assertThatThrownBy(() ->
                this.putRelationBetweenObjects(postedObject.getObjectId().getInternalObjectId()
                                                , objectId, springApplicationName, email))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());

        assertAfterRelationNotShallBeUpdated(postedObject.getObjectId().getInternalObjectId()
                ,springApplicationName, email, 10 , 0);

    }

    @Test
    @DisplayName("Bind object relation to itself")
    public void bindRelationToItselfTest() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 3. db contain only one object

        SuperAppObjectBoundary postedObject
                = createObjectAndPostHelper(email, this.springApplicationName, "demo");

        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/postedObject-internalObjectId/children"
        // AND pass ObjectId of postedObject

        // THEN
        // "The server response with status 409 conflict AND relation between objects not updated/saved."

        assertThatThrownBy(() ->
                this.putRelationBetweenObjects(postedObject.getObjectId().getInternalObjectId()
                                    , postedObject.getObjectId(), springApplicationName, email
                ))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.CONFLICT.value());

        assertAfterRelationNotShallBeUpdated(postedObject.getObjectId().getInternalObjectId()
                , springApplicationName, email , 10 , 0);

    }

    @Test
    @DisplayName("Bind object relations with ObjectIdBoundary(child) null")
    public void bindRelationWithObjectIdNullTest() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 3. db contains 2 objects without relation

        // parent object
        SuperAppObjectBoundary postedObjectParent
                = createObjectAndPostHelper(email, this.springApplicationName, "demo parent");

        // child object
        SuperAppObjectBoundary postedObjectChild
                = createObjectAndPostHelper(email, this.springApplicationName, "demo child");

        // when
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/postedObjectParent-internalObjectId/children"
        // AND pass ObjectIdBoundary : null

        // THEN
        // The server response with status 415 unsupported media type AND relation between objects not updated/saved.
        assertThatThrownBy(() ->
                this.putRelationBetweenObjects(postedObjectParent.getObjectId().getInternalObjectId()
                                                , null, springApplicationName, email)
                )
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());

        assertAfterRelationNotShallBeUpdated(postedObjectParent.getObjectId().getInternalObjectId()
                ,springApplicationName ,email ,10 , 0);
        assertAfterRelationNotShallBeUpdated(postedObjectChild.getObjectId().getInternalObjectId(), springApplicationName
        , email ,10 ,0);
    }

    @Test
    @DisplayName("Bind object relation with superappApplicationName null")
    public void bindRelationWithSuperappApplicationNameNullTest() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 3. db contains 2 objects without relation

        // parent object
        SuperAppObjectBoundary postedObjectParent
                = createObjectAndPostHelper(email, this.springApplicationName, "demo parent");

        // child object
        SuperAppObjectBoundary postedObjectChild
                = createObjectAndPostHelper(email, this.springApplicationName, "demo child");

        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/postedObjectParent-internalObjectId/children"
        // AND pass ObjectId with invalid "superapp":null

        // THEN
        // The server response with status 404 bad request AND relation between objects not updated/saved.

        String childInternalObjectId = postedObjectChild.getObjectId().getInternalObjectId();
        assertThatThrownBy(() ->
                this.putRelationBetweenObjects(postedObjectParent.getObjectId().getInternalObjectId()
                                    , new ObjectId().setInternalObjectId(childInternalObjectId)
                        , springApplicationName , email
                ))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());

        assertAfterRelationNotShallBeUpdated(postedObjectParent.getObjectId().getInternalObjectId()
                , springApplicationName , email , 10 ,0);
        assertAfterRelationNotShallBeUpdated(postedObjectChild.getObjectId().getInternalObjectId()
                , springApplicationName , email , 10 ,0);
    }

    @Test
    @DisplayName("Bind object relations with invalid attributes of superappApplicationName ")
    public void bindRelationWithInvalidSuperappApplicationNameTest() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 3. db contains 2 objects without relation

        // parent object
        SuperAppObjectBoundary postedObjectParent
                = createObjectAndPostHelper(email, this.springApplicationName, "demo parent");

        // child object
        SuperAppObjectBoundary postedObjectChild
                = createObjectAndPostHelper(email, this.springApplicationName, "demo child");

        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/postedObjectParent-internalObjectId/children"
        // AND pass ObjectId with invalid "superapp":"2023b-LiorAriely"

        // THEN
        // The server response with status 404 bad request AND relation between objects not updated/saved.
        String childInternalObjectId = postedObjectChild.getObjectId().getInternalObjectId();
        assertThatThrownBy(() ->
                this.putRelationBetweenObjects(postedObjectParent.getObjectId().getInternalObjectId()
                                    , new ObjectId().setSuperapp("2023b-LiorAriely").setInternalObjectId(childInternalObjectId)
                        ,springApplicationName , email
                ))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());

        assertAfterRelationNotShallBeUpdated(postedObjectParent.getObjectId().getInternalObjectId()
                , springApplicationName , email , 10 ,0);
        assertAfterRelationNotShallBeUpdated(postedObjectChild.getObjectId().getInternalObjectId()
                , springApplicationName , email , 10 ,0);
    }

    @Test
    @DisplayName("Bind object relations with invalid internalObjectId in ObjectId")
    public void bindRelationWithInvalidInternalObjectIdTest() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 3. db contains 2 objects without relation

        // parent object
        SuperAppObjectBoundary postedObjectParent
                = createObjectAndPostHelper(email, this.springApplicationName,"demo parent");

        // child object
        SuperAppObjectBoundary postedObjectChild
                = createObjectAndPostHelper(email, this.springApplicationName,"demo child");

        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/postedObjectParent-internalObjectId/children"
        // AND pass ObjectId with invalid "internalObjectId":"0"

        // THEN
        // The server response with status 404 bad request AND relation between objects not updated/saved.
        assertThatThrownBy(() ->
                this.putRelationBetweenObjects(postedObjectParent.getObjectId().getInternalObjectId()
                                        , new ObjectId().setSuperapp(this.springApplicationName).setInternalObjectId("0")
                        , springApplicationName , email
                ))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());

        assertAfterRelationNotShallBeUpdated(postedObjectParent.getObjectId().getInternalObjectId()
                , springApplicationName , email , 10 ,0);
        assertAfterRelationNotShallBeUpdated(postedObjectChild.getObjectId().getInternalObjectId()
                , springApplicationName , email , 10 ,0);
    }

    @Test
    @DisplayName("Bind object relations with null internalObjectId in ObjectId")
    public void bindRelationWithInternalObjectIdNullTest() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 3. db contains 2 objects without relation

        // parent object
        SuperAppObjectBoundary postedObjectParent
                = createObjectAndPostHelper(email, this.springApplicationName,"demo parent");

        // child object
        SuperAppObjectBoundary postedObjectChild
                = createObjectAndPostHelper(email, this.springApplicationName,"demo child");

        // WHEN
        // A PUT request is made to the path
        // "superapp/objects/2023b.LiorAriely/postedObjectParent-internalObjectId/children"
        // AND pass ObjectId with "internalObjectId":null

        // THEN
        // The server response with status 404 bad request AND relation between objects not updated/saved.

        assertThatThrownBy(() ->
                this.putRelationBetweenObjects(postedObjectParent.getObjectId().getInternalObjectId()
                                        , new ObjectId().setSuperapp(this.springApplicationName)
                        , springApplicationName , email
                ))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());

        assertAfterRelationNotShallBeUpdated(postedObjectParent.getObjectId().getInternalObjectId()
                , springApplicationName , email , 10 ,0);
        assertAfterRelationNotShallBeUpdated(postedObjectChild.getObjectId().getInternalObjectId()
                , springApplicationName , email , 10 ,0);
    }

    @Test
    @DisplayName("Get all children of parent that has children")
    public void getChildrenOfParentTest() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 3. db contains 4 objects: 1 parent object, 2 children objects, 1 object without relation

        // parent object
        SuperAppObjectBoundary postedObjectParent
                = createObjectAndPostHelper(email, this.springApplicationName,"demo parent");

        // child 1 object
        SuperAppObjectBoundary postedObjectChild_1
                = createObjectAndPostHelper(email, this.springApplicationName,"demo child 1");

        // child 2 object
        SuperAppObjectBoundary postedObjectChild_2
                = createObjectAndPostHelper(email, this.springApplicationName,"demo child 2");

        // create relation 1
        this.putRelationBetweenObjects(postedObjectParent.getObjectId().getInternalObjectId()
                                        , postedObjectChild_1.getObjectId() , springApplicationName , email);
        // create relation 2
        this.putRelationBetweenObjects(postedObjectParent.getObjectId().getInternalObjectId()
                                        , postedObjectChild_2.getObjectId() , springApplicationName , email);

        //  object without relation
        SuperAppObjectBoundary noRelationObject
                = createObjectAndPostHelper(email, this.springApplicationName,"demo");


        // WHEN
        // A GET request is made to the path
        // "superapp/objects/2023b.LiorAriely/postedObjectParent--internalObjectId/children"

        SuperAppObjectBoundary[] children
                = this.getRelationChildren(postedObjectParent.getObjectId().getInternalObjectId()
        , springApplicationName , email , 10 , 0);

        // THEN
        // The server response with status 2xx OK AND return array of children objects

        assertThat(children)
                .isNotNull()
                .hasSize(2)
                .doesNotContain(noRelationObject);

    }

    @Test
    @DisplayName("Get all children of parent that does not have children")
    public void getChildrenOfParentWithoutChildrenTest() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 3. db contain only one object

        // parent object
        SuperAppObjectBoundary postedObjectParent
                = createObjectAndPostHelper(email, this.springApplicationName,"demo parent");

        // WHEN
        // A GET request is made to the path
        // "superapp/objects/2023b.LiorAriely/postedObjectParent-internalObjectId/children"
        SuperAppObjectBoundary[] children
                = this.getRelationChildren(postedObjectParent.getObjectId().getInternalObjectId()
                , springApplicationName , email , 10 ,0);

        // THEN
        // The server response with status 200 OK AND return array of children objects
        assertThat(children)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Get parent of child")
    public void getParentOfChildTest() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 3. db contains 4 objects: 1 parent object, 2 children objects, 1 object without relation

        // parent object
        SuperAppObjectBoundary postedObjectParent
                = createObjectAndPostHelper(email, this.springApplicationName,"demo parent");

        // child 1 object
        SuperAppObjectBoundary postedObjectChild_1
                = createObjectAndPostHelper(email, this.springApplicationName,"demo child 1");

        // child 2 object
        SuperAppObjectBoundary postedObjectChild_2
                = createObjectAndPostHelper(email, this.springApplicationName,"demo child 2");

        // create relation
        this.putRelationBetweenObjects(postedObjectParent.getObjectId().getInternalObjectId()
                                        , postedObjectChild_1.getObjectId()
        ,springApplicationName, email );

        this.putRelationBetweenObjects(postedObjectParent.getObjectId().getInternalObjectId()
                                        , postedObjectChild_2.getObjectId()
                ,springApplicationName, email);

        //  object without relation
        SuperAppObjectBoundary postedObject
                = createObjectAndPostHelper(email, this.springApplicationName, "demo");

        // WHEN
        // A GET request is made to the path
        // "superapp/objects/2023b.LiorAriely/postedObjectChild_1--internalObjectId/parents"

        SuperAppObjectBoundary[] parentOfChild1
                = this.getRelationParents(postedObjectChild_1.getObjectId().getInternalObjectId()
                ,springApplicationName, email, 10, 0);

        // WHEN
        // A GET request is made to the path
        // "superapp/objects/2023b.LiorAriely/postedObjectChild_2--internalObjectId/parents"
        SuperAppObjectBoundary[] parentOfChild2
                = this.getRelationParents(postedObjectChild_2.getObjectId().getInternalObjectId()
                ,springApplicationName, email, 10, 0);

        // THEN
        // The server response with status 2xx OK AND return array with one parent object
        assertThat(parentOfChild1)
                .isNotNull()
                .hasSize(1);

        assertThat(parentOfChild2)
                .isNotNull()
                .hasSize(1);

        assertThat(parentOfChild1)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(parentOfChild2);

        this.assertAfterRelationNotShallBeUpdated(postedObject.getObjectId().getInternalObjectId()
                ,springApplicationName, email, 10, 0);

    }

    @Test
    @DisplayName("Get parent object of object, object does not have parent")
    public void getParentWithoutChildrenTest() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 3. db contain only one  objects

        // child object
        SuperAppObjectBoundary postedObject
                = createObjectAndPostHelper(email, this.springApplicationName, "demo");

        // WHEN
        // A GET request is made to the path
        // "superapp/objects/2023b.LiorAriely/postedObject-internalObjectId/parents"
        SuperAppObjectBoundary[] parents = this.getRelationParents(postedObject.getObjectId().getInternalObjectId()
                ,springApplicationName, email, 10, 0);

        // THEN
        // The server response with status 2xx OK and return empty array
        assertThat(parents)
                .isNotNull()
                .isEmpty();

    }

}
