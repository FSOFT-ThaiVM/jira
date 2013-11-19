package com.atlassian.plugins.tutorial;

import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.util.SimpleErrorCollection;
import org.ofbiz.core.entity.GenericDataSourceException;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.jdbc.SQLProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: datlt2
 * Date: 6/28/13
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class SyncTempoProjectAction extends AbstractJiraWebActionSupport {
    private List<RoleOfUser> listRoleOfUser;
    private List<ProjectRole> projectRoles;

    public List<RoleOfUser> getListRoleOfUser() {
        return listRoleOfUser;
    }

    public List<ProjectRole> getProjectRoles() {
        if (projectRoles == null)
        {
            projectRoles = new ArrayList<ProjectRole>(
                    getProjectRoleService().getProjectRoles(
                            getAuthenticationContext().getLoggedInUser(), new SimpleErrorCollection()));
        }
        return projectRoles;
    }

    @Override
	public String doDefault() throws Exception {

        initRequest();
        this.listRoleOfUser = getListUserOfTempo(super.getProject().getId());
		return SUCCESS;
	}

    public static List<RoleOfUser> getListUserOfTempo(long projectId) {
        SQLProcessor sql = new SQLProcessor("defaultDS");
        List<RoleOfUser> listRoleOfUser = new ArrayList<RoleOfUser>();
        try {
//            sql.prepareStatement("SELECT DISTINCT collaborator username, c.name role " +
//                    "FROM AO_86ED1B_TIMEPLAN  A  LEFT JOIN PROJECTROLEACTOR  B " +
//                    "ON A.collaborator = B.roletypeparameter " +
//                    "LEFT JOIN PROJECTROLE C ON B.projectroleid = C.id " +
//                    "WHERE plan_id = " + projectId +
//                    "AND  plan_type = 'project'  " +
//                    "AND (roletype = 'atlassian-user-role-actor' OR roletype IS NULL)");
            sql.prepareStatement("SELECT  DISTINCT COLLABORATOR  username, B.name role " +
                    "FROM PROJECTROLEACTOR A, PROJECTROLE  B, AO_86ED1B_TIMEPLAN C " +
                    "WHERE C.COLLABORATOR = A.ROLETYPEPARAMETER " +
                    "AND A.PROJECTROLEID = B.ID AND A.PID = '" + projectId + "' " +
                    "AND (A.ROLETYPE = 'atlassian-user-role-actor' or A.ROLETYPE is null)");
            ResultSet rs=  sql.executeQuery();
            while(rs.next()){
                listRoleOfUser = addRoleForUser(listRoleOfUser, rs.getString("username"), rs.getString("role"));
            }

            sql.prepareStatement("SELECT DISTINCT COLLABORATOR  username " +
                    "FROM AO_86ED1B_TIMEPLAN C " +
                    "WHERE  C.PLAN_ID = '" + projectId + "' " +
                    "AND COLLABORATOR NOT IN " +
                        "(SELECT  DISTINCT COLLABORATOR  username " +
                        "FROM PROJECTROLEACTOR A, PROJECTROLE  B, AO_86ED1B_TIMEPLAN C  " +
                        "WHERE C.COLLABORATOR = A.ROLETYPEPARAMETER " +
                        "AND A.PROJECTROLEID = B.ID " +
                        "AND A.PID = '" + projectId + "' " +
                        "AND (A.ROLETYPE = 'atlassian-user-role-actor' or A.ROLETYPE is null)) ");
            ResultSet rs1=  sql.executeQuery();
            while(rs1.next()){
                listRoleOfUser = addRoleForUser(listRoleOfUser, rs1.getString("username"), null);
            }

        } catch (GenericDataSourceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (GenericEntityException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return listRoleOfUser;
    }

    public static List<RoleOfUser> addRoleForUser(List<RoleOfUser> listRoleOfUser, String username, String role) {
        List<String> roles = new ArrayList<String>();
        roles.add(role);
        RoleOfUser roleOfUser = new RoleOfUser(username, roles);
        if (listRoleOfUser.contains(roleOfUser)) {
            listRoleOfUser.get(listRoleOfUser.indexOf(roleOfUser)).addRole(role);
        } else {
            listRoleOfUser.add(roleOfUser);
        }
        return listRoleOfUser;
    }
}
