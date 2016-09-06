package com.pantherman594.gssentials.database;

import com.pantherman594.gssentials.Dictionary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by david on 8/31.
 */

public class MsgGroups extends Database {

    public MsgGroups() {
        super("msggroups", "(" +
                "`groupname` varchar(32) NOT NULL," +
                "`owner` varchar(32) NOT NULL," +
                "`members` varchar(32) NOT NULL," +
                "`invited` varchar(32) NOT NULL", "groupname");
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

        Connection conn = getSQLConnection();
        try (
                PreparedStatement ps = conn.prepareStatement("INSERT INTO " + dbName + " (groupname, owner, members, invited) VALUES (?,?,?,?);")
        ) {
            setValues(ps, groupName, "", "", "");
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

        Connection conn = getSQLConnection();
        try (
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
        addMember(groupName, uuid);
    }

    public Set<String> getMembers(String groupName) {
        return setFromString((String) getData(groupName, "members"));
    }

    public void addMember(String groupName, String uuid) {
        Set<String> members = getMembers(groupName);
        members.add(uuid);
        setData(groupName, "members", Dictionary.combine(";", members));
    }

    public void removeMember(String groupName, String uuid) {
        Set<String> members = getMembers(groupName);
        members.remove(uuid);
        if (members.isEmpty()) {
            remove(groupName);
        } else {
            setData(groupName, "members", Dictionary.combine(";", members));
        }
    }

    public Set<String> getInvited(String groupName) {
        return setFromString((String) getData(groupName, "invited"));
    }

    public void addInvited(String groupName, String uuid) {
        Set<String> invited = getInvited(groupName);
        invited.add(uuid);
        setData(groupName, "invited", Dictionary.combine(";", invited));
    }

    public void removeInvited(String groupName, String uuid) {
        Set<String> invited = getInvited(groupName);
        invited.remove(uuid);
        setData(groupName, "invited", Dictionary.combine(";", invited));
    }
}
