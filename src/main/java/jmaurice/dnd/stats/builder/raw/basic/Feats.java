package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Collections;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Feats extends BaseBuilder {

    public Feats(final Stats stats) { super(stats); }

    public void build() {
        aggN("feats", leaf, values -> values);
        toN("fortitude", "feats", values -> getStringValues(values).contains("great fortitude")
                ? Collections.singletonList(new Value("2", "great fortitude"))
                : Collections.emptyList());
        toN("reflex", "feats", values -> getStringValues(values).contains("lightning reflexes")
                ? Collections.singletonList(new Value("2", "lightning reflexes"))
                : Collections.emptyList());
        toN("will", "feats", values -> getStringValues(values).contains("iron will")
                ? Collections.singletonList(new Value("2", "iron will"))
                : Collections.emptyList());
    }

}
