package superapp.miniapps;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import superapp.BaseTestSet;
import superapp.data.UserRole;
import superapp.logic.boundaries.*;
import superapp.miniapps.datingMiniApp.Gender;
import superapp.miniapps.datingMiniApp.objects.Address;
import superapp.miniapps.datingMiniApp.objects.PrivateDatingProfile;
import superapp.miniapps.datingMiniApp.objects.PublicDatingProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
                , new CreatedBy().setUserId(new UserID(this.springApplicationName, email))
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

        // create object boundary with dating profiles
        SuperAppObjectBoundary postedObjA
                = createObjectAndPostHelper("PrivateDatingProfile", "A@gmail.com", "key", profileA);
        SuperAppObjectBoundary postedObjB
                = createObjectAndPostHelper("PrivateDatingProfile", "B@gmail.com", "key", profileB);



        // create command A like B
        MiniAppCommandBoundary likeBbyA = new MiniAppCommandBoundary()
                .setCommand("1") // 1 is LIKE_PROFILE command
                .setCommandId(new CommandId().setMiniapp("DATING"))
                .setInvokedBy(new InvokedBy().setUserId(new UserID(this.springApplicationName, "A@gmail.com")))
                .setTargetObject(
                        new TargetObject()
                                .setObjectId(new ObjectId().setInternalObjectId(postedObjB.getObjectId().getInternalObjectId())
                                        .setSuperapp(this.springApplicationName)))
                .setCommandAttributes(new HashMap<>());



        MiniAppCommandBoundary likeAbyB = new MiniAppCommandBoundary()
                .setCommand("1") // 1 is LIKE_PROFILE command
                .setCommandId(new CommandId().setMiniapp("DATING"))
                .setInvokedBy(new InvokedBy().setUserId(new UserID(this.springApplicationName, "B@gmail.com")))
                .setTargetObject(
                        new TargetObject()
                                .setObjectId(new ObjectId().setInternalObjectId(postedObjA.getObjectId().getInternalObjectId())
                                        .setSuperapp(this.springApplicationName)))
                .setCommandAttributes(new HashMap<>());



    }
}