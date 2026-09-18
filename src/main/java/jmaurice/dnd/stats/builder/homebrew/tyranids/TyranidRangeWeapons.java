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
        //resisted by: touch, attack bonus, armor pen, roll twice and take better, reflex half, reflex negates,
        //area attack
        //damage types: physical, acid, ability score damage,
        //crit range, crit multiplier, 
        //additional effects: 
        //      affliction (ongoing),
        //      sickened, nauseated, 
        //      entangle, entangled and anchored
        //      grab, constrict, rend, 
        //      swallow whole,
        
        //--
        //Gaunt

        //Shoots darts
        //12 in, atks 2, str 3, AP 0, dmg 1, assault, pistol, 
        weapon("spinefists", 20, 5, "d4", Arrays.asList("additional effect=does not provoke", "half strength to damage"));
        
        //Shoots beetles that eat into enemy
        //18 in, atks 1, str 5, AP -1, dmg 1, assault
        weapon("fleshborer", 30, 5, "d4", Arrays.asList("additional effect=vs touch AC", "additional effect=borer beetles"));
        stat("fleshborer").to1("borer beetles");
        
        //Shoots worms that eat into enemy
        //18 in, atks 2, str 3, AP  0, dmg 1, assault
        weapon("termagant devourer", 30, 5, "d6", Arrays.asList("additional effect=flesh worms"));
        stat("termagant devourer").to1("flesh worms");
        
        //Shoots a sticky mesh that wraps around a target and quickly shrinks and hardens
        //18 in, atks d6, str 2, AP 0, dmg 1, assault, torrent (auto-hits), devastating wounds (natural 6 wound roll bypasses all saves and invuln rolls) (10th)
        weapon("strangleweb", 30, 5, "d6", Arrays.asList("additional effect=entangle"));
        
        //Shoots harpoons
        //24 in, atks 1, str 4, AP -1, dmg 1, heavy (10th)
        weapon("spike rifle", 40, 5, "d8", Arrays.asList());
        
        //Shoots shrapnel warhead as artillery
        //18 in, atks d3, str 5, AP 0, dmg 1, heavy, blast
        weapon("shardlauncher", 30, 5, "d6", Arrays.asList());
        
        //Shoots superior shrapnel warhead as artillery
        //24 in, atks d6, str 5, AP 0, dmg 1, heavy, blast
        weapon("barblauncher", 40, 5, "d6", Arrays.asList());
        
        //--
        //Warrior
        
        //Shoots worms that eat into enemy
        //18 in, atks 5, str 4, AP  0, dmg 1, assault
        weapon("devourer", 30, 12, "d6", Arrays.asList("additional effect=flesh worms"));
        stat("warrior devourer").agg(root).to1("devourer");
        stat("warrior devourers").agg(root).to1("devourer");
        stat("carnifex devourer").agg(root).to1("devourer");
        stat("carnifex devourers").agg(root).to1("devourer");
        stat("devourer").to1("flesh worms");
        
        //Shoots maggots that explode on contact, releasing acid
        //24 in, atks 3, str 5, AP -2, dmg 1, assault
        weapon("warrior deathspitter", 40, 12, "d4", Arrays.asList("damage type=acid", "attack modifier=2"));
        stat("warrior deathspitters").agg(root).to1("warrior deathspitter");

        //Shoots metallic crystaline shards (coated with poison or corrosive substances) 
        //36 in, atks d3, str 8, AP -3, dmg 2, assault
        weapon("venom cannon", 60, 12, "d6", Arrays.asList("attack modifier=3"));
        
        //Shoots a seedpod that grows to maturity in a second, releasing thorny vines in all directions
        //36 in, atks d6, str 6, AP -1, dmg 1, assault 
        weapon("barbed strangler", 60, 12, "d6", Arrays.asList("additional effect=entangle and anchored"));
        
        //--
        //Carnifex
        
        //Shoots maggots that eat into target and then explode
        //24 in, atks 3, str 7, AP -3, dmg 1, assault
        weapon("carnifex deathspitter with slimer maggots", 40, 25, "d4", Arrays.asList("damage type=acid", "additional effect=vs touch AC"));
        stat("carnifex deathspitters with slimer maggots").agg(root).to1("carnifex deathspitter with slimer maggots");
        stat("carnifex deathspitter with slimer maggots").to1("slimer maggots");
        
        //Shoots worms that eat into the target and seek our nerves and the brain
        //18 in, atks 6, str 6, AP 0, dmg 1, assault
        weapon("carnifex deathspitter with brainleech worms", 30, 25, "d6", Arrays.asList());
        stat("carnifex deathspitters with brainleech worms").agg(root).to1("carnifex deathspitter with brainleech worms");
        stat("carnifex deathspitter with brainleech worms").to1("brainleech worms");
        
        //Shoots a bigger seedpod that grows to maturity in a second, releasing thorny vines in all directions
        //36 in, atks d3+3, str 8, AP -2, dmg 2, heavy, blast
        weapon("stranglethorn cannon", 60, 25, "d6", Arrays.asList("additional effect=area attack", "additional effect=entangle and anchored"));
        
        //6 in, atks 5, str 5, AP 0, dmg 1
        //spine banks
        
        //--
        //tyrant
        
        //Shoots bigger metallic crystaline shards (coated with poison or corrosive substances) 
        //36 in, atks 3, str 9, AP -3, dmg 4, heavy
        weapon("heavy venom cannon", 60, 25, "d6", Arrays.asList("attack modifier=3"));
        
        //also stranglethorn cannon, same stats as above
        
        //--
        //tyrannofex
        
        //acid spray; same as bio-acid spray
        
        //Shoots beetles that eat into enemy
        //18 in, atks  1, str 5, AP -1, dmg 1, assault ... termagant fleshborer stats
        //24 in, atks 30, str 5, AP -1, dmg 1, assault ... fleshborer hive stats
        weapon("fleshborer hive", 30, 5, "d4", Arrays.asList("num attacks multiplier=6", "additional effect=vs touch AC", "additional effect=borer beetles"));
        stat("fleshborer hive").to1("borer beetles");
        
        //48 in, atks 3, str 14, AP -4, dmg d6+4, heavy, TODO verify
        weapon("rupture cannon", 80, 25, "d6", Arrays.asList("damage type=fire"));
        
        //24 in, atks 8, str 5, AP -1, dmg 1, assault, TODO verify
        weapon("stinger salvo", 40, 5, "d4", Arrays.asList("additional effect=half strength to damage"));
        
        
        //--
        //titans
        
        //Shoots maggots that explode on contact releasing acid
        //48 in, atks 8, str 10, AP -3, dmg d3, heavy
        weapon("bio-cannons", 80, 25, "d4", Arrays.asList("damage type=acid", "additional effect=vs touch AC"));
        stat("bio-cannon").agg(root).to1("bio-cannons");
        
        //?? in, atks 10, str 14, AP -2, dmg D3+3
        stat("bio-acid spray").agg(root);
        input1("special abilities long", Arrays.asList("bio-acid spray", "aberration hit dice", "constitution modifier"), stats -> {
            final int numWeapons = stats.get("bio-acid spray").val01().map(x -> x.getIntValue()).orElse(0);
            if (numWeapons == 0)
                return null;
            final int numHitDice = stats.get("aberration hit dice").getIntValue();
            final int conMod = stats.get("constitution modifier").getIntValue();
            return new Value(
                    """
                    <b>Bio-Acid Spray (Ex):</b> Fire _NUM_ 60 ft _CONES_ of acid. Reflex half, DC _DC_. SR no.
                    """
                    .replace("_NUM_", "" + numWeapons)
                    .replace("_CONES_", numWeapons == 1 ? "cone" : "cones")
                    .replace("_DC_", "" + (10 + numHitDice + conMod))
                    );
        });
        
        stat("bio-plasma torrent").agg(root).to1("special abilities long", new Value("<b>Bio-Plasma Torrent (Ex)</b><br/> TODO"));
        
        //--
        //Zoanthropes et al
        
        //TODO
        weapon("warp blast", 20, 1, "d6", Arrays.asList());
        
        //--
        
        borerBeetles();
        fleshWorms();
        slimerMaggots();
        brainleechWorms();
    }
    
    private void weapon(
            final String weaponName,
            final int rangeIncrementFt,
            final int numDamageDice,
            final String damageDiceSize,
            final List<String> additionalProps
            ) {
        stat(weaponName)
        .agg(root, values -> sumAsInts(values));
        
        stat(weaponName)
        .to1(weaponName + " damage dice", new Value(numDamageDice))
        .agg(values -> sumAsInts(values));
        
        input1("weapon properties", Arrays.asList(weaponName, weaponName + " damage dice"), stats -> {
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
                The DC is the lowest DC among the effects plus 1 for each additional stack.
                """.replace("\n", " ").replaceAll(" +", " ").trim();
        stat("borer beetles")
        .agg(values -> values.isEmpty() ? null : new Value(1));
        
        input1("borer beetles dc", Arrays.asList("borer beetles", "aberration hit dice", "constitution modifier"), stats -> {
            if (stats.get("borer beetles").getValues().size() == 0)
                return null;
            final int numHitDice = stats.get("aberration hit dice").val1().getIntValue();
            final int conMod = stats.get("constitution modifier").val1().getIntValue();
            final int dc = 10 + numHitDice / 2 + conMod;
            return new Value(dc);
        })
        .to1("special abilities long", value -> new Value(descript.replace("__DC__", value.getStringValue())));
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
                The DC is the lowest DC among the effects plus 1 for each additional stack.
                """.replace("\n", " ").replaceAll(" +", " ").trim();
        stat("flesh worms")
        .agg(values -> values.isEmpty() ? null : new Value(1));
        
        input1("flesh worms dc", Arrays.asList("flesh worms", "aberration hit dice", "constitution modifier"), stats -> {
            if (stats.get("flesh worms").getValues().size() == 0)
                return null;
            final int numHitDice = stats.get("aberration hit dice").val1().getIntValue();
            final int conMod = stats.get("constitution modifier").val1().getIntValue();
            final int dc = 10 + numHitDice / 2 + conMod;
            return new Value(dc);
        })
        .to1("special abilities long", value -> new Value(descript.replace("__DC__", value.getStringValue())));
    }
    
    private void slimerMaggots() {
        stat("slimer maggots")
        .to1("special abilities long", new Value("<b>Slimer Maggots</b><br/> TODO"));
    }
    
    private void brainleechWorms() {
        stat("brainleech worms")
        .to1("special abilities long", new Value("<b>Brainleech Worms</b><br/> TODO"));
    }
    
}
