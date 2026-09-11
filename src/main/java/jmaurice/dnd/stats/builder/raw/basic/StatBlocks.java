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
            "auras",
            "base attack bonus",
            "challenge rating",
            "charisma",
            "combat maneuvers bonus",
            "combat maneuvers defense",
            "constitution",
            "creature type",
            "creature subtypes",
            "damage reduction",
            "defensive abilities",
            "dexterity",
            "divine rank",
            "epic base attack bonus",
            "epic feats",
            "equipment",
            "feats",
            "flat-footed armor class",
            "fortitude",
            "gender",
            "hit dice",
            "hit points",
            "immunities",
            "initiative",
            "intelligence",
            "languages",
            "mythic rank",
            "name",
            "natural armor bonus",
            "power resistance",
            "race",
            "reach",
            "reflex",
            "relevant skills",
            "resistances",
            "senses",
            "size",
            "spell resistance",
            "strength",
            "space",
            "special abilities long",
            "special abilities short",
            "speeds",
            "templates",
            "touch armor class",
            "weaknesses",
            "will",
            "wisdom",
            }));
    
    public StatBlocks(final Stats stats) { super(stats); }

    public void build() {
        statBlock35ShortForm();
        statBlockPFSRD();
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
            r.append(" ");
            if (stats.get("race").getValues().size() > 0)
                r.append(stats.get("race").getStringValue());
            else
                r.append(stats.get("creature type").getStringValue());
            r.append("; ");
            r.append(stats.get("size").getStringValue());
            stats.get("creature type").val01().ifPresent(v -> r.append(" ").append(v.getStringValue()));
            join(stats.get("creature subtypes")).ifPresent(x -> r.append(" (").append(x.getStringValue()).append(")"));
            r.append("; HD ").append(stats.get("hit dice").getStringValue());
            r.append(", hp ").append(stats.get("hit points").getIntValue());
            r.append("; Init ").append(withSign(stats.get("initiative").getIntValue()));
            join(stats.get("speeds")).ifPresent(x -> r.append("; Spd ").append(x.getStringValue()));
            r.append("; AC ").append(stats.get("armor class").getIntValue());
            if (stats.get("natural armor bonus").val01().filter(x -> x.getIntValue() != 0).map(x -> true).orElse(false))
                r.append(" (").append(stats.get("natural armor bonus").getIntValue()).append(")");
            r.append(", touch ").append(stats.get("touch armor class").getIntValue());
            r.append(", flat-footed ").append(stats.get("flat-footed armor class").getIntValue());
            r.append(", CMD ").append(stats.get("combat maneuvers defense").getIntValue());
            r.append("; Base Atk ");
            r.append(withSign(
                    stats.get("base attack bonus").getIntValue()
                    + stats.get("epic base attack bonus").val01().map(x -> x.getIntValue()).orElse(0)));
            r.append(", CMB ").append(withSign(stats.get("combat maneuvers bonus").getIntValue()));
            r.append("; Full Atk ").append(stats.get("attack routine").getStringValue());
            r.append("; Space/Reach ");
            r.append(stats.get("space").getStringValue());
            r.append("/");
            r.append(stats.get("reach").getStringValue());
            r.append("; SA ");
            
            final List<String> specialAbilitiesShort = new ArrayList<>();
            stats.get("power resistance").val01().ifPresent(x -> specialAbilitiesShort.add("PR " + x.getIntValue()));
            stats.get("spell resistance").val01().ifPresent(x -> specialAbilitiesShort.add("SR " + x.getIntValue()));
            specialAbilitiesShort.addAll(stats.get("defensive abilities").getValues().stream().map(x -> x.getStringValue()).toList());
            specialAbilitiesShort.addAll(stats.get("senses").getValues().stream().map(x -> x.getStringValue()).toList());
            specialAbilitiesShort.addAll(stats.get("special abilities short").getValues().stream().map(x -> x.getStringValue()).toList());
            join(stats.get("immunities")).ifPresent(x -> r.append("immune (").append(x.getStringValue()).append(")"));
            join(stats.get("resistances")).ifPresent(x -> r.append("resist (").append(x.getStringValue()).append(")"));
            join(stats.get("weaknesses")).ifPresent(x -> r.append("weaknesses (").append(x.getStringValue()).append(")"));
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
            r.append(stats.get("relevant skills").getValues().stream().map(x -> x.getStringValue()).collect(Collectors.joining(", ")));
            r.append("; ");
            r.append(stats.get("feats").getValues().stream().map(x -> x.getStringValue()).collect(Collectors.joining(", ")));
            r.append(".");
            
            final List<String> specialAbilitiesLong = new ArrayList<>();
            specialAbilitiesLong.addAll(stats.get("special abilities long").getValues().stream().map(x -> x.getStringValue()).toList());
            join(stats.get("equipment")).ifPresent(x -> specialAbilitiesLong.add("<b>equipment:</b> " + x.getStringValue()));
            if (specialAbilitiesLong.size() > 0) {
                r.append("<br/>    ");
                r.append(specialAbilitiesLong.stream().collect(Collectors.joining("<br/>    ")));
            }
            
            return new Value(r.toString());
        });
    }
    
    private void statBlockPFSRD() {
        agg("stat block PFSRD", leaf);
        input("stat block PFSRD", inputStatNames, stats -> {
            final StringBuilder r = new StringBuilder();
            
            //TODO class levels
            
            r.append("<b>");
            r.append(stats.get("name").getStringValue());
            r.append(" (CR " + stats.get("challenge rating").getIntValue());
            stats.get("divine rank").val01().ifPresent(x -> r.append("/DR " + x.getStringValue()));
            stats.get("mythic rank").val01().ifPresent(x -> r.append("/MR " + x.getStringValue()));
            r.append(")");
            r.append("</b>");
            r.append("<br/>");
            
            stats.get("alignment").val01().ifPresent(v -> r.append(v.getStringValue()));
            r.append(" ");
            r.append(stats.get("size").getStringValue());
            stats.get("templates").val01().ifPresent(v -> r.append(" ").append(v.getStringValue()));
            stats.get("creature type").val01().ifPresent(v -> r.append(" ").append(v.getStringValue()));
            join(stats.get("creature subtypes")).ifPresent(x -> r.append(" (").append(x.getStringValue()).append(")"));
            r.append("<br/>");
            
            r.append("<b>Init</b> ").append(withSign(stats.get("initiative").getIntValue()));
            r.append(" (");
            r.append(stats.get("initiative").val1().source);
            r.append(")");
            join(stats.get("senses")).ifPresent(x -> r.append("; <b>Senses</b> ").append(x.getStringValue()));
            join(stats.get("auras")).ifPresent(x -> r.append("; <b>Aura</b> ").append(x.getStringValue()));
            r.append("<br/>");
            
            r.append("<span style=\"color: blue;\"><i>DEFENSE</i></span>");
            r.append("<br/>");
            
            r.append("<b>AC</b> ").append(stats.get("armor class").getIntValue());
            r.append(",");
            r.append(" touch ").append(stats.get("touch armor class").getIntValue());
            r.append(",");
            r.append(" flat-footed ").append(stats.get("flat-footed armor class").getIntValue());
            r.append(" (");
            r.append(stats.get("armor class").val1().source);
            r.append(")");
            r.append("<br/>");
            
            r.append("<b>hp</b> ").append(stats.get("hit points").getIntValue());
            r.append(" (").append(stats.get("hit dice").getStringValue()).append(")");
            r.append("<br/>");

            r.append("<b>Fort</b> ").append(withSign(stats.get("fortitude").getIntValue()));
            r.append(",");
            r.append(" <b>Ref</b> ").append(withSign(stats.get("reflex").getIntValue()));
            r.append(",");
            r.append(" <b>Will</b> ").append(withSign(stats.get("will").getIntValue()));
            r.append("<br/>");
            
            final StringBuilder defenses1 = new StringBuilder();
            join(stats.get("damage reduction")).ifPresent(x -> defenses1.append("; <b>DR</b> ").append(x.getStringValue()));
            stats.get("power resistance").val01().ifPresent(x -> defenses1.append("; <b>PR</b> " + x.getIntValue()));
            stats.get("spell resistance").val01().ifPresent(x -> defenses1.append("; <b>SR</b> " + x.getIntValue()));
            if ( ! defenses1.isEmpty()) {
                r.append(defenses1.toString().substring(2));
                r.append("<br/>");
            }
            
            final StringBuilder defenses2 = new StringBuilder();
            join(stats.get("defensive abilities")).ifPresent(x -> defenses2.append("; <b>Defensive Abilities</b> ").append(x.getStringValue()));
            join(stats.get("immunities")).ifPresent(x -> defenses2.append("; <b>Immune</b> ").append(x.getStringValue()));
            join(stats.get("resistances")).ifPresent(x -> defenses2.append("; <b>Resist</b> ").append(x.getStringValue()));
            if ( ! defenses2.isEmpty()) {
                r.append(defenses2.toString().substring(2));
                r.append("<br/>");
            }
            
            join(stats.get("weaknesses")).ifPresent(x -> r.append("; <b>Weaknessesk</b> ").append(x.getStringValue()).append("<br/>"));
            
            r.append("<span style=\"color: blue;\"><i>OFFENSE</i></span>");
            r.append("<br/>");
            
            join(stats.get("speeds")).ifPresent(x -> r.append("<b>Spd</b> ").append(x.getStringValue()).append("<br/>"));
            
            join(stats.get("attack routine")).ifPresent(x -> r.append("<b>Full Atk</b> ").append(x.getStringValue()).append("<br/>"));
            
            r.append("<b>Space</b> ").append(stats.get("space").getStringValue());
            r.append("; <b>Reach</b> ").append(stats.get("reach").getStringValue());
            r.append("<br/>");
            
            join(stats.get("special abilities short")).ifPresent(x -> r.append("<b>Special Abilities</b> ").append(x.getStringValue()).append("<br/>"));
            
            //TODO SLAs
            
            //TODO spells
            
            r.append("<span style=\"color: blue;\"><i>STATISTICS</i></span>");
            r.append("<br/>");
            
            r.append("<b>Str</b> ").append(stats.get("strength").getIntValue());
            r.append(", <b>Dex</b> ").append(stats.get("dexterity").getIntValue());
            r.append(", <b>Con</b> ").append(stats.get("constitution").getIntValue());
            r.append(", <b>Int</b> ").append(stats.get("intelligence").getIntValue());
            r.append(", <b>Wis</b> ").append(stats.get("wisdom").getIntValue());
            r.append(", <b>Cha</b> ").append(stats.get("charisma").getIntValue());
            r.append("<br/>");
            
            r.append("<b>Base Atk</b> ").append(withSign(
                    stats.get("base attack bonus").getIntValue()
                    + stats.get("epic base attack bonus").val01().map(x -> x.getIntValue()).orElse(0)));
            r.append("; <b>CMB</b> ").append(withSign(stats.get("combat maneuvers bonus").getIntValue()));
            r.append("; <b>CMD</b> ").append(stats.get("combat maneuvers defense").getIntValue());
            r.append("<br/>");
            
            join(stats.get("feats")).ifPresent(x -> r.append("<b>Feats</b> ").append(x.getStringValue()).append("<br/>"));
            join(stats.get("epic feats")).ifPresent(x -> r.append("<b>Epic Feats</b> ").append(x.getStringValue()).append("<br/>"));
            join(stats.get("relevant skills")).ifPresent(x -> r.append("<b>Skills</b> ").append(x.getStringValue()).append("<br/>"));
            join(stats.get("languages")).ifPresent(x -> r.append("<b>Languages</b> ").append(x.getStringValue()).append("<br/>"));
            join(stats.get("equipment")).ifPresent(x -> r.append("<b>Equipment</b> ").append(x.getStringValue()).append("<br/>"));

            join(stats.get("special abilities long")).ifPresent(x -> {
                r.append("<span style=\"color: blue;\"><i>SPECIAL ABILITIES</i></span>");
                r.append("<br/>");
                r.append(x.getStringValue());
                r.append("<br/>");
            });
            
            final String r2 = r.toString();
            return new Value(r2.substring(0, r2.length() - 5));
        });
    }

}
