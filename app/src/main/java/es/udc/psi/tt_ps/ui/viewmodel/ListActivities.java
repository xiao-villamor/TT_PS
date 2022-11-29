package es.udc.psi.tt_ps.ui.viewmodel;


import android.graphics.PointF;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class ListActivities implements Serializable {
    public String activityImage;
    public String title;
    public transient PointF location;
    public Date end_date;
    public String description;
    public Date start_date;
    public Date creation_date;
    public String adminId;
    public Integer participants;
    public List<String> tags;


    public ListActivities(String activityImage, String title, PointF location, Date end_date, String description, Date start_date, Date creation_date, String adminId, Integer participants, List<String> tags) {
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

    public ListActivities() {

    }

    public ListActivities(String activityImage, String title, PointF location, Date end_date, String description) {
        this.activityImage = activityImage;
        this.title = title;
        this.location = location;
        this.end_date = end_date;
        this.description = description;

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

    public PointF getLocation() {
        return location;
    }

    public void setLocation(PointF location) {
        this.location = location;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Timestamp end_date) {this.end_date = end_date;}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}