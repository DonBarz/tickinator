package de.donbarz.tickinator;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tickinator implements ModInitializer {
    public static final String MOD_ID = "tickinator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final TagKey<Block> INCLUDE = TagKey.create(Registries.BLOCK, ResourceLocation.tryBuild("tickinator", "include"));
    public static final TagKey<Block> EXCLUDE = TagKey.create(Registries.BLOCK, ResourceLocation.tryBuild("tickinator", "exclude"));

    @Override
    public void onInitialize() {

    }
}
