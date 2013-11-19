package com.atlassian.plugins.tutorial;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: thaivm
 * Date: 7/22/13
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class TeamResourceAllocation {
    private String name;
    private List<User> users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public TeamResourceAllocation() {

    }

    public TeamResourceAllocation(String name) {
        this.name = name;
    }

    public TeamResourceAllocation(String name, List<User> users) {
        this.name = name;
        this.users = users;
    }

    public static class User {
        private String userName;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public User() {

        }

        public User(String userName) {
            this.userName = userName;
        }
    }
}
