package nordmods.uselessreptile.client.model;

import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.projectile.AcidBlast;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;


public class AcidBlastModel extends DefaultedEntityGeoModel<AcidBlast> {
    public AcidBlastModel() {
        super(UselessReptile.id("acid_blast/acid_blast"));
    }
}
