package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;
import java.util.List;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class ArmorClass extends BaseBuilder {

    public ArmorClass(final Stats stats) { super(stats); }

    public void build() {
        agg("armor class",                          leaf, values -> sumAsInts(values));
        agg("touch armor class",                    leaf, values -> sumAsInts(values));
        agg("combat maneuvers defense",             leaf, values -> sumAsInts(values));
        agg("flat-footed armor class",              leaf, values -> sumAsInts(values));
        agg("flat-footed touch armor class",        leaf, values -> sumAsInts(values));
        agg("flat-footed combat maneuvers defense", leaf, values -> sumAsInts(values));
        
        to1("armor class",                          "default", new Value(10));
        to1("touch armor class",                    "default", new Value(10));
        to1("combat maneuvers defense",             "default", new Value(10));
        to1("flat-footed armor class",              "default", new Value(10));
        to1("flat-footed touch armor class",        "default", new Value(10));
        to1("flat-footed combat maneuvers defense", "default", new Value(10));
        
        to1("armor class",                          "size modifier to attack", value -> value.source("size"));
        to1("touch armor class",                    "size modifier to attack", value -> value.source("size"));
        to1("combat maneuvers defense",             "size modifier to attack", value -> value.mult(-1).source("size"));
        to1("flat-footed armor class",              "size modifier to attack", value -> value.source("size"));
        to1("flat-footed touch armor class",        "size modifier to attack", value -> value.source("size"));
        to1("flat-footed combat maneuvers defense", "size modifier to attack", value -> value.mult(-1).source("size"));
        
        to1("combat maneuvers defense",             "base attack bonus", value -> value.source("base attack bonus"));
        to1("flat-footed combat maneuvers defense", "base attack bonus", value -> value.source("base attack bonus"));
        to1("combat maneuvers defense",             "epic base attack bonus", value -> value.source("epic base attack bonus"));
        to1("flat-footed combat maneuvers defense", "epic base attack bonus", value -> value.source("epic base attack bonus"));
        
        to1("combat maneuvers defense",             "strength modifier", value -> value.source("str"));
        to1("flat-footed combat maneuvers defense", "strength modifier", value -> value.source("str"));
        
        to1("armor class",                          "dexterity modifier", value -> value.source("dex"));
        to1("touch armor class",                    "dexterity modifier", value -> value.source("dex"));
        to1("combat maneuvers defense",             "dexterity modifier", value -> value.source("dex"));
        to1("flat-footed armor class",              "dexterity penalty", value -> value.getIntValue() < 0 ? value.source("dex") : null);
        to1("flat-footed touch armor class",        "dexterity penalty", value -> value.getIntValue() < 0 ? value.source("dex") : null);
        to1("flat-footed combat maneuvers defense", "dexterity penalty", value -> value.getIntValue() < 0 ? value.source("dex") : null);
        
        agg("natural armor bonus", leaf, values -> sumAsInts(values));
        to1("armor class",                          "natural armor bonus", value -> value.source("natural"));
        to1("flat-footed armor class",              "natural armor bonus", value -> value.source("natural"));
        
        final List<String> types = Arrays.asList("deflection", "luck", "divine", "profane", "sacred");
        types.forEach(type -> agg(type + " bonus to armor class", root, values -> maxAsInts(values)));
        types.forEach(type -> to1("armor class",                          type + " bonus to armor class"));
        types.forEach(type -> to1("touch armor class",                    type + " bonus to armor class"));
        types.forEach(type -> to1("combat maneuvers defense",             type + " bonus to armor class"));
        types.forEach(type -> to1("flat-footed armor class",              type + " bonus to armor class"));
        types.forEach(type -> to1("flat-footed touch armor class",        type + " bonus to armor class"));
        types.forEach(type -> to1("flat-footed combat maneuvers defense", type + " bonus to armor class"));
        
    }

}
