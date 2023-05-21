package superapp.miniapps.datingMiniApp;

import superapp.miniapps.Gender;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrivateDatingProfile {

    private PublicDatingProfile publicProfile;
    private Date dateOfBirthday;
    private int distanceRange;
    private int maxAge;
    private int minAge;


    private List<Gender> genderPreferences;
    private List<String> matches; // list of matches id
    private List<String> likes; // list of profile dating id that I liked


    public PrivateDatingProfile() {
        this.genderPreferences = new ArrayList<>();
        this.matches = new ArrayList<>();
        this.likes = new ArrayList<>();
    }

    public Date getDateOfBirthday() {
        return dateOfBirthday;
    }

    public PrivateDatingProfile setDateOfBirthday(Date dateOfBirthday) {
        this.dateOfBirthday = dateOfBirthday;
        return this;
    }

    public PublicDatingProfile getPublicProfile() {
        return publicProfile;
    }

    public PrivateDatingProfile setPublicProfile(PublicDatingProfile publicProfile) {
        this.publicProfile = publicProfile;
        return this;
    }


    public int getDistanceRange() {
        return distanceRange;
    }

    public PrivateDatingProfile setDistanceRange(int distanceRange) {
        this.distanceRange = distanceRange;
        return this;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public PrivateDatingProfile setMaxAge(int maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public int getMinAge() {
        return minAge;
    }

    public PrivateDatingProfile setMinAge(int minAge) {
        this.minAge = minAge;
        return this;
    }

    public List<Gender> getGenderPreferences() {
        return genderPreferences;
    }

    public PrivateDatingProfile setGenderPreferences(List<Gender> genderPreferences) {
        this.genderPreferences = genderPreferences;
        return this;
    }

    public List<String> getMatches() {
        return matches;
    }

    public PrivateDatingProfile setMatches(List<String> matches) {
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
                ", dateOfBirthday=" + dateOfBirthday +
                ", distanceRange=" + distanceRange +
                ", maxAge=" + maxAge +
                ", minAge=" + minAge +
                ", genderPreferences=" + genderPreferences +
                ", matches=" + matches +
                ", likes=" + likes +
                '}';
    }
}
