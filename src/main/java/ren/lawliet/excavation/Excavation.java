package ren.lawliet.excavation;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import java.util.Set;

public final class Excavation extends JavaPlugin implements Listener {

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
            for (Block b : getConnectedBlocks(block)) {
                b.breakNaturally(player.getInventory().getItemInMainHand());
                Damageable meta = (Damageable) player.getInventory().getItemInMainHand().getItemMeta();
                if (meta != null) meta.setDamage(meta.getDamage() + 1);
                player.getInventory().getItemInMainHand().setItemMeta(meta);
                count ++ ;
            }
            player.sendMessage("【Excavation】已挖掘"+count+"个方块");
        }
    }

    public Set<Block> getConnectedBlocks(Block startBlock) {
        Set<Block> connectedBlocks = new HashSet<>();
        if (startBlock == null) {
            return connectedBlocks;
        }
        recursiveConnect(startBlock, connectedBlocks);
        return connectedBlocks;
    }

    private void recursiveConnect(Block block, Set<Block> connectedBlocks) {
        if (connectedBlocks.contains(block)) {
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
                recursiveConnect(relativeBlock, connectedBlocks);
            }
        }
    }
}
