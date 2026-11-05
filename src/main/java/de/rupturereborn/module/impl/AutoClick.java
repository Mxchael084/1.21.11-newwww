package de.rupturereborn.module.impl;

import de.rupturereborn.module.CombatModule;
import de.rupturereborn.module.setting.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;

import java.util.concurrent.ThreadLocalRandom;

/** Klickt automatisch, solange die linke Maustaste gehalten wird und ein Ziel im Fadenkreuz ist. */
public class AutoClick extends CombatModule {
    private final SliderSetting minCps = add(new SliderSetting("Min CPS", 1, 20, 8, 0));
    private final SliderSetting maxCps = add(new SliderSetting("Max CPS", 1, 20, 12, 0));
    private long nextClick;

    public AutoClick() { super("AutoClick", "Automatisches Klicken mit einstellbarer CPS (nur lokal)"); }

    @Override
    protected void tick(Minecraft mc) {
        if (mc.screen != null || !mc.options.keyAttack.isDown()) return;
        if (!(mc.hitResult instanceof EntityHitResult hit)) return;
        long now = System.currentTimeMillis();
        if (now < nextClick) return;

        mc.gameMode.attack(mc.player, hit.getEntity());
        mc.player.swing(InteractionHand.MAIN_HAND);

        double lo = Math.min(minCps.get(), maxCps.get());
        double hi = Math.max(minCps.get(), maxCps.get());
        double cps = lo + ThreadLocalRandom.current().nextDouble() * (hi - lo);
        nextClick = now + (long) (1000.0 / Math.max(1.0, cps));
    }
}
