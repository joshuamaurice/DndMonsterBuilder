package jmaurice.dnd.stats.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ValuedStat {
    
    final Stat stat;
    List<Value> values = new ArrayList<>();
    
    public ValuedStat(final Stat stat) {
        this.stat = stat;
    }
    
    @Override
    public String toString() {
        return stat.toString();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(stat);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ValuedStat other = (ValuedStat) obj;
        return Objects.equals(stat, other.stat);
    }
    
    public String name() {
        return stat.name();
    }
    
    public Stat stat() {
        return stat;
    }
    
    public List<Value> getValues() {
        return Collections.unmodifiableList(values);
    }
    
    public void setValues(List<Value> values) {
        this.values = Collections.unmodifiableList(values);
    }
    
    public void addInitialValue(Value value) {
        values.add(value);
    }
    
    public Optional<Value> val01() {
        if (values.size() == 0)
            return Optional.empty();
        if (values.size() == 1)
            return Optional.of(values.get(0));
        throw new RuntimeException("Stat " + stat.name() + "; expected 0 or 1 value; found: " + values);
    }
    
    public Value val1() {
        if (values.size() == 1)
            return values.get(0);
        throw new RuntimeException("Stat " + stat.name() + "; expected 1 value; found: " + values);
    }
    
    public Boolean getBooleanValue() { return val01().map(x -> x.getBooleanValue()).orElse(null); }
    public Boolean getBooleanValue(boolean defaultValue) { return val01().map(x -> x.getBooleanValue()).orElse(defaultValue); }
    public Double getDoubleValue() { return val01().map(x -> x.getDoubleValue()).orElse(null); }
    public Integer getIntValue() { return val01().map(x -> x.getIntValue()).orElse(null); }
    public String getStringValue() { return val01().map(x -> x.getStringValue()).orElse(null); }
    
}
