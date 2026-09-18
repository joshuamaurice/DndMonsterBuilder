package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;

public class AbilityScores extends BaseBuilder {
    
    public static final List<String> abilityScoreStatNames = Arrays.asList("strength", "dexterity", "constitution", "intelligence", "wisdom", "charisma");
    
    public AbilityScores(final Stats stats) { super(stats); }
    
    public void build() {
        for (final String name : abilityScoreStatNames) {
            stat(name).agg(rootleaf, values -> {
                final Set<String> values2 = values.stream().map(x -> x.getStringValue()).collect(Collectors.toSet());
                if (values2.contains("-"))
                    return null;
                if (values2.contains("none"))
                    return null;
                return sumAsInts(values);
            });
            stat(name)
            .to1(name + " modifier", input -> input.value.equals("-") ? null : input.add(-10).mult(0.5).floor())
            .agg(leaf, values -> sumAsInts(sort(values, value -> value.source)).source(name));
            
            stat(name)
            .to1(name + " bonus", input -> input.value.equals("-") ? null : input.add(-10).mult(0.5).floor())
            .agg(leaf, values -> sumAsInts(sort(values, value -> value.source)).source(name).atLeast(0));
            
            stat(name)
            .to1(name + " penalty", input -> input.value.equals("-") ? null : input.add(-10).mult(0.5).floor())
            .agg(leaf, values -> sumAsInts(sort(values, value -> value.source)).source(name).atMost(0));
        }
    }
    

}
