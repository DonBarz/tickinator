package de.donbarz.tickinator.mixin;

import de.donbarz.tickinator.Tickinator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin (targets = "net.minecraft.world.level.chunk.LevelChunk$BoundTickingBlockEntity")
public class LevelChunkMixin {

    // Injects at the ticking of block entities during the server ticks, but keeps the important functionality (still checks if the block is still valid)
    // Skips ticks for blocks specified in the tags
    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntityTicker;tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;)V"))
    private void tickSelectedBlockEntities(BlockEntityTicker instance, Level level, BlockPos blockPos, BlockState blockState, BlockEntity t) {
        if (blockState.is(Tickinator.EXCLUDE) || !blockState.is(Tickinator.INCLUDE)) {
            instance.tick(level, blockPos, blockState, t);
        }
        // else Tickinator.LOGGER.info("Suppressed tick of \"" + blockState.getBlock().toString() + "\" at Position " + blockPos);
    }
}
