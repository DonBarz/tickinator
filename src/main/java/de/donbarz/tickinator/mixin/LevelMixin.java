package de.donbarz.tickinator.mixin;

import de.donbarz.tickinator.Tickinator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.logging.Logger;

@Mixin(Level.class)
public abstract class LevelMixin {

    //@Inject( method = "tickBlockEntities()V", at = @At( "HEAD"))
    //protected void tickBlockEntities0(CallbackInfo ci) {
    //    Tickinator.LOGGER.info("skibidy");
    //}

    @Shadow
    public abstract BlockState getBlockState(BlockPos blockPos);

    @Redirect(method = "tickBlockEntities()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V"))
    private void injected(TickingBlockEntity blockEntity) {
        Tickinator.LOGGER.info(blockEntity.getType() + ", " + blockEntity.getPos());

        if (getBlockState(blockEntity.getPos()).is(Tickinator.EXCLUDE) && ! getBlockState(blockEntity.getPos()).is(Tickinator.INCLUDE)) {
            blockEntity.tick();
        }
    }

}
