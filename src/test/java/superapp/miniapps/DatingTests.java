//package superapp.miniapps;
//
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import superapp.BaseTestSet;
//import superapp.data.SuperAppObjectEntity;
//import superapp.data.UserDetails;
//import superapp.data.UserRole;
//import superapp.logic.boundaries.*;
//import superapp.miniapps.datingMiniApp.PrivateDatingProfile;
//import superapp.miniapps.datingMiniApp.PublicDatingProfile;
//
//import java.util.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class DatingTests extends BaseTestSet {
//
//    // GIVEN for all tests in this test set
//    // 1. the server is up and running
//    // 2. the database (local) is up and running
//
//
//    private SuperAppObjectBoundary createObjectAndPostHelper(String type, String email, String key, Object obj) {
//
//        Map<String, Object> objectDetails = new HashMap<>();
//        objectDetails.put(key, obj);
//
//        return  this.help_PostObjectBoundary(null
//                , type
//                , type
//                ,null
//                ,true
//                , new Location(10.200, 10.200)
//                , new CreatedBy().setUserId(new UserId(this.springApplicationName, email))
//                , objectDetails
//        );
//
//    }
//    @Test
//    @DisplayName("SuccessFull Dating")
//    public void test() {
//
//        // post 2 users A, B
//
//        // A like B
//        // get likes of A, B
//        // get matches A, B
//
//        // B like A
//        // get likes A, B
//        // get matches A, B
//
//
//        /////////////////////////////////
//        // Create and post users
//        UserBoundary userA = this.help_PostUserBoundary("A@gmail.com", UserRole.SUPERAPP_USER.toString(),"A", "A");
//        UserBoundary userB = this.help_PostUserBoundary("B@gmail.com", UserRole.SUPERAPP_USER.toString(),"B", "B");
//
//        // need to related profile to user?/
//        // post 2 dating profile for each user
//        PublicDatingProfile publicA = new PublicDatingProfile()
//                .setAge(18)
//                .setBio("bio A")
//                .setGender(Gender.FEMALE)
//                .setPictures(new ArrayList<>(Arrays.asList( "pic A 1", "pic 2" )))
//                .setNickName("nick A")
//                .setSexOrientation(new ArrayList<>(Arrays.asList( Gender.FEMALE, Gender.OTHER)));
//
//        PrivateDatingProfile profileA = new PrivateDatingProfile()
//                .setPublicProfile(publicA)
//
//                .setDistanceRange(100)
//                .setGenderPreferences(new ArrayList<>(Arrays.asList( Gender.FEMALE, Gender.OTHER)));
//
//        PublicDatingProfile publicB = new PublicDatingProfile()
//                .setAge(18)
//                .setBio("bio B")
//                .setGender(Gender.FEMALE)
//                .setPictures(new ArrayList<>(Arrays.asList( "pic B 1", "pic 2" )))
//                .setNickName("nick B")
//                .setSexOrientation(new ArrayList<>(Arrays.asList( Gender.FEMALE, Gender.OTHER)));
//
//        PrivateDatingProfile profileB = new PrivateDatingProfile()
//                .setPublicProfile(publicB)
//                .setDistanceRange(100)
//                .setGenderPreferences(new ArrayList<>(Arrays.asList( Gender.FEMALE, Gender.OTHER)));
//
//        // create and post object boundary with dating profiles
//        SuperAppObjectBoundary postedObjA
//                = createObjectAndPostHelper("PrivateDatingProfile", "A@gmail.com", "key", profileA);
//
//        String objectIdA = this.springApplicationName + "_" + postedObjA.getObjectId().getInternalObjectId();
//
//        SuperAppObjectBoundary postedObjB
//                = createObjectAndPostHelper("PrivateDatingProfile", "B@gmail.com", "key", profileB);
//        String objectIdB = this.springApplicationName + "_" + postedObjB.getObjectId().getInternalObjectId();
//
//
//        // change role
//        help_PutUserBoundary(new UserBoundary().setRole(UserRole.MINIAPP_USER.toString()), userA.getUserId().getEmail());
//        help_PutUserBoundary(new UserBoundary().setRole(UserRole.MINIAPP_USER.toString()), userB.getUserId().getEmail());
//
//        // create command
//        InvokedBy invokedByA = new InvokedBy()
//                                    .setUserId(new UserId(this.springApplicationName, "A@gmail.com"));
//        TargetObject targetObjectA = new TargetObject()
//                                                    .setObjectId(postedObjB.getObjectId());
//
//        Map<String, Object> commandAttributeA = new HashMap<>();
//        commandAttributeA.put("myDatingProfileId", objectIdA);
//
//        // post  command A like B
//        Object commandALikeBResult = this.help_PostCommandBoundary(
//                MiniAppNames.DATING
//                , new CommandId()
//                , "LIKE_PROFILE"
//                , targetObjectA
//                ,null
//                , invokedByA
//                , commandAttributeA);
//
//
//
//        // create command B like A
//
//        UserId userIdB = new UserId(this.springApplicationName, "B@gmail.com");
//        InvokedBy invokedByB = new InvokedBy()
//                .setUserId(userIdB);
//        TargetObject targetObjectB = new TargetObject()
//                .setObjectId(postedObjA.getObjectId());
//
//        Map<String, Object> commandAttributeB = new HashMap<>();
//        commandAttributeB.put("myDatingProfileId", objectIdB);
//        // post  command B like A
//        this.help_PostCommandBoundary(
//                MiniAppNames.DATING
//                , new CommandId()
//                , "LIKE_PROFILE"
//                , targetObjectB
//                ,null
//                , invokedByB
//                , commandAttributeB);
//
//        int y = 9;
//
//    }
//
//    @Test
//    @DisplayName("SuccessFull get user det")
//    public void test1() {
//        UserBoundary userA = this.help_PostUserBoundary("A@gmail.com", UserRole.SUPERAPP_USER.toString(),"A", "A");
//        UserBoundary userB = this.help_PostUserBoundary("B@gmail.com", UserRole.SUPERAPP_USER.toString(),"B", "B");
//
//        UserBoundary userC = this.help_PostUserBoundary("C@gmail.com", UserRole.MINIAPP_USER.toString(),"C", "C");
//
//        UserDetails userDetailsA = new UserDetails().setName("IDO").setPhoneNum("052-5762230");
//        UserDetails userDetailsB = new UserDetails().setName("Yosef").setPhoneNum("052-5762230");
//
//        SuperAppObjectBoundary postedA = createObjectAndPostHelper("UserDetails", "A@gmail.com", "key1", userDetailsA);
//        SuperAppObjectBoundary postedB = createObjectAndPostHelper("UserDetails", "B@gmail.com", "key1", userDetailsB);
//
//
//        UserId userIdC = new UserId(this.springApplicationName, "C@gmail.com");
//
//        InvokedBy invokedByC = new InvokedBy()
//                .setUserId(userIdC);
//
//        TargetObject targetObject = new TargetObject()
//                .setObjectId(new ObjectId().setSuperapp(this.springApplicationName).setInternalObjectId("EMPTY_OBJECT_FOR_COMMAND_THAT_NO_TARGET"));
//
//        Map<String, Object> commandAttributeB = new HashMap<>();
//        commandAttributeB.put("createdBy", this.springApplicationName + "_" + "B@gmail.com");
//        commandAttributeB.put("type", "UserDetails");
//        // post  command B like A
//        this.help_PostCommandBoundary(
//                MiniAppNames.DATING
//                , new CommandId()
//                , "GET_USER_DETAILS_BY_EMAIL"
//                , targetObject
//                , null
//                , invokedByC
//                , commandAttributeB);
//
//        int x = 1;
//
//
//    }
//
//
//
//    @Test
//    @DisplayName("locationTEST")
//    public void locationTEST() {
//
//        // GIVEN
//        // 1. the server is up and running
//        // 2. the database is up and running
//
//        String email = "demo@gmail.com";
//        String role = UserRole.SUPERAPP_USER.toString();
//        String username = "demo_user";
//        String avatar = "demo_avatar";
//        help_PostUserBoundary(email, role, username, avatar);
//
//
//        // WHEN
//        // A POST request is made to the path "superapp/objects
//
//        Map<String, Object> objectDetails = new HashMap<>();
//        objectDetails.put("details", "String object demo");
//
//        SuperAppObjectBoundary postObject_1 =
//                help_PostObjectBoundary(null, "DATING", "cir", null,
//                        true, new Location(           34.78488849231121,
//                                32.06677768447233), new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" )), objectDetails );
//
//        SuperAppObjectBoundary postObject_5 =
//                help_PostObjectBoundary(null, "DATING", "cir", null,
//                        true, new Location(          34.76981572319056,
//                                32.08225893781888), new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" )), objectDetails );
//        SuperAppObjectBoundary postObject_6 =
//                help_PostObjectBoundary(null, "DATING", "cir", null,
//                        true, new Location(          34.80331076567859,
//                                32.08316193000917), new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" )), objectDetails );
//        SuperAppObjectBoundary postObject_7 =
//                help_PostObjectBoundary(null, "DATING", "cir", null,
//                        true, new Location(          34.784736242117106,
//                                32.095544922695225), new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" )), objectDetails );
//        SuperAppObjectBoundary postObject_8 =
//                help_PostObjectBoundary(null, "DATING", "demo1", null,
//                        true, new Location(          34.77727598265457,
//                                32.070519227564134), new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" )), objectDetails );
//        SuperAppObjectBoundary postObject_9 =
//                help_PostObjectBoundary(null, "DATING", "demo2", null,
//                        true, new Location(          34.79752525833942,
//                                32.06948709303613 ), new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" )), objectDetails );
//        SuperAppObjectBoundary postObject_10 =
//                help_PostObjectBoundary(null, "DATING", "demo3", null,
//                        true, new Location(          34.78595424366256,
//                                32.08316193000917 ), new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" )), objectDetails );
//
//        SuperAppObjectBoundary postObject_11 =
//                help_PostObjectBoundary(null, "DATING", "demo4", null,
//                        true, new Location(          34.796764007374776,
//                                32.09283628644175), new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" )), objectDetails );
//
//
//        SuperAppObjectBoundary postObject_12 =
//                help_PostObjectBoundary(null, "DATING", "demo5", null,
//                        true, new Location(          34.7757534807223,
//                                32.09257831689622), new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" )), objectDetails );
//        // THEN
//        // THEN
//
//
//        // THEN
//        // the server response with status 2xx code and return  SuperAppObjectBoundary as json
//        int y = 5;
//        System.err.println();
////        SuperAppObjectBoundary objectFromGet = help_GetObjectBoundary(
////                postObject.getObjectId().getInternalObjectId(),
////                postObject.getObjectId().getSuperapp(),springApplicationName,email);
////        objectFromGet.setActive(false);
////
////        assertThat(objectFromGet)
////                .isNotNull()
////                .usingRecursiveComparison()
////                .isEqualTo(postObject);
//    }
//
//}