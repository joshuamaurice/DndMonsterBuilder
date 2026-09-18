package jmaurice.dnd.stats.builder.homebrew.tyranids;

import java.util.Arrays;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class TyranidMeleeWeapons extends BaseBuilder {

    public TyranidMeleeWeapons(final Stats stats) { super(stats); }

    public void build() {
        stat("boneswords").agg(root).to1("bonesword");
        stat("bonesword").agg(root);
        input1("weapon properties", Arrays.asList("bonesword", "psionic manifester level"), stats -> {
            final Integer numWeapons = stats.get("bonesword").getIntValue();
            if (numWeapons == null)
                return null;
            final int manifesterLevel = stats.get("psionic manifester level").getIntValue();
            final int attackModifier = (1 + manifesterLevel) / 2;
            return new Value("name=bonesword,num=" + numWeapons + ",melee,natural,natural weapon damage size modifiers=1,critical threat range=19,attack modifier=" + attackModifier);
        });
        stat("flesh hooks").agg(root).to1("weapon properties", input -> {
            return new Value("name=flesh hooks,num=" + input.getIntValue() + ",melee,natural,natural weapon damage size modifiers=0,additional effect=entangle as per a net weapon");
        });
        stat("lashwhip").agg(root).to1("weapon properties", input -> {
            return new Value("name=lashwhip,num=" + input.getIntValue() + ",melee,natural,natural weapon damage size modifiers=-2,half strength to damage,additional effect=entangle as per a net weapon");
        });
        stat("lashwhip pod").agg(root).to1("weapon properties", input -> {
            return new Value("name=lashwhip pod,num=1,num attacks multiplier=10,melee,natural,natural weapon damage size modifiers=-2,half strength to damage,additional effect=entangle as per a net weapon");
        });
        
        //
        stat("crushing claws").agg(root).to1("weapon properties", input -> {
            return new Value("name=crushing claws,num=" + input.getIntValue() + ",melee,natural,natural weapon damage size modifiers=1,additional effect=grab");
        });
        stat("rending claws").agg(root).to1("weapon properties", input -> {
            return new Value("name=crushing claws,num=" + input.getIntValue() + ",melee,natural,natural weapon damage size modifiers=1,additional effect=rend");
        });
        stat("scything talons").agg(root).to1("weapon properties", input -> {
            return new Value("name=scything talons,num=" + input.getIntValue() + ",melee,natural,natural weapon damage size modifiers=1,critical threat multiplier=4");
        });
        stat("tail bone mace").agg(root).to1("weapon properties", input -> {
            return new Value("name=tail bone mace,num=" + input.getIntValue() + ",melee,natural,natural weapon damage size modifiers=1,secondary natural");
        });
        stat("ultrasharp claws").agg(root).to1("weapon properties", input -> {
            return new Value("name=ultrasharp claws,num=" + input.getIntValue() + ",melee,natural,natural weapon damage size modifiers=0,additional effect=overcomes hardness and DR like adamantine");
        });
    }

}
