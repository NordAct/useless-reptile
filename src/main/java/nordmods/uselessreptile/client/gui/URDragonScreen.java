package nordmods.uselessreptile.client.gui;

import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
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
    private final URDragonEntity entity;
    private final Button follow;
    private final Button stay;
    private final Button sit;
    private final Button unbindInstrument;
    private final Button wanderClose;
    private final Button wanderMedium;
    private final Button wanderFar;
    private static final int COMMAND_BUTTON_SIZE = 20;
    private static final int COMMAND_SPRITE_SIZE = 16;

    public URDragonScreen(T handler, Inventory inventory, URDragonEntity entity) {
        super(handler, inventory, entity.getDisplayName());
        this.entity = entity;

        follow = SpriteIconButton
                .builder(Component.translatable("button.uselessreptile.follow"), (_) -> {
                    ClientPlayNetworking.send(new OrderPayload(URDragonEntity.Order.FOLLOW, entity.getId()));
                }, true)
                .size(COMMAND_BUTTON_SIZE, COMMAND_BUTTON_SIZE)
                .sprite(UselessReptile.id("follow"), COMMAND_SPRITE_SIZE, COMMAND_SPRITE_SIZE)
                .withTootip()
                .build();

        stay = SpriteIconButton
                .builder(Component.translatable("button.uselessreptile.stay"), (_) -> {
                    ClientPlayNetworking.send(new OrderPayload(URDragonEntity.Order.STAY, entity.getId()));
                }, true)
                .size(COMMAND_BUTTON_SIZE, COMMAND_BUTTON_SIZE)
                .sprite(UselessReptile.id("stay"), COMMAND_SPRITE_SIZE, COMMAND_SPRITE_SIZE)
                .withTootip()
                .build();

        sit = SpriteIconButton
                .builder(Component.translatable("button.uselessreptile.sit"), (_) -> {
                    ClientPlayNetworking.send(new OrderPayload(URDragonEntity.Order.SIT, entity.getId()));
                }, true)
                .size(COMMAND_BUTTON_SIZE, COMMAND_BUTTON_SIZE)
                .sprite(UselessReptile.id("sit"), COMMAND_SPRITE_SIZE, COMMAND_SPRITE_SIZE)
                .withTootip()
                .build();

        unbindInstrument = SpriteIconButton
                .builder(Component.translatable("button.uselessreptile.unbind_instrument_sound"), (_) -> {
                    ClientPlayNetworking.send(new UnbindInstrumentPayload(entity.getId()));
                }, true)
                .size(COMMAND_BUTTON_SIZE, COMMAND_BUTTON_SIZE)
                .sprite(UselessReptile.id("unbind_instrument_sound"), COMMAND_SPRITE_SIZE, COMMAND_SPRITE_SIZE)
                .withTootip()
                .build();

        wanderClose = SpriteIconButton
                .builder(Component.translatable("button.uselessreptile.wander_small"), (_) -> {
                    ClientPlayNetworking.send(new ChangeWanderRadiusPayload(URDragonEntity.WanderRadius.MEDIUM, entity.getId()));
                }, true)
                .size(COMMAND_BUTTON_SIZE, COMMAND_BUTTON_SIZE)
                .sprite(UselessReptile.id("wander_small"), COMMAND_SPRITE_SIZE, COMMAND_SPRITE_SIZE)
                .withTootip()
                .build();

        wanderMedium = SpriteIconButton
                .builder(Component.translatable("button.uselessreptile.wander_medium"), (_) -> {
                    ClientPlayNetworking.send(new ChangeWanderRadiusPayload(URDragonEntity.WanderRadius.BIG, entity.getId()));
                }, true)
                .size(COMMAND_BUTTON_SIZE, COMMAND_BUTTON_SIZE)
                .sprite(UselessReptile.id("wander_medium"), COMMAND_SPRITE_SIZE, COMMAND_SPRITE_SIZE)
                .withTootip()
                .build();

        wanderFar = SpriteIconButton
                .builder(Component.translatable("button.uselessreptile.wander_big"), (_) -> {
                    ClientPlayNetworking.send(new ChangeWanderRadiusPayload(URDragonEntity.WanderRadius.SMALL, entity.getId()));
                }, true)
                .size(COMMAND_BUTTON_SIZE, COMMAND_BUTTON_SIZE)
                .sprite(UselessReptile.id("wander_big"), COMMAND_SPRITE_SIZE, COMMAND_SPRITE_SIZE)
                .withTootip()
                .build();
    }

    @Override
    public void init() {
        super.init();
        addWidget(follow);
        addWidget(stay);
        addWidget(sit);
        addWidget(unbindInstrument);
        addWidget(wanderClose);
        addWidget(wanderMedium);
        addWidget(wanderFar);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractBackground(context, mouseX, mouseY, delta);
        int centerX = (width - imageWidth) / 2;
        int centerY = (height - imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, centerX, centerY, 0, 0, imageWidth, imageHeight, 256, 256);
        extractSaddle(context, centerX, centerY);
        extractBanner(context, centerX, centerY);
        extractArmor(context, centerX, centerY);
        extractStorage(context, centerX, centerY);
        if (entity != null) extractEntity(context, centerX + 26, centerY + 18, centerX + 78, centerY + 70, mouseX, mouseY, entity);

        follow.active = entity.getCurrentOrder() != URDragonEntity.Order.FOLLOW;
        follow.setPosition(centerX - COMMAND_BUTTON_SIZE, centerY + 14);
        follow.extractRenderState(context, mouseX, mouseY, delta);

        stay.active = entity.getCurrentOrder() != URDragonEntity.Order.STAY;
        stay.setPosition(centerX - COMMAND_BUTTON_SIZE, centerY + 14 + COMMAND_BUTTON_SIZE);
        stay.extractRenderState(context, mouseX, mouseY, delta);

        sit.active = entity.getCurrentOrder() != URDragonEntity.Order.SIT;
        sit.setPosition(centerX - COMMAND_BUTTON_SIZE,  centerY + 14 + COMMAND_BUTTON_SIZE * 2);
        sit.extractRenderState(context, mouseX, mouseY, delta);

        Button wander = null;

        switch (entity.getWanderRadius()) {
            case BIG -> {
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
            case SMALL -> {
                wander = wanderClose;
                wanderFar.visible = false;
                wanderMedium.visible = false;
                wanderClose.visible = true;
            }
        }

        wander.active = sit.active;
        wander.setPosition(centerX - COMMAND_BUTTON_SIZE,  centerY + 14 + COMMAND_BUTTON_SIZE * 4);
        wander.extractRenderState(context, mouseX, mouseY, delta);

        unbindInstrument.active = !entity.getBoundedInstrumentSound().isEmpty();
        unbindInstrument.setPosition(centerX - COMMAND_BUTTON_SIZE,  centerY + 14 + COMMAND_BUTTON_SIZE * 5);
        unbindInstrument.extractRenderState(context, mouseX, mouseY, delta);


    }

    protected void extractSaddle(GuiGraphicsExtractor context, int x, int y) {
        if (entity.getInventory().hasSaddle)
            context.blit( //saddle
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    x + 7,
                    y + 35 - URDragonMenu.SLOT_SIDE,
                    0,
                    imageHeight + URDragonMenu.ENTITY_WINDOW_SIDE - (entity.getItemBySlot(EquipmentSlot.SADDLE).isEmpty() ? 0 : URDragonMenu.SLOT_SIDE),
                    URDragonMenu.SLOT_SIDE,
                    URDragonMenu.SLOT_SIDE,
                    256, 256);
    }

    protected void extractArmor(GuiGraphicsExtractor context, int x, int y) {
        if (entity.getInventory().hasHelmet) context.blit( //head
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                x + 7 + URDragonMenu.SLOT_SIDE + URDragonMenu.ENTITY_WINDOW_SIDE,
                y + 35 -  URDragonMenu.SLOT_SIDE,
                URDragonMenu.SLOT_SIDE,
                imageHeight + URDragonMenu.ENTITY_WINDOW_SIDE - (entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty() ? 0 : URDragonMenu.SLOT_SIDE),
                URDragonMenu.SLOT_SIDE,
                URDragonMenu.SLOT_SIDE,
                256, 256
        );
        if (entity.getInventory().hasChestplate) context.blit( //body
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                x + 7 + URDragonMenu.SLOT_SIDE + URDragonMenu.ENTITY_WINDOW_SIDE,
                y + 35,  URDragonMenu.SLOT_SIDE * 2,
                imageHeight + URDragonMenu.ENTITY_WINDOW_SIDE - (entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty() ? 0 : URDragonMenu.SLOT_SIDE),
                URDragonMenu.SLOT_SIDE,
                URDragonMenu.SLOT_SIDE,
                256, 256
        );
        if (entity.getInventory().hasTailArmor) context.blit( //tail
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                x + 7 + URDragonMenu.SLOT_SIDE + URDragonMenu.ENTITY_WINDOW_SIDE,
                y + 35 +  URDragonMenu.SLOT_SIDE,
                URDragonMenu.SLOT_SIDE * 3,
                imageHeight + URDragonMenu.ENTITY_WINDOW_SIDE - (entity.getItemBySlot(EquipmentSlot.LEGS).isEmpty() ? 0 : URDragonMenu.SLOT_SIDE),
                URDragonMenu.SLOT_SIDE,
                URDragonMenu.SLOT_SIDE,
                256, 256
        );

    }

    private void extractEntity(GuiGraphicsExtractor context, int x1, int y1, int x2, int y2, float mouseX, float mouseY, LivingEntity entity) {
        float centerX = (x1 + x2) / 2f;
        float centerY = (y1 + y2) / 2f;
        float dx = (float)Math.atan((centerX - mouseX) / 40f);
        float dy = (float) Math.atan((centerY - mouseY) / 40f);
        float tickDelta = RenderUtil.getTickDelta(false);

        context.enableScissor(x1, y1, x2, y2);

        EntityRenderer<? super LivingEntity, ?> renderer = RenderUtil.getEntityRenderer(entity);
        LivingEntityRenderState state = (LivingEntityRenderState) renderer.createRenderState(entity, tickDelta);
        state.nameTag = null;
        state.setStateData(URStateDataTypes.PASSENGERS_SHOULD_RENDER_TO_CLIENT, state.getStateData(URStateDataTypes.PASSENGERS_SHOULD_RENDER_TO_CLIENT, List.of()).stream().map(_ -> false).toList());
        state.setStateData(ClientStateDataTypes.LIGHT, LightCoordsUtil.FULL_BRIGHT);

        Quaternionf rot = new Quaternionf();
        Quaternionf cam = Axis.XP.rotationDegrees(-dy * 20 + 180).mul(Axis.YP.rotationDegrees(-dx * 40 + state.bodyRot));
        rot.mul(cam);

        float size = 2.5f/Math.max(entity.getBbHeight(), entity.getBbWidth());
        context.entity(state, 13 * size, new Vector3f(0, state.boundingBoxHeight / 2f + 0.4f, 0).add(0, -0.2f * (size - 1), 0), rot, cam, x1, y1, x2, y2);

        context.disableScissor();
    }

    protected void extractStorage(GuiGraphicsExtractor context, int x, int y) {
        int size = entity.getInventory().storageSize.getSize()/3;
        int offset = entity.getInventory().hasHelmet || entity.getInventory().hasChestplate || entity.getInventory().hasTailArmor ? 2 : 1;
        context.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                x + 7 + URDragonMenu.ENTITY_WINDOW_SIDE + URDragonMenu.SLOT_SIDE * offset,
                y + 17,
                0,
                this.imageHeight,
                size * URDragonMenu.SLOT_SIDE,
                URDragonMenu.ENTITY_WINDOW_SIDE,
                256, 256
        );
    }

    protected void extractBanner(GuiGraphicsExtractor context, int x, int y) {
        if (entity.getInventory().hasSaddle)
            context.blit( //banner
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    x + 7,
                    y + 35,
                    URDragonMenu.SLOT_SIDE * 4,
                    imageHeight + URDragonMenu.ENTITY_WINDOW_SIDE - (entity.getItemBySlot(EquipmentSlot.BODY).isEmpty() ? 0 : URDragonMenu.SLOT_SIDE),
                    URDragonMenu.SLOT_SIDE,
                    URDragonMenu.SLOT_SIDE,
                    256, 256
            );
    }
}
