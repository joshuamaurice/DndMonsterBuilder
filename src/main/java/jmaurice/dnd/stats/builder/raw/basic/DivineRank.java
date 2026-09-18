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
        stat("divine rank").agg(root);
        stat("divine rank").to1("divine rank 0", value -> value.getIntValue() == 0 ? value : null);
        stat("divine rank").to1("divine rank 1+", value -> value.getIntValue() >= 1 ? value : null);
        
        stat("divine rank").to1("special abilities short", value -> new Value("<a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm\">divine rank " + value.getIntValue() + "</a>"));
        stat("divine rank 1+").to1("special abilities short", new Value("deity epic outsider hit dice (usually 20)"));
        
        abilityScores();
        directDivineRankBonuses();
        hitPoints();
        speed();
        immunities();
        energyResistances();
        damageReduction();
        otherDefensiveAbilities();
        otherSpecialAbilities();
    }
    
    private void abilityScores() {
        stat("divine rank").to1("special abilities long", new Value("""
                <b>Divine Ability Scores</b><br/>
                A deity gains an untyped +4 bonus to strength, dexterity, constitution, intelligence, and charisma.
                In addition, the deity adds their divine rank as an additional bonus to one ability score (which depends on the deity's theme).
                """
                ));
        for (final String abilityScore : AbilityScores.abilityScoreStatNames) {
            stat("divine rank").to1(abilityScore, new Value(4).source("deity"));
        }
    }
    
    private void directDivineRankBonuses() {
        stat("divine rank").to1("initiative", value -> value.type("divine").source("divine rank as divine bonus"));
        stat("divine rank").to1("global attack modifiers", value -> value.type("divine").source("divine rank as divine bonus"));
        stat("divine rank").to1("armor class bonus", value -> value.type("divine").source("divine rank as divine bonus to armor class"));
        stat("divine rank").to1("saves bonus", value -> value.type("divine").source("divine rank as divine bonus"));
        stat("divine rank").to1("skills bonus", value -> value.type("divine").source("divine rank as divine bonus"));
        deflectionBonus();
        naturalArmorBonus();
        incorporealDodgeBonus();
        //TODO divineRank to ability checks, caster level checks, and turning checks
    }
    
    private void deflectionBonus() {
        stat("divine rank").to1("special abilities short", new Value("<a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm#armorClass\">divine deflection</a>"));
        input1("armor class bonus", Arrays.asList("divine rank", "charisma bonus"), stats -> {
            if (stats.get("divine rank").getIntValue() == null)
                return null;
            return stats.get("charisma bonus").val1().type("deflection").source("deity charisma to deflection");
        });
    }
    
    private void naturalArmorBonus() {
        inputN("divine natural armor", Arrays.asList("incorporeal", "divine rank"), stats -> {
            if (stats.get("incorporeal").getValues().size() > 0)
                return null;
            final Integer divineRank = stats.get("divine rank").getIntValue();
            if (divineRank == null || divineRank < 1)
                return null;
            return Arrays.asList(
                new Value(13).source("deity natural armor bonus"), 
                new Value(divineRank).source("divine rank as divine bonus to natural armor")); 
        })
        .aggN()
        .many(
            x -> x.toN("natural armor bonus"),
            x -> x.toN("special abilities short", new Value("<a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm#armorClass\">divine natural armor</a>"))
        );
    }

    private void incorporealDodgeBonus() {
        inputN("divine dodge", Arrays.asList("incorporeal", "divine rank"), stats -> {
            if (stats.get("incorporeal").getValues().size() == 0)
                return null;
            final Integer divineRank = stats.get("divine rank").getIntValue();
            if (divineRank == null || divineRank < 1)
                return null;
            return Arrays.asList(
                new Value(7).type("dodge").source("incorporeal deity dodge bonus"), 
                new Value(divineRank).type("dodge").source("incorporeal deity divine rank as additional dodge bonus"));
        })
        .aggN()
        .many(
            x -> x.toN("armor class bonus"),
            x -> x.toN("special abilities long", new Value("""
                    <b>Divine Dodge</b><br/>
                    An incorporeal deity gains a dodge bonus equal to 7 + their divine rank (instead of the <a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm#armorClass\">standard natural armor bonus of a deity</a>).
                    """
                    ))
        );
    }
    
    private void hitPoints() {
        input1("hit points", Arrays.asList("divine rank", "hit dice"), stats -> {
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
                    .source("divine hit points");
        });
    }

    private void immunities() {
        stat("divine rank 0").to1("immunities", new Value("divine immunities"));
        stat("divine rank 0").to1("special abilities long", new Value("""
                <b>Divine Immunities</b><br/> 
                The demigod is immune to transmutations that involuntarily alters their form
                including polymorph, petrification, and <a href="https://www.d20pfsrd.com/magic/all-spells/s/slow/">slow</a>.
                (Special: disintegration still deals damage and can kill a demigod, but it cannot turn the deity's body to dust.)
                Demigods gain one free-action saving throw per round per ongoing mind-affecting effect on the demigod to end the effect.
                A demigods divine immunities apply against normal effects, mythic effects, and the effects from other demigods,
                but not effects from other deities with higher divine rank.
                """
                ));
        stat("divine rank 1+").to1("immunities", new Value("divine immunities"));
        stat("divine rank 1+").to1("special abilities long", new Value("""
                <b>Divine Immunities</b><br/> 
                The deity is immune to 
                ability damage, ability drain, death effects, energy drain,
                paralysis, sleep, disease, poison, stunning, 
                and transmutations that involuntarily alters their form
                including polymorph, petrification, and <a href="https://www.d20pfsrd.com/magic/all-spells/s/slow/">slow</a>.
                (Special: disintegration still deals damage and can kill a deity, but it cannot turn the deity's body to dust.)
                The deity gains one free-action saving throw per round per ongoing mind-affecting effect on the deity to end the effect.
                A deity's divine immunities apply against normal effects, mythic effects, 
                and the effects from other deities of equal or lesser divine rank,
                but not effects from other deities with higher divine rank.
                """
                ));
    }
    
    private void energyResistances() {
        stat("divine rank 1+").toN("special abilities long", new Value("""
                <b>Divine Energy Resistances</b><br/>
                A good-aligned deity usually has resistance 10 (fire, sonic) and resistance 30 (acid, cold, electricity).
                An evil-aligned deity usually has resistance 10 (electricity, sonic) and resistance 30 (acid, cold, fire).
                A deity that is neutral on the good-evil axis usually has one of the two options above for energy resistances.
                """).source("divine rank"));
    }
    
    private void damageReduction() {
        stat("divine rank").toN("damage reduction", values -> {
            final int divineRank = val1(values).getIntValue();
            if (divineRank == 0)
                return Arrays.asList(new Value("10/magic"), new Value("5/epic"));
            if (divineRank <= 5)
                return Arrays.asList(new Value("20/magic"), new Value("10/epic"));
            if (divineRank <= 10)
                return Arrays.asList(new Value("30/magic"), new Value("15/epic"));
            if (divineRank <= 15)
                return Arrays.asList(new Value("40/magic"), new Value("20/epic"));
            if (divineRank <= 20)
                return Arrays.asList(new Value("50/magic"), new Value("25/epic"));
            throw new RuntimeException();
        });
    }
    
    private void otherDefensiveAbilities() {
        stat("divine rank").to1("defensive abilities", new Value("<a href=\"https://www.d20pfsrd.com/ALTERNATIVE-RULE-SYSTEMS/PAIZO-RULES-SYSTEMS/MYTHIC/MYTHIC-HEROES/#hard_to_kill_ex\">mythic hard to kill</a>"));
        stat("divine rank").to1("defensive abilities", new Value("<a href=\"https://www.d20pfsrd.com/ALTERNATIVE-RULE-SYSTEMS/PAIZO-RULES-SYSTEMS/MYTHIC/MYTHIC-HEROES/#mythic_saving_throws_ex\">mythic saving throws</a>"));
        
        stat("divine rank").to1("defensive abilities", new Value("divine immortality"));
        stat("divine rank").to1("special abilities long", new Value("""
                <b>Divine Immortality</b><br/> 
                A deity stops aging at a certain age (which depends on the deity's theme). 
                Deities do not die of old age, and they are immune to magical aging effects.
                """
                ));
    }
    
    private void otherSpecialAbilities() {
        stat("divine rank").to1("special abilities short", new Value("<a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm#hitPoints\">divine hit points</a>"));
        
        stat("divine rank 1+").many(
            x -> x.to1("spell resistance", value -> new Value(value.getIntValue() + 32).source("deity")),
            x -> x.to1("special abilities short",      new Value("<a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm#automaticActions\">divine automatic actions (2 / round)</a>")),
            x -> x.to1("special abilities short",      new Value("<a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm#blockSensing\">divine block sensing</a>")),
            x -> x.to1("special abilities short",      new Value("<a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm#createMagicItems\">divine create magic items</a>")),
            x -> x.to1("auras",                        new Value("<a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm#divineAura\">divine aura (choose one)</a>")),
            x -> x.to1("special abilities short",      new Value("<a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm#divineCharacteristics\">alignment subtypes that match alignment</a>")),
            x -> x.to1("special abilities short",      new Value("<a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm#divineFamiliar\">divine familiar</a>")),
            x -> x.to1("special abilities short",      new Value("<a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm#godlyRealm\">divine godly realm</a>")),
            x -> x.to1("special abilities short",      new Value("<a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm#portfolio\">divine portfolio</a>")),
            x -> x.to1("special abilities short", v -> new Value("<a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm#salientDivineAbilities\">divine salient ability (choose " + v.getIntValue() + ")</a>")),
            x -> x.to1("special abilities short",      new Value("<a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm#spontaneousCasting\">divine spontaneous casting</a>"))
        );
        
        stat("divine rank 1+").to1("special abilities long", new Value("""
                <b>Divine Communication</b><br/>
                The deity can understand, speak, and read any language, including non-verbal languages.
                """
                ));
        
        stat("divine rank 1+").to1("special abilities long", new Value("""
                <b>Divine Grant Spells</b><br/>
                The deity automatically grants spells and domain powers to mortal divine spellcasters who pray to it.
                Most deities can grant spells from the cleric spell list, the ranger spell list, and from their deity domains.
                Deities with levels in the druid class can grant spells from the druid spell list, 
                and deities with paladin levels can grant spells from the paladin spell list. 
                A deity can withhold spells from any particular mortal as a free action; 
                once a spell has been granted, it remains in the mortal’s mind until expended.
                """
                ));
        
        stat("divine rank 1+").to1("special abilities long", new Value("""
                <b>Divine Domains</b><br/>
                A deity has three or more domains with zero or more subdomains of those domains which match the deity's theme and portfolio.
                <br/>
                    Three times per day per spell level, a deity can use a spell of one of their divine domains or subdomains as a spell-like ability.
                For this purpose, the caster level is 20, and the DC is 10 + the spell level + the deity's Charisma bonus + the deity's divine rank.
                For this purpose, spells from a subdomain are "in addition to" and not "in place of" the spells of the parent domain.
                <br/> 
                    The deity gains the divine powers of their divine domains.
                For this purpose, their effective cleric level is 20, and the deity adds their divine rank as a bonus to the DCs, if any, of their domain powers.
                The number of daily uses, if any, of the domain powers is as normal for a 20th level cleric.
                For this purpose, domain powers from a subdomain are "in addition to" and not "in place of" the domain powers of the parent domain.
                """
                ));
        
        stat("divine rank 1+").to1("special abilities long", new Value("""
                <b>Divine No-Botches</b><br/>
                A deity does not automatically fail an attack roll or saving throw on a natural 1.
                """
                ));
        
        stat("divine rank 1+").to1("senses", new Value("divine portfolio sense"));
        stat("divine rank 1+").to1("special abilities long", new Value("""
                <b>Divine Portfolio Sense</b><br/>
                A deity automatically become aware about all of the following:
                <ul>
                <li>The location of all holy sites dedicated to the deity within 1 mile.</li>
                <li>The location of all statues and likenesses of the deity within 1 mile.</li>
                <li>The location where someone speaks the deity's name.</li>
                <li>The location of any event that involves one thousand or more people that also involves the deity's divine portfolio.</li>
                </ul>
                The ability is limited to the present. 
                Lesser deities (divine rank 6 - 10) automatically sense any event that involves their portfolios and affects five hundred or more people. 
                Intermediate deities (divine rank 11 - 15) automatically sense any event that involves their portfolios, regardless of the number of people involved. In addition, their senses extend one week into the past for every divine rank they have. 
                Greater deities (divine rank 16 - 20) automatically sense any event that involves their portfolios, regardless of the number of people involved. In addition, their senses extend one week into the past and one week into the future for every divine rank they have.
                <br/>
                    When a deity senses an event, it merely knows that the event is occurring (or occurred) and where it is (or was). 
                The deity receives no additional information about the event. 
                Once a deity notices an event, they can use their divine remote sensing power to view a location at the event (at the current time only).
                """
                ));
        
        stat("divine rank 1+").to1("special abilities long", value -> new Value("""
                <b>Divine Remote Communication</b><br/>
                A deity can carry on a two-way conversation with any creature that they can see, including creatures that they can see via magic spells such as scrying, their divine senses ability, and their divine remote sensing ability. Their divine remote communication ability can cross any plane and any barrier. It is a standard action to initiate a remote communication, and a free action to end it. 
                They can have at most __NUM__ active divine remote communication effects active at any one time. Concentration is not required to maintain remote communication.
                <br/>
                    This communication is telepathic.
                <br/> 
                    Alternatively, the deity may choose to create an intangible visual and auditory image near the specified creature to communicate. Typically, this image creates audible human speech which any nearby creature can hear, and they can see and hear the remote location as though with a clairvoyance plus clairaudience spell.
                <br/>
                    Alternatively, if the deity activates divine remote communication on a statue or likeness of the deity which they sensed via divine remote sensing, then they may choose to temporarily manipulate the statue or likeness to speak for them.
                """
                .replace("__NUM__", "" + (2 + (value.getIntValue() >= 6 ? 3 : 0) + (value.getIntValue() >= 11 ? 5 : 0) + (value.getIntValue() >= 16 ? 10 : 0)))
                ));
        
        stat("divine rank 1+").to1("senses", new Value("divine remote sensing"));
        stat("divine rank 1+").to1("special abilities long", value -> new Value("""
                <b>Divine Remote Sensing</b><br/>
                The deity can view faraway locations at-will; standard action to begin; free action to end. 
                Treat this as a scrying spell with the following differences. 
                The deity can have at most __NUM__ active divine remote sensing effects active at any one time. 
                They can actively view multiple remote locations without interfering with their ability to perceive and interact with their current surroundings.
                <br/>
                    The deity can only target the following areas:
                <ul>
                <li>One of their worshippers, unambiguously identified by name or description.</li>
                <li>One of their holy sites anywhere in the world, unambiguously identified by name or description. The deity can move the scrying effect to any place inside the holy site that they can unambiguously specify with a separate standard action.</li>
                <li>Any location that the deity sensed with divine portfolio sense; up to one hour later. The deity can move the scrying effect to any place inside the relevant area (DM fiat) with a separate standard action.</li>
                </ul>
                    The scrying effect:
                <ul>
                <li>Crosses all planar barriers and all magical boundaries except for the divine shield salient divine ability, and except for the block sensing divine ability of a deity of equal or higher divine rank.</li>
                <li>Is not fooled by any mortal magic such as a misdirection spell or nondetection spell.</li> 
                <li>Does not create a magical sensor that can be detected or dispelled.</li>
                </ul>
                """
                .replace("__NUM__", "" + (2 + (value.getIntValue() >= 6 ? 3 : 0) + (value.getIntValue() >= 11 ? 5 : 0) + (value.getIntValue() >= 16 ? 10 : 0)))
                ));
        
        stat("divine rank 1+").to1("senses", new Value("divine senses"));
        stat("divine rank 1+").to1("special abilities long", new Value("""
                <b>Divine Senses</b><br/>
                The deity ignores distance penalties on their Perception checks out to a range of 1 mile per divine rank.
                (This ability does not allow the deity to see around corners or through walls or other opaque objects.)
                """
                ));
        
        stat("divine rank").to1("special abilities long", value -> {
            if (value.getIntValue() == 0)
                return null;
            if (value.getIntValue() <= 5) {
                return new Value("""
                        <b>Divine Travel (SLA)</b><br/>
                        Greater teleport, as the spell, at will.
                        Caster level 20.
                        """);
            }
            if (value.getIntValue() <= 20) {
                return new Value("""
                        <b>Divine Travel (SLA)</b><br/>
                        Greater teleport, as the spell, at will. 
                        Plane shfit, as the spell, at will. 
                        Caster level 20.
                        """);
            }
            throw new RuntimeException();
        });
        
        stat("divine rank").to1("special abilities long", value -> {
            if (value.getIntValue() <= 5)
                return null;
            if (value.getIntValue() <= 10) {
                return new Value("""
                        <b>Divine Checks</b><br/>
                        The deity can take-10 on skill checks, ability checks, caster level checks, concentration checks, and turning checks, even while under stress.
                        """);
            }
            if (value.getIntValue() <= 20) {
                return new Value("""
                        <b>Divine Checks</b><br/>
                        The deity always gets a result of 20 on skill checks, ability checks, caster level checks, concentration checks, and turning checks.
                        """);
            }
            throw new RuntimeException();
        });
        
        stat("divine rank").to1("special abilities long", value -> {
            if (value.getIntValue() <= 15)
                return null;
            if (value.getIntValue() <= 20) {
                return new Value("""
                        <b>Divine Always-Roll-Twenties</b><br/>
                        The deity always gets a result of 20 on d20 rolls. The deity still has to roll to determine if they get a critical threat."
                        """);
            }
            throw new RuntimeException();
        });
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
    
    private void speed() {
        stat("divine rank").many(
            x -> x.to1("special abilities short", new Value("<a href=\"https://www.d20srd.org/srd/divine/divineRanksAndPowers.htm#speed\">divine speed</a>")),
            x -> x.to1("divine speed", new Value(true))
        );
        for (final String speedName : Speeds.movementModeNames) {
            final String speedStatName = "ft " + speedName + " speed";
            stats.preAgg(
                speedStatName,
                Arrays.asList("divine speed", "shape", "size"), 
                (writableStats, readOnlyStats) -> {
                    if (readOnlyStats.get("divine speed").getValues().size() == 0)
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
                    speedStatValues.add(new Value(divineBaseSpeed).source("deity"));
                    speedStat.setValues(speedStatValues);
                }
            );
        };
    }

}
