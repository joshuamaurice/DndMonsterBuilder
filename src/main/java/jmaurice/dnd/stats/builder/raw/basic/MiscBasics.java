package jmaurice.dnd.stats.builder.raw.basic;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class MiscBasics extends BaseBuilder {

    public MiscBasics(final Stats stats) { super(stats); }

    public void build() {
        agg("alignment", rootleaf); 
        agg("best psionic power DC", leaf, values -> maxAsInts(values));
//        agg("best spell DC", leaf, values -> maxAsInts(values)); //TODO
//        agg("caster level", values -> sumAsInts(values)); //TODO
        agg("challenge rating", leaf, values -> sumAsInts(values));
        agg("creature subtypes", leaf, values -> join(sort(values), ", "));
        agg("defensive abilities", leaf, values -> join(sort(values), ", "));
        agg("equipment", rootleaf, values -> join(sort(values), ", "));
        agg("immunities", leaf, values -> join(sort(values), ", "));
        agg("incorporeal", rootleaf, values -> values.size() >= 1 ? new Value(1) : null);
        agg("initiative", leaf, values -> sumAsInts(values));
        to1("initiative", "dexterity modifier");
        agg("psionic manifester level", values -> sumAsInts(values));
        agg("senses", leaf, values -> join(sort(values), ", "));
        agg("weaknesses", rootleaf, values -> join(sort(values), ", ")); //TODO remove root
        agg("special abilities long", leaf, values -> join(sort(values), "\n"));
        agg("special abilities short", rootleaf, values -> join(sort(values), ", ")); //TODO remove root
    }

}
