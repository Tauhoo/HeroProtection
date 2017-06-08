package com.mcheroth.heroprotection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import net.md_5.bungee.api.ChatColor;

public class HeroProtection extends JavaPlugin implements Listener{
	Check check = new Check();
	BukkitScheduler scheduler = getServer().getScheduler();
	Tool tool = new Tool();
	HashMap<Player,String> playerArea = new HashMap<Player,String>();
	public void onEnable(){
		getServer().getLogger().info("[HeroProtection] working");
		getConfig().options().copyDefaults();
		saveConfig();
		Bukkit.getPluginManager().registerEvents(this, this);
		HeroProtectionListener hpl = new HeroProtectionListener(this);
		Loop();
	}
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args){
		if(cmd.getName().equalsIgnoreCase("home") && sender instanceof Player){
			Player player = (Player) sender;
			if(args.length==0){
				if(getConfig().getString("OwnerList."+player.getName()).equals("no")){
					player.sendMessage(ChatColor.RED+"คุณยังไม่ได้เซตบริเวรบ้านของคุณ");
				}else{
					Location loc = tool.StringToLocation(getConfig().getString("OwnerList."+player.getName()));
					player.teleport(loc);
				}
			}
			if(args.length==1 && sender.isOp()){
				Player p = Bukkit.getPlayer(args[0]);
				if(getConfig().getString("OwnerList."+p.getName()).equals("no")){
					player.sendMessage(ChatColor.RED+args[0]+"ยังไม่ได้เซตบริเวรบ้านของคุณ");
				}else{
					Location loc = tool.StringToLocation(getConfig().getString("OwnerList."+player.getName()));
					player.teleport(loc);
				}
			}
			
			return true;
		}
		if(!sender.isOp())return false;
		if(cmd.getName().equalsIgnoreCase("heroprotect")){
			if(args.length==2 && args[0].equals("give") ){
				Player player = Bukkit.getPlayer(args[1]);
				giveProtectionStone(player);
				return true;
			}
			if(args.length==1 && args[0].equals("setworld") && sender instanceof Player){
				Player player = (Player) sender;
				getConfig().set("WorldList."+player.getWorld().getName(),player.getWorld().getUID().toString());
				saveConfig();
				return true;
			}
			if(args.length==1 && args[0].equals("reload") && sender instanceof Player){
				reloadConfig();
				return true;
			}
			sender.sendMessage(ChatColor.GREEN+"/heropotect give <player>");
			sender.sendMessage(ChatColor.GRAY+"Give player a ProtectionStone.");
			sender.sendMessage(ChatColor.GREEN+"/heropotect setworld");
			sender.sendMessage(ChatColor.GRAY+"register home zone.");
			sender.sendMessage(ChatColor.GREEN+"/heropotect reload");
			sender.sendMessage(ChatColor.GRAY+"Reload config.");
			sender.sendMessage(ChatColor.GREEN+"/home");
			sender.sendMessage(ChatColor.GRAY+"Go to your home.");
		}
		return false;
	}
	public void giveProtectionStone(Player player){
		if(getConfig().contains("OwnerList."+player.getName())){
			player.sendMessage(ChatColor.RED+"คุณเคยรับ ProtectionStone ไปแล้ว");
		}else{
			getConfig().set("OwnerList."+player.getName(), "no");
			player.getInventory().addItem(tool.ProtectionStone());
			player.sendTitle(ChatColor.GREEN+"ProtectionStone", ChatColor.GRAY+"คุณได้รับ ProtectionStone 1 ชิ้น");
			saveConfig();
		}
		
	}
	public void Loop(){
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
	    	@Override
	    	public void run() {
	    		UpdatePlayerArea();
	    		
	    	}
	    }, 0L, 20L);
	}
	public void UpdatePlayerArea(){
		Iterator<World> itWorld = Bukkit.getWorlds().iterator();
		while(itWorld.hasNext()){
			World w = itWorld.next();
			if(getConfig().contains("WorldList."+w.getName())){
				Iterator<Player> itPlayer = w.getPlayers().iterator();
				while(itPlayer.hasNext()){
					Player player = itPlayer.next();
					String ownerArea = check.checkAreaOwner(this, player.getLocation());
					WarningPlayerArea(player,playerArea.get(player),ownerArea);
					playerArea.replace(player, ownerArea);
				}
			}
		}
	}
	public void WarningPlayerArea(Player player,String areaOld,String areaNew){
		if(areaOld.equals(areaNew))return;
		if(areaNew.equals("no"))return;
		String Title;
		String detail;
		if(areaNew.equals(player.getName())){
			Title = ChatColor.GREEN+"ยินดีต้อนรับกลับบ้าน";
			detail = ChatColor.GRAY+"นี้คือบ้านของคุณ";
		}else{
			Title = ChatColor.GREEN+"ยินดีต้อนรับสู่บ้านของ "+areaNew;
			detail = ChatColor.GRAY+"นี้คือไม่ใช้บ้านของคุณ";
		}
		player.sendTitle(ChatColor.GREEN+Title, detail);
	}
	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent e){
		playerArea.put(e.getPlayer(), "no");
	}
	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent e){
		playerArea.remove(e.getPlayer());
	}
}
