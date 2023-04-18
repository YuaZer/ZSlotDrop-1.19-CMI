package io.github.yuazer.zslotdrop.Packet;

import com.Zrips.CMI.Modules.Economy.Economy;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import io.github.yuazer.zslotdrop.Main;
import io.github.yuazer.zslotdrop.Utils.YamlUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ChestEntityGenerator {
    public static void spawnChestEntity(Player player, Location location, double coins) {
        // 创建一个新的 PacketContainer，用于生成箱子实体
        PacketContainer packet = Main.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        // 设置实体的类型为箱子
        packet.getIntegers().write(0, 11451466); // 实体ID，可以设置为任意非负整数，不与已有实体ID冲突即可
        packet.getIntegers().write(1, 1); // 实体类型，1 代表箱子
        NbtCompound nbtCompound = NbtFactory.ofCompound("ZSlotDrop");
        nbtCompound.put("ZCoinChest", coins);
        packet.getNbtModifier().write(0, nbtCompound);
        // 设置实体的位置
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY());
        packet.getDoubles().write(2, location.getZ());
        // 设置实体的 DataWatcher
        WrappedDataWatcher entityWatcher = WrappedDataWatcher.getEntityWatcher(player);
        // 设置实体的自定义名称
        WrappedDataWatcher.WrappedDataWatcherObject customNameObject = new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true));
        entityWatcher.setObject(customNameObject, WrappedChatComponent.fromChatMessage("金币掉落箱")); // 替换为自定义的箱子名称
        // 设置实体的锁定状态（是否为锁定的箱子）
        WrappedDataWatcher.WrappedDataWatcherObject lockObject = new WrappedDataWatcher.WrappedDataWatcherObject(10, WrappedDataWatcher.Registry.get(Boolean.class));
        entityWatcher.setObject(lockObject, true); // 替换为自定义的锁定状态
        packet.getDataWatcherModifier().write(0, entityWatcher);
        // 发送生成箱子实体的数据包给指定玩家
        try {
            for (Player p : Bukkit.getOnlinePlayers()) {
                Main.getProtocolManager().sendServerPacket(p, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registerChestOpenEvent() {
        Main.getProtocolManager().addPacketListener(new PacketAdapter(Main.getInstance(), PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                    // 获取玩家
                    Player player = event.getPlayer();
                    PacketContainer packet = event.getPacket();
                    // 检查数据包是否包含足够的字段
                    if (packet.getBooleans().size() > 0 && packet.getIntegers().size() > 0) {
                        NbtCompound nbtCompound = NbtFactory.ofCompound("ZSlotDrop");
                        int clickedEntityId = packet.getIntegers().read(0);
                        if (nbtCompound.containsKey("ZCoinChest")) {
                            double coins = nbtCompound.getDouble("ZCoinChest");
                            Economy.depositPlayer(player.getName(), coins);
                            player.sendMessage(YamlUtils.getConfigMessage("Message.getMoney").replace("%balance%", String.valueOf(coins)));
                            removeChestEntity(player, clickedEntityId);
                        }
                    }

                }
            }
        });
    }

    public static void removeChestEntity(Player player, int entityId) {
        ProtocolManager protocolManager = Main.getProtocolManager();
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntegerArrays().write(0, new int[]{entityId});
        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
