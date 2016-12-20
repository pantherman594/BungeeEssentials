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

import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.Dictionary;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by david on 7/30.
 */

@SuppressWarnings({"unchecked", "MismatchedQueryAndUpdateOfCollection"})
public class PlayerData extends Database {
    private static final String SETUP_SQL = "(" +
            "`uuid` VARCHAR(36) NOT NULL," +
            "`lastname` VARCHAR(32) NOT NULL," +
            "`ip` VARCHAR(32) NOT NULL," +
            "`friends` TEXT NOT NULL," +
            "`outRequests` TEXT NOT NULL," +
            "`inRequests` TEXT NOT NULL," +
            "`ignores` TEXT NOT NULL," +
            "`hidden` INT(1) NOT NULL," +
            "`spy` INT(1) NOT NULL," +
            "`cSpy` INT(1) NOT NULL," +
            "`globalChat` INT(1) NOT NULL," +
            "`staffChat` INT(1) NOT NULL," +
            "`muted` INT(1) NOT NULL," +
            "`msging` INT(1) NOT NULL";

    private Map<String, String> lastname = new HashMap<>();
    private Map<String, String> ip = new HashMap<>();
    private Map<String, String> friends = new HashMap<>();
    private Map<String, String> outRequests = new HashMap<>();
    private Map<String, String> inRequests = new HashMap<>();
    private Map<String, String> ignores = new HashMap<>();
    private Map<String, Integer> hidden = new HashMap<>();
    private Map<String, Integer> spy = new HashMap<>();
    private Map<String, Integer> cSpy = new HashMap<>();
    private Map<String, Integer> globalChat = new HashMap<>();
    private Map<String, Integer> staffChat = new HashMap<>();
    private Map<String, Integer> muted = new HashMap<>();
    private Map<String, Integer> msging = new HashMap<>();

    public PlayerData() {
        super("playerdata", SETUP_SQL, "uuid");
    }

    public PlayerData(String url, String username, String password, String prefix) {
        super(prefix + "playerdata", SETUP_SQL, "uuid", url, username, password);
    }

    public void convert() {
        if (isNewMySql) {
            PlayerData oldPD = new PlayerData();
            List<Object> uuids = oldPD.listAllData("uuid");
            if (uuids != null && !uuids.isEmpty()) {
                BungeeEssentials.getInstance().getLogger().info("New MySQL configuration found. Converting " + uuids.size() + " PlayerDatas...");
                for (Object uuidO : uuids) {
                    String uuid = (String) uuidO;
                    setName(uuid, oldPD.getName(uuid));
                    setIp(uuid, oldPD.getIp(uuid));
                    setFriends(uuid, oldPD.getFriends(uuid));
                    setOutRequests(uuid, oldPD.getOutRequests(uuid));
                    setInRequests(uuid, oldPD.getInRequests(uuid));
                    setIgnores(uuid, oldPD.getIgnores(uuid));
                    setHidden(uuid, oldPD.isHidden(uuid));
                    setSpy(uuid, oldPD.isSpy(uuid));
                    setCSpy(uuid, oldPD.isCSpy(uuid));
                    setGlobalChat(uuid, oldPD.isGlobalChat(uuid));
                    setStaffChat(uuid, oldPD.isStaffChat(uuid));
                    setMuted(uuid, oldPD.isMuted(uuid));
                    setMsging(uuid, oldPD.isMsging(uuid));
                }
                BungeeEssentials.getInstance().getLogger().info("PlayerData conversion complete!");
            }
        } else {
            BungeeEssentials.getInstance().getLogger().info("A database conversion was requested, but no empty database was found. If you want to convert, please delete the existing MySQL database.");
        }
    }

    public boolean createDataNotExist(String uuid) {

        if (getData("uuid", uuid, "uuid") != null) {
            return true;
        }

        ProxiedPlayer p = null;
        if (!uuid.equals("CONSOLE")) {
            p = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
        }

        Connection conn = getSQLConnection();
        try (
                PreparedStatement ps = conn.prepareStatement("INSERT INTO " + tableName +
                        " (uuid, lastname, ip, friends, outRequests, inRequests, ignores, hidden, spy, cSpy, globalChat, staffChat, muted, msging) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);")
        ) {
            if (uuid.equals("CONSOLE")) {
                setValues(ps, uuid, "Console", "127.0.0.1");
            } else if (p != null) {
                setValues(ps, uuid, p.getName(), p.getAddress().getAddress().getHostAddress());
            } else {
                setValues(ps, uuid, "", "");
            }
            insertDefaults(ps);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void insertDefaults(PreparedStatement ps) throws SQLException {
        setValues(4, ps, "", "", "", "", false, false, false, false, false, false, true);
    }

    private Map<String, Map> getData() {
        Map<String, Map> data = new HashMap<>();
        data.put("lastname", lastname);
        data.put("ip", ip);
        data.put("friends", friends);
        data.put("outRequests", outRequests);
        data.put("inRequests", inRequests);
        data.put("ignores", ignores);
        data.put("hidden", hidden);
        data.put("spy", spy);
        data.put("cSpy", cSpy);
        data.put("globalChat", globalChat);
        data.put("staffChat", staffChat);
        data.put("muted", muted);
        data.put("msging", msging);
        return data;
    }

    private Object getData(String uuid, String label) {
        if (getData().containsKey(label)) {
            if (!getData().get(label).containsKey(uuid)) {
                getData().get(label).put(uuid, getData("uuid", uuid, label));
            }
            return getData().get(label).get(uuid);
        }
        return getData("uuid", uuid, label);
    }

    private void setData(String uuid, String label, Object labelVal) {
        if (getData().containsKey(label)) {
            getData().get(label).put(uuid, labelVal);
        }
        setData("uuid", uuid, label, labelVal);
    }

    public String getIp(String uuid) {
        return (String) getData(uuid, "ip");
    }

    public void setIp(String uuid, String ip) {
        setData(uuid, "ip", ip);
    }

    public Set<String> getFriends(String uuid) {
        return setFromString((String) getData(uuid, "friends"));
    }

    public void addFriend(String uuid, String friend) {
        Set<String> friends = getFriends(uuid);
        friends.add(friend);
        setFriends(uuid, friends);
    }

    public void removeFriend(String uuid, String friend) {
        Set<String> friends = getFriends(uuid);
        friends.remove(friend);
        setFriends(uuid, friends);
    }

    public void setFriends(String uuid, Set<String> friends) {
        setData(uuid, "friends", Dictionary.combine(";", friends));
    }

    public Set<String> getOutRequests(String uuid) {
        return setFromString((String) getData(uuid, "outRequests"));
    }

    public void addOutRequest(String uuid, String outRequest) {
        Set<String> outRequests = getOutRequests(uuid);
        outRequests.add(outRequest);
        setOutRequests(uuid, outRequests);
    }

    public void removeOutRequest(String uuid, String outRequest) {
        Set<String> outRequests = getOutRequests(uuid);
        outRequests.remove(outRequest);
        setOutRequests(uuid, outRequests);
    }

    public void setOutRequests(String uuid, Set<String> outRequests) {
        setData(uuid, "outRequests", Dictionary.combine(";", outRequests));
    }

    public Set<String> getInRequests(String uuid) {
        return setFromString((String) getData(uuid, "inRequests"));
    }

    public void addInRequest(String uuid, String inRequest) {
        Set<String> inRequests = getInRequests(uuid);
        inRequests.add(inRequest);
        setInRequests(uuid, inRequests);
    }

    public void removeInRequest(String uuid, String inRequest) {
        Set<String> inRequests = getInRequests(uuid);
        inRequests.remove(inRequest);
        setInRequests(uuid, inRequests);
    }

    public void setInRequests(String uuid, Set<String> inRequests) {
        setData(uuid, "inRequests", Dictionary.combine(";", inRequests));
    }

    public boolean isMuted(String uuid) {
        return (int) getData(uuid, "muted") == 1;
    }

    public void setMuted(String uuid, boolean muted) {
        setData(uuid, "muted", muted ? 1 : 0);
    }

    public boolean toggleMuted(String uuid) {
        boolean status = !isMuted(uuid);
        setMuted(uuid, status);
        return status;
    }

    public Set<String> getIgnores(String uuid) {
        return setFromString((String) getData(uuid, "ignores"));
    }

    public boolean isIgnored(String uuid, String ignoreUuid) {
        return getIgnores(uuid).contains(ignoreUuid);
    }

    public void setIgnored(String uuid, String ignoreUuid, boolean status) {
        Set<String> ignoreSet = setFromString((String) getData(uuid, "ignores"));
        if (status) {
            ignoreSet.add(ignoreUuid);
        } else {
            ignoreSet.remove(ignoreUuid);
        }
        setIgnores(uuid, ignoreSet);
    }

    public void setIgnores(String uuid, Set<String> ignoreSet) {
        setData(uuid, "ignores", Dictionary.combine(";", ignoreSet));
    }

    public boolean toggleIgnore(String uuid, String ignoreUuid) {
        boolean status = !isIgnored(uuid, ignoreUuid);
        setIgnored(uuid, ignoreUuid, status);
        return status;
    }

    public boolean isHidden(String uuid) {
        return (int) getData(uuid, "hidden") == 1;
    }

    public void setHidden(String uuid, boolean hidden) {
        setData(uuid, "hidden", hidden ? 1 : 0);
    }

    public boolean toggleHidden(String uuid) {
        boolean status = !isHidden(uuid);
        setHidden(uuid, status);
        return status;
    }

    public boolean isSpy(String uuid) {
        return (int) getData(uuid, "spy") == 1;
    }

    public void setSpy(String uuid, boolean spy) {
        setData(uuid, "spy", spy ? 1 : 0);
    }

    public boolean toggleSpy(String uuid) {
        boolean status = !isSpy(uuid);
        setSpy(uuid, status);
        return status;
    }

    public boolean isCSpy(String uuid) {
        return (int) getData(uuid, "cSpy") == 1;
    }

    public void setCSpy(String uuid, boolean cSpy) {
        setData(uuid, "cSpy", cSpy ? 1 : 0);
    }

    public boolean toggleCSpy(String uuid) {
        boolean status = !isCSpy(uuid);
        setCSpy(uuid, status);
        return status;
    }

    public boolean isGlobalChat(String uuid) {
        return (int) getData(uuid, "globalChat") == 1;
    }

    public void setGlobalChat(String uuid, boolean globalChat) {
        setData(uuid, "globalChat", globalChat ? 1 : 0);
    }

    public boolean toggleGlobalChat(String uuid) {
        boolean status = !isGlobalChat(uuid);
        setGlobalChat(uuid, status);
        return status;
    }

    public boolean isStaffChat(String uuid) {
        return (int) getData(uuid, "staffChat") == 1;
    }

    public void setStaffChat(String uuid, boolean staffChat) {
        setData(uuid, "staffChat", staffChat ? 1 : 0);
    }

    public boolean toggleStaffChat(String uuid) {
        boolean status = !isStaffChat(uuid);
        setStaffChat(uuid, status);
        return status;
    }

    public boolean isMsging(String uuid) {
        return (int) getData(uuid, "msging") == 1;
    }

    public void setMsging(String uuid, boolean msging) {
        setData(uuid, "msging", msging ? 1 : 0);
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
