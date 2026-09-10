package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;
import jmaurice.dnd.stats.impl.ValuedStat;

public class Speeds extends BaseBuilder {
    
    public static final List<String> movementModeNames = Collections.unmodifiableList(Arrays.asList(
            "land", "fly", "burrow", "swim", "climb"));

    public Speeds(final Stats stats) { super(stats); }

    public void build() {
        aggN("speeds", leaf, values -> values);
        
        movementModeNames.forEach(type -> agg("ft " + type + " speed", root, values -> sumAsInts(values)));
        movementModeNames.stream().filter(x -> ! x.equals("fly")).forEach(type -> 
            to1("speeds", "ft " + type + " speed", value -> new Value(value.getIntValue() + " ft " + type))
        );
        agg("fly maneuverability", root);
        input("speeds", Arrays.asList("ft fly speed", "fly maneuverability"), stats -> {
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
        
        agg("ft enhance all speeds", root, values -> maxAsInts(values));
        movementModeNames.forEach(type -> {
            aggN("ft enhance " + type + " speed", root, values -> values);
            to1("ft enhance " + type + " speed", "ft enhance all speeds");
            stats.postAgg(
                "ft " + type + " speed", 
                Arrays.asList("ft enhance " + type + " speed"),
                (writableStats, readOnlyStats) -> {
                    final Integer enhanceType = readOnlyStats.get("ft enhance " + type + " speed").getIntValue();
                    final ValuedStat typeSpeedStat = writableStats.get("ft " + type + " speed");
                    Integer typeSpeed = typeSpeedStat.getIntValue();
                    if (typeSpeed != null && enhanceType != null) {
                        typeSpeedStat.getValues().clear();
                        typeSpeedStat.getValues().add(new Value(typeSpeed + enhanceType));
                    }
                }
            );
        });
    }

}
