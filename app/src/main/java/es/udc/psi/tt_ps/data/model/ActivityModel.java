package es.udc.psi.tt_ps.data.model;

import android.graphics.Point;

import java.sql.Timestamp;
import java.util.List;

public class ActivityModel {
    private String id;
    private String title;
    private String description;
    private Timestamp start_date;
    private Timestamp end_date;
    private Point location;
    private String adminId;
    private List<String> participants;
    private List<String> tags;

    public ActivityModel(String id, String title, String description,
                         Timestamp start_date, Timestamp end_date,
                         Point location, String adminId,
                         List<String> participants, List<String> tags) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.start_date = start_date;
        this.end_date = end_date;
        this.location = location;
        this.adminId = adminId;
        this.participants = participants;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getStart_date() {
        return start_date;
    }

    public Timestamp getEnd_date() {
        return end_date;
    }

    public Point getLocation() {
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
