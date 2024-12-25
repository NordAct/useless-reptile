package nordmods.uselessreptile.client.renderer.special;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import nordmods.uselessreptile.common.entity.special.LightningBreathEntity;
import org.jetbrains.annotations.Nullable;

public class LightningBreathEntityRenderState extends EntityRenderState {
    public int length;
    @Nullable
    public Entity owner;
    public float alpha = 1;
    public final LightningBreathEntity.LightningBreathBolt[] lightningBreathBolts = new LightningBreathEntity.LightningBreathBolt[5];
}
