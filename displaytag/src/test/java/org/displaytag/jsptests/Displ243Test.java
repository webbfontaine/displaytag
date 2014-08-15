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

import org.displaytag.properties.SortOrderEnum;
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
 * Test for DISPL-243 - Default column sort breaks sorting after a few sorts of the column
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public class Displ243Test extends DisplaytagCase
{

    /**
     * @see org.displaytag.test.DisplaytagCase#getJspName()
     */
    public String getJspName()
    {
        return "DISPL-243.jsp";
    }

    /**
     * CHeck sort order after some clicks
     * @param jspName jsp name, with full path
     * @throws Exception any axception thrown during test.
     */
    @Override
    @Test
    public void doTest() throws Exception
    {
        WebRequest request = new GetMethodWebRequest(getJspUrl(getJspName()));
        ParamEncoder encoder = new ParamEncoder("table");
        String orderParameter = encoder.encodeParameterName(TableTagParameters.PARAMETER_ORDER);

        WebResponse response = runner.getResponse(request);

        if (log.isDebugEnabled())
        {
            log.debug(response.getText());
        }

        WebTable[] tables = response.getTables();
        Assert.assertEquals("Wrong number of tables.", 1, tables.length);

        WebLink[] links = response.getLinks();
        Assert.assertEquals("Wrong number of links.", 1, links.length);

        Assert.assertEquals(
            "wrong sorting order",
            Integer.toString(SortOrderEnum.DESCENDING.getCode()),
            links[0].getParameterValues(orderParameter)[0]);

        // a few clicks...
        for (int j = 0; j < 10; j++)
        {
            String expectedSortOrder = (j % 2 == 0) ? SortOrderEnum.ASCENDING.getName() : SortOrderEnum.DESCENDING
                .getName();

            response = links[0].click();

            if (log.isDebugEnabled())
            {
                log.debug(response.getText());
            }

            tables = response.getTables();
            Assert.assertEquals("Wrong number of tables.", 1, tables.length);

            links = response.getLinks();
            Assert.assertEquals("Wrong number of links.", 1, links.length);

            Assert.assertEquals(
                "Wrong sorting order for iteration " + j,
                expectedSortOrder,
                SortOrderEnum.fromCode(Integer.parseInt(links[0].getParameterValues(orderParameter)[0])).getName());
        }

    }

}