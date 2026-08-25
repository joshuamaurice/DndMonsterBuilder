package jmaurice.dnd.stats.fods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Range {
    
    public Range(final List<List<Cell>> data) {
        this.data = data.stream().map(x -> Collections.unmodifiableList(new ArrayList<>(x))).toList();
    }
    
    private final List<List<Cell>> data;
    
    public Range getSubRange(final int nRow, final int nCol, final int nRows, final int nCols) {
        final int nRowEnd = nRows == -1 ? data.size() : (nRow + nRows);
        final int nColEnd = nCols == -1 ? data.get(0).size() : (nCol + nCols);
        return new Range(data.subList(nRow, nRowEnd).stream().map(row -> row.subList(nCol, nColEnd)).toList());
    }
    
    public List<Cell> asRow() {
        if (data.size() != 1)
            throw new RuntimeException();
        return new ArrayList<>(data.get(0));
    }
    
    public List<Cell> asCol() {
        if (data.get(0).size() != 1) //relies on validation by code that calls Range ctor.
            throw new RuntimeException();
        return data.stream().map(x -> x.get(0)).collect(Collectors.toCollection(ArrayList::new));
    }
    
}
