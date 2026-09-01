package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Feats extends BaseBuilder {

    public Feats(final Stats stats) { super(stats); }

    public void build() {
        aggN("feats", values -> sort(values));
        toN("feats2", "feats", values -> values);
        agg("feats2", leaf, values -> join(sort(values), ", "));
        
        to1("feats", "critical focus", root, new Value("critical focus"));
        to1("feats", "crowded charge", root, new Value("crowded charge"));
        to1("feats", "cunning feat", root, new Value("cunning"));
        to1("feats", "death from above feat", root, new Value("death from above"));
        to1("feats", "deadly aim", root, new Value("deadly aim"));
        to1("feats", "dreadful carnage", root, new Value("dreadful carnage"));
        to1("feats", "fleet of foot feat", root, new Value("fleet of foot"));
        to1("feats", "furious focus", root, new Value("furious focus"));
        
        to1("feats", "great fortitude", root, new Value("great fortitude"));
        to1("fortitude", "great fortitude", new Value(2));
        
        to1("feats", "greater weapon focus", root, value -> new Value("greater weapon focus (" + value.getStringValue() + ")"));
        to1("weapon properties", "greater weapon focus", value -> new Value("name=" + value.getStringValue() + ",attack modifier=1"));
        
        to1("feats", "greater weapon specialization", root, value -> new Value("greater weapon specialization (" + value.getStringValue() + ")"));
        to1("weapon properties", "greater weapon specialization", value -> new Value("name=" + value.getStringValue() + ",damage modifier=2"));
        
        to1("feats", "impaling critical", root, new Value("impaling critical"));
        to1("feats", "improved impaling critical", root, new Value("improved impaling critical"));
        
        to1("feats", "improved critical", root, value -> new Value("improved critical (" + value.getStringValue() + ")"));
        to1("weapon properties", "improved critical", value -> new Value("name=" + value.getStringValue() + ",improved critical"));
        
        to1("feats", "improved initiative", root, new Value("improved initiative"));
        to1("initiative", "improved initiative", new Value(4));
        
        to1("feats", "improved bull rush", root, new Value("improved bull rush"));
        
        to1("feats", "iron will", root, new Value("iron will"));
        to1("will", "iron will", new Value(2));
        
        to1("feats", "lightning reflexes", root, new Value("lightning reflexes"));
        to1("reflex", "lightning reflexes", new Value(2));
        
        to1("feats", "lightning stance feat", root, new Value("lightning stance"));
        to1("feats", "point-blank shot", root, new Value("point-blank shot"));
        to1("feats", "power attack", root, new Value("power attack"));
        to1("feats", "precise shot", root, new Value("precise shot"));
        to1("feats", "quiet death feat", root, new Value("quiet death"));
        to1("feats", "rhino charge feat", root, new Value("rhino charge"));
        to1("feats", "run feat", root, new Value("run"));
        
        Skills.allSkills.forEach(skill -> to1("feats", "skill focus " + skill, root, new Value("skill focus (" + skill + ")")));
        Skills.allSkills.forEach(skill -> input(skill, Arrays.asList("skill focus " + skill, skill + " ranks"), stats -> {
            if ( ! stats.get("skill focus " + skill).getBooleanValue(false))
                return null;
            final Integer ranks = stats.get(skill + " ranks").getIntValue();
            if (ranks != null && ranks >= 10)
                return new Value(6);
            return new Value(3);
        }));
        
        to1("feats", "weapon focus", root, value -> new Value("weapon focus (" + value.getStringValue() + ")"));
        to1("weapon properties", "weapon focus", value -> new Value("name=" + value.getStringValue() + ",attack modifier=1"));
        
        to1("feats", "weapon specialization", root, value -> new Value("weapon specialization (" + value.getStringValue() + ")"));
        to1("weapon properties", "weapon specialization", value -> new Value("name=" + value.getStringValue() + ",damage modifier=2"));
        
        to1("feats", "wind stance feat", root, new Value("wind stance"));
    }

}
