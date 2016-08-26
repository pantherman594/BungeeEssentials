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

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by david on 7/30.
 */
public abstract class Database {
    String dbName;
    private Connection connection;

    public Database(String dbName, String setupSql) {
        this.dbName = dbName;
        load(setupSql);
    }

    private Connection getSQLConnection() {
        File dbFile = new File(BungeeEssentials.getInstance().getDataFolder(), dbName + ".db");
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);

            return connection;

        } catch (SQLException ex) {
            BungeeEssentials.getInstance().getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            BungeeEssentials.getInstance().getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }

        return null;
    }

    public abstract boolean createDataNotExist(String keyVal);

    public List<Object> listAllData(String label) {
        List<Object> datas = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + dbName + ";");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                datas.add(rs.getObject(label));
            }

            if (datas.size() > 0) {
                return datas;
            }

        } catch (SQLException e) {
            BungeeEssentials.getInstance().getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement: ", e);
        } finally {
            close(ps, conn);
        }
        return null;
    }

    public List<Object> getDataMultiple(String key, String keyVal, String label) {
        if (key.equals("uuid") && !createDataNotExist(keyVal)) {
            return null;
        }

        List<Object> datas = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + dbName + " WHERE " + key + " = ?;");
            ps.setObject(1, keyVal);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (rs.getString(key).equals(keyVal)) {
                    datas.add(rs.getObject(label));
                }
            }

            if (datas.size() > 0) {
                return datas;
            }

        } catch (SQLException e) {
            BungeeEssentials.getInstance().getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement: ", e);
        } finally {
            close(ps, conn);
        }
        return null;
    }

    public Object getData(String key, String keyVal, String label) {
        List<Object> datas = getDataMultiple(key, keyVal, label);
        if (datas != null) {
            return datas.get(0);
        }
        return null;
    }

    public void setData(String key, String keyVal, String label, Object labelVal) {
        if (!createDataNotExist(keyVal)) {
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE " + dbName + " SET " + label + " = ? WHERE " + key + " = ?;");
            ps.setObject(1, labelVal);
            ps.setObject(2, keyVal);
            ps.executeUpdate();
        } catch (SQLException e) {
            BungeeEssentials.getInstance().getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement: ", e);
        } finally {
            close(ps, conn);
        }
    }

    void setValues(PreparedStatement ps, Object... values) throws SQLException {
        setValues(1, ps, values);
    }

    void setValues(int start, PreparedStatement ps, Object... values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            ps.setObject(i + start, values[i]);
        }
    }

    private void load(String setupSql) {
        connection = getSQLConnection();

        try {
            Statement s = connection.createStatement();
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + dbName + " " + setupSql);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void close(PreparedStatement ps, Connection conn) {
        try {
            if (ps != null)
                ps.close();
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            BungeeEssentials.getInstance().getLogger().log(Level.SEVERE, "Failed to close SQLite prepared statement: ", e);
        }
    }
}
