package superapp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import superapp.data.UserRole;
import superapp.logic.boundaries.*;
import superapp.logic.utils.convertors.ConvertIdsHelper;
import superapp.miniapps.Gender;
import superapp.miniapps.MiniAppNames;
import superapp.miniapps.command.MiniAppsCommand;
import superapp.miniapps.datingMiniApp.PublicDatingProfile;

import java.util.*;
import java.util.stream.IntStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestSetDating extends BaseTestSet {



    private Map<String, Object> createPrivateProfileAsMap(PublicDatingProfile publicProfile,
                                                     Date bod, int distanceRang,
                                                     List<Gender> genderPreferences,
                                                     String phoneNum, int minAge, int maxAge,
                                                     List<String> likes,
                                                     List<String> matches) {

        Map<String, Object> privateDatingProfile = new HashMap<>();
        privateDatingProfile.put("publicProfile", publicProfile);
        privateDatingProfile.put("dateOfBirthday", bod);
        privateDatingProfile.put("distanceRange", distanceRang);
        privateDatingProfile.put("genderPreferences", genderPreferences);
        privateDatingProfile.put("phoneNumber", phoneNum);
        privateDatingProfile.put("minAge", minAge);
        privateDatingProfile.put("maxAge", maxAge);
        privateDatingProfile.put("likes", likes == null ? new ArrayList<String>() : likes);
        privateDatingProfile.put("matches", matches == null ? new ArrayList<String>() : matches);

        return privateDatingProfile;
    }


    private PublicDatingProfile createPublicProfile(String nickName, int age,
                                                    Gender gender, String bio,
                                                    List<String> pictures) {

        return new PublicDatingProfile()
                .setNickName(nickName)
                .setAge(age)
                .setGender(gender)
                .setBio(bio)
                .setPictures(pictures == null ? new ArrayList<>() : pictures);
    }


    @Test
    @DisplayName("Successfully get all private dating profiles that user already liked")
    public void testGetMyLikes(){

        // create user
        String email = "charming@gmail.com";
        this.help_PostUserBoundary(email, UserRole.SUPERAPP_USER.name(), "username", "avatar");


        String type = "PRIVATE_DATING_PROFILE";

        // post 5 dating profile objects : active is true
        List<String> idsOfDatingProfileWithActiveTrue = IntStream
                            .range(0, 5)
                            .mapToObj(privateProfileId -> {
                                    PublicDatingProfile publicProfile =
                                            createPublicProfile("true", 20, Gender.FEMALE, "mybio ", null);
                                    Map<String, Object> datingProfile =
                                            createPrivateProfileAsMap(publicProfile, new Date(), 10, new ArrayList<>(),
                                                    "052-8976625", 18, 99, null, null);

                                    SuperAppObjectBoundary boundary = this.help_PostObjectBoundary(null, type, "alias",
                                            null, true, null,
                                            new CreatedBy().setUserId(new UserId(this.springApplicationName, email)), datingProfile);

                                    // return objectId
                                    return ConvertIdsHelper
                                                        .concatenateIds(new String[] {boundary.getObjectId().getSuperapp(),
                                                                                boundary.getObjectId().getInternalObjectId()});
                            })
                            .toList();

        // post 5 dating profile objects : active is false

        List<String> idsOfDatingProfileWithActiveFalse = IntStream
                .range(0, 5)
                .mapToObj(privateProfileId -> {
                    PublicDatingProfile publicProfile =
                            createPublicProfile("false", 20, Gender.MALE, "mybio ", null);
                    Map<String, Object> datingProfile =
                            createPrivateProfileAsMap(publicProfile, new Date(), 10, new ArrayList<>(),
                                    "052-8976625", 18, 99, null, null);

                    SuperAppObjectBoundary boundary = this.help_PostObjectBoundary(null, type, "alias",
                            null, false, null,
                            new CreatedBy().setUserId(new UserId(this.springApplicationName, email)), datingProfile);

                    return ConvertIdsHelper
                            .concatenateIds(new String[] {boundary.getObjectId().getSuperapp(),
                                    boundary.getObjectId().getInternalObjectId()});
                })
                .toList();

        List<String> likes = new ArrayList<>();
        likes.addAll(idsOfDatingProfileWithActiveFalse);
        likes.addAll(idsOfDatingProfileWithActiveTrue);

        // post dating profile objects with active is true
        // this dating profile is a profile that likes other profile
        PublicDatingProfile profile = createPublicProfile("i liked everyone", 20, Gender.MALE, "bio ", null);
        Map<String, Object> privateProfile = createPrivateProfileAsMap(profile, new Date(), 10, new ArrayList<>(),
                "052-8976625", 18, 99, likes, null);

        SuperAppObjectBoundary objectBoundary = this.help_PostObjectBoundary(null, type, "alias",
                null, true, null,
                new CreatedBy().setUserId(new UserId(this.springApplicationName, email)), privateProfile);



        this.help_PutUserBoundary(new UserBoundary().setRole(UserRole.MINIAPP_USER.name()), email);


        Map<String, Object> comandAttr = new HashMap<>();
        comandAttr.put("page", 0);
        comandAttr.put("size", 100);
        Object result = this.help_PostCommandBoundary(MiniAppNames.DATING.name(),
                null,
                MiniAppsCommand.commands.GET_LIKES.name(),
                new TargetObject()
                        .setObjectId(new ObjectId(this.springApplicationName, objectBoundary.getObjectId().getInternalObjectId())),
                null,
                new InvokedBy().setUserId(new UserId(this.springApplicationName, email)),
                comandAttr);
        // TODO add assert
        int x = 1;
    }
}
