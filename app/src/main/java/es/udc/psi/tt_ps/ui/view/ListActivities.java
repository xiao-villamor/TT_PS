package es.udc.psi.tt_ps.ui.viewmodel;

import android.graphics.Point;

import java.io.Serializable;
import java.sql.Timestamp;

public class ListActivities implements Serializable {
    public int activityImage;
    public String title;
    public Point location;
    public Timestamp end_date;

    public ListActivities(int activityImage, String title, Point location, Timestamp end_date) {
        this.activityImage = activityImage;
        this.title = title;
        this.location = location;
        this.end_date = end_date;
    }

    public int getActivityImage() {
        return activityImage;
    }

    public void setActivityImage(int activityImage) {
        this.activityImage = activityImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Timestamp getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Timestamp end_date) {
        this.end_date = end_date;
    }
}