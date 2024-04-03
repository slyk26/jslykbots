package com.slykbots.components.settings;

import com.slykbots.components.db.DB;
import com.slykbots.components.db.dao.Dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class SettingDao extends Dao<Setting, ReadData> {
    @Override
    protected Setting mapSingleRs(ResultSet rs) throws SQLException {
        Setting s = new Setting();
        s.setId(rs.getInt(1));
        s.setGuildId(rs.getString(2));
        s.setKey(rs.getString(3));
        s.setValue(rs.getString(4));
        return s;
    }

    @Override
    public void update(Setting setting) {
        String sql = "insert into bot_settings(guild_id, setting, val) values(?,?,?) on conflict (guild_id, setting) do update set val = ?";
        try (var c = DB.connect(); var stmt = Objects.requireNonNull(c, "[SettingsDao] Connection is null").prepareStatement(sql)) {
            stmt.setString(1, setting.getGuildId());
            stmt.setString(2, setting.getKey());
            stmt.setString(3, setting.getValue());
            stmt.setString(4, setting.getValue());
            stmt.executeUpdate();
        }catch (SQLException | NullPointerException e) {
            logger.error("Cannot set Setting in db {}: {}", setting, e.getMessage());
        }
    }

    @Override
    public Setting read(ReadData data) {
        String sql = "select id, guild_id, setting, val from bot_settings where guild_id = ? and setting = ?";
        try (var c = DB.connect(); var stmt = Objects.requireNonNull(c).prepareStatement(sql)) {
            stmt.setString(1, data.getGuildId());
            stmt.setString(2, data.getKey());
            var rs = stmt.executeQuery();
            rs.next();
            return this.mapSingleRs(rs);
        }catch (SQLException | NullPointerException e) {
            logger.error("Cannot read Setting in db {}: {}", data, e.getMessage());
            return null;
        }
    }
}
