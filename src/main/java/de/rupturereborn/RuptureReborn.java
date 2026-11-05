package de.rupturereborn;

import de.rupturereborn.gui.ClickGuiScreen;
import de.rupturereborn.module.Module;
import de.rupturereborn.module.ModuleManager;
import de.rupturereborn.module.impl.Gui;
import de.rupturereborn.module.impl.Hud;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.StringJoiner;

@Mod(RuptureReborn.MODID)
public class RuptureReborn {
    public static final String MODID = "rupturereborn";
    public static final String NAME = "Rupture Reborn";
    public static final ModuleManager MODULES = new ModuleManager();

    /** Tasten, die die ClickGUI öffnen. */
    private static final int[] GUI_KEYS = {GLFW.GLFW_KEY_RIGHT_SHIFT, GLFW.GLFW_KEY_INSERT, GLFW.GLFW_KEY_K};

    private static final boolean[] KEY_DOWN = new boolean[GLFW.GLFW_KEY_LAST + 1];
    private static volatile boolean destructed;
    private static float lastStrength = 1f;

    public RuptureReborn() {
        TickEvent.ClientTickEvent.Post.BUS.addListener(RuptureReborn::onClientTick);
    }

    /** SelfDestruct: alles ausschalten, GUI schließen, Client bis zum Neustart stilllegen. */
    public static void destruct(Minecraft mc) {
        destructed = true;
        for (Module m : MODULES.all()) m.setEnabled(false);
        if (mc.screen instanceof ClickGuiScreen) mc.setScreen(null);
    }

    private static void onClientTick(TickEvent.ClientTickEvent.Post event) {
        if (destructed) return;
        Minecraft mc = Minecraft.getInstance();
        pollKeys(mc);
        if (mc.player == null || mc.level == null) return;

        // Treffer erkennen (Angriffs-Cooldown fällt abrupt ab) -> für WTap/STap
        float strength = mc.player.getAttackStrengthScale(0f);
        if (lastStrength > 0.8f && strength < 0.3f && mc.hitResult instanceof EntityHitResult hit) {
            for (Module m : MODULES.all()) {
                if (m.isEnabled()) m.onAttack(mc, hit.getEntity());
            }
        }
        lastStrength = strength;

        for (Module m : MODULES.all()) {
            if (m.isEnabled()) m.onTick(mc);
        }

        drawHud(mc);
    }

    private static void pollKeys(Minecraft mc) {
        long window = GLFW.glfwGetCurrentContext();
        if (window == 0L) return;
        boolean canAct = mc.screen == null && mc.level != null && mc.player != null;

        for (int key : GUI_KEYS) {
            if (edge(window, key) && canAct) mc.setScreen(new ClickGuiScreen());
        }
        for (Module m : MODULES.all()) {
            int key = m.getKey();
            if (key <= 0 || key > GLFW.GLFW_KEY_LAST) continue;
            if (edge(window, key) && canAct) m.toggle();
        }
    }

    /** true genau in dem Tick, in dem die Taste neu gedrückt wurde. */
    private static boolean edge(long window, int key) {
        boolean now = GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
        boolean pressed = now && !KEY_DOWN[key];
        KEY_DOWN[key] = now;
        return pressed;
    }

    /** Anzeige der aktiven Module über der Hotbar (Action-Bar). */
    private static void drawHud(Minecraft mc) {
        Hud hud = MODULES.get(Hud.class);
        if (hud == null || !hud.isEnabled()) return;
        StringJoiner names = new StringJoiner(" ");
        for (Module m : MODULES.all()) {
            if (m.isEnabled() && !(m instanceof Hud) && !(m instanceof Gui)) names.add(m.getName());
        }
        if (names.length() == 0) return;
        mc.gui.setOverlayMessage(Component.literal("§d" + NAME + " §7| §f" + names), false);
    }
}
