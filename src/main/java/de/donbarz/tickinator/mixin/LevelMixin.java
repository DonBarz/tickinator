package de.donbarz.tickinator.mixin;

import de.donbarz.tickinator.Tickinator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Level.class)
public abstract class LevelMixin {

    @Shadow
    public abstract BlockState getBlockState(BlockPos blockPos);

    // Injects at the ticking of block entities during the server ticks
    // Skips ticks for blocks specified in the tags
    @Redirect(method = "tickBlockEntities()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V"))
    private void tickSelectedBlockEntities(TickingBlockEntity blockEntity) {
        if (getBlockState(blockEntity.getPos()).is(Tickinator.EXCLUDE) && !getBlockState(blockEntity.getPos()).is(Tickinator.INCLUDE)) {
            blockEntity.tick();
        }
        else Tickinator.LOGGER.info("Suppressed tick of \"" + blockEntity.getType() + "\" at Position " + blockEntity.getPos());
    }
}
