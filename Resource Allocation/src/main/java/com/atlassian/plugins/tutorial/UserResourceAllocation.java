package com.atlassian.plugins.tutorial;

import com.atlassian.jira.project.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: datlt2
 * Date: 7/15/13
 * Time: 9:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserResourceAllocation {

    private String username;
    private List<ProjectUserResourceAllocation> projectUserResourceAllocations;

    public UserResourceAllocation() {
    }

    public UserResourceAllocation(String username, List<ProjectUserResourceAllocation> projectUserResourceAllocations) {
        this.username = username;
        this.projectUserResourceAllocations = projectUserResourceAllocations;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<ProjectUserResourceAllocation> getProjectUserResourceAllocations() {
        return projectUserResourceAllocations;
    }

    public void setProjectUserResourceAllocations(List<ProjectUserResourceAllocation> projectUserResourceAllocations) {
        this.projectUserResourceAllocations = projectUserResourceAllocations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserResourceAllocation)) return false;

        UserResourceAllocation that = (UserResourceAllocation) o;

        if (!username.equals(that.username)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    public static class ProjectUserResourceAllocation {

        private Project project;
        private List<CustomPlan> plans;
        private List<CustomIssue> issues;

        public ProjectUserResourceAllocation() {
        }

        public ProjectUserResourceAllocation(Project project, List<CustomPlan> plans, List<CustomIssue> issues) {
            this.project = project;
            this.plans = plans;
            this.issues = issues;
        }

        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ProjectUserResourceAllocation)) return false;

            ProjectUserResourceAllocation that = (ProjectUserResourceAllocation) o;

            if (project == that.project) return true;
            if (project.getId() != that.project.getId()) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return (int) (project.getId() ^ (project.getId() >>> 32));
        }
    }
}
