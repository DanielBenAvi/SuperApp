package superapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import superapp.data.UserRole;
import superapp.logic.boundaries.*;
import superapp.logic.utils.UtilHelper;
import superapp.logic.utils.convertors.ConvertIdsHelper;
import superapp.miniapps.Gender;
import superapp.miniapps.MiniAppNames;
import superapp.miniapps.command.MiniAppsCommand;
import superapp.miniapps.datingMiniApp.Match;
import superapp.miniapps.datingMiniApp.PrivateDatingProfile;
import superapp.miniapps.datingMiniApp.PublicDatingProfile;

import java.io.File;
import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    @DisplayName("Successfully get all private dating profiles that user liked")
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


        Map<String, Object> commandAttr = new HashMap<>();
        commandAttr.put("page", 0);
        commandAttr.put("size", 100);
        Object result = this.help_PostCommandBoundary(MiniAppNames.DATING.name(),
                null,
                MiniAppsCommand.commands.GET_LIKES.name(),
                new TargetObject()
                        .setObjectId(new ObjectId(this.springApplicationName, objectBoundary.getObjectId().getInternalObjectId())),
                null,
                new InvokedBy().setUserId(new UserId(this.springApplicationName, email)),
                commandAttr);


        List<SuperAppObjectBoundary> objectBoundaries = this.objectToListOfObjectBoundaries(result);

        assertEquals(5, objectBoundaries.size());

    }


    @Test
    @DisplayName("Successfully get all private dating profiles that user has match with")
    public void testGetMyMatches(){

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


        List<String> likes = new ArrayList<>();
        likes.addAll(idsOfDatingProfileWithActiveTrue);

        // post dating profile objects with active is true
        // this dating profile is a profile that likes other profile
        PublicDatingProfile profile = createPublicProfile("i liked everyone", 20, Gender.MALE, "bio ", null);
        Map<String, Object> privateProfile = createPrivateProfileAsMap(profile, new Date(), 10, new ArrayList<>(),
                "052-8976625", 18, 99, likes, null);

        SuperAppObjectBoundary objectBoundary = this.help_PostObjectBoundary(null, type, "alias",
                null, true, null,
                new CreatedBy().setUserId(new UserId(this.springApplicationName, email)), privateProfile);


        // post 5 matches objects

        List<String> idsOfMatches = IntStream
                .range(0, 5)
                .mapToObj( i -> {

                    Match match = new Match(idsOfDatingProfileWithActiveTrue.get(i),
                        ConvertIdsHelper.concatenateIds(new String[] {objectBoundary.getObjectId().getSuperapp(),
                                objectBoundary.getObjectId().getInternalObjectId()}));


                    Map<String,Object> matchEntity = UtilHelper.jacksonHandle(match, Map.class);

                    SuperAppObjectBoundary boundary = this.help_PostObjectBoundary(null, "MATCH", "match",
                            null, true, null,
                            new CreatedBy().setUserId(new UserId(this.springApplicationName, email)), matchEntity);

                    return ConvertIdsHelper
                            .concatenateIds(new String[] {boundary.getObjectId().getSuperapp(),
                                    boundary.getObjectId().getInternalObjectId()});
                })
                .toList();

        List<String> matches = new ArrayList<>();

        matches.addAll(idsOfMatches);

        privateProfile = createPrivateProfileAsMap(profile, new Date(), 10, new ArrayList<>(),
                "052-8976625", 18, 99, likes, matches);


        objectBoundary.setObjectDetails(privateProfile);


        this.help_PutUserBoundary(new UserBoundary().setRole(UserRole.SUPERAPP_USER.name()), email);

        this.help_PutObjectBoundary(objectBoundary,
                objectBoundary.getObjectId().getInternalObjectId(),
                this.springApplicationName,
                this.springApplicationName, email);


        this.help_PutUserBoundary(new UserBoundary().setRole(UserRole.MINIAPP_USER.name()), email);
        Map<String, Object> commandAttr = new HashMap<>();
        commandAttr.put("page", 0);
        commandAttr.put("size", 100);

        Map<String, Object> result = UtilHelper.jacksonHandle(this.help_PostCommandBoundary(MiniAppNames.DATING.name(),
                null,
                MiniAppsCommand.commands.GET_MATCHES.name(),
                new TargetObject()
                        .setObjectId(new ObjectId(this.springApplicationName, objectBoundary.getObjectId().getInternalObjectId())),
                null,
                new InvokedBy().setUserId(new UserId(this.springApplicationName, email)),
                commandAttr), Map.class);

        List<Object> objectBoundaries = new ArrayList<>();
        for (Map.Entry<String, Object> entry: result.entrySet()) {

            Map<Object, Object>  insideRes = UtilHelper.jacksonHandle(entry.getValue(), Map.class);
            for (Map.Entry<Object, Object> objectEntry: insideRes.entrySet()) {
                objectBoundaries.add(objectEntry.getValue());
            }
        }

        assertEquals(5, objectBoundaries.size());

    }

    @Test
    @DisplayName("Successfully get potential date by gender and in ageRange and not in my likes")
    public void testGetPotentialDate(){

        // GIVEN
        // server and database is up
        // db contain the follow objects
        // 15 users
        // 15 SuperAppObject with objectDetails of UserDetails
        // 15 SuperAppObject with objectDetails PrivetDatingProfile

        // Create an instance of ObjectMapper from Jackson library
        ObjectMapper objectMapper = new ObjectMapper();
        String absolutePath = new File("src/test/resources").getAbsolutePath();



        JsonNode inputs = null;
        // Read the JSON file and parse it into a JsonNode object
        List<String> emails = new ArrayList<>();

        List<ObjectId> objectIdsOfUserDetails = new ArrayList<>();
        List<ObjectId> objectIdsOfDatingProfile = new ArrayList<>();

        try {
            inputs = objectMapper.readTree(new File(absolutePath +"/PotentialDateTestInput.json"));
            for (JsonNode element: inputs) {

                // extract tree objects - to post
                NewUserBoundary user = UtilHelper.jacksonHandle(element.get("user"), NewUserBoundary.class);
                Map<String,Object> userDetailsAsMap = UtilHelper.jacksonHandle(element.get("userDetails"), Map.class);
                Map<String,Object> datingProfilesAsMap = UtilHelper.jacksonHandle(element.get("datingProfile"), Map.class);


                UserBoundary userBoundary = this.help_PostUserBoundary(user.getEmail(), user.getRole(), user.getUsername(), user.getAvatar());

                emails.add(user.getEmail());

                SuperAppObjectBoundary userDetailsBoundary
                        = this.help_PostObjectBoundary(null, "USER_DETAILS", "USER_DETAILS",
                        null,true, null,
                        new CreatedBy().setUserId(new UserId(this.springApplicationName, userBoundary.getUserId().getEmail())),
                        userDetailsAsMap);


                objectIdsOfUserDetails.add(userDetailsBoundary.getObjectId());

                SuperAppObjectBoundary datingProfileBoundary
                        = this.help_PostObjectBoundary(null, "PRIVATE_DATING_PROFILE", "PRIVATE_DATING_PROFILE",
                        null,true, null,
                        new CreatedBy().setUserId(new UserId(this.springApplicationName, userBoundary.getUserId().getEmail())),
                        datingProfilesAsMap);

                objectIdsOfDatingProfile.add(datingProfileBoundary.getObjectId());

                this.putRelationBetweenObjects(userDetailsBoundary.getObjectId().getInternalObjectId(),
                        datingProfileBoundary.getObjectId(), this.springApplicationName,
                        userBoundary.getUserId().getEmail());

            }
        } catch (Exception e) {
            System.err.println(e);
        }

        int[] potentialExpected = new int[] {11, 8, 4,4, 4, 11, 8, 4, 4, 4, 11, 8, 4, 4, 4,};

        for (int i =0 ; i< inputs.size() ; i++){
            Map<String, Object> commandAttribute = new HashMap<>();
            commandAttribute.put("size", 15);
            commandAttribute.put("page", 0);
            commandAttribute.put("userDetailsId", objectIdsOfUserDetails.get(i));

            this.help_PutUserBoundary(new UserBoundary().setRole(UserRole.MINIAPP_USER.name()), emails.get(i));

            Object results =
                            this.help_PostCommandBoundary(
                                    MiniAppNames.DATING.name(),
                                    null,
                                    MiniAppsCommand.commands.GET_POTENTIAL_DATES.name(),
                                    new TargetObject().setObjectId(objectIdsOfDatingProfile.get(i)),
                                    null,
                                    new InvokedBy().setUserId(new UserId(this.springApplicationName, emails.get(i))),
                                    commandAttribute);


            List<SuperAppObjectBoundary> objectBoundaries = this.objectToListOfObjectBoundaries(results);

            assertEquals(potentialExpected[i], objectBoundaries.size());

            // test of not in my likes list
            if (potentialExpected[i] == 11) {
                SuperAppObjectBoundary objectBoundary = this.help_GetObjectBoundary(
                        objectIdsOfDatingProfile.get(i).getInternalObjectId(),
                        this.springApplicationName,
                        this.springApplicationName,
                        emails.get(i));
                PrivateDatingProfile profile = UtilHelper.jacksonHandle(objectBoundary.getObjectDetails(), PrivateDatingProfile.class);

                List<String> ids = new ArrayList<>();
                ids.add(this.springApplicationName + "_" + objectIdsOfDatingProfile.get(i+1).getInternalObjectId());
                ids.add(this.springApplicationName + "_" + objectIdsOfDatingProfile.get(i+2).getInternalObjectId());
                ids.add(this.springApplicationName + "_" + objectIdsOfDatingProfile.get(i+3).getInternalObjectId());
                ids.add(this.springApplicationName + "_" + objectIdsOfDatingProfile.get(i+4).getInternalObjectId());

                profile.setLikes(ids);
                objectBoundary.setObjectDetails(UtilHelper.jacksonHandle(profile, Map.class));

                help_PutUserBoundary(new UserBoundary().setRole(UserRole.SUPERAPP_USER.name()), emails.get(i));

                this.restTemplate.put(
                        this.baseUrl + "/superapp/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&" +
                                "userEmail={email}"
                        , objectBoundary, this.springApplicationName, objectBoundary.getObjectId().getInternalObjectId(), this.springApplicationName, emails.get(i));

                this.help_PutUserBoundary(new UserBoundary().setRole(UserRole.MINIAPP_USER.name()), emails.get(i));


                results =
                        this.help_PostCommandBoundary(
                                MiniAppNames.DATING.name(),
                                null,
                                MiniAppsCommand.commands.GET_POTENTIAL_DATES.name(),
                                new TargetObject().setObjectId(objectIdsOfDatingProfile.get(i)),
                                null,
                                new InvokedBy().setUserId(new UserId(this.springApplicationName, emails.get(i))),
                                commandAttribute);


                objectBoundaries = this.objectToListOfObjectBoundaries(results);

                assertEquals(potentialExpected[i] - 3, objectBoundaries.size());

            }

        }

    }



    @Test
    @DisplayName("Successfully get potential date by common preferences")
    public void testGetPotentialDateByCommonPreferences(){

        // GIVEN
        // server and database is up
        // db contain the follow objects

    }


    @Test
    @DisplayName("Successfully like other profile without match")
    public void testLikeProfile(){

        // create user
        String email = "charming@gmail.com";
        this.help_PostUserBoundary(email, UserRole.SUPERAPP_USER.name(), "username", "avatar");

        String type = "PRIVATE_DATING_PROFILE";

        // post 2 dating profile objects with active is true

        Map<String, Object> privateProfileA = createPrivateProfileAsMap(new PublicDatingProfile()
                .setNickName("i liked everyone A")
                .setAge(20)
                .setGender(Gender.MALE)
                .setBio("bio A"), new Date(), 10, new ArrayList<>(),
                "052-8976625", 18, 99, null, null);

        SuperAppObjectBoundary objectBoundaryA = this.help_PostObjectBoundary(null, type, "alias A",
                null, true, null,
                new CreatedBy().setUserId(new UserId(this.springApplicationName, email)), privateProfileA);


        Map<String, Object> privateProfileB = createPrivateProfileAsMap(new PublicDatingProfile()
                        .setNickName("i liked everyone B")
                        .setAge(20)
                        .setGender(Gender.MALE)
                        .setBio("bio B"), new Date(), 10, new ArrayList<>(),
                "052-8976625", 18, 99, null, null);

        SuperAppObjectBoundary objectBoundaryB = this.help_PostObjectBoundary(null, type, "alias",
                null, true, null,
                new CreatedBy().setUserId(new UserId(this.springApplicationName, email)), privateProfileB);


        this.help_PutUserBoundary(new UserBoundary().setRole(UserRole.MINIAPP_USER.name()), email);


        Map<String, Object> commandAttr = new HashMap<>();
        commandAttr.put("myDatingProfileId", objectBoundaryA.getObjectId());

        Map<String, Map> result = UtilHelper.jacksonHandle(this.help_PostCommandBoundary(MiniAppNames.DATING.name(),
                null,
                MiniAppsCommand.commands.LIKE_PROFILE.name(),
                new TargetObject()
                        .setObjectId(new ObjectId(this.springApplicationName, objectBoundaryB.getObjectId().getInternalObjectId())),
                null,
                new InvokedBy().setUserId(new UserId(this.springApplicationName, email)),
                commandAttr), Map.class);


        for (Map.Entry<String, Map> entry: result.entrySet()) {

            Map<String, Object>  insideRes = UtilHelper.jacksonHandle(entry.getValue(), Map.class);

            assertEquals(true, insideRes.get("like_status"));
            assertEquals(false, insideRes.get("match_status"));

        }

    }


    @Test
    @DisplayName("Successfully like other profile with match")
    public void testLikeProfileWithMatch(){

        // create user
        String email = "charming@gmail.com";
        this.help_PostUserBoundary(email, UserRole.SUPERAPP_USER.name(), "username", "avatar");

        String type = "PRIVATE_DATING_PROFILE";

        // post 2 dating profile objects with active is true

        Map<String, Object> privateProfileA = createPrivateProfileAsMap(new PublicDatingProfile()
                        .setNickName("i liked everyone A")
                        .setAge(20)
                        .setGender(Gender.MALE)
                        .setBio("bio A"), new Date(), 10, new ArrayList<>(),
                "052-8976625", 18, 99, null, null);

        SuperAppObjectBoundary objectBoundaryA = this.help_PostObjectBoundary(null, type, "alias A",
                null, true, null,
                new CreatedBy().setUserId(new UserId(this.springApplicationName, email)), privateProfileA);


        Map<String, Object> privateProfileB = createPrivateProfileAsMap(new PublicDatingProfile()
                        .setNickName("i liked everyone B")
                        .setAge(20)
                        .setGender(Gender.MALE)
                        .setBio("bio B"), new Date(), 10, new ArrayList<>(),
                "052-8976625", 18, 99, null, null);

        SuperAppObjectBoundary objectBoundaryB = this.help_PostObjectBoundary(null, type, "alias",
                null, true, null,
                new CreatedBy().setUserId(new UserId(this.springApplicationName, email)), privateProfileB);


        this.help_PutUserBoundary(new UserBoundary().setRole(UserRole.MINIAPP_USER.name()), email);

        Map<String, Object> commandAttrB = new HashMap<>();
        commandAttrB.put("myDatingProfileId", objectBoundaryB.getObjectId());
        this.help_PostCommandBoundary(MiniAppNames.DATING.name(),
                null,
                MiniAppsCommand.commands.LIKE_PROFILE.name(),
                new TargetObject()
                        .setObjectId(new ObjectId(this.springApplicationName, objectBoundaryA.getObjectId().getInternalObjectId())),
                null,
                new InvokedBy().setUserId(new UserId(this.springApplicationName, email)),
                commandAttrB);

        Map<String, Object> commandAttrA = new HashMap<>();
        commandAttrA.put("myDatingProfileId", objectBoundaryA.getObjectId());
        this.help_PutUserBoundary(new UserBoundary().setRole(UserRole.MINIAPP_USER.name()), email);
        Map<String, Map> result = UtilHelper.jacksonHandle(this.help_PostCommandBoundary(MiniAppNames.DATING.name(),
                null,
                MiniAppsCommand.commands.LIKE_PROFILE.name(),
                new TargetObject()
                        .setObjectId(new ObjectId(this.springApplicationName, objectBoundaryB.getObjectId().getInternalObjectId())),
                null,
                new InvokedBy().setUserId(new UserId(this.springApplicationName, email)),
                commandAttrA), Map.class);


        for (Map.Entry<String, Map> entry: result.entrySet()) {

            Map<String, Object>  insideRes = UtilHelper.jacksonHandle(entry.getValue(), Map.class);

            assertEquals(true, insideRes.get("like_status"));
            assertEquals(true, insideRes.get("match_status"));

        }

    }

    @Test
    @DisplayName("Successfully do unmatch")
    public void testUnmatch(){


        // create user
        String email = "charming@gmail.com";
        this.help_PostUserBoundary(email, UserRole.SUPERAPP_USER.name(), "username", "avatar");

        String type = "PRIVATE_DATING_PROFILE";

        // post 2 dating profile objects with active is true

        Map<String, Object> privateProfileA = createPrivateProfileAsMap(new PublicDatingProfile()
                        .setNickName("i liked everyone A")
                        .setAge(20)
                        .setGender(Gender.MALE)
                        .setBio("bio A"), new Date(), 10, new ArrayList<>(),
                "052-8976625", 18, 99, null, null);

        SuperAppObjectBoundary objectBoundaryA = this.help_PostObjectBoundary(null, type, "alias A",
                null, true, null,
                new CreatedBy().setUserId(new UserId(this.springApplicationName, email)), privateProfileA);


        Map<String, Object> privateProfileB = createPrivateProfileAsMap(new PublicDatingProfile()
                        .setNickName("i liked everyone B")
                        .setAge(20)
                        .setGender(Gender.MALE)
                        .setBio("bio B"), new Date(), 10, new ArrayList<>(),
                "052-8976625", 18, 99, null, null);

        SuperAppObjectBoundary objectBoundaryB = this.help_PostObjectBoundary(null, type, "alias",
                null, true, null,
                new CreatedBy().setUserId(new UserId(this.springApplicationName, email)), privateProfileB);


        this.help_PutUserBoundary(new UserBoundary().setRole(UserRole.MINIAPP_USER.name()), email);

        Map<String, Object> commandAttrB = new HashMap<>();
        commandAttrB.put("myDatingProfileId", objectBoundaryB.getObjectId());
        this.help_PostCommandBoundary(MiniAppNames.DATING.name(),
                null,
                MiniAppsCommand.commands.LIKE_PROFILE.name(),
                new TargetObject()
                        .setObjectId(new ObjectId(this.springApplicationName, objectBoundaryA.getObjectId().getInternalObjectId())),
                null,
                new InvokedBy().setUserId(new UserId(this.springApplicationName, email)),
                commandAttrB);

        Map<String, Object> commandAttrA = new HashMap<>();
        commandAttrA.put("myDatingProfileId", objectBoundaryA.getObjectId());

        Map<String, Map> result = UtilHelper.jacksonHandle(this.help_PostCommandBoundary(MiniAppNames.DATING.name(),
                null,
                MiniAppsCommand.commands.LIKE_PROFILE.name(),
                new TargetObject()
                        .setObjectId(new ObjectId(this.springApplicationName, objectBoundaryB.getObjectId().getInternalObjectId())),
                null,
                new InvokedBy().setUserId(new UserId(this.springApplicationName, email)),
                commandAttrA), Map.class);


        ObjectId matchId = null;
        for (Map.Entry<String, Map> entry: result.entrySet()) {
            Map<String, Object>  insideRes = UtilHelper.jacksonHandle(entry.getValue(), Map.class);
            matchId = UtilHelper.jacksonHandle(insideRes.get("match_id"), ObjectId.class);
        }

        Map<String, Map> unmatchResult = UtilHelper
                .jacksonHandle(this.help_PostCommandBoundary(MiniAppNames.DATING.name(),
                null,
                MiniAppsCommand.commands.UNMATCH_PROFILE.name(),
                new TargetObject()
                        .setObjectId(new ObjectId(this.springApplicationName, matchId.getInternalObjectId())),
                null,
                new InvokedBy().setUserId(new UserId(this.springApplicationName, email)),
                commandAttrA), Map.class);

        for (Map.Entry<String, Map> entry: unmatchResult.entrySet()) {
            Map<String, Object>  insideRes = UtilHelper.jacksonHandle(entry.getValue(), Map.class);
            assertEquals("canceled", insideRes.get("match_status"));
            assertEquals("removed", insideRes.get("like_status"));
        }

        this.help_PutUserBoundary(new UserBoundary().setRole(UserRole.SUPERAPP_USER.name()), email);

        SuperAppObjectBoundary profile1 = help_GetObjectBoundary(objectBoundaryB.getObjectId().getInternalObjectId(), springApplicationName, springApplicationName, email);
        assertThat(UtilHelper.jacksonHandle(profile1.getObjectDetails(), PrivateDatingProfile.class).getMatches())
                .isEmpty();
        assertThat(UtilHelper.jacksonHandle(profile1.getObjectDetails(), PrivateDatingProfile.class).getLikes())
                .isEmpty();

        SuperAppObjectBoundary profile2 = help_GetObjectBoundary(objectBoundaryA.getObjectId().getInternalObjectId(), springApplicationName, springApplicationName, email);
        assertThat(UtilHelper.jacksonHandle(profile2.getObjectDetails(), PrivateDatingProfile.class).getMatches())
                .isEmpty();
        assertThat(UtilHelper.jacksonHandle(profile2.getObjectDetails(), PrivateDatingProfile.class).getLikes())
                .isEmpty();

        SuperAppObjectBoundary match = help_GetObjectBoundary(matchId.getInternalObjectId(), springApplicationName, springApplicationName, email);
        assertEquals(false, match.getActive());

    }

}
