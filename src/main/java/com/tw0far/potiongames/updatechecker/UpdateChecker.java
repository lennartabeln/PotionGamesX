package com.tw0far.potiongames.updatechecker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.function.Consumer;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public record UpdateChecker(JavaPlugin plugin, int resourceId) {
    public void getVersion(final Consumer<String> consumer) {
        String URI = "https://api.spigotmc.org/legacy/update.php?resource=" + resourceId + "&" + System.currentTimeMillis();
        Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> {
            try (InputStream inputStream = new URI(URI).toURL().openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException e) {
                plugin.getLogger().info("Cannot look for updates: " + e.getMessage());
            } catch (URISyntaxException e) {
                plugin.getLogger().info("Cannot look for updates: " + e.getMessage());
            }
        });
    }
}
