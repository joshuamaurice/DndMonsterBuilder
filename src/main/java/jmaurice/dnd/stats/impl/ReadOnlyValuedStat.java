package jmaurice.dnd.stats.impl;

import java.util.List;
import java.util.Optional;

public interface ReadOnlyValuedStat {
    String name();
    List<Value> getValues();
    Optional<Value> val01();
    Value val1();
}
