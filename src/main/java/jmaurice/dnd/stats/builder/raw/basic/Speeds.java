package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Speeds extends BaseBuilder {
    
    public static final List<String> movementModeNames = Collections.unmodifiableList(Arrays.asList(
            "land", "fly", "burrow", "swim", "climb"));

    public Speeds(final Stats stats) { super(stats); }

    public void build() {
        stat("speeds").aggN(leaf);
        
        for (final String mode : movementModeNames) {
            stat("ft " + mode + " speed").agg(root, values -> sumAsInts(values));
            if ( ! mode.equals("fly")) 
                stat("ft " + mode + " speed").to1("speeds", value -> new Value(value.getIntValue() + " ft " + mode));
        }
        stat("fly maneuverability").agg(root);
        input1("speeds", Arrays.asList("ft fly speed", "fly maneuverability"), stats -> {
            final Integer fly = stats.get("ft fly speed").getIntValue();
            final String maneuverability = stats.get("fly maneuverability").getStringValue();
            if (fly == null && maneuverability == null)
                return null;
            if (fly == null)
                throw new RuntimeException("missing fly speed");
            if (maneuverability == null)
                throw new RuntimeException("missing fly maneuverability");
            return new Value(fly + " ft fly (" + maneuverability + ")");
        });
    }

}
