package de.rupturereborn.module.impl;

import de.rupturereborn.module.Module;
import de.rupturereborn.util.Keys;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class Sprint extends Module {
    public Sprint() { super("Sprint", "Sprintet automatisch beim Vorwärtslaufen", GLFW.GLFW_KEY_G); }

    @Override
    public void onTick(Minecraft mc) {
        var p = mc.player;
        if (Keys.forward(mc) > 0 && !p.horizontalCollision && !p.isUsingItem() && p.getFoodData().getFoodLevel() > 6) {
            p.setSprinting(true);
        }
    }
}
