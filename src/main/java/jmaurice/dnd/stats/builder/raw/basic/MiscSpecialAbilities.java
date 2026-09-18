package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class MiscSpecialAbilities extends BaseBuilder {

    public MiscSpecialAbilities(final Stats stats) { super(stats); }

    public void build() {
        stat("all-around vision").agg(root).to1("defensive abilities", new Value("<a href=\"https://www.d20pfsrd.com/BESTIARY/RULES-FOR-MONSTERS/UNIVERSAL-MONSTER-RULES/#all-around_vision_ex\">all-around vision</a>"));
        stat("pounce").agg(root).to1("special abilities short", new Value("pounce"));
        stat("tail sweep").agg(root).to1("special abilities short", new Value("tail sweep"));
        
        powerResistance();
        spellResistance();
    }
    
    private void powerResistance() {
        stat("power resistance").agg();
        
        stat("bad PR").agg(root);
        input1("power resistance", Arrays.asList("bad PR", "challenge rating"), stats -> {
            if (stats.get("bad PR").getValues().size() == 0)
                return null;
            return new Value(6 + stats.get("challenge rating").getIntValue());
        });
        
        stat("medium PR").agg(root);
        input1("power resistance", Arrays.asList("medium PR", "challenge rating"), stats -> {
            if (stats.get("medium PR").getValues().size() == 0)
                return null;
            return new Value(11 + stats.get("challenge rating").getIntValue());
        });
        
        stat("good PR").agg(root);
        input1("power resistance", Arrays.asList("good PR", "challenge rating"), stats -> {
            if (stats.get("good PR").getValues().size() == 0)
                return null;
            return new Value(16 + stats.get("challenge rating").getIntValue());
        });

    }

    private void spellResistance() {
        stat("spell resistance").agg();
        
        stat("bad SR").agg(root);
        input1("spell resistance", Arrays.asList("bad SR", "challenge rating"), stats -> {
            if (stats.get("bad SR").getValues().size() == 0)
                return null;
            return new Value(6 + stats.get("challenge rating").getIntValue());
        });
        
        stat("medium SR").agg(root);
        input1("spell resistance", Arrays.asList("medium SR", "challenge rating"), stats -> {
            if (stats.get("medium SR").getValues().size() == 0)
                return null;
            return new Value(11 + stats.get("challenge rating").getIntValue());
        });
        
        stat("good SR").agg(root);
        input1("spell resistance", Arrays.asList("good SR", "challenge rating"), stats -> {
            if (stats.get("good SR").getValues().size() == 0)
                return null;
            return new Value(16 + stats.get("challenge rating").getIntValue());
        });

    }

}
