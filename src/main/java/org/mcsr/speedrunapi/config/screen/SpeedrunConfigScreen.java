package org.mcsr.speedrunapi.config.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.Nullable;
import org.mcsr.speedrunapi.SpeedrunAPI;
import org.mcsr.speedrunapi.config.SpeedrunConfigContainer;
import org.mcsr.speedrunapi.config.screen.widgets.list.SpeedrunOptionListWidget;

import java.io.IOException;
import java.util.function.Predicate;

public class SpeedrunConfigScreen extends Screen {

    private final SpeedrunConfigContainer<?> config;
    @Nullable
    private final Predicate<InputUtil.Key> inputListener;
    private final Screen parent;
    private SpeedrunOptionListWidget list;

    public SpeedrunConfigScreen(SpeedrunConfigContainer<?> config, @Nullable Predicate<InputUtil.Key> inputListener, Screen parent) {
        super(new LiteralText(config.getModContainer().getMetadata().getName()));
        this.config = config;
        this.inputListener = inputListener;
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.list = new SpeedrunOptionListWidget(this.config, this, this.client, this.width, this.height, 25, this.height - 32);
        this.addChild(this.list);
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, button -> this.onClose()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.list.render(matrices, mouseX, mouseY, delta);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.inputListener != null && this.inputListener.test(InputUtil.fromKeyCode(keyCode, scanCode))) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.inputListener != null && this.inputListener.test(InputUtil.Type.MOUSE.createFromCode(button))) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        assert this.client != null;
        this.client.openScreen(this.parent);
    }

    @Override
    public void removed() {
        try {
            this.config.save();
        } catch (IOException e) {
            SpeedrunAPI.LOGGER.warn("Failed to save config file for {}.", this.config.getConfig().modID());
        }
    }
}
