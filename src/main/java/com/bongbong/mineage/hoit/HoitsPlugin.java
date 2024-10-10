package com.bongbong.mineage.hoit;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class HoitsPlugin extends JavaPlugin {

    private static World WORLD = Bukkit.getWorld("");
    private static Location SPAWN_LOCATION = new Location(WORLD, 0, 0, 0, 0, 0);
    private static String MOB_1 = "", MOB_2 = "";
    private static ZoneId ZONE_ID = ZoneId.of("America/New_York");


    @Override
    public void onEnable() {
        if (MythicBukkit.inst() == null) {
            System.out.println("Mythic mobs not found, shutting down");
            onDisable();
        }

        runTaskDailyAtTime(5, ZONE_ID, () -> {
            ActiveMob mob = getMob(MOB_1);
            if (mob == null) getLogger().log(Level.WARNING, "Mob did not spawn because it does not exist.");
        });
    }

    private ActiveMob getMob(String rawMob) {
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(rawMob).orElse(null);
        if (mob != null)
            return mob.spawn(BukkitAdapter.adapt(SPAWN_LOCATION),1);

        return null;
    }

    /**
     * @param time The time of day on a 24-hour clock to run it (Ex: 14 for 2pm).
     */
    private void runTaskDailyAtTime(int time, ZoneId zoneId, Runnable runnable) {
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime nextRun = now.withHour(time).withMinute(0).withSecond(0);
        if(now.compareTo(nextRun) > 0)
            nextRun = nextRun.plusDays(1);

        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(runnable,
                initialDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS);
    }
}
