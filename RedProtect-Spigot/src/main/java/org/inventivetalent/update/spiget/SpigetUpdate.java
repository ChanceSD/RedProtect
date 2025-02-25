/*
 * Copyright (c) 2020 - @FabioZumbi12
 * Last Modified: 25/10/2019 22:04.
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

package org.inventivetalent.update.spiget;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.update.spiget.comparator.VersionComparator;
import org.inventivetalent.update.spiget.download.DownloadCallback;
import org.inventivetalent.update.spiget.download.UpdateDownloader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;

public class SpigetUpdate extends SpigetUpdateAbstract {

    protected final Plugin plugin;
    protected DownloadFailReason failReason = DownloadFailReason.UNKNOWN;

    public SpigetUpdate(Plugin plugin, int resourceId) {
        super(resourceId, plugin.getDescription().getVersion(), plugin.getLogger());
        this.plugin = plugin;
        setUserAgent("SpigetResourceUpdater/Bukkit");
    }

    @Override
    public SpigetUpdate setUserAgent(String userAgent) {
        super.setUserAgent(userAgent);
        return this;
    }

    @Override
    public SpigetUpdate setVersionComparator(VersionComparator comparator) {
        super.setVersionComparator(comparator);
        return this;
    }

    @Override
    protected void dispatch(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public boolean downloadUpdate() {
        if (latestResourceInfo == null) {
            failReason = DownloadFailReason.NOT_CHECKED;
            return false;// Update not yet checked
        }
        if (!isVersionNewer(currentVersion, latestResourceInfo.latestVersion.name)) {
            failReason = DownloadFailReason.NO_UPDATE;
            return false;// Version is no update
        }
        if (latestResourceInfo.external) {
            failReason = DownloadFailReason.NO_DOWNLOAD;
            return false;// No download available
        }

        File pluginFile = getPluginFile();// /plugins/XXX.jar
        if (pluginFile == null) {
            failReason = DownloadFailReason.NO_PLUGIN_FILE;
            return false;
        }
        File updateFolder = Bukkit.getUpdateFolderFile();
        if (!updateFolder.exists()) {
            if (!updateFolder.mkdirs()) {
                failReason = DownloadFailReason.NO_UPDATE_FOLDER;
                return false;
            }
        }
        final File updateFile = new File(updateFolder, pluginFile.getName());

        Properties properties = getUpdaterProperties();
        boolean allowExternalDownload = properties != null && properties.containsKey("externalDownloads") && Boolean.parseBoolean(properties.getProperty("externalDownloads"));

        if (!allowExternalDownload && latestResourceInfo.external) {
            failReason = DownloadFailReason.EXTERNAL_DISALLOWED;
            return false;
        }

        log.info("[SpigetUpdate] Downloading update...");
        dispatch(UpdateDownloader.downloadAsync(latestResourceInfo, updateFile, getUserAgent(), new DownloadCallback() {
            @Override
            public void finished() {
                log.info("[SpigetUpdate] Update saved as " + updateFile.getPath());
            }

            @Override
            public void error(Exception exception) {
                log.log(Level.WARNING, "[SpigetUpdate] Could not download update", exception);
            }
        }));

        return true;
    }

    public DownloadFailReason getFailReason() {
        return failReason;
    }

    public Properties getUpdaterProperties() {
        File file = new File(Bukkit.getUpdateFolderFile(), "spiget.properties");
        Properties properties = new Properties();
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return null;
                }
                properties.setProperty("externalDownloads", "false");
                properties.store(new FileWriter(file), """
                        Configuration for the Spiget auto-updater. https://spiget.org | https://github.com/InventivetalentDev/SpigetUpdater
                        Use 'externalDownloads' if you want to auto-download resources hosted on external sites
                        """);
            } catch (Exception ignored) {
                return null;
            }
        }
        try {
            properties.load(new FileReader(file));
        } catch (IOException e) {
            return null;
        }
        return properties;
    }

    /**
     * Get the plugin's file name
     *
     * @return the plugin file name
     */
    private File getPluginFile() {
        if (!(this.plugin instanceof JavaPlugin)) {
            return null;
        }
        try {
            Method method = JavaPlugin.class.getDeclaredMethod("getFile");
            method.setAccessible(true);
            return (File) method.invoke(this.plugin);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not get plugin file", e);
        }
    }

    public enum DownloadFailReason {
        NOT_CHECKED,
        NO_UPDATE,
        NO_DOWNLOAD,
        NO_PLUGIN_FILE,
        NO_UPDATE_FOLDER,
        EXTERNAL_DISALLOWED,
        UNKNOWN
    }

}
