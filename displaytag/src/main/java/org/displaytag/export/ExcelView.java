package org.displaytag.export;

import org.apache.commons.lang.StringUtils;
import org.displaytag.model.TableModel;

/**
 * Export view for excel exporting
 * @author fgiust
 * @version $Revision$ ($Author$)
 */
public class ExcelView extends BaseExportView
{

    /**
     * @see org.displaytag.export.BaseExportView#BaseExportView(TableModel, boolean, boolean)
     */
    public ExcelView(TableModel tableModel, boolean exportFullList, boolean includeHeader)
    {
        super(tableModel, exportFullList, includeHeader);
    }

    /**
     * @see org.displaytag.export.BaseExportView#getMimeType()
     * @return "application/vnd.ms-excel"
     */
    public String getMimeType()
    {
        return "application/vnd.ms-excel";
    }

    /**
     * @see org.displaytag.export.BaseExportView#getRowStart()
     * @return ""
     */
    protected String getRowStart()
    {
        return "";
    }

    /**
     * @see org.displaytag.export.BaseExportView#getRowEnd()
     * @return "\n"
     */
    protected String getRowEnd()
    {
        return "\n";
    }

    /**
     * @see org.displaytag.export.BaseExportView#getCellStart()
     * @return ""
     */
    protected String getCellStart()
    {
        return "";
    }

    /**
     * @see org.displaytag.export.BaseExportView#getCellEnd()
     * @return "\t"
     */
    protected String getCellEnd()
    {
        return "\t";
    }

    /**
     * @see org.displaytag.export.BaseExportView#getDocumentStart()
     * @return ""
     */
    protected String getDocumentStart()
    {
        return "";
    }

    /**
     * @see org.displaytag.export.BaseExportView#getDocumentEnd()
     * @return ""
     */
    protected String getDocumentEnd()
    {
        return "";
    }

    /**
     * @see org.displaytag.export.BaseExportView#getAlwaysAppendCellEnd()
     * @return false
     */
    protected boolean getAlwaysAppendCellEnd()
    {
        return false;
    }

    /**
     * @see org.displaytag.export.BaseExportView#getAlwaysAppendRowEnd()
     * @return false
     */
    protected boolean getAlwaysAppendRowEnd()
    {
        return false;
    }

    /**
     * Escaping for excel format:
     * <ul>
     * <li>Quotes inside quoted strings are escaped with a double quote.</li>
     * <li>Fields are surrounded by "" (should be optional, but sometimes you get a "Sylk error" without those)</li>
     * </ul>
     * @see org.displaytag.export.BaseExportView#escapeColumnValue(java.lang.Object)
     */
    protected Object escapeColumnValue(Object value)
    {
        if (value != null)
        {
            // quotes around fields are needed to avoid occasional "Sylk format invalid" messages from excel
            return "\"" + StringUtils.replace(StringUtils.trim(value.toString()), "\"", "\"\"") + "\"";
        }

        return null;
    }

}
