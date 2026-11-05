package de.rupturereborn.module;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;

/** Kampf-Module laufen nur, wenn eine lokale (Einzelspieler-/LAN-)Welt läuft. */
public abstract class CombatModule extends Module {
    protected CombatModule(String name, String description, int key) { super(name, description, key); }
    protected CombatModule(String name, String description) { super(name, description); }

    @Override
    public final void onTick(Minecraft mc) {
        if (!mc.hasSingleplayerServer()) return;
        tick(mc);
    }

    @Override
    public final void onAttack(Minecraft mc, Entity target) {
        if (!mc.hasSingleplayerServer()) return;
        attacked(mc, target);
    }

    protected abstract void tick(Minecraft mc);

    protected void attacked(Minecraft mc, Entity target) {}

    protected static LivingEntity nearestTarget(Minecraft mc, double range) {
        Player me = mc.player;
        LivingEntity best = null;
        double bestDist = range * range;
        for (var e : mc.level.entitiesForRendering()) {
            if (!(e instanceof LivingEntity le) || e == me || e instanceof ArmorStand || !le.isAlive()) continue;
            double d = me.distanceToSqr(le);
            if (d < bestDist) { bestDist = d; best = le; }
        }
        return best;
    }
}
