package jmaurice.dnd.stats.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class InputRule {
    public List<String> inputStatNames;
    public Function<Map<String, ReadOnlyStat>, List<Value>> rule;
}
