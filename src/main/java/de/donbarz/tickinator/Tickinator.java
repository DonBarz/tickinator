package de.donbarz.tickinator;

import dev.lemonnik.fern_config.utils.Mask;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class Tickinator implements ModInitializer {
    public static final String MOD_ID = "tickinator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static BlockConfig BLOCK_CONFIG = new BlockConfig();

    @Override
    public void onInitialize() {
        LOGGER.info(String.valueOf(BLOCK_CONFIG.reload()));

        // adding command to reload the config at runtime
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("tickinator")
                    .then(Commands.literal("reload").executes(context -> {
                        BLOCK_CONFIG.reload();
                        context.getSource().getPlayerOrException().sendSystemMessage(Component.literal("Reloaded block entity suppression!"));
                        return 0;
                    }))
                    .then(Commands.literal("suppress").then(Commands.argument("block", BlockStateArgument.block(registryAccess))
                            .then(Commands.literal("add").executes(context -> {
                                Block block = BlockStateArgument.getBlock(context, "block").getState().getBlock();
                                context.getSource().getPlayerOrException().sendSystemMessage(Component.literal("Now suppressing ").append(block.getName()));


                                // add a block to the existing lists
                                ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);

                                if (BLOCK_CONFIG.exclude_mask.get().matches(blockId)) {
                                    String[] newMaskString = BLOCK_CONFIG.exclude_mask.get().getEntries().stream().map(ResourceLocation::toString).filter(string -> !string.equals(blockId.toString())).toArray(String[]::new);
                                    BLOCK_CONFIG.updateExcludeMask(newMaskString);
                                }
                                if (!BLOCK_CONFIG.include_mask.get().matches(blockId)) {
                                    ArrayList<ResourceLocation> newEntries = new ArrayList<>(BLOCK_CONFIG.include_mask.get().getEntries().stream().toList());
                                    newEntries.add(blockId);
                                    String[] newMaskString = newEntries.stream().map(ResourceLocation::toString).toArray(String[]::new);
                                    BLOCK_CONFIG.updateIncludeMask(newMaskString);
                                }

                                BLOCK_CONFIG.save();
                                LOGGER.info(String.valueOf(BLOCK_CONFIG.reload()));

                                return 0;
                            }))
                            .then(Commands.literal("remove").executes(context -> {
                                Block block = BlockStateArgument.getBlock(context, "block").getState().getBlock();
                                context.getSource().getPlayerOrException().sendSystemMessage(Component.literal("No longer suppressing ").append(block.getName()));

                                // remove a block from the existing lists
                                ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);

                                if (BLOCK_CONFIG.include_mask.get().matches(blockId)) {
                                    String[] newMaskString = BLOCK_CONFIG.include_mask.get().getEntries().stream().map(ResourceLocation::toString).filter(string -> !string.equals(blockId.toString())).toArray(String[]::new);
                                    BLOCK_CONFIG.updateIncludeMask(newMaskString);
                                }

                                BLOCK_CONFIG.save();
                                LOGGER.info(String.valueOf(BLOCK_CONFIG.reload()));

                                return 0;
                            }))
                    ))
            );
        });
    }
}
