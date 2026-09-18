package jmaurice.dnd.stats.builder.raw.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.ReadOnlyValuedStat;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;
import jmaurice.dnd.stats.impl.ValuedStat;

public class AttackRoutine extends BaseBuilder {

    public AttackRoutine(final Stats stats) { super(stats); }

    public void build() {
        stat("base attack bonus").agg(values -> sumAsDoubles(values).floor()); //sumAsDoubles to support partial base attack bonus multiclassing
        stat("epic base attack bonus").agg(values -> sumAsDoubles(values).floor()); //sumAsDoubles to support partial base attack bonus multiclassing
        stat("default").to1("base attack bonus", new Value(0));
        combatManeuversBonus();
        weaponAttackRoutines();
    }
    
    private void combatManeuversBonus() {
        stat("combat maneuvers bonus").agg(values -> withSign(sumAsInts(values)));
        stat("default"                ).to1("combat maneuvers bonus", new Value(0));
        stat("base attack bonus"      ).to1("combat maneuvers bonus", value -> value.source("base attack bonus"));
        stat("epic base attack bonus" ).to1("combat maneuvers bonus", value -> value.source("epic base attack bonus"));
        stat("size modifier to attack").to1("combat maneuvers bonus", value -> value.mult(-1).source("size"));
        stat("strength modifier"      ).to1("combat maneuvers bonus", value -> value.source("str"));
    }
    
    private void weaponAttackRoutines() {
        final List<String> weaponIds = IntStream.range(1, 6+1).mapToObj(x -> x + "").toList();
    
        stat("global attack modifiers").aggN(rootleaf);
        stat("global melee attack modifiers").aggN(rootleaf);
        stat("global range attack modifiers").aggN(rootleaf);
        
        stat("weapon properties").aggN();
        stat("attack routine").aggN(rootleaf);
        stat("weapon names").aggN(rootleaf);
        stat("using unarmed strikes").agg(root);
        stat("using manufactured weapons").agg(root);
        for (final String weaponId : weaponIds) {
            stat("weapon " + weaponId + " attack routine").agg(root);
            stat("weapon " + weaponId + " properties").aggN(root);
            stat("weapon " + weaponId + " attack modifiers").aggN(rootleaf); //for debugging
            stat("weapon " + weaponId + " damage modifiers").aggN(rootleaf); //for debugging
        }
        
        final List<String> parseWeaponPropertiesPostStats = new ArrayList<>();
        parseWeaponPropertiesPostStats.add("weapon names");
        parseWeaponPropertiesPostStats.add("using unarmed strikes");
        parseWeaponPropertiesPostStats.add("using manufactured weapons");
        weaponIds.forEach(weaponId -> parseWeaponPropertiesPostStats.add("weapon " + weaponId + " properties"));
        final List<String> parseWeaponPropertiesOtherInputs = new ArrayList<>();
        parseWeaponPropertiesOtherInputs.add("weapon properties");
        stat("parse weapon properties").agg(leaf);
        stats.postAggX("parse weapon properties", parseWeaponPropertiesPostStats, parseWeaponPropertiesOtherInputs, (stats, readOnlyStats) -> {
            final List<Value> allWeaponProperties = readOnlyStats.get("weapon properties").getValues();
            if (allWeaponProperties.isEmpty())
                return;
            final Map<String, Map<String, List<String>>> weapons = new LinkedHashMap<>();
            for (final Value allWeaponPropertiesValue : allWeaponProperties) {
                final List<String> namesInValue = new ArrayList<>();
                final Map<String, List<String>> propsInValue = new LinkedHashMap<>();
                for (final String prop : allWeaponPropertiesValue.getStringValue().split(",", -1)) {
                    final List<String> propSplit = Arrays.asList(prop.split("=", -1)).stream().map(x -> x.trim()).toList();
                    if (propSplit.size() > 2)
                        throw new RuntimeException("illegal weapon property >>" + prop + "<< in value: " + allWeaponPropertiesValue.getStringValue());
                    final String propName = propSplit.get(0);
                    final String propValue = propSplit.size() == 1 ? null : propSplit.get(1);
                    if (propName.equals("name")) {
                        if (propValue == null || propValue.isBlank())
                            throw new RuntimeException("a weapon property with key \"name\" is missing value: " + allWeaponPropertiesValue.getStringValue());
                        namesInValue.add(propValue);
                    } else {
                        propsInValue.computeIfAbsent(propName, k -> new ArrayList<>()).add(propValue == null ? "true" : propValue);
                    }
                }
                if (namesInValue.size() == 0)
                    throw new RuntimeException("one or more weapon properties value is missing a name: " + allWeaponPropertiesValue.getStringValue());
                if (namesInValue.size() >= 2)
                    throw new RuntimeException("one or more weapon properties value has two or more names: " + allWeaponPropertiesValue.getStringValue());
                final String nameInValue = namesInValue.get(0);
                final Map<String, List<String>> existingPropsForName = weapons.computeIfAbsent(nameInValue, k -> new LinkedHashMap<>());
                propsInValue.forEach((k,v) -> existingPropsForName.computeIfAbsent(k, k2 -> new ArrayList<>()).addAll(v));
            }
            if (weapons.size() > weaponIds.size())
                throw new RuntimeException("too many weapons: " + weapons.keySet());
    
            final ValuedStat weaponNamesStat = stats.get("weapon names");
            if (weaponNamesStat.getValues().size() > 0)
                throw new RuntimeException("weapon names stat should not be populated");
            weaponNamesStat.setValues(weapons.keySet().stream().map(x -> new Value(x)).toList());
    
            final ValuedStat usingUnarmedStrikesStat = stats.get("using unarmed strikes");
            if (usingUnarmedStrikesStat.getValues().size() > 0)
                throw new RuntimeException("using unarmed strikes stat should not be populated");
            if (weapons.keySet().contains("unarmed"))
                usingUnarmedStrikesStat.setValues(Collections.singletonList(new Value(true)));
    
            final ValuedStat usingManufacturedWeaponsStat = stats.get("using manufactured weapons");
            if (usingManufacturedWeaponsStat.getValues().size() > 0)
                throw new RuntimeException("using manufactured weapons stat should not be populated");
            boolean usingManufacturedWeapons = false;
            for (final Map.Entry<String, Map<String, List<String>>> weapon : weapons.entrySet()) {
                final boolean unarmed = weapon.getKey().equals("unarmed");
                final boolean naturalOverride = weapon.getValue().get("natural") != null;
                final boolean secondaryNaturalOverride = weapon.getValue().get("secondary two-handed weapon") != null;
                if ( ! unarmed && ! naturalOverride && ! secondaryNaturalOverride)
                    usingManufacturedWeapons = true;
            }
            if (usingManufacturedWeapons)
                usingUnarmedStrikesStat.setValues(Collections.singletonList(new Value(true)));
    
            parseWeaponPropertiesPostStats.add("");
            parseWeaponPropertiesPostStats.add("");
            
            int weaponId = 0;
            for (final Map.Entry<String, Map<String, List<String>>> weapon : weapons.entrySet()) {
                ++weaponId;
                final ValuedStat weaponProperties = stats.get("weapon " + weaponId + " properties");
                if (weaponProperties.getValues().size() > 0)
                    throw new RuntimeException("weapon " + weaponId + " properties stat should not be populated");
                final List<Value> weaponPropertiesValues = new ArrayList<>();
                weaponPropertiesValues.add(new Value("name=" + weapon.getKey()));
                for (final Map.Entry<String, List<String>> prop : weapon.getValue().entrySet()) {
                    final String propName = prop.getKey();
                    for (final String propValue : prop.getValue()) {
                        weaponPropertiesValues.add(new Value(propName + "=" + propValue));
                    }
                }
                weaponProperties.setValues(weaponPropertiesValues);
            }
        });
        
        stat("finesse dex to damage").agg(root);
        stat("multiattack").agg(root);
        stat("weapon finesse").agg(root);
        stat("high melee attack bonus").agg(rootleaf, values -> maxAsInts(values));
        stat("high range attack bonus").agg(rootleaf, values -> maxAsInts(values));
        stat("average melee damage").agg(rootleaf, values -> sumAsDoubles(values));
        stat("average range damage").agg(rootleaf, values -> sumAsDoubles(values));

        weaponIds.forEach(weaponId -> {
            final List<String> postStatNames = new ArrayList<>();
            postStatNames.add("weapon " + weaponId + " attack modifiers");
            postStatNames.add("weapon " + weaponId + " damage modifiers");
            postStatNames.add("high melee attack bonus");
            postStatNames.add("average melee damage");
            postStatNames.add("high range attack bonus");
            postStatNames.add("average range damage");
            final List<String> otherInputStatNames = new ArrayList<>();
            otherInputStatNames.add("weapon names");
            otherInputStatNames.add("weapon " + weaponId + " properties");
            otherInputStatNames.add("strength modifier");
            otherInputStatNames.add("dexterity modifier");
            otherInputStatNames.add("finesse dex to damage");
            otherInputStatNames.add("base attack bonus");
            otherInputStatNames.add("epic base attack bonus");
            otherInputStatNames.add("size modifier to attack");
            otherInputStatNames.add("size");
            otherInputStatNames.add("incorporeal");
            otherInputStatNames.add("weapon finesse");
            otherInputStatNames.add("multiattack");
            otherInputStatNames.add("using unarmed strikes");
            otherInputStatNames.add("using manufactured weapons");
            otherInputStatNames.add("global attack modifiers");
            otherInputStatNames.add("global melee attack modifiers");
            otherInputStatNames.add("global range attack modifiers");
            stats.postAggX("weapon " + weaponId + " attack routine", postStatNames, otherInputStatNames, (stats, readOnlyStats) -> {
                final List<Value> propsList = readOnlyStats.get("weapon " + weaponId + " properties").getValues();
                if (propsList.isEmpty())
                    return;
                final List<String> weaponNames = readOnlyStats.get("weapon names").getValues().stream().map(x -> x.getStringValue()).toList();
                final String weaponName = weaponNames.get(Integer.parseInt(weaponId) - 1);
                final ValuedStat attackRoutineStat = stats.get("weapon " + weaponId + " attack routine");
                final ValuedStat attackModifiersStat = stats.get("weapon " + weaponId + " attack modifiers"); //for debuggability
                final ValuedStat damageModifiersStat = stats.get("weapon " + weaponId + " damage modifiers"); //for debuggability
                final ValuedStat highMeleeAttackBonus = stats.get("high melee attack bonus"); //for later comparison
                final ValuedStat highRangeAttackBonus = stats.get("high range attack bonus"); //for later comparison
                final ValuedStat averageMeleeDamage = stats.get("average melee damage"); //for later comparison
                final ValuedStat averageRangeDamage = stats.get("average range damage"); //for later comparison
                final Integer strengthModifier = readOnlyStats.get("strength modifier").getIntValue();
                final Integer dexterityModifier = readOnlyStats.get("dexterity modifier").getIntValue();
                final boolean globalFinesseDexToDamage = readOnlyStats.get("finesse dex to damage").getValues().size() > 0;
                final Integer baseAttackBonus = readOnlyStats.get("base attack bonus").getIntValue();
                final Integer epicBaseAttackBonus = readOnlyStats.get("epic base attack bonus").getIntValue();
                final Integer sizeModifierToAttack = readOnlyStats.get("size modifier to attack").getIntValue();
                final String size = readOnlyStats.get("size").getStringValue();
                final boolean incorporeal = readOnlyStats.get("incorporeal").getValues().size() > 0;
                final boolean weaponFinesse = readOnlyStats.get("weapon finesse").getValues().size() > 0;
                final boolean multiattack = readOnlyStats.get("multiattack").getValues().size() > 0;
                final boolean usingUnarmedStrikes = readOnlyStats.get("using unarmed strikes").getValues().size() > 0;
                final boolean usingManufacturedWeapons = readOnlyStats.get("using manufactured weapons").getValues().size() > 0;
                final List<Value> globalAttackModifiers = readOnlyStats.get("global attack modifiers").getValues();
                final List<Value> globalMeleeAttackModifiers = readOnlyStats.get("global melee attack modifiers").getValues();
                final List<Value> globalRangeAttackModifiers = readOnlyStats.get("global range attack modifiers").getValues();
                if (attackModifiersStat.getValues().size() > 0)
                    throw new RuntimeException(attackModifiersStat.name() + " stat should not be populated");
                if (damageModifiersStat.getValues().size() > 0)
                    throw new RuntimeException(attackModifiersStat.name() + " stat should not be populated");
                final Map<String, List<String>> props = new LinkedHashMap<>();
                for (final Value prop : propsList) {
                    final List<String> propSplit = Arrays.asList(prop.getStringValue().split("=", -1)).stream().map(x -> x.trim()).toList();
                    final String propName = propSplit.get(0);
                    final String propValue = propSplit.size() == 1 ? null : propSplit.get(1);
                    props.computeIfAbsent(propName, k -> new ArrayList<>()).add(propValue);
                }
                final String name = val1(props.remove("name"));
                final int numWeaponsOfSameName = Optional.ofNullable(props.remove("num")).map(x -> sumAsIntsS(x)).orElse(1);
                final int numAttacksMultiplier = val01(props.remove("num attacks multiplier")).map(x -> Integer.parseInt(x)).orElse(1);
                final boolean melee = props.remove("melee") != null;
                final boolean range = props.remove("range") != null;
                final boolean thrown = props.remove("thrown") != null;
                final Integer rangeIncrement = val01(props.remove("ft range increment")).map(x -> Integer.parseInt(x)).orElse(null);
                final String baseDamageOverride = val01(props.remove("base damage")).orElse(null);
                final String damageType = val01(props.remove("damage type")).orElse(null);
                final int naturalWeaponDamageSizeModifier = Optional.ofNullable(props.remove("natural weapon damage size modifiers"))
                        .map(x -> sumInts(x.stream().map(y -> Integer.parseInt(y)).toList()))
                        .orElse(0);
                final boolean noStrengthToDamage = props.remove("no strength to damage") != null;
                final boolean halfStrengthToDamage = props.remove("half strength to damage") != null;
                final boolean finessable = props.remove("finessable") != null;
                final boolean weaponDexToDamage = props.remove("finesse dex to damage") != null;
                final boolean light = props.remove("light") != null;
                final boolean oneHanded = props.remove("one-handed") != null;
                final boolean twoHanded = props.remove("two-handed") != null;
                final boolean unarmed = name.equals("unarmed");
                final boolean naturalOverride = props.remove("natural") != null;
                final boolean secondaryNaturalOverride = props.remove("secondary natural") != null;
                final boolean natural = naturalOverride || secondaryNaturalOverride;
                final boolean secondaryNatural = secondaryNaturalOverride || (natural && (usingUnarmedStrikes || usingManufacturedWeapons));
                final boolean inTwoHands = props.remove("in two hands") != null;
                final boolean inOffHand = props.remove("in off-hand") != null;
                final boolean swarm = props.remove("swarm") != null;
                final Integer criticalThreatRange = val01(props.remove("critical threat range")).map(x -> Integer.parseInt(x)).orElse(null);
                final Integer criticalThreatMultiplier = val01(props.remove("critical threat multiplier")).map(x -> Integer.parseInt(x)).orElse(null);
                final boolean weaponImprovedCritical = props.remove("improved critical") != null;
                final List<String> additionalEffects = props.remove("additional effect");
                final List<String> weaponMiscAttackModifiers = props.remove("attack modifier");
                final List<String> weaponMiscDamageModifiers = props.remove("damage modifier");
                if ( ! props.isEmpty())
                    throw new RuntimeException("unrecognized weapon properties: " + props.keySet());
                if (melee && ((light ? 1 : 0) + (oneHanded ? 1 : 0) + (twoHanded ? 1 : 0) + (unarmed ? 1 : 0) + (natural ? 1 : 0) == 0))
                    throw new RuntimeException("the melee weapon has zero types (light, one-handed, two-handed, unarmed, natural): " + name);
                if (melee && ((light ? 1 : 0) + (oneHanded ? 1 : 0) + (twoHanded ? 1 : 0) + (unarmed ? 1 : 0) + (natural ? 1 : 0) >= 2))
                    throw new RuntimeException("the melee weapon has two or more types (light, one-handed, two-handed, unarmed, natural): " + name);
                
                //
                
                final Integer sizeRating;
                if (size == null) {
                    sizeRating = null;
                } else {
                    sizeRating = switch (size) {
                        case "fine"       -> 1;
                        case "diminutive" -> 2;
                        case "tiny"       -> 3;
                        case "small"      -> 4;
                        case "medium"     -> 5;
                        case "large"      -> 6;
                        case "huge"       -> 7;
                        case "gargantuan" -> 8;
                        case "colossal"   -> 9;
                        case "colossal-plus" -> 10;
                        default -> throw new RuntimeException("unrecognized size value >>" + size + "<<");
                    };
                }
    
                //
                
                List<Value> attackModifiers = new ArrayList<>();
                if ( ! swarm) {
                    if (baseAttackBonus != null)
                        attackModifiers.add(new Value(baseAttackBonus).source("base attack bonus"));
                    if (epicBaseAttackBonus != null)
                        attackModifiers.add(new Value(epicBaseAttackBonus).source("epic base attack bonus"));
                    if (sizeModifierToAttack != null)
                        attackModifiers.add(new Value(sizeModifierToAttack).source("size"));
                    if (secondaryNatural) {
                        if (multiattack) {
                            attackModifiers.add(new Value(-2).source("multiattack secondary natural weapon"));
                        } else {
                            attackModifiers.add(new Value(-5).source("secondary natural weapon"));
                        }
                    }
                    if (melee && weaponFinesse && finessable && dexterityModifier != null) {
                        if (strengthModifier == null) {
                            attackModifiers.add(new Value(dexterityModifier).source("finessable dexterity"));
                        } else if (strengthModifier < dexterityModifier) {
                            attackModifiers.add(new Value(dexterityModifier).source("finessable dexterity"));
                        } else {
                            attackModifiers.add(new Value(strengthModifier).source("finessable strength"));
                        }
                    } else if (melee && incorporeal) {
                        attackModifiers.add(new Value(dexterityModifier).source("incorporeal melee dexterity"));
                    } else if (range) {
                        attackModifiers.add(new Value(dexterityModifier).source("range dexterity"));
                    } else if (thrown) {
                        attackModifiers.add(new Value(dexterityModifier).source("thrown dexterity"));
                    } else if (melee) {
                        attackModifiers.add(new Value(strengthModifier).source("default melee strength"));
                    } else {
                        throw new RuntimeException("don't know what ability score modifier to use for attack for weapon: " + weaponName);
                    }
                    if (weaponMiscAttackModifiers != null) {
                        for (final String x : weaponMiscAttackModifiers) {
                            attackModifiers.add(new Value(Integer.parseInt(x)));
                        }
                    }
                    if (globalAttackModifiers != null) {
                        attackModifiers.addAll(globalAttackModifiers);
                    }
                    if (globalMeleeAttackModifiers != null && melee) {
                        attackModifiers.addAll(globalMeleeAttackModifiers);
                    }
                    if (globalRangeAttackModifiers != null && range) {
                        attackModifiers.addAll(globalRangeAttackModifiers);
                    }
                    attackModifiersStat.setValues(attackModifiers);
                }
                
                //
                
                String baseDamage = baseDamageOverride;
                if ((baseDamage == null && natural) || (baseDamage != null && baseDamage.equals("natural"))) {
                    final int sizeRating2 = sizeRating + naturalWeaponDamageSizeModifier;
                    baseDamage = switch (sizeRating2) {
                        // https://www.d20pfsrd.com/feats/monster-feats/improved-natural-attack/
                        case  1 ->  "1";   // 1
                        case  2 ->  "1d2"; // 1.5
                        case  3 ->  "1d3"; // 2
                        case  4 ->  "1d4"; // 2.5
                        case  5 ->  "1d6"; // 3.5
                        case  6 ->  "1d8"; // 4.5
                        case  7 ->  "2d6"; // 7
                        case  8 ->  "2d8"; // 9
                        case  9 ->  "4d6"; //14
                        case 10 ->  "6d6"; //21
                        case 11 ->  "8d6"; //28
                        case 12 -> "12d6"; //42
                        default -> throw new RuntimeException(
                                "cannot determine base damage for natural weapon " + name
                                + " with creature size " + size 
                                + " and natural weapon damage size modifier " + naturalWeaponDamageSizeModifier);
                    };
                }
                if (baseDamage == null && unarmed) {
                    throw new RuntimeException("TODO unarmed base damage");
                }
                if (baseDamage == null) {
                    throw new RuntimeException("missing base damage for weapon: " + natural);
                }
                
                //
                
                List<Value> damageModifiers = new ArrayList<>();
                Value strToDamage = null;
                if (strengthModifier != null) {
                    if (noStrengthToDamage) {
                    } else if (halfStrengthToDamage) {
                        strToDamage = new Value(strengthModifier / 2).source("half strength");
                    } else if (melee) {
                        if (natural && weaponNames.size() == 1 && numWeaponsOfSameName == 1 && numAttacksMultiplier == 1) {
                            strToDamage = new Value(strengthModifier + strengthModifier / 2).source("single natural weapon strength and a half");
                        } else if (secondaryNatural) {
                            strToDamage = new Value(strengthModifier / 2).source("secondary natural weapon half strength");
                        } else if (natural) {
                            strToDamage = new Value(strengthModifier).source("natural weapon strength");
                        } else if (twoHanded) { //assumed in two hands
                            strToDamage = new Value(strengthModifier + strengthModifier / 2).source("two-handed weapon strength and a half");
                        } else if (oneHanded && inTwoHands) {
                            strToDamage = new Value(strengthModifier + strengthModifier / 2).source("one-handed weapon in two hands strength and a half");
                        } else if (inOffHand) {
                            strToDamage = new Value(strengthModifier / 2).source("off-hand half strength");
                        } else {
                            strToDamage = new Value(strengthModifier).source("default melee strength");
                        }
                    } else if (range) {
                        strToDamage = new Value(strengthModifier).source("default range strength");
                    } else if (thrown) {
                        strToDamage = new Value(strengthModifier).source("default thrown strength");
                    }
                }
                if (dexterityModifier != null && melee && finessable && (globalFinesseDexToDamage || weaponDexToDamage)) {
                    if (strToDamage == null) {
                        damageModifiers.add(new Value(dexterityModifier).source("dexterity"));
                    } else if (strToDamage.getIntValue() < dexterityModifier) {
                        damageModifiers.add(new Value(dexterityModifier).source("dexterity"));
                    } else {
                        damageModifiers.add(strToDamage);
                    }
                } else if (strToDamage != null) {
                    damageModifiers.add(strToDamage);
                }
                if (weaponMiscDamageModifiers != null) {
                    for (final String weaponMiscDamageModifier : weaponMiscDamageModifiers) {
                        damageModifiers.add(new Value(Integer.parseInt(weaponMiscDamageModifier)));
                    }
                }
                damageModifiersStat.setValues(damageModifiers);
                
                //
                
                final int attackModifier = sumAsInts(attackModifiers).getIntValue();
                final int damageModifier = sumAsInts(damageModifiers).getIntValue();
                final boolean iterative5  = ! natural && baseAttackBonus >=  6 && ( ! inOffHand);
                final boolean iterative10 = ! natural && baseAttackBonus >= 11 && ( ! inOffHand);
                final boolean iterative15 = ! natural && baseAttackBonus >= 16 && ( ! inOffHand);
                
                int numAttacks = 0;
                final StringBuilder attackRoutine = new StringBuilder();
                if (swarm) {
                    attackRoutine.append(name);
                    numAttacks++;
                } else if (numWeaponsOfSameName != 1 && ! iterative5) {
                    attackRoutine.append(numWeaponsOfSameName);
                    attackRoutine.append(" ");
                    attackRoutine.append(name);
                    attackRoutine.append(" ");
                    attackRoutine.append(withSign(attackModifier));
                    numAttacks = numWeaponsOfSameName;
                } else {
                    attackRoutine.append(name);
                    if (rangeIncrement != null)
                        attackRoutine.append(" (").append(rangeIncrement).append(" ft incr)");
                    attackRoutine.append(" ");
                    attackRoutine.append(withSign(attackModifier));
                    numAttacks++;
                    for (int i = 2; i <= numWeaponsOfSameName; ++i) {
                        attackRoutine.append("/").append(withSign(attackModifier));
                        numAttacks++;
                    }
                    if (iterative5) {
                        attackRoutine.append("/").append(withSign(attackModifier - 5));
                        numAttacks++;
                    }
                    if (iterative10) {
                        attackRoutine.append("/").append(withSign(attackModifier - 10));
                        numAttacks++;
                    }
                    if (iterative15) {
                        attackRoutine.append("/").append(withSign(attackModifier - 15));
                        numAttacks++;
                    }
                }
                attackRoutine.append(" (");
                attackRoutine.append(baseDamage);
                if (damageModifier != 0)
                    attackRoutine.append(withSign(damageModifier));
                if (damageType != null)
                    attackRoutine.append(" ").append(damageType);
                if (weaponImprovedCritical) {
                    if (criticalThreatRange == null || criticalThreatRange == 20) {
                        attackRoutine.append("/19-20");
                    } else {
                        attackRoutine.append("/" + (((criticalThreatRange - 21) * 2) + 21) + "-20");
                    }
                } else if (criticalThreatRange != null && criticalThreatRange != 20) {
                    attackRoutine.append("/" + criticalThreatRange + "-20");
                }
                if (criticalThreatMultiplier != null && criticalThreatMultiplier != 2)
                    attackRoutine.append("/×" + criticalThreatMultiplier);
                if (additionalEffects != null)
                    attackRoutine.append(" plus " + additionalEffects.stream().collect(Collectors.joining(" and ")));
                attackRoutine.append(")");
                attackRoutineStat.setValues(Collections.singletonList(new Value(attackRoutine.toString())));
                
                //
                final ValuedStat averageDamageStat;
                if (melee || swarm)
                    averageDamageStat = averageMeleeDamage;
                else if (range)
                    averageDamageStat = averageRangeDamage;
                else   
                    throw new RuntimeException();
                final Matcher baseDamageMatcher = Pattern.compile("^([0-9]+)(?:d([0-9]+))?$").matcher(baseDamage);
                if ( ! baseDamageMatcher.matches())
                    throw new RuntimeException("invalid baseDamage: " + baseDamage);
                final int numDamageDice = Integer.parseInt(baseDamageMatcher.group(1));
                final Integer damageDiceSize = Optional.ofNullable(baseDamageMatcher.group(2)).map(x -> Integer.parseInt(x)).orElse(null);
                final double averageBaseDamage = damageDiceSize == null ? numDamageDice : numDamageDice * (damageDiceSize + 1) * 0.5;
                final List<Value> averageDamageValues = new ArrayList<>(averageDamageStat.getValues());
                averageDamageValues.add(new Value(numAttacks * (averageBaseDamage + damageModifier)).source(weaponName));
                averageDamageStat.setValues(averageDamageValues);
                
                //
                final ValuedStat highAttackBonusStat;
                if (melee || swarm)
                    highAttackBonusStat = highMeleeAttackBonus;
                else if (range)
                    highAttackBonusStat = highRangeAttackBonus;
                else
                    throw new RuntimeException();
                if (highAttackBonusStat.getIntValue() == null || highAttackBonusStat.getIntValue() < attackModifier)
                    highAttackBonusStat.setValues(Collections.singletonList(new Value(attackModifier).source(weaponName)));
            });
        });
            
        final List<String> individualAttackRoutineNames = weaponIds.stream().map(x -> "weapon " + x + " attack routine").toList();
        stats.postAgg("attack routine", individualAttackRoutineNames, (writeableStats, readOnlyStats) -> {
            final List<ReadOnlyValuedStat> individualAttackRoutines = weaponIds.stream().map(x -> readOnlyStats.get("weapon " + x + " attack routine")).toList();
            final StringBuilder fullAttackRoutine = new StringBuilder();
            for (final ReadOnlyValuedStat individualAttackRoutine : individualAttackRoutines) {
                final Value x = individualAttackRoutine.val01().orElse(null);
                if (x == null)
                    continue;
                if ( ! fullAttackRoutine.isEmpty())
                    fullAttackRoutine.append(", ");
                fullAttackRoutine.append(x.getStringValue());
            }
            writeableStats.get("attack routine").setValues(Collections.singletonList(new Value(fullAttackRoutine.toString())));
        });
    }

}
