package es.udc.psi.tt_ps.data.model;

import android.graphics.Point;
import android.graphics.PointF;

import java.util.Date;
import java.util.List;

public class ActivityModel {
    private String title;
    private String description;
    private Date start_date;
    private Date end_date;
    private Date creation_date;
    private PointF location;
    private String adminId;
    private List<String> participants;
    private List<String> tags;

    public ActivityModel(String title, String description,
                         Date start_date, Date end_date,Date creation_date,
                         PointF location, String adminId,
                         List<String> participants, List<String> tags) {

        this.title = title;
        this.description = description;
        this.start_date = start_date;
        this.end_date = end_date;
        this.creation_date = creation_date;
        this.location = location;
        this.adminId = adminId;
        this.participants = participants;
        this.tags = tags;
    }
    public ActivityModel() {
    }

    public Date getCreation_date() {
        return creation_date;
    }
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getStart_date() {
        return start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public PointF getLocation() {
        return location;
    }

    public String getAdminId() {
        return adminId;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public List<String> getTags() {
        return tags;
    }
}
