package jmaurice.dnd.stats.builder.raw.basic;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Feats extends BaseBuilder {

    public Feats(final Stats stats) { super(stats); }

    public void build() {
        aggN("feats", leaf, values -> sort(values));
        
        to1("feats", "deadly aim", root, new Value("deadly aim"));
        
        to1("feats", "great fortitude", root, new Value("great fortitude"));
        to1("fortitude", "great fortitude", new Value(2));
        
        to1("feats", "iron will", root, new Value("iron will"));
        to1("will", "iron will", new Value(2));
        
        to1("feats", "lightning reflexes", root, new Value("lightning reflexes"));
        to1("reflex", "lightning reflexes", new Value(2));
        
        to1("feats", "point-blank shot", root, new Value("point-blank shot"));
        to1("feats", "power attack", root, new Value("power attack"));
        to1("feats", "precise shot", root, new Value("precise shot"));
        
    }

}
