/*
 * Copyright (c) 2020 - @FabioZumbi12
 * Last Modified: 02/07/2020 19:25.
 *
 * This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
 *  damages arising from the use of this class.
 *
 * Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
 * redistribute it freely, subject to the following restrictions:
 * 1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
 * use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
 * 2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
 * 3 - This notice may not be removed or altered from any source distribution.
 *
 * Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
 * responsabilizados por quaisquer danos decorrentes do uso desta classe.
 *
 * É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
 * alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
 * 1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
 *  classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
 * 2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
 * classe original.
 * 3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
 */

package br.net.fabiozumbi12.redbackups;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class RedBackups extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {

    private final List<String> backupList = new ArrayList<>();
    private RedBackups plugin;
    private BukkitTask taskAfterStart;
    private BukkitTask taskInterval;
    private BukkitTask task;

    @Override
    public void onDisable() {
        if (taskAfterStart != null) {
            taskAfterStart.cancel();
            taskAfterStart = null;
        }
        if (taskInterval != null) {
            taskInterval.cancel();
            taskInterval = null;
        }
    }

    @Override
    public void onEnable() {
        this.plugin = this;

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("redbackups").setExecutor(this);

        getConfig().addDefault("backup.enabled", false);
        getConfig().addDefault("backup.worlds", Bukkit.getServer().getWorlds().stream().map(World::getName).collect(Collectors.toList()));
        getConfig().addDefault("backup.mode", "server-start");
        getConfig().addDefault("backup.modes.server-start.delay-after-start", 10);
        getConfig().addDefault("backup.modes.interval.minutes", 120);
        getConfig().addDefault("backup.modes.timed.time", "03:00");

        getConfig().options().header("""
                ---- RedBackups Configuration ----
                Description: This plugin makes backups fo redprotect player regions
                Configurations:
                - backup-mode = Backup modes to use.
                - - Values:\s
                - - server-start = Backup on every server start with delay (in minutes).
                - - interval = Backup on every interval in minutes (start counting on server start/not persistent on server reboot).
                - - timed = Backup on exact time and minute.
                """
        );
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Init backup task
        startBackupScheduler();
    }

    private void startBackupScheduler() {
        if (!getConfig().getBoolean("backup.enabled", false)) return;

        String mode = getConfig().getString("backup.mode", "server-start");

        if (mode.equals("server-start") && taskAfterStart == null) {
            taskAfterStart = Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> createBackup(Bukkit.getConsoleSender(), null), (long) getConfig().getInt("backup.modes.server-start.delay-after-start") * 60 * 20);
        }

        if (mode.equals("interval")) {
            int delay = getConfig().getInt("backup.modes.interval.minutes") * 60 * 20;
            taskInterval = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> createBackup(Bukkit.getConsoleSender(), null), delay, delay);
        }

        if (mode.equals("timed")) {
            String timed = getConfig().getString("backup.modes.timed.time");
            final String[] lastBackup = {""};

            taskInterval = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
                String timeStamp = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                if (timeStamp.equals(timed) && !lastBackup[0].equals(timeStamp)) {
                    lastBackup[0] = timeStamp;
                    createBackup(Bukkit.getConsoleSender(), null);
                }
            }, 600, 600);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> tab = new ArrayList<>();
        if (args.length == 0) {
            if (sender.hasPermission("redbackups.cmd.reload")) {
                tab.add("reload");
            }
            if (sender.hasPermission("redbackups.cmd.backup")) {
                tab.add("backup");
            }
        }
        if (args.length == 1) {
            if ("reload".startsWith(args[0]) && sender.hasPermission("redbackups.cmd.reload")) {
                tab.add("reload");
            }
            if ("backup".startsWith(args[0]) && sender.hasPermission("redbackups.cmd.backup")) {
                tab.add("backup");
            }
        }
        return tab;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equals("reload") && sender.hasPermission("redbackups.cmd.reload")) {
                if (taskAfterStart != null && !getConfig().getString("backup.mode", "server-start").equals("server-start")) {
                    taskAfterStart.cancel();
                    taskAfterStart = null;
                }

                if (taskInterval != null) {
                    taskInterval.cancel();
                    taskInterval = null;
                }

                reloadConfig();
                startBackupScheduler();

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&4Red&cBackups&7] &aRedBackups reloaded with success!"));
                return true;
            }

            if (args[0].equals("backup") && sender.hasPermission("redbackups.cmd.backup")) {
                createBackup(sender, null);
                return true;
            }
        }

        if (args.length == 2 && args[0].equals("backup") && args[1].equals("here") && sender.hasPermission("redbackups.cmd.backup")) {
            if (sender instanceof Player) {
                createBackup(sender, ((Player) sender).getLocation());
            } else {
                sender.sendMessage(ChatColor.RED + "Only players can use this command with argument 'here'!");
            }
            return true;
        }

        return false;
    }

    private void createBackup(CommandSender sender, Location location) {
        List<String> worlds = getConfig().getStringList("backup.worlds");

        if (task != null && Bukkit.getScheduler().getActiveWorkers().stream().anyMatch(t -> t.getOwner().equals(task.getOwner()))) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&4Red&cBackups&7] &eThere is already a backup operation in progress!"));
            return;
        }

        task = Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&4Red&cBackups&7] &2Backup started..."));
            String mainWorld = Bukkit.getWorlds().get(0).getName();
            // Clear last backups
            backupList.clear();

            if (location != null) {
                if (!worlds.contains(location.getWorld().getName())) return;

                try {
                    String worldName = location.getWorld().getName();
                    File tempWorld = new File(getServer().getWorldContainer().getCanonicalPath(), worldName);
                    String[] dim = tempWorld.list((dir, name) -> name.startsWith("DIM"));
                    if (dim != null && dim.length > 0)
                        worldName += File.separator + dim[0];

                    String file = worldName + File.separator + "region" + File.separator + "r." + (location.getBlockX() >> 4 >> 5) + "." + (location.getBlockZ() >> 4 >> 5) + ".mca";
                    File worldFile = new File(getServer().getWorldContainer().getCanonicalPath(), file);
                    if (worldFile.exists()) {
                        backupList.add(file);
                    } else {
                        file = mainWorld + File.separator + location.getWorld().getName() + File.separator + "region" + File.separator + "r." + (location.getBlockX() >> 4 >> 5) + "." + (location.getBlockZ() >> 4 >> 5) + ".mca";
                        worldFile = new File(getServer().getWorldContainer().getCanonicalPath(), file);
                        if (worldFile.exists()) {
                            backupList.add(file);
                        }
                    }
                } catch (Exception ignored) {
                }
            } else {
                Set<Region> regionSet = RedProtect.get().getAPI().getAllRegions();

                for (Region region : regionSet.stream().filter(r -> worlds.contains(r.getWorld())).collect(Collectors.toList())) {
                    try {
                        String worldName = region.getWorld();
                        File tempWorld = new File(getServer().getWorldContainer().getCanonicalPath(), worldName);
                        String[] dim = tempWorld.list((dir, name) -> name.startsWith("DIM"));
                        if (dim != null && dim.length > 0)
                            worldName += File.separator + dim[0];

                        for (int x = region.getMinMbrX(); x <= region.getMaxMbrX(); x++) {
                            for (int z = region.getMinMbrZ(); z <= region.getMaxMbrZ(); z++) {

                                String file = worldName + File.separator + "region" + File.separator + "r." + (x >> 4 >> 5) + "." + (z >> 4 >> 5) + ".mca";
                                File worldFile = new File(getServer().getWorldContainer().getCanonicalPath(), file);
                                if (worldFile.exists() && !backupList.contains(file)) {
                                    backupList.add(file);
                                } else {
                                    file = mainWorld + File.separator + worldName + File.separator + "region" + File.separator + "r." + (x >> 4 >> 5) + "." + (z >> 4 >> 5) + ".mca";
                                    worldFile = new File(getServer().getWorldContainer().getCanonicalPath(), file);
                                    if (worldFile.exists() && !backupList.contains(file)) {
                                        backupList.add(file);
                                    }
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }

            if (backupList.size() > 0) {
                Bukkit.getLogger().info("Starting copy of " + backupList.size() + " world chunk files to backups...");

                // Start backup files
                for (String file : backupList) {
                    try {
                        if (!new File(getDataFolder(), "backups").exists()) {
                            new File(getDataFolder(), "backups").mkdir();
                        }

                        File fileFromCopy = new File(getServer().getWorldContainer().getCanonicalPath(), file);
                        File fileToCopy = new File(getDataFolder().getCanonicalPath(), "backups" + File.separator + file);

                        // Create child directories
                        fileToCopy.getParentFile().mkdirs();

                        Files.copy(fileFromCopy.toPath(), fileToCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Clear backups
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&4Red&cBackups&7] &2Backup of " + backupList.size() + " chunk files finished with success!"));
                backupList.clear();
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&4Red&cBackups&7] &2Theres no regions to backup!"));
            }
        });
    }
}
