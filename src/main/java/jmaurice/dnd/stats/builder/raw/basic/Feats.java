package jmaurice.dnd.stats.builder.raw.basic;

import java.util.Arrays;

import jmaurice.dnd.stats.builder.BaseBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Feats extends BaseBuilder {

    public Feats(final Stats stats) { super(stats); }

    public void build() {
        stat("feat")
        .aggN(root, values -> sort(values))
        .toN("feats")
        .aggN(values -> sort(values));
        
        stat("epic feat")
        .aggN(root, values -> sort(values))
        .toN("epic feats")
        .aggN(values -> sort(values));
        
        stat("critical focus").agg(root).to1("feats", new Value("critical focus"));
        stat("crowded charge").agg(root).to1("feats", new Value("crowded charge"));
        stat("cunning feat").agg(root).to1("feats", new Value("cunning"));
        stat("death from above feat").agg(root).to1("feats", new Value("death from above"));
        stat("deadly aim").agg(root).to1("feats", new Value("deadly aim"));
        stat("dreadful carnage").agg(root).to1("feats", new Value("dreadful carnage"));
        stat("fleet of foot feat").agg(root).to1("feats", new Value("fleet of foot"));
        stat("furious focus").agg(root).to1("feats", new Value("furious focus"));
        
        stat("great fortitude")
        .agg(root)
        .many(
            x -> x.to1("feats", new Value("great fortitude")),
            x -> x.to1("fortitude", new Value(2))
        );
        
        stat("greater weapon focus")
        .agg(root)
        .many(
            x -> x.to1("feats", value -> new Value("greater weapon focus (" + value.getStringValue() + ")")),
            x -> x.to1("weapon properties", value -> new Value("name=" + value.getStringValue() + ",attack modifier=1"))
        );
        
        stat("greater weapon specialization")
        .agg(root)
        .many(
            x -> x.to1("feats", value -> new Value("greater weapon specialization (" + value.getStringValue() + ")")),
            x -> x.to1("weapon properties", value -> new Value("name=" + value.getStringValue() + ",damage modifier=2"))
        );
        
        stat("impaling critical").agg(root).to1("feats", new Value("impaling critical"));
        stat("improved impaling critical").agg(root).to1("feats", new Value("improved impaling critical"));
        
        stat("improved critical")
        .agg(root)
        .many(
            x -> x.to1("feats", value -> new Value("improved critical (" + value.getStringValue() + ")")),
            x -> x.to1("weapon properties", value -> new Value("name=" + value.getStringValue() + ",improved critical"))
        );
        
        stat("improved initiative")
        .agg(root)
        .many(
            x -> x.to1("feats", new Value("improved initiative")),
            x -> x.to1("initiative", new Value(4))
        );
        
        stat("improved bull rush").agg(root).to1("feats", new Value("improved bull rush"));
        
        stat("iron will")
        .agg(root)
        .many(
            x -> x.to1("feats", new Value("iron will")),
            x -> x.to1("will", new Value(2))
        );
        
        stat("lightning reflexes")
        .agg(root)
        .many(
            x -> x.to1("feats", new Value("lightning reflexes")),
            x -> x.to1("reflex", new Value(2))
        );
        
        stat("lightning stance feat").agg(root).to1("feats", new Value("lightning stance"));
        stat("point-blank shot").agg(root).to1("feats", new Value("point-blank shot"));
        stat("power attack").agg(root).to1("feats", new Value("power attack"));
        stat("precise shot").agg(root).to1("feats", new Value("precise shot"));
        stat("quiet death feat").agg(root).to1("feats", new Value("quiet death"));
        stat("rhino charge feat").agg(root).to1("feats", new Value("rhino charge"));
        stat("run feat").agg(root).to1("feats", new Value("run"));
        
        Skills.allSkills.forEach(skill -> stat("skill focus " + skill).agg(root).to1("feats", new Value("skill focus (" + skill + ")")));
        Skills.allSkills.forEach(skill -> input1(skill, Arrays.asList("skill focus " + skill, skill + " ranks"), stats -> {
            if (stats.get("skill focus " + skill).getValues().size() == 0)
                return null;
            final Integer ranks = stats.get(skill + " ranks").getIntValue();
            if (ranks != null && ranks >= 10)
                return new Value(6);
            return new Value(3);
        }));
        
        stat("weapon focus")
        .agg(root)
        .many(
            x -> x.to1("feats", value -> new Value("weapon focus (" + value.getStringValue() + ")")),
            x -> x.to1("weapon properties", value -> new Value("name=" + value.getStringValue() + ",attack modifier=1"))
        );
        
        stat("weapon specialization")
        .agg(root)
        .many(
            x -> x.to1("feats", value -> new Value("weapon specialization (" + value.getStringValue() + ")")),
            x -> x.to1("weapon properties", value -> new Value("name=" + value.getStringValue() + ",damage modifier=2"))
        );
        
        stat("wind stance feat").agg(root).to1("feats", new Value("wind stance"));
    }

}
