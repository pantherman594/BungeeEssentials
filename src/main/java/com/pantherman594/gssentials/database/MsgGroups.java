package com.pantherman594.gssentials.database;

import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.Dictionary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Created by david on 8/31.
 */

public class MsgGroups extends Database {
    private static final String SETUP_SQL = "(" +
            "`groupname` VARCHAR(36) NOT NULL," +
            "`owner` VARCHAR(36) NOT NULL," +
            "`members` TEXT NOT NULL," +
            "`invited` TEXT NOT NULL";

    public MsgGroups() {
        super("msggroups", SETUP_SQL, "groupname");
    }

    public MsgGroups(String url, String username, String password, String prefix) {
        super(prefix + "msggroups", SETUP_SQL, "groupname", url, username, password);
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
                PreparedStatement ps = conn.prepareStatement("INSERT INTO " + tableName + " (groupname, owner, members, invited) VALUES (?,?,?,?);")
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
                PreparedStatement ps = conn.prepareStatement("DELETE FROM " + tableName + " WHERE groupname = ?;")
        ) {
            setValues(ps, groupName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void convert() {
        if (isNewMySql) {
            MsgGroups oldMG = new MsgGroups();
            List<Object> groups = oldMG.listAllData("groupname");
            if (groups != null && !groups.isEmpty()) {
                BungeeEssentials.getInstance().getLogger().info("New MySQL configuration found. Converting " + groups.size() + " MsgGroups...");
                for (Object groupO : groups) {
                    String groupName = (String) groupO;
                    create(groupName);
                    setOwner(groupName, oldMG.getOwner(groupName));
                    setMembers(groupName, oldMG.getMembers(groupName));
                    setInvited(groupName, oldMG.getInvited(groupName));
                }
                BungeeEssentials.getInstance().getLogger().info("MsgGroup conversion complete!");
            }
        } else {
            BungeeEssentials.getInstance().getLogger().info("A database conversion was requested, but no empty database was found. If you want to convert, please delete the existing MySQL database.");
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
        setMembers(groupName, members);
    }

    public void removeMember(String groupName, String uuid) {
        Set<String> members = getMembers(groupName);
        members.remove(uuid);
        if (members.isEmpty()) {
            remove(groupName);
        } else {
            setMembers(groupName, members);
        }
    }

    public void setMembers(String groupName, Set<String> members) {
        setData(groupName, "members", Dictionary.combine(";", members));
    }

    public Set<String> getInvited(String groupName) {
        return setFromString((String) getData(groupName, "invited"));
    }

    public void addInvited(String groupName, String uuid) {
        Set<String> invited = getInvited(groupName);
        invited.add(uuid);
        setInvited(groupName, invited);
    }

    public void removeInvited(String groupName, String uuid) {
        Set<String> invited = getInvited(groupName);
        invited.remove(uuid);
        setInvited(groupName, invited);
    }

    public void setInvited(String groupName, Set<String> invited) {
        setData(groupName, "invited", Dictionary.combine(";", invited));
    }
}
