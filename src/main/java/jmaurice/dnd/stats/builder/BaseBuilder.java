package jmaurice.dnd.stats.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
    /** the rule will always be called; there is no implicit short-circuiting when some or all of the input is empty */
    protected StatContext input1(String outputStatName, List<String> inputStatNames, Function<Map<String, ReadOnlyValuedStat>, Value> rule) {
        final Function<Map<String, ReadOnlyValuedStat>, List<Value>> rule2 = input -> list01(rule.apply(input));
        stats.input(outputStatName, inputStatNames, rule2);
        return new StatContext(outputStatName);
    }
    
    /** the rule will always be called; there is no implicit short-circuiting when some or all of the input is empty */
    protected StatContext inputN(String outputStatName, List<String> inputStatNames, Function<Map<String, ReadOnlyValuedStat>, List<Value>> rule) {
        stats.input(outputStatName, inputStatNames, rule);
        return new StatContext(outputStatName);
    }
    
    protected Optional<String> joinS(List<String> values) {
        return joinS(values, ", ");
    }

    protected Optional<String> joinS(List<String> values, String delimiter) {
        if (values.isEmpty())
            return Optional.empty();
        final StringBuilder r = new StringBuilder();
        for (String value : values) {
            if ( ! r.isEmpty())
                r.append(delimiter);
            r.append(value.trim());
        }
        final String r2 = r.toString().replaceAll("^( *,)* *", "").replaceAll("( *,)* *$", "");
        if (r2.isEmpty())
            return Optional.empty();
        return Optional.of(r2);
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
    
    protected List<String> sortS(List<String> values) {
        values = new ArrayList<>(values);
        Collections.sort(values);
        return values;
    }
    
    protected List<String> sortS(List<String> values, Function<String, String> sortBy) {
        values = new ArrayList<>(values);
        Collections.sort(values, (a,b) -> {
            final String a2 = sortBy.apply(a);
            final String b2 = sortBy.apply(b);
            return a2.compareTo(b2);
        });
        return values;
    }
    
    //TODO improve pattern
    private static final Pattern stripHyperLinkPattern = Pattern.compile("^ *<a +href=\"[^\"]*\">(.*)</a> *$");
    protected static final Function<String, String> stripHyperLinkS = input -> {
        final Matcher m = stripHyperLinkPattern.matcher(input);
        if (m.matches())
            return m.group(1);
        return input;
    };
    protected static final Function<Value, String> stripHyperLink = input -> {
        final String s = input.getStringValue();
        final Matcher m = stripHyperLinkPattern.matcher(s);
        if (m.matches())
            return m.group(1);
        return s;
    };
    
    protected List<Value> sort(List<Value> values) {
        return sort(values, value -> value.getStringValue());
    }
    
    protected <E extends Comparable<E>> List<Value> sort(List<Value> values, Function<Value, E> sortBy) {
        values = new ArrayList<>(values);
        Collections.sort(values, (a,b) -> {
            final E a2 = sortBy.apply(a);
            final E b2 = sortBy.apply(b);
            if (a2 == null && b2 == null)
                return 0;
            if (a2 == null)
                return 1;
            if (b2 == null)
                return -1;
            return a2.compareTo(b2);
        });
        return values;
    }
    
    protected List<Value> sort(ReadOnlyValuedStat stat) {
        return sort(stat.getValues());
    }
    
    protected <E extends Comparable<E>> List<Value> sort(ReadOnlyValuedStat stat, Function<Value, E> sortBy) {
        return sort(stat.getValues(), sortBy);
    }
    
    protected static interface SumOption {};
    protected static final class SkipZero implements SumOption {};
    protected static final SkipZero skipZero = new SkipZero();
    
    protected Value sumAsInts(List<Value> values, final SumOption... options) {
        final boolean skipZeroB = Arrays.asList(options).contains(skipZero);
        
        Map<String, List<Value>> valuesByType = new LinkedHashMap<>();
        values.forEach(value -> valuesByType.computeIfAbsent(value.type, k -> new ArrayList<>()).add(value));
        for (final String type : Arrays.asList("divine", "profane", "sacred")) {
            final List<Value> valuesOfType = valuesByType.remove(type);
            if (valuesOfType != null)
                valuesByType.computeIfAbsent("religious", k -> new ArrayList<>()).addAll(valuesOfType);
        }
        
        final Set<Value> ignored = new LinkedHashSet<>();
        for (final Map.Entry<String, List<Value>> valuesOfType0 : valuesByType.entrySet()) {
            final String type = valuesOfType0.getKey();
            final List<Value> valuesOfType = valuesOfType0.getValue();
            if (type.equals("untyped") || type.equals("dodge"))
                continue;
            final int maxValueInt = maxAsInts(valuesOfType).getIntValue();
            final Value maxValue = valuesOfType.stream().filter(x -> x.getIntValue() == maxValueInt).findFirst().get();
            valuesOfType.stream().filter(x -> x != maxValue).forEach(x -> ignored.add(x));
        }
        
        int sum = 0;
        final StringBuilder sumSource = new StringBuilder();
        for (final Value value : values) {
            final int valueInt = value.getIntValue();
            if (skipZeroB && valueInt == 0)
                continue;
            if ( ! ignored.contains(value))
                sum += valueInt;
            if ( ! (value.source != null && value.source.equals("default"))) {
                if ( ! sumSource.isEmpty())
                    sumSource.append(", ");
                if (value.source != null && ! value.source.isBlank()) {
                    sumSource.append(valueInt).append(" ").append(value.source);
                } else {
                    sumSource.append(valueInt).append(" ").append(value.type);
                }
                if (ignored.contains(value))
                    sumSource.append(" (non-stacking)");
            }
        }
        return new Value(sum).source(sumSource.toString());
    }
    
    protected Value sumAsDoubles(List<Value> values, final SumOption... options) {
        final boolean skipZeroB = Arrays.asList(options).contains(skipZero);
        
        Map<String, List<Value>> valuesByType = new LinkedHashMap<>();
        values.forEach(value -> valuesByType.computeIfAbsent(value.type, k -> new ArrayList<>()).add(value));
        for (final String type : Arrays.asList("divine", "profane", "sacred")) {
            final List<Value> valuesOfType = valuesByType.remove(type);
            if (valuesOfType != null)
                valuesByType.computeIfAbsent("religious", k -> new ArrayList<>()).addAll(valuesOfType);
        }
        
        final Set<Value> ignored = new LinkedHashSet<>();
        for (final Map.Entry<String, List<Value>> valuesOfType0 : valuesByType.entrySet()) {
            final String type = valuesOfType0.getKey();
            final List<Value> valuesOfType = valuesOfType0.getValue();
            if (type.equals("untyped") || type.equals("dodge"))
                continue;
            final double maxValueDouble = maxAsDoubles(valuesOfType).getDoubleValue();
            final Value maxValue = valuesOfType.stream().filter(x -> x.getDoubleValue() == maxValueDouble).findFirst().get();
            valuesOfType.stream().filter(x -> x != maxValue).forEach(x -> ignored.add(x));
        }
        
        double sum = 0;
        final StringBuilder sumSource = new StringBuilder();
        for (final Value value : values) {
            final double valueDouble = value.getDoubleValue();
            if (skipZeroB && valueDouble == 0)
                continue;
            if ( ! ignored.contains(value))
                sum += valueDouble;
            if ( ! (value.source != null && value.source.equals("default"))) {
                if ( ! sumSource.isEmpty())
                    sumSource.append(", ");
                if (value.source != null && ! value.source.isBlank()) {
                    sumSource.append(valueDouble).append(" ").append(value.source);
                } else {
                    sumSource.append(valueDouble).append(" ").append(value.type);
                }
                if (ignored.contains(value))
                    sumSource.append(" (non-stacking)");
            }
        }
        return new Value(sum).source(sumSource.toString());
    }
    
    protected Integer sumAsIntsS(List<String> values, SumOption... options) {
        return sumAsInts(values.stream().map(x -> new Value(x)).toList(), options).getIntValue();
    }
    
    protected Double sumAsDOublesS(List<String> values, SumOption... options) {
        return sumAsDoubles(values.stream().map(x -> new Value(x)).toList(), options).getDoubleValue();
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
        return value.value(withSign(value.getIntValue()));
    }
    
    //
    
    protected StatContext stat(String statName) {
        stats.getOrCreateStat(statName);
        return new StatContext(statName);
    }
    
    protected static final class RootLeafOption {}
    protected static final RootLeafOption root = new RootLeafOption();
    protected static final RootLeafOption leaf = new RootLeafOption();
    protected static final RootLeafOption rootleaf = new RootLeafOption();
    
    public final class StatContext {
        
        private String contextStatName;
        
        private StatContext(final String contextStatName) { 
            this.contextStatName = contextStatName;
        }
        
        //--
        
        /** the rule will not be called when the input is empty aka zero-values */
        public StatContext to1(String outputStatName) {
            return to1(outputStatName, value -> value);
        }
        
        /** the rule will not be called when the input is empty aka zero-values */
        public StatContext to1(String outputStatName, Value value) {
            return to1(outputStatName, inputValue -> value);
        }
        
        /** the rule will not be called when the input is empty aka zero-values */
        public StatContext to1(String outputStatName, Function<Value, Value> rule) {
            final Function<Map<String, ReadOnlyValuedStat>, List<Value>> rule2 = stats -> {
                Optional<Value> inputValue = stats.get(contextStatName).val01();
                if (inputValue.isEmpty())
                    return Collections.emptyList();
                Value outputValue = rule.apply(inputValue.get());
                if (outputValue == null)
                    return Collections.emptyList();
                if (outputValue.source == null)
                    outputValue = outputValue.source(contextStatName);
                return Collections.singletonList(outputValue);
            };
            stats.input(outputStatName, Collections.singletonList(contextStatName), rule2);
            return new StatContext(outputStatName);
        }
        
        //--
        
        /** the rule will not be called when the input is empty aka zero-values */
        public StatContext toN(String outputStatName) {
            return toN(outputStatName, values -> values);
        }
        
        /** the rule will not be called when the input is empty aka zero-values */
        public StatContext toN(String outputStatName, Value value) {
            return toN(outputStatName, values -> Collections.singletonList(value));
        }
        
        /** the rule will not be called when the input is empty aka zero-values */
        public StatContext toN(String outputStatName, Function<List<Value>, List<Value>> rule) {
            final Function<Map<String, ReadOnlyValuedStat>, List<Value>> rule2 = stats -> {
                List<Value> inputValues = stats.get(contextStatName).getValues();
                if (inputValues.isEmpty())
                    return Collections.emptyList();
                List<Value> outputValues = rule.apply(inputValues);
                if (outputValues == null)
                    return Collections.emptyList();
                outputValues = new ArrayList<>(outputValues);
                for (int i = 0; i < outputValues.size(); ++i) {
                    if (outputValues.get(i).source == null)
                        outputValues.set(i, outputValues.get(i).source(contextStatName));
                }
                return outputValues;
            };
            stats.input(outputStatName, Collections.singletonList(contextStatName), rule2);
            return new StatContext(outputStatName);
        }
        
        //--
        
        /** Use the default agg rule that validates 0 or 1 num values. */
        public StatContext agg() {
            return agg((RootLeafOption)null);
        }
        
        /** Use the default agg rule that validates 0 or 1 num values. */
        public StatContext agg(RootLeafOption rootLeafOption) {
            return agg(rootLeafOption, values -> {
                if (values.size() == 0)
                    return null;
                if (values.size() == 1)
                    return values.get(0);
                throw new RuntimeException("implicit agg rule failed for stat " + contextStatName + ". Values: " + values);
            });
        }
        
        /** the rule will not be called when the input is empty aka zero-values */
        public StatContext agg(Function<List<Value>, Value> rule) {
            return agg(null, rule);
        }
        
        /** the rule will not be called when the input is empty aka zero-values */
        public StatContext agg(RootLeafOption rootLeafOption, Function<List<Value>, Value> rule) {
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
            stats.agg(contextStatName, rule2);
            if (rootLeafOption == root || rootLeafOption == rootleaf)
                stats.getStat(contextStatName).setRoot(true);
            if (rootLeafOption == leaf || rootLeafOption == rootleaf)
                stats.getStat(contextStatName).setLeaf(true);
            return new StatContext(contextStatName);
        }
        
        /** use no-op agg rule (disables implicit validate-one-value agg rule) */
        public StatContext aggN() {
            return aggN(null, values -> values);
        }
        
        /** use a no-op agg-rule */
        public StatContext aggN(RootLeafOption rootLeafOption) {
            return aggN(rootLeafOption, values -> values);
        }
        
        /** the rule will not be called when the input is empty aka zero-values */
        public StatContext aggN(Function<List<Value>, List<Value>> rule) {
            return aggN(null, rule);
        }
        
        /** the rule will not be called when the input is empty aka zero-values */
        public StatContext aggN(RootLeafOption rootLeafOption, Function<List<Value>, List<Value>> rule) {
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
            stats.agg(contextStatName, rule2);
            if (rootLeafOption == root || rootLeafOption == rootleaf)
                stats.getStat(contextStatName).setRoot(true);
            if (rootLeafOption == leaf || rootLeafOption == rootleaf)
                stats.getStat(contextStatName).setLeaf(true);
            return new StatContext(contextStatName);
        }

        @SafeVarargs
        public final void many(Consumer<StatContext>... actions) {
            for (final Consumer<StatContext> action : actions) {
                action.accept(this);
            }
        }
        
    }
    
}
