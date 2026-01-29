package nordmods.uselessreptile.client.gui;

import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
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
import net.minecraft.world.inventory.AbstractContainerMenu;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.gui.URDragonMenu;
import nordmods.uselessreptile.common.network.c2s.ChangeWanderRadiusPayload;
import nordmods.uselessreptile.common.network.c2s.OrderPayload;
import nordmods.uselessreptile.common.network.c2s.UnbindInstrumentPayload;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class URDragonScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected static final Identifier TEXTURE = UselessReptile.id("textures/gui/dragon_inventory.png");
    private int mouseX;
    private int mouseY;
    private final URDragonEntity entity;
    private int i;
    private int j;
    private final Button follow;
    private final Button stay;
    private final Button sit;
    private final Button unbindInstrument;
    private final Button wanderClose;
    private final Button wanderMedium;
    private final Button wanderFar;
    private static final int COMMAND_BUTTON_SIZE = 20;

    public URDragonScreen(T handler, Inventory inventory, URDragonEntity entity) {
        super(handler, inventory, entity.getDisplayName());
        this.entity = entity;

        follow = SpriteIconButton
                .builder(Component.translatable("button.uselessreptile.follow"), (button) -> {
                    ClientPlayNetworking.send(new OrderPayload(URDragonEntity.Order.FOLLOW, entity.getId()));
                }, true)
                .size(COMMAND_BUTTON_SIZE, COMMAND_BUTTON_SIZE)
                .sprite(UselessReptile.id("follow"), 16, 16)
                .withTootip()
                .build();
        addWidget(follow);

        stay = SpriteIconButton
                .builder(Component.translatable("button.uselessreptile.stay"), (button) -> {
                    ClientPlayNetworking.send(new OrderPayload(URDragonEntity.Order.STAY, entity.getId()));
                }, true)
                .size(COMMAND_BUTTON_SIZE, COMMAND_BUTTON_SIZE)
                .sprite(UselessReptile.id("stay"), 16, 16)
                .withTootip()
                .build();
        addWidget(stay);

        sit = SpriteIconButton
                .builder(Component.translatable("button.uselessreptile.sit"), (button) -> {
                    ClientPlayNetworking.send(new OrderPayload(URDragonEntity.Order.SIT, entity.getId()));
                }, true)
                .size(COMMAND_BUTTON_SIZE, COMMAND_BUTTON_SIZE)
                .sprite(UselessReptile.id("sit"), 16, 16)
                .withTootip()
                .build();
        addWidget(sit);

        unbindInstrument = SpriteIconButton
                .builder(Component.translatable("button.uselessreptile.unbind_instrument_sound"), (button) -> {
                    ClientPlayNetworking.send(new UnbindInstrumentPayload(entity.getId()));
                }, true)
                .size(COMMAND_BUTTON_SIZE, COMMAND_BUTTON_SIZE)
                .sprite(UselessReptile.id("unbind_instrument_sound"), 16, 16)
                .withTootip()
                .build();
        addWidget(unbindInstrument);

        wanderClose = SpriteIconButton
                .builder(Component.translatable("button.uselessreptile.wander_small"), (button) -> {
                    ClientPlayNetworking.send(new ChangeWanderRadiusPayload(URDragonEntity.WanderRadius.MEDIUM, entity.getId()));
                }, true)
                .size(COMMAND_BUTTON_SIZE, COMMAND_BUTTON_SIZE)
                .sprite(UselessReptile.id("wander_small"), 16, 16)
                .withTootip()
                .build();
        addWidget(wanderClose);

        wanderMedium = SpriteIconButton
                .builder(Component.translatable("button.uselessreptile.wander_medium"), (button) -> {
                    ClientPlayNetworking.send(new ChangeWanderRadiusPayload(URDragonEntity.WanderRadius.FAR, entity.getId()));
                }, true)
                .size(COMMAND_BUTTON_SIZE, COMMAND_BUTTON_SIZE)
                .sprite(UselessReptile.id("wander_medium"), 16, 16)
                .withTootip()
                .build();
        addWidget(wanderMedium);

        wanderFar = SpriteIconButton
                .builder(Component.translatable("button.uselessreptile.wander_big"), (button) -> {
                    ClientPlayNetworking.send(new ChangeWanderRadiusPayload(URDragonEntity.WanderRadius.CLOSE, entity.getId()));
                }, true)
                .size(COMMAND_BUTTON_SIZE, COMMAND_BUTTON_SIZE)
                .sprite(UselessReptile.id("wander_big"), 16, 16)
                .withTootip()
                .build();
        addWidget(wanderFar);
    }

    @Override
    protected void renderBg(@NonNull GuiGraphics context, float delta, int mouseX, int mouseY) {
        i = (width - imageWidth) / 2;
        j = (height - imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0, 0, imageWidth, imageHeight, 256, 256);
        drawSaddle(context);
        drawBanner(context);
        drawArmor(context);
        drawStorage(context);
        drawEntity(context);

        follow.active = entity.getCurrentOrder() != URDragonEntity.Order.FOLLOW;
        follow.setPosition(i - COMMAND_BUTTON_SIZE, j + 14);
        follow.render(context, mouseX, mouseY, delta);

        stay.active = entity.getCurrentOrder() != URDragonEntity.Order.STAY;
        stay.setPosition(i - COMMAND_BUTTON_SIZE, j + 14 + COMMAND_BUTTON_SIZE);
        stay.render(context, mouseX, mouseY, delta);

        sit.active = entity.getCurrentOrder() != URDragonEntity.Order.SIT;
        sit.setPosition(i - COMMAND_BUTTON_SIZE,  j + 14 + COMMAND_BUTTON_SIZE * 2);
        sit.render(context, mouseX, mouseY, delta);

        Button wander = null;

        switch (entity.getWanderRadius()) {
            case FAR -> {
                wander = wanderFar;
                wanderFar.visible = true;
                wanderMedium.visible = false;
                wanderClose.visible = false;
            }
            case MEDIUM -> {
                wander = wanderMedium;
                wanderFar.visible = false;
                wanderMedium.visible = true;
                wanderClose.visible = false;
            }
            case CLOSE -> {
                wander = wanderClose;
                wanderFar.visible = false;
                wanderMedium.visible = false;
                wanderClose.visible = true;
            }
        }

        wander.active = sit.active;
        wander.setPosition(i - COMMAND_BUTTON_SIZE,  j + 14 + COMMAND_BUTTON_SIZE * 4);
        wander.render(context, mouseX, mouseY, delta);

        unbindInstrument.active = !entity.getBoundedInstrumentSound().isEmpty();
        unbindInstrument.setPosition(i - COMMAND_BUTTON_SIZE,  j + 14 + COMMAND_BUTTON_SIZE * 5);
        unbindInstrument.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void render(@NonNull GuiGraphics context, int mouseX, int mouseY, float delta) {
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
                    imageHeight + URDragonMenu.ENTITY_WINDOW_SIDE - (entity.getItemBySlot(EquipmentSlot.SADDLE).isEmpty() ? 0 : URDragonMenu.SLOT_SIDE),
                    URDragonMenu.SLOT_SIDE,
                    URDragonMenu.SLOT_SIDE,
                    256, 256);
    }

    protected void drawArmor(GuiGraphics context) {
        if (entity.getInventory().hasHelmet) context.blit( //head
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
        if (entity.getInventory().hasChestplate) context.blit( //body
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                i + 7 + URDragonMenu.SLOT_SIDE + URDragonMenu.ENTITY_WINDOW_SIDE,
                j + 35,  URDragonMenu.SLOT_SIDE * 2,
                imageHeight + URDragonMenu.ENTITY_WINDOW_SIDE - (entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty() ? 0 : URDragonMenu.SLOT_SIDE),
                URDragonMenu.SLOT_SIDE,
                URDragonMenu.SLOT_SIDE,
                256, 256
        );
        if (entity.getInventory().hasTailArmor) context.blit( //tail
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

    protected void drawEntity(GuiGraphics context) {
        if (entity != null) drawEntity(context, i + 26, j + 18, i + 78, j + 70, this.mouseX, this.mouseY, this.entity);
    }

    private void drawEntity(GuiGraphics context, int x1, int y1, int x2, int y2, float mouseX, float mouseY, LivingEntity entity) {
        float centerX = (x1 + x2) / 2f;
        float centerY = (y1 + y2) / 2f;
        float dx = (float)Math.atan((centerX - mouseX) / 40f);
        float dy = (float) Math.atan((centerY - mouseY) / 40f);
        float tickDelta = RenderUtil.getTickDelta(false);

        context.enableScissor(x1, y1, x2, y2);

        EntityRenderer<? super LivingEntity, ?> renderer = RenderUtil.getEntityRenderer(entity);
        LivingEntityRenderState state = (LivingEntityRenderState) renderer.createRenderState(entity, tickDelta);
        state.nameTag = null;
        state.setStateData(URStateDataTypes.PASSENGERS_SHOULD_RENDER_TO_CLIENT, state.getStateData(URStateDataTypes.PASSENGERS_SHOULD_RENDER_TO_CLIENT, List.of()).stream().map(val -> false).toList());
        state.setStateData(ClientStateDataTypes.LIGHT, LightTexture.FULL_BRIGHT);

        Quaternionf rot = new Quaternionf();
        Quaternionf cam = Axis.XP.rotationDegrees(-dy * 20 + 180).mul(Axis.YP.rotationDegrees(-dx * 40 + state.bodyRot));
        rot.mul(cam);

        float size = 2.5f/Math.max(entity.getBbHeight(), entity.getBbWidth());
        context.submitEntityRenderState(state, 13 * size, new Vector3f(0, state.boundingBoxHeight / 2f + 0.4f, 0).add(0, -0.2f * (size - 1), 0), rot, cam, x1, y1, x2, y2);

        context.disableScissor();
    }

    protected void drawStorage(GuiGraphics context) {
        int size = entity.getInventory().storageSize.getSize()/3;
        int offset = entity.getInventory().hasHelmet || entity.getInventory().hasChestplate || entity.getInventory().hasTailArmor ? 2 : 1;
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
        if (entity.getInventory().hasSaddle)
            context.blit( //banner
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    i + 7,
                    j + 35,
                    URDragonMenu.SLOT_SIDE * 4,
                    imageHeight + URDragonMenu.ENTITY_WINDOW_SIDE - (entity.getItemBySlot(EquipmentSlot.BODY).isEmpty() ? 0 : URDragonMenu.SLOT_SIDE),
                    URDragonMenu.SLOT_SIDE,
                    URDragonMenu.SLOT_SIDE,
                    256, 256
            );
    }
}
