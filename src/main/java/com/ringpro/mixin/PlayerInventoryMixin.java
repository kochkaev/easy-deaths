package com.ringpro.mixin;

import com.ringpro.EasyDeathsMod;
import com.ringpro.access.ItemEntityInterface;
import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Final @Shadow private DefaultedList<ItemStack> main;
    @Final @Shadow private EntityEquipment equipment;

    @Inject(method = "dropAll", at = @At(value = "HEAD"), cancellable = true)
    private void dropAllInject(CallbackInfo ci) {
        for (int i = 0; i < main.size(); ++i) {
            ItemStack itemStack = main.get(i);
            if (!itemStack.isEmpty()) {
                ItemEntity itemEntity = ((PlayerInventory) (Object) this).player.dropItem(itemStack, true, false);
                main.set(i, ItemStack.EMPTY);
                if (itemEntity != null) processItemEntity(itemEntity);
            }
        }
        for (var slot : EquipmentSlot.VALUES) {
            ItemStack itemStack = equipment.get(slot);
            if (!itemStack.isEmpty()) {
                ItemEntity itemEntity = ((PlayerInventory) (Object) this).player.dropItem(itemStack, true, false);
                equipment.put(slot, ItemStack.EMPTY);
                if (itemEntity != null) processItemEntity(itemEntity);
            }
        }
        ci.cancel();
    }
    @Unique
    private void processItemEntity(ItemEntity entity) {
        var world = Objects.requireNonNull(entity.getServer()).getWorld(entity.getEntityWorld().getRegistryKey());
        assert world != null;
        boolean isInvulnerable = world.getGameRules().get(EasyDeathsMod.DEATH_ITEMS_INVULNERABLE).get();
        boolean itemNeverDespawn = world.getGameRules().get(EasyDeathsMod.DEATH_ITEMS_NEVER_DESPAWN).get();
        boolean itemShouldGlow = world.getGameRules().get(EasyDeathsMod.DEATH_ITEMS_GLOW).get();

        if (itemNeverDespawn) {
            entity.setNeverDespawn();
        }
        if (isInvulnerable) {
            ((ItemEntityInterface) entity).setAcutallyInvulnerable(true);
        }
        if (itemShouldGlow) {
            ((ItemEntityInterface) entity).setShouldGlow(true);
        }
    }

}
