package es.udc.psi.tt_ps.data.model;


import java.util.Date;
import java.util.List;

public class UserModel {
    private String name;
    private String surname;
    private Date birthDate;
    private String email;
    private String phone;
    private String profilePic;
    private List<String> rrss;
    private List<Float> rating;
    private List<String> interests;
    private String description;

    public UserModel( String name,
                     String surname, Date birthDate, String email,
                     String phone, String profilePic, List<String> rrss,
                     List<Float> rating, List<String> interests) {

        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
        this.profilePic = profilePic;
        this.rrss = rrss;
        this.rating = rating;
        this.interests = interests;
    }

    public UserModel() {
    }

    public String getDescription() {
        return description;
    }
    public String setDescription(String description) {
        return this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){this.name=name;}

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname){this.surname=surname;}

    public Date getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone){this.phone=phone;}

    public String getProfilePic() {
        return profilePic;
    }

    public List<String> getrrss() {
        return rrss;
    }

    public List<Float> getRating() {
        return rating;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> newInterests){interests=newInterests;}

    public void profilePic(String image){
        this.profilePic = image;
    }

}
