package es.udc.psi.tt_ps.ui.viewmodel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ListUsers implements Serializable {
    private String name;
    private String surname;
    private Date birthDate;
    private String email;
    private String phone;
    private String profilePic;
    private List<String> rrss;
    private Float rating;
    private List<String> interests;
    private String description;

    public ListUsers(String name, String surname, Date birthDate, String email,
                     String phone, String profilePic, List<String> rrss, Float rating,
                     List<String> interests, String description) {
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
        this.profilePic = profilePic;
        this.rrss = rrss;
        this.rating = rating;
        this.interests = interests;
        this.description = description;
    }



    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public List<String> getRrss() {
        return rrss;
    }

    public Float getRating() {
        return rating;
    }

    public List<String> getInterests() {
        return interests;
    }

    public String getDescription() {
        return description;
    }
}
