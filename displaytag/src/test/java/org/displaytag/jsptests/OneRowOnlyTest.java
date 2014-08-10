package org.displaytag.jsptests;

import org.displaytag.test.DisplaytagCase;
import org.junit.Assert;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;


/**
 * A table with a single row.
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public class OneRowOnlyTest extends DisplaytagCase
{

    /**
     * @see org.displaytag.test.DisplaytagCase#getJspName()
     */
    public String getJspName()
    {
        return "onerow.jsp";
    }

    /**
     * Checks for the expected values in columns.
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
            log.debug("RESPONSE: " + response.getText());
        }

        WebTable[] tables = response.getTables();

        Assert.assertEquals("Wrong number of tables.", 1, tables.length);

        Assert.assertEquals("Bad number of generated columns.", 2, tables[0].getColumnCount());
        Assert.assertEquals("Bad number of generated rows.", 2, tables[0].getRowCount());

        Assert.assertEquals("Bad content in column 1.", "ant", tables[0].getCellAsText(1, 0));
        Assert.assertEquals("Bad content in column 2.", "bee", tables[0].getCellAsText(1, 1));
    }
}