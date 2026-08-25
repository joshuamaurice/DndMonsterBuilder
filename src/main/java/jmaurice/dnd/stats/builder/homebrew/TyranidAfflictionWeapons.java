package jmaurice.dnd.stats.builder.homebrew;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class TyranidAfflictionWeapons extends BaseBuilder {

    public TyranidAfflictionWeapons(final Stats stats) { super(stats); }

    public void build() {
        agg("fleshborer", root, values -> sumAsInts(values));
        agg("fleshborer damage dice", values -> sumAsInts(values));
        to1("weapon properties", "fleshborer", value -> new Value("name=fleshborer,num=" + value.getIntValue() + ",natural,range,ft range increment=30,two-handed,no strength to damage"));
        to1("weapon properties", "fleshborer damage dice", value -> new Value("name=fleshborer,base damage=" + value.getIntValue() + "d6"));
        to1("fleshborer damage dice", "default", new Value(5));
    }

}
