package jmaurice.dnd.stats.builder.raw.basic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class MiscBasics extends BaseBuilder {

    public MiscBasics(final Stats stats) { super(stats); }

    public void build() {
        stat("alignment").agg(root); 
        stat("challenge rating").agg(values -> sumAsInts(values));
        stat("auras").aggN(values -> sort(values));
        stat("creature subtypes").aggN(values -> sort(values));
        stat("damage reduction").aggN(root, values -> sort(values));
        stat("defensive abilities").aggN(values -> sort(values));
        stat("equipment").aggN(root, values -> sort(values));
        stat("gender").agg(root);
        stat("high attack DC").agg(leaf, values -> maxAsInts(values));
        stat("immunities").aggN(values -> sort(values));
        
        stat("initiative").agg(values -> withSign(sumAsInts(sort(values, value -> value.source))));
        stat("dexterity modifier").to1("initiative");
        stat("default").to1("initiative", new Value(0));
        
        stat("languages").aggN(root, values -> sort(values));
        stat("mythic rank").agg(root);
        stat("name").agg(root);
        stat("psionic manifester level").agg(values -> sumAsInts(values));
        stat("race").agg(root);
        resistances();
        stat("senses").aggN(values -> sort(values));
        stat("shape").agg(root);
        stat("special abilities long").aggN(values -> sort(values));
        stat("special abilities short").aggN(values -> sort(values));
        stat("templates").aggN(root, values -> sort(values));
        stat("weaknesses").aggN(root, values -> sort(values));
    }
    
    private void resistances() {
        stat("resistances").aggN(root, values -> {
            final Map<String, Value> best = new LinkedHashMap<>();
            for (final Value value : values) {
                best.compute(value.type, (k,v) -> {
                    if (v == null)
                        return value;
                    if (value.getIntValue() > v.getIntValue())
                        return value;
                    return v;
                });
            }
            return new ArrayList<>(best.values());
        });
    }

}
