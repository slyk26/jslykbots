package com.slykbots.components.settings;

public class SettingService {
    private final SettingDao dao;

    public SettingService() {
        this.dao = new SettingDao();
    }

    public void setSetting(String guildId, String key, String value) {
        Setting s = new Setting();
        s.setGuildId(guildId);
        s.setKey(key);
        s.setValue(value);
        this.dao.update(s);
    }

    public String getSetting(String guildId, String key) {
        return this.dao.read(new ReadData(guildId, key)).getValue();
    }

}
