package jmaurice.dnd.stats.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import jmaurice.dnd.stats.impl.ReadOnlyValuedStat;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class BaseBuilder {
    
    protected Stats stats;
    
    protected BaseBuilder(final Stats stats) {
        this.stats = stats;
    }
    
    protected <E> E first(Collection<E> collection) {
        return collection.stream().findFirst().get();
    }
    
    protected <E> List<E> list01(E x) {
        if (x == null)
            return null;
        return Collections.singletonList(x);
    }
    
    protected <E> List<E> list01(Optional<E> x) {
        if (x.isEmpty())
            return null;
        return Collections.singletonList(x.get());
    }
    
    //
    
    public <E> Optional<E> val01(List<E> values) {
        if (values == null)
            return Optional.empty();
        if (values.size() == 0)
            return Optional.empty();
        if (values.size() == 1)
            return Optional.of(values.get(0));
        throw new RuntimeException("expected 0 or 1 value; found: " + values);
    }
    
    protected <E> E val1(List<E> values) {
        if (values == null)
            throw new RuntimeException("expected 1 value. Found null values.");
        if (values.size() == 0)
            throw new RuntimeException("expected 1 value. Found 0 values.");
        if (values.size() == 1)
            return values.get(0);
        throw new RuntimeException("expected 1 value. Found: " + values);
    }
    
    protected List<String> getStringValues(List<Value> values) {
        return values.stream().map(x -> x.getStringValue()).toList();
    }
    
    //
    
    protected static class RootLeafOption {}
    protected static final RootLeafOption rootleaf = new RootLeafOption();
    protected static final RootLeafOption root = new RootLeafOption();
    protected static final RootLeafOption leaf = new RootLeafOption();
    
    protected void to1(String outputStatName, Value value) {
        to1(outputStatName, "default", null, value);
    }
    
    /** the rule will not be called when the input is empty aka zero-values */
    protected void to1(String outputStatName, String inputStatName) {
        to1(outputStatName, inputStatName, null, value -> value);
    }
    
    /** the rule will not be called when the input is empty aka zero-values */
    protected void to1(String outputStatName, String inputStatName, Value value) {
        to1(outputStatName, inputStatName, null, inputValue -> value);
    }
    
    /** the rule will not be called when the input is empty aka zero-values */
    protected void to1(String outputStatName, String inputStatName, RootLeafOption rootLeafOption, Value value) {
        to1(outputStatName, inputStatName, rootLeafOption, inputValue -> value);
    }
    
    /** the rule will not be called when the input is empty aka zero-values */
    protected void to1(String outputStatName, String inputStatName, Function<Value, Value> rule) {
        to1(outputStatName, inputStatName, null, rule);
    }
    
    /** the rule will not be called when the input is empty aka zero-values */
    protected void to1(String outputStatName, String inputStatName, RootLeafOption rootLeafOption, Function<Value, Value> rule) {
        final Function<Map<String, ReadOnlyValuedStat>, List<Value>> rule2 = stats -> {
            Optional<Value> inputValue = stats.get(inputStatName).val01();
            if (inputValue.isEmpty())
                return Collections.emptyList();
            Value outputValue = rule.apply(inputValue.get());
            if (outputValue == null)
                return Collections.emptyList();
            if (outputValue.source == null)
                outputValue = outputValue.source(inputStatName);
            return Collections.singletonList(outputValue);
        };
        stats.input(outputStatName, Collections.singletonList(inputStatName), rule2);
        if (rootLeafOption == root || rootLeafOption == rootleaf)
            stats.getStat(inputStatName).setRoot(true);
        if (rootLeafOption == leaf || rootLeafOption == rootleaf)
            stats.getStat(inputStatName).setLeaf(true);
    }
    
    /** the rule will not be called when the input is empty aka zero-values */
    protected void toN(String outputStatName, String inputStatName, Function<List<Value>, List<Value>> rule) {
        toN(outputStatName, inputStatName, null, rule);
    }
    
    /** the rule will not be called when the input is empty aka zero-values */
    protected void toN(String outputStatName, String inputStatName, RootLeafOption rootLeafOption, Function<List<Value>, List<Value>> rule) {
        final Function<Map<String, ReadOnlyValuedStat>, List<Value>> rule2 = stats -> {
            List<Value> inputValues = stats.get(inputStatName).getValues();
            if (inputValues.isEmpty())
                return Collections.emptyList();
            List<Value> outputValues = rule.apply(inputValues);
            if (outputValues == null)
                return Collections.emptyList();
            return outputValues;
        };
        stats.input(outputStatName, Collections.singletonList(inputStatName), rule2);
        if (rootLeafOption == root || rootLeafOption == rootleaf)
            stats.getStat(inputStatName).setRoot(true);
        if (rootLeafOption == leaf || rootLeafOption == rootleaf)
            stats.getStat(inputStatName).setLeaf(true);
    }
    
    /** the rule will always be called; there is no implicit short-circuiting when some or all of the input is empty */
    protected void input(String outputStatName, List<String> inputStatNames, Function<Map<String, ReadOnlyValuedStat>, Value> rule) {
        final Function<Map<String, ReadOnlyValuedStat>, List<Value>> rule2 = input -> list01(rule.apply(input));
        stats.input(outputStatName, inputStatNames, rule2);
    }
    
    /** 
     * asserts zero or one value
     * the rule will not be called when the input is empty aka zero-values 
     */
    protected void agg(String statName, RootLeafOption rootLeafOption) {
        stats.agg(statName, values -> list01(val01(values)));
        if (rootLeafOption == root || rootLeafOption == rootleaf)
            stats.getStat(statName).setRoot(true);
        if (rootLeafOption == leaf || rootLeafOption == rootleaf)
            stats.getStat(statName).setLeaf(true);
    }
    
    /** the rule will not be called when the input is empty aka zero-values */
    protected void agg(String statName, Function<List<Value>, Value> rule) {
        agg(statName, (RootLeafOption)null, rule);
    }
    
    /** the rule will not be called when the input is empty aka zero-values */
    protected void agg(String statName, RootLeafOption rootLeafOption, Function<List<Value>, Value> rule) {
        final Function<List<Value>, List<Value>> rule2 = values -> {
            if (values == null)
                return null;
            if (values.isEmpty())
                return values;
            final Value agg = rule.apply(values);
            if (agg == null)
                return Collections.emptyList();
            return Collections.singletonList(agg);
        };
        stats.agg(statName, rule2);
        if (rootLeafOption == root || rootLeafOption == rootleaf)
            stats.getStat(statName).setRoot(true);
        if (rootLeafOption == leaf || rootLeafOption == rootleaf)
            stats.getStat(statName).setLeaf(true);
    }
    
    /** the rule will not be called when the input is empty aka zero-values */
    protected void aggN(String statName, Function<List<Value>, List<Value>> rule) {
        aggN(statName, (RootLeafOption)null, rule);
    }
    
    /** the rule will not be called when the input is empty aka zero-values */
    protected void aggN(String statName, RootLeafOption rootLeafOption, Function<List<Value>, List<Value>> rule) {
        final Function<List<Value>, List<Value>> rule2 = values -> {
            if (values == null)
                return null;
            if (values.isEmpty())
                return values;
            values = rule.apply(values);
            if (values == null)
                return null;
            return values;
        };
        stats.agg(statName, rule2);
        if (rootLeafOption == root || rootLeafOption == rootleaf)
            stats.getStat(statName).setRoot(true);
        if (rootLeafOption == leaf || rootLeafOption == rootleaf)
            stats.getStat(statName).setLeaf(true);
    }
    
    //

    protected Value join(List<Value> values, String delimiter) {
        final StringBuilder r = new StringBuilder();
        for (Value value : values) {
            if ( ! r.isEmpty())
                r.append(delimiter);
            r.append(value.getStringValue());
        }
        if (r.isEmpty())
            return null;
        return new Value(r.toString());
    }
    
    protected Value maxAsDoubles(List<Value> values) {
        Value max = null;
        for (Value value : values) {
            if (max == null || value.getDoubleValue() > max.getDoubleValue())
                max = value.asDouble();
        }
        return max;
    }

    protected Value maxAsDoubles(Map<String, ReadOnlyValuedStat> stats) {
        return maxAsDoubles(stats.values());
    }
    
    protected Value maxAsDoubles(Collection<ReadOnlyValuedStat> stats) {
        return maxAsDoubles(stats.stream().flatMap(stat -> stat.getValues().stream()).toList());
    }
    
    protected Value maxAsInts(List<Value> values) {
        Value max = null;
        for (Value value : values) {
            if (max == null || value.getIntValue() > max.getIntValue())
                max = value.asInt();
        }
        return max;
    }

    protected Value maxAsInts(Map<String, ReadOnlyValuedStat> stats) {
        return maxAsInts(stats.values());
    }
    
    protected Value maxAsInts(Collection<ReadOnlyValuedStat> stats) {
        return maxAsInts(stats.stream().flatMap(stat -> stat.getValues().stream()).toList());
    }
    
    protected List<Value> sort(List<Value> values) {
        values = new ArrayList<>(values);
        Collections.sort(values, (a,b) -> a.getStringValue().compareTo(b.getStringValue()));
        return values;
    }
    
    protected Value sumAsDoubles(List<Value> values) {
        double sum = 0;
        final StringBuilder source = new StringBuilder();
        for (Value value : values) {
            final double valueDouble = value.getDoubleValue();
            sum += valueDouble;
            if ( ! source.isEmpty())
                source.append(", ");
            source.append(valueDouble);
            if (value.source != null)
                source.append(" ").append(value.source);
        }
        return new Value(sum, source.isEmpty() ? null : source.toString());
    }
    
    protected Value sumAsInts(List<Value> values) {
        int sum = 0;
        final StringBuilder source = new StringBuilder();
        for (Value value : values) {
            final int valueInt = value.getIntValue();
            sum += valueInt;
            if ( ! source.isEmpty())
                source.append(", ");
            source.append(valueInt);
            if (value.source != null)
                source.append(" ").append(value.source);
        }
        return new Value(sum, source.isEmpty() ? null : source.toString());
    }
    
    protected int sumStringAsInts(List<String> values) {
        int sum = 0;
        for (String value : values)
            sum += Integer.parseInt(value);
        return sum;
    }
    
    protected int sumInts(List<Integer> values) {
        int sum = 0;
        for (int value : values)
            sum += value;
        return sum;
    }
    
    protected String withSign(int value) {
        if (value >= 0)
            return "+" + value;
        return "" + value;
    }
    
    protected Value withSign(Value value) {
        return new Value(withSign(value.getIntValue()), value.source);
    }
    
}
