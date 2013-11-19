package com.atlassian.plugins.tutorial;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.projectroles.ProjectRoleService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.actor.UserRoleActorFactory;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static com.atlassian.jira.rest.v1.util.CacheControl.NO_CACHE;

/**
 * Created by IntelliJ IDEA.
 * User: datlt2
 * Date: 6/24/13
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/fsoft-sync-roles-service")
@AnonymousAllowed
@Produces(MediaType.APPLICATION_JSON)
public class MyService {

    private WebResourceManager webResourceManager;

    MyService(WebResourceManager webResourceManager) {
        this.webResourceManager = webResourceManager;
    }

    @POST @Consumes(MediaType.APPLICATION_JSON)
    @Path("/syncTempoProject")
    public Response syncTempoProject(final SyncTempoProjectBean bean) {

        JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();

        ProjectRoleService projectRoleService = (ProjectRoleService) ComponentManager.getComponentInstanceOfType(ProjectRoleService.class);
        SimpleErrorCollection errorCollection = new SimpleErrorCollection();
        List<ProjectRole> roles = new ArrayList<ProjectRole>(projectRoleService.getProjectRoles(authenticationContext.getLoggedInUser(), errorCollection));

        SampleObject sample = new SampleObject();

        if (roles != null && roles.size() > 0) {
            sample = new SampleObject(roles.get(0).getName());
        }

        List<String> users = new ArrayList<String>();
        users.add(bean.getUsername());

        ProjectManager projectManager = (ProjectManager) ComponentManager.getComponentInstanceOfType(ProjectManager.class);

        List<Project> projects = projectManager.getProjectObjects();

        projectRoleService.addActorsToProjectRole(authenticationContext.getLoggedInUser(),
                users,
                roles.get(0),
                projects.get(0),
                UserRoleActorFactory.TYPE, errorCollection);

        return Response.ok(sample).cacheControl(NO_CACHE).build();
    }

    @POST @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Path("/addRole")
    public Response addRole(@QueryParam("projectId") String projectId, final RoleOfUser roleOfUser) {
        JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();

        ProjectRoleService projectRoleService = (ProjectRoleService) ComponentManager.getComponentInstanceOfType(ProjectRoleService.class);
        SimpleErrorCollection errorCollection = new SimpleErrorCollection();
        List<ProjectRole> roles = new ArrayList<ProjectRole>(projectRoleService.getProjectRoles(authenticationContext.getLoggedInUser(), errorCollection));
        SampleObject sample = new SampleObject();

        List<String> users = new ArrayList<String>();
        users.add(roleOfUser.getUserName());

        ProjectManager projectManager = (ProjectManager) ComponentManager.getComponentInstanceOfType(ProjectManager.class);

        Project project = projectManager.getProjectObj(new Long(projectId));

        if (roles != null && roles.size() > 0) {
            sample = new SampleObject(roles.get(0).getName());
        }

        for(int i = 0;i< roles.size();i++){
            ProjectRole role = projectRoleService.getProjectRoleByName(authenticationContext.getLoggedInUser(), roles.get(i).getName() , errorCollection);
            projectRoleService.removeActorsFromProjectRole(authenticationContext.getLoggedInUser(),
                    users,
                    role,
                    project,
                    UserRoleActorFactory.TYPE,
                    errorCollection);
        }

        for(int i = 0; i < roleOfUser.getRoles().size();i++){
            ProjectRole role = projectRoleService.getProjectRoleByName(authenticationContext.getLoggedInUser(), roleOfUser.getRoles().get(i) , errorCollection);
            projectRoleService.addActorsToProjectRole(authenticationContext.getLoggedInUser(),
                    users,
                    role,
                    project,
                    UserRoleActorFactory.TYPE, errorCollection);
        }

        return Response.ok(sample).cacheControl(NO_CACHE).build();
    }

    @POST @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Path("/syncRole")
    public Response syncRole(@QueryParam("projectId") long projectId, final List<RoleAddRemoveOfUser> listRoleAddRemoveOfUsers) {
        JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();

        ProjectRoleService projectRoleService = (ProjectRoleService) ComponentManager.getComponentInstanceOfType(ProjectRoleService.class);
        SimpleErrorCollection errorCollection = new SimpleErrorCollection();
        //List<ProjectRole> roles = new ArrayList<ProjectRole>(projectRoleService.getProjectRoles(authenticationContext.getLoggedInUser(), errorCollection));
        SampleObject sample = new SampleObject();

        ProjectManager projectManager = (ProjectManager) ComponentManager.getComponentInstanceOfType(ProjectManager.class);

		Project project = projectManager.getProjectObj(new Long(projectId));

        for(int i = 0; i < listRoleAddRemoveOfUsers.size(); i++) {
            List<String> users = new ArrayList<String>();
            users.add(listRoleAddRemoveOfUsers.get(i).getUserName());
            List<String> addRoles = listRoleAddRemoveOfUsers.get(i).getAddRoles();
            for (int j=0; j < addRoles.size(); j++) {
                ProjectRole role = projectRoleService.getProjectRoleByName(authenticationContext.getLoggedInUser(), addRoles.get(j) , errorCollection);
                projectRoleService.addActorsToProjectRole(authenticationContext.getLoggedInUser(),
                    users,
                    role,
                    project,
                    UserRoleActorFactory.TYPE, errorCollection);
            }

            List<String> removeRoles = listRoleAddRemoveOfUsers.get(i).getRemoveRoles();
            for (int j=0; j < removeRoles.size(); j++) {
                ProjectRole role = projectRoleService.getProjectRoleByName(authenticationContext.getLoggedInUser(), removeRoles.get(j) , errorCollection);
                projectRoleService.removeActorsFromProjectRole(authenticationContext.getLoggedInUser(),
                    users,
                    role,
                    project,
                    UserRoleActorFactory.TYPE,
                    errorCollection);
            }
        }

        return Response.ok(sample).cacheControl(NO_CACHE).build();
    }

    @XmlRootElement
    public static class SyncTempoProjectBean {
        private long projectId;
        private String username;

        public long getProjectId() {
            return projectId;
        }

        public void setProjectId(long projectId) {
            this.projectId = projectId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    @XmlRootElement
    public static class SampleObject {
        @XmlElement
        private String url;

        @SuppressWarnings({"UnusedDeclaration", "unused"})
        private SampleObject() {
        }

        SampleObject(final String url) {
            this.url = url;
        }

        public String getURL() {
            return url;
        }
    }
}
