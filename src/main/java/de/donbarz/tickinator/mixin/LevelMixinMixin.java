package de.donbarz.tickinator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.donbarz.tickinator.Tickinator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import observable.Observable;
import observable.Props;
import observable.mixin.LevelMixin; // very important line for some reason
import observable.server.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Level.class)
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
        else {
            // Tickinator.LOGGER.info("Suppressed tick of \"" + blockState.getBlock().toString() + "\" at Position " + blockEntity.getPos());

            // This is just the Observable stuff copied over to maintain simplicity
            if (!Props.notProcessing) {
                if (Props.blockEntityDepth < 0) {
                    Props.blockEntityDepth = Thread.currentThread().getStackTrace().length - 1;
                }

                if (self instanceof ServerLevel) {
                    Profiler.TimingData data = Observable.INSTANCE.getPROFILER().processBlockEntity(blockEntity, self);
                    Props.currentTarget.set(data);
                    // if this is set to -1, the overlay for suppressed blocks won't render (might be useful >:))
                    data.setTime(0);
                    Props.currentTarget.set(null);
                    data.setTicks(data.getTicks() + 1);
                }
            }
        }
    }
}
