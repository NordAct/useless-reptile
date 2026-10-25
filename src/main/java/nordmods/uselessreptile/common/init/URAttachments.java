package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.EquipmentSlot;
import nordmods.biscuit_roll.common.animation.processor.ControllerState;
import nordmods.uselessreptile.UselessReptile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URAttachments {
    public static final AttachmentType<Map<EquipmentSlot, List<ControllerState>>> EQUIPMENT_CONTROLLER_STATES = AttachmentRegistry.create(
            UselessReptile.id("equipment_controller_states"),
            builder -> builder
                    .initializer(HashMap::new)
                    .syncWith(
                            ByteBufCodecs.map(
                                    HashMap::new,
                                    EquipmentSlot.STREAM_CODEC,
                                    ControllerState.LIST_STREAM_CODEC,
                                    2
                            ),
                            AttachmentSyncPredicate.all()
                    )
    );
    public static void init() {}
}
