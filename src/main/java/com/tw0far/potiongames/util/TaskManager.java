package com.tw0far.potiongames.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized scheduler task management.
 * 
 * FIXES:
 * - Tasks not canceled on disable causing memory leaks
 * - No tracking of scheduled tasks
 * - Task IDs lost after scheduling
 * 
 * FEATURES:
 * - Track all scheduled tasks
 * - Automatic cleanup on shutdown
 * - Safe task cancellation
 * - Debugging/diagnostics
 */
public class TaskManager {
    private final Plugin plugin;
    private final Logger logger;
    private final Set<BukkitTask> activeTasks = new HashSet<>();
    
    public TaskManager(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }
    
    /**
     * Schedule async task with tracking
     */
    public BukkitTask scheduleAsync(Runnable runnable, long delayTicks, long periodTicks) {
        try {
            // Use the newer scheduler API to run async repeating tasks and track them
            BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin,
                runnable,
                delayTicks,
                periodTicks
            );

            if (task != null) {
                activeTasks.add(task);
                logger.log(Level.FINE, "Scheduled async task: " + task.getTaskId());
            }

            return task;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to schedule async task", e);
            return null;
        }
    }
    
    /**
     * Schedule sync task with tracking
     */
    public BukkitTask scheduleSyncRepeating(Runnable runnable, long delayTicks, long periodTicks) {
        try {
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                plugin,
                runnable,
                delayTicks,
                periodTicks
            );
            activeTasks.add(task);
            logger.log(Level.FINE, "Scheduled sync repeating task: " + task.getTaskId());
            return task;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to schedule sync repeating task", e);
            return null;
        }
    }
    
    /**
     * Schedule delayed task with tracking
     */
    public BukkitTask scheduleDelayed(Runnable runnable, long delayTicks) {
        try {
            BukkitTask task = Bukkit.getScheduler().runTaskLater(
                plugin,
                runnable,
                delayTicks
            );
            activeTasks.add(task);
            logger.log(Level.FINE, "Scheduled delayed task: " + task.getTaskId());
            return task;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to schedule delayed task", e);
            return null;
        }
    }
    
    /**
     * Schedule immediate task with tracking
     */
    public BukkitTask scheduleImmediate(Runnable runnable) {
        try {
            BukkitTask task = Bukkit.getScheduler().runTask(plugin, runnable);
            activeTasks.add(task);
            logger.log(Level.FINE, "Scheduled immediate task: " + task.getTaskId());
            return task;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to schedule immediate task", e);
            return null;
        }
    }
    
    /**
     * Cancel a specific task
     */
    public void cancel(BukkitTask task) {
        if (task == null) return;
        
        try {
            task.cancel();
            activeTasks.remove(task);
            logger.log(Level.FINE, "Canceled task: " + task.getTaskId());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error canceling task", e);
        }
    }
    
    /**
     * Cancel all tracked tasks
     */
    public void cancelAll() {
        logger.info("Canceling " + activeTasks.size() + " scheduled tasks...");
        
        for (BukkitTask task : new HashSet<>(activeTasks)) {
            try {
                if (task != null && !task.isCancelled()) {
                    task.cancel();
                }
                activeTasks.remove(task);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error canceling task", e);
            }
        }
        
        activeTasks.clear();
        logger.info("All tasks canceled");
    }
    
    /**
     * Get count of active tasks
     */
    public int getActiveTaskCount() {
        return activeTasks.size();
    }
    
    /**
     * Debugging: Print all active tasks
     */
    public void debugPrintTasks() {
        logger.info("=== Active Tasks ===");
        for (BukkitTask task : activeTasks) {
            logger.info("  Task " + task.getTaskId() + ": " + (task.isCancelled() ? "CANCELLED" : "RUNNING"));
        }
        logger.info("Total: " + activeTasks.size());
    }
    
    /**
     * Clean up on shutdown
     */
    public void shutdown() {
        cancelAll();
    }
}
