package org.displaytag.jsptests;

import org.displaytag.test.DisplaytagCase;
import org.junit.Assert;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;


/**
 * Tests for content in column body.
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public class ColumnBodyTest extends DisplaytagCase
{

    /**
     * @see org.displaytag.test.DisplaytagCase#getJspName()
     */
    public String getJspName()
    {
        return "columnbody.jsp";
    }

    /**
     * Check content in generated table.
     * @param jspName jsp name, with full path
     * @throws Exception any axception thrown during test.
     */
    @Override
    @Test
    public void doTest() throws Exception
    {
        WebRequest request = new GetMethodWebRequest(getJspUrl(getJspName()));
        WebResponse response;

        response = runner.getResponse(request);

        WebTable[] tables = response.getTables();
        Assert.assertEquals("Wrong number of tables in result.", 1, tables.length);

        if (log.isDebugEnabled())
        {
            log.debug(response.getText());
        }

        for (int j = 0; j < tables[0].getColumnCount(); j++)
        {
            Assert.assertEquals("Wrong content in table cell", "ant", tables[0].getCellAsText(1, j));
        }

    }
}