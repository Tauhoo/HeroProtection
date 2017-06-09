package com.mcheroth.heroprotection;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class Tool {
	public String LocationToString(Location loc){
		String x = loc.getBlockX()+"";
		String y = loc.getBlockY()+"";
		String z = loc.getBlockZ()+"";
		String uid = loc.getWorld().getUID().toString();
		return uid+"_"+x+"_"+y+"_"+z;
	}
	public Location StringToLocation(String locs){
		String[] locdetail = locs.split("_");
		int x = Integer.parseInt(locdetail[1]);
		int y = Integer.parseInt(locdetail[2]);
		int z = Integer.parseInt(locdetail[3]);
		World w = getWorld(locdetail[0]);
		return new Location(w,x,y,z);
	}
	public World getWorld(String uid){
		Iterator<World> Itw = Bukkit.getWorlds().iterator();
		while(Itw.hasNext()){
			World w = Itw.next();
			if(w.getUID().toString().equals(uid)){
				return w;
			}			
		}
		return null;
	}
	public ItemStack ProtectionStone(){
		ItemStack ProtectionStone = new ItemStack(Material.OBSIDIAN);
		ItemMeta ProtectionStoneMeta = ProtectionStone.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY+"วางบล้อกนี้เพื่อสร้างอนาเขตบ้าน");
		ProtectionStoneMeta.setDisplayName(ChatColor.GOLD+"ProtectionStone");
		ProtectionStoneMeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		ProtectionStoneMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		ProtectionStoneMeta.setLore(lore);
		ProtectionStone.setItemMeta(ProtectionStoneMeta);
		return ProtectionStone;
	};
}
