package de.donbarz.tickinator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.donbarz.tickinator.Tickinator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(blockState.getBlock());

        // elite level ferning
        if (!(Tickinator.BLOCK_CONFIG.exclude_mask.get().isOkay(blockId)) || !(Tickinator.BLOCK_CONFIG.include_mask.get().isOkay(blockId))) {
            original.call(blockEntity);
        }
        else {
            // Tickinator.LOGGER.info("Suppressed tick of \"" + blockState.getBlock().toString() + "\" at Position " + blockEntity.getPos());

            // optional dependency for observable
            if (FabricLoader.getInstance().isModLoaded("observable")) {
                // This is just the Observable stuff copied over
                if (!observable.Props.notProcessing) {
                    if (observable.Props.blockEntityDepth < 0) {
                        observable.Props.blockEntityDepth = Thread.currentThread().getStackTrace().length - 1;
                    }
                    if (self instanceof ServerLevel) {
                        observable.server.Profiler.TimingData data = observable.Observable.INSTANCE.getPROFILER().processBlockEntity(blockEntity, self);
                        observable.Props.currentTarget.set(data);
                        // this is the displayed observable impact
                        // if this is set to -1, the overlay for suppressed blocks won't render (might be useful >:))
                        data.setTime(0); // I'm going to be setting it to 0, it doesn't make a difference
                        observable.Props.currentTarget.set(null);
                        data.setTicks(data.getTicks() + 1);
                    }
                }
            }
        }
    }
}
