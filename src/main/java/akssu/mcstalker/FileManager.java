package akssu.mcstalker;

import akssu.mcstalker.setting.Setting;
import net.minecraft.client.MinecraftClient;

import java.io.*;

public enum FileManager
{
    INSTANCE;

    public File MCStalkerfile;
    public File MCStalkerSettings;

    FileManager()
    {
        this.MCStalkerfile = new File(MinecraftClient.getInstance().runDirectory.getPath() + File.separator + MCStalker.NAME);
        if(!this.MCStalkerfile.exists()) this.MCStalkerfile.mkdirs();

        this.MCStalkerSettings = new File(MinecraftClient.getInstance().runDirectory.getPath() + File.separator + MCStalker.NAME + File.separator + "Settings");
        if(!this.MCStalkerSettings.exists()) this.MCStalkerSettings.mkdirs();
    }

    public void
    saveSettingsList()
    {
        try
        {
            File file = new File(MCStalkerSettings.getAbsolutePath(), "Setting.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for(Setting i : MCStalker.settings.settingsList)
            {
                Setting slider = (Setting) i;
                out.write(slider.name + ":" + slider.get() + "\r\n");
            }
            out.close();
        }
        catch(Exception e)
        {
        }


    }

    public void
    loadSettingsList()
    {
        try
        {
            File file = new File(MCStalkerSettings.getAbsolutePath(), "Setting.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null)
            {
                String curLine = line.trim();
                String name = curLine.split(":")[0];
                String isOn = curLine.split(":")[1];
                for(Setting setting : MCStalker.settings.settingsList)
                {
                    if(setting.name.equals(name))
                    {
                        ((Setting) setting).set(Boolean.parseBoolean(isOn));
                    }
                }
            }
            br.close();
        }
        catch(Exception e)
        {
            saveSettingsList();
        }


    }

}


