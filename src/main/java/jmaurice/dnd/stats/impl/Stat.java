package jmaurice.dnd.stats.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Stat {
    
    String name;
    List<InputRule> inputRules = new ArrayList<>();
    AggRule aggRule;
    PostRule postRule;
    boolean root;
    boolean leaf;
    
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
    
    public boolean isRoot() { return root; }
    public void setRoot(final boolean root) { this.root = root; }
    
    public boolean isLeaf() { return leaf; }
    public void setLeaf(final boolean leaf) { this.leaf = leaf; }
    
}
