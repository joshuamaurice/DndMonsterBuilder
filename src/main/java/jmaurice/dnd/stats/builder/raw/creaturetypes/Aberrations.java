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
        agg("aberration hit dice", values -> sumAsInts(values));
        to1("hit dice", "aberration hit dice", input -> new Value(input.getIntValue() + "d8", "aberration"));
        to1("base attack bonus", "aberration hit dice", input -> input.mult(0.75).source("aberration"));
        to1("base fortitude", "aberration hit dice", input -> input.mult(0.3334).source("aberration bad fortitude"));
        to1("base reflex",    "aberration hit dice", input -> input.mult(0.3334).source("aberration bad reflex"));
        to1("base will",      "aberration hit dice", input -> input.mult(0.5).source("aberration good will"));
        to1("good will",      "aberration hit dice", input -> new Value(true, "aberration"));
        
        //aberration creature type
        to1("aberration", "creature type", input -> input.getStringValue().equals("aberration") ? new Value(true) : null);
        to1("senses", "aberration", new Value("darkvision 60 ft")); 
        final List<String> classSkills = Arrays.asList(
                "acrobatics", "climb", "escape artist", "fly", "intimidate", 
                "perception", "spellcraft", "stealth", "survival", "swim"
                //"knowledge (pick one)
                );
        classSkills.forEach(skill -> to1(skill + " class skill", "aberration", input -> new Value(true, "aberration")));
    }

}
