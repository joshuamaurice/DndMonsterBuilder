package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class HitDiceHitPoints extends BaseBuilder {

    public HitDiceHitPoints(final Stats stats) { super(stats); }

    public void build() {
        stat("hit dice")
        .agg(values -> {
            final Map<Integer, Integer> aggregated = new TreeMap<>();
            values.forEach(x -> {
                aggregated.compute(x.regexExtract("^[0-9]+d([0-9]+)$").getIntValue(), (Integer k, Integer v) -> {
                    if (v == null)
                        v = 0;
                    v += x.regexExtract("^([0-9]+)d[0-9]+$").getIntValue();
                    return v;
                });
            });
            final StringBuilder v = new StringBuilder();
            for (final Map.Entry<Integer, Integer> x : aggregated.entrySet()) {
                if ( ! v.isEmpty())
                    v.append("+");
                v.append(x.getValue());
                v.append("d");
                v.append(x.getKey());
            }
            return new Value(v.toString());
        });
        
        stat("hit dice")
        .toN("num hit dice", values -> {
            return val01(values).stream()
                    .flatMap(x -> x.split("\\+").stream())
                    .map(x -> x.regexExtract("^([0-9]+)d[0-9]+$"))
                    .toList();
        })
        .agg(values -> sumAsInts(values));
        
        stat("hit points")
        .agg(values -> sumAsInts(values));
        
        input1("hit points", Arrays.asList("constitution modifier", "num hit dice"), stats -> {
            final Integer constitutionModifier = stats.get("constitution modifier").getIntValue();
            if (constitutionModifier == null)
                return null;
            final Integer numHitDice = stats.get("num hit dice").getIntValue();
            if (numHitDice == null)
                return null;
            return new Value(numHitDice * constitutionModifier).source("con");
        });
        
        stat("hit dice")
        .toN("hit points", value -> {
            return Collections.singletonList(sumAsInts(val01(value).stream()
                    .flatMap(x -> x.split("\\+").stream())
                    .map(x -> x.regexExtract("^([0-9]+)d[0-9]+$").mult((x.regexExtract("^[0-9]+d([0-9]+)$").getIntValue() / 2) + 1).floor())
                    .toList()).source("hit dice"));
        });
    }

}
