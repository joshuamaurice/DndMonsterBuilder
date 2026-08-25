package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;
import java.util.List;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class NaturalWeapons extends BaseBuilder {

    public NaturalWeapons(final Stats stats) { super(stats); }

    public void build() {
        final List<String> naturalWeapons = Arrays.asList(
                "bite", "bites", "gore", "gores", "pincer", "pincers", "tail slap", "tail slaps",
                "claw", "claws", "hoof", "hoofs", "hooves", "tentacle", "tentacles", 
                "wing", "wings", "slam", "slams", "sting", "stings", "tail sting", "tail stings", "talon", "talons"
                );
        final List<String> reducedDamageNaturalWeapons = Arrays.asList(
                "claw", "claws", "hoof", "hoofs", "hooves", "tentacle", "tentacles", 
                "wing", "wings", "slam", "slams", "sting", "stings", "tail sting", "tail stings", "talon", "talons"
                );
        final List<String> secondaryNaturalWeapons = Arrays.asList(
                "hoof", "hoofs", "hooves", "tentacle", "tentacles", "wing", "wings", 
                "pincer", "pincers", "tail slap", "tail slaps"
                );
        
        naturalWeapons.forEach(name -> agg(name, root, values -> sumAsInts(values)));
        naturalWeapons.forEach(name -> to1("weapon properties", name, value -> {
            final int num = value.getStringValue().equals("true") ? 1 : value.getIntValue();
            return new Value("name=" + name + ",num=" + num + ",melee,natural");
        }));
        reducedDamageNaturalWeapons.forEach(name -> to1("weapon properties", name, new Value("name=" + name + ",natural weapon damage size modifiers=-1")));
        secondaryNaturalWeapons.forEach(name -> to1("weapon properties", name, new Value("name=" + name + ",secondary natural")));
    }

}
