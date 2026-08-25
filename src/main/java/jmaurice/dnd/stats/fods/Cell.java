package jmaurice.dnd.stats.fods;

public class Cell {
    
    private String content;
    private String formula;
    
    public String content() { return content; }
    public String formula() { return formula; }
    
    public void content(String content) { this.content = content; }
    public void formula(String formula) { this.formula = formula; }
    
    @Override public String toString() {
        if (content == null && formula == null)
            return "";
        if (content != null && formula == null)
            return content;
        if (content == null && formula != null)
            return "FORMULA[" + formula + "]";
        if (content != null && formula != null)
            return "FORMULA[" + formula + "]=" + content;
        throw new RuntimeException();
    }

}
