package superapp.miniapps.datingMiniApp;

import java.util.ArrayList;
import java.util.List;

public class PrivateDatingProfile {

    private PublicDatingProfile publicProfile;
    private Address address;
    private int distanceRange;
    private int ageRange;   // (18-120)
    private List<Gender> genderPreferences;
    private List<Match> matches;
    private List<String> myLikes; // list of profile dating id that I liked them
    private List<String> unlikes; // list of profile dating id that I unliked them
    private List<String> likesMe; // list of profile dating id that liked me


    public PrivateDatingProfile() {
        this.genderPreferences = new ArrayList<>();
        this.matches = new ArrayList<>();
        this.myLikes = new ArrayList<>();
        this.likesMe = new ArrayList<>();
        this.unlikes = new ArrayList<>();
    }

    public PublicDatingProfile getPublicProfile() {
        return publicProfile;
    }

    public PrivateDatingProfile setPublicProfile(PublicDatingProfile publicProfile) {
        this.publicProfile = publicProfile;
        return this;
    }

    public Address getAddress() {
        return address;
    }

    public PrivateDatingProfile setAddress(Address address) {
        this.address = address;
        return this;
    }

    public int getDistanceRange() {
        return distanceRange;
    }

    public PrivateDatingProfile setDistanceRange(int distanceRange) {
        this.distanceRange = distanceRange;
        return this;
    }

    public int getAgeRange() {
        return ageRange;
    }

    public PrivateDatingProfile setAgeRange(int ageRange) {
        this.ageRange = ageRange;
        return this;
    }

    public List<Gender> getGenderPreferences() {
        return genderPreferences;
    }

    public PrivateDatingProfile setGenderPreferences(List<Gender> genderPreferences) {
        this.genderPreferences = genderPreferences;
        return this;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public PrivateDatingProfile setMatches(List<Match> matches) {
        this.matches = matches;
        return this;
    }

    public List<String> getMyLikes() {
        return myLikes;
    }

    public PrivateDatingProfile setMyLikes(List<String> myLikes) {
        this.myLikes = myLikes;
        return this;
    }

    public List<String> getLikesMe() {
        return likesMe;
    }

    public PrivateDatingProfile setLikesMe(List<String> likesMe) {
        this.likesMe = likesMe;
        return this;
    }

    public List<String> getUnlikes() {
        return unlikes;
    }

    public PrivateDatingProfile setUnlikes(List<String> unlikes) {
        this.unlikes = unlikes;
        return this;
    }

    @Override
    public String toString() {
        return "PrivetDatingProfile{" +
                "publicProfile=" + publicProfile.toString() +
                ", address=" + address +
                ", distanceRange=" + distanceRange +
                ", ageRange=" + ageRange +
                ", genderPreferences=" + genderPreferences +
                ", matches=" + matches +
                ", myLikes=" + myLikes +
                ", unlikes=" + unlikes +
                ", likesMe=" + likesMe +
                '}';
    }
}
