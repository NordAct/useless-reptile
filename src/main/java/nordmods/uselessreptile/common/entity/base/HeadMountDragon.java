package nordmods.uselessreptile.common.entity.base;

public interface HeadMountDragon {
    default URDragonEntity asURDragon() {
        return (URDragonEntity) this;
    }
}
