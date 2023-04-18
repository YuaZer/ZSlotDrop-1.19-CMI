package io.github.yuazer.zslotdrop.Utils;


import io.github.yuazer.zslotdrop.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class YamlUtils {

    public static String getConfigMessage(String path) {
        return Main.getInstance().getConfig().getString(path).replace("&", "§");
    }

    public static void createYamlConfig(String fileName, boolean replace) {
        File file = new File(Main.getInstance().getDataFolder(), fileName);
        if (!file.exists()) {
            Main.getInstance().saveResource(fileName, replace);
        }

    }

    public static List<String> getAllFile(String directoryPath, boolean isAddDirectory) {
        List<String> list = new ArrayList();
        File baseFile = new File(directoryPath);
        if (!baseFile.isFile() && baseFile.exists()) {
            File[] files = baseFile.listFiles();
            File[] var5 = files;
            int var6 = files.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                File file = var5[var7];
                if (file.isDirectory()) {
                    if (isAddDirectory) {
                        list.add(file.getName());
                    }

                    list.addAll(getAllFile(file.getName(), isAddDirectory));
                } else {
                    list.add(file.getName());
                }
            }

            return list;
        } else {
            return list;
        }
    }

    public static double getConfigDouble(String path) {
        return Main.getInstance().getConfig().getDouble(path);
    }

    public static FileConfiguration getFile(String fileName) {
        File file = new File(Main.getInstance().getDataFolder(), fileName);
        return YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration getFile(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

    public static File setFile(String fileName) {
        File file = new File(Main.getInstance().getDataFolder(), fileName);
        return file;
    }

    public static boolean getConfigBoolean(String path) {
        return Main.getInstance().getConfig().getBoolean(path);
    }

    public static String getYamlString(String fileName, String textPath) {
        File file = new File(Main.getInstance().getDataFolder(), fileName);
        FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
        return conf.getString(textPath).replace("&", "§");
    }

    public static boolean getYamlBoolean(String fileName, String textPath) {
        File file = new File(Main.getInstance().getDataFolder(), fileName);
        FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
        return conf.getBoolean(textPath);
    }

    public static boolean checkListContains(List<String> list, String checkString) {
        return list.contains(checkString);
    }

    public static List<String> getYamlStringList(String fileName, String textPath) {
        File file = new File(Main.getInstance().getDataFolder(), fileName);
        FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
        return conf.getStringList(textPath);
    }

    public static List<String> getConfigStringList(String path) {
        return Main.getInstance().getConfig().getStringList(path);
    }

    public static int getConfigInt(String path) {
        try {
            return Main.getInstance().getConfig().getInt(path);
        } catch (NullPointerException var3) {
            return 0;
        }
    }

    public static void addYamlString(File yamlFile, String path, String text) {
        try {
            if (!yamlFile.exists()) {
                System.out.println("该文件不存在");
                return;
            }

            FileConfiguration conf = YamlConfiguration.loadConfiguration(yamlFile);
            List<String> tempList = conf.getStringList(path);
            tempList.add(text);
            conf.set(path, tempList);
            conf.save(yamlFile);
            System.out.println("修改成功");
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }
}