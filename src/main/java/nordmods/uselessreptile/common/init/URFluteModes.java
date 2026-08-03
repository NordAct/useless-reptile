package nordmods.uselessreptile.common.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.GathererDragon;
import nordmods.uselessreptile.common.item.FluteItem;

public class URFluteModes {
    public static final FluteItem.FluteMode CALL = register("call", new FluteItem.FluteMode(URSoundEvent.FLUTE_CALL, dragon -> {
        if (!dragon.isOrderedToSit()) dragon.shouldFollow = true;
    }));
    public static final FluteItem.FluteMode GATHER = register("gather", new FluteItem.FluteMode(URSoundEvent.FLUTE_GATHER, dragon -> {
        if (dragon instanceof GathererDragon gathererDragon) gathererDragon.startGathering();
    }));
    public static final FluteItem.FluteMode TARGET = register("target", new FluteItem.FluteMode(URSoundEvent.FLUTE_TARGET, dragon -> {
        if (!(dragon.getOwner() instanceof Player player)) return;

        int range = URGameEvents.FLUTE_USED.value().notificationRadius();
        Vec3 rot = player.getViewVector(1);
        EntityHitResult hitResult = ProjectileUtil
                .getEntityHitResult(player,
                        player.getEyePosition(1),
                        player.getEyePosition(1).add(rot.scale(range)),
                        player.getBoundingBox().expandTowards(rot.scale(range)).inflate(1.0, 1.0, 1.0),
                        entity -> entity instanceof LivingEntity && !entity.isSpectator() && entity.isPickable(), range * range);

        if (hitResult != null) dragon.setTarget((LivingEntity) hitResult.getEntity());
    }));
    public static final FluteItem.FluteMode SIT_DOWN = register("sit_down", new FluteItem.FluteMode(URSoundEvent.FLUTE_SIT_DOWN, dragon -> dragon.setOrderedToSit(true)));
    public static final FluteItem.FluteMode STAND_UP = register("stand_up", new FluteItem.FluteMode(URSoundEvent.FLUTE_STAND_UP, dragon -> dragon.setOrderedToSit(false)));

    private static FluteItem.FluteMode register(String id, FluteItem.FluteMode fluteMode) {
        return register(UselessReptile.id(id), fluteMode);
    }

    public static FluteItem.FluteMode register(Identifier id, FluteItem.FluteMode fluteMode) {
        return Registry.register(URRegistries.FLUTE_MODE, id, fluteMode);
    }

    public static void init() {}
}
