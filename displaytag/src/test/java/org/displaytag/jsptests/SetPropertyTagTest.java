package org.displaytag.jsptests;

import org.displaytag.test.DisplaytagCase;
import org.junit.Assert;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;


/**
 * Tests for SetProperty tag.
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public class SetPropertyTagTest extends DisplaytagCase
{

    /**
     * @see org.displaytag.test.DisplaytagCase#getJspName()
     */
    public String getJspName()
    {
        return "setproperty.jsp";
    }

    /**
     * Check that the "show header" property only affects the correct tables in the page.
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

        Assert.assertEquals("Expected 3 table in result.", 3, tables.length);

        Assert.assertEquals("First table should contain one row only", 1, tables[0].getRowCount());
        Assert.assertEquals("Second table should contain header plus one row", 2, tables[1].getRowCount());
        Assert.assertEquals("Third table should contain one row only", 1, tables[2].getRowCount());
    }
}