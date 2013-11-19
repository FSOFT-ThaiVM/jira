package com.atlassian.plugins.tutorial;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.projectroles.ProjectRoleService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.ExecutingHttpRequest;
import com.atlassian.jira.web.action.JiraWebActionSupport;

import javax.servlet.http.HttpServletRequest;

public class AbstractJiraWebActionSupport extends JiraWebActionSupport
{
    private JiraAuthenticationContext authenticationContext;
    private ProjectRoleService projectRoleService;
    private String projectKey;
    private Project project;

    protected void initRequest()
    {
        final HttpServletRequest request = ExecutingHttpRequest.get();
        request.setAttribute("com.atlassian.jira.projectconfig.util.ServletRequestProjectConfigRequestCache:project", getProject());
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

    public String getProjectKey()
    {
        return projectKey;
    }

    public void setProjectKey(final String projectKey)
    {
        this.projectKey = projectKey;
    }
}