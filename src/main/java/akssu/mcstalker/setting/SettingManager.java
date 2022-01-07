package akssu.mcstalker.setting;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingManager
{
    public ArrayList<Setting> settingsList;
    public HashMap<String, Setting> settingsMap;

    public SettingManager()
    {
        this.settingsList = new ArrayList<>();
        this.settingsMap = new HashMap<>();
    }

    public void
    addSetting(Setting setting)
    {
        this.settingsList.add(setting);
        this.settingsMap.put(setting.name, setting);
    }


    public Setting
    getSettingByName(String name)
    {
        return this.settingsMap.get(name);
    }

}
