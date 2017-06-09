package com.mcheroth.heroprotection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Check {
	Tool tool = new Tool();
	public boolean checkItemEqual(ItemStack first,ItemStack second){
		if(first==null||second==null)return false;
		if(first.getType()==Material.AIR || second.getType()==Material.AIR)return false;
		ItemStack i1 = first;
		ItemStack i2 = second;
		i1.setAmount(1);
		i2.setAmount(1);
		if(i1.equals(i2)){
			return true;
		}
		return false;
	}
	public boolean checkPlayerHave(String name){
		OfflinePlayer[] itop = Bukkit.getOfflinePlayers();
		for(OfflinePlayer op : itop){
			if(op.getName().equals(name)){
				return true;
			}
		}
		return false;
		
	}
	public List<String> checkAreaOwner(HeroProtection hp,Location loc){
		Iterator<Entity> it = loc.getWorld().getNearbyEntities(loc, 10, 1000, 10).iterator();
		while(it.hasNext()){
			Entity entity = it.next();
			Location entityLoc = entity.getLocation();
			String configText = "ProtectionList."+entityLoc.getWorld().getName()+"."+tool.LocationToString(entityLoc);
			if(entity.getType() == EntityType.ARMOR_STAND && hp.getConfig().contains(configText)){
				return hp.getConfig().getStringList(configText);
			}
		}
		List<String> list = new ArrayList<String>();
		return list;
	}
	public List<String> checkAreaPlace(HeroProtection hp,Location loc){
		Iterator<Entity> it = loc.getWorld().getNearbyEntities(loc, 25, 1000, 25).iterator();
		while(it.hasNext()){
			Entity entity = it.next();
			Location entityLoc = entity.getLocation();
			String configText = "ProtectionList."+entityLoc.getWorld().getName()+"."+tool.LocationToString(entityLoc);
			if(entity.getType() == EntityType.ARMOR_STAND && hp.getConfig().contains(configText)){
				return hp.getConfig().getStringList(configText);
			}
		}
		List<String> list = new ArrayList<String>();
		return list;
	}
	public  Entity MarkPlaceWhenBreakUnder(HeroProtection hp,Location loc){
		Iterator<Entity> it = loc.getWorld().getNearbyEntities(loc, 3, 1000, 3).iterator();
		while(it.hasNext()){
			Entity entity = it.next();
			Location entityLoc = entity.getLocation();
			String configText = "ProtectionList."+entityLoc.getWorld().getName()+"."+tool.LocationToString(entityLoc);
			if(entity.getType() == EntityType.ARMOR_STAND && hp.getConfig().contains(configText)){
 				return entity;
			}
		}
		return null;
	}
}
