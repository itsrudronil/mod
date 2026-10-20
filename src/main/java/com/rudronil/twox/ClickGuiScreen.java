package com.rudronil.twox;

import com.rudronil.twox.module.Module;
import com.rudronil.twox.module.Module.Category;
import com.rudronil.twox.module.ModuleManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClickGuiScreen extends Screen {
    private static Category selected = Category.HUD;
    private static final int W = 390, H = 232;
    private int px, py;

    public ClickGuiScreen() {
        super(Component.literal("2X Client"));
    }

    @Override
    protected void init() {
        px = (this.width - W) / 2;
        py = (this.height - H) / 2;

        // Category tabs
        int i = 0;
        for (Category c : Category.values()) {
            boolean sel = c == selected;
            Component label = Component.literal(c.label).withStyle(sel ? ChatFormatting.AQUA : ChatFormatting.WHITE);
            addRenderableWidget(Button.builder(label, b -> {
                selected = c;
                rebuildWidgets();
            }).bounds(px + 8, py + 40 + i * 24, 82, 20).build());
            i++;
        }

        // Opens the drag-and-drop HUD editor
        addRenderableWidget(Button.builder(Component.literal("Edit HUD").withStyle(ChatFormatting.YELLOW),
                b -> this.minecraft.setScreen(new HudEditorScreen()))
                .bounds(px + 8, py + H - 28, 82, 20).build());

        // Module toggles for the selected category
        int n = 0;
        for (Module m : ModuleManager.inCategory(selected)) {
            int col = n % 2, row = n / 2;
            int x = px + 100 + col * 142;
            int y = py + 40 + row * 24;
            addRenderableWidget(Button.builder(label(m), b -> {
                m.toggle();
                b.setMessage(label(m));
                ModuleManager.save();
            }).bounds(x, y, 138, 20).build());
            n++;
        }
    }

    private static Component label(Module m) {
        return Component.literal(m.getName() + "  ")
                .append(Component.literal(m.isEnabled() ? "ON" : "OFF")
                        .withStyle(m.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.RED));
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.renderBackground(g, mouseX, mouseY, delta);
        g.fill(px, py, px + W, py + H, 0xE00F0F18);                // panel
        g.fill(px, py, px + W, py + 28, 0xFF1B2A41);               // header
        g.fill(px, py + 28, px + W, py + 30, 0xFF55FFFF);          // accent line
        g.fill(px, py + 30, px + 96, py + H, 0xFF14141E);          // sidebar
        g.drawString(this.font, "2X CLIENT", px + 10, py + 10, 0xFF55FFFF, true);
        String by = "by " + TwoXClient.AUTHOR;
        g.drawString(this.font, by, px + W - 10 - this.font.width(by), py + 10, 0xFFAAAAAA, true);
        g.drawString(this.font, selected.label, px + 100, py + 33 - 1, 0xFF888888, false);
    }

    @Override
    public void onClose() {
        ModuleManager.save();
        super.onClose();
    }
}
