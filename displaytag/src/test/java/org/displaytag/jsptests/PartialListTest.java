/**
 * Copyright (C) 2002-2014 Fabrizio Giustina, the Displaytag team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.displaytag.jsptests;

import org.displaytag.tags.TableTagParameters;
import org.displaytag.test.DisplaytagCase;
import org.displaytag.util.ParamEncoder;
import org.junit.Assert;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;


/**
 * Basic tests for pagination.
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public class PartialListTest extends DisplaytagCase
{

    /**
     * @see org.displaytag.test.DisplaytagCase#getJspName()
     */
    public String getJspName()
    {
        return "partialList.jsp";
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

        ParamEncoder p2 = new ParamEncoder("table2");
        ParamEncoder p3 = new ParamEncoder("table3");

        request.setParameter(p2.encodeParameterName(TableTagParameters.PARAMETER_PAGE), "2"); // table 2 start page 2
        request.setParameter(p3.encodeParameterName(TableTagParameters.PARAMETER_SORT), "0"); // table 3 sort 0
        request.setParameter(p3.encodeParameterName(TableTagParameters.PARAMETER_ORDER), "1"); // table 3 order desc

        WebResponse response = runner.getResponse(request);

        if (log.isDebugEnabled())
        {
            log.debug("RESPONSE: " + response.getText());
        }

        WebLink[] links = response.getLinks();
        WebTable[] tables = response.getTables();
        // ensure all our search bar links contain page 2 as the param since we only have 2 pages
        Assert.assertEquals(
            "2",
            links[0].getParameterValues(p2.encodeParameterName(TableTagParameters.PARAMETER_PAGE))[0]);
        Assert.assertEquals(
            "2",
            links[1].getParameterValues(p2.encodeParameterName(TableTagParameters.PARAMETER_PAGE))[0]);
        Assert.assertEquals(
            "2",
            links[2].getParameterValues(p2.encodeParameterName(TableTagParameters.PARAMETER_PAGE))[0]);
        Assert.assertEquals(3, tables[0].getRowCount()); // title row + 2 data's
        Assert.assertEquals("1", tables[0].getCellAsText(1, 0));
        Assert.assertEquals("4", tables[0].getCellAsText(2, 0));

        // second table assertions
        // links should point to first page
        Assert.assertEquals(
            "1",
            links[4].getParameterValues(p2.encodeParameterName(TableTagParameters.PARAMETER_PAGE))[0]);
        Assert.assertEquals(
            "1",
            links[5].getParameterValues(p2.encodeParameterName(TableTagParameters.PARAMETER_PAGE))[0]);
        Assert.assertEquals(
            "1",
            links[6].getParameterValues(p2.encodeParameterName(TableTagParameters.PARAMETER_PAGE))[0]);
        Assert.assertEquals(3, tables[1].getRowCount()); // title row + 2 data's
        Assert.assertEquals("1", tables[1].getCellAsText(1, 0));
        Assert.assertEquals("4", tables[1].getCellAsText(2, 0));

        // third table assertions
        Assert.assertEquals(3, tables[2].getRowCount()); // title row + 2 data's
        Assert.assertEquals("4", tables[2].getCellAsText(1, 0));
        Assert.assertEquals("1", tables[2].getCellAsText(2, 0));
    }
}