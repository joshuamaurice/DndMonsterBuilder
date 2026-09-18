package jmaurice.dnd.stats.builder.raw.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.ReadOnlyValuedStat;
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
            "racial skill bonuses",
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
        stat("stat block 3.5 short-form").agg(leaf);
        input1("stat block 3.5 short-form", inputStatNames, stats -> {
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
            r.append("; Full Atk ").append(join(stats.get("attack routine")).map(x -> x.getStringValue()).orElse(null));
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
            sortS(specialAbilitiesShort, stripHyperLinkS);
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
        stat("stat block PFSRD").agg(leaf);
        input1("stat block PFSRD", inputStatNames, stats -> {
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
            Optional.ofNullable(stats.get("initiative").val1().source).map(x -> x.isBlank() ? null : x)
                    .ifPresent(x -> r.append(" <i>(").append(x).append(")</i>"));
            join(stats.get("senses")).ifPresent(x -> r.append("; <b>Senses</b> ").append(x.getStringValue()));
            join(stats.get("auras")).ifPresent(x -> r.append("; <b>Aura</b> ").append(x.getStringValue()));
            r.append("<br/>");
            
            r.append("<span style=\"color: purple;\"><i>DEFENSE</i></span>");
            r.append("<br/>");
            
            r.append("<b>AC</b> ").append(stats.get("armor class").getIntValue());
            r.append(",");
            r.append(" touch ").append(stats.get("touch armor class").getIntValue());
            r.append(",");
            r.append(" flat-footed ").append(stats.get("flat-footed armor class").getIntValue());
            stats.get("armor class").val01().map(x -> x.source).ifPresent(x -> r.append(" <i>(").append(x).append(")</i>"));
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
            join(sort(stats.get("defensive abilities"), stripHyperLink)).ifPresent(x -> defenses2.append("; <b>Defensive Abilities</b> ").append(x.getStringValue()));
            join(sort(stats.get("immunities"), stripHyperLink)).ifPresent(x -> defenses2.append("; <b>Immune</b> ").append(x.getStringValue()));
            joinWithType(sort(stats.get("resistances"), x -> x.type)).ifPresent(x -> defenses2.append("; <b>Resist</b> ").append(x.getStringValue()));
            if ( ! defenses2.isEmpty()) {
                r.append(defenses2.toString().substring(2));
                r.append("<br/>");
            }
            
            join(sort(stats.get("weaknesses"), stripHyperLink)).ifPresent(x -> r.append("; <b>Weaknessesk</b> ").append(x.getStringValue()).append("<br/>"));
            
            r.append("<span style=\"color: purple;\"><i>OFFENSE</i></span>");
            r.append("<br/>");
            
            join(stats.get("speeds")).ifPresent(x -> r.append("<b>Spd</b> ").append(x.getStringValue()).append("<br/>"));
            
            join(stats.get("attack routine")).ifPresent(x -> r.append("<b>Full Atk</b> ").append(x.getStringValue()).append("<br/>"));
            
            r.append("<b>Space</b> ").append(stats.get("space").getStringValue());
            r.append("; <b>Reach</b> ").append(stats.get("reach").getStringValue());
            r.append("<br/>");
            
            join(sort(stats.get("special abilities short").getValues(), stripHyperLink)).ifPresent(x -> r.append("<b>Special Abilities</b> ").append(x.getStringValue()).append("<br/>"));
            
            //TODO SLAs
            
            //TODO spells
            
            r.append("<span style=\"color: purple;\"><i>STATISTICS</i></span>");
            r.append("<br/>");
            
            for (int i = 0; i < 6; ++i) {
                final String statName = Arrays.asList("strength", "dexterity", "constitution", "intelligence", "wisdom", "charisma").get(i);
                final String outputName = Arrays.asList("Str", "Dex", "Con", "Int", "Wis", "Cha").get(i);
                final Value statValue = stats.get(statName).val01().orElse(null);
                r.append("<b>").append(outputName).append("</b> ");
                if (statValue != null) {
                    r.append(statValue.getIntValue());
                    if (statValue.source != null && ! statValue.source.isBlank()) {
                        r.append(" <i>(").append(statValue.source).append(")</i>");
                    }
                } else {
                    r.append("-");
                }
                r.append("<br/>");
            }
            
            r.append("<b>Base Atk</b> ").append(withSign(
                    stats.get("base attack bonus").getIntValue()
                    + stats.get("epic base attack bonus").val01().map(x -> x.getIntValue()).orElse(0)));
            r.append("; <b>CMB</b> ").append(withSign(stats.get("combat maneuvers bonus").getIntValue()));
            r.append("; <b>CMD</b> ").append(stats.get("combat maneuvers defense").getIntValue());
            r.append("<br/>");
            
            join(stats.get("feats")).ifPresent(x -> r.append("<b>Feats</b> ").append(x.getStringValue()).append("<br/>"));
            join(stats.get("epic feats")).ifPresent(x -> r.append("<b>Epic Feats</b> ").append(x.getStringValue()).append("<br/>"));
            
            r.append("<b>Skills</b> ").append(join(stats.get("relevant skills")).get().getStringValue());
            join(stats.get("racial skill bonuses")).ifPresent(x -> r.append("; <b>Racial</b> ").append(x.getStringValue()));
            r.append("<br/>");
            
            join(stats.get("languages")).ifPresent(x -> r.append("<b>Languages</b> ").append(x.getStringValue()).append("<br/>"));
            join(stats.get("equipment")).ifPresent(x -> r.append("<b>Equipment</b> ").append(x.getStringValue()).append("<br/>"));

            if (stats.get("special abilities long").getValues().size() > 0) {
                r.append("<span style=\"color: purple;\"><i>SPECIAL ABILITIES</i></span>");
                r.append("<br/>");
                for (final Value value : sort(stats.get("special abilities long").getValues(), x -> x.getStringValue())) {
                    final String s = value.getStringValue()
                            .replaceAll("<li> *", "<li>")
                            .replaceAll("</li> *", "</li>")
                            .replaceAll("<ul> *", "<ul>")
                            .replaceAll("</ul> *</li>", "</ul></li>")
                            .replaceAll("<br/> {4,}", "<br>&nbsp;&nbsp;&nbsp;&nbsp;")
                            .replaceAll("</ul> {4,}", "</ul>&nbsp;&nbsp;&nbsp;&nbsp;")
                            .replaceAll("([^> ]) +", "$1 ")
                            .replaceAll("^ *", "")
                            .replaceAll(" *$", "")
                            ;
                    r.append(s);
                    if ( ! s.endsWith("<br/>") && ! s.endsWith("</ul>"))
                        r.append("<br/>");
                }
            }
            
            final String r2 = r.toString();
            return new Value(r2.substring(0, r2.length() - 5));
        });
    }
    
    protected static Optional<Value> join(List<Value> values) {
        return join(values, ", ");
    }
    
    protected static Optional<Value> join(List<Value> values, String delimiter) {
        if (values.isEmpty())
            return Optional.empty();
        final StringBuilder r = new StringBuilder();
        for (Value value : values) {
            if ( ! r.isEmpty())
                r.append(delimiter);
            r.append(value.getStringValue().trim());
        }
        final String r2 = r.toString().replaceAll("^( *,)* *", "").replaceAll("( *,)* *$", "");
        if (r2.isEmpty())
            return Optional.empty();
        return Optional.of(new Value(r2));
    }

    protected static Optional<Value> joinWithSource(List<Value> values) {
        return joinWithSource(values, ", ");
    }
    
    protected static Optional<Value> joinWithSource(List<Value> values, String delimiter) {
        if (values.isEmpty())
            return Optional.empty();
        final StringBuilder r = new StringBuilder();
        for (Value value : values) {
            if ( ! r.isEmpty())
                r.append(delimiter);
            r.append(value.getStringValue().trim());
            if (value.source != null && ! value.source.isBlank() && ! value.source.equals("default"))
                r.append(" ").append(value.source);
            else
                r.append(" ").append(value.type);
        }
        final String r2 = r.toString().replaceAll("^( *,)* *", "").replaceAll("( *,)* *$", "");
        if (r2.isEmpty())
            return Optional.empty();
        return Optional.of(new Value(r2));
    }

    protected static Optional<Value> joinWithType(List<Value> values) {
        return joinWithType(values, ", ");
    }
    
    protected static Optional<Value> joinWithType(List<Value> values, String delimiter) {
        if (values.isEmpty())
            return Optional.empty();
        final StringBuilder r = new StringBuilder();
        for (Value value : values) {
            if ( ! r.isEmpty())
                r.append(delimiter);
            r.append(value.getStringValue().trim());
            r.append(" ").append(value.type);
        }
        final String r2 = r.toString().replaceAll("^( *,)* *", "").replaceAll("( *,)* *$", "");
        if (r2.isEmpty())
            return Optional.empty();
        return Optional.of(new Value(r2));
    }

    protected Optional<Value> join(ReadOnlyValuedStat stat) {
        return join(stat.getValues(), ", ");
    }
    
    protected Optional<Value> join(ReadOnlyValuedStat stat, String delimiter) {
        return join(stat.getValues(), delimiter);
    }
    
}
