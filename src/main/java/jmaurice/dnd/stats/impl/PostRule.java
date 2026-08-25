package jmaurice.dnd.stats.impl;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class PostRule {
    public List<String> postStatNames;
    public List<String> otherInputStatNames;
    public BiConsumer<Map<String, Stat>, Map<String, ReadOnlyStat>> rule;
}
