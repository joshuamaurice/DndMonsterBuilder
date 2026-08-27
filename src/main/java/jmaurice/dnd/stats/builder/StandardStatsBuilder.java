package jmaurice.dnd.stats.builder;

import jmaurice.dnd.stats.builder.homebrew.tyranids.Tyranids;
import jmaurice.dnd.stats.builder.raw.basic.AbilityScores;
import jmaurice.dnd.stats.builder.raw.basic.ArmorClass;
import jmaurice.dnd.stats.builder.raw.basic.AttackRoutine;
import jmaurice.dnd.stats.builder.raw.basic.Feats;
import jmaurice.dnd.stats.builder.raw.basic.HitDiceHitPoints;
import jmaurice.dnd.stats.builder.raw.basic.MiscBasics;
import jmaurice.dnd.stats.builder.raw.basic.NaturalWeapons;
import jmaurice.dnd.stats.builder.raw.basic.Saves;
import jmaurice.dnd.stats.builder.raw.basic.SizeSpaceReach;
import jmaurice.dnd.stats.builder.raw.basic.Skills;
import jmaurice.dnd.stats.builder.raw.basic.Speeds;
import jmaurice.dnd.stats.builder.raw.creaturetypes.Aberrations;
import jmaurice.dnd.stats.builder.raw.creaturetypes.Constructs;
import jmaurice.dnd.stats.impl.Stats;

public class StandardStatsBuilder {
    
    private Stats stats;
    
    public StandardStatsBuilder() {}
    
    public Stats build() {
        stats = new Stats();
        stats.getOrCreateStat("default").setRoot(true);
        
        //RAW basics
        new AbilityScores(stats).build();
        new ArmorClass(stats).build();
        new AttackRoutine(stats).build();
        new Feats(stats).build();
        new HitDiceHitPoints(stats).build();
        new MiscBasics(stats).build();
        new NaturalWeapons(stats).build();
        new Saves(stats).build();
        new SizeSpaceReach(stats).build();
        new Skills(stats).build();
        new Speeds(stats).build();
        
        //RAW creature types
        new Aberrations(stats).build();
        new Constructs(stats).build();
        
        //homebrew
        new Tyranids(stats).build();
        
        //
        stats.initializeGraphAndInverseGraph();
        return stats;
    }
    
}
