package jmaurice.dnd.stats.builder.raw.basic;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class MiscBasics extends BaseBuilder {

    public MiscBasics(final Stats stats) { super(stats); }

    public void build() {
        agg("alignment", root); 
        agg("challenge rating", values -> sumAsInts(values));
        aggN("auras", root, values -> sort(values));
        aggN("creature subtypes", values -> sort(values));
        aggN("damage reduction", root, values -> sort(values));
        aggN("defensive abilities", values -> sort(values));
        aggN("equipment", root, values -> sort(values));
        agg("gender", root);
        agg("high attack DC", leaf, values -> maxAsInts(values));
        aggN("immunities", values -> sort(values));
        
        agg("initiative", values -> withSign(sumAsInts(values)));
        to1("initiative", "dexterity modifier");
        to1("initiative", "default", new Value(0));
        
        aggN("languages", root, values -> sort(values));
        agg("mythic rank", root);
        agg("name", root);
        agg("psionic manifester level", values -> sumAsInts(values));
        agg("race", root);
        aggN("resistances", values -> sort(values));
        aggN("senses", values -> sort(values));
        agg("shape", root);
        aggN("special abilities long", values -> sort(values));
        aggN("special abilities short", values -> sort(values));
        aggN("templates", root, values -> sort(values));
        aggN("weaknesses", root, values -> sort(values));
    }

}
