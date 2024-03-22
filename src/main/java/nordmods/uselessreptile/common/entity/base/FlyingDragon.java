package nordmods.uselessreptile.common.entity.base;

public interface FlyingDragon {
    int getInAirTimer();
    int getMaxInAirTimer();
    void setInAirTimer(int state);
    boolean isGliding();
    void setGliding(boolean state);
    boolean isFlying();
    void setFlying(boolean state);
    byte getTiltState();
    void setTiltState(byte state);
    void startToFly();
    float getVerticalSpeed();
}
