package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;
import java.util.stream.Stream;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class DivineRank extends BaseBuilder {

    public DivineRank(final Stats stats) { super(stats); }

    public void build() {
        agg("divine rank", root);
        divineRank0();
        divineRank1();
    }
    
    private void divineRank0() {
        to1("strength",     "divine rank", new Value(4));
        to1("dexterity",    "divine rank", new Value(4));
        to1("constitution", "divine rank", new Value(4));
        to1("intelligence", "divine rank", new Value(4));
        to1("wisdom",       "divine rank", new Value(4));
        to1("charisma",     "divine rank", new Value(4));
        
        to1("defensive abilities", "divine rank", new Value("divine resistance to mind-affecting effects"));
        to1("defensive abilities", "divine rank", new Value("mythic saving throws"));
        to1("immunities", "divine rank", new Value("harmful transmutations"));
        to1("resistances", "divine rank", value -> new Value("fire " + (5 + value.getIntValue())));
        to1("special abilities short", "divine rank", value -> new Value("divine rank " + value.getIntValue()));
        to1("special abilities short", "divine rank", new Value("mythic hard to kill"));
        to1("special abilities short", "divine rank", new Value("divine immortality"));
        to1("special abilities short", "divine rank", new Value("divine speed"));
        
        to1("special abilities short", "divine rank", new Value("divine hit points"));
        input("hit points", Arrays.asList("divine rank", "hit dice"), stats -> {
            if (stats.get("divine rank").getIntValue() == null)
                return null;
            final Value hitDice = stats.get("hit dice").val01().orElse(null);
            if (hitDice == null)
                return null;
            return sumAsDoubles(
                        Stream.of(hitDice)
                        .flatMap(x -> x.split("\\+").stream())
                        .map(x -> x.regexExtract("^([0-9]+)d[0-9]+$").mult((x.regexExtract("^[0-9]+d([0-9]+)$").getIntValue() - 1.0) * 0.5))
                        .toList()
                    )
                    .floor()
                    .asInt()
                    .source("divive hit points");
        });
        
        to1("special abilities short", "divine rank", new Value("divine reflection"));
        input("deflection bonus to armor class", Arrays.asList("divine rank", "charisma bonus"), stats -> {
            if (stats.get("divine rank").getIntValue() == null)
                return null;
            return new Value(stats.get("charisma bonus").getIntValue());
        });
        
//        xtoy    divineRank  defensiveAbilities  IF(divineRank=0,"resist 5 (acid, cold, electricity)",)
//        xtoy    divineRank  defensiveAbilities  IF(divineRank=1,"resist 30 (acid, cold, electricity)",)

//        xtoy    divineRank  defensiveAbilities  IF(divineRank=0,"DR 5 / magic",)
//        xtoy    divineRank  defensiveAbilities  IF((divineRank>=1)*(divineRank<=5),"DR 20 / magic and DR 10 / epic",)
        
    }
    
    private void divineRank1() {
        to1("divine rank 1+", "divine rank", value -> value.getIntValue() >= 1 ? value : null);
        
        //usually 20 epic outsider hit dice in addition to existing hit dice
        
        to1("defensive abilities", "divine rank 1+", value -> new Value("SR " + (value.getIntValue() + 32)));
        to1("immunities", "divine rank 1+", new Value("paralysis"));
        to1("immunities", "divine rank 1+", new Value("sleep"));
        to1("immunities", "divine rank 1+", new Value("disease"));
        to1("immunities", "divine rank 1+", new Value("poison"));
        to1("immunities", "divine rank 1+", new Value("stunning"));
        to1("immunities", "divine rank 1+", new Value("ability damage"));
        to1("immunities", "divine rank 1+", new Value("energy drain"));
        to1("immunities", "divine rank 1+", new Value("death effects"));
        to1("immunities", "divine rank 1+", new Value("ability drain"));
        
        to1("special abilities short", "divine rank 1+", value -> new Value("divine aura (choose " + value.getIntValue() + ")"));
        to1("special abilities short", "divine rank 1+", value -> new Value("divine salient divine ability (choose " + value.getIntValue() + ")"));
        
        to1("special abilities short", "divine rank 1+", new Value("divine automatic actions (2 / round)"));
        to1("special abilities short", "divine rank 1+", new Value("divine block sensing"));
        to1("special abilities short", "divine rank 1+", new Value("divine communication"));
        to1("special abilities short", "divine rank 1+", new Value("divine create magic items"));
        to1("special abilities short", "divine rank 1+", new Value("divine domains (choose 5)"));
        to1("special abilities short", "divine rank 1+", new Value("divine domain powers"));
        to1("special abilities short", "divine rank 1+", new Value("divine domains SLAs (3 / day / splvl)"));
        to1("special abilities short", "divine rank 1+", new Value("divine familiar"));
        to1("special abilities short", "divine rank 1+", new Value("divine godly realm"));
        to1("special abilities short", "divine rank 1+", new Value("divine no botches"));
        to1("special abilities short", "divine rank 1+", new Value("divine portfolio"));
        to1("special abilities short", "divine rank 1+", new Value("divine portfolio sense"));
        to1("special abilities short", "divine rank 1+", new Value("divine remote communication"));
        to1("special abilities short", "divine rank 1+", new Value("divine remote sensing"));
        to1("special abilities short", "divine rank 1+", new Value("divine senses"));
        to1("special abilities short", "divine rank 1+", new Value("divine spontaneous casting"));
        to1("special abilities short", "divine rank 1+", new Value("divine travel"));

        to1("initiative", "divine rank 1+");
        to1("global attack modifiers", "divine rank 1+");
        to1("natural armor bonus", "divine rank 1+");
        Skills.allSkills.forEach(skill -> to1(skill, "divine rank 1+"));
        
        to1("armor class",                          "divine rank 1+");
        to1("touch armor class",                    "divine rank 1+");
        to1("combat maneuvers defense",             "divine rank 1+");
        to1("flat-footed armor class",              "divine rank 1+");
        to1("flat-footed touch armor class",        "divine rank 1+");
        to1("flat-footed combat maneuvers defense", "divine rank 1+");
        
        to1("fortitude", "divine rank 1+");
        to1("reflex", "divine rank 1+");
        to1("will", "divine rank 1+");
        
        //TODO divineRank to ability checks, caster level checks, and turning checks
        
        to1("special abilities short", "divine rank 1+", new Value("add divine rank to one ability score of your choice"));
        
    }

}
