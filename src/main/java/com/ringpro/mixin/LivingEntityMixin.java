package com.ringpro.mixin;

import com.ringpro.EasyDeathsMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyArgs(
            method = "createItemEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;setVelocity(DDD)V", ordinal = 0)
    )
    private void multiplySpread(Args args){
        if (this.isPlayer()) {
            double spreadMultiplier = ((ServerWorld) this.getEntityWorld()).getGameRules().get(EasyDeathsMod.DEATH_ITEMS_SPREAD_AMOUNT).get();
            for (int i = 0; i < args.size(); i++) args.set(i, (double) args.get(i) * spreadMultiplier);
        }
    }

}