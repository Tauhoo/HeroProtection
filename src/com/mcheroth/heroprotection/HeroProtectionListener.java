package com.mcheroth.heroprotection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;

import net.md_5.bungee.api.ChatColor;

public class HeroProtectionListener implements Listener{
	Check check = new Check();
	HeroProtection hp;
	HashMap<Player,String> playerArea ;
	Tool tool = new Tool();
	public HeroProtectionListener(HeroProtection heroprotecttion){
		hp = heroprotecttion;
		Bukkit.getPluginManager().registerEvents(this, hp);
		playerArea = hp.playerArea;
	}
	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(!hp.getConfig().contains("WorldList."+player.getWorld().getName()))return;
		Location loc  = null;
		List<String> area  = null;
		if(e.getClickedBlock()==null||e.getClickedBlock().getType()==Material.AIR){
			
		}else{
			loc  = e.getClickedBlock().getLocation();
			area  = check.checkAreaOwner(hp,loc);
			if(area.contains(player.getName()) || area.contains(player.getName()+" [Owner]")){
				if(check.checkItemEqual(player.getItemInHand(), tool.ProtectionStone())){
				
				}
				return;
			}else if(area.size()==0){
				if(check.checkItemEqual(player.getItemInHand(), tool.ProtectionStone())){
					if(hp.getConfig().contains("OwnerList."+player.getName())  && !hp.getConfig().getString("OwnerList."+player.getName()).equals("no")){
						e.setCancelled(true); 
						player.sendMessage(ChatColor.RED+"คุณมีพื้นที่เป็นของตนอยู่แล้ว");
						return;
					}else{
		            	placeProtectionStone(player,loc,e);
						return;
					}
				}else{
					player.sendMessage(ChatColor.RED+"คุณต้องอยู่ในพื้นที่ของตนเอง");
					e.setCancelled(true);
				}
			}else{
				player.sendMessage(ChatColor.RED+"คุณต้องอยู่ในพื้นที่ของตนเอง");
				e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void PlayerInteractAtEntityEventv(PlayerInteractAtEntityEvent e){
		Player player = e.getPlayer();
		if(!hp.getConfig().contains("WorldList."+player.getWorld().getName()))return;
		List<String> area =check.checkAreaOwner(hp,e.getRightClicked().getLocation());
		if(area.contains(player.getName()) || area.contains(player.getName()+" [Owner]"))return;
		player.sendMessage(ChatColor.RED+"คุณต้องอยู่ในพื้นที่ของตนเอง");
		e.setCancelled(true);
	}
	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent e){
		Player player = e.getPlayer();
		if(!hp.getConfig().contains("WorldList."+player.getWorld().getName()))return;
		if(player.getItemInHand().getType() == Material.TNT){
			player.sendMessage(ChatColor.RED+"ห้ามวาง TNT ใน home zone");
			e.setCancelled(true);
			return;
		}
		if(player.getItemInHand().getType() == Material.PISTON_BASE){
			player.sendMessage(ChatColor.RED+"ห้ามวาง Pistion Base ใน home zone");
			e.setCancelled(true);
			return;
		}
		if(player.getItemInHand().getType() == Material.PISTON_STICKY_BASE){
			player.sendMessage(ChatColor.RED+"ห้ามวาง Pistion Sticky base ใน home zone");
			e.setCancelled(true);
			return;
		}
		List<String> ownerArea = check.checkAreaOwner(hp,e.getBlockPlaced().getLocation());
		if(ownerArea.contains(e.getPlayer().getName()) || ownerArea.contains(e.getPlayer().getName()+" [Owner]"))return;
		player.sendMessage(ChatColor.RED+"คุณต้องอยู่ในพื้นที่ของตนเอง");
		e.setCancelled(true);
	}
	@EventHandler
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent e){
		if(!(e.getDamager() instanceof Player))return;
		Player player = (Player) e.getDamager();
		if(!hp.getConfig().contains("WorldList."+player.getWorld().getName()))return;
		Location loc = e.getEntity().getLocation();
		List<String> ownerArea = check.checkAreaOwner(hp,loc);
		if(ownerArea.contains(player.getName()) || ownerArea.contains(player.getName()+" [Owner]"))return;
			player.sendMessage(ChatColor.RED+"คุณต้องอยู่ในพื้นที่ของตนเอง");
			e.setCancelled(true);
	}
	@EventHandler
	public void EntityDamageEvent(EntityDamageEvent e){
		Entity en = e.getEntity();
		if(!hp.getConfig().contains("WorldList."+en.getWorld().getName()))return;
		if(en.getType() != EntityType.ARMOR_STAND)return;
		if(!hp.getConfig().contains("ProtectionList."+en.getWorld().getName()+"."+tool.LocationToString(en.getLocation())))return;
		e.setCancelled(true);
	}
	@EventHandler
	public void PlayerDamageEvent(EntityDamageEvent e){
		if(!(e.getEntity() instanceof Player))return;
		Player player = (Player) e.getEntity();
		List<String> area = check.checkAreaOwner(hp, player.getLocation());
		if(area.contains(player.getName()) || area.contains(player.getName()+" [Owner]")){
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void BlockBreakEvent(BlockBreakEvent e){
		Location loc = e.getBlock().getLocation();
		Player player = e.getPlayer();
		List<String> AreaOwner = check.checkAreaOwner(hp, player.getLocation());
		if(e.getBlock().getType() != Material.OBSIDIAN)return;
		if(!AreaOwner.contains(player.getName()+" [Owner]")){
			e.setCancelled(true);
			player.sendMessage(ChatColor.RED+"คุณต้องเป็นเจ้าของบ้านถึงจะทุบ ProtectionStone ได้");
			return;
		}
		loc.setY(loc.getY()+500);
		if(hp.getConfig().contains("ProtectionList."+loc.getWorld().getName()+"."+tool.LocationToString(loc))){
			Entity protectionstone =check.MarkPlaceWhenBreakUnder(hp,loc);
			protectionstone.remove();
			player.getInventory().addItem(tool.ProtectionStone());
			hp.getConfig().set("ProtectionList."+loc.getWorld().getName()+"."+tool.LocationToString(loc), null);
			hp.getConfig().set("OwnerList."+player.getName(), "no");
			hp.saveConfig();
		}
	}
	public void placeProtectionStone(Player player,Location loc,PlayerInteractEvent e){
		List<String> ownerArea = check.checkAreaPlace(hp, loc);
		
		if(ownerArea.size()==0){
			e.setCancelled(true);
			player.setItemInHand(new ItemStack(Material.AIR));
			Location Spawnloc = loc;
			Spawnloc.setY(Spawnloc.getY()+1);
			Spawnloc.getBlock().setType(Material.OBSIDIAN);
			Spawnloc = loc;
			Spawnloc.setY(loc.getY()+500);
			ArmorStand en = (ArmorStand) Spawnloc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
			en.setRemoveWhenFarAway(false);
			en.setMaxHealth(1000);
			en.setHealth(1000);
			en.setGravity(false);
			
			registerarea(player,en);
			
		}else{
			e.setCancelled(true);
			player.sendMessage(ChatColor.RED+"พื้นที่ที่คุณต้องการซ้อนทับกับพื้นที่ของ "+ownerArea.get(0));
		}
		
	}
	public void registerarea(Player player,Entity en){
		Location loc = en.getLocation();
		String LocatinString = tool.LocationToString(en.getLocation());
		List<String> slist = new ArrayList<String>();
		slist.add(player.getName()+" [Owner]");
		hp.getConfig().set("ProtectionList"+"."+en.getLocation().getWorld().getName()+"."+LocatinString,slist);
		hp.getConfig().set("OwnerList."+player.getName(),LocatinString);
		player.sendMessage(ChatColor.GREEN+"ได้บันทึกบริเวณบ้านของคุณไว้แล้ว");
		hp.saveConfig();
	}
}
