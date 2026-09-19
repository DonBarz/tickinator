package de.donbarz.tickinator;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tickinator implements ModInitializer {
    public static final String MOD_ID = "tickinator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static BlockConfig BLOCK_CONFIG = new BlockConfig();

    @Override
    public void onInitialize() {
        BLOCK_CONFIG.reload();

        // adding command to reload the config at runtime
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("tickinator")
                    .then(Commands.literal("reload").executes(context -> {
                        BLOCK_CONFIG.reload();
                        return 0;
                    })));
        });
    }
}
