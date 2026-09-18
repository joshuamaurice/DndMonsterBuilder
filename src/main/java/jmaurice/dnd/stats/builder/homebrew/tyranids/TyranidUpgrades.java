package jmaurice.dnd.stats.builder.homebrew.tyranids;

import java.util.Arrays;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class TyranidUpgrades extends BaseBuilder {

    public TyranidUpgrades(final Stats stats) { super(stats); }

    public void build() {
        stat("tyranid upgrades").agg(root);
        upgrade1();
        upgrade2();
        upgrade3();
    }
    
    private void upgrade1() {
        stat("tyranid upgrades").many(
            x -> x.to1("challenge rating",    new Value(1)),
            x -> x.to1("aberration hit dice", new Value(2)),
            x -> x.to1("natural armor bonus", new Value(1)),
            x -> x.to1("strength",            new Value(1)),
            x -> x.to1("dexterity",           new Value(1))
        );
        input1("intelligence", Arrays.asList("tyranid upgrades", "synapse"), stats -> {
            if (stats.get("tyranid upgrades").val01().isEmpty())
                return null;
            if (stats.get("synapse").val01().isEmpty())
                return null;
            return null;
        });
        input1("psionic manifester level", Arrays.asList("tyranid upgrades", "synapse"), stats -> {
            if (stats.get("tyranid upgrades").val01().isEmpty())
                return null;
            if (stats.get("synapse").val01().isEmpty())
                return null;
            return null;
        });
        stat("tyranid upgrades").to1("feats", new Value("iron will"));
        stat("tyranid upgrades").to1("special abilities long", new Value("""
                <b>Adrenal Glands (Ex)</b><br/> This creature benefits from a haste spell for 10 rounds per day. However, this haste effect does not grant a bonus attack in a full attack. This is an extraordinary effect. These rounds may be split up.
                """));
        //unlock bioforms: gargoyle, carnifex
    }
    
    private void upgrade2() {
        stat("tyranid upgrades").many(
            x -> x.to1("challenge rating",    new Value(1)),
            x -> x.to1("aberration hit dice", new Value(2)),
            x -> x.to1("natural armor bonus", new Value(1)),
            x -> x.to1("strength",            new Value(1)),
            x -> x.to1("dexterity",           new Value(1))
        );
        input1("intelligence", Arrays.asList("tyranid upgrades", "synapse"), stats -> {
            if (stats.get("tyranid upgrades").val01().isEmpty())
                return null;
            if (stats.get("synapse").val01().isEmpty())
                return null;
            return null;
        });
        input1("psionic manifester level", Arrays.asList("tyranid upgrades", "synapse"), stats -> {
            if (stats.get("tyranid upgrades").val01().isEmpty())
                return null;
            if (stats.get("synapse").val01().isEmpty())
                return null;
            return null;
        });
        stat("tyranid upgrades").to1("feats", new Value("lightning reflexes"));
        stat("tyranid upgrades").to1("defensive abilities", new Value("still mind"));
        //unlock bioforms: bio-titans
    }

    private void upgrade3() {
        stat("tyranid upgrades").many(
            x -> x.to1("challenge rating",    new Value(1)),
            x -> x.to1("aberration hit dice", new Value(2)),
            x -> x.to1("natural armor bonus", new Value(1)),
            x -> x.to1("strength",            new Value(1)),
            x -> x.to1("dexterity",           new Value(1))
        );
        input1("intelligence", Arrays.asList("tyranid upgrades", "synapse"), stats -> {
            if (stats.get("tyranid upgrades").val01().isEmpty())
                return null;
            if (stats.get("synapse").val01().isEmpty())
                return null;
            return new Value(1).source("tyranid upgrade");
        });
        input1("psionic manifester level", Arrays.asList("tyranid upgrades", "synapse"), stats -> {
            if (stats.get("tyranid upgrades").val01().isEmpty())
                return null;
            if (stats.get("synapse").val01().isEmpty())
                return null;
            return null;
        });
        stat("tyranid upgrades").to1("feats", new Value("great fortitude"));
        input1("immunities", Arrays.asList("tyranid upgrades", "bio-titan"), stats -> {
            if (stats.get("tyranid upgrades").val01().isEmpty())
                return null;
            if (stats.get("bio-titan").val01().isEmpty())
                return null;
            return null;
        });
        //TODO some other ability
        //unlock bioforms: mawloc, trigun, trigun prime
    }


}
