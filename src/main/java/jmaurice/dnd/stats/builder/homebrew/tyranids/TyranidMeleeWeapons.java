package jmaurice.dnd.stats.builder.homebrew.tyranids;

import java.util.Arrays;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class TyranidMeleeWeapons extends BaseBuilder {

    public TyranidMeleeWeapons(final Stats stats) { super(stats); }

    public void build() {
        to1("bonesword", "boneswords", root);
        agg("bonesword", root);
        input("weapon properties", Arrays.asList("bonesword", "psionic manifester level"), stats -> {
            final Integer numWeapons = stats.get("bonesword").getIntValue();
            if (numWeapons == null)
                return null;
            final int manifesterLevel = stats.get("psionic manifester level").getIntValue();
            final int attackModifier = (1 + manifesterLevel) / 2;
            return new Value("name=bonesword,num=" + numWeapons + ",melee,natural,natural weapon damage size modifiers=1,critical threat range=19,attack modifier=" + attackModifier);
        });
        to1("weapon properties", "flesh hooks", root, input -> {
            return new Value("name=flesh hooks,num=" + input.getIntValue() + ",melee,natural,natural weapon damage size modifiers=0,additional effect=entangle as per a net weapon");
        });
        to1("weapon properties", "lashwhip", root, input -> {
            return new Value("name=lashwhip,num=" + input.getIntValue() + ",melee,natural,natural weapon damage size modifiers=-2,half strength to damage,additional effect=entangle as per a net weapon");
        });
        
        //
        to1("weapon properties", "crushing claws", root, input -> {
            return new Value("name=crushing claws,num=" + input.getIntValue() + ",melee,natural,natural weapon damage size modifiers=1,additional effect=grab");
        });
        to1("weapon properties", "rending claws", root, input -> {
            return new Value("name=crushing claws,num=" + input.getIntValue() + ",melee,natural,natural weapon damage size modifiers=1,additional effect=rend");
        });
        to1("weapon properties", "scything talons", root, input -> {
            return new Value("name=scything talons,num=" + input.getIntValue() + ",melee,natural,natural weapon damage size modifiers=1,critical threat multiplier=4");
        });
        to1("weapon properties", "tail bone mace", root, input -> {
            return new Value("name=tail bone mace,num=" + input.getIntValue() + ",melee,natural,natural weapon damage size modifiers=1,secondary natural");
        });
        to1("weapon properties", "ultrasharp claws", root, input -> {
            return new Value("name=ultrasharp claws,num=" + input.getIntValue() + ",melee,natural,natural weapon damage size modifiers=0,additional effect=overcomes hardness and DR like adamantine");
        });
    }

}
