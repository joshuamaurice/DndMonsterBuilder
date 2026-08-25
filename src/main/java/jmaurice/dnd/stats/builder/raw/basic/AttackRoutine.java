package jmaurice.dnd.stats.builder.raw.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.ReadOnlyStat;
import jmaurice.dnd.stats.impl.Stat;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class AttackRoutine extends BaseBuilder {

    public AttackRoutine(final Stats stats) { super(stats); }

    public void build() {
        combatManeuversBonus();
        weaponAttackRoutines();
    }
    
    private void combatManeuversBonus() {
        agg("base attack bonus", rootleaf, values -> sumAsDoubles(values).floor()); //sumAsDoubles to support partial base attack bonus multiclassing
        agg("epic base attack bonus", rootleaf, values -> sumAsDoubles(values).floor()); //sumAsDoubles to support partial base attack bonus multiclassing
        agg("combat maneuvers bonus", leaf, values -> withSign(sumAsInts(values)));
        to1("combat maneuvers bonus", "base attack bonus", value -> value.source("base attack bonus"));
        to1("combat maneuvers bonus", "epic base attack bonus", value -> value.source("epic base attack bonus"));
        to1("combat maneuvers bonus", "size modifier to attack", value -> value.mult(-1).source("size"));
        to1("combat maneuvers bonus", "strength modifier", value -> value.source("str"));
    }
    
    private void weaponAttackRoutines() {
        final List<String> weaponIds = IntStream.range(1, 6+1).mapToObj(x -> x + "").toList();
    
        aggN("weapon properties", values -> values);
        agg("attack routine", rootleaf, values -> join(values, ", "));
        aggN("weapon names", rootleaf, values -> values);
        agg("using unarmed strikes", root);
        agg("using manufactured weapons", root);
        weaponIds.forEach(weaponId -> agg("weapon " + weaponId + " attack routine", root));
        weaponIds.forEach(weaponId -> aggN("weapon " + weaponId + " properties", root, values -> values));
        weaponIds.forEach(weaponId -> aggN("weapon " + weaponId + " attack modifiers", rootleaf, values -> values)); //for debugging
        weaponIds.forEach(weaponId -> aggN("weapon " + weaponId + " damage modifiers", rootleaf, values -> values)); //for debugging
        
        final List<String> parseWeaponPropertiesPostStats = new ArrayList<>();
        parseWeaponPropertiesPostStats.add("weapon names");
        parseWeaponPropertiesPostStats.add("using unarmed strikes");
        parseWeaponPropertiesPostStats.add("using manufactured weapons");
        weaponIds.forEach(weaponId -> parseWeaponPropertiesPostStats.add("weapon " + weaponId + " properties"));
        final List<String> parseWeaponPropertiesOtherInputs = new ArrayList<>();
        parseWeaponPropertiesOtherInputs.add("weapon properties");
        stats.post("parse weapon properties", parseWeaponPropertiesPostStats, parseWeaponPropertiesOtherInputs, (stats, readOnlyStats) -> {
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
                    throw new RuntimeException("a weapon properties value is missing a name: " + allWeaponPropertiesValue.getStringValue());
                if (namesInValue.size() >= 2)
                    throw new RuntimeException("a weapon properties value has two or more names: " + allWeaponPropertiesValue.getStringValue());
                final String nameInValue = namesInValue.get(0);
                final Map<String, List<String>> existingPropsForName = weapons.computeIfAbsent(nameInValue, k -> new LinkedHashMap<>());
                propsInValue.forEach((k,v) -> existingPropsForName.computeIfAbsent(k, k2 -> new ArrayList<>()).addAll(v));
            }
            if (weapons.size() > weaponIds.size())
                throw new RuntimeException("too many weapons: " + weapons.keySet());
    
            final Stat weaponNamesStat = stats.get("weapon names");
            if (weaponNamesStat.getValues().size() > 0)
                throw new RuntimeException("weapon names stat should not be populated");
            weaponNamesStat.setValues(weapons.keySet().stream().map(x -> new Value(x)).toList());
    
            final Stat usingUnarmedStrikesStat = stats.get("using unarmed strikes");
            if (usingUnarmedStrikesStat.getValues().size() > 0)
                throw new RuntimeException("using unarmed strikes stat should not be populated");
            if (weapons.keySet().contains("unarmed"))
                usingUnarmedStrikesStat.setValues(Collections.singletonList(new Value(true)));
    
            final Stat usingManufacturedWeaponsStat = stats.get("using manufactured weapons");
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
                final Stat weaponProperties = stats.get("weapon " + weaponId + " properties");
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
        
        agg("finesse dex to damage", root);
        agg("multiattack", root);
        agg("weapon finesse", root);
        weaponIds.forEach(weaponId -> {
            final List<String> postStatNames = new ArrayList<>();
            postStatNames.add("weapon " + weaponId + " attack routine");
            postStatNames.add("weapon " + weaponId + " attack modifiers");
            postStatNames.add("weapon " + weaponId + " damage modifiers");
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
            stats.post("generate weapon " + weaponId + " attack routine", postStatNames, otherInputStatNames, (stats, readOnlyStats) -> {
                final List<Value> propsList = readOnlyStats.get("weapon " + weaponId + " properties").getValues();
                if (propsList.isEmpty())
                    return;
                final List<String> weaponNames = readOnlyStats.get("weapon names").getValues().stream().map(x -> x.getStringValue()).toList();
                final Stat attackRoutineStat = stats.get("weapon " + weaponId + " attack routine");
                final Stat attackModifiersStat = stats.get("weapon " + weaponId + " attack modifiers"); //for debuggability
                final Stat damageModifiersStat = stats.get("weapon " + weaponId + " damage modifiers"); //for debuggability
                final Integer strengthModifier = readOnlyStats.get("strength modifier").val01().map(x -> x.getIntValue()).orElse(null);
                final Integer dexterityModifier = readOnlyStats.get("dexterity modifier").val01().map(x -> x.getIntValue()).orElse(null);
                final boolean globalFinesseDexToDamage = readOnlyStats.get("finesse dex to damage").val01().map(x -> true).orElse(false);
                final int baseAttackBonus = readOnlyStats.get("base attack bonus").val1().getIntValue();
                final Integer epicBaseAttackBonus = readOnlyStats.get("epic base attack bonus").val01().map(x -> x.getIntValue()).orElse(null);
                final int sizeModifierToAttack = readOnlyStats.get("size modifier to attack").val1().getIntValue();
                final String size = readOnlyStats.get("size").val1().getStringValue();
                final boolean incorporeal = readOnlyStats.get("incorporeal").val01().map(x -> true).orElse(false);
                final boolean weaponFinesse = readOnlyStats.get("weapon finesse").val01().map(x -> true).orElse(false);
                final boolean multiattack = readOnlyStats.get("multiattack").val01().map(x -> true).orElse(false);
                final boolean usingUnarmedStrikes = readOnlyStats.get("using unarmed strikes").val01().map(x -> true).orElse(false);
                final boolean usingManufacturedWeapons = readOnlyStats.get("using manufactured weapons").val01().map(x -> true).orElse(false);
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
                final int numWeaponsOfSameName = Optional.ofNullable(props.remove("num")).map(x -> sumStringAsInts(x)).orElse(1);
                final int numAttacksMultiplier = val01(props.remove("num attacks multiplier")).map(x -> Integer.parseInt(x)).orElse(1);
                final boolean melee = props.remove("melee") != null;
                final boolean range = props.remove("range") != null;
                final boolean thrown = props.remove("thrown") != null;
                final Integer rangeIncrement = val01(props.remove("ft range increment")).map(x -> Integer.parseInt(x)).orElse(null);
                final String baseDamageOverride = val01(props.remove("base damage")).orElse(null);
                final int naturalWeaponDamageSizeModifier = Optional.ofNullable(props.remove("natural weapon damage size modifiers"))
                        .map(x -> sumInts(x.stream().map(y -> Integer.parseInt(y)).toList()))
                        .orElse(0);
                final boolean noStrengthToDamage = props.remove("no strength to damage") != null;
                final boolean finessable = props.remove("finessable") != null;
                final boolean weaponFinesseDexToDamage = props.remove("finesse dex to damage") != null;
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
                if ( ! props.isEmpty())
                    throw new RuntimeException("unrecognized weapon properties: " + props.keySet());
                if (melee && ((light ? 1 : 0) + (oneHanded ? 1 : 0) + (twoHanded ? 1 : 0) + (unarmed ? 1 : 0) + (natural ? 1 : 0) == 0))
                    throw new RuntimeException("the melee weapon has zero types (light, one-handed, two-handed, unarmed, natural): " + name);
                if (melee && ((light ? 1 : 0) + (oneHanded ? 1 : 0) + (twoHanded ? 1 : 0) + (unarmed ? 1 : 0) + (natural ? 1 : 0) >= 2))
                    throw new RuntimeException("the melee weapon has two or more types (light, one-handed, two-handed, unarmed, natural): " + name);
                
                //
                
                final int sizeRating = switch (size) {
                    case "fine"       -> 1;
                    case "diminutive" -> 2;
                    case "tiny"       -> 3;
                    case "small"      -> 4;
                    case "medium"     -> 5;
                    case "large"      -> 6;
                    case "huge"       -> 7;
                    case "gargantuan" -> 8;
                    case "colossal"   -> 9;
                    default -> throw new RuntimeException("unrecognized size value >>" + size + "<<");
                };
    
                //
                
                List<Value> attackModifiers = new ArrayList<>();
                attackModifiers.add(new Value(baseAttackBonus, "base attack bonus"));
                if (epicBaseAttackBonus != null)
                    attackModifiers.add(new Value(epicBaseAttackBonus, "epic base attack bonus"));
                attackModifiers.add(new Value(sizeModifierToAttack, "size"));
                if (secondaryNatural) {
                    if (multiattack) {
                        attackModifiers.add(new Value(-2, "multiattack secondary natural weapon"));
                    } else {
                        attackModifiers.add(new Value(-5, "secondary natural weapon"));
                    }
                }
                if (melee && weaponFinesse && finessable && dexterityModifier != null) {
                    if (strengthModifier == null) {
                        attackModifiers.add(new Value(dexterityModifier, "finessable dexterity"));
                    } else if (strengthModifier < dexterityModifier) {
                        attackModifiers.add(new Value(dexterityModifier, "finessable dexterity"));
                    } else {
                        attackModifiers.add(new Value(strengthModifier, "finessable strength"));
                    }
                } else if (melee && incorporeal) {
                    attackModifiers.add(new Value(dexterityModifier, "incorporeal melee dexterity"));
                } else if (range) {
                    attackModifiers.add(new Value(dexterityModifier, "range dexterity"));
                } else if (thrown) {
                    attackModifiers.add(new Value(dexterityModifier, "thrown dexterity"));
                } else if (melee) {
                    attackModifiers.add(new Value(strengthModifier, "default melee strength"));
                } else {
                    throw new RuntimeException("don't know what ability score modifier to use for attack");
                }
                attackModifiersStat.setValues(attackModifiers);
                
                //
                
                String baseDamage = baseDamageOverride;
                if ((baseDamage == null && natural) || (baseDamage != null && baseDamage.equals("natural"))) {
                    final int sizeRating2 = sizeRating + naturalWeaponDamageSizeModifier;
                    baseDamage = switch (sizeRating2) {
                        case 1 -> "1";
                        case 2 -> "1d2";
                        case 3 -> "1d3";
                        case 4 -> "1d4";
                        case 5 -> "1d6";
                        case 6 -> "1d8";
                        case 7 -> "2d6";
                        case 8 -> "2d8";
                        case 9 -> "4d6";
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
                if (noStrengthToDamage == false && strengthModifier != null) {
                    if (melee) {
                        if (natural && weaponNames.size() == 1 && numWeaponsOfSameName == 1 && numAttacksMultiplier == 1) {
                            strToDamage = new Value(strengthModifier + strengthModifier / 2, "single natural weapon strength and a half");
                        } else if (secondaryNatural) {
                            strToDamage = new Value(strengthModifier / 2, "secondary natural weapon half strength");
                        } else if (natural) {
                            strToDamage = new Value(strengthModifier, "natural weapon strength");
                        } else if (twoHanded) { //assumed in two hands
                            strToDamage = new Value(strengthModifier + strengthModifier / 2, "two-handed weapon strength and a half");
                        } else if (oneHanded && inTwoHands) {
                            strToDamage = new Value(strengthModifier + strengthModifier / 2, "one-handed weapon in two hands strength and a half");
                        } else if (inOffHand) {
                            strToDamage = new Value(strengthModifier / 2, "off-hand half strength");
                        } else {
                            strToDamage = new Value(strengthModifier, "default melee strength");
                        }
                    } else if (range) {
                        strToDamage = new Value(strengthModifier, "default range strength");
                    } else if (thrown) {
                        strToDamage = new Value(strengthModifier, "default thrown strength");
                    }
                }
                if (dexterityModifier != null && melee && finessable && (globalFinesseDexToDamage || weaponFinesseDexToDamage)) {
                    if (strToDamage == null) {
                        damageModifiers.add(new Value(dexterityModifier, "dexterity"));
                    } else if (strToDamage.getIntValue() < dexterityModifier) {
                        damageModifiers.add(new Value(dexterityModifier, "dexterity"));
                    } else {
                        damageModifiers.add(strToDamage);
                    }
                } else if (strToDamage != null) {
                    damageModifiers.add(strToDamage);
                }
                damageModifiersStat.setValues(damageModifiers);
                
                //
                
                final int attackModifier = sumAsInts(attackModifiers).getIntValue();
                final int damageModifier = sumAsInts(damageModifiers).getIntValue();
                final boolean iterative5  = ! natural && baseAttackBonus >=  6 && ( ! inOffHand);
                final boolean iterative10 = ! natural && baseAttackBonus >= 11 && ( ! inOffHand);
                final boolean iterative15 = ! natural && baseAttackBonus >= 16 && ( ! inOffHand);
                
                final StringBuilder attackRoutine = new StringBuilder();
                if (numWeaponsOfSameName != 1 && ! iterative5) {
                    attackRoutine.append(numWeaponsOfSameName);
                    attackRoutine.append(" ");
                }
                attackRoutine.append(name);
                if (rangeIncrement != null)
                    attackRoutine.append(" (").append(rangeIncrement).append(" ft incr)");
                attackRoutine.append(" ");
                attackRoutine.append(withSign(attackModifier));
                if (iterative5)
                    attackRoutine.append("/").append(withSign(attackModifier - 5));
                if (iterative10)
                    attackRoutine.append("/").append(withSign(attackModifier - 10));
                if (iterative15)
                    attackRoutine.append("/").append(withSign(attackModifier - 15));
                attackRoutine.append(" (");
                attackRoutine.append(baseDamage);
                if (damageModifier != 0)
                    attackRoutine.append(withSign(damageModifier));
                attackRoutine.append(")");
                attackRoutineStat.setValues(Collections.singletonList(new Value(attackRoutine.toString())));
            });
        });
            
        final List<String> individualAttackRoutineNames = weaponIds.stream().map(x -> "weapon " + x + " attack routine").toList();
        stats.post("generate attack routine", Arrays.asList("attack routine"), individualAttackRoutineNames, (writeableStats, readOnlyStats) -> {
            final List<ReadOnlyStat> individualAttackRoutines = weaponIds.stream().map(x -> readOnlyStats.get("weapon " + x + " attack routine")).toList();
            final StringBuilder fullAttackRoutine = new StringBuilder();
            for (final ReadOnlyStat individualAttackRoutine : individualAttackRoutines) {
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
