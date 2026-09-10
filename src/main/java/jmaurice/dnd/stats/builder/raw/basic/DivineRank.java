package jmaurice.dnd.stats.builder.raw.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;
import jmaurice.dnd.stats.impl.ValuedStat;

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
        
        divineSpeed();
        
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
    }
    
    private void divineRank1() {
        to1("divine rank 1+", "divine rank", value -> value.getIntValue() >= 1 ? value : null);
        
        //usually 20 epic outsider hit dice in addition to existing hit dice
        
        to1("spell resistance", "divine rank 1+", value -> new Value(value.getIntValue() + 32, "deity"));
        to1("immunities", "divine rank 1+", new Value("divine immunities (" + joinS(sortS(Arrays.asList(
                "paralysis", "sleep", "disease", "poison", "stunning", "ability damage", "energy drain", "death effects", "ability drain"
                )), ", ").get() + ")", "deity"));
        
        to1("special abilities short", "divine rank 1+", value -> new Value("divine aura (choose " + value.getIntValue() + ")", "deity"));
        to1("special abilities short", "divine rank 1+", value -> new Value("divine salient divine ability (choose " + value.getIntValue() + ")", "deity"));
        
        to1("special abilities short", "divine rank 1+", new Value("divine automatic actions (2 / round)", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine block sensing", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine communication", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine create magic items", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine domains (choose 5)", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine domain powers", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine domains SLAs (3 / day / splvl)", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine familiar", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine godly realm", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine no botches", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine portfolio", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine portfolio sense", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine remote communication", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine remote sensing", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine senses", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine spontaneous casting", "deity"));
        to1("special abilities short", "divine rank 1+", new Value("divine travel", "deity"));

        to1("initiative", "divine rank 1+", value -> value.source("divine rank"));
        to1("global attack modifiers", "divine rank 1+", value -> value.source("divine rank"));
        to1("natural armor bonus", "divine rank 1+", value -> value.source("divine rank"));
        Skills.allSkills.forEach(skill -> to1(skill, "divine rank 1+", value -> value.source("divine rank")));
        
        to1("armor class",                          "divine rank 1+", value -> value.source("divine rank"));
        to1("touch armor class",                    "divine rank 1+", value -> value.source("divine rank"));
        to1("combat maneuvers defense",             "divine rank 1+", value -> value.source("divine rank"));
        to1("flat-footed armor class",              "divine rank 1+", value -> value.source("divine rank"));
        to1("flat-footed touch armor class",        "divine rank 1+", value -> value.source("divine rank"));
        to1("flat-footed combat maneuvers defense", "divine rank 1+", value -> value.source("divine rank"));
        
        to1("fortitude", "divine rank 1+", value -> value.source("divine rank"));
        to1("reflex", "divine rank 1+", value -> value.source("divine rank"));
        to1("will", "divine rank 1+", value -> value.source("divine rank"));
        
        //TODO divineRank to ability checks, caster level checks, and turning checks
        
        to1("special abilities short", "divine rank 1+", new Value("add divine rank to one ability score of your choice"));
        
    }
    
    private static int getDivineSpeed1(final String size) {
        return switch (size) {
            case "fine"       -> 20;
            case "diminutive" -> 30;
            case "tiny"       -> 40;
            case "small"      -> 50;
            case "medium"     -> 60;
            case "large"      -> 80;
            case "huge"       -> 100;
            case "gargantuan" -> 120;
            case "colossal"   -> 140;
            case "colossal-plus" -> 160;
            default -> throw new RuntimeException("unsupported size: " + size);
        };
    }
    
    private static int getDivineSpeed2(final String size) {
        return switch (size) {
            case "fine"       -> 60;
            case "diminutive" -> 70;
            case "tiny"       -> 80;
            case "small"      -> 90;
            case "medium"     -> 100;
            case "large"      -> 120;
            case "huge"       -> 140;
            case "gargantuan" -> 160;
            case "colossal"   -> 180;
            case "colossal-plus" -> 200;
            default -> throw new RuntimeException("unsupported size: " + size);
        };
    }
    
    private void divineSpeed() {
        to1("special abilities short", "divine rank", new Value("divine speed"));
        to1("divine speed", "divine rank", new Value(true));
        for (final String speedName : Speeds.movementModeNames) {
            final String speedStatName = "ft " + speedName + " speed";
            stats.preAgg(
                speedStatName,
                Arrays.asList("divine speed", "shape", "size"), 
                (writableStats, readOnlyStats) -> {
                    if ( ! readOnlyStats.get("divine speed").getBooleanValue(false))
                        return;
                    final ValuedStat speedStat = writableStats.get(speedStatName);
                    if (speedStat.getValues().isEmpty())
                        return;
                    final String size = readOnlyStats.get("size").getStringValue();
                    final String shape = readOnlyStats.get("shape").val01().map(x -> x.getStringValue()).orElse("biped");
                    final boolean biped;
                    if (Arrays.asList("biped").contains(shape)) {
                        biped = true;
                    } else if (Arrays.asList("quadruped", "snake").contains(shape)) {
                        biped = false;
                    } else {
                        throw new RuntimeException("unsupported shape: " + shape);
                    }
                    final List<String> baseSpeedBonusTypes = Arrays.asList(null, "race", "racial", "input");
                    final int preDivineBaseSpeed = sumAsInts(speedStat.getValues().stream()
                            .filter(v -> baseSpeedBonusTypes.contains(v.source))
                            .toList()).getIntValue();
                    final int divineBaseSpeed = switch (speedName) {
                        case "land" -> biped ? getDivineSpeed1(size) : getDivineSpeed2(size);
                        case "fly" -> 2 * getDivineSpeed2(size);
                        case "swim" -> getDivineSpeed1(size);
                        case "burrow" -> getDivineSpeed1(size);
                        case "climb" -> getDivineSpeed1(size) / 2;
                        default -> throw new RuntimeException();
                    };
                    if (preDivineBaseSpeed > divineBaseSpeed)
                        return;
                    List<Value> speedStatValues = speedStat.getValues();
                    speedStatValues = speedStatValues.stream()
                            .filter(v -> ! baseSpeedBonusTypes.contains(v.source))
                            .collect(Collectors.toCollection(ArrayList::new));
                    speedStatValues.add(new Value(divineBaseSpeed, "deity"));
                    speedStat.setValues(speedStatValues);
                }
            );
        };
    }

}
