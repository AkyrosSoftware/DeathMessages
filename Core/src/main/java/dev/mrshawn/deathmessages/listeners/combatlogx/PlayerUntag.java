package dev.mrshawn.deathmessages.listeners.combatlogx;

import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastDeathMessageEvent;
import dev.mrshawn.deathmessages.config.Config;
import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.listeners.EntityDeath;
import dev.mrshawn.deathmessages.utils.Assets;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class PlayerUntag implements Listener {

	@EventHandler
	public void untagPlayer(PlayerUntagEvent e) {
		Player p = e.getPlayer();
		Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(p);
		getPlayer.ifPresent(pm -> {
			UntagReason reason = e.getUntagReason();

			if (!reason.equals(UntagReason.QUIT)) return;
			int radius = (int) Config.gangs.gangsGroup.get("player").get("Radius");
			int amount = (int) Config.gangs.gangsGroup.get("player").get("Amount");
			boolean gangKill = false;

			if (Config.gangs.enableGangs) {
				int totalMobEntities = 0;
				for (Entity entities : p.getNearbyEntities(radius, radius, radius)) {
					if (entities.getType().equals(EntityType.PLAYER)) {
						totalMobEntities++;
					}
				}
				if (totalMobEntities >= amount) {
					gangKill = true;
				}
			}
			TextComponent deathMessage = Assets.get(gangKill, pm, (LivingEntity) e.getPreviousEnemies().get(0), "CombatLogX-Quit");
			BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(p, (LivingEntity) e.getPreviousEnemies().get(0), MessageType.PLAYER, deathMessage, EntityDeath.getWorlds(p), gangKill);
			Bukkit.getPluginManager().callEvent(event);
		});
		if (!getPlayer.isPresent()) {
			new PlayerManager(p);
		}
	}
}
