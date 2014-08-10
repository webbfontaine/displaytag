/**
 * Licensed under the Artistic License; you may not use this file
 * except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://displaytag.sourceforge.net/license.html
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.displaytag.render;

import java.util.*;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.displaytag.decorator.TableDecorator;
import org.displaytag.decorator.hssf.DecoratesHssf;
import org.displaytag.model.Column;
import org.displaytag.model.HeaderCell;
import org.displaytag.model.Row;
import org.displaytag.model.TableModel;
import org.displaytag.export.XmlTotalsWriter;
import org.displaytag.export.excel.ExcelUtils;


/**
 * A table writer that formats a table in Excel's spreadsheet format, and writes it to an HSSF workbook.
 * @author Jorge L. Barroso
 * @version $Revision$ ($Author$)
 * @see org.displaytag.render.TableWriterTemplate
 */
public class HssfTableWriter extends TableWriterAdapter
{

    public static final HSSFRichTextString EMPTY_TEXT = new HSSFRichTextString("");

    protected MessageFormat totalLabel = new MessageFormat("{0} Total");

    protected boolean decorated = false;

    /**
     * The workbook to which the table is written.
     */
    private HSSFWorkbook wb;

    /**
     * Generated sheet.
     */
    protected HSSFSheet sheet;

    /**
     * Current row number.
     */
    protected int sheetRowNum;

    /**
     * Current row.
     */
    private HSSFRow currentRow;

    /**
     * Current column number.
     */
    protected int colNum;

    /**
     * Current cell.
     */
    protected HSSFCell currentCell;

    protected int currentGrouping = 0;

    /**
     * Percent Excel format.
     */

    protected short intFormat = HSSFDataFormat.getBuiltinFormat("0");

    /**
     * Some operations require the model.
     */
    protected TableModel model;

    protected String sheetName = "-";

    protected ExcelUtils utils;

    /**
     * This table writer uses an HSSF workbook to write the table.
     * @param wb The HSSF workbook to write the table.
     */
    public HssfTableWriter(HSSFWorkbook wb)
    {
        this.wb = wb;
        utils = new ExcelUtils(wb);
    }

    /**
     * @see org.displaytag.render.TableWriterTemplate#writeTableOpener(org.displaytag.model.TableModel)
     */
    protected void writeTableOpener(TableModel model) throws Exception
    {
        this.sheet = wb.createSheet(sheetName);
        setModel(model);
        init(model);
        this.sheetRowNum = 0;

    }

    /**
     * Override this to do local config, but you should call super() first so that this can set up the ExcelUtils.
     * @param model
     */
    protected void init(TableModel model)
    {
        utils.initCellStyles(model.getProperties());
    }

    /**
     * @see org.displaytag.render.TableWriterTemplate#writeCaption(org.displaytag.model.TableModel)
     */
    protected void writeCaption(TableModel model) throws Exception
    {
        HSSFCellStyle style = this.wb.createCellStyle();
        HSSFFont bold = this.wb.createFont();
        bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        bold.setFontHeightInPoints((short) 14);
        style.setFont(bold);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        this.colNum = 0;
        this.currentRow = this.sheet.createRow(this.sheetRowNum++);
        this.currentCell = this.currentRow.createCell(this.colNum);
        this.currentCell.setCellStyle(style);
        String caption = model.getCaption();
        this.currentCell.setCellValue(new HSSFRichTextString(caption));
        this.rowSpanTable(model);
    }

    /**
     * Obtain the region over which to merge a cell.
     * @param first Column number of first cell from which to merge.
     * @param last Column number of last cell over which to merge.
     * @return The region over which to merge a cell.
     */
    private CellRangeAddress getMergeCellsRegion(int first, int last)
    {
        return new CellRangeAddress(this.currentRow.getRowNum(), this.currentRow.getRowNum(), first, last);
    }

    /**
     * @see org.displaytag.render.TableWriterTemplate#writeTableHeader(org.displaytag.model.TableModel)
     */
    protected void writeTableHeader(TableModel model) throws Exception
    {
        this.currentRow = this.sheet.createRow(this.sheetRowNum++);
        this.colNum = 0;
        HSSFCellStyle headerStyle = this.getHeaderFooterStyle();
        for (HeaderCell headerCell : model.getHeaderCellList())
        {
            String columnHeader = headerCell.getTitle();
            if (columnHeader == null)
            {
                columnHeader = StringUtils.capitalize(headerCell.getBeanPropertyName());
            }

            this.writeHeaderFooter(columnHeader, this.currentRow, headerStyle);
        }
    }

    /**
     * @see org.displaytag.render.TableWriterTemplate#writeDecoratedRowStart(org.displaytag.model.TableModel)
     */
    protected void writeDecoratedRowStart(TableModel model)
    {
        model.getTableDecorator().startRow();
    }

    /**
     */
    protected void writeRowOpener(Row row) throws Exception
    {
        this.currentRow = this.sheet.createRow(sheetRowNum++);
        this.colNum = 0;
    }

    /**
     * Write a column's opening structure to a HSSF document.
     * @see org.displaytag.render.TableWriterTemplate#writeColumnOpener(org.displaytag.model.Column)
     */
    protected void writeColumnOpener(Column column) throws Exception
    {
        if (column != null)
        {
            column.getOpenTag(); // has side effect, setting its stringValue, which affects grouping logic.
        }
        this.currentCell = this.currentRow.createCell(this.colNum++);
    }

    /**
     * @see org.displaytag.render.TableWriterTemplate#writeColumnValue(Object,org.displaytag.model.Column)
     */
    @Override
    protected void writeColumnValue(Object value, Column column) throws Exception
    {
        // is this a detail row for a column that is currently grouped?
        int myGroup = column.getHeaderCell().getGroup();
        Object cellValue = column.getValue(this.decorated);
        if (myGroup > 0)
        {
            cellValue = "";
        }
        writeCellValue(cellValue);
    }


    /**
     * Override in subclasses to handle local data types.
     * @param value the value object to write
     */
    protected void writeCellValue(Object value)
    {
        if (value instanceof Number)
        {
            Number num = (Number) value;
            // Percentage
            if (value.toString().indexOf("%") > -1)
            {
                this.currentCell.setCellValue(num.doubleValue() / 100);
                this.currentCell.setCellStyle(utils.getStyle(ExcelUtils.STYLE_PCT));
            }
            else if (value instanceof Integer)
            {
                this.currentCell.setCellStyle(utils.getStyle(ExcelUtils.STYLE_INTEGER));
                this.currentCell.setCellValue(num.intValue());
            }
            else
            {
                this.currentCell.setCellValue(num.doubleValue());
            }
            
        }
        else if (value instanceof Date )
        {
            this.currentCell.setCellValue((Date) value);
            this.currentCell.setCellStyle(utils.getStyle(ExcelUtils.STYLE_DATE));
        }
        else if (value instanceof Calendar)
        {
            Calendar c = (Calendar) value;
            this.currentCell.setCellValue(c);
            this.currentCell.setCellStyle(utils.getStyle(ExcelUtils.STYLE_DATE));
        }
        else if (value == null)
        {
            this.currentCell.setCellValue(EMPTY_TEXT);
        }
        else
        {
            String v = value.toString();
            if (v.length() > utils.getWrapAtLength())
            {
                this.currentCell.getCellStyle().setWrapText(true);
            }
            this.currentCell.setCellValue(new HSSFRichTextString(ExcelUtils.escapeColumnValue(value)));
        }

    }

    /**
     * Decorators that help render the table to an HSSF table must implement DecoratesHssf.
     * @see org.displaytag.render.TableWriterTemplate#writeDecoratedRowFinish(org.displaytag.model.TableModel)
     */
    protected void writeDecoratedRowFinish(TableModel model) throws Exception
    {
        TableDecorator decorator = model.getTableDecorator();
        if (decorator instanceof DecoratesHssf)
        {
            DecoratesHssf hdecorator = (DecoratesHssf) decorator;
            hdecorator.setSheet(this.sheet);
        }
        decorator.finishRow();
        this.sheetRowNum = this.sheet.getLastRowNum();
        this.sheetRowNum++;
    }

    /**
     * @see org.displaytag.render.TableWriterTemplate#writePostBodyFooter(org.displaytag.model.TableModel)
     */
    protected void writePostBodyFooter(TableModel model) throws Exception
    {
        this.colNum = 0;
        this.currentRow = this.sheet.createRow(this.sheetRowNum++);
        this.writeHeaderFooter(model.getFooter(), this.currentRow, this.getHeaderFooterStyle());
        this.rowSpanTable(model);
    }

    /**
     * Make a row span the width of the table.
     * @param model The table model representing the rendered table.
     */
    private void rowSpanTable(TableModel model)
    {
        this.sheet.addMergedRegion(this.getMergeCellsRegion(this.currentCell.getColumnIndex(), (model
            .getNumberOfColumns() - 1)));
    }

    /**
     * @see org.displaytag.render.TableWriterTemplate#writeDecoratedTableFinish(org.displaytag.model.TableModel)
     */
    protected void writeDecoratedTableFinish(TableModel model)
    {
        model.getTableDecorator().finish();
    }

    /**
     * Is this value numeric? You should probably override this method to handle your locale.
     * @param rawValue the object value
     * @return true if numeric
     */
    protected boolean isNumber(String rawValue)
    {
        if (rawValue == null)
        {
            return false;
        }
        String rawV = rawValue;
        if (rawV.indexOf('%') > -1)
        {
            rawV = rawV.replace('%', ' ').trim();
        }
        if (rawV.indexOf('$') > -1)
        {
            rawV = rawV.replace('$', ' ').trim();
        }
        if (rawV.indexOf(',') > -1)
        {
            rawV = StringUtils.replace(rawV, ",", "");
        }
        return NumberUtils.isNumber(rawV.trim());
    }

    /**
     * Writes a table header or a footer.
     * @param value Header or footer value to be rendered.
     * @param row The row in which to write the header or footer.
     * @param style Style used to render the header or footer.
     */
    private void writeHeaderFooter(String value, HSSFRow row, HSSFCellStyle style)
    {
        this.currentCell = row.createCell(this.colNum++);
        this.currentCell.setCellValue(new HSSFRichTextString(value));
        this.currentCell.setCellStyle(style);
    }

    /**
     * Obtain the style used to render a header or footer.
     * @return The style used to render a header or footer.
     */
    private HSSFCellStyle getHeaderFooterStyle()
    {
        HSSFCellStyle style = this.wb.createCellStyle();
//        style.setFillPattern(HSSFCellStyle.FINE_DOTS);
//        style.setFillBackgroundColor(HSSFColor.BLUE_GREY.index);
        HSSFFont bold = this.wb.createFont();
        bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//        bold.setColor(HSSFColor.WHITE.index);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        style.setFont(bold);
        return style;
    }

    /**
     * @see org.displaytag.render.TableWriterAdapter#writeBottomBanner(org.displaytag.model.TableModel)
     */
    protected void writeBottomBanner(TableModel model) throws Exception
    {
        // adjust the column widths
        int colCount = 0;
        while (colCount <= colNum)
        {
            sheet.autoSizeColumn((short) colCount++);
        }
    }

    @Override
    protected void writeSubgroupStart(TableModel model) throws Exception
    {
        TableTotaler tt = model.getTotaler();
        if (tt.howManyGroups == 0)
        {
            return;
        }

        // for each newly opened subgroup we need to output the opener, in order;
        //   so we need to know somehow which groups are new since we last wrote out openers; how about we track a list of the
        //    already opened groups, and ask the tt for a list of all known groups?

        for (int dtColumnNumber : tt.getOpenedColumns())
        {
            currentGrouping++;
            writeRowOpener(null);
            // for each subgroup

            for (HeaderCell cell : model.getHeaderCellList())
            {
                writeColumnOpener(null);
                int thisCellAsDtNumber = asDtColNumber(cell.getColumnNumber());
                String columnValue = ( thisCellAsDtNumber != dtColumnNumber) ? "" : tt.getGroupingValue(dtColumnNumber);
                writeCellValue(columnValue);
                writeColumnCloser(null);
            }

            writeRowCloser(null);
                // Have to handle a case where this is a nested subgroup start;
                // put out the blanks for any column that has already exists
            // now write the label for the group that is opening
        }
    }

    /**
     * DT columns are 1 based, excel columns are 0 based.
     * @param cellColumnNumber
     * @return
     */
    protected int asDtColNumber(int cellColumnNumber){
        return cellColumnNumber + 1;
    }

    public String getTotalLabel(String groupingValue)
    {
        String gv = StringUtils.defaultString(groupingValue);
        return totalLabel.format( "{0} Total", gv );
    }

    @Override
    protected void writeSubgroupStop(TableModel model) throws Exception
    {
        TableTotaler tt = model.getTotaler();

        // for each newly opened subgroup we need to output the opener, in order;
        //   so we need to know somehow which groups are new since we last wrote out openers; how about we track a list of the
        //    already opened groups, and ask the tt for a list of all known groups?

        if (tt.howManyGroups == 0)
        {
            return;
        }
        List<Integer> closedColumns = tt.getClosedColumns();
        Collections.reverse(closedColumns);
        for (int columnNumber : closedColumns)
        {
            writeRowOpener(null);
            // for each subgroup

            for (HeaderCell cell : model.getHeaderCellList())
            {
                writeColumnOpener(null);
                Object columnValue;
                int cellColumnNumberAsDt = asDtColNumber(cell.getColumnNumber());
                if (cellColumnNumberAsDt > columnNumber && cell.isTotaled())
                {
                    columnValue = tt.getTotalForColumn(cell.getColumnNumber(), currentGrouping);
                }
                else if (cellColumnNumberAsDt == columnNumber)
                {
                    columnValue = getTotalLabel(tt.getGroupingValue(columnNumber));
                }
                else
                {
                    columnValue = null;
                }
                writeCellValue(columnValue);
                writeColumnCloser(null);
            }

            writeRowCloser(null);
            writeGroupExtraInfo(model);
            currentGrouping--;
        }

        assert currentGrouping > -1;
        super.writeSubgroupStop(model);
    }

    public void setModel(TableModel m)
    {
        m.setTableDecorator(XmlTotalsWriter.NOOP);
        if (m.getTotaler() == null || m.getTotaler() == TableTotaler.NULL)
        {
            TableTotaler tt = new TableTotaler();
            tt.init(m);
            m.setTotaler(tt);
        }
        this.model = m;
    }

    public String getSheetName()
    {
        return sheetName;
    }

    public void setSetSheetName(String name)
    {
        this.sheetName = name;
    }

    public HSSFSheet getSheet()
    {
        return sheet;
    }

    @Override
    protected void writeTableBodyCloser(TableModel model) throws Exception
    {
        //write totals, if there are any
        boolean hasTotals = false;
        for (HeaderCell cell : model.getHeaderCellList())
        {
            hasTotals = hasTotals || cell.isTotaled();
        }
        if (!hasTotals)
        {
            return;
        }
        TableTotaler tt = model.getTotaler();
        writeRowOpener(null);
        for (HeaderCell cell : model.getHeaderCellList())
        {
            writeColumnOpener(null);
            Object columnValue = (cell.isTotaled()) ? tt.getTotalForColumn(cell.getColumnNumber(), 0) : null;
            writeCellValue(columnValue);
            CellStyle st = utils.getNewCellStyle();
            st.cloneStyleFrom(currentCell.getCellStyle());
            st.setBorderTop(CellStyle.BORDER_THIN);
            st.setTopBorderColor(IndexedColors.BLACK.getIndex());
            currentCell.setCellStyle(st);
            writeColumnCloser(null);
        }
        writeRowCloser(null);
    }

    protected void writeGroupExtraInfo(TableModel model) throws Exception
    {
    }
 }
