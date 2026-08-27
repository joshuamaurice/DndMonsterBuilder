package jmaurice.dnd.stats.builder.homebrew;

import java.util.Arrays;
import java.util.Collections;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Tyranids extends BaseBuilder {

    public Tyranids(final Stats stats) { super(stats); }

    public void build() {
        agg("tyranid", root);
        agg("synapse", root);
        agg("bio-titan", root);
        to1("creature type", "tyranid", input -> new Value("aberration", "tyranid"));
        to1("creature subtypes", "tyranid", input -> new Value("tyranid"));
        stats.input("best psionic power DC", Arrays.asList("intelligence modifier", "psionic manifester level"), stats -> {
            final Integer psionicManifesterLevel = stats.get("psionic manifester level").getIntValue();
            if (psionicManifesterLevel == null)
                return null;
            final int intelligenceModifier = stats.get("intelligence modifier").getIntValue();
            return Collections.singletonList(new Value(10 + intelligenceModifier + (psionicManifesterLevel + 1) / 2));
        });
        new TyranidAfflictionWeapons(stats).build();
        new TyranidUpgrades(stats).build();
    }
        
}
