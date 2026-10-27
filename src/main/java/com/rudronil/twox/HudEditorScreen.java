package com.rudronil.twox;

import com.rudronil.twox.module.Module;
import com.rudronil.twox.module.ModuleManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Drag any HUD element to move it. Positions are saved to config/2x-client.json. */
public class HudEditorScreen extends Screen {
    private Module dragging;
    private double accX, accY;

    public HudEditorScreen() {
        super(Component.literal("Edit HUD"));
    }

    @Override
    protected void init() {
        addRenderableWidget(Button.builder(Component.literal("Done"),
                b -> this.minecraft.setScreen(new ClickGuiScreen()))
                .bounds(this.width / 2 - 104, this.height - 28, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Reset Positions"), b -> {
            ModuleManager.resetOffsets();
            ModuleManager.save();
        }).bounds(this.width / 2 + 4, this.height - 28, 100, 20).build());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        // No blur here so the HUD stays sharp while editing
        g.fill(0, 0, this.width, this.height, 0x90000000);
        ModuleManager.renderHud(g, true);

        for (Module m : ModuleManager.all()) {
            if (!m.isEnabled() || !m.hasBounds()) continue;
            boolean hot = m == dragging || (dragging == null && hit(m, mouseX, mouseY));
            outline(g, m.boundsX0() - 1, m.boundsY0() - 1, m.boundsX1() + 1, m.boundsY1() + 1,
                    hot ? 0xFF55FFFF : 0x80FFFFFF);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractRenderState(g, mouseX, mouseY, delta);
        g.centeredText(this.font, "Drag HUD elements to move them", this.width / 2, 10, 0xFF55FFFF);
        g.centeredText(this.font, "Only enabled modules can be moved. Grey [names] are modules with nothing to show yet.",
                this.width / 2, 22, 0xFFAAAAAA);
    }

    private static boolean hit(Module m, double mx, double my) {
        return mx >= m.boundsX0() && mx <= m.boundsX1() && my >= m.boundsY0() && my <= m.boundsY1();
    }

    private static void outline(GuiGraphicsExtractor g, int x0, int y0, int x1, int y1, int c) {
        g.fill(x0, y0, x1, y0 + 1, c);
        g.fill(x0, y1 - 1, x1, y1, c);
        g.fill(x0, y0, x0 + 1, y1, c);
        g.fill(x1 - 1, y0, x1, y1, c);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true; // buttons first
        if (event.button() == 0) {
            List<Module> mods = ModuleManager.all();
            for (int i = mods.size() - 1; i >= 0; i--) {
                Module m = mods.get(i);
                if (m.isEnabled() && m.hasBounds() && hit(m, event.x(), event.y())) {
                    dragging = m;
                    accX = 0;
                    accY = 0;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (dragging == null) return super.mouseDragged(event, dx, dy);
        accX += dx;
        accY += dy;
        int ix = (int) accX, iy = (int) accY;
        accX -= ix;
        accY -= iy;
        // keep the element fully on screen
        if (dragging.boundsX0() + ix < 0 || dragging.boundsX1() + ix > this.width) ix = 0;
        if (dragging.boundsY0() + iy < 0 || dragging.boundsY1() + iy > this.height) iy = 0;
        dragging.offX += ix;
        dragging.offY += iy;
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (dragging != null) {
            dragging = null;
            ModuleManager.save();
        }
        return super.mouseReleased(event);
    }

    @Override
    public void onClose() {
        ModuleManager.save();
        this.minecraft.setScreen(new ClickGuiScreen());
    }
}
