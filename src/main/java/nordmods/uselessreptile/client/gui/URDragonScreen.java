package nordmods.uselessreptile.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;

public abstract class URDragonScreen<T extends ScreenHandler> extends HandledScreen<T> {
    protected static final Identifier TEXTURE = new Identifier(UselessReptile.MODID,"textures/gui/dragon_inventory.png");
    private int mouseX;
    private int mouseY;
    private final URDragonEntity entity;
    private int i;
    private int j;
    protected boolean hasArmor = false;
    protected boolean hasSaddle = false;
    protected boolean hasBanner = false;
    public static int entityToRenderID;
    protected URDragonScreenHandler.StorageSize storageSize = URDragonScreenHandler.StorageSize.NONE;

    public URDragonScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        PlayerEntity player = inventory.player;
        entity = (URDragonEntity) player.getWorld().getEntityById(entityToRenderID);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        i = (width - backgroundWidth) / 2;
        j = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, backgroundWidth, backgroundHeight);
        drawSaddle(context);
        drawBanner(context);
        drawArmor(context);
        drawStorage(context);
        drawEntity(context);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    protected void drawSaddle(DrawContext context) {
        if (hasSaddle) context.drawTexture(TEXTURE, i + 7, j + 35 - 18, 0, backgroundHeight + 54, 18, 18); //saddle
    }

    protected void drawArmor(DrawContext context) {
        if (hasArmor) {
            context.drawTexture(TEXTURE, i + 7 + 18 + 54, j + 35 - 18, 18, backgroundHeight + 54, 18, 18); //head
            context.drawTexture(TEXTURE, i + 7 + 18 + 54, j + 35, 18 * 2, backgroundHeight + 54, 18, 18); //body
            context.drawTexture(TEXTURE, i + 7 + 18 + 54, j + 35 + 18, 18 * 3, backgroundHeight + 54, 18, 18); //tail
        }
    }

    protected void drawEntity(DrawContext context) {
        //if (entity != null) InventoryScreen.drawEntity(context, i + 51, j + 68, 13, i + 51 - mouseX, j + 75 - 50 - mouseY, entity);
        if (entity != null) InventoryScreen.drawEntity(context, i + 26, j + 18, i + 78, j + 70, 13, 0.25F, this.mouseX, this.mouseY, this.entity);
    }

    protected void drawStorage(DrawContext context) {
        int size = storageSize.getSize()/3;
        int offset = hasArmor ? 1 : 0;
        context.drawTexture(TEXTURE, i + 79 + 18 * offset, j + 17, 0, this.backgroundHeight, size * 18, 54);
    }

    protected void drawBanner(DrawContext context) {
        if (hasBanner) context.drawTexture(TEXTURE, i + 7, j + 35, 18 * 4, backgroundHeight + 54, 18, 18); //banner
    }
}
