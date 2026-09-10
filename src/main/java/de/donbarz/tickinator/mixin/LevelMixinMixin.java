package de.donbarz.tickinator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.donbarz.tickinator.Tickinator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import observable.mixin.LevelMixin; // very important line for some reason
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Level.class)
public class LevelMixinMixin{

    // Injects at the ticking of block entities during the server ticks, but keeps the important functionality (still checks if the block is still valid)
    // Skips ticks for blocks specified in the tags
    @WrapOperation(
            method = {"tickBlockEntities"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V"
            )
    )    private void tickSelectedBlockEntities(TickingBlockEntity blockEntity, Operation<Void> original) {
        Level self = (Level)(Object)this;
        BlockState blockState = self.getBlockState(blockEntity.getPos());
        if (blockState.is(Tickinator.EXCLUDE) || !blockState.is(Tickinator.INCLUDE)) {
            original.call(blockEntity);
        }
        // else Tickinator.LOGGER.info("Suppressed tick of \"" + blockState.getBlock().toString() + "\" at Position " + blockEntity.getPos());
    }
}
