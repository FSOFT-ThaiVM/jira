package com.atlassian.plugins.tutorial;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: thaivm
 * Date: 7/19/13
 * Time: 11:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class RoleAddRemoveOfUser {
    private String userName;
    private List<String> addRoles;
    private List<String> removeRoles;

    public String getUserName() {
        return userName;
    }

    public List<String> getAddRoles() {
        return addRoles;
    }

    public List<String> getRemoveRoles() {
        return removeRoles;
    }

    public RoleAddRemoveOfUser() {

    }

    public RoleAddRemoveOfUser(final String userName, List<String> addRoles, List<String> removeRoles) {
        this.userName = userName;
        this.addRoles = addRoles;
        this.removeRoles = removeRoles;
    }
}
