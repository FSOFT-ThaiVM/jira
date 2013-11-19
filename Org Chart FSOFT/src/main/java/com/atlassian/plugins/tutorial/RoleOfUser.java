package com.atlassian.plugins.tutorial;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: thaivm
 * Date: 7/2/13
 * Time: 1:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoleOfUser {

    private String userName;
    private List<String> roles;

    RoleOfUser() {

    }

    RoleOfUser(final String userName, List<String> roles){
        this.userName = userName;
        this.roles = roles;
    }

    public String getUserName() {
        return userName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void addRole(String role) {
        boolean isExist = false;
        if (roles != null) {
            for (int i = 0; i < roles.size() && !isExist; i++) {
                if (roles.get(i).equals(role)) {
                    isExist = true;
                }
            }
        } else {
            roles = new ArrayList<String>();
        }
        if (!isExist) {
            roles.add(role);
        }
    }

    @Override
    public boolean equals(Object obj) {
        RoleOfUser roleOfUser = (RoleOfUser) obj;
        if (this.userName.equals(roleOfUser.userName)) {
            return true;
        }
        return false;
    }
}
