package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;
import java.util.List;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;

public class AbilityScores extends BaseBuilder {
    
    public AbilityScores(final Stats stats) { super(stats); }

    public void build() {
        final List<String> abilityScoreNames = Arrays.asList("strength", "dexterity", "constitution", "intelligence", "wisdom", "charisma");
        abilityScoreNames.forEach(n -> agg(n, rootleaf, values -> {
            if (values.stream().map(x -> x.getStringValue()).filter(x -> x.equals("-")).findAny().isPresent())
                return null;
            return sumAsInts(values);
        }));
        abilityScoreNames.forEach(n -> agg(n + " modifier", leaf, values -> sumAsInts(values).source(n)));
        abilityScoreNames.forEach(n -> agg(n + " bonus",    leaf));
        abilityScoreNames.forEach(n -> agg(n + " penalty",  leaf));
        abilityScoreNames.forEach(n -> to1(n + " modifier", n,               input -> input.value.equals("-") ? null : input.add(-10).mult(0.5).floor()));
        abilityScoreNames.forEach(n -> to1(n + " bonus",    n + " modifier", input -> input.max(0)));
        abilityScoreNames.forEach(n -> to1(n + " penalty",  n + " modifier", input -> input.min(0)));
    }
    

}
