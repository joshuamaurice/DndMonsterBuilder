package jmaurice.dnd.stats.builder.homebrew.tyranids;

import java.util.Arrays;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class TyranidAbilityScores extends BaseBuilder {

    public TyranidAbilityScores(final Stats stats) { super(stats); }

    public void build() {
        base();
        input("intelligence", Arrays.asList("tyranid", "synapse"), stats -> {
            if ( ! stats.get("tyranid").getBooleanValue(false))
                return null;
            if ( ! stats.get("synapse").getBooleanValue(false))
                return null;
            return new Value(8, "synapse");
        });
        size();
        advancement();
        leadership();
    }
    
    private void base() {
        to1("strength",     "tyranid", new Value(11));
        to1("dexterity",    "tyranid", new Value(10));
        to1("constitution", "tyranid", new Value(11));
        to1("intelligence", "tyranid", new Value(2));
        to1("wisdom",       "tyranid", new Value(10));
        to1("charisma",     "tyranid", new Value(5));
    }
    
    private void size() {
        input("strength", Arrays.asList("tyranid", "size"), stats -> {
            if ( ! stats.get("tyranid").getBooleanValue(false))
                return null;
            final String size = stats.get("size").getStringValue();
            if (size == null)
                return null;
            final int output = switch (size) {
                case "fine"       -> -16;
                case "diminutive" -> -12;
                case "tiny"       -> -8;
                case "small"      -> -4;
                case "medium"     -> 0;
                case "large"      -> 4;
                case "huge"       -> 8;
                case "gargantuan" -> 12;
                case "colossal"   -> 16;
                default -> throw new RuntimeException("unexpected size value: " + size);
            };
            return new Value(output, "size");
        });
        input("dexterity", Arrays.asList("tyranid", "size"), stats -> {
            if ( ! stats.get("tyranid").getBooleanValue(false))
                return null;
            final String size = stats.get("size").getStringValue();
            if (size == null)
                return null;
            final int output = switch (size) {
                case "fine"       -> 8;
                case "diminutive" -> 6;
                case "tiny"       -> 4;
                case "small"      -> 2;
                case "medium"     -> 0;
                case "large"      -> -2;
                case "huge"       -> -4;
                case "gargantuan" -> -6;
                case "colossal"   -> -8;
                default -> throw new RuntimeException("unexpected size value: " + size);
            };
            return new Value(output, "size");
        });
        input("constitution", Arrays.asList("tyranid", "size"), stats -> {
            if ( ! stats.get("tyranid").getBooleanValue(false))
                return null;
            final String size = stats.get("size").getStringValue();
            if (size == null)
                return null;
            final int output = switch (size) {
                case "fine"       -> -8;
                case "diminutive" -> -6;
                case "tiny"       -> -4;
                case "small"      -> -2;
                case "medium"     -> 0;
                case "large"      -> 2;
                case "huge"       -> 4;
                case "gargantuan" -> 6;
                case "colossal"   -> 8;
                default -> throw new RuntimeException("unexpected size value: " + size);
            };
            return new Value(output, "size");
        });
    }
        
    private void advancement() {
        input("strength", Arrays.asList("tyranid", "aberration hit dice"), stats -> {
            if ( ! stats.get("tyranid").getBooleanValue(false))
                return null;
            final Integer numHitDice = stats.get("aberration hit dice").getIntValue();
            if (numHitDice == null)
                return null;
            return new Value((int)Math.floor(numHitDice * 0.8), "advancement");
        });
        input("dexterity", Arrays.asList("tyranid", "aberration hit dice"), stats -> {
            if ( ! stats.get("tyranid").getBooleanValue(false))
                return null;
            final Integer numHitDice = stats.get("aberration hit dice").getIntValue();
            if (numHitDice == null)
                return null;
            return new Value((int)Math.floor(numHitDice * 0.8), "advancement");
        });
        input("constitution", Arrays.asList("tyranid", "aberration hit dice"), stats -> {
            if ( ! stats.get("tyranid").getBooleanValue(false))
                return null;
            final Integer numHitDice = stats.get("aberration hit dice").getIntValue();
            if (numHitDice == null)
                return null;
            return new Value((int)Math.floor(numHitDice * 0.4), "advancement");
        });
        input("intelligence", Arrays.asList("tyranid", "aberration hit dice", "synapse"), stats -> {
            if ( ! stats.get("tyranid").getBooleanValue(false))
                return null;
            if ( ! stats.get("synapse").getBooleanValue(false))
                return null;
            final Integer numHitDice = stats.get("aberration hit dice").getIntValue();
            if (numHitDice == null)
                return null;
            return new Value((int)Math.floor(numHitDice * 0.2), "advancement");
        });
        input("wisdom", Arrays.asList("tyranid", "aberration hit dice"), stats -> {
            if ( ! stats.get("tyranid").getBooleanValue(false))
                return null;
            final Integer numHitDice = stats.get("aberration hit dice").getIntValue();
            if (numHitDice == null)
                return null;
            return new Value((int)Math.floor(numHitDice * 0.2), "advancement");
        });
        input("charisma", Arrays.asList("tyranid", "aberration hit dice"), stats -> {
            if ( ! stats.get("tyranid").getBooleanValue(false))
                return null;
            final Integer numHitDice = stats.get("aberration hit dice").getIntValue();
            if (numHitDice == null)
                return null;
            return new Value((int)Math.floor(numHitDice * 0.2), "advancement");
        });
    }
    
    private void leadership() {
        agg("40k 9th ed leadership", root);
        input("intelligence", Arrays.asList("tyranid", "40k 9th ed leadership"), stats -> {
            if ( ! stats.get("tyranid").getBooleanValue(false))
                return null;
            final Integer leadership = stats.get("40k 9th ed leadership").getIntValue();
            if (leadership == null)
                return null;
            if (leadership <= 5)
                return null;
            return new Value(2 * (leadership - 5), "leadership");
        });
        input("wisdom", Arrays.asList("tyranid", "40k 9th ed leadership"), stats -> {
            if ( ! stats.get("tyranid").getBooleanValue(false))
                return null;
            final Integer leadership = stats.get("40k 9th ed leadership").getIntValue();
            if (leadership == null)
                return null;
            if (leadership <= 5)
                return null;
            return new Value(2 * (leadership - 5), "leadership");
        });
        input("charisma", Arrays.asList("tyranid", "40k 9th ed leadership"), stats -> {
            if ( ! stats.get("tyranid").getBooleanValue(false))
                return null;
            final Integer leadership = stats.get("40k 9th ed leadership").getIntValue();
            if (leadership == null)
                return null;
            if (leadership <= 5)
                return null;
            return new Value(2 * (leadership - 5), "leadership");
        });
    }
    
}
