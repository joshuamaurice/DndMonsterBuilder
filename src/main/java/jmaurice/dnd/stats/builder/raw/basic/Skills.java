package jmaurice.dnd.stats.builder.raw.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Skills extends BaseBuilder {

    public Skills(final Stats stats) { super(stats); }

    public void build() {
        final List<String> strSkills = Arrays.asList(
                "climb", "swim"
                );
        final List<String> dexSkills = Arrays.asList(
                "acrobatics", "disable device", "escape artist", "fly", "ride", "sleight of hand", "stealth"
                );
        final List<String> conSkills = Arrays.asList();
        final List<String> intSkills = Arrays.asList(
                "appraise", "linguistics", "psycraft", "spellcraft", //TODO craft
                "knowledge arcana",  "knowledge dungeoneering", "knowledge engineering", "knowledge geography", 
                "knowledge history", "knowledge local",         "knowledge nature",      "knowledge nobility",
                "knowledge planes",  "knowledge psionics",      "knowledge tactics"
                );
        final List<String> wisSkills = Arrays.asList(
                "heal", "perception", "sense motive", "survival" //TODO profession
                );
        final List<String> chaSkills = Arrays.asList(
                "bluff", "diplomacy", "disguise", "handle animal", "intimidate", "use magic device" //TODO perform
                );
        final List<String> armorCheckPenaltySkills = Arrays.asList(
                "acrobatics", "climb", "disable device", "escape artist", "fly", "ride", "sleight of hand", "stealth", "swim"
                );
        
        final List<String> allSkills = new ArrayList<>();
        allSkills.addAll(strSkills);
        allSkills.addAll(dexSkills);
        allSkills.addAll(conSkills);
        allSkills.addAll(intSkills);
        allSkills.addAll(wisSkills);
        allSkills.addAll(chaSkills);
        
        aggN("skills", leaf, values -> sort(values));
        allSkills.forEach(skill -> to1("skills", skill, value -> new Value(skill + " " + withSign(value.getIntValue()), value.source)));
        
        aggN("trained skills", leaf, values -> sort(values));
        allSkills.forEach(skill -> stats.input("trained skills", Arrays.asList(skill, skill + " ranks"), stats -> {
            final Integer skillRanks = stats.get(skill + " ranks").getIntValue();
            if (skillRanks == null)
                return null;
            final Value skillMod = stats.get(skill).val1();
            return Collections.singletonList(new Value(skill + " " + withSign(skillMod.getIntValue()), skillMod.source));
        }));
        
        allSkills.forEach(skill -> agg(skill + " class skill", root, input -> new Value(3, first(input).source))); //TODO remove root
        allSkills.forEach(skill -> agg(skill + " ranks", root, input -> sumAsInts(input)));
        allSkills.forEach(skill -> agg(skill, input -> sumAsInts(input)));
        
        allSkills.forEach(skill -> agg("max " + skill + " ranks", root));
        allSkills.forEach(skill -> stats.input(skill + " ranks", Arrays.asList("max " + skill + " ranks", "num hit dice"), stats -> {
            if (stats.get("max " + skill + " ranks").getValues().size() > 0)
                return Collections.singletonList(new Value(stats.get("num hit dice").val1().getIntValue()));
            return null;
        }));
        
        allSkills.forEach(skill -> to1(skill, skill + " ranks",       input -> input.asInt().source("ranks")));
        allSkills.forEach(skill -> to1(skill, skill + " class skill", input -> new Value(3, input.source + " class skill")));
        strSkills.forEach(skill -> to1(skill, "strength modifier"));
        dexSkills.forEach(skill -> to1(skill, "dexterity modifier"));
        conSkills.forEach(skill -> to1(skill, "constitution modifier"));
        intSkills.forEach(skill -> to1(skill, "intelligence modifier"));
        wisSkills.forEach(skill -> to1(skill, "wisdom modifier"));
        chaSkills.forEach(skill -> to1(skill, "charisma modifier"));
        
        to1("fly", "size modifier to fly");
        to1("stealth", "size modifier to stealth");
    }

}
