package com.rudronil.twox;

import com.rudronil.twox.module.Module;
import com.rudronil.twox.module.ModuleManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModMenuScreen extends Screen {
    public ModMenuScreen() {
        super(Component.literal("2X Client"));
    }

    @Override
    protected void init() {
        int col = 0, row = 0;
        int w = 150, h = 20, gap = 6;
        int startX = this.width / 2 - w - gap / 2;
        int startY = 50;
        for (Module m : ModuleManager.all()) {
            int x = startX + col * (w + gap);
            int y = startY + row * (h + gap);
            addRenderableWidget(Button.builder(label(m), b -> {
                m.toggle();
                b.setMessage(label(m));
                ModuleManager.save();
            }).bounds(x, y, w, h).build());
            if (++col == 2) { col = 0; row++; }
        }
    }

    private static Component label(Module m) {
        return Component.literal(m.getName() + ": " + (m.isEnabled() ? "ON" : "OFF"));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        g.drawCenteredString(this.font, "2X Client  -  by " + TwoXClient.AUTHOR, this.width / 2, 20, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        ModuleManager.save();
        super.onClose();
    }
}
