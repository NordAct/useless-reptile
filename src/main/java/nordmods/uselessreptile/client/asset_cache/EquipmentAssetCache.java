package nordmods.uselessreptile.client.asset_cache;

public class EquipmentAssetCache extends AssetCache {
    private boolean canRender = true;
    private String[] hidBones = new String[0];

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
        hidBones = new String[0];
    }

    public String[] getHidBones() {
        return hidBones;
    }

    public void setHidBones(String[] hidBones) {
        this.hidBones = hidBones;
    }
}
