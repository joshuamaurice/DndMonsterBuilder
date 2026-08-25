package jmaurice.dnd.stats.builder.raw.creaturetypes;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Constructs extends BaseBuilder {

    public Constructs(final Stats stats) { super(stats); }

    public void build() {
        to1("construct", "creature type", input -> input.getStringValue().equals("construct") ? new Value(true) : null);
        to1("immunities", "construct", new Value("construct immunities", "construct type"));
        //TODO
    }

}
