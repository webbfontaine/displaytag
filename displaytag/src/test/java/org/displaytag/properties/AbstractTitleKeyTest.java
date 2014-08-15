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
package org.displaytag.properties;

import org.displaytag.localization.I18nResourceProvider;
import org.displaytag.localization.LocaleResolver;
import org.displaytag.test.DisplaytagCase;
import org.junit.Assert;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;


/**
 * Tests for "titlekey" column attribute.
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public abstract class AbstractTitleKeyTest extends DisplaytagCase
{

    /**
     * @see org.displaytag.test.DisplaytagCase#getJspName()
     */
    public String getJspName()
    {
        return "titlekey.jsp";
    }

    /**
     * Returns the LocaleResolver instance to be used in this test.
     * @return LocaleResolver
     */
    protected abstract LocaleResolver getResolver();

    /**
     * Returns the I18nResourceProvider instance to be used in this test.
     * @return I18nResourceProvider
     */
    protected abstract I18nResourceProvider getI18nResourceProvider();

    /**
     * Returns the suffix expected in the specific resource bundle.
     * @return expected suffix
     */
    protected abstract String getExpectedSuffix();

    /**
     * Test that headers are correctly removed.
     * @param jspName jsp name, with full path
     * @throws Exception any axception thrown during test.
     */
    @Override
    @Test
    public void doTest() throws Exception
    {
        // test keep
        WebRequest request = new GetMethodWebRequest(getJspUrl(getJspName()));

        TableProperties.setLocaleResolver(getResolver());
        TableProperties.setResourceProvider(getI18nResourceProvider());

        WebResponse response;
        try
        {
            response = runner.getResponse(request);
        }
        finally
        {
            // reset
            TableProperties.setLocaleResolver(null);
            TableProperties.setResourceProvider(null);
        }

        if (log.isDebugEnabled())
        {
            log.debug(response.getText());
        }

        WebTable[] tables = response.getTables();
        Assert.assertEquals("Expected one table", 1, tables.length);

        Assert.assertEquals("Header from resource is not valid.", //
            "foo title" + getExpectedSuffix(),
            tables[0].getCellAsText(0, 0));

        Assert.assertEquals("Header from resource is not valid.", //
            "baz title" + getExpectedSuffix(),
            tables[0].getCellAsText(0, 1));

        Assert.assertEquals("Header from resource is not valid.", //
            "camel title" + getExpectedSuffix(),
            tables[0].getCellAsText(0, 2));

        Assert.assertEquals(
            "Missing resource should generate the ???missing??? header.",
            "???missing???",
            tables[0].getCellAsText(0, 3));

    }
}