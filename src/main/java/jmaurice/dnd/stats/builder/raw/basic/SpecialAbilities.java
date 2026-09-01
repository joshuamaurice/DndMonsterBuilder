package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class SpecialAbilities extends BaseBuilder {

    public SpecialAbilities(final Stats stats) { super(stats); }

    public void build() {
        to1("special abilities short", "pounce", root, new Value("pounce"));
        
        agg("bad SR", root);
        input("defensive abilities", Arrays.asList("bad SR", "challenge rating"), stats -> {
            if ( ! stats.get("bad SR").getBooleanValue(false))
                return null;
            final int challengeRating = stats.get("challenge rating").getIntValue();
            return new Value("SR " + (6 + challengeRating));
        });
        
        agg("medium SR", root);
        input("defensive abilities", Arrays.asList("medium SR", "challenge rating"), stats -> {
            if ( ! stats.get("medium SR").getBooleanValue(false))
                return null;
            final int challengeRating = stats.get("challenge rating").getIntValue();
            return new Value("SR " + (11 + challengeRating));
        });
        
        agg("good SR", root);
        input("defensive abilities", Arrays.asList("good SR", "challenge rating"), stats -> {
            if ( ! stats.get("good SR").getBooleanValue(false))
                return null;
            final int challengeRating = stats.get("challenge rating").getIntValue();
            return new Value("SR " + (16 + challengeRating));
        });

    }

}
