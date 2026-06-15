package me.lofn.realisticMechanics.core.calendar;

import me.lofn.realisticMechanics.utils.TimeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class CalendarCommand implements CommandExecutor {

    private static final int DAYS_PER_YEAR = 96;

    private static final String[] MEDIEVAL_DAYS = {
            "Sunrest",
            "Moonsday",
            "Ironstag",
            "Wyrmday",
            "Oakentide",
            "Kingsday",
            "Emberwane",
            "Halloweve"
    };

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage("Only players can use this.");
            return true;
        }

        long worldDay = TimeUtils.getWorldDay(player.getWorld());

        long year = ((worldDay - 1) / DAYS_PER_YEAR) + 1;
        long dayOfYear = ((worldDay - 1) % DAYS_PER_YEAR) + 1;

        String dayName = MEDIEVAL_DAYS[(int) ((worldDay - 1) % MEDIEVAL_DAYS.length)];

        player.sendMessage("§eIt is " + dayName + " and the year is " + year + " The moon's shape is " + TimeUtils.getMoonPhaseName(player.getWorld()) + ".");

        if(TimeUtils.isFullMoon(player.getWorld())) {
            player.sendMessage("§cFull moon is empowering the creatures of the night!");
        }

        return true;
    }
}
