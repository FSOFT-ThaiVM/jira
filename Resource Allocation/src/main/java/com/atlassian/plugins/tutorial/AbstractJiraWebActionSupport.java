package com.atlassian.plugins.tutorial;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.projectroles.ProjectRoleService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.ExecutingHttpRequest;
import com.atlassian.jira.web.action.JiraWebActionSupport;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class AbstractJiraWebActionSupport extends JiraWebActionSupport
{
    protected JiraAuthenticationContext authenticationContext;
    protected ProjectRoleService projectRoleService;
    protected String projectKey;
    protected Project project;
    protected static ProjectManager projectManager;
    protected static List<Project> projects;

    protected void initRequest()
    {
        final HttpServletRequest request = ExecutingHttpRequest.get();
        request.setAttribute("com.atlassian.jira.projectconfig.util.ServletRequestProjectConfigRequestCache:project", getProject());

        if (projectManager == null) {
            projectManager = getProjectManager();
        }

        if (projects == null) {
            projects = getProjectManager().getProjectObjects();
        }
    }

    public JiraAuthenticationContext getAuthenticationContext() {
        if (authenticationContext == null) {
            authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
        }
        return authenticationContext;
    }

    public ProjectRoleService getProjectRoleService() {
        if (projectRoleService == null) {
            projectRoleService = (ProjectRoleService) ComponentManager.getComponentInstanceOfType(ProjectRoleService.class);
        }
        return projectRoleService;
    }

    public Project getProject()
    {
        if (project == null)
        {
            project = getProjectManager().getProjectObjByKey(projectKey);
        }
        return project;
    }

    public static Project getProject(long projectId)
        {
            return projectManager.getProjectObj(projectId);

        }

    public String getProjectKey()
    {
        return projectKey;
    }

    public void setProjectKey(final String projectKey)
    {
        this.projectKey = projectKey;
    }

    public List<Project> getProjects() {
        if (projects == null) {
            projects = getProjectManager().getProjectObjects();
        }
        return projects;
    }
}