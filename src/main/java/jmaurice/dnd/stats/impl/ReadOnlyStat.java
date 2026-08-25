package jmaurice.dnd.stats.impl;

import java.util.List;
import java.util.Optional;

public interface ReadOnlyStat {
    String name();
    List<Value> getValues();
    Optional<Value> val01();
    Value val1();
}
