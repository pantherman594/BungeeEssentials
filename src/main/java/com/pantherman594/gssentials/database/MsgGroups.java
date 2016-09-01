package com.pantherman594.gssentials.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by david on 8/31.
 */
@SuppressWarnings("unchecked")
public class MsgGroups extends Database {

    public MsgGroups() {
        super("msggroups", "(" +
                "`groupname` varchar(32) NOT NULL," +
                "`owner` varchar(32) NOT NULL," +
                "`members` varchar(32) NOT NULL," +
                "`invited` varchar(32) NOT NULL,", "groupname");
    }

    public boolean createDataNotExist(String groupName) {
        return getData(groupName, "groupname") != null;
        // Because we don't want the group being created automatically, we just return whether the group exists.
    }

    private Object getData(String groupName, String label) {
        return getData("groupname", groupName, label);
    }

    private void setData(String groupName, String label, Object labelVal) {
        setData("groupname", groupName, label, labelVal);
    }

    public boolean create(String groupName) {
        if (getData("groupname", groupName, "groupname") != null) {
            return true;
        }

        try (
                Connection conn = getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO " + dbName + " (groupname, owner, members, invited) VALUES (?,?,?);")
        ) {
            setValues(ps, groupName, "", new HashSet<String>(), new HashSet<String>());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void remove(String groupName) {
        if (getData("groupname", groupName, "groupname") == null) {
            return;
        }

        try (
                Connection conn = getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM " + dbName + " WHERE groupname = ?;")
        ) {
            setValues(ps, groupName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setName(String groupName, String newName) {
        setData(groupName, "groupname", newName);
    }

    public String getOwner(String groupName) {
        return (String) getData(groupName, "owner");
    }

    public void setOwner(String groupName, String uuid) {
        setData(groupName, "owner", uuid);
    }

    public Set<String> getMembers(String groupName) {
        return (Set<String>) getData(groupName, "members");
    }

    public void addMember(String groupName, String uuid) {
        Set<String> members = getMembers(groupName);
        members.add(uuid);
        setData(groupName, "members", members);
    }

    public void removeMember(String groupName, String uuid) {
        Set<String> members = getMembers(groupName);
        members.remove(uuid);
        if (members.isEmpty()) {
            remove(groupName);
        } else {
            setData(groupName, "members", members);
        }
    }

    public Set<String> getInvited(String groupName) {
        return (Set<String>) getData(groupName, "invited");
    }

    public void addInvited(String groupName, String uuid) {
        Set<String> invited = getInvited(groupName);
        invited.add(uuid);
        setData(groupName, "invited", invited);
    }

    public void removeInvited(String groupName, String uuid) {
        Set<String> invited = getInvited(groupName);
        invited.remove(uuid);
        if (invited.isEmpty()) {
            remove(groupName);
        } else {
            setData(groupName, "invited", invited);
        }
    }
}
