package com.mcheroth.heropet;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class HeroProtection extends JavaPlugin{
	public void onEnable(){
		getServer().getLogger().info("[HeroProtection] working");
		getConfig().options().copyDefaults();
		saveConfig();
	}
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args){
		if(!sender.isOp())return false;
		if(cmd.getName().equalsIgnoreCase("heroprotect")){
			if(args.length==2 && args[0].equals("give") ){
				Player player = Bukkit.getPlayer(args[1]);
				giveProtectionStone(player);
			}
			sender.sendMessage(ChatColor.GREEN+"heropotect give <player>");
			sender.sendMessage(ChatColor.GRAY+"Give player a ProtectionStone.");
		}
		return false;
	}
	public void giveProtectionStone(Player player){
		if(getConfig().contains("OwnerList."+player.getName())){
			player.sendMessage(ChatColor.RED+"คุณเคยรับ ProtectionStone ไปแล้ว");
		}else{
			getConfig().set("OwnerList."+player.getName(), "no");
			player.getInventory().addItem(ProtectionStone());
			player.sendTitle(ChatColor.GREEN+"ProtectionStone", ChatColor.GRAY+"คุณได้รับ ProtectionStone 1 ชิ้น");
		}
		
	}
	public ItemStack ProtectionStone(){
		ItemStack ProtectionStone = new ItemStack(Material.MOB_SPAWNER);
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
