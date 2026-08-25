package jmaurice.dnd.stats;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jmaurice.dnd.stats.fods.Cell;
import jmaurice.dnd.stats.fods.Range;
import jmaurice.dnd.stats.fods.ReadFodsXml;

public class ParseCreatureInputsFromSheet {
    
    //returns map creatureName -> creatureInput
    public static Map<String, String> read(final File file, final String inputSheetName) {
        final Range sheet = ReadFodsXml.readSheet(file, inputSheetName);
        final List<Cell> row1 = sheet.getSubRange(0, 0, 1, -1).asRow();
        final int nNameCol = row1.stream().map(x -> x.content()).toList().indexOf("Name");
        final int nCombinedInputCol = row1.stream().map(x -> x.content()).toList().indexOf("CombinedInput");
        final List<Cell> names = new ArrayList<>(sheet.getSubRange(0, nNameCol, -1, 1).asCol());
        final List<Cell> combinedInput = new ArrayList<>(sheet.getSubRange(0, nCombinedInputCol, -1, 1).asCol());
        names.remove(0);
        combinedInput.remove(0);
        if (names.size() != combinedInput.size())
            throw new RuntimeException();
        final Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < names.size(); ++i) {
            if (names.get(i).content().isBlank())
                continue;
            if (names.get(i).content().trim().equals("#"))
                continue;
            m.put(names.get(i).content(), combinedInput.get(i).content());
        }
        return m;
    }
    
}
