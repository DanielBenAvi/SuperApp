package superapp.miniapps;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import superapp.BaseTestSet;
import superapp.OpenStreetMapUtils;
import superapp.data.UserRole;
import superapp.logic.boundaries.*;
import superapp.miniapps.datingMiniApp.Gender;
import superapp.miniapps.datingMiniApp.objects.Address;
import superapp.miniapps.datingMiniApp.objects.PrivateDatingProfile;
import superapp.miniapps.datingMiniApp.objects.PublicDatingProfile;

import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DatingTests extends BaseTestSet {

    // GIVEN for all tests in this test set
    // 1. the server is up and running
    // 2. the database (local) is up and running


    private SuperAppObjectBoundary createObjectAndPostHelper(String type, String email, String key, Object obj) {

        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put(key, obj);

        return  this.help_PostObjectBoundary(null
                , type
                , type
                ,null
                ,true
                , new Location(10.200, 10.200)
                , new CreatedBy().setUserId(new UserId(this.springApplicationName, email))
                , objectDetails
        );

    }
    @Test
    @DisplayName("SuccessFull Dating")
    public void test() {

        // post 2 users A, B

        // A like B
        // get likes of A, B
        // get matches A, B

        // B like A
        // get likes A, B
        // get matches A, B


        /////////////////////////////////
        // Create and post users
        this.help_PostUserBoundary("A@gmail.com", UserRole.MINIAPP_USER.toString(),"B", "B");
        this.help_PostUserBoundary("B@gmail.com", UserRole.MINIAPP_USER.toString(),"A", "A");

        // need to related profile to user?/
        // post 2 dating profile for each user
        PublicDatingProfile publicA = new PublicDatingProfile()
                .setAge(18)
                .setBio("bio A")
                .setGender(Gender.FEMALE)
                .setPictures(new ArrayList<>(Arrays.asList( "pic A 1", "pic 2" )))
                .setNickName("nick A")
                .setSexOrientation(new ArrayList<>(Arrays.asList( Gender.FEMALE, Gender.OTHER)));

        PrivateDatingProfile profileA = new PrivateDatingProfile()
                .setPublicProfile(publicA)
                .setAddress(new Address().setCity("jeru").setState("isr").setHomeNum(2).setStreet("gaza").setZipCode("12"))
                .setAgeRange(12)
                .setDistanceRange(100)
                .setGenderPreferences(new ArrayList<>(Arrays.asList( Gender.FEMALE, Gender.OTHER)));

        PublicDatingProfile publicB = new PublicDatingProfile()
                .setAge(18)
                .setBio("bio B")
                .setGender(Gender.FEMALE)
                .setPictures(new ArrayList<>(Arrays.asList( "pic B 1", "pic 2" )))
                .setNickName("nick B")
                .setSexOrientation(new ArrayList<>(Arrays.asList( Gender.FEMALE, Gender.OTHER)));

        PrivateDatingProfile profileB = new PrivateDatingProfile()
                .setPublicProfile(publicB)
                .setAddress(new Address().setCity("jeru").setState("isr").setHomeNum(2).setStreet("gaza").setZipCode("12"))
                .setAgeRange(12)
                .setDistanceRange(100)
                .setGenderPreferences(new ArrayList<>(Arrays.asList( Gender.FEMALE, Gender.OTHER)));

        // create and post object boundary with dating profiles
        SuperAppObjectBoundary postedObjA
                = createObjectAndPostHelper("PrivateDatingProfile", "A@gmail.com", "key", profileA);

        String objectIdA = this.springApplicationName + "_" + postedObjA.getObjectId().getInternalObjectId();

        SuperAppObjectBoundary postedObjB
                = createObjectAndPostHelper("PrivateDatingProfile", "B@gmail.com", "key", profileB);
        String objectIdB = this.springApplicationName + "_" + postedObjB.getObjectId().getInternalObjectId();



        // create command
        InvokedBy invokedByA = new InvokedBy()
                                    .setUserId(new UserId(this.springApplicationName, "A@gmail.com"));
        TargetObject targetObjectA = new TargetObject()
                                                    .setObjectId(postedObjB.getObjectId());

        Map<String, Object> commandAttributeA = new HashMap<>();
        commandAttributeA.put("myDatingProfileId", objectIdA);

        // post  command A like B
        Object commandALikeBResult = this.help_PostCommandBoundary(
                MiniAppNames.DATING
                , new CommandId()
                , "LIKE" // 1 is LIKE_PROFILE command
                , targetObjectA
                ,null
                , invokedByA
                , commandAttributeA);

        System.err.println("Result A Like B : " + commandALikeBResult.toString());


        // create command B like A

        UserId userIdB = new UserId(this.springApplicationName, "B@gmail.com");
        InvokedBy invokedByB = new InvokedBy()
                .setUserId(userIdB);
        TargetObject targetObjectB = new TargetObject()
                .setObjectId(postedObjA.getObjectId());

        Map<String, Object> commandAttributeB = new HashMap<>();
        commandAttributeB.put("myDatingProfileId", objectIdB);
        // post  command B like A
        Object commandBLikeAResult = this.help_PostCommandBoundary(
                MiniAppNames.DATING
                , new CommandId()
                , "LIKE" // 1 is LIKE_PROFILE command
                , targetObjectB
                ,null
                , invokedByB
                , commandAttributeB);

        System.err.println("Result B Like A : " + commandBLikeAResult.toString());


    }


    @Test
    public void getLatitudeLongitudeByAddress() {
        Map<String, Double> coords;
        String address = "The White House, Washington DC";


        coords = OpenStreetMapUtils.getInstance().getCoordinates(address);

        System.out.println("latitude :" + coords.get("lat"));
        System.out.println("longitude:" + coords.get("lon"));

    }
}