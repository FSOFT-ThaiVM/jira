package com.atlassian.plugins.tutorial;

import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: datlt2
 * Date: 7/15/13
 * Time: 9:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class CustomIssue {

    private long id;
    private String name;
    private String key;
    private String type;
    private long timeOriginalEstimate;
    private Timestamp plannedStart;
    private Timestamp plannedEnd;

    public CustomIssue() {
    }

    public CustomIssue(long id, String name, String key, String type, long timeOriginalEstimate, Timestamp plannedStart, Timestamp plannedEnd) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.type = type;
        this.timeOriginalEstimate = timeOriginalEstimate;
        this.plannedStart = plannedStart;
        this.plannedEnd = plannedEnd;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimeOriginalEstimate() {
        return timeOriginalEstimate;
    }

    public void setTimeOriginalEstimate(long timeOriginalEstimate) {
        this.timeOriginalEstimate = timeOriginalEstimate;
    }

    public Timestamp getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(Timestamp plannedStart) {
        this.plannedStart = plannedStart;
    }

    public Timestamp getPlannedEnd() {
        return plannedEnd;
    }

    public void setPlannedEnd(Timestamp plannedEnd) {
        this.plannedEnd = plannedEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomIssue that = (CustomIssue) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
