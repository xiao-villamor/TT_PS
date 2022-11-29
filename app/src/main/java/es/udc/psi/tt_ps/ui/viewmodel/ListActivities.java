package es.udc.psi.tt_ps.ui.viewmodel;


import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class ListActivities implements Serializable {
    public String activityImage;
    public String title;
    public transient GeoPoint location;
    public Date end_date;
    public String description;
    public Date start_date;
    public Date creation_date;
    public String adminId;
    public List<String> participants;
    public List<String> tags;


    public ListActivities(String activityImage, String title, GeoPoint location, Date end_date, String description, Date start_date, Date creation_date, String adminId, List<String> participants, List<String> tags) {
        this.activityImage = activityImage;
        this.title = title;
        this.location = location;
        this.end_date = end_date;
        this.description = description;
        this.start_date = start_date;
        this.creation_date = creation_date;
        this.adminId = adminId;
        this.participants = participants;
        this.tags = tags;
    }


    public String getActivityImage() {
        return activityImage;
    }

    public void setActivityImage(String activityImage) {
        this.activityImage = activityImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Timestamp end_date) {this.end_date = end_date;}

    public String getDescription() {
        return description;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public String getParticioants(){
        return ((Integer) participants.size()).toString();
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}