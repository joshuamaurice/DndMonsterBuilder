package jmaurice.dnd.stats.builder.raw.creaturetypes;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Outsiders extends BaseBuilder {

    public Outsiders(final Stats stats) { super(stats); }

    public void build() {
        //outsider hit dice
        agg("outsider hit dice", root, values -> sumAsInts(values));
        to1("hit dice", "outsider hit dice", input -> new Value(input.getIntValue() + "d10", "outsider"));
        to1("base attack bonus", "outsider hit dice", input -> input.mult(1.00).source("outsider"));
        saves();
        //epic outsider hit dice
        agg("epic outsider hit dice", root, values -> sumAsInts(values));
        to1("hit dice", "epic outsider hit dice", input -> new Value(input.getIntValue() + "d10", "epic outsider"));
        to1("base attack bonus", "epic outsider hit dice", input -> input.mult(0.5).source("epic outsider"));
        Arrays.asList("fortitude", "reflex", "will").forEach(save -> 
                to1(save, "epic outsider hit dice", input -> input.mult(0.5).source("epic outsider")));
        //outsider creature type
        to1("outsider", "creature type", input -> input.getStringValue().equals("outsider") ? new Value(true) : null);
        to1("senses", "outsider", new Value("darkvision 60 ft")); 
        final List<String> classSkills = Arrays.asList(
                "acrobatics", "climb", "escape artist", "fly", "intimidate", 
                "perception", "spellcraft", "stealth", "survival", "swim"
                //"knowledge (pick one)
                );
        classSkills.forEach(skill -> to1(skill + " class skill", "outsider", input -> new Value(true, "aberration")));
    }
    
    private void saves() {
        agg("outsider good saves", root, values -> {
            if (values.isEmpty())
                return null;
            if (values.size() != 2)
                throw new RuntimeException("expected exactly two outsider good saves");
            final Set<String> goodSaves = new LinkedHashSet<>(values.stream().map(x -> x.getStringValue()).toList());
            if (goodSaves.size() != 2)
                throw new RuntimeException("expected exactly two outsider good saves");
            for (final String goodSave : goodSaves) {
                if ( ! Arrays.asList("fortitude", "reflex", "will").contains(goodSave))
                    throw new RuntimeException("invalid outsider good save: " + goodSave);
            }
            return join(sort(values), ", ").orElse(null);
        });
        
        for (final String save : Arrays.asList("fortitude", "reflex", "will")) {
            input(save + " bad levels", Arrays.asList("outsider hit dice", "outsider good saves"), stats -> {
                final Integer outsiderHitDice = stats.get("outsider hit dice").getIntValue();
                if (outsiderHitDice == null)
                    return null;
                if (stats.get("outsider good saves").val1().getStringValue().contains(save))
                    return null;
                return new Value(outsiderHitDice, "outsider");
            });
            input(save + " good levels", Arrays.asList("outsider hit dice", "outsider good saves"), stats -> {
                final Integer outsiderHitDice = stats.get("outsider hit dice").getIntValue();
                if (outsiderHitDice == null)
                    return null;
                if (stats.get("outsider good saves").val1().getStringValue().contains(save))
                    return new Value(outsiderHitDice, "outsider");
                return null;
            });
        }
    }

}
