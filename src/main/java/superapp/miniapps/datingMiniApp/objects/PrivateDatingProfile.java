package superapp.miniapps.datingMiniApp.objects;

import superapp.miniapps.datingMiniApp.Gender;

import java.util.ArrayList;
import java.util.List;

public class PrivateDatingProfile {

    private PublicDatingProfile publicProfile;
    private Address address;
    private int distanceRange;
    private int ageRange;   // (18-120)
    private List<Gender> genderPreferences;
    private List<Match> matches;
    private List<String> likes; // list of profile dating id that I liked


    public PrivateDatingProfile() {
        this.genderPreferences = new ArrayList<>();
        this.matches = new ArrayList<>();
        this.likes = new ArrayList<>();
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

    public List<String> getLikes() {
        return likes;
    }

    public PrivateDatingProfile setLikes(List<String> likes) {
        this.likes = likes;
        return this;
    }

    @Override
    public String toString() {
        return "PrivateDatingProfile{" +
                "publicProfile=" + publicProfile +
                ", address=" + address +
                ", distanceRange=" + distanceRange +
                ", ageRange=" + ageRange +
                ", genderPreferences=" + genderPreferences +
                ", matches=" + matches +
                ", likes=" + likes +
                '}';
    }
}
