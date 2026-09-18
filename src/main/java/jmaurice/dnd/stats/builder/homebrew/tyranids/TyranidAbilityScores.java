package jmaurice.dnd.stats.builder.homebrew.tyranids;

import java.util.Arrays;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class TyranidAbilityScores extends BaseBuilder {

    public TyranidAbilityScores(final Stats stats) { super(stats); }

    public void build() {
        base();
        input1("intelligence", Arrays.asList("tyranid", "synapse"), stats -> {
            if (stats.get("tyranid").getValues().size() == 0)
                return null;
            if (stats.get("synapse").getValues().size() == 0)
                return null;
            return new Value(8).source("synapse");
        });
        size();
        advancement();
        leadership();
    }
    
    private void base() {
        stat("tyranid").to1("strength",     new Value(11));
        stat("tyranid").to1("dexterity",    new Value(10));
        stat("tyranid").to1("constitution", new Value(11));
        stat("tyranid").to1("intelligence", new Value(2));
        stat("tyranid").to1("wisdom",       new Value(10));
        stat("tyranid").to1("charisma",     new Value(5));
    }
    
    private void size() {
        input1("strength", Arrays.asList("tyranid", "size"), stats -> {
            if (stats.get("tyranid").getValues().size() == 0)
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
                case "colossal-plus" -> 21;
                default -> throw new RuntimeException("unexpected size value: " + size);
            };
            return new Value(output).source("size");
        });
        input1("dexterity", Arrays.asList("tyranid", "size"), stats -> {
            if (stats.get("tyranid").getValues().size() == 0)
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
                case "colossal-plus" -> -10;
                default -> throw new RuntimeException("unexpected size value: " + size);
            };
            return new Value(output).source("size");
        });
        input1("constitution", Arrays.asList("tyranid", "size"), stats -> {
            if (stats.get("tyranid").getValues().size() == 0)
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
                case "colossal-plus" -> 10;
                default -> throw new RuntimeException("unexpected size value: " + size);
            };
            return new Value(output).source("size");
        });
    }
        
    private void advancement() {
        input1("strength", Arrays.asList("tyranid", "aberration hit dice"), stats -> {
            if (stats.get("tyranid").getValues().size() == 0)
                return null;
            final Integer numHitDice = stats.get("aberration hit dice").getIntValue();
            if (numHitDice == null)
                return null;
            return new Value((int)Math.floor(numHitDice * 0.8)).source("advancement");
        });
        input1("dexterity", Arrays.asList("tyranid", "aberration hit dice"), stats -> {
            if (stats.get("tyranid").getValues().size() == 0)
                return null;
            final Integer numHitDice = stats.get("aberration hit dice").getIntValue();
            if (numHitDice == null)
                return null;
            return new Value((int)Math.floor(numHitDice * 0.8)).source("advancement");
        });
        input1("constitution", Arrays.asList("tyranid", "aberration hit dice"), stats -> {
            if (stats.get("tyranid").getValues().size() == 0)
                return null;
            final Integer numHitDice = stats.get("aberration hit dice").getIntValue();
            if (numHitDice == null)
                return null;
            return new Value((int)Math.floor(numHitDice * 0.4)).source("advancement");
        });
        input1("intelligence", Arrays.asList("tyranid", "aberration hit dice", "synapse"), stats -> {
            if (stats.get("tyranid").getValues().size() == 0)
                return null;
            if (stats.get("synapse").getValues().size() == 0)
                return null;
            final Integer numHitDice = stats.get("aberration hit dice").getIntValue();
            if (numHitDice == null)
                return null;
            return new Value((int)Math.floor(numHitDice * 0.2)).source("advancement");
        });
        input1("wisdom", Arrays.asList("tyranid", "aberration hit dice"), stats -> {
            if (stats.get("tyranid").getValues().size() == 0)
                return null;
            final Integer numHitDice = stats.get("aberration hit dice").getIntValue();
            if (numHitDice == null)
                return null;
            return new Value((int)Math.floor(numHitDice * 0.2)).source("advancement");
        });
        input1("charisma", Arrays.asList("tyranid", "aberration hit dice"), stats -> {
            if (stats.get("tyranid").getValues().size() == 0)
                return null;
            final Integer numHitDice = stats.get("aberration hit dice").getIntValue();
            if (numHitDice == null)
                return null;
            return new Value((int)Math.floor(numHitDice * 0.2)).source("advancement");
        });
    }
    
    private void leadership() {
        stat("40k 9th ed leadership").agg(root);
        input1("intelligence", Arrays.asList("tyranid", "40k 9th ed leadership"), stats -> {
            if (stats.get("tyranid").getValues().size() == 0)
                return null;
            final Integer leadership = stats.get("40k 9th ed leadership").getIntValue();
            if (leadership == null)
                return null;
            if (leadership <= 5)
                return null;
            return new Value(2 * (leadership - 5)).source("leadership");
        });
        input1("wisdom", Arrays.asList("tyranid", "40k 9th ed leadership"), stats -> {
            if (stats.get("tyranid").getValues().size() == 0)
                return null;
            final Integer leadership = stats.get("40k 9th ed leadership").getIntValue();
            if (leadership == null)
                return null;
            if (leadership <= 5)
                return null;
            return new Value(2 * (leadership - 5)).source("leadership");
        });
        input1("charisma", Arrays.asList("tyranid", "40k 9th ed leadership"), stats -> {
            if (stats.get("tyranid").getValues().size() == 0)
                return null;
            final Integer leadership = stats.get("40k 9th ed leadership").getIntValue();
            if (leadership == null)
                return null;
            if (leadership <= 5)
                return null;
            return new Value(2 * (leadership - 5)).source("leadership");
        });
    }
    
}
