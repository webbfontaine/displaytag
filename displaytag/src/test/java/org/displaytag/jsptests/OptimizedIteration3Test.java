package org.displaytag.jsptests;

import org.displaytag.properties.MediaTypeEnum;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.test.DisplaytagCase;
import org.displaytag.util.ParamEncoder;
import org.junit.Assert;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;


/**
 * Tests for optimized iterations (don't evaluate unneeded body of columns).
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public class OptimizedIteration3Test extends DisplaytagCase
{

    /**
     * @see org.displaytag.test.DisplaytagCase#getJspName()
     */
    public String getJspName()
    {
        return "optimizediteration3.jsp";
    }

    /**
     * Verifies that the generated page contains the pagination links with the inupt parameter. Tests #917200 ("{}" in
     * parameters).
     * @param jspName jsp name, with full path
     * @throws Exception any axception thrown during test.
     */
    @Override
    @Test
    public void doTest() throws Exception
    {

        WebRequest request = new GetMethodWebRequest(getJspUrl(getJspName()));
        ParamEncoder encoder = new ParamEncoder("table");

        // not sorted
        request.setParameter(encoder.encodeParameterName(TableTagParameters.PARAMETER_PAGE), "1");
        checkNumberOfIterations(runner.getResponse(request), 1);

        // page 2, sorted
        request.setParameter(encoder.encodeParameterName(TableTagParameters.PARAMETER_SORT), "1");
        request.setParameter(encoder.encodeParameterName(TableTagParameters.PARAMETER_PAGE), "2");
        checkNumberOfIterations(runner.getResponse(request), 1);

        // export full
        request.setParameter(encoder.encodeParameterName(TableTagParameters.PARAMETER_PAGE), "2");
        request.setParameter(
            encoder.encodeParameterName(TableTagParameters.PARAMETER_EXPORTTYPE),
            Integer.toString(MediaTypeEnum.CSV.getCode()));
        request.setParameter(encoder.encodeParameterName(TableTagParameters.PARAMETER_PAGE), "1");

        WebResponse response = runner.getResponse(request);
        String csvExport = response.getText();
        if (log.isDebugEnabled())
        {
            log.debug(response.getText());
        }

        Assert.assertEquals("Wrong csv export", "ant,1\nant,2\nant,3\nant,4\n", csvExport);

    }

    /**
     * @param response WebResponse
     * @param iterations expected number of iterations
     * @throws Exception any axception thrown during test.
     */
    private void checkNumberOfIterations(WebResponse response, int iterations) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug(response.getText());
        }

        WebTable[] tables = response.getTables();
        Assert.assertEquals("Expected 1 table in result.", 1, tables.length);
        Assert.assertEquals("Expected 2 rows in table.", 2, tables[0].getRowCount());

        Assert.assertEquals(
            "Wrong number of iterations. Evaluated column bodies number is different from expected",
            Integer.toString(iterations),
            response.getElementWithID("iterations").getText());
    }
}