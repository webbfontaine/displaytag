package org.displaytag.jsptests;

import org.displaytag.test.DisplaytagCase;
import org.junit.Assert;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;


/**
 * Test for DISPL-234 - HTML title not added with chopped value (column tag - maxLength attribute).
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public class Displ234Test extends DisplaytagCase
{

    /**
     * @see org.displaytag.test.DisplaytagCase#getJspName()
     */
    public String getJspName()
    {
        return "DISPL-234.jsp";
    }

    /**
     * Title should be added to td.
     * @param jspName jsp name, with full path
     * @throws Exception any axception thrown during test.
     */
    @Override
    @Test
    public void doTest() throws Exception
    {
        WebRequest request = new GetMethodWebRequest(getJspUrl(getJspName()));

        WebResponse response = runner.getResponse(request);

        if (log.isDebugEnabled())
        {
            log.debug(response.getText());
        }

        WebTable[] tables = response.getTables();
        Assert.assertEquals("Wrong number of tables in result.", 1, tables.length);
        Assert.assertEquals("Wrong number of rows in result.", 2, tables[0].getRowCount());

        Assert.assertEquals("Wrong or missing title for cropped text.", "123456789012", tables[0]
            .getTableCell(1, 0)
            .getAttribute("title"));
        Assert.assertEquals("Wrong or missing title for cropped text.", "12345678901234", tables[0]
            .getTableCell(1, 1)
            .getAttribute("title"));
        Assert.assertEquals(
            "Wrong or missing title for cropped text.",
            "12345678901234567",
            tables[0].getTableCell(1, 2).getAttribute("title"));
        Assert.assertEquals("Title should not be added.", "", tables[0].getTableCell(1, 3).getAttribute("title"));

    }

}