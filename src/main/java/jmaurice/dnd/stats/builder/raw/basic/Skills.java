package jmaurice.dnd.stats.builder.raw.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;
import jmaurice.dnd.stats.impl.ValuedStat;

public class Skills extends BaseBuilder {
    
    public static final List<String> strSkills = Arrays.asList(
            "climb", "swim"
            );
    public static final List<String> dexSkills = Arrays.asList(
            "acrobatics", "disable device", "escape artist", "fly", "ride", "sleight of hand", "stealth"
            );
    public static final List<String> conSkills = Arrays.asList();
    public static final List<String> intSkills = Arrays.asList(
            "appraise", "linguistics", "psycraft", "spellcraft", //TODO craft
            "knowledge arcana",  "knowledge dungeoneering", "knowledge engineering", "knowledge geography", 
            "knowledge history", "knowledge local",         "knowledge nature",      "knowledge nobility",
            "knowledge planes",  "knowledge religion",      "knowledge psionics",    "knowledge tactics"
            );
    public static final List<String> wisSkills = Arrays.asList(
            "heal", "perception", "sense motive", "survival" //TODO profession
            );
    public static final List<String> chaSkills = Arrays.asList(
            "bluff", "diplomacy", "disguise", "handle animal", "intimidate", "use magic device" //TODO perform
            );
    public static final List<String> armorCheckPenaltySkills = Arrays.asList(
            "acrobatics", "climb", "disable device", "escape artist", "fly", "ride", "sleight of hand", "stealth", "swim"
            );
    
    public static final List<String> allSkills = new ArrayList<>();
    static {
        allSkills.addAll(strSkills);
        allSkills.addAll(dexSkills);
        allSkills.addAll(conSkills);
        allSkills.addAll(intSkills);
        allSkills.addAll(wisSkills);
        allSkills.addAll(chaSkills);
    }

    public Skills(final Stats stats) { super(stats); }

    public void build() {
        for (final String skill : allSkills) {
            stat("default")
            .to1(skill, new Value(0))
            .agg(input -> sumAsInts(input))
            .to1("skills", value -> new Value(skill + " " + withSign(value.getIntValue())));
        }
        stat("skills").aggN(leaf, values -> sort(values));
        
        stat("size modifier to fly").to1("fly");
        stat("size modifier to stealth").to1("stealth");
        relevantSkills();
        skillRanks();
        maxSkillRanks();
        classSkill();
        abilityScores();
        otherBonuses();
        racialSkillBonuses();
    }
    
    private void relevantSkills() {
        stat("relevant skills").aggN(values -> sort(values));
        for (final String skill : allSkills) {
            input1("relevant skills", Arrays.asList(skill, skill + " ranks"), stats -> {
                final Value skillMod = stats.get(skill).val1();
                final Integer skillRanks = stats.get(skill + " ranks").getIntValue();
                if (skillRanks != null)
                    return new Value(skill + " " + withSign(skillMod.getIntValue()));
                if (Arrays.asList("perception", "stealth").contains(skill))
                    return new Value(skill + " " + withSign(skillMod.getIntValue()));
                return null;
            });
        }
    }
    
    private void skillRanks() {
        for (final String skill : allSkills) {
            stat(skill + " ranks")
            .agg(root, input -> sumAsInts(input))
            .to1(skill, input -> input.asInt().source("ranks"));
        }
    }
    
    private void maxSkillRanks() {
        for (final String skill : allSkills) {
            stat("max " + skill + " ranks").agg(root);
            input1(skill + " ranks", Arrays.asList("max " + skill + " ranks", "num hit dice"), stats -> {
                if (stats.get("max " + skill + " ranks").getValues().size() > 0)
                    return new Value(stats.get("num hit dice").val1().getIntValue());
                return null;
            });
        }
    }
    
    private void classSkill() {
        for (final String skill : allSkills) {
            stat(skill + " class skill")
            .agg(root, input -> new Value(3).source(first(input).source)) //TODO remove root
            .to1(skill, input -> new Value(3).source(input.source + " class skill"));
        }
    }
    
    private void abilityScores() {
        strSkills.forEach(skill -> stat("strength modifier"    ).to1(skill));
        dexSkills.forEach(skill -> stat("dexterity modifier"   ).to1(skill));
        conSkills.forEach(skill -> stat("constitution modifier").to1(skill));
        intSkills.forEach(skill -> stat("intelligence modifier").to1(skill));
        wisSkills.forEach(skill -> stat("wisdom modifier"      ).to1(skill));
        chaSkills.forEach(skill -> stat("charisma modifier"    ).to1(skill));
    }
    
    private void otherBonuses() {
        stat("skills bonus").aggN(root);
        for (final String skill : allSkills) {
            stat("skills bonus")
            .toN(skill + " bonus")
            .aggN()
            .toN(skill);
        }
    }
    
    private void racialSkillBonuses() {
        stat("racial skill bonuses").aggN(root, values -> sort(values));
        for (final String skill : allSkills) {
            stats.preAggX(skill, Arrays.asList("racial skill bonuses"), Arrays.asList(), (writableStats, readOnlyStats) -> {
                final Value v = maxAsInts(writableStats.get(skill).getValues().stream().filter(x -> x.type.equals("racial")).toList());
                if (v != null) {
                    final ValuedStat racialSkillBonusesStat = writableStats.get("racial skill bonuses");
                    final List<Value> racialSkillBonuses = new ArrayList<>(racialSkillBonusesStat.getValues());
                    racialSkillBonuses.add(new Value(skill + " " + withSign(v.getIntValue())));
                    racialSkillBonusesStat.setValues(racialSkillBonuses);
                }
            });
        }
    }

}
