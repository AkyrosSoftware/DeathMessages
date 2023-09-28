package dev.mrshawn.deathmessages.api;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.UserData;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileSettings;
import dev.mrshawn.deathmessages.kotlin.files.FileStore;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

	private static final FileSettings<Config> config = FileStore.INSTANCE.getCONFIG();

	private final UUID playerUUID;
	private final String playerName;
	private boolean messagesEnabled;
	private boolean isBlacklisted;
	private DamageCause damageCause;
	private Entity lastEntityDamager;
	private Entity lastExplosiveEntity;
	private Projectile lastProjectileEntity;
	private Material climbing;
	private Location explosionCauser;
	private Location location;
	private int cooldown = 0;
	private Inventory cachedInventory;

	private ScheduledTask lastEntityTask;

	private static final List<PlayerManager> players = new ArrayList<>();

	public final boolean saveUserData = config.getBoolean(Config.SAVED_USER_DATA);

	public PlayerManager(Player p) {


		this.playerUUID = p.getUniqueId();
		this.playerName = p.getName();

		if (saveUserData && !UserData.getInstance().getConfig().contains(playerUUID.toString())) {
			UserData.getInstance().getConfig().set(playerUUID + ".username", playerName);
			UserData.getInstance().getConfig().set(playerUUID + ".messages-enabled", true);
			UserData.getInstance().getConfig().set(playerUUID + ".is-blacklisted", false);
			UserData.getInstance().save();
		}
		if (saveUserData) {
			messagesEnabled = UserData.getInstance().getConfig().getBoolean(playerUUID + ".messages-enabled");
			isBlacklisted = UserData.getInstance().getConfig().getBoolean(playerUUID + ".is-blacklisted");
		} else {
			messagesEnabled = true;
			isBlacklisted = false;
		}
		this.damageCause = DamageCause.CUSTOM;
		players.add(this);
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(playerUUID);
	}

	public UUID getUUID() {
		return playerUUID;
	}

	public String getName() {
		return playerName;
	}

	public boolean getMessagesEnabled() {
		return messagesEnabled;
	}

	public void setMessagesEnabled(boolean b) {
		this.messagesEnabled = b;
		if (saveUserData) {
			UserData.getInstance().getConfig().set(playerUUID.toString() + ".messages-enabled", b);
			UserData.getInstance().save();
		}
	}

	public boolean isBlacklisted() {
		return isBlacklisted;
	}

	public void setBlacklisted(boolean b) {
		this.isBlacklisted = b;
		if (saveUserData) {
			UserData.getInstance().getConfig().set(playerUUID.toString() + ".is-blacklisted", b);
			UserData.getInstance().save();
		}
	}

	public void setLastDamageCause(DamageCause dc) {
		this.damageCause = dc;
	}

	public DamageCause getLastDamage() {
		return damageCause;
	}

	public void setLastEntityDamager(Entity e) {
		setLastExplosiveEntity(null);
		setLastProjectileEntity(null);
		this.lastEntityDamager = e;
		if (e == null) return;
		if (lastEntityTask != null) {
			lastEntityTask.cancel();
		}
		lastEntityTask = Bukkit.getGlobalRegionScheduler().runDelayed(DeathMessages.getInstance(),
				task -> setLastEntityDamager(null), config.getInt(Config.EXPIRE_LAST_DAMAGE_EXPIRE_PLAYER) * 20L);
	}

	public Entity getLastEntityDamager() {
		return lastEntityDamager;
	}

	public void setLastExplosiveEntity(Entity e) {
		this.lastExplosiveEntity = e;
	}

	public Entity getLastExplosiveEntity() {
		return lastExplosiveEntity;
	}

	public Projectile getLastProjectileEntity() {
		return lastProjectileEntity;
	}

	public void setLastProjectileEntity(Projectile lastProjectileEntity) {
		this.lastProjectileEntity = lastProjectileEntity;
	}

	public Material getLastClimbing() {
		return climbing;
	}

	public void setLastClimbing(Material climbing) {
		this.climbing = climbing;
	}

	public void setExplosionCauser(Location location) {
		this.explosionCauser = location;
	}

	public Location getExplosionCauser() {
		return explosionCauser;
	}

	public Location getLastLocation() {
		return getPlayer().getLocation();
	}

	public boolean isInCooldown() {
		return cooldown > 0;
	}

	public void setCooldown() {
		cooldown = config.getInt(Config.COOLDOWN);
		BukkitTask cooldownTask = new BukkitRunnable() {
			@Override
			public void run() {
				if (cooldown <= 0) {
					this.cancel();
				}
				cooldown--;
			}
		}.runTaskTimer(DeathMessages.getInstance(), 0, 20);
		// For Folia
		// java.lang.IllegalArgumentException: Initial delay ticks may not be <= 0
//		ScheduledTask cooldownTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(DeathMessages.getInstance(), task -> {
//				if (cooldown <= 0) {
//					task.cancel();
//				}
//				cooldown--;
//			}, 0L, 20L);
	}

	public void setCachedInventory(Inventory inventory) {
		cachedInventory = inventory;
	}

	public Inventory getCachedInventory() {
		return cachedInventory;
	}

	public static PlayerManager getPlayer(Player p) {
		for (PlayerManager pm : players) {
			if (pm.getUUID().equals(p.getUniqueId())) {
				return pm;
			}
		}
		return null;
	}

	public static PlayerManager getPlayer(UUID uuid) {
		for (PlayerManager pm : players) {
			if (pm.getUUID().equals(uuid)) {
				return pm;
			}
		}
		return null;
	}

	public void removePlayer() {
		players.remove(this);
	}
}

