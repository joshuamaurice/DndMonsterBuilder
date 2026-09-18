package jmaurice.dnd.stats.builder.raw.creaturetypes;

import java.util.Arrays;
import java.util.List;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Aberrations extends BaseBuilder {

    public Aberrations(final Stats stats) { super(stats); }

    public void build() {
        //aberration hit dice
        stat("aberration hit dice").agg(values -> sumAsInts(values));
        stat("aberration hit dice").to1("hit dice", input -> new Value(input.getIntValue() + "d8").source("aberration"));
        stat("aberration hit dice").to1("base attack bonus", input -> input.mult(0.75).source("aberration"));
        stat("aberration hit dice").to1("fortitude bad levels");
        stat("aberration hit dice").to1("reflex bad levels");
        stat("aberration hit dice").to1("will good levels");
        
        //aberration creature type
        stat("creature type").to1("aberration", input -> input.getStringValue().equals("aberration") ? new Value(true) : null);
        stat("aberration").to1("senses", new Value("darkvision 60 ft")); 
        final List<String> classSkills = Arrays.asList(
                "acrobatics", "climb", "escape artist", "fly", "intimidate", 
                "perception", "spellcraft", "stealth", "survival", "swim"
                //"knowledge (pick one)
                );
        classSkills.forEach(skill -> stat("aberration").to1(skill + " class skill", input -> new Value(true)));
    }

}
