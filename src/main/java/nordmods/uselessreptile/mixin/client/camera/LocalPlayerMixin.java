package nordmods.uselessreptile.mixin.client.camera;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.util.duck.PassengerCameraRollOwner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.LinkedList;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer implements PassengerCameraRollOwner {
    @Unique
    private float zRot;
    @Unique
    private final LinkedList<Float> zRotO = new LinkedList<>();
    @Unique
    private static final int MAX_Z_ROT_SAMPLES = 20;

    public LocalPlayerMixin(ClientLevel level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Override
    public float useless_reptile$getZRot(float partialTicks) {
        if (!URClientConfig.getConfig().enableCameraRoll) return 0;
        float sum = 0;
        for (int i = 0; i < zRotO.size(); i++) {
            if (i != zRotO.size() - 1) sum += Mth.lerp(partialTicks, zRotO.get(i), zRotO.get(i + 1));
            else sum += Mth.lerp(partialTicks, zRotO.get(i), zRot);
        }
        return sum / MAX_Z_ROT_SAMPLES;
    }

    @Override
    public void useless_reptile$updateZRot(float zRot) {
        if (!URClientConfig.getConfig().enableCameraRoll) return;
        zRotO.addLast(this.zRot);
        if (zRotO.size() >= MAX_Z_ROT_SAMPLES) zRotO.removeFirst();
        this.zRot = zRot;
    }
}
