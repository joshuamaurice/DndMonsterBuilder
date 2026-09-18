package jmaurice.dnd.stats.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Value {

    public final Object value;
    public final String type;
    public final String source;
    
    private Value(Object value, String type, String source) {
        if (value == null)
            throw new NullPointerException("value");
        if (value instanceof Boolean b && ! b)
            throw new IllegalArgumentException("false");
        this.value = value;
        this.type = Optional.ofNullable(type).orElse("untyped");
        this.source = source;
    }
    
    private Value(String value, String type, String source) {
        if (value == null)
            throw new NullPointerException("value");
        value = value
                .replace("\r\n", " ")
                .replace("\n", " ")
                .replace("\r", " ")
                ;
        this.value = value;
        this.type = Optional.ofNullable(type).orElse("untyped");
        this.source = source;
    }
    
    private Value(boolean value, String type, String source) {
        if ( ! value)
            throw new IllegalArgumentException("false");
        this.value = value;
        this.type = Optional.ofNullable(type).orElse("untyped");
        this.source = source;
    }
    
    private Value(double value, String type, String source) {
        this.value = value;
        this.type = Optional.ofNullable(type).orElse("untyped");
        this.source = source;
    }
    
    private Value(int value, String type, String source) {
        this.value = value;
        this.type = Optional.ofNullable(type).orElse("untyped");
        this.source = source;
    }
    
    public Value(String value) {
        this(value, null, null);
    }
    
    public Value(boolean value) {
        this(value, null, null);
    }
    
    public Value(double value) {
        this(value, null, null);
    }
    
    public Value(int value) {
        this(value, null, null);
    }
    
    public Value value(String value) {
        return new Value(value, type, source);
    }
    
    public Value value(boolean value) {
        return new Value(value, type, source);
    }
    
    public Value value(double value) {
        return new Value(value, type, source);
    }
    
    public Value value(int value) {
        return new Value(value, type, source);
    }
    
    public Value type(String type) {
        if (type == null)
            throw new NullPointerException("type");
        if (type.isBlank())
            throw new IllegalArgumentException("type");
        if (type.trim().length() != type.length())
            throw new IllegalArgumentException("type");
        return new Value(value, type, source);
    }
    
    public Value source(String source) {
        return new Value(value, type, source);
    }
    
    public Value sourcePrefix(String prefix) {
        if (source == null || source.isBlank())
            return source(prefix);
        if (source.equals(prefix) || source.startsWith(prefix) || source.endsWith(prefix) || source.contains(" " + prefix + " "))
            return this;
        return source(prefix + " " + source);
    }
    
    @Override
    public String toString() {
        StringBuilder r = new StringBuilder();
        r.append(value);
        if (type != null)
            r.append(" ").append(type);
        if (source != null)
            r.append(" (").append(source).append(")");
        return r.toString();
    }
    
    public double getDoubleValue() {
        if (value instanceof Double x)
            return x;
        if (value instanceof Integer x)
            return x;
        if (value instanceof Boolean x)
            return x ? 1.0 : 0.0;
        if (value instanceof String x)
            return Double.parseDouble(x);
        throw new RuntimeException("unexpected value class-type: " + value.getClass().getName());
    }
    
    public int getIntValue() {
        if (value instanceof Integer x)
            return x;
        if (value instanceof Double x) {
            if (Math.floor(x) == Math.ceil(x))
                return (int)Math.floor(x);
            throw new RuntimeException("cannot cast non-integer Double value to int: " + value);
        }
        if (value instanceof Boolean x)
            return x ? 1 : 0;
        if (value instanceof String x)
            return Integer.parseInt(x);
        throw new RuntimeException("unexpected value class-type: " + value.getClass().getName());
    }
    
    public String getStringValue() {
        return value.toString();
    }
    
    public Value asDouble() { return this.value(getDoubleValue()); }
    public Value asInt() { return this.value(getIntValue()); }
    public Value asString() { return this.value(getStringValue()); }
    public Value floor() { return this.value((int)Math.floor(getDoubleValue())); }
    
    public Value atMost(int x) { return (getIntValue() <= x) ? this : this.value(x); }
    public Value atLeast(int x) { return (getIntValue() >= x) ? this : this.value(x); }
    
    public Value add(int x) { return this.value(getIntValue() + x); }
    public Value add(double x) { return this.value(getDoubleValue() + x); }
    public Value mult(int x) { return this.value(getIntValue() * x); }
    public Value mult(double x) { return this.value(getDoubleValue() * x); }
    
    public List<Value> split(String pattern) {
        return Arrays.asList(getStringValue().split(pattern)).stream().map(x -> new Value(x, type, source)).toList();
    }
    
    public Value regexExtract(String pattern) { 
        final String value = getStringValue();
        final Matcher m = Pattern.compile(pattern).matcher(value);
        if ( ! m.matches())
            throw new IllegalArgumentException("pattern >>" + pattern + "<< does not match value >>" + value + "<<");
        if (m.groupCount() != 1)
            throw new IllegalArgumentException("pattern >>" + pattern + "<< on value >>" + value + "<< expected 1 group; has " + m.groupCount() + " groups");
        return new Value(m.group(1), type, source);
    }

}
