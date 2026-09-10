package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class MiscSpecialAbilities extends BaseBuilder {

    public MiscSpecialAbilities(final Stats stats) { super(stats); }

    public void build() {
        to1("special abilities short", "pounce", root, new Value("pounce"));
        to1("special abilities short", "tail sweep", root, new Value("tail sweep"));
        
        commonPowerResistance();
        commonSpellResistance();
    }
    
    private void commonPowerResistance() {
        agg("power resistance");
        
        agg("bad PR", root);
        input("power resistance", Arrays.asList("bad PR", "challenge rating"), stats -> {
            if ( ! stats.get("bad PR").getBooleanValue(false))
                return null;
            return new Value(6 + stats.get("challenge rating").getIntValue());
        });
        
        agg("medium PR", root);
        input("power resistance", Arrays.asList("medium PR", "challenge rating"), stats -> {
            if ( ! stats.get("medium PR").getBooleanValue(false))
                return null;
            return new Value(11 + stats.get("challenge rating").getIntValue());
        });
        
        agg("good PR", root);
        input("power resistance", Arrays.asList("good PR", "challenge rating"), stats -> {
            if ( ! stats.get("good PR").getBooleanValue(false))
                return null;
            return new Value(16 + stats.get("challenge rating").getIntValue());
        });

    }

    private void commonSpellResistance() {
        agg("spell resistance");
        
        agg("bad SR", root);
        input("spell resistance", Arrays.asList("bad SR", "challenge rating"), stats -> {
            if ( ! stats.get("bad SR").getBooleanValue(false))
                return null;
            return new Value(6 + stats.get("challenge rating").getIntValue());
        });
        
        agg("medium SR", root);
        input("spell resistance", Arrays.asList("medium SR", "challenge rating"), stats -> {
            if ( ! stats.get("medium SR").getBooleanValue(false))
                return null;
            return new Value(11 + stats.get("challenge rating").getIntValue());
        });
        
        agg("good SR", root);
        input("spell resistance", Arrays.asList("good SR", "challenge rating"), stats -> {
            if ( ! stats.get("good SR").getBooleanValue(false))
                return null;
            return new Value(16 + stats.get("challenge rating").getIntValue());
        });

    }

}
