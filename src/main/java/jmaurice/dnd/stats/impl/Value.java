package jmaurice.dnd.stats.impl;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Value {

    public final Object value;
    public final String source;
    
    public Value(String value) {
        if (value == null)
            throw new NullPointerException("value");
        this.value = value;
        this.source = null;
    }
    
    public Value(boolean value) {
        this.value = value;
        this.source = null;
    }
    
    public Value(double value) {
        this.value = value;
        this.source = null;
    }
    
    public Value(int value) {
        this.value = value;
        this.source = null;
    }
    
    private Value(Object value, String source) {
        if (value == null)
            throw new NullPointerException("value");
        this.value = value;
        this.source = source;
    }
    
    public Value(String value, String source) {
        if (value == null)
            throw new NullPointerException("value");
        this.value = value;
        this.source = source;
    }
    
    public Value(boolean value, String source) {
        this.value = value;
        this.source = source;
    }
    
    public Value(double value, String source) {
        this.value = value;
        this.source = source;
    }
    
    public Value(int value, String source) {
        this.value = value;
        this.source = source;
    }
    
    public Value source(String source) {
        return new Value(value, source);
    }
    
    @Override
    public String toString() {
        if (source == null || source.isBlank())
            return "" + value;
        return value + " (" + source + ")";
    }
    
    public double getDoubleValue() {
        if (value instanceof String value2)
            return Double.parseDouble(value2);
        if (value instanceof Number value2)
            return value2.doubleValue();
        throw new IllegalStateException("cannot convert " + value.getClass().getName() + " to double; requires String or Number");
    }
    
    public int getIntValue() {
        if (value instanceof String value2)
            return Integer.parseInt(value2);
        if (value instanceof Number value2)
            return value2.intValue();
        throw new IllegalStateException("cannot convert " + value.getClass().getName() + " to int; requires String or Number");
    }
    
    public String getStringValue() {
        return value.toString();
    }
    
    public Value add(int x) { return new Value(getIntValue() + x, source); }
    public Value asDouble() { return new Value(getDoubleValue(), source); }
    public Value asInt() { return new Value(getIntValue(), source); }
    public Value asString() { return new Value(getStringValue(), source); }
    public Value floor() { return new Value((int)Math.floor(getDoubleValue()), source); }
    public Value min(int x) { return new Value(Math.min(getIntValue(), x), source); }
    public Value max(int x) { return new Value(Math.max(getIntValue(), x), source); }
    public Value mult(double x) { return new Value(getDoubleValue() * x, source); }
    
    public List<Value> split(String pattern) {
        return Arrays.asList(getStringValue().split(pattern)).stream().map(x -> new Value(x, source)).toList();
    }
    
    public Value regexExtract(String pattern) { 
        final String value = getStringValue();
        final Matcher m = Pattern.compile(pattern).matcher(value);
        if ( ! m.matches())
            throw new IllegalArgumentException("pattern >>" + pattern + "<< does not match value >>" + value + "<<");
        if (m.groupCount() != 1)
            throw new IllegalArgumentException("pattern >>" + pattern + "<< on value >>" + value + "<< expected 1 group; has " + m.groupCount() + " groups");
        return new Value(m.group(1), source);
    }
    
}
