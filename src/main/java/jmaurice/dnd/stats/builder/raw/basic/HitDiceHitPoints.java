package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class HitDiceHitPoints extends BaseBuilder {

    public HitDiceHitPoints(final Stats stats) { super(stats); }

    public void build() {
        agg("hit dice", leaf, values -> {
            final Map<Integer, Integer> aggregated = new TreeMap<>();
            values.forEach(x -> {
                aggregated.compute(x.regexExtract("^([0-9]+)d[0-9]+$").getIntValue(), (Integer k, Integer v) -> {
                    if (v == null)
                        v = 0;
                    v += x.regexExtract("^[0-9]+d([0-9]+)$").getIntValue();
                    return v;
                });
            });
            final StringBuilder v = new StringBuilder();
            for (final Map.Entry<Integer, Integer> x : aggregated.entrySet()) {
                if ( ! v.isEmpty())
                    v.append("+");
                v.append(x.getKey());
                v.append("d");
                v.append(x.getValue());
            }
            return new Value(v.toString());
        });
        agg("num hit dice", values -> sumAsInts(values));
        toN("num hit dice", "hit dice", values -> {
            return val01(values).stream()
                    .flatMap(x -> x.split("\\+").stream())
                    .map(x -> x.regexExtract("^([0-9]+)d[0-9]+$"))
                    .toList();
        });
        agg("hit points", leaf, values -> sumAsInts(values));
        input("hit points", Arrays.asList("constitution modifier", "num hit dice"), stats -> {
            final Integer constitutionModifier = stats.get("constitution modifier").val01().map(x -> x.getIntValue()).orElse(null);
            if (constitutionModifier == null)
                return null;
            final int numHitDice = stats.get("num hit dice").val1().getIntValue();
            return new Value(numHitDice * constitutionModifier, "con");
        });
    }

}
