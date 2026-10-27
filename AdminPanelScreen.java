package com.adminpanel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AdminPanelScreen extends Screen {
    private static final int W = 320;
    private static final int H = 20;

    private TextFieldWidget playerField;
    private TextFieldWidget itemField;
    private TextFieldWidget countField;
    private int left;

    public AdminPanelScreen() {
        super(Text.literal("Admin Panel"));
    }

    @Override
    protected void init() {
        left = this.width / 2 - W / 2;
        int top = Math.max(10, this.height / 2 - 105);

        // --- Gracz ---
        playerField = new TextFieldWidget(this.textRenderer, left + 50, top + 18, W - 50, H, Text.literal("Nick"));
        playerField.setMaxLength(16);
        playerField.setPlaceholder(Text.literal("Nick gracza"));
        addDrawableChild(playerField);

        int bw = 77;
        int y = top + 42;
        addDrawableChild(btn("Kick", left, y, bw, () -> withPlayer("kick ")));
        addDrawableChild(btn("Ban", left + (bw + 4), y, bw, () -> withPlayer("ban ")));
        addDrawableChild(btn("TP do gracza", left + 2 * (bw + 4), y, bw, () -> withPlayer("tp ")));
        addDrawableChild(btn("Gracz do mnie", left + 3 * (bw + 4), y, bw, () -> {
            String n = cleanName(playerField.getText());
            if (!n.isEmpty()) run("tp " + n + " @s");
        }));

        // --- Give ---
        y = top + 70;
        itemField = new TextFieldWidget(this.textRenderer, left + 40, y, 170, H, Text.literal("Przedmiot"));
        itemField.setMaxLength(100);
        itemField.setPlaceholder(Text.literal("diamond_sword"));
        addDrawableChild(itemField);

        countField = new TextFieldWidget(this.textRenderer, left + 214, y, 40, H, Text.literal("Ilość"));
        countField.setMaxLength(4);
        countField.setText("1");
        addDrawableChild(countField);

        addDrawableChild(btn("Give", left + 258, y, 62, () -> {
            String id = cleanId(itemField.getText());
            if (id.isEmpty()) return;
            int count = 1;
            try {
                count = Integer.parseInt(countField.getText().trim());
            } catch (NumberFormatException ignored) {
            }
            count = Math.max(1, Math.min(6400, count));
            run("give @s " + id + " " + count);
        }));

        // --- Tryb gry ---
        y = top + 98;
        int gw = 66;
        addDrawableChild(btn("Survival", left + 40, y, gw, () -> run("gamemode survival")));
        addDrawableChild(btn("Creative", left + 40 + (gw + 4), y, gw, () -> run("gamemode creative")));
        addDrawableChild(btn("Adventure", left + 40 + 2 * (gw + 4), y, gw, () -> run("gamemode adventure")));
        addDrawableChild(btn("Spectator", left + 40 + 3 * (gw + 4), y, gw, () -> run("gamemode spectator")));

        // --- Czas ---
        y = top + 126;
        addDrawableChild(btn("Dzień", left + 40, y, gw, () -> run("time set day")));
        addDrawableChild(btn("Południe", left + 40 + (gw + 4), y, gw, () -> run("time set noon")));
        addDrawableChild(btn("Noc", left + 40 + 2 * (gw + 4), y, gw, () -> run("time set night")));
        addDrawableChild(btn("Północ", left + 40 + 3 * (gw + 4), y, gw, () -> run("time set midnight")));

        // --- Pogoda ---
        y = top + 154;
        int ww = 90;
        addDrawableChild(btn("Czysto", left + 40, y, ww, () -> run("weather clear")));
        addDrawableChild(btn("Deszcz", left + 40 + (ww + 4), y, ww, () -> run("weather rain")));
        addDrawableChild(btn("Burza", left + 40 + 2 * (ww + 4), y, ww, () -> run("weather thunder")));

        // --- Zamknij ---
        addDrawableChild(btn("Zamknij", this.width / 2 - 40, top + 184, 80, this::close));
    }

    private ButtonWidget btn(String label, int x, int y, int w, Runnable action) {
        return ButtonWidget.builder(Text.literal(label), b -> action.run())
                .dimensions(x, y, w, H)
                .build();
    }

    private void withPlayer(String prefix) {
        String n = cleanName(playerField.getText());
        if (!n.isEmpty()) run(prefix + n);
    }

    private void run(String command) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() != null) {
            client.getNetworkHandler().sendChatCommand(command);
        }
    }

    // Tylko znaki dozwolone w nicku / id przedmiotu, żeby nie dało się wstrzyknąć własnej komendy.
    private static String cleanName(String s) {
        return s.trim().replaceAll("[^A-Za-z0-9_]", "");
    }

    private static String cleanId(String s) {
        return s.trim().toLowerCase().replaceAll("[^a-z0-9_:./-]", "");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int top = Math.max(10, this.height / 2 - 105);
        int gray = 0xFFAAAAAA;
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, top, 0xFFFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Gracz:"), left, top + 24, gray);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Give:"), left, top + 76, gray);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Tryb:"), left, top + 104, gray);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Czas:"), left, top + 132, gray);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Pogoda:"), left, top + 160, gray);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
