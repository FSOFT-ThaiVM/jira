package com.atlassian.plugins.tutorial;

import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: datlt2
 * Date: 7/15/13
 * Time: 9:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class CustomPlan {

    private Timestamp fromDate;
    private Timestamp toDate;
    private long secondsPerDay;

    public Timestamp getFromDate() {
        return fromDate;
    }

    public void setFromDate(Timestamp fromDate) {
        this.fromDate = fromDate;
    }

    public Timestamp getToDate() {
        return toDate;
    }

    public void setToDate(Timestamp toDate) {
        this.toDate = toDate;
    }

    public long getSecondsPerDay() {
        return secondsPerDay;
    }

    public void setSecondsPerDay(long secondsPerDay) {
        this.secondsPerDay = secondsPerDay;
    }

    public CustomPlan() {
    }

    public CustomPlan(Timestamp fromDate, Timestamp toDate, long secondsPerDay) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.secondsPerDay = secondsPerDay;
    }
}
