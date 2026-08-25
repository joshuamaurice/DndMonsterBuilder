package jmaurice.dnd.stats.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class ReadXml {
    
    public static Map<String, String> readCombinedInputColumn(final File file, final String inputSheetName) throws SAXException, IOException, ParserConfigurationException {
        final Document doc = DocumentBuilderFactory.newDefaultNSInstance().newDocumentBuilder().parse(file);
        final Element officeDocument = doc.getDocumentElement();
        if ( ! qname(officeDocument).equals(new QName("urn:oasis:names:tc:opendocument:xmlns:office:1.0", "document", "office")))
            throw new RuntimeException();
        final Element officeBody = (Element)val1(officeDocument.getElementsByTagName("office:body"));
        if ( ! qname(officeBody).equals(new QName("urn:oasis:names:tc:opendocument:xmlns:office:1.0", "body", "office")))
            throw new RuntimeException();
        final List<Element> spreadsheets = elements(children(officeBody));
        for (final Element spreadsheet : spreadsheets) {
            if ( ! qname(spreadsheet).equals(new QName("urn:oasis:names:tc:opendocument:xmlns:office:1.0", "spreadsheet", "office")))
                throw new RuntimeException();
        }
        final Element spreadsheet = getSpreadsheetByName(spreadsheets, inputSheetName);
        final Element table = getTable(spreadsheet);
        final List<Cell> row1 = row(getRange(table, 0, 0, 1, -1));
        final int nNameCol = row1.stream().map(x -> x.content).toList().indexOf("Name");
        final int nCombinedInputCol = row1.stream().map(x -> x.content).toList().indexOf("CombinedInput");
        final List<Cell> names = new ArrayList<>(col(getRange(table, 0, nNameCol, -1, 1)));
        final List<Cell> combinedInput = new ArrayList<>(col(getRange(table, 0, nCombinedInputCol, -1, 1)));
        names.remove(0);
        combinedInput.remove(0);
        if (names.size() != combinedInput.size())
            throw new RuntimeException();
        final Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < names.size(); ++i)
            m.put(names.get(i).content, combinedInput.get(i).content);
        return m;
    }
    
    private static class Range {
        List<List<Cell>> data = new ArrayList<>();
    }
    
    private static class Cell {
        String content;
        String formula;
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
    
    private static List<Cell> row(final Range range) {
        return val1(range.data);
    }
    
    private static List<Cell> col(final Range range) {
        return range.data.stream().map(x -> val1(x)).toList();
    }
    
    private static Range getRange(final Element table, final int targetRow, final int targetCol, final int nTargetRows, final int nTargetCols) {
        final int targetEndRow = nTargetRows == -1 ? Integer.MAX_VALUE : targetRow + nTargetRows;
        final int targetEndCol = nTargetCols == -1 ? Integer.MAX_VALUE : targetCol + nTargetCols;
        if ( ! qname(table).equals(new QName("urn:oasis:names:tc:opendocument:xmlns:table:1.0", "table", "table")))
            throw new RuntimeException("" + qname(table));
        final Range range = new Range();
        final List<Element> rows = new ArrayList<>(elements(children(table)).stream().filter(x -> qname(x).equals(new QName("urn:oasis:names:tc:opendocument:xmlns:table:1.0", "table-row", "table"))).toList());
        removeTrailingEmptyRows(rows);
        int r = 0;
        for (final Element row : rows) {
            final String sNumberRowsRepeated = row.getAttributeNS("urn:oasis:names:tc:opendocument:xmlns:table:1.0", "number-rows-repeated");
            final int nRowsRepeated = sNumberRowsRepeated.isEmpty() ? 1: Integer.parseInt(sNumberRowsRepeated);
            if (r >= targetEndRow)
                break;
            for (int iRowsRepeated = 0; iRowsRepeated < nRowsRepeated; ++iRowsRepeated) {
                if (r >= targetEndRow)
                    break;
                if (r >= targetRow) {
                    range.data.add(new ArrayList<>());
                    final List<Element> cells = new ArrayList<>(elements(children(row)).stream().filter(x -> qname(x).equals(new QName("urn:oasis:names:tc:opendocument:xmlns:table:1.0", "table-cell", "table"))).toList());
                    removeTrailingEmptyCells(cells);
                    int c = 0;
                    for (final Element cell : cells) {
                        final String sNumberColsRepeated = row.getAttributeNS("urn:oasis:names:tc:opendocument:xmlns:table:1.0", "number-columns-repeated");
                        final int nColsRepeated = sNumberColsRepeated.isEmpty() ? 1: Integer.parseInt(sNumberColsRepeated);
                        if (c >= targetEndCol)
                            break;
                        for (int iColsRepeated = 0; iColsRepeated < nColsRepeated; ++iColsRepeated) {
                            if (c >= targetEndCol)
                                break;
                            if (c >= targetCol) {
                                final Cell c2 = new Cell();
                                c2.content = val01(
                                    elements(children(cell)).stream()
                                    .filter(x -> qname(x).equals(new QName("urn:oasis:names:tc:opendocument:xmlns:text:1.0", "p", "text")))
                                    .toList()
                                ).map(x -> getStringContent(x)).orElse(null);
                                c2.formula = Optional.of(cell.getAttributeNS("urn:oasis:names:tc:opendocument:xmlns:table:1.0", "formula"))
                                        .filter(x -> ! x.isEmpty())
                                        .orElse(null);
                                range.data.get(range.data.size() - 1).add(c2);
                            }
                            ++c;
                        }
                    }
                }
                ++r;
            }
        }
        return range;
    }
    
    private static void removeTrailingEmptyRows(final List<Element> rows) {
        for (int i = rows.size(); i > 0; --i) {
            final Element row = rows.get(i - 1);
            if ( ! isRowEmpty(row))
                return;
            rows.remove(i - 1);
        }
    }
    
    private static void removeTrailingEmptyCells(final List<Element> cells) {
        for (int i = cells.size(); i > 0; --i) {
            final Element cell = cells.get(i - 1);
            if ( ! isCellEmpty(cell))
                return;
            cells.remove(i - 1);
        }
    }
    
    private static boolean isRowEmpty(final Element row) {
        final List<Element> cells = elements(children(row)).stream().filter(x -> qname(x).equals(new QName("urn:oasis:names:tc:opendocument:xmlns:table:1.0", "table-cell", "table"))).toList();
        for (final Element cell : cells) {
            if ( ! isCellEmpty(cell))
                return false;
        }
        return true;
    }
    
    private static boolean isCellEmpty(final Element cell) {
        final String content = val01(
            elements(children(cell)).stream()
            .filter(x -> qname(x).equals(new QName("urn:oasis:names:tc:opendocument:xmlns:text:1.0", "p", "text")))
            .toList()
        ).map(x -> getStringContent(x)).orElse(null);
        final String formula = Optional.of(cell.getAttributeNS("urn:oasis:names:tc:opendocument:xmlns:table:1.0", "formula"))
                .filter(x -> ! x.isEmpty())
                .orElse(null);
        return content == null && formula == null;
    }
    
    private static Element getSpreadsheetByName(final List<Element> spreadsheets, final String sheetName) {
        for (final Element spreadsheet : spreadsheets) {
            if ( ! qname(spreadsheet).equals(new QName("urn:oasis:names:tc:opendocument:xmlns:office:1.0", "spreadsheet", "office")))
                throw new RuntimeException();
            if (getTable(spreadsheet).getAttributeNS("urn:oasis:names:tc:opendocument:xmlns:table:1.0", "name").equals(sheetName))
                return spreadsheet;
        }
        throw new RuntimeException();
    }
    
    private static Element getTable(final Element spreadsheet) {
        if ( ! qname(spreadsheet).equals(new QName("urn:oasis:names:tc:opendocument:xmlns:office:1.0", "spreadsheet", "office")))
            throw new RuntimeException();
        final Element table = val1(elements(children(spreadsheet)).stream().filter(x -> qname(x).equals(new QName("urn:oasis:names:tc:opendocument:xmlns:table:1.0", "table", "table"))).toList());
        if ( ! qname(table).equals(new QName("urn:oasis:names:tc:opendocument:xmlns:table:1.0", "table", "table")))
            throw new RuntimeException("" + qname(table));
        return table;
    }

    private static QName qname(final Element x) {
        return new QName(x.getNamespaceURI(), x.getLocalName(), x.getPrefix());
    }
    
    private static <E> Optional<E> val01(final List<E> x) {
        if (x.size() == 0)
            return Optional.empty();
        if (x.size() == 1)
            return Optional.of(x.get(0));
        throw new RuntimeException();
    }

    private static <E> E val1(final List<E> x) {
        if (x.size() == 0)
            throw new RuntimeException();
        if (x.size() == 1)
            return x.get(0);
        throw new RuntimeException();
    }

    private static Node val1(final NodeList x) {
        return val1(toList(x));
    }

    private static List<Node> toList(final NodeList x) {
        return IntStream.range(0, x.getLength()).mapToObj(i -> x.item(i)).toList();
    }
    
    private static List<Node> children(final Element x) {
        return toList(x.getChildNodes());
    }
    
    private static List<Element> elements(final List<Node> input) {
        return input.stream()
                .filter(x -> ! ( x.getNodeType() == Node.TEXT_NODE && x.getNodeValue().isBlank()))
                .filter(x -> x.getNodeType() != Node.COMMENT_NODE)
                .map(x -> (Element)x)
                .toList();
    }
    
    private static String getStringContent(final Element input) {
        return children(input).stream()
                .filter(x -> x.getNodeType() != Node.COMMENT_NODE)
                .map(x -> (Text)x)
                .map(x -> x.getNodeValue())
                .collect(Collectors.joining());
    }

}
