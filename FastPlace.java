package de.rupturereborn.module.impl;

import de.rupturereborn.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/** Setzt Blöcke jeden Tick, solange die rechte Maustaste gehalten wird. */
public class FastPlace extends Module {
    public FastPlace() { super("FastPlace", "Blöcke ohne Verzögerung setzen"); }

    @Override
    public void onTick(Minecraft mc) {
        var p = mc.player;
        if (mc.screen != null || !mc.options.keyUse.isDown()) return;
        if (!(p.getMainHandItem().getItem() instanceof BlockItem)) return;
        if (mc.hitResult instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK) {
            InteractionResult result = mc.gameMode.useItemOn(p, InteractionHand.MAIN_HAND, hit);
            if (result instanceof InteractionResult.Success) p.swing(InteractionHand.MAIN_HAND);
        }
    }
}
