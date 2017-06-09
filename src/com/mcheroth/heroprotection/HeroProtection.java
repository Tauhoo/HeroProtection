package com.mcheroth.heroprotection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
					loc.setY(loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()));
					player.teleport(loc);
				}
				return true;
			}
			if(args.length==1 && sender.isOp()){
				Player p = Bukkit.getPlayer(args[0]);
				if(getConfig().getString("OwnerList."+p.getName()).equals("no")){
					player.sendMessage(ChatColor.RED+args[0]+"ยังไม่ได้เซตบริเวรบ้านของคุณ");
				}else{
					Location loc = tool.StringToLocation(getConfig().getString("OwnerList."+player.getName()));
					player.teleport(loc);
				}
				return true;
			}
			if(args.length==2 && args[0].equals("add") ){
				if(!check.checkPlayerHave(args[1])){
					player.sendMessage(ChatColor.RED+"ไม่พบผู้เล่นนี้");
					return false;
				}
				Player p = Bukkit.getPlayer(args[1]);
				if(getConfig().getString("OwnerList."+player.getName()).equals("no")){
					player.sendMessage(ChatColor.RED+"คุณยังไม่ได้เซตบริเวรบ้านของคุณ");
				}else{
					Location loc = tool.StringToLocation(getConfig().getString("OwnerList."+player.getName()));
					List<String> sl = getConfig().getStringList("ProtectionList."+loc.getWorld().getName()+"."+tool.LocationToString(loc));
					sl.add(p.getName());
					getConfig().set("ProtectionList."+loc.getWorld().getName()+"."+tool.LocationToString(loc), sl);
					saveConfig();
					p.sendMessage(ChatColor.GREEN+"คุณถูกเพิ่มเป็นผู้อยู่อาศัยในบ้านของ"+findOwner(sl));
					sender.sendMessage(ChatColor.GREEN+args[1]+"ถูกเพิ่มเป็นผู้อยู่อาศัยในบ้านคุณแล้ว");
					
				}
				return true;
			}
			if(args.length==2 && args[0].equals("remove") ){
				if(!check.checkPlayerHave(args[1])){
					player.sendMessage(ChatColor.RED+"ไม่พบผู้เล่นนี้");
					return false;
				}
				if(args[1].equals(player.getName()+" [Owner]")){
					player.sendMessage("คุณไม่สามารถไล่ตัวเองที่เป็นเจ้าบ้านออกได้");
					return false;
				}
				OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
				if(getConfig().getString("OwnerList."+player.getName()).equals("no")){
					player.sendMessage(ChatColor.RED+"คุณยังไม่ได้เซตบริเวรบ้านของคุณ");
				}else{
					Location loc = tool.StringToLocation(getConfig().getString("OwnerList."+player.getName()));
					List<String> sl = getConfig().getStringList("ProtectionList."+loc.getWorld().getName()+"."+tool.LocationToString(loc));
					sl.remove(op.getName());
					getConfig().set("ProtectionList."+loc.getWorld().getName()+"."+tool.LocationToString(loc), sl);
					saveConfig();
					if(op.isOnline()){
						Player p = (Player) op;
						p.sendMessage(ChatColor.GREEN+"คุณถูกไล่ออกจากการเป็นผู้อยู่อาศัยในบ้านของ"+findOwner(sl));
					}
					
					sender.sendMessage(ChatColor.GREEN+args[1]+"ถูกไล่อกกจากการเป็นผู้อยู่อาศัยในบ้านคุณแล้ว");
				}
				return true;
			}
			if(args.length==1 && args[0].equals("removeall") ){
				if(getConfig().getString("OwnerList."+player.getName()).equals("no")){
					player.sendMessage(ChatColor.RED+"คุณยังไม่ได้เซตบริเวรบ้านของคุณ");
				}else{
					Location loc = tool.StringToLocation(getConfig().getString("OwnerList."+player.getName()));
					List<String> sl = getConfig().getStringList("ProtectionList."+loc.getWorld().getName()+"."+tool.LocationToString(loc));
					Iterator<String> itsl = sl.iterator();
					while(itsl.hasNext()){
						String name = itsl.next();
						if(!name.contains(" [Owner]")){
							OfflinePlayer op = Bukkit.getOfflinePlayer(name);
							if(op.isOnline()){
								Player p = (Player) op;
								p.sendMessage(ChatColor.GREEN+"คุณถูกไล่ออกจากการเป็นผู้อยู่อาศัยในบ้านของ"+findOwner(sl));
							}
						}
					}
					sl.clear();
					sl.add(player.getName()+" [Owner]");
					getConfig().set("ProtectionList."+loc.getWorld().getName()+"."+tool.LocationToString(loc), sl);
					saveConfig();
					sender.sendMessage(ChatColor.GREEN+"คนอื่นๆถูกไล่อกกจากการเป็นผู้อยู่อาศัยในบ้านคุณแล้ว");
				}
				return true;
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
					List<String> ownerArea = check.checkAreaOwner(this, player.getLocation());
					String ownerString = findOwner(ownerArea);
					WarningPlayerArea(player,playerArea.get(player),ownerString);
					playerArea.replace(player,ownerString);
				}
			}
		}
	}
	public String findOwner(List<String> sl){
		Iterator<String> sit = sl.iterator();
		while(sit.hasNext()){
			String ps = sit.next();
			if(ps.contains(" [Owner]")){
				return ps.replace(" [Owner]", "");
			}
		}
		return "no";
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
