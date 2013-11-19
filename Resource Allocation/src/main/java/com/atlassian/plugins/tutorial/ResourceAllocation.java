package com.atlassian.plugins.tutorial;

import com.atlassian.jira.project.Project;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: datlt2
 * Date: 7/8/13
 * Time: 4:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceAllocation {

    private Project project;
    private List<UserResourceAllocation> userResourceAllocations;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<UserResourceAllocation> getUserResourceAllocations() {
        return userResourceAllocations;
    }

    public void setUserResourceAllocations(List<UserResourceAllocation> userResourceAllocations) {
        this.userResourceAllocations = userResourceAllocations;
    }

    public ResourceAllocation() {
    }

    public ResourceAllocation(Project project, List<UserResourceAllocation> userResourceAllocations) {
        this.project = project;
        this.userResourceAllocations = userResourceAllocations;
    }

    public static class UserResourceAllocation {

        private String username;
        private List<CustomPlan> plans;
        private List<CustomIssue> issues;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public List<CustomPlan> getPlans() {
            return plans;
        }

        public void setPlans(List<CustomPlan> plans) {
            this.plans = plans;
        }

        public List<CustomIssue> getIssues() {
            return issues;
        }

        public void setIssues(List<CustomIssue> issues) {
            this.issues = issues;
        }

        public void addPlan(CustomPlan plan) {
            boolean isExist = false;
            if (plans != null) {
                for (int i = 0; i < plans.size() && !isExist; i++) {
                    if (plans.get(i).equals(plan)) {
                        isExist = true;
                    }
                }
            } else {
                plans = new ArrayList<CustomPlan>();
            }
            // plan does not exist
            if (!isExist) {
                plans.add(plan);
            }
        }

        public void addIssue(CustomIssue issue) {
            boolean isExist = false;
            if (issues != null) {
                for (int i = 0; i < issues.size() && !isExist; i++) {
                    if (issues.get(i).equals(issue)) {
                        isExist = true;
                        // update fields
                        if (issue.getPlannedStart() != null) {
                            issues.get(i).setPlannedStart(issue.getPlannedStart());
                        }
                        if (issue.getPlannedEnd() != null) {
                            issues.get(i).setPlannedEnd(issue.getPlannedEnd());
                        }
                    }
                }
            } else {
                issues = new ArrayList<CustomIssue>();
            }
            // issue does not exist
            if (!isExist) {
                issues.add(issue);
            }
        }

        public UserResourceAllocation() {
        }

        public UserResourceAllocation(String username, List<CustomPlan> plans, List<CustomIssue> issues) {
            this.username = username;
            this.plans = plans;
            this.issues = issues;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserResourceAllocation that = (UserResourceAllocation) o;

            if (!username.equals(that.username)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return username.hashCode();
        }

        public static class CustomPlan {

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

        public static class CustomIssue {

            private long id;
            private String name;
            private String key;
            private String type;
            private long estimate;
            private Timestamp plannedStart;
            private Timestamp plannedEnd;

            public CustomIssue() {
            }

            public CustomIssue(long id, String name, String key, String type, long estimate, Timestamp plannedStart, Timestamp plannedEnd) {
                this.id = id;
                this.name = name;
                this.key = key;
                this.type = type;
                this.estimate = estimate;
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

            public long getEstimate() {
                return estimate;
            }

            public void setEstimate(long estimate) {
                this.estimate = estimate;
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
    }
}
