/*
 * BungeeEssentials: Full customization of a few necessary features for your server!
 * Copyright (C) 2016 David Shen (PantherMan594)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pantherman594.gssentials.database;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by david on 7/30.
 */
@SuppressWarnings("unchecked")
public class PlayerData extends Database {

    public PlayerData() {
        super("playerdata", "(" +
                "`uuid` varchar(32) NOT NULL," +
                "`lastname` varchar(32) NOT NULL," +
                "`ip` varchar(32) NOT NULL," +
                "`friends` varchar(32) NOT NULL," +
                "`outRequests` varchar(32) NOT NULL," +
                "`inRequests` varchar(32) NOT NULL," +
                "`ignores` varchar(32) NOT NULL," +
                "`hidden` int(1) NOT NULL," +
                "`spy` int(1) NOT NULL," +
                "`cSpy` int(1) NOT NULL," +
                "`globalChat` int(1) NOT NULL," +
                "`staffChat` int(1) NOT NULL," +
                "`muted` int(1) NOT NULL," +
                "`msging` int(1) NOT NULL," +
                "PRIMARY KEY (`uuid`)" +
                ");");
    }

    public boolean createDataNotExist(String uuid) {

        if (getData("uuid", uuid, "uuid") != null) {
            return true;
        }

        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));

        if (p == null && !uuid.equals("CONSOLE")) {
            return false;
        }

        try (
                Connection conn = getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO " + dbName +
                        " (uuid, lastname, ip, friends, outRequests, inRequests, ignores, hidden, spy, cSpy, globalChat, staffChat, muted, msging) " +
                        "VALUES " +
                        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)")
        ) {
            if (uuid.equals("CONSOLE")) {
                setValues(ps, uuid, "Console", "127.0.0.1");
            } else {
                setValues(ps, uuid, p.getName(), p.getAddress().getAddress().getHostAddress());
            }
            insertDefaults(ps);
            ps.executeUpdate();
            com.pantherman594.gssentials.PlayerData.convertPlayerData(uuid);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void insertDefaults(PreparedStatement ps) throws SQLException {
        setValues(4, ps, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), false, false, false, false, false, false, false);
    }

    private Object getData(String uuid, String label) {
        return getData("uuid", uuid, label);
    }

    private void setData(String uuid, String label, Object labelVal) {
        setData("uuid", uuid, label, labelVal);
    }

    public String getIp(String uuid) {
        return (String) getData(uuid, "ip");
    }

    public void setIp(String uuid, String ip) {
        setData(uuid, "ip", ip);
    }

    public List<String> getFriends(String uuid) {
        return (List<String>) getData(uuid, "friends");
    }

    public void addFriend(String uuid, String friend) {
        List<String> friends = getFriends(uuid);
        friends.add(friend);
        setData(uuid, "friends", friends);
    }

    public void removeFriend(String uuid, String friend) {
        List<String> friends = getFriends(uuid);
        friends.remove(friend);
        setData(uuid, "friends", friends);
    }

    public List<String> getOutRequests(String uuid) {
        return (List<String>) getData(uuid, "outRequests");
    }

    public void addOutRequest(String uuid, String friend) {
        List<String> friends = getOutRequests(uuid);
        friends.add(friend);
        setData(uuid, "outRequests", friends);
    }

    public void removeOutRequest(String uuid, String friend) {
        List<String> friends = getOutRequests(uuid);
        friends.remove(friend);
        setData(uuid, "outRequests", friends);
    }

    public List<String> getInRequests(String uuid) {
        return (List<String>) getData(uuid, "inRequests");
    }

    public void addInRequest(String uuid, String friend) {
        List<String> friends = getInRequests(uuid);
        friends.add(friend);
        setData(uuid, "inRequests", friends);
    }

    public void removeInRequest(String uuid, String friend) {
        List<String> friends = getInRequests(uuid);
        friends.remove(friend);
        setData(uuid, "inRequests", friends);
    }

    public boolean isMuted(String uuid) {
        return (Boolean) getData(uuid, "muted");
    }

    public void setMuted(String uuid, boolean muted) {
        setData(uuid, "muted", muted);
    }

    public boolean toggleMuted(String uuid) {
        boolean status = !isMuted(uuid);
        setMuted(uuid, status);
        return status;
    }

    public boolean isIgnored(String uuid, String ignoreUuid) {
        List<String> ignoreList = (List<String>) getData(uuid, "ignores");
        return ignoreList.contains(ignoreUuid);
    }

    public void setIgnored(String uuid, String ignoreUuid, boolean status) {
        List<String> ignoreList = (List<String>) getData(uuid, "ignores");
        if (status) {
            ignoreList.add(ignoreUuid);
        } else {
            ignoreList.remove(ignoreUuid);
        }
    }

    public boolean toggleIgnore(String uuid, String ignoreUuid) {
        boolean status = !isIgnored(uuid, ignoreUuid);
        setIgnored(uuid, ignoreUuid, status);
        return status;
    }

    public boolean isHidden(String uuid) {
        return (Boolean) getData(uuid, "hidden");
    }

    public void setHidden(String uuid, boolean hidden) {
        setData(uuid, "hidden", hidden);
    }

    public boolean toggleHidden(String uuid) {
        boolean status = !isHidden(uuid);
        setHidden(uuid, status);
        return status;
    }

    public boolean isSpy(String uuid) {
        return (Boolean) getData(uuid, "spy");
    }

    public void setSpy(String uuid, boolean spy) {
        setData(uuid, "spy", spy);
    }

    public boolean toggleSpy(String uuid) {
        boolean status = !isSpy(uuid);
        setSpy(uuid, status);
        return status;
    }

    public boolean isCSpy(String uuid) {
        return (Boolean) getData(uuid, "cSpy");
    }

    public void setCSpy(String uuid, boolean cSpy) {
        setData(uuid, "cSpy", cSpy);
    }

    public boolean toggleCSpy(String uuid) {
        boolean status = !isCSpy(uuid);
        setCSpy(uuid, status);
        return status;
    }

    public boolean isGlobalChat(String uuid) {
        return (Boolean) getData(uuid, "globalChat");
    }

    public void setGlobalChat(String uuid, boolean globalChat) {
        setData(uuid, "globalChat", globalChat);
    }

    public boolean toggleGlobalChat(String uuid) {
        boolean status = !isGlobalChat(uuid);
        setGlobalChat(uuid, status);
        return status;
    }

    public boolean isStaffChat(String uuid) {
        return (Boolean) getData(uuid, "staffchat");
    }

    public void setStaffChat(String uuid, boolean staffChat) {
        setData(uuid, "staffchat", staffChat);
    }

    public boolean toggleStaffChat(String uuid) {
        boolean status = !isStaffChat(uuid);
        setStaffChat(uuid, status);
        return status;
    }

    public boolean isMsging(String uuid) {
        return (Boolean) getData(uuid, "msging");
    }

    public void setMsging(String uuid, boolean msging) {
        setData(uuid, "msging", msging);
    }

    public boolean toggleMsging(String uuid) {
        boolean status = !isMsging(uuid);
        setMsging(uuid, status);
        return status;
    }

    public String getName(String uuid) {
        return (String) getData(uuid, "lastname");
    }

    public void setName(String uuid, String name) {
        setData(uuid, "lastname", name);
    }
}
