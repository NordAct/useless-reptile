package nordmods.uselessreptile.common.entity.base;

import nordmods.uselessreptile.common.item.FluteItem;

public interface FluteListener {
    void startGathering();
    void respondToFlute(FluteItem.FluteAction action);
}
