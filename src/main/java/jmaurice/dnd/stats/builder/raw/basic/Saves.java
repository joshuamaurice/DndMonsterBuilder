package jmaurice.dnd.stats.builder.raw.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Saves extends BaseBuilder {

    public Saves(final Stats stats) { super(stats); }

    public void build() {
        final List<String> saves = Arrays.asList("fortitude", "reflex", "will");
        saves.forEach(save -> agg(save, leaf, values -> sumAsInts(values)));
        
        saves.forEach(save -> agg("base " + save,                 leaf, values -> sumAsDoubles(values).floor())); //sumAsDoubles for partial save bonus multiclassing
        saves.forEach(save -> to1(          save, "base " + save,       value  -> value.source("base save")));
        saves.forEach(save -> to1("base " + save, "good " + save,                 new Value(2, "flat +2 for good save")));
        saves.forEach(save -> agg("good " + save,                 root, values -> new Value(true, first(values).source))); //TODO remove root
        
        to1("fortitude", "constitution modifier");
        to1("reflex", "dexterity modifier");
        to1("will", "wisdom modifier");
        
        final List<String> types = Arrays.asList("resistance", "luck", "divine", "profane", "sacred");
        types.forEach(type ->                       agg(type + " bonus to saves",   root, values -> maxAsInts(values)));
        types.forEach(type -> saves.forEach(save -> agg(type + " bonus to " + save, root, values -> maxAsInts(values))));
        Arrays.asList("resistance", "luck").forEach(type -> saves.forEach(saveName -> {
            final List<String> inputStatNames = new ArrayList<>();;
            inputStatNames.add(type + " bonus to saves");
            inputStatNames.add(type + " bonus to " + saveName);
            stats.input(saveName, inputStatNames, input -> list01(maxAsInts(input)));
        }));
        //house rule: divine, profane, sacred, do not stack with each other
        saves.forEach(saveName -> {
            final List<String> inputStatNames = new ArrayList<>();;
            Arrays.asList("divine", "profane", "sacred").forEach(type -> inputStatNames.add(type + " bonus to saves"));
            Arrays.asList("divine", "profane", "sacred").forEach(type -> inputStatNames.add(type + " bonus to " + saveName));
            stats.input(saveName, inputStatNames, input -> list01(maxAsInts(input)));
        });
    }

}
