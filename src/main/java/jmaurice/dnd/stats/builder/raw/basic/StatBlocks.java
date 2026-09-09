package jmaurice.dnd.stats.builder.raw.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class StatBlocks extends BaseBuilder {

    private static final List<String> inputStatNames = new ArrayList<>(Arrays.asList(new String[] {
            "alignment",
            "armor class",
            "attack routine",
            "base attack bonus",
            "challenge rating",
            "charisma",
            "combat maneuvers bonus",
            "combat maneuvers defense",
            "constitution",
            "creature subtypes2",
            "creature type",
            "defensive abilities",
            "dexterity",
            "epic base attack bonus",
            "equipment2",
            "feats2",
            "flat-footed armor class",
            "fortitude",
            "gender",
            "hit dice",
            "hit points",
            "immunities",
            "initiative",
            "intelligence",
            "name",
            "natural armor bonus",
            "race",
            "reach",
            "reflex",
            "resistances",
            "senses",
            "size",
            "strength",
            "space",
            "special abilities long",
            "special abilities short",
            "speeds",
            "templates",
            "touch armor class",
            "trained skills2",
            "weaknesses",
            "will",
            "wisdom",
            }));
    
    public StatBlocks(final Stats stats) { super(stats); }

    public void build() {
        statBlock35ShortForm();
    }
    
    private void statBlock35ShortForm() {
        agg("stat block 3.5 short-form", leaf);
        input("stat block 3.5 short-form", inputStatNames, stats -> {
            final StringBuilder r = new StringBuilder();
            
            //TODO class levels
            
            r.append("<b>");
            r.append(stats.get("name").getStringValue());
            r.append(" (CR " + stats.get("challenge rating").getIntValue() + ")");
            r.append(":</b>");
            
            stats.get("alignment").val01().ifPresent(v -> r.append(" ").append(v.getStringValue()));
            stats.get("gender").val01().ifPresent(v -> r.append(" ").append(v.getStringValue()));
            stats.get("templates").val01().ifPresent(v -> r.append(" ").append(v.getStringValue()));
            r.append(" ").append(stats.get("race").getStringValue());
            r.append(";");
            r.append(" ").append(stats.get("size").getStringValue());
            stats.get("creature type").val01().ifPresent(v -> r.append(" ").append(v.getStringValue()));
            stats.get("creature subtypes2").val01().ifPresent(v -> r.append(" (").append(v.getStringValue()).append(")"));
            r.append(";");
            r.append(" HD ").append(stats.get("hit dice").getStringValue());
            r.append(",");
            r.append(" hp").append(stats.get("hit points").getIntValue());
            r.append(";");
            r.append(" Init ").append(withSign(stats.get("initiative").getIntValue()));
            r.append(";");
            r.append(" Spd ").append(stats.get("speeds").getStringValue());
            r.append(";");
            r.append(" AC ").append(stats.get("armor class").getIntValue());
            if (stats.get("natural armor bonus").val01().filter(x -> x.getIntValue() != 0).map(x -> true).orElse(false))
                r.append(" (").append(stats.get("natural armor bonus").getIntValue()).append(")");
            r.append(",");
            r.append(" touch ").append(stats.get("touch armor class").getIntValue());
            r.append(",");
            r.append(" flat-footed ").append(stats.get("flat-footed armor class").getIntValue());
            r.append(",");
            r.append(" CMD ").append(stats.get("combat maneuvers defense").getIntValue());
            r.append(";");
            if (stats.get("base attack bonus").val01().isPresent()) {
                r.append(" Base Atk ").append(withSign(
                        stats.get("base attack bonus").getIntValue()
                        + stats.get("epic base attack bonus").val01().map(x -> x.getIntValue()).orElse(0)));
                r.append(",");
            }
            r.append(" CMB ").append(withSign(stats.get("combat maneuvers bonus").getIntValue()));
            r.append(";");
            r.append(" Full Atk ").append(stats.get("attack routine").getStringValue());
            r.append(";");
            r.append(" Space/Reach ");
            r.append(stats.get("space").getStringValue());
            r.append("/");
            r.append(stats.get("reach").getStringValue());
            r.append(";");
            r.append(" SA ");
            
            final List<String> specialAbilitiesShort = new ArrayList<>();
            specialAbilitiesShort.addAll(stats.get("special abilities short").getValues().stream().map(x -> x.getStringValue()).toList());
            specialAbilitiesShort.addAll(stats.get("senses").getValues().stream().map(x -> x.getStringValue()).toList());
            specialAbilitiesShort.addAll(stats.get("defensive abilities").getValues().stream().map(x -> x.getStringValue()).toList());
            if (stats.get("immunities").getValues().size() > 0) {
                specialAbilitiesShort.add("immune (" 
                        + stats.get("immunities").getValues().stream().map(x -> x.getStringValue()).collect(Collectors.joining(", "))
                        + ")");
            }
            if (stats.get("resistances").getValues().size() > 0) {
                specialAbilitiesShort.add("resist (" 
                        + stats.get("resistances").getValues().stream().map(x -> x.getStringValue()).collect(Collectors.joining(", "))
                        + ")");
            }
            if (stats.get("weaknesses").getValues().size() > 0) {
                specialAbilitiesShort.add("weak (" 
                        + stats.get("weaknesses").getValues().stream().map(x -> x.getStringValue()).collect(Collectors.joining(", "))
                        + ")");
            }
            Collections.sort(specialAbilitiesShort);
            r.append(specialAbilitiesShort.stream().collect(Collectors.joining(", ")));
            
            r.append(";");
            r.append(" SV");
            r.append(" fort ").append(withSign(stats.get("fortitude").getIntValue()));
            r.append(",");
            r.append(" ref ").append(withSign(stats.get("reflex").getIntValue()));
            r.append(",");
            r.append(" will ").append(withSign(stats.get("will").getIntValue()));
            r.append(";");
            r.append(" Str ").append(stats.get("strength").getIntValue());
            r.append(",");
            r.append(" Dex ").append(stats.get("dexterity").getIntValue());
            r.append(",");
            r.append(" Con ").append(stats.get("constitution").getIntValue());
            r.append(",");
            r.append(" Int ").append(stats.get("intelligence").getIntValue());
            r.append(",");
            r.append(" Wis ").append(stats.get("wisdom").getIntValue());
            r.append(",");
            r.append(" Cha ").append(stats.get("charisma").getIntValue());
            r.append(".");
            
            r.append("<br/>");
            
            r.append("    <i>skills and feats:</i> ");
            r.append(stats.get("trained skills2").getStringValue());
            r.append("; ");
            r.append(stats.get("feats2").getStringValue());
            r.append(".");
            
            final List<String> specialAbilitiesLong = new ArrayList<>();
            specialAbilitiesLong.addAll(stats.get("special abilities long").getValues().stream().map(x -> x.getStringValue()).toList());
            stats.get("equipment2").val01().ifPresent(x -> specialAbilitiesLong.add("<b>equipment:</b> " + x.getStringValue()));
            if (specialAbilitiesLong.size() > 0) {
                r.append("<br/>    ");
                r.append(specialAbilitiesLong.stream().collect(Collectors.joining("<br/>    ")));
            }
            
            return new Value(r.toString());
        });
    }

}
