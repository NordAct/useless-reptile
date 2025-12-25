package nordmods.uselessreptile.client.util;

public class EquipmentAssetCache extends AssetCache {
    private boolean canRender = true;

    public boolean canRender() {
        return canRender;
    }

    public void setCanRender(boolean canRender) {
        this.canRender = canRender;
    }

    @Override
    public void cleanCache() {
        super.cleanCache();
        canRender = true;
    }
}
