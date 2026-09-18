package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class ArmorClass extends BaseBuilder {

    public ArmorClass(final Stats stats) { super(stats); }

    public void build() {
        for (final String type : Arrays.asList("armor class", "touch armor class", "combat maneuvers defense")) {
            for (final boolean flatFooted : Arrays.asList(false, true)) {
                stat("default")
                .to1((flatFooted ? "flat-footed " : "") + type, new Value(10).source("default"))
                .agg(leaf, values -> sumAsInts(sort(values, value -> value.source), skipZero));
            }
        }
        size();
        baseAttack();
        str();
        dex();
        armorShieldNaturalArmor();
        otherBonuses();
    }
    
    private void size() {
        for (final String type : Arrays.asList("armor class", "touch armor class")) {
            for (final boolean flatFooted : Arrays.asList(false, true)) {
                stat("size modifier to attack")
                .to1((flatFooted ? "flat-footed " : "") + type, value -> value.source("size"));
            }
        }
        for (final boolean flatFooted : Arrays.asList(false, true)) {
            stat("size modifier to attack")
            .to1((flatFooted ? "flat-footed " : "") + "combat maneuvers defense", value -> value.mult(-1).source("size"));
        }
    }
    
    private void baseAttack() {
        stat("base attack bonus").to1("combat maneuvers defense",             value -> value.source("base attack bonus"));
        stat("base attack bonus").to1("flat-footed combat maneuvers defense", value -> value.source("base attack bonus"));
        stat("epic base attack bonus").to1("combat maneuvers defense",             value -> value.source("epic base attack bonus"));
        stat("epic base attack bonus").to1("flat-footed combat maneuvers defense", value -> value.source("epic base attack bonus"));
    }
    
    private void str() {
        stat("strength modifier").to1("combat maneuvers defense",             value -> value.source("str"));
        stat("strength modifier").to1("flat-footed combat maneuvers defense", value -> value.source("str"));
    }
    
    private void dex() {
        for (final String type : Arrays.asList("armor class", "touch armor class", "combat maneuvers defense")) {
            for (final boolean flatFooted : Arrays.asList(false, true)) {
                if (flatFooted) {
                    stat("dexterity penalty").to1("flat-footed " + type, value -> value.source("dex"));
                } else {
                    stat("dexterity modifier").to1(type, value -> value.source("dex"));
                }
            }
        }
    }
    
    private void armorShieldNaturalArmor() {
        for (final String type : Arrays.asList("armor", "shield", "natural armor")) {
            //TODO prevent stacking of multiple sources of armor, etc for shield and natural armor
            stat(type + " bonus")
            .agg(root, values -> sumAsInts(sort(values, value -> value.source), skipZero))
            .many(
                x -> x.to1("armor class", value -> value.type(type).source(type + " (" + value.source + ")")),
                x -> x.to1("flat-footed armor class", value -> value.type(type).source(type + " (" + value.source + ")"))
            );
        }
    }
    
    private void otherBonuses() {
        stat("armor class bonus")
        .aggN(root);
        for (final String type : Arrays.asList("armor class", "touch armor class", "combat maneuvers defense")) {
            for (final boolean flatFooted : Arrays.asList(false, true)) {
                stat("armor class bonus")
                .toN((flatFooted ? "flat-footed " : "") + type, 
                        values -> values
                        );
            }
        }
    }
    
}
