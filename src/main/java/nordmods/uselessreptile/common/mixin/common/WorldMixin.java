package nordmods.uselessreptile.common.mixin.common;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.multipart.URDragonPart;
import nordmods.uselessreptile.common.entity.multipart.WorldMultipartHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(World.class)
public abstract class WorldMixin implements WorldMultipartHelper {
    @Unique
    private final Int2ObjectMap<URDragonPart> URDragonParts = new Int2ObjectOpenHashMap<>();

    public Int2ObjectMap<URDragonPart> getPartMap() {
        return URDragonParts;
    }

    @Inject(method = "getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;", at = @At("TAIL"))
    private void getDragonParts(Entity except, Box box, Predicate<? super Entity> predicate, CallbackInfoReturnable<List<Entity>> cir) {
        for (URDragonPart part : getParts())
            if (part != null && part != except && part.getBoundingBox().intersects(box) && predicate.test(part)) cir.getReturnValue().add(part);
    }

    @Inject(method = "getEntitiesByType(Lnet/minecraft/util/TypeFilter;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;", at = @At("TAIL"))
    private <T extends Entity> void getDragonPartsByType(TypeFilter<Entity, T> filter, Box box, Predicate<? super T> predicate, CallbackInfoReturnable<List<T>> cir) {
        for (URDragonPart part : getParts()) {
            T type = filter.downcast(part);
            if (type != null && part.getBoundingBox().intersects(box) && predicate.test(type)) cir.getReturnValue().add(type);
        }
    }
}
