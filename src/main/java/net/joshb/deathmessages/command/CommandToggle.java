package net.joshb.deathmessages.command;

import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.UserData;
import net.joshb.deathmessages.enums.Permission;
import net.joshb.deathmessages.manager.PlayerManager;
import org.bukkit.entity.Player;

public class CommandToggle extends DeathMessagesCommand {


    @Override
    public String command() {
        return "toggle";
    }

    @Override
    public void onCommand(Player p, String[] args) {
        if(!p.hasPermission(Permission.DEATHMESSAGES_COMMAND_TOGGLE.getValue())){
            p.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        PlayerManager pm = PlayerManager.getPlayer(p);
        boolean b = UserData.getInstance().getConfig().getBoolean(p.getUniqueId().toString() + ".messages-enabled");
        if(b){
            pm.setMessagesEnabled(false);
            p.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Toggle.Toggle-Off"));
        } else {
            pm.setMessagesEnabled(true);
            p.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Toggle.Toggle-On"));
        }
    }
}
