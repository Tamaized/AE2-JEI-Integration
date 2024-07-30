package tamaized.ae2jeiintegration.integration.modules.jei;

import appeng.integration.abstraction.ItemListModAdapter;
import com.google.common.base.Strings;

import mezz.jei.api.runtime.IJeiRuntime;

import tamaized.ae2jeiintegration.integration.abstraction.IJEI;

public class JeiRuntimeAdapter implements IJEI, ItemListModAdapter {

    private final IJeiRuntime runtime;

    JeiRuntimeAdapter(IJeiRuntime jeiRuntime) {
        this.runtime = jeiRuntime;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getShortName() {
        return "JEI";
    }

    public IJeiRuntime getRuntime() {
        return runtime;
    }

    @Override
    public String getSearchText() {
        return Strings.nullToEmpty(this.runtime.getIngredientFilter().getFilterText());
    }

    @Override
    public void setSearchText(String text) {
        this.runtime.getIngredientFilter().setFilterText(text);
    }

    @Override
    public boolean hasSearchFocus() {
        return this.runtime.getIngredientListOverlay().hasKeyboardFocus();
    }
}
