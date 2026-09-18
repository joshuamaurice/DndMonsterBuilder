package jmaurice.dnd.stats.builder.raw.specabil;

import java.util.Arrays;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Incorporeal extends BaseBuilder {

    public Incorporeal(final Stats stats) { super(stats); }

    public void build() {
        stat("incorporeal").agg(root, values -> values.size() >= 1 ? new Value(1) : null);
        stat("incorporeal").to1("special abilities short", new Value("<a href=\"https://www.d20pfsrd.com/BESTIARY/RULES-FOR-MONSTERS/UNIVERSAL-MONSTER-RULES/#incorporeal_ex\">incorporeal</a>"));
        stat("incorporeal").to1("weapon finesse", new Value(true));
        input1("armor class bonus", Arrays.asList("incorporeal", "charisma modifier"), stats -> {
            if (stats.get("incorporeal").getValues().size() == 0)
                return null;
            return stats.get("charisma modifier").val1().type("deflection").source("incorporeal charisma to deflection");
        });
    }

}
