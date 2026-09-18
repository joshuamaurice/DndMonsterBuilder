package jmaurice.dnd.stats.builder.raw.creaturetypes;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.builder.raw.basic.Saves;
import jmaurice.dnd.stats.builder.raw.basic.Skills;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Outsiders extends BaseBuilder {
    
    public static final List<String> classSkills = Arrays.asList(
            "acrobatics", "climb", "escape artist", "fly", "intimidate", 
            "perception", "spellcraft", "stealth", "survival", "swim"
            //plus one more which depends on the outsider
            );

    public Outsiders(final Stats stats) { super(stats); }

    public void build() {
        //outsider hit dice
        stat("outsider hit dice").agg(root, values -> sumAsInts(values));
        stat("outsider hit dice").to1("hit dice", input -> new Value(input.getIntValue() + "d10").source("outsider"));
        stat("outsider hit dice").to1("base attack bonus", input -> input.mult(1.00).source("outsider"));
        chooseTwoGoodSaves();
        chooseOneAdditionalClassSkill();
        
        //epic outsider hit dice
        stat("epic outsider hit dice").agg(root, values -> sumAsInts(values));
        stat("epic outsider hit dice").to1("hit dice", input -> new Value(input.getIntValue() + "d10").source("epic outsider"));
        stat("epic outsider hit dice").to1("epic base attack bonus", input -> input.mult(0.5).source("epic outsider"));
        Saves.saves.forEach(save -> stat("epic outsider hit dice").to1(save, input -> input.mult(0.5).source("epic outsider")));
        
        //outsider creature type
        stat("creature type").to1("outsider", input -> input.getStringValue().equals("outsider") ? new Value(true) : null);
        stat("outsider").to1("senses", new Value("darkvision 60 ft")); 
        classSkills.forEach(skill -> stat("outsider").to1(skill + " class skill", input -> new Value(true).source("aberration")));
    }
    
    private void chooseTwoGoodSaves() {
        stat("outsider good saves").aggN(root, values -> {
            if (values.isEmpty())
                throw new RuntimeException("expected \"good outsider saves\" would have exactly two unique values");
            if (values.size() != 2)
                throw new RuntimeException("<b>ERROR expected \"outsider good saves\" would have exactly two unique values</b>");
            final Set<String> valuesUnique = new LinkedHashSet<>(values.stream().map(x -> x.getStringValue()).toList());
            if (valuesUnique.size() != 2)
                throw new RuntimeException("<b>ERROR expected \"outsider good saves\" would have exactly two unique values</b>");
            for (final String value : valuesUnique) {
                if ( ! Arrays.asList("fortitude", "reflex", "will").contains(value))
                    throw new RuntimeException("invalid value \"" + value + "\" for \"outsider good saves\"");
            }
            return values;
        });
        
        for (final String save : Saves.saves) {
            input1(save + " bad levels", Arrays.asList("outsider hit dice", "outsider good saves"), stats -> {
                final Integer outsiderHitDice = stats.get("outsider hit dice").getIntValue();
                if (outsiderHitDice == null)
                    return null;
                if (stats.get("outsider good saves").getValues().stream().filter(x -> x.getStringValue().equals(save)).findAny().isEmpty())
                    return null;
                return new Value(outsiderHitDice).source("outsider");
            });
            input1(save + " good levels", Arrays.asList("outsider hit dice", "outsider good saves"), stats -> {
                final Integer outsiderHitDice = stats.get("outsider hit dice").getIntValue();
                if (outsiderHitDice == null)
                    return null;
                if (stats.get("outsider good saves").getValues().stream().filter(x -> x.getStringValue().equals(save)).findAny().isPresent())
                    return new Value(outsiderHitDice).source("outsider");
                return null;
            });
        }
    }
    
    private void chooseOneAdditionalClassSkill() {
        stat("outsider additional class skill").aggN(root, values -> {
            if (values.size() != 1)
                throw new RuntimeException("expected \"outsider additional class skill\" would have exactly one value");
            final String value = values.get(0).getStringValue();
            if ( ! Skills.allSkills.contains(value))
                throw new RuntimeException("invalid value \"" + value + "\" for \"outsider additional class skill\"");
            return values;
        });
        for (final String skill : Skills.allSkills) {
            input1(skill + " class skill", Arrays.asList("outsider hit dice", "outsider additional class skill"), stats -> {
                if (stats.get("outsider hit dice").getValues().isEmpty())
                    return null;
                if (stats.get("outsider additional class skill").val1().getStringValue().equals(skill))
                    return new Value(1);
                return null;
            });
        }
    }

}
