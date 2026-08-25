package jmaurice.dnd.stats.builder.homebrew;

import java.util.Arrays;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class TyranidUpgrades extends BaseBuilder {

    public TyranidUpgrades(final Stats stats) { super(stats); }

    public void build() {
        agg("tyranid upgrades", root);
        upgrade1();
        upgrade2();
        upgrade3();
    }
    
    private void upgrade1() {
        to1("challenge rating",    "tyranid upgrades", new Value(1, "tyranid upgrade"));
        to1("aberration hit dice", "tyranid upgrades", new Value(2, "tyranid upgrade"));
        to1("natural armor bonus", "tyranid upgrades", new Value(1, "tyranid upgrade"));
        to1("strength",            "tyranid upgrades", new Value(1, "tyranid upgrade"));
        to1("dexterity",           "tyranid upgrades", new Value(1, "tyranid upgrade"));
        input("intelligence", Arrays.asList("tyranid upgrades", "synapse"), stats -> {
            if (stats.get("tyranid upgrades").val01().isEmpty())
                return null;
            if (stats.get("synapse").val01().isEmpty())
                return null;
            return null;
        });
        input("psionic manifester level", Arrays.asList("tyranid upgrades", "synapse"), stats -> {
            if (stats.get("tyranid upgrades").val01().isEmpty())
                return null;
            if (stats.get("synapse").val01().isEmpty())
                return null;
            return null;
        });
        to1("feats", "tyranid upgrades", new Value("iron will", "tyranid upgrades"));
        to1("special abilities long", "tyranid upgrades", new Value("""
                <b>Adrenal Glands (Ex)</b>: This creature benefits from a haste spell for 10 rounds per day. However, this haste effect does not grant a bonus attack in a full attack. This is an extraordinary effect. These rounds may be split up.""",
                "tyranid upgrades"));
        //unlock bioforms: gargoyle, carnifex
    }
    
    private void upgrade2() {
        to1("challenge rating",    "tyranid upgrades", new Value(1, "tyranid upgrade"));
        to1("aberration hit dice", "tyranid upgrades", new Value(2, "tyranid upgrade"));
        to1("natural armor bonus", "tyranid upgrades", new Value(1, "tyranid upgrade"));
        to1("strength",            "tyranid upgrades", new Value(1, "tyranid upgrade"));
        to1("dexterity",           "tyranid upgrades", new Value(1, "tyranid upgrade"));
        input("intelligence", Arrays.asList("tyranid upgrades", "synapse"), stats -> {
            if (stats.get("tyranid upgrades").val01().isEmpty())
                return null;
            if (stats.get("synapse").val01().isEmpty())
                return null;
            return null;
        });
        input("psionic manifester level", Arrays.asList("tyranid upgrades", "synapse"), stats -> {
            if (stats.get("tyranid upgrades").val01().isEmpty())
                return null;
            if (stats.get("synapse").val01().isEmpty())
                return null;
            return null;
        });
        to1("feats", "tyranid upgrades", new Value("lightning reflexes", "tyranid upgrades"));
        to1("defensive abilities", "tyranid upgrades", new Value("still mind", "tyranid upgrades"));
        //unlock bioforms: bio-titans
    }

    private void upgrade3() {
        to1("challenge rating",    "tyranid upgrades", new Value(1, "tyranid upgrade"));
        to1("aberration hit dice", "tyranid upgrades", new Value(2, "tyranid upgrade"));
        to1("natural armor bonus", "tyranid upgrades", new Value(1, "tyranid upgrade"));
        to1("strength",            "tyranid upgrades", new Value(1, "tyranid upgrade"));
        to1("dexterity",           "tyranid upgrades", new Value(1, "tyranid upgrade"));
        input("intelligence", Arrays.asList("tyranid upgrades", "synapse"), stats -> {
            if (stats.get("tyranid upgrades").val01().isEmpty())
                return null;
            if (stats.get("synapse").val01().isEmpty())
                return null;
            return new Value(1, "tyranid upgrade");
        });
        input("psionic manifester level", Arrays.asList("tyranid upgrades", "synapse"), stats -> {
            if (stats.get("tyranid upgrades").val01().isEmpty())
                return null;
            if (stats.get("synapse").val01().isEmpty())
                return null;
            return null;
        });
        to1("feats", "tyranid upgrades", new Value("great fortitude", "tyranid upgrades"));
        input("immunities", Arrays.asList("tyranid upgrades", "bio-titan"), stats -> {
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
