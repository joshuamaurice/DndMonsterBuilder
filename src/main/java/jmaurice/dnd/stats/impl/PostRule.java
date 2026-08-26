package jmaurice.dnd.stats.impl;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class PostRule {
    public List<String> writableStatNames;
    public List<String> readOnlyStatNames;
    public BiConsumer<Map<String, ValuedStat>, Map<String, ReadOnlyValuedStat>> rule;
}
