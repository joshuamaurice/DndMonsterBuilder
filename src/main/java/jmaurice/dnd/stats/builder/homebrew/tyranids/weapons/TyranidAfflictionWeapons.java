package jmaurice.dnd.stats.builder.homebrew.tyranids.weapons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class TyranidAfflictionWeapons extends BaseBuilder {

    public TyranidAfflictionWeapons(final Stats stats) { super(stats); }

    public void build() {
        agg("fleshborer", root, values -> sumAsInts(values));
        agg("fleshborer damage dice", values -> sumAsInts(values));
        input("fleshborer damage dice", Arrays.asList("fleshborer", "num hit dice"), input -> {
            final Integer weaponCount = input.get("fleshborer").getIntValue();
            if (weaponCount == null)
                return null;
            final int numHitDice = input.get("num hit dice").val1().getIntValue();
            return new Value(numHitDice);
        });
        input("weapon properties", Arrays.asList("fleshborer", "fleshborer damage dice"), input -> {
            final Integer weaponCount = input.get("fleshborer").getIntValue();
            if (weaponCount == null)
                return null;
            final int damageDice = input.get("fleshborer damage dice").val1().getIntValue();
            final List<String> properties = new ArrayList<>();
            properties.add("name=fleshborer");
            properties.add("num=" + weaponCount);
            properties.add("natural");
            properties.add("range");
            properties.add("ft range increment=30");
            properties.add("two-handed");
            properties.add("no strength to damage");
            properties.add("base damage=" + damageDice + "d6");
            return new Value(properties.stream().collect(Collectors.joining(",")));
        });
        to1("fleshborer damage dice", "default", new Value(5));
    }

}
