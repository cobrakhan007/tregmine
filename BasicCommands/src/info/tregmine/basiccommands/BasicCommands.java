package info.tregmine.basiccommands;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;

import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import info.tregmine.Tregmine; 
import info.tregmine.api.TregminePlayer;
//import info.tregmine.api.TregminePlayer;
import info.tregmine.api.encryption.BCrypt;


public class BasicCommands extends JavaPlugin {

	public final Logger log = Logger.getLogger("Minecraft");
	public Tregmine tregmine = null;
	public final BasicCommandsBlock block = new BasicCommandsBlock(this);

	public void onEnable(){
		Plugin test = this.getServer().getPluginManager().getPlugin("Tregmine");

		if(this.tregmine == null) {
			if(test != null) {
				this.tregmine = ((Tregmine)test);
			} else {
				log.info(this.getDescription().getName() + " " + this.getDescription().getVersion() + " - could not find Tregmine");
				this.getServer().getPluginManager().disablePlugin(this);
			}
		}
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, block, Priority.Highest, this);
	}

	public void onDisable(){
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();

		if(!(sender instanceof Player)) {

			if (commandName.matches("kick")) {
				Player victim = this.getServer().matchPlayer(args[0]).get(0);

				if (victim != null) {
					info.tregmine.api.TregminePlayer victimPlayer = this.tregmine.tregminePlayer.get(victim.getName());
					this.getServer().broadcastMessage("GOD kicked " + victimPlayer.getChatName() + ChatColor.AQUA + " for 1 min.");
					victim.kickPlayer("kicked by GOD.");
				}
				return true;
			}


			return false;
		}

		Player player = (Player) sender;
		info.tregmine.api.TregminePlayer tregminePlayer = this.tregmine.tregminePlayer.get(player.getName());
		boolean isAdmin = tregminePlayer.isAdmin();
		boolean isDonator = tregminePlayer.isDonator();
		boolean isMentor = tregminePlayer.getMetaBoolean("mentor");

		if (commandName.matches("password")) {
			String hash = BCrypt.hashpw(args[0], BCrypt.gensalt());
			player.sendMessage(hash);
		}

		if (commandName.matches("creative") && (tregminePlayer.isAdmin() || tregminePlayer.getMetaBoolean("builder"))) {
			final TregminePlayer tregPlayer = tregmine.getPlayer(player);
			tregPlayer.setGameMode(GameMode.CREATIVE);
			tregPlayer.sendMessage(ChatColor.YELLOW + "You are now in creative mode.");

			/*this.getServer().getScheduler().scheduleSyncDelayedTask(this,new Runnable() {
				public void run() {
					tregPlayer.sendMessage(ChatColor.YELLOW + "You are now back in survival mode.");
					tregPlayer.setGameMode(GameMode.SURVIVAL);
				}},12000L);*/
		}

		if (commandName.matches("survival") && (tregminePlayer.isAdmin() || tregminePlayer.getMetaBoolean("builder"))) {
			final TregminePlayer tregPlayer = tregmine.getPlayer(player);
			tregPlayer.setGameMode(GameMode.SURVIVAL);
			tregPlayer.sendMessage(ChatColor.YELLOW + "You are now in survival mode.");
		}


		if (commandName.matches("pos")) {
			Location loc = player.getLocation();
			Location spawn = player.getWorld().getSpawnLocation();
			double distance = info.tregmine.api.math.Distance.calc2d(spawn, loc);


			player.sendMessage(ChatColor.DARK_AQUA + "World: " + ChatColor.WHITE + player.getWorld().getName());
			this.log.info("World: " + player.getWorld().getName());
			player.sendMessage(ChatColor.DARK_AQUA + "X: " + ChatColor.WHITE + loc.getX() + ChatColor.RED + " (" + loc.getBlockX() + ")" );
			this.log.info("X: "  + loc.getX()  + " (" + loc.getBlockX() + ")" );
			player.sendMessage(ChatColor.DARK_AQUA + "Y: " + ChatColor.WHITE + loc.getY() + ChatColor.RED + " (" + loc.getBlockY() + ")" );
			this.log.info("Y: "  + loc.getY()  + " (" + loc.getBlockY() + ")" );
			player.sendMessage(ChatColor.DARK_AQUA + "Z: " + ChatColor.WHITE + loc.getZ() + ChatColor.RED + " (" + loc.getBlockZ() + ")" );
			this.log.info("Z: "  + loc.getZ()  + " (" + loc.getBlockZ() + ")" );
			player.sendMessage(ChatColor.DARK_AQUA + "Yaw: " + ChatColor.WHITE + loc.getYaw());
			this.log.info("Yaw: " +  loc.getYaw());
			player.sendMessage(ChatColor.DARK_AQUA + "Pitch: " + ChatColor.WHITE + loc.getPitch());
			this.log.info("Pitch: " + loc.getPitch() );
			player.sendMessage(ChatColor.DARK_AQUA + "Blocks from spawn: " + ChatColor.WHITE + distance);
			return true;
		}

		if (commandName.matches("cname") && tregminePlayer.isAdmin()) {
			ChatColor color = ChatColor.getByCode(Integer.parseInt(args[0]));
			tregminePlayer.setTemporaryChatName(color + args[1]);
			tregminePlayer.sendMessage("You are now: " + tregminePlayer.getChatName());
			this.log.info(tregminePlayer.getName() + "changed name to" + tregminePlayer.getChatName());
		}

		if (commandName.matches("t") && tregminePlayer.isOp()) {
			Player victim = this.getServer().matchPlayer(args[0]).get(0);
			victim.getWorld().strikeLightningEffect(victim.getLocation());
			return true;
		}

		if (commandName.matches("td") && tregminePlayer.isOp()) {
			Player victim = this.getServer().matchPlayer(args[0]).get(0);
			victim.getWorld().strikeLightning(victim.getLocation());
			victim.setHealth(0);
			return true;
		}

		if (commandName.matches("time") && tregminePlayer.isDonator()) {
			if (args.length == 1) {

				if (args[0].matches("day")) {
					player.setPlayerTime(6000, false);
				} else if (args[0].matches("night")) {
					player.setPlayerTime(18000, false);
				} else if (args[0].matches("normal")) {
					player.resetPlayerTime();
				}
			}
			else {
				player.sendMessage(ChatColor.YELLOW + "Say /time day|night|normal");
			}

			log.info(player.getName() + "TIME");
			return true;
		}

		if (commandName.matches("tpblock") && isDonator) {
			if (args[0].matches("on")) {
				tregminePlayer.setMetaString("tpblock", "true");
				player.sendMessage("Teleportation is now blocked to you.");
				return true;
			}

			if (args[0].matches("off")) {
				tregminePlayer.setMetaString("tpblock", "false");
				player.sendMessage("Teleportation is now allowed to you.");
				return true;
			}

			if (args[0].matches("status")) {
				player.sendMessage("Your tpblock is set to " + tregminePlayer.getMetaString("tpblock") + ".");
				return true;
			}

			player.sendMessage(ChatColor.RED + "The commands are /tpblock on, /tpblock off and /tpblock status.");
			return true;
		}

		if (commandName.matches("normal")) {
			info.tregmine.api.TregminePlayer tregPlayer = this.tregmine.tregminePlayer.get(player.getName());
			if (isAdmin) {
				tregPlayer.setTempMetaString("admin", "false");
				tregPlayer.setTemporaryChatName(ChatColor.GOLD + tregPlayer.getName());
				player.sendMessage(ChatColor.YELLOW + "You are no longer admin, until you reconnect!");
			} else if (tregPlayer.getMetaBoolean("builder")) {
				tregPlayer.setTempMetaString("builder", "false");
				tregPlayer.setTemporaryChatName(ChatColor.GOLD + tregPlayer.getName());
				player.sendMessage(ChatColor.YELLOW + "You are no longer builder, until you reconnect!");
			} else if (tregPlayer.getMetaBoolean("mentor")) {
				tregPlayer.setTempMetaString("mentor", "false");
				tregPlayer.setTemporaryChatName(ChatColor.GOLD + tregPlayer.getName());
				player.sendMessage(ChatColor.YELLOW + "You are no longer guardian, until you reconnect!");            
			}

			return true;
		}

		if (commandName.matches("nuke") && isAdmin) {
			player.sendMessage("You nuked all mobs in this world!");
			for (Entity ent : player.getWorld().getLivingEntities()) {
				if(ent instanceof Monster) {
					Monster mob = (Monster) ent;
					mob.setHealth(0);
				}

				if(ent instanceof Slime) {
					Slime slime = (Slime) ent;
					slime.setHealth(0);
				}

			}
			return true;
		}


		if (commandName.matches("user") && args.length > 0  && (isAdmin || isMentor)) {

			if (args[0].matches("reload")) {
				this.tregmine.tregminePlayer.get(this.getServer().matchPlayer(args[1]).get(0).getDisplayName()).load();
				player.sendMessage("Player reloaded "+ this.getServer().matchPlayer(args[1]).get(0).getDisplayName());
				return true;
			}

			if (args[0].matches("make")) {
				Player victim = this.getServer().matchPlayer(args[2]).get(0);
				info.tregmine.api.TregminePlayer victimPlayer = this.tregmine.tregminePlayer.get(victim.getName());
				TregminePlayer vtregPlayer = this.tregmine.tregminePlayer.get(victim.getName());

				if (args[1].matches("settler")) {
					vtregPlayer.setMetaString("color", "trial");
					vtregPlayer.setMetaString("trusted", "true");
					vtregPlayer.setTemporaryChatName(vtregPlayer.getNameColor() + vtregPlayer.getName());

					player.sendMessage(ChatColor.AQUA + "You made " + victimPlayer.getChatName() + ChatColor.AQUA + " settler of this server." );
					victim.sendMessage("Welcome! You are now made settler.");
					this.log.info(victim.getName() + " was given settler rights by " + player.getName() + ".");
					return true;
				}

				if (args[1].matches("warn")) {
					vtregPlayer.setMetaString("color", "warned");
					player.sendMessage(ChatColor.AQUA + "You warned " + victimPlayer.getChatName() + ".");
					victim.sendMessage("You are now warned");
					this.log.info(victim.getName() + " was warned by " + player.getName() + ".");
					vtregPlayer.setTemporaryChatName(vtregPlayer.getNameColor() + vtregPlayer.getName());
					return true;
				}

				if (args[1].matches("hardwarn")) {
					vtregPlayer.setMetaString("color", "warned");
					vtregPlayer.setMetaString("trusted", "false");
					player.sendMessage(ChatColor.AQUA + "You warned " + victimPlayer.getChatName() + " and removed his building rights." );
					victim.sendMessage("You are now warned and bereft of your building rights.");
					this.log.info(victim.getName() + " was hardwarned by " + player.getName() + ".");
					vtregPlayer.setTemporaryChatName(vtregPlayer.getNameColor() + vtregPlayer.getName());
					return true;
				}

				if (args[1].matches("trial")) {
					player.sendMessage(ChatColor.RED + "Please use /user make settler name");
				}

				if (args[1].matches("resident") && player.isOp() ) {
					vtregPlayer.setMetaString("color", "trusted");
					vtregPlayer.setMetaString("trusted", "true");
					this.log.info(victim.getName() + " was given trusted rights by " + tregminePlayer.getChatName() + ".");
					player.sendMessage(ChatColor.AQUA + "You made " + victimPlayer.getChatName() + ChatColor.AQUA + " a resident." );
					victim.sendMessage("Welcome! You are now a resident");
					vtregPlayer.setTemporaryChatName(vtregPlayer.getNameColor() + vtregPlayer.getName());
					return true;
				}

				if (args[1].matches("donator") && player.isOp() ) {
					vtregPlayer.setMetaString("donator", "true");
					vtregPlayer.setMetaString("compass", "true");
					vtregPlayer.setMetaString("color", "donator");
					player.sendMessage(ChatColor.AQUA + "You made  " + vtregPlayer.getChatName() + " a donator." );
					this.log.info(victim.getName() + " was made donator by" + tregminePlayer.getChatName() + ".");
					victim.sendMessage("Congratulations, you are now a donator!");
					vtregPlayer.setTemporaryChatName(vtregPlayer.getNameColor() + vtregPlayer.getName());
					return true;
				}

				if (args[1].matches("guardian") && player.isOp() ) {
					if(vtregPlayer.isDonator()) {
						vtregPlayer.setMetaString("police", "true");
						vtregPlayer.setMetaString("color", "police");

						player.sendMessage(ChatColor.AQUA + "You made  " + vtregPlayer.getChatName() + " a police." );
						this.log.info(victim.getName() + " was made police by" + tregminePlayer.getChatName() + ".");
						victim.sendMessage("Congratulations, you are now a police!");
					} else {
						player.sendMessage(ChatColor.AQUA + "Sorry this person is not a  " + ChatColor.GOLD + " donator." );
					}
					vtregPlayer.setTemporaryChatName(vtregPlayer.getNameColor() + vtregPlayer.getName());
					return true;
				}

			}
		}

		if (commandName.matches("kick") && (isAdmin || isMentor)) {
			Player victim = this.getServer().matchPlayer(args[0]).get(0);

			if (victim != null) {
				info.tregmine.api.TregminePlayer victimPlayer = this.tregmine.tregminePlayer.get(victim.getName());

				if(victim.getName().matches("einand")) {
					this.getServer().broadcastMessage("Never try to kick a god!");
					player.kickPlayer("Never kick a god!");
					return true;
				}

				this.getServer().broadcastMessage(tregminePlayer.getChatName() + ChatColor.AQUA + " kicked " + victimPlayer.getChatName() + ChatColor.AQUA + " for 1 minute.");
				this.log.info(victim.getName() + " kicked by " + player.getName());
				victim.kickPlayer("kicked by " + player.getName());
			}
			return true;
		}

		if (commandName.matches("ride.") && tregminePlayer.isOp() ) {
			Player v = this.getServer().matchPlayer(args[0]).get(0);
			Player v2 = this.getServer().matchPlayer(args[1]).get(0);
			v2.setPassenger(v);
			return true;
		}
		
		if (commandName.matches("eject")) {
			Player v = this.getServer().matchPlayer(args[0]).get(0);
			v.eject();
		}

		if (commandName.matches("newspawn") && isAdmin) {
			player.getWorld().setSpawnLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
			return true;
		}

		if (commandName.matches("sendto") && isAdmin) {
			Player victim = this.getServer().matchPlayer(args[0]).get(0);
			if (victim != null){
				Location cpspawn = this.getServer().getWorld(args[1]).getSpawnLocation();
				player.teleport(cpspawn);
			}
			return true;
		}

		if (commandName.matches("test")) {
			player.sendMessage("Admin: " + tregminePlayer.isAdmin());
			player.sendMessage("Donator: " +tregminePlayer.isDonator());
			player.sendMessage("Trusted: " +tregminePlayer.isTrusted());
		}

		if (commandName.matches("createmob") && isAdmin) {
			int amount = 1;
			CreatureType mobtyp;
			try {
				amount = Integer.parseInt( args[1] );
			} catch (Exception e) {
				amount = 1;
			}

			try {
				String mobname = args[0].substring(0,1).toUpperCase() + args[0].substring(1).toLowerCase(); 
				mobtyp = CreatureType.fromName(mobname);
			} catch (Exception e) {
				player.sendMessage(ChatColor.RED + "Sorry that mob doesn't exist.");
				return true;
			}



			if (mobtyp != null) {
				for (int i = 0; i<amount;i++) {

					player.getWorld().spawnCreature(player.getLocation(), mobtyp);

				} 

				player.sendMessage(ChatColor.YELLOW + "You created " + amount + " " + mobtyp.getName() + ".");
			}
			else {
				for (CreatureType mob : CreatureType.values()) {
					player.sendMessage(mob.getName());
				}

			}
			return true;

		}

		if (commandName.matches("ban") && (isAdmin || isMentor)) {
			Player victim = this.getServer().matchPlayer(args[0]).get(0);
			if (victim != null) {
				info.tregmine.api.TregminePlayer victimPlayer = this.tregmine.tregminePlayer.get(victim.getName());

				if(victim.getName().matches("einand")) {
					this.getServer().broadcastMessage("Never ban a god!");
					player.kickPlayer("Never ban a god!");
					return true;
				}

				victimPlayer.setMetaString("banned", "true");
				this.getServer().broadcastMessage(victimPlayer.getChatName() + ChatColor.RED + " was banned by " + tregminePlayer.getChatName() + ".");
				this.log.info(victim.getName() + " Banned by " + player.getName());
				victim.kickPlayer("banned by " + player.getName());
			}
			return true;
		}

		if (commandName.matches("clean")) {
			player.getInventory().clear();
			return true;
		}

		return false;
	}

	public void onLoad() {
	}
}
