package io.github.yuazer.zslotdrop.Listener;

import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Economy.CMIEconomyAcount;
import com.Zrips.CMI.Modules.Economy.Economy;
import io.github.yuazer.zslotdrop.Main;
import io.github.yuazer.zslotdrop.Packet.ChestEntityGenerator;
import io.github.yuazer.zslotdrop.Utils.YamlUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import javax.script.ScriptException;
import java.util.*;

public class PlayerEvent implements Listener {
    public static ItemStack air = new ItemStack(Material.AIR, 1);
    private static HashMap<UUID, Collection<PotionEffect>> potionEffects = new HashMap<>();
    private static HashMap<UUID, Integer> foodLevels = new HashMap<>();

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (potionEffects.containsKey(player.getUniqueId())) {
            Collection<PotionEffect> effects = potionEffects.get(player.getUniqueId());
            potionEffects.remove(player.getUniqueId());
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (PotionEffect effect : effects) {
                        player.addPotionEffect(effect);
                    }
                }
            }.runTaskLater(Main.getInstance(), 3L);
        }
        if (foodLevels.containsKey(player.getUniqueId())) {
            int food = foodLevels.get(player.getUniqueId());
            foodLevels.remove(player.getUniqueId());
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setFoodLevel(food);
                }
            }.runTaskLater(Main.getInstance(), 3L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDead(PlayerDeathEvent event) throws ScriptException {
        Player player = event.getEntity();
        String worldName = player.getWorld().getName();
        if (Main.getInstance().getConfig().getConfigurationSection("WorldSetting").getKeys(false).contains(worldName)) {
            //保留药水效果
            if (YamlUtils.getConfigBoolean("WorldSetting." + worldName + ".keepEffect")) {
                potionEffects.put(player.getUniqueId(), player.getActivePotionEffects());
            }
            //保留饱食度
            if (YamlUtils.getConfigBoolean("WorldSetting." + worldName + ".keepFood")) {
                foodLevels.put(player.getUniqueId(), player.getFoodLevel());
            }
            List<Integer> slotList = YamlUtils.getConfigStringList("WorldSetting." + worldName + ".dropSlot").stream()
                    .map(Integer::parseInt)
                    .toList();
            // 随机选择背包槽位ID
            Random random = new Random();
            List<Integer> selectedSlots = new ArrayList<>();
            int numSlots = random.nextInt(slotList.size()) + 1; // 从dropSlot中随机选择几个槽位
            while (selectedSlots.size() < numSlots) {
                int slot = slotList.get(random.nextInt(slotList.size()));
                if (!selectedSlots.contains(slot)) {
                    selectedSlots.add(slot);
                }
            }
            PlayerInventory inventory = player.getInventory();
            for (int slot : selectedSlots) {
                int dropAmount = getRandomNumberFromString(YamlUtils.getConfigMessage("WorldSetting." + worldName + ".dropAmount"));
                ItemStack itemStack = inventory.getItem(slot);
                if (itemStack != null && !itemStack.getType().isAir()) {
                    List<String> lockLore = YamlUtils.getConfigStringList("WorldSetting." + worldName + ".LockLore")
                            .stream().map(ChatColor::stripColor).toList();
                    if (checkLore(lockLore, getLore(itemStack))) {
                        continue;
                    }
                    int shoudDropAmount = Math.min(dropAmount, itemStack.getAmount());
                    if (itemStack.getAmount() - shoudDropAmount > 0) {
                        itemStack.setAmount(itemStack.getAmount() - shoudDropAmount);
                        inventory.setItem(slot, itemStack);
                        itemStack.setAmount(shoudDropAmount);
                    } else {
                        inventory.setItem(slot, air);
                        itemStack.setAmount(itemStack.getAmount());
                    }
                    player.getWorld().dropItem(player.getLocation(), itemStack);

                }
            }
            double dropMoney = Math.min(Economy.getBalance(player.getName()), getRandomDoubleFromString(YamlUtils.getConfigMessage("WorldSetting." + worldName + ".dropMoney")));
            Economy.withdrawPlayer(player.getName(), dropMoney);
            player.sendMessage(YamlUtils.getConfigMessage("Message.dropMoney").replace("%balance%", String.format("%.2f", dropMoney)));
            double entityMoney = Double.parseDouble(Main.getEngine().eval(PlaceholderAPI.setPlaceholders(player, YamlUtils.getConfigMessage("WorldSetting." + worldName + ".dropMoneyPickUp").replace("%dropMoney%", String.valueOf(dropMoney)))).toString());
            ChestEntityGenerator.spawnChestEntity(player, player.getLocation(), entityMoney);
        }
    }

    public static List<String> getLore(ItemStack itemStack) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
            return itemStack.getItemMeta().getLore().stream().map(ChatColor::stripColor).toList();
        }
        return lore.stream().map(ChatColor::stripColor).toList();
    }

    public static boolean checkLore(List<String> lore1, List<String> lore2) {
        for (String lore : lore1) {
            if (lore2.contains(lore)) {
                return true;
            }
        }
        return false;
    }

    public static int getRandomNumberFromString(String input) {
        String[] range = input.split("-");
        int min = Integer.parseInt(range[0]);
        int max = Integer.parseInt(range[1]);
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public static double getRandomDoubleFromString(String input) {
        String[] range = input.split("-");
        double min = Double.parseDouble(range[0]);
        double max = Double.parseDouble(range[1]);
        Random random = new Random();
        return random.nextDouble(max - min + 1) + min;
    }
}
