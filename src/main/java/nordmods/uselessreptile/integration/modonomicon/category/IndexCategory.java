package nordmods.uselessreptile.integration.modonomicon.category;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.datagen.book.demo.indexmode.Demo1IndexEntry;
import nordmods.uselessreptile.common.init.URItems;

public class IndexCategory extends CategoryProvider {
    public IndexCategory(ModonomiconProviderBase parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[0];
    }

    @Override
    protected void generateEntries() {
        this.add(new Demo1IndexEntry(this).generate());
    }

    @Override
    protected String categoryName() {
        return "that's a placeholder, Nord";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(URItems.WYVERN_SKIN);
    }

    @Override
    public String categoryId() {
        return "index";
    }
}
