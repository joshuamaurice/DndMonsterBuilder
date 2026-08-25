package jmaurice.dnd.stats.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Stat {
    
    String name;
    List<Value> values = new ArrayList<>();
    Set<Stat> inputStats = new LinkedHashSet<>();
    List<InputRule> inputRules = new ArrayList<>();
    AggRule aggRule;
    PostRule postRule;
    Set<Stat> preOutputStats = new LinkedHashSet<>();
    Set<Stat> outputStats = new LinkedHashSet<>();
    boolean root;
    boolean leaf;
    boolean started;
    boolean finished;
    
    @Override
    public String toString() {
        return "Stat(" + name + ")";
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Stat other = (Stat) obj;
        return Objects.equals(name, other.name);
    }
    
    public String name() {
        return name;
    }
    
    public List<Value> getValues() {
        return Collections.unmodifiableList(values);
    }
    
    public void setValues(List<Value> values) {
        this.values = values;
    }
    
    public void addInitialValue(Value value) {
        values.add(value);
    }
    
    public Optional<Value> val01() {
        if (values.size() == 0)
            return Optional.empty();
        if (values.size() == 1)
            return Optional.of(values.get(0));
        throw new RuntimeException("Stat " + name + "; expected 0 or 1 value; found: " + values);
    }
    
    public Value val1() {
        if (values.size() == 1)
            return values.get(0);
        throw new RuntimeException("Stat " + name + "; expected 1 value; found: " + values);
    }
    
    public boolean isRoot() { return root; }
    public void setRoot(final boolean root) { this.root = root; }
    
    public boolean isLeaf() { return leaf; }
    public void setLeaf(final boolean leaf) { this.leaf = leaf; }
    
}
