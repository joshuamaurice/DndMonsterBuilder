package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class SizeSpaceReach extends BaseBuilder {

    public SizeSpaceReach(final Stats stats) { super(stats); }

    public void build() {
        agg("size", rootleaf);
        to1("size modifier to attack", "size", value -> {
            final int x = switch (value.getStringValue()) {
                case "fine"       -> 8;
                case "diminutive" -> 4;
                case "tiny"       -> 2;
                case "small"      -> 1;
                case "medium"     -> 0;
                case "large"      -> -1;
                case "huge"       -> -2;
                case "gargantuan" -> -4;
                case "colossal"   -> -8;
                default -> throw new RuntimeException("unrecognized size value >>" + value + "<<");
            };
            return new Value(x, "size");
        });
        
        to1("size modifier to fly", "size", value -> {
            final int x = switch (value.getStringValue()) {
                case "fine"       -> 8;
                case "diminutive" -> 6;
                case "tiny"       -> 4;
                case "small"      -> 2;
                case "medium"     -> 0;
                case "large"      -> -2;
                case "huge"       -> -4;
                case "gargantuan" -> -6;
                case "colossal"   -> -8;
                default -> throw new RuntimeException("unrecognized size value >>" + value + "<<");
            };
            return new Value(x, "size");
        });
        to1("size modifier to stealth", "size", value -> {
            final int x = switch (value.getStringValue()) {
                case "fine"       -> 16;
                case "diminutive" -> 12;
                case "tiny"       -> 8;
                case "small"      -> 4;
                case "medium"     -> 0;
                case "large"      -> -4;
                case "huge"       -> -8;
                case "gargantuan" -> -12;
                case "colossal"   -> -16;
                default -> throw new RuntimeException("unrecognized size value >>" + value + "<<");
            };
            return new Value(x, "size");
        });
        
        agg("space", leaf);
        to1("space", "size", value -> {
            final String space = switch (value.getStringValue()) {
                case "fine"       -> "1/2";
                case "diminutive" -> "1";
                case "tiny"       -> "2-1/2";
                case "small"      -> "5";
                case "medium"     -> "5";
                case "large"      -> "10";
                case "huge"       -> "15";
                case "gargantuan" -> "20";
                case "colossal"   -> "30";
                default -> throw new RuntimeException("unrecognized size value >>" + value + "<<");
            };
            return new Value(space + " ft");
        });
        
        agg("reach", leaf);
        agg("short creature reach", root);
        input("reach", Arrays.asList("size", "short creature reach"), stats -> {
            final String size = stats.get("size").val1().getStringValue();
            final boolean shortCreatureReach = stats.get("short creature reach").val01().map(x -> true).orElse(false);
            final String reach = switch (size) {
                case "fine"       -> "0";
                case "diminutive" -> "0";
                case "tiny"       -> "0";
                case "small"      -> "5";
                case "medium"     -> "5";
                case "large"      -> shortCreatureReach ? "5" : "10";
                case "huge"       -> shortCreatureReach ? "10" : "15";
                case "gargantuan" -> shortCreatureReach ? "15" : "20";
                case "colossal"   -> shortCreatureReach ? "20" : "30";
                default -> throw new RuntimeException("unrecognized size value >>" + size + "<<");
            };
            return new Value(reach + " ft");
        });
    }

}
