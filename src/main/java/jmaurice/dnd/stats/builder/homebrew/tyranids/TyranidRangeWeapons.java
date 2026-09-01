package jmaurice.dnd.stats.builder.homebrew.tyranids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class TyranidRangeWeapons extends BaseBuilder {

    public TyranidRangeWeapons(final Stats stats) { super(stats); }

    public void build() {
        //"no strength to damage"

        //Shoots beetles that eat into enemy
        //18 in, atks 1, str 5, AP -1, dmg 1, assault
        weapon("fleshborer", 30, 5, "d6", Arrays.asList("additional effect=borer beetles"));
        to1("borer beetles", "fleshborer");
        
        //Shoots worms that eat into enemy
        //18 in, atks 2, str 4, AP  0, dmg 1, assault
        weapon("termagant devourer", 30, 5, "d6", Arrays.asList("additional effect=flesh worms"));
        to1("flesh worms", "termagant devourer");
        
        //Shoots a sticky mesh that wraps around a target and quickly shrinks and hardens
        //18 in, atks d6, str 2, AP 0, dmg 1, assault, torrent (auto-hits), devastating wounds (natural 6 wound roll bypasses all saves and invuln rolls) (10th)
        weapon("strangleweb", 30, 5, "d6", Arrays.asList("additional effect=entangle"));
        
        //Shoots harpoons
        //24 in, atks 1, str 4, AP -1, dmg 1, heavy (+1 to-hit if stationary), (10th)
        weapon("spike rifle", 40, 7, "d6", Arrays.asList());
        
        //Shoots shrapnel warhead as artillery
        //18 in, atks d3, str 5, AP 0, dmg 1, heavy, blast
        weapon("shardlauncher", 30, 5, "d6", Arrays.asList());
        
        //Shoots superior shrapnel warhead as artillery
        //24 in, atks d6, str 5, AP 0, dmg 1, heavy, blast
        weapon("barblauncher", 40, 5, "d6", Arrays.asList());
        
        //Shoots darts
        //12 in, atks 2, str 5, AP -1, dmg 1, assault, pistol, 
        weapon("spinefists", 20, 3, "d4", Arrays.asList("additional effect=does not provoke", "half strength to damage"));
        
        //--
        
        //Shoots worms that eat into enemy
        //18 in, atks 5, str 4, AP  0, dmg 1, assault
        weapon("devourer", 30, 12, "d6", Arrays.asList("additional effect=flesh worms"));
        to1("devourer", "devourers", root);
        to1("flesh worms", "devourer");
        
        //Shoots maggots that explode on contact, releasing acid
        //24 in, atks 3, str 5, AP -2, dmg 1, assault
        weapon("deathspitter", 40, 12, "d4", Arrays.asList("additional effect=touch"));
        to1("deathspitter", "deathspitters", root);

        //Shoots metallic crystaline shards (coated with poison or corrosive substances) 
        //36 in, atks d3, str 8, AP -3, dmg 2, assault
        weapon("venom cannon", 60, 12, "d6", Arrays.asList("attack modifier=3"));
        
        //Shoots a seedpod that grows to maturity in a second, releasing thorny vines in all directions
        //36 in, atks d6, str 6, AP -1, dmg 1, assault 
        weapon("barbed strangler", 60, 12, "d6", Arrays.asList("additional effect=entangle and anchored"));
        
        //--
        
        //Shoots bigger metallic crystaline shards (coated with poison or corrosive substances) 
        //36 in, atks 3, str 9, AP -3, dmg 4, heavy
        weapon("heavy venom cannon", 60, 25, "d6", Arrays.asList("attack modifier=3"));
        
        //Shoots a bigger seedpod that grows to maturity in a second, releasing thorny vines in all directions
        //36 in, atks d3+3, str 8, AP -2, dmg 2, heavy, blast
        weapon("stranglethorn cannon", 60, 25, "d6", Arrays.asList("additional effect=area attack", "additional effect=entangle and anchored"));
        
        borerBeetles();
        fleshWorms();
    }
    
    private void weapon(
            final String weaponName,
            final int rangeIncrementFt,
            final int numDamageDice,
            final String damageDiceSize,
            final List<String> additionalProps
            ) {
        agg(weaponName, root, values -> sumAsInts(values));
        
        agg(weaponName + " damage dice", values -> sumAsInts(values));
        input(weaponName + " damage dice", Arrays.asList(weaponName, "aberration hit dice"), stats -> {
            final Integer weaponCount = stats.get(weaponName).getIntValue();
            if (weaponCount == null)
                return null;
            final int numHitDice = stats.get("aberration hit dice").val1().getIntValue();
            return new Value(numDamageDice).floor();
        });
        
        input("weapon properties", Arrays.asList(weaponName, weaponName + " damage dice"), stats -> {
            final Integer weaponCount = stats.get(weaponName).getIntValue();
            if (weaponCount == null)
                return null;
            final int damageDice = stats.get(weaponName + " damage dice").val1().getIntValue();
            final List<String> properties = new ArrayList<>();
            properties.add("name=" + weaponName);
            properties.add("num=" + weaponCount);
            properties.add("natural");
            properties.add("range");
            properties.add("ft range increment=" + rangeIncrementFt);
            properties.add("base damage=" + damageDice + damageDiceSize);
            for (final String additionalProp : additionalProps)
                properties.add(additionalProp);
            return new Value(properties.stream().collect(Collectors.joining(",")));
        });
    }
    
    private void borerBeetles() {
        final String descript = """
                <b>Borer Beetles:</b>
                A creature damaged by a fleshborer gains one stack of borer beetles, no save.
                A creature with one or more stacks of borer beetles is nauseated and takes 1d6 damage per round per stack.
                A creature afflicted with borer beetles makes one Fortitude save per round.
                On a success,
                the creature is sickened for the round instead of nauseated,
                and reduces the damage by half for the round,
                and reduces the number of stacks of borer beetles by one.
                The Fortitude saving throw DC is __DC__.
                A creature afflicted with multiple stacks of borer beetles, flesh worms, etc.,
                makes a single Fortitude saving throw.
                The DC is the lowest DC among the effects - plus 1 for each additional stack.
                """.replace("\n", " ").replaceAll(" +", " ").trim();
        
        input("borer beetles dc", Arrays.asList("borer beetles", "aberration hit dice", "constitution modifier"), stats -> {
            final boolean borerBeetles2 = stats.get("borer beetles").getBooleanValue(false);
            if ( ! borerBeetles2)
                return null;
            final int numHitDice = stats.get("aberration hit dice").val1().getIntValue();
            final int conMod = stats.get("constitution modifier").val1().getIntValue();
            final int dc = 10 + numHitDice / 2 + conMod;
            return new Value(dc);
        });
        to1("special abilities long", "borer beetles dc", value -> new Value(descript.replace("__DC__", value.getStringValue())));
    }
    
    private void fleshWorms() {
        final String descript = """
                <b>Flesh Worms:</b>
                A creature damaged by a devourer gains one stack of flesh worms, no save.
                A creature with one or more stacks of flesh worms is nauseated and takes 1d6 damage per round per stack.
                A creature afflicted with flesh worms makes one Fortitude save per round.
                On a success,
                the creature is sickened for the round instead of nauseated,
                and reduces the damage by half for the round,
                and reduces the number of stacks of flesh worms by one.
                The Fortitude saving throw DC is __DC__.
                A creature afflicted with multiple stacks of flesh worms, flesh worms, etc.,
                makes a single Fortitude saving throw.
                The DC is the lowest DC among the effects - plus 1 for each additional stack.
                """.replace("\n", " ").replaceAll(" +", " ").trim();
        input("flesh worms dc", Arrays.asList("flesh worms", "aberration hit dice", "constitution modifier"), stats -> {
            final boolean fleshWorms2 = stats.get("flesh worms").getBooleanValue(false);
            if ( ! fleshWorms2)
                return null;
            final int numHitDice = stats.get("aberration hit dice").val1().getIntValue();
            final int conMod = stats.get("constitution modifier").val1().getIntValue();
            final int dc = 10 + numHitDice / 2 + conMod;
            return new Value(dc);
        });
        to1("special abilities long", "flesh worms dc", value -> new Value(descript.replace("__DC__", value.getStringValue())));
    }
    
}
