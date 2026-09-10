package jmaurice.dnd.stats.builder.raw.specabil;

import java.util.Arrays;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Incorporeal extends BaseBuilder {

    public Incorporeal(final Stats stats) { super(stats); }

    public void build() {
        agg("incorporeal", root, values -> values.size() >= 1 ? new Value(1) : null);
        to1("special abilities short", "incorporeal", new Value("incorpoeal"));
        to1("weapon finesse", "incorporeal", new Value(true));
        input("deflection bonus to armor class", Arrays.asList("incorporeal", "charisma bonus"), stats -> {
            if ( ! stats.get("incorporeal").getBooleanValue(false))
                return null;
            return stats.get("charisma bonus").val01().map(x -> x.asInt().max(1)).orElse(null);
        });
    }

}
