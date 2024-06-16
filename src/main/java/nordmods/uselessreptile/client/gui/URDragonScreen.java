package nordmods.uselessreptile.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;

public abstract class URDragonScreen<T extends ScreenHandler> extends HandledScreen<T> {
    protected static final Identifier TEXTURE = UselessReptile.id("textures/gui/dragon_inventory.png");
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
        if (hasSaddle) context.drawTexture(TEXTURE, i + 7, j + 35 - 18, 0, backgroundHeight + 54 - (entity.getEquippedStack(EquipmentSlot.FEET).isEmpty() ? 0 : 18), 18, 18); //saddle
    }

    protected void drawArmor(DrawContext context) {
        if (hasArmor) {
            context.drawTexture(TEXTURE, i + 7 + 18 + 54, j + 35 - 18, 18, backgroundHeight + 54 - (entity.getEquippedStack(EquipmentSlot.HEAD).isEmpty() ? 0 : 18), 18, 18); //head
            context.drawTexture(TEXTURE, i + 7 + 18 + 54, j + 35, 18 * 2, backgroundHeight + 54 - (entity.getEquippedStack(EquipmentSlot.CHEST).isEmpty() ? 0 : 18), 18, 18); //body
            context.drawTexture(TEXTURE, i + 7 + 18 + 54, j + 35 + 18, 18 * 3, backgroundHeight + 54 - (entity.getEquippedStack(EquipmentSlot.LEGS).isEmpty() ? 0 : 18), 18, 18); //tail
        }
    }

    protected void drawEntity(DrawContext context) {
        if (entity != null) drawEntity(context, i + 26, j + 18, i + 78, j + 70, 13, this.mouseX, this.mouseY, this.entity);
    }

    private void drawEntity(DrawContext context, int x1, int y1, int x2, int y2, int size, float mouseX, float mouseY, LivingEntity entity) {
        float centerX = (x1 + x2) / 2f;
        float centerY = (y1 + y2) / 2f;
        float dx = (float)Math.atan((centerX - mouseX) / 40f);
        float dy = (float) Math.atan((centerY - mouseY) / 40f);
        float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);

        context.getMatrices().push();
        context.enableScissor(x1, y1, x2, y2);

        context.getMatrices().translate(centerX, centerY, 100);
        context.getMatrices().scale(size, size, -size);
        context.getMatrices().translate(0, entity.getHeight() / 2f + 0.4f, 0);
        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(-dy * 20 + 180));
        context.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-dx * 40 + entity.getYaw(tickDelta)));

        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        entityRenderDispatcher.render(entity, 0, 0, 0, 0, tickDelta, context.getMatrices(), context.getVertexConsumers(), 15728880);
        DiffuseLighting.enableGuiDepthLighting();

        context.draw();
        context.disableScissor();
        context.getMatrices().pop();

    }

    protected void drawStorage(DrawContext context) {
        int size = storageSize.getSize()/3;
        int offset = hasArmor ? 1 : 0;
        context.drawTexture(TEXTURE, i + 79 + 18 * offset, j + 17, 0, this.backgroundHeight, size * 18, 54);
    }

    protected void drawBanner(DrawContext context) {
        if (hasBanner) context.drawTexture(TEXTURE, i + 7, j + 35, 18 * 4, backgroundHeight + 54 - (entity.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty() ? 0 : 18), 18, 18); //banner
    }
}
