package nordmods.uselessreptile.client.model.special;

import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.special.AcidBlastEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;


public class AcidBlastEntityModel extends DefaultedEntityGeoModel<AcidBlastEntity> {
    public AcidBlastEntityModel() {
        super(new Identifier(UselessReptile.MODID, "acid_blast/acid_blast"));
    }
}
