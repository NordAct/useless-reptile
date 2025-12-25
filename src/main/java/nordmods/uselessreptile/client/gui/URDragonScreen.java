package nordmods.uselessreptile.client.gui;

import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.gui.URDragonMenu;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class URDragonScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected static final Identifier TEXTURE = UselessReptile.id("textures/gui/dragon_inventory.png");
    private int mouseX;
    private int mouseY;
    private final URDragonEntity entity;
    private int i;
    private int j;
    public static int entityToRenderID;
    protected float entityRenderSize = 12;
    protected Vector3f entityScreenOffset = new Vector3f();

    public URDragonScreen(T handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        Player player = inventory.player;
        entity = (URDragonEntity) player.level().getEntity(entityToRenderID);
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        i = (width - imageWidth) / 2;
        j = (height - imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0, 0, imageWidth, imageHeight, 256, 256);
        drawSaddle(context);
        drawBanner(context);
        drawArmor(context);
        drawStorage(context);
        drawEntity(context);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        super.render(context, mouseX, mouseY, delta);
        renderTooltip(context, mouseX, mouseY);
    }

    protected void drawSaddle(GuiGraphics context) {
        if (entity.getInventory().hasSaddle)
            context.blit( //saddle
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    i + 7,
                    j + 35 - URDragonMenu.SLOT_SIDE,
                    0,
                    imageHeight + URDragonMenu.ENTITY_WINDOW_SIDE - (entity.getItemBySlot(EquipmentSlot.FEET).isEmpty() ? 0 : URDragonMenu.SLOT_SIDE),
                    URDragonMenu.SLOT_SIDE,
                    URDragonMenu.SLOT_SIDE,
                    256, 256);
    }

    protected void drawArmor(GuiGraphics context) {
        if (entity.getInventory().hasArmor) {
            context.blit( //head
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    i + 7 + URDragonMenu.SLOT_SIDE + URDragonMenu.ENTITY_WINDOW_SIDE,
                    j + 35 -  URDragonMenu.SLOT_SIDE,
                    URDragonMenu.SLOT_SIDE,
                    imageHeight + URDragonMenu.ENTITY_WINDOW_SIDE - (entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty() ? 0 : URDragonMenu.SLOT_SIDE),
                    URDragonMenu.SLOT_SIDE,
                    URDragonMenu.SLOT_SIDE,
                    256, 256
            );
            context.blit( //body
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    i + 7 + URDragonMenu.SLOT_SIDE + URDragonMenu.ENTITY_WINDOW_SIDE,
                    j + 35,  URDragonMenu.SLOT_SIDE * 2,
                    imageHeight + URDragonMenu.ENTITY_WINDOW_SIDE - (entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty() ? 0 : URDragonMenu.SLOT_SIDE),
                    URDragonMenu.SLOT_SIDE,
                    URDragonMenu.SLOT_SIDE,
                    256, 256
            );
            context.blit( //tail
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    i + 7 + URDragonMenu.SLOT_SIDE + URDragonMenu.ENTITY_WINDOW_SIDE,
                    j + 35 +  URDragonMenu.SLOT_SIDE,
                    URDragonMenu.SLOT_SIDE * 3,
                    imageHeight + URDragonMenu.ENTITY_WINDOW_SIDE - (entity.getItemBySlot(EquipmentSlot.LEGS).isEmpty() ? 0 : URDragonMenu.SLOT_SIDE),
                    URDragonMenu.SLOT_SIDE,
                    URDragonMenu.SLOT_SIDE,
                    256, 256
            );
        }
    }

    protected void drawEntity(GuiGraphics context) {
        if (entity != null) drawEntity(context, i + 26, j + 18, i + 78, j + 70, entityRenderSize, this.mouseX, this.mouseY, this.entity);
    }

    private void drawEntity(GuiGraphics context, int x1, int y1, int x2, int y2, float size, float mouseX, float mouseY, LivingEntity entity) {
        float centerX = (x1 + x2) / 2f;
        float centerY = (y1 + y2) / 2f;
        float dx = (float)Math.atan((centerX - mouseX) / 40f);
        float dy = (float) Math.atan((centerY - mouseY) / 40f);
        float tickDelta = RenderUtil.getTickDelta(false);

        context.enableScissor(x1, y1, x2, y2);

        EntityRenderer<? super LivingEntity, ?> renderer = RenderUtil.getEntityRenderer(entity);
        LivingEntityRenderState state = (LivingEntityRenderState) renderer.createRenderState(entity, tickDelta);
        state.nameTag = null;
        state.setStateData(URStateDataTypes.PASSENGERS_SHOULD_RENDER_TO_CLIENT, state.getStateData(URStateDataTypes.PASSENGERS_SHOULD_RENDER_TO_CLIENT).stream().map(val -> false).toList());
        state.setStateData(ClientStateDataTypes.LIGHT, LightTexture.FULL_BRIGHT);

        Quaternionf rot = new Quaternionf();
        Quaternionf cam = Axis.XP.rotationDegrees(-dy * 20 + 180).mul(Axis.YP.rotationDegrees(-dx * 40 + state.bodyRot));
        rot.mul(cam);

        context.submitEntityRenderState(state, size / state.scale, new Vector3f(0, state.boundingBoxHeight / 2f + 0.4f, 0).add(entityScreenOffset), rot, cam, x1, y1, x2, y2);

        context.disableScissor();
    }

    protected void drawStorage(GuiGraphics context) {
        int size = entity.getInventory().storageSize.getSize()/3;
        int offset = entity.getInventory().hasArmor ? 2 : 1;
        context.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                i + 7 + URDragonMenu.ENTITY_WINDOW_SIDE + URDragonMenu.SLOT_SIDE * offset,
                j + 17,
                0,
                this.imageHeight,
                size * URDragonMenu.SLOT_SIDE,
                URDragonMenu.ENTITY_WINDOW_SIDE,
                256, 256
        );
    }

    protected void drawBanner(GuiGraphics context) {
        if (entity.getInventory().hasBanner)
            context.blit( //banner
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    i + 7,
                    j + 35,
                    URDragonMenu.SLOT_SIDE * 4,
                    imageHeight + URDragonMenu.ENTITY_WINDOW_SIDE - (entity.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty() ? 0 : URDragonMenu.SLOT_SIDE),
                    URDragonMenu.SLOT_SIDE,
                    URDragonMenu.SLOT_SIDE,
                    256, 256
            );
    }
}
