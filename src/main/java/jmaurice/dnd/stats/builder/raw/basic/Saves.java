package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;
import java.util.List;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Saves extends BaseBuilder {

    public static final List<String> saves = Arrays.asList("fortitude", "reflex", "will");
    
    public Saves(final Stats stats) { super(stats); }
    
    public void build() {
        for (final String save : saves) {
            stat("default")
            .to1(save, new Value(0))
            .agg(leaf, values -> sumAsInts(values));
        }
        baseSaveBonuses();
        abilityScoreBonuses();
        otherBonuses();
    }
    
    private void baseSaveBonuses() {
        for (final String save : saves) {
            stat(save + " good levels")
            .agg(root, values -> sumAsInts(values)) //TODO remove root
            .to1(save + " good bonus", value -> value.mult(0.5).add(2.0).source("good saves"))
            .agg(values -> sumAsDoubles(values)) //sumAsDoubles for partial save bonus multiclassing
            .to1(save + " base bonus", value -> value.source("good save levels"));
            
            stat(save + " bad levels")
            .agg(root, values -> sumAsInts(values)) //TODO remove root
            .to1(save + " bad bonus", value -> value.mult(0.3334).source("bad saves"))
            .agg(values -> sumAsDoubles(values)) //sumAsDoubles for partial save bonus multiclassing
            .to1(save + " base bonus", value -> value.source("bad save levels"));
            
            stat(save + " base bonus")
            .agg(leaf, values -> sumAsDoubles(values).floor()) 
            .to1(save); 
        }
    }

    private void abilityScoreBonuses() {
        stat("constitution modifier").to1("fortitude");
        stat("dexterity modifier").to1("reflex");
        stat("wisdom modifier").to1("will");
    }
    
    private void otherBonuses() {
        stat("saves bonus").aggN(root);
        for (final String save : saves) {
            stat("saves bonus")
            .toN(save + " bonus")
            .aggN()
            .toN(save);
        }
    }
    
}
