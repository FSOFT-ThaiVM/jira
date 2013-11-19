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
public class ProjectResourceAllocation {

    private Project project;
    private List<UserProjectResourceAllocation> userProjectResourceAllocations;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<UserProjectResourceAllocation> getUserProjectResourceAllocations() {
        return userProjectResourceAllocations;
    }

    public void setUserProjectResourceAllocations(List<UserProjectResourceAllocation> userProjectResourceAllocations) {
        this.userProjectResourceAllocations = userProjectResourceAllocations;
    }

    public ProjectResourceAllocation() {
    }

    public ProjectResourceAllocation(Project project, List<UserProjectResourceAllocation> userProjectResourceAllocations) {
        this.project = project;
        this.userProjectResourceAllocations = userProjectResourceAllocations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectResourceAllocation)) return false;

        ProjectResourceAllocation that = (ProjectResourceAllocation) o;

        if (project == that.project) return true;
        if (project.getId() != that.project.getId()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (project.getId() ^ (project.getId() >>> 32));
    }

    public static class UserProjectResourceAllocation {

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

        public UserProjectResourceAllocation() {
        }

        public UserProjectResourceAllocation(String username, List<CustomPlan> plans, List<CustomIssue> issues) {
            this.username = username;
            this.plans = plans;
            this.issues = issues;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserProjectResourceAllocation that = (UserProjectResourceAllocation) o;

            if (!username.equals(that.username)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return username.hashCode();
        }
    }
}
