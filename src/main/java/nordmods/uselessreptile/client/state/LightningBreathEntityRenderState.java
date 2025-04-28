package nordmods.uselessreptile.client.state;

import net.minecraft.client.render.entity.state.EntityRenderState;
import nordmods.uselessreptile.common.entity.special.LightningBreathEntity;

public class LightningBreathEntityRenderState extends EntityRenderState {
    public int length;
    public float alpha = 1;
    public LightningBreathEntity.LightningBreathBolt[] lightningBreathBolts = new LightningBreathEntity.LightningBreathBolt[0];
}
