package com.atlassian.plugins.tutorial;

import com.atlassian.jira.project.Project;
import org.ofbiz.core.entity.GenericDataSourceException;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.jdbc.SQLProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: datlt2
 * Date: 7/8/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceAllocationAction extends AbstractJiraWebActionSupport {

    private static final String PROJECT_RESOURCES = "project_resources";
    private static final String USER_RESOURCES = "user_resources";
    private static final byte QUERY_BY_USER = 0;
    private static final byte QUERY_BY_PROJECT = 1;

    private List<ProjectResourceAllocation> projectResourceAllocations;
    private List<UserResourceAllocation> userResourceAllocations;
    private List<TeamResourceAllocation> teamResourceAllocations;

    public List<UserResourceAllocation> getUserResourceAllocations() {
        return userResourceAllocations;
    }

    public List<ProjectResourceAllocation> getProjectResourceAllocations() {
        return projectResourceAllocations;
    }

    public List<TeamResourceAllocation> getTeamResourceAllocations() {
        return teamResourceAllocations;
    }

    public String doGetProjectResources() throws Exception {
        initRequest();

        //this.projectResourceAllocations = getAllResourceAllocations();
        //log.info("projectResourceAllocations size(): " + this.projectResourceAllocations.size());
        this.projectResourceAllocations = queryResourceAllocations(QUERY_BY_PROJECT);

        return PROJECT_RESOURCES;
    }

    public String doGetUserResources() throws Exception {
        initRequest();

        this.userResourceAllocations = queryResourceAllocations(QUERY_BY_USER);
        this.teamResourceAllocations = queryTeamResourceAllocation();
        return USER_RESOURCES;
    }

    public static List<TeamResourceAllocation> queryTeamResourceAllocation() {
        SQLProcessor sql = new SQLProcessor("defaultDS");
        List<TeamResourceAllocation> teamResourceAllocations = new ArrayList<TeamResourceAllocation>();
        try {
            sql.prepareStatement("select ID, NAME from AO_AEFED0_TEAM");
            ResultSet rs =  sql.executeQuery();
            while(rs.next()){
                List<TeamResourceAllocation.User> users = new ArrayList<TeamResourceAllocation.User>();

                sql.prepareStatement("SELECT DISTINCT MEMBER_KEY " +
                        "FROM AO_AEFED0_TEAM_MEMBER A, AO_AEFED0_TEAM B, AO_AEFED0_TEAM_TO_MEMBER C " +
                        "WHERE B.ID = C.TEAM_ID " +
                        "AND A.ID = C.TEAM_MEMBER_ID " +
                        "AND B.ID = " + rs.getInt("ID") +
                        "AND A.MEMBER_TYPE = 'user'");
                ResultSet resultSet = sql.executeQuery();
                while (resultSet.next()) {
                    TeamResourceAllocation.User user = new TeamResourceAllocation.User(resultSet.getString("MEMBER_KEY"));
                    users.add(user);
                }

                sql.prepareStatement("SELECT DISTINCT CHILD_NAME " +
                        "FROM AO_AEFED0_TEAM_MEMBER A, AO_AEFED0_TEAM B, AO_AEFED0_TEAM_TO_MEMBER C , CWD_MEMBERSHIP D " +
                        "WHERE B.ID = C.TEAM_ID AND A.ID = C.TEAM_MEMBER_ID " +
                        "AND A.MEMBER_TYPE = 'group' " +
                        "AND A.MEMBER_KEY = D.PARENT_NAME " +
                        "AND B.ID = " + rs.getInt("ID"));
                ResultSet resultSet1 = sql.executeQuery();
                while (resultSet1.next()) {
                    TeamResourceAllocation.User user = new TeamResourceAllocation.User(resultSet1.getString("CHILD_NAME"));
                    users.add(user);
                }

                TeamResourceAllocation teamResourceAllocation = new TeamResourceAllocation(rs.getString("NAME"), users);
                teamResourceAllocations.add(teamResourceAllocation);


            }
        } catch (GenericDataSourceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (GenericEntityException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return teamResourceAllocations;
    }

    public static List<ProjectResourceAllocation> getAllResourceAllocations() {
        List<ProjectResourceAllocation> projectResourceAllocations = new ArrayList<ProjectResourceAllocation>();

        List<Project> projects = ResourceAllocationAction.projects;
        for (Project project : projects) {
            projectResourceAllocations.add(new ProjectResourceAllocation(project, getUserResourceAllocations(project.getId())));
        }

        return projectResourceAllocations;
    }

    public static <T> List<T> queryResourceAllocations(byte queryBy) {
        SQLProcessor sql = new SQLProcessor("defaultDS");
        List<T> results;
        switch (queryBy) {
            case QUERY_BY_PROJECT:
                results = (List<T>) new ArrayList<ProjectResourceAllocation>();
                for (Project project : projects) {
                    results.add((T) new ProjectResourceAllocation(project, null));
                }
                break;
            case QUERY_BY_USER:
            default:
                results = (List<T>) new ArrayList<UserResourceAllocation>();
                break;
        }

        try {
            sql.prepareStatement("SELECT collaborator username, from_date, to_date, seconds seconds_per_day, plan_id pid " +
                                "FROM AO_86ED1B_TIMEPLAN " +
                                "WHERE plan_type='project'");
            ResultSet rs = sql.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                Timestamp fromDate = rs.getTimestamp("from_date");
                Timestamp toDate = rs.getTimestamp("to_date");
                long secondsPerDay = rs.getLong("seconds_per_day");
                long projectId = rs.getLong("pid");
                CustomPlan plan = new CustomPlan(fromDate, toDate, secondsPerDay);
                switch (queryBy) {
                    case QUERY_BY_PROJECT:
                        addProjectResourceAllocation((List<ProjectResourceAllocation>) results, projectId, username, plan, null);
                        break;
                    case QUERY_BY_USER:
                    default:
                        addUserResourceAllocation((List<UserResourceAllocation>) results, projectId, username, plan, null);
                        break;
                }
            }

            sql.prepareStatement("SELECT i.id, i.summary name, i.pkey key, t.pname type, i.timeoriginalestimate, i.assignee, c.cfname field, c.datevalue, p.id pid " +
                                "FROM JIRAISSUE i JOIN PROJECT p on i.project = p.id " +
                                "LEFT JOIN (SELECT cf.id, cf.cfname, cfv.issue, cfv.datevalue INTO c " +
                                "           FROM CUSTOMFIELDVALUE cfv, CUSTOMFIELD cf " +
                                "           WHERE cfv.customfield = cf.id) c " +
                                "   ON i.id = c.issue " +
                                "JOIN ISSUETYPE t ON i.issuetype = t.id");
            rs =  sql.executeQuery();
            while(rs.next()){
                String username = rs.getString("assignee");
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String key = rs.getString("key");
                String type = rs.getString("type");
                long projectId = rs.getLong("pid");
                long timeOriginalEstimate = rs.getLong("timeoriginalestimate");
                String field = rs.getString("field");
                Timestamp start = null, end = null;
                if (field != null && field.toLowerCase().contains("start")) {
                    start = rs.getTimestamp("datevalue");
                } else if (field != null) {
                    end = rs.getTimestamp("datevalue");
                }
                CustomIssue issue = new CustomIssue(id, name, key, type, timeOriginalEstimate, start, end);
                switch (queryBy) {
                    case QUERY_BY_PROJECT:
                        addProjectResourceAllocation((List<ProjectResourceAllocation>) results, projectId, username, null, issue);
                        break;
                    case QUERY_BY_USER:
                    default:
                        addUserResourceAllocation((List<UserResourceAllocation>) results, projectId, username, null, issue);
                        break;
                }
            }
            sql.close();
        } catch (GenericDataSourceException e) {
            e.printStackTrace();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public static List<ProjectResourceAllocation.UserProjectResourceAllocation> getUserResourceAllocations(long projectId) {
        SQLProcessor sql = new SQLProcessor("defaultDS");
        List<ProjectResourceAllocation.UserProjectResourceAllocation> userProjectResourceAllocations =
                new ArrayList<ProjectResourceAllocation.UserProjectResourceAllocation>();
        try {
            sql.prepareStatement("SELECT collaborator username, from_date, to_date, seconds seconds_per_day " +
                    "FROM AO_86ED1B_TIMEPLAN " +
                    "WHERE plan_type='project' AND plan_id=" + projectId);
            ResultSet rs = sql.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                Timestamp fromDate = rs.getTimestamp("from_date");
                Timestamp toDate = rs.getTimestamp("to_date");
                long secondsPerDay = rs.getLong("seconds_per_day");
                CustomPlan plan = new CustomPlan(fromDate, toDate, secondsPerDay);
                userProjectResourceAllocations = addUserResourceAllocationBK(userProjectResourceAllocations, username, plan, null);
            }

            sql.prepareStatement("SELECT i.id, i.summary name, i.pkey key, t.pname type, i.timeoriginalestimate, i.assignee, c.cfname field, c.datevalue " +
                    "FROM JIRAISSUE i JOIN PROJECT p on i.project = p.id and p.id=" + projectId +
                    "LEFT JOIN (SELECT cf.id, cf.cfname, cfv.issue, cfv.datevalue INTO c " +
                    "           FROM CUSTOMFIELDVALUE cfv, CUSTOMFIELD cf " +
                    "           WHERE cfv.customfield = cf.id) c " +
                    "   ON i.id = c.issue " +
                    "JOIN ISSUETYPE t ON i.issuetype = t.id");
            rs =  sql.executeQuery();
            while(rs.next()){
                String username = rs.getString("assignee");
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String key = rs.getString("key");
                String type = rs.getString("type");
                long timeOriginalEstimate = rs.getLong("timeoriginalestimate");
                String field = rs.getString("field");
                Timestamp start = null, end = null;
                if (field != null && field.toLowerCase().contains("start")) {
                    start = rs.getTimestamp("datevalue");
                } else if (field != null) {
                    end = rs.getTimestamp("datevalue");
                }
                CustomIssue issue = new CustomIssue(id, name, key, type, timeOriginalEstimate, start, end);
                userProjectResourceAllocations = addUserResourceAllocationBK(userProjectResourceAllocations, username, null, issue);
            }
            sql.close();
        } catch (GenericDataSourceException e) {
            e.printStackTrace();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userProjectResourceAllocations;
    }

    public static List<ProjectResourceAllocation> addProjectResourceAllocation(
            List<ProjectResourceAllocation> projectResourceAllocations,
            long projectId,
            String username,
            CustomPlan plan,
            CustomIssue issue) {
        ProjectResourceAllocation tmpProject = new ProjectResourceAllocation(getProject(projectId), null);
        ProjectResourceAllocation projectResourceAllocation = projectResourceAllocations.get(
                projectResourceAllocations.indexOf(tmpProject));
        ProjectResourceAllocation.UserProjectResourceAllocation userProjectResourceAllocation =
                new ProjectResourceAllocation.UserProjectResourceAllocation(username, null, null);
        if (projectResourceAllocation.getUserProjectResourceAllocations() == null) {
            projectResourceAllocation.setUserProjectResourceAllocations(new ArrayList<ProjectResourceAllocation.UserProjectResourceAllocation>());
        }
        if (!projectResourceAllocation.getUserProjectResourceAllocations().contains(userProjectResourceAllocation)) {
            projectResourceAllocation.getUserProjectResourceAllocations().add(userProjectResourceAllocation);
        }

        if (plan != null) {
            projectResourceAllocation.getUserProjectResourceAllocations().get(
                    projectResourceAllocation.getUserProjectResourceAllocations().indexOf(userProjectResourceAllocation))
                    .addPlan(plan);
        }

        if (issue != null) {
            projectResourceAllocation.getUserProjectResourceAllocations().get(
                    projectResourceAllocation.getUserProjectResourceAllocations().indexOf(userProjectResourceAllocation))
                    .addIssue(issue);
        }

        return projectResourceAllocations;
    }

    public static List<UserResourceAllocation> addUserResourceAllocation(
            List<UserResourceAllocation> userResourceAllocations,
            long projectId,
            String username,
            CustomPlan plan,
            CustomIssue issue) {
        UserResourceAllocation.ProjectUserResourceAllocation projectUserResourceAllocation =
                new UserResourceAllocation.ProjectUserResourceAllocation(getProject(projectId), null, null);
        UserResourceAllocation userResourceAllocation = new UserResourceAllocation(username, null);
        if (!userResourceAllocations.contains(userResourceAllocation)) {
            userResourceAllocations.add(userResourceAllocation);
        }
        userResourceAllocation = userResourceAllocations.get(
                userResourceAllocations.indexOf(userResourceAllocation));
        if (userResourceAllocation.getProjectUserResourceAllocations() == null) {
            userResourceAllocation.setProjectUserResourceAllocations(new ArrayList<UserResourceAllocation.ProjectUserResourceAllocation>());
        }
        if (!userResourceAllocation.getProjectUserResourceAllocations().contains(projectUserResourceAllocation)) {
            userResourceAllocation.getProjectUserResourceAllocations().add(projectUserResourceAllocation);
        }

        if (plan != null) {
            userResourceAllocation.getProjectUserResourceAllocations().get(
                    userResourceAllocation.getProjectUserResourceAllocations().indexOf(projectUserResourceAllocation))
                    .addPlan(plan);
        }

        if (issue != null) {
            userResourceAllocation.getProjectUserResourceAllocations().get(
                    userResourceAllocation.getProjectUserResourceAllocations().indexOf(projectUserResourceAllocation))
                    .addIssue(issue);
        }

        return userResourceAllocations;
    }

    public static List<ProjectResourceAllocation.UserProjectResourceAllocation> addUserResourceAllocationBK(
            List<ProjectResourceAllocation.UserProjectResourceAllocation> userProjectResourceAllocations,
            String username,
            CustomPlan plan,
            CustomIssue issue) {
        if (plan != null) {
            List<CustomPlan> plans = new ArrayList<CustomPlan>();
            plans.add(plan);
            ProjectResourceAllocation.UserProjectResourceAllocation userProjectResourceAllocation =
                    new ProjectResourceAllocation.UserProjectResourceAllocation(username, plans, null);
            if (userProjectResourceAllocations.contains(userProjectResourceAllocation)) {
                userProjectResourceAllocations.get(userProjectResourceAllocations.indexOf(userProjectResourceAllocation)).addPlan(plan);
            } else {
                userProjectResourceAllocations.add(userProjectResourceAllocation);
            }
        }

        if (issue != null) {
            List<CustomIssue> issues = new ArrayList<CustomIssue>();
            issues.add(issue);
            ProjectResourceAllocation.UserProjectResourceAllocation userProjectResourceAllocation =
                    new ProjectResourceAllocation.UserProjectResourceAllocation(username, null, issues);
            if (userProjectResourceAllocations.contains(userProjectResourceAllocation)) {
                userProjectResourceAllocations.get(userProjectResourceAllocations.indexOf(userProjectResourceAllocation)).addIssue(issue);
            } else {
                userProjectResourceAllocations.add(userProjectResourceAllocation);
            }
        }

        return userProjectResourceAllocations;
    }
}
