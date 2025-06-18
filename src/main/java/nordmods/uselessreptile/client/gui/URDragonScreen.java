package nordmods.uselessreptile.client.gui;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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
        i = (width - backgroundWidth) / 2;
        j = (height - backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
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
        if (hasSaddle) context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 7, j + 35 - 18, 0, backgroundHeight + 54 - (entity.getEquippedStack(EquipmentSlot.FEET).isEmpty() ? 0 : 18), 18, 18, 256, 256); //saddle
    }

    protected void drawArmor(DrawContext context) {
        if (hasArmor) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 7 + 18 + 54, j + 35 - 18, 18, backgroundHeight + 54 - (entity.getEquippedStack(EquipmentSlot.HEAD).isEmpty() ? 0 : 18), 18, 18, 256, 256); //head
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 7 + 18 + 54, j + 35, 18 * 2, backgroundHeight + 54 - (entity.getEquippedStack(EquipmentSlot.CHEST).isEmpty() ? 0 : 18), 18, 18, 256, 256); //body
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 7 + 18 + 54, j + 35 + 18, 18 * 3, backgroundHeight + 54 - (entity.getEquippedStack(EquipmentSlot.LEGS).isEmpty() ? 0 : 18), 18, 18, 256, 256); //tail
        }
    }

    protected void drawEntity(DrawContext context) {
        if (entity != null) InventoryScreen.drawEntity(context, i + 26, j + 18, i + 78, j + 70, 13, 0.25F, this.mouseX, this.mouseY, this.entity);
    }

//    private void drawEntity(DrawContext context, int x1, int y1, int x2, int y2, int size, float mouseX, float mouseY, LivingEntity entity) {
//        float centerX = (x1 + x2) / 2f;
//        float centerY = (y1 + y2) / 2f;
//        float dx = (float)Math.atan((centerX - mouseX) / 40f);
//        float dy = (float) Math.atan((centerY - mouseY) / 40f);
//        float tickDelta = RenderUtil.getTickDelta(false);
//
//        context.getMatrices().pushMatrix();
//        context.enableScissor(x1, y1, x2, y2);
//
//        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
//        EntityRenderer<? super LivingEntity, ?> renderer = RenderUtil.getEntityRenderer(entity);
//        LivingEntityRenderState state = (LivingEntityRenderState) renderer.getAndUpdateRenderState(entity, tickDelta);
//        state.displayName = null;
//        if (state instanceof GeoRenderState geoRenderState) {
//            geoRenderState.addGeckolibData(URDataTickets.PASSENGER_SHOULD_RENDER_TO_CLIENT, false);
//            geoRenderState.addGeckolibData(DataTickets.PACKED_LIGHT, LightmapTextureManager.MAX_LIGHT_COORDINATE); //geckolib moment
//        }
//
//        context.getMatrices().translate(centerX, centerY);
//        context.getMatrices().scale(size / state.baseScale, size / state.baseScale);
//        context.getMatrices().translate(0, state.height / 2f + 0.4f);
//        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(-dy * 20 + 180));
//        context.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-dx * 40 + state.bodyYaw));
//
//        DiffuseLighting.enableGuiShaderLighting();
//        context.draw(vertexConsumerProvider -> entityRenderDispatcher.render(state, 0d, 0d, 0d, context.getMatrices(), vertexConsumerProvider, LightmapTextureManager.MAX_LIGHT_COORDINATE));
//        DiffuseLighting.enableGuiDepthLighting();
//
//        context.disableScissor();
//        context.getMatrices().popMatrix();
//
//    }

    protected void drawStorage(DrawContext context) {
        int size = storageSize.getSize()/3;
        int offset = hasArmor ? 1 : 0;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 79 + 18 * offset, j + 17, 0, this.backgroundHeight, size * 18, 54, 256, 256);
    }

    protected void drawBanner(DrawContext context) {
        if (hasBanner) context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 7, j + 35, 18 * 4, backgroundHeight + 54 - (entity.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty() ? 0 : 18), 18, 18,  256, 256); //banner
    }
}
