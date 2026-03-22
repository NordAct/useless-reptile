package nordmods.uselessreptile.client.gui;

import com.mojang.math.Axis;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.util.LightCoordsUtil;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.uselessreptile.client.util.FakeDragon;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.network.c2s.SetVariantChangingOrbVariantPayload;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class VariantChangingOrbScreen extends Screen {
    private final FakeDragon fakeDragon;
    private static final int DRAGON_WIDTH = 200;
    private static final int ARROW_HEIGHT = 17;
    private static final int ARROW_WIDTH = 12;
    private static final WidgetSprites NEXT_VARIANT_SPRITES = new WidgetSprites(
            Identifier.withDefaultNamespace("recipe_book/page_forward"),
            Identifier.withDefaultNamespace("recipe_book/page_forward_highlighted")
    );
    private static final WidgetSprites PREVIOUS_VARIANT_SPRITES = new WidgetSprites(
            Identifier.withDefaultNamespace("recipe_book/page_backward"),
            Identifier.withDefaultNamespace("recipe_book/page_backward_highlighted")
    );
    private final Button nextVariant;
    private final Button previousVariant;
    private final Button nextType;
    private final Button previousType;
    private Component dragonType;
    @Nullable
    private Component dragonDisplayName;
    private Component dragonVariant;
    private final Button accept;
    private final Button cancel;
    public VariantChangingOrbScreen(DragonVariantType<?> variantType, String variantName) {
        super(GameNarrator.NO_TITLE);
        this.fakeDragon = new FakeDragon(variantType, variantName);

        nextVariant = new ImageButton(
                ARROW_WIDTH, ARROW_HEIGHT,
                NEXT_VARIANT_SPRITES,
                _ -> {
                    fakeDragon.nextVariant();
                    updateText();
                },
                Component.translatable("button.uselessreptile.next_variant")
        );
        nextVariant.setTooltip(Tooltip.create(Component.translatable("button.uselessreptile.next_variant")));

        previousVariant = new ImageButton(
                ARROW_WIDTH, ARROW_HEIGHT,
                PREVIOUS_VARIANT_SPRITES,
                _ -> {
                    fakeDragon.previousVariant();
                    updateText();
                },
                Component.translatable("button.uselessreptile.previous_variant")
        );
        previousVariant.setTooltip(Tooltip.create(Component.translatable("button.uselessreptile.previous_variant")));

        nextType = new ImageButton(
                ARROW_WIDTH, ARROW_HEIGHT,
                NEXT_VARIANT_SPRITES,
                _ -> {
                    fakeDragon.nextType();
                    updateText();
                },
                Component.translatable("button.uselessreptile.next_type")
        );
        nextType.setTooltip(Tooltip.create(Component.translatable("button.uselessreptile.next_type")));

        previousType = new ImageButton(
                ARROW_WIDTH, ARROW_HEIGHT,
                PREVIOUS_VARIANT_SPRITES,
                _ -> {
                    fakeDragon.previousType();
                    updateText();
                },
                Component.translatable("button.uselessreptile.previous_type")
        );
        previousType.setTooltip(Tooltip.create(Component.translatable("button.uselessreptile.previous_type")));

        accept = Button
                .builder(
                        Component.translatable("button.uselessreptile.accept"),
                        _ -> {
                            SetVariantChangingOrbVariantPayload.send(fakeDragon.getVariantType(), fakeDragon.getDragonVariant().common().name());
                            onClose();
                        }
                )
                .build();

        cancel = Button
                .builder(
                        Component.translatable("button.uselessreptile.cancel"),
                        button -> onClose()
                )
                .build();

        updateText();
    }

    @Override
    public void init() {
        super.init();
        addWidget(nextVariant);
        nextVariant.setPosition(width/2 + DRAGON_WIDTH/2 - ARROW_WIDTH, 30);

        addWidget(previousVariant);
        previousVariant.setPosition(width/2 - DRAGON_WIDTH/2, 30);

        addWidget(nextType);
        nextType.setPosition(width/2 + DRAGON_WIDTH/2 - ARROW_WIDTH, 10);

        addWidget(previousType);
        previousType.setPosition(width/2 - DRAGON_WIDTH/2, 10);

        addWidget(accept);
        accept.setSize(DRAGON_WIDTH/2 - 5, 20);
        accept.setPosition(width/2, height - 30);

        addWidget(cancel);
        cancel.setSize(DRAGON_WIDTH/2 - 5, 20);
        cancel.setPosition(width/2 - DRAGON_WIDTH/2, height - 30);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractBackground(context, mouseX, mouseY, delta);
        extractFakeDragon(
                context,
                0, 0,
                width, height - 40,
                mouseX,
                mouseY
        );

        nextVariant.extractRenderState(context, mouseX, mouseY, delta);
        previousVariant.extractRenderState(context, mouseX, mouseY, delta);
        nextType.extractRenderState(context, mouseX, mouseY, delta);
        previousType.extractRenderState(context, mouseX, mouseY, delta);
        accept.extractRenderState(context, mouseX, mouseY, delta);
        cancel.extractRenderState(context, mouseX, mouseY, delta);

        context.centeredText(
                font,
                dragonType,
                width/2,
                15,
                CommonColors.WHITE
        );
        boolean hasDisplayName = dragonDisplayName != null;
        if (hasDisplayName) context.centeredText(
                font,
                dragonDisplayName,
                width/2,
                30,
                CommonColors.WHITE
        );

        context.centeredText(
                font,
                dragonVariant,
                width/2,
                hasDisplayName ? 39 : 35,
                CommonColors.WHITE
        );
    }

    private void extractFakeDragon(GuiGraphicsExtractor context, int x0, int y0, int x1, int y1, float mouseX, float mouseY) {
        if (!ResourceUtil.isResourceReloadFinished) return;

        float centerX = (x0 + x1) / 2f;
        float centerY = (y0 + y1) / 2f;
        float dx = (float)Math.atan((centerX - mouseX) / 40f);
        float dy = (float)Math.atan((centerY - mouseY) / 40f);


        LivingEntityRenderState state = fakeDragon.createDragonRenderState();
        state.setStateData(ClientStateDataTypes.LIGHT, LightCoordsUtil.FULL_BRIGHT);

        Quaternionf rot = new Quaternionf();
        Quaternionf cam = Axis.XP.rotationDegrees(-dy * 20 + 180).mul(Axis.YP.rotationDegrees(-dx * 40));
        rot.mul(cam);

        float scale = state.getStateData(StateDataTypes.SCALE, 1f);
        scale = 200/(scale * scale);
        context.entity(
                state,
                scale,
                new Vector3f(0, height/3f/scale, 0),
                rot,
                cam,
                x0, y0,
                x1, y1
        );
    }

    private void updateText() {
        dragonType = Component.translatable(fakeDragon.getVariantType().getTranslationKey());
        dragonDisplayName = fakeDragon.getDragonVariant().common().displayNameKey().isPresent() ?
                Component.translatable("gui.uselessreptile.dragon_display_name", Component.translatable(fakeDragon.getDragonVariant().common().displayNameKey().get())) :
                null;
        dragonVariant = Component.translatable("gui.uselessreptile.dragon_variant_name", Component.translatable(fakeDragon.getDragonVariant().common().variantNameKey()));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }
}
