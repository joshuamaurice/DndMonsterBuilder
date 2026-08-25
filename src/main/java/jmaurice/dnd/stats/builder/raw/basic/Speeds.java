package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;
import java.util.List;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Speeds extends BaseBuilder {

    public Speeds(final Stats stats) { super(stats); }

    public void build() {
        final List<String> types = Arrays.asList("land", "fly", "burrow", "swim");
        
        agg("speeds", leaf, values -> join(sort(values), ", "));
        
        types.forEach(type -> agg("ft " + type + " speed", values -> sumAsInts(values)));
        types.stream().filter(x -> ! x.equals("fly")).forEach(type -> 
            to1("speeds", "ft " + type + " speed", value -> new Value(value.getIntValue() + " ft " + type))
        );
        agg("fly maneuverability", root);
        input("speeds", Arrays.asList("ft fly speed", "fly maneuverability"), stats -> {
            final Integer fly = stats.get("ft fly speed").val01().map(x -> x.getIntValue()).orElse(null);
            final String maneuverability = stats.get("fly maneuverability").val01().map(x -> x.getStringValue()).orElse(null);
            if (fly == null && maneuverability == null)
                return null;
            if (fly == null)
                throw new RuntimeException("missing fly speed");
            if (maneuverability == null)
                throw new RuntimeException("missing fly maneuverability");
            return new Value(fly + " ft fly (" + maneuverability + ")");
        });
        
        types.forEach(type -> agg("ft base " + type + " speed", root, values -> maxAsInts(values)));
        types.forEach(type -> to1("ft " + type + " speed", "ft base " + type + " speed"));
        
        agg("ft enhance all speeds", root, values -> maxAsInts(values));
        types.forEach(type -> {
            aggN("ft enhance " + type + " speed", root, values -> values);
            final List<String> inputStatNames = Arrays.asList("ft enhance all speeds", "ft enhance " + type + " speed");
            input("ft " + type + " speed", inputStatNames, input -> maxAsInts(input));
        });
    }

}
