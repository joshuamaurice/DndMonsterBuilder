package jmaurice.dnd.stats.builder.raw.basic;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class MiscBasics extends BaseBuilder {

    public MiscBasics(final Stats stats) { super(stats); }

    public void build() {
        agg("alignment", root); 
        agg("challenge rating", values -> sumAsInts(values));
        
        aggN("creature subtype", root, values -> values); 
        toN("creature subtypes", "creature subtype");
        aggN("creature subtypes", values -> sort(values));
        agg("creature subtypes2", leaf, values -> join(sort(values), ", "));
        toN("creature subtypes2", "creature subtypes", values -> values);
        
        aggN("defensive abilities", values -> sort(values));
        agg("defensive abilities2", leaf, values -> join(sort(values), ", "));
        toN("defensive abilities2", "defensive abilities", values -> values);
        
        aggN("equipment", root, values -> sort(values));
        agg("equipment2", leaf, values -> join(sort(values), ", "));
        toN("equipment2", "equipment", values -> values);
        
        agg("gender", root);
        agg("high attack DC", leaf, values -> maxAsInts(values));
        
        aggN("immunities", values -> sort(values));
        agg("immunities2", leaf, values -> join(sort(values), ", "));
        toN("immunities2", "immunities", values -> values);
        
        agg("initiative", values -> withSign(sumAsInts(values)));
        to1("initiative", "dexterity modifier");
        to1("initiative", "default", new Value(0));
        
        agg("name", root);
        agg("psionic manifester level", values -> sumAsInts(values));
        agg("race", root);
        
        aggN("resistances", values -> sort(values));
        agg("resistances2", leaf, values -> join(sort(values), ", "));
        toN("resistances2", "resistances", values -> values);
        
        aggN("senses", values -> sort(values));
        agg("senses2", leaf, values -> join(sort(values), ", "));
        toN("senses2", "senses", values -> values);
        
        aggN("special abilities long", values -> sort(values));
        agg("special abilities long2", leaf, values -> join(sort(values), ", "));
        toN("special abilities long2", "special abilities long", values -> values);
        
        aggN("special abilities short", values -> sort(values));
        agg("special abilities short2", leaf, values -> join(sort(values), ", "));
        toN("special abilities short2", "special abilities short", values -> values);
        
        aggN("templates", root, values -> sort(values));
        
        aggN("weaknesses", root, values -> sort(values));
        agg("weaknesses2", leaf, values -> join(sort(values), ", "));
        toN("weaknesses2", "weaknesses", values -> values);
        
    }

}
