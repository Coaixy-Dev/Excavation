package ren.lawliet.excavation;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public final class Excavation extends JavaPlugin implements Listener {
    private final Random random = new Random();

    @Override
    public void onEnable() {
        getLogger().info("Loading Excavation");
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    // Contains PicAxe and Axe and sneaking
    public boolean getMiningMode(Player player) {
        return player.getGameMode() == GameMode.SURVIVAL &&
                player.getInventory().getItemInMainHand().getType().toString().toUpperCase().contains("AXE")
                && player.getPose() == Pose.SNEAKING;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        // If player is in survival mode and holding an axe or pickaxe
        Block block = event.getBlock();
        if (getMiningMode(player)) {
            player.sendMessage("【Excavation】连锁挖矿");
            // Get the block that was broken
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            // Get the block type
            Material blockMaterial = block.getType();
            // Get All Around block is same material use bfs
            int count = 0;
            for (Block b : getConnectedBlocks(block, player)) {
                b.breakNaturally(player.getInventory().getItemInMainHand());
                Damageable meta = (Damageable) player.getInventory().getItemInMainHand().getItemMeta();
                if (meta != null) meta.setDamage(meta.getDamage() + 1);
                player.getInventory().getItemInMainHand().setItemMeta(meta);
                count++;
            }
            player.sendMessage("【Excavation】已挖掘" + count + "个方块");

        }
    }

    public Set<Block> getConnectedBlocks(Block startBlock, Player player) {
        Set<Block> connectedBlocks = new HashSet<>();
        if (startBlock == null) {
            return connectedBlocks;
        }
        recursiveConnect(startBlock, connectedBlocks, player);
        return connectedBlocks;
    }

    private void recursiveConnect(Block block, Set<Block> connectedBlocks, Player player) {
        if (connectedBlocks.contains(block)) {
            return;
        }
        if (connectedBlocks.size() >= 30) {
            return;
        }

        ItemMeta itemMeta = player.getInventory().getItemInMainHand().getItemMeta();
        if (itemMeta instanceof Damageable) {
            player.getInventory().getItemInMainHand().setAmount(0);
            return;
        }
        connectedBlocks.add(block);
        BlockFace[] faces = new BlockFace[]{
                BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
                BlockFace.UP, BlockFace.DOWN
        };
        for (BlockFace face : faces) {
            Block relativeBlock = block.getRelative(face);
            if (relativeBlock.getType() == block.getType()) {
                recursiveConnect(relativeBlock, connectedBlocks, player);
            }
        }
    }

    // this code is from PuddingKc
    private void dropExperience(Block block, Player player) {
        int expToDrop = 0;
        String var4 = block.getType().toString().toUpperCase();
        byte var5 = -1;
        switch (var4.hashCode()) {
            case -2143360393:
                if (var4.equals("REDSTONE_ORE")) {
                    var5 = 9;
                }
                break;
            case -2117014516:
                if (var4.equals("DEEPSLATE_COAL_ORE")) {
                    var5 = 1;
                }
                break;
            case -1606773547:
                if (var4.equals("DEEPSLATE_EMERALD_ORE")) {
                    var5 = 5;
                }
                break;
            case -1310813950:
                if (var4.equals("ANCIENT_DEBRIS")) {
                    var5 = 12;
                }
                break;
            case -1119837590:
                if (var4.equals("NETHER_GOLD_ORE")) {
                    var5 = 11;
                }
                break;
            case -910594043:
                if (var4.equals("DEEPSLATE_DIAMOND_ORE")) {
                    var5 = 3;
                }
                break;
            case -866289145:
                if (var4.equals("EMERALD_ORE")) {
                    var5 = 4;
                }
                break;
            case -807349915:
                if (var4.equals("NETHER_QUARTZ_ORE")) {
                    var5 = 8;
                }
                break;
            case -170109641:
                if (var4.equals("DIAMOND_ORE")) {
                    var5 = 2;
                }
                break;
            case -163486694:
                if (var4.equals("COAL_ORE")) {
                    var5 = 0;
                }
                break;
            case 671426921:
                if (var4.equals("DEEPSLATE_REDSTONE_ORE")) {
                    var5 = 10;
                }
                break;
            case 1411455414:
                if (var4.equals("DEEPSLATE_LAPIS_ORE")) {
                    var5 = 7;
                }
                break;
            case 1841275752:
                if (var4.equals("LAPIS_ORE")) {
                    var5 = 6;
                }
        }

        switch (var5) {
            case 0:
            case 1:
                expToDrop = this.random.nextInt(3);
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                expToDrop = this.random.nextInt(6) + 3;
                break;
            case 6:
            case 7:
            case 8:
                expToDrop = this.random.nextInt(4) + 2;
                break;
            case 9:
            case 10:
                expToDrop = this.random.nextInt(4) + 1;
                break;
            case 11:
                expToDrop = this.random.nextInt(2);
                break;
            case 12:
                expToDrop = 2;
        }

        if (expToDrop > 0) {
            ((ExperienceOrb) block.getWorld().spawn(player.getLocation(), ExperienceOrb.class)).setExperience(expToDrop);
        }

    }
}
