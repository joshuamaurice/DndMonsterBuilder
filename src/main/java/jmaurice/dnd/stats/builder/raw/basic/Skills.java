package jmaurice.dnd.stats.builder.raw.basic;

import java.util.ArrayList;
import java.util.Arrays;
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
                "knowledge planes",  "knowledge psionics",      "knowledgeTactics"
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
        allSkills.forEach(skill -> to1("skills", skill, value -> new Value(skill + " " + withSign(value.getIntValue()), "")));
        
        allSkills.forEach(skill -> agg(skill + " class skill", root, input -> new Value(3, first(input).source))); //TODO remove
        allSkills.forEach(skill -> agg(skill + " ranks", root, input -> sumAsInts(input)));
        allSkills.forEach(skill -> agg(skill, input -> sumAsInts(input)));
        
        allSkills.forEach(skill -> to1(skill, skill + " ranks",       input -> new Value(3, input.source + " ranks")));
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
