package jmaurice.dnd.stats.builder.raw.creaturetypes;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Constructs extends BaseBuilder {

    public Constructs(final Stats stats) { super(stats); }

    public void build() {
        stat("creature type").to1("construct", input -> input.getStringValue().equals("construct") ? new Value(true) : null);
        stat("construct").to1("immunities", new Value("construct immunities").source("construct type"));
        //TODO
    }

}
