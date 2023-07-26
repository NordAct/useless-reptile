package nordmods.uselessreptile.client.model;

import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.WyvernProjectileEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;


public class WyvernProjectileEntityModel extends DefaultedEntityGeoModel<WyvernProjectileEntity> {

    public WyvernProjectileEntityModel() {
        super(new Identifier(UselessReptile.MODID, "wyvern_proj/wyvern_proj"));
    }
}
