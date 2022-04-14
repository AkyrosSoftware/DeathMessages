package dev.mrshawn.deathmessages.config;

import dev.mrshawn.deathmessages.DeathMessages;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.Date;
import java.util.logging.Level;

public class UserData {

    public final String fileName = "UserData";

    FileConfiguration config;

    File file;

    public UserData(){ }
    private static final UserData instance = new UserData();
    public static UserData getInstance(){
        return instance;
    }

    public FileConfiguration getConfig(){
        return config;
    }

    public void save(){
        try {
            config.save(file);
        } catch (IOException e){
            File f = new File(DeathMessages.getInstance().getDataFolder(), fileName + ".broken." + new Date().getTime());
            DeathMessages.getInstance().getLogger().log(Level.SEVERE, "Could not save: " + fileName + ".yml");
            DeathMessages.getInstance().getLogger().log(Level.SEVERE, "Regenerating file and renaming the current file to: " + f.getName());
            DeathMessages.getInstance().getLogger().log(Level.SEVERE, "You can try fixing the file with a yaml parser online!");
            file.renameTo(f);
            initialize();
        }
    }

    public void reload(){
        try {
            config.load(file);
        } catch (Exception e){
            File f = new File(DeathMessages.getInstance().getDataFolder(), fileName + ".broken." + new Date().getTime());
            DeathMessages.getInstance().getLogger().log(Level.SEVERE, "Could not reload: " + fileName + ".yml");
            DeathMessages.getInstance().getLogger().log(Level.SEVERE, "Regenerating file and renaming the current file to: " + f.getName());
            DeathMessages.getInstance().getLogger().log(Level.SEVERE, "You can try fixing the file with a yaml parser online!");
            file.renameTo(f);
            initialize();
        }
    }

    public void initialize(){

        file = new File(DeathMessages.getInstance().getDataFolder(), fileName + ".yml");

        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        save();
        reload();
    }
}
