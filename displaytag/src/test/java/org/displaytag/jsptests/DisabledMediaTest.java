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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.displaytag.tags.ColumnTag;
import org.displaytag.test.DisplaytagCase;
import org.junit.Assert;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpInternalErrorException;
import com.meterware.httpunit.WebRequest;


/**
 * Test for #968559.
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public class DisabledMediaTest extends DisplaytagCase
{

    /**
     * @see org.displaytag.test.DisplaytagCase#getJspName()
     */
    public String getJspName()
    {
        return "disabledmedia.jsp";
    }

    /**
     * Should not break on media="foo", since foo could be a valid media not enabled at runtime.
     * @param jspName jsp name, with full path
     * @throws Exception any axception thrown during test.
     */
    @Override
    @Test
    public void doTest() throws Exception
    {
        // test keep
        WebRequest request = new GetMethodWebRequest(getJspUrl(getJspName()));

        // save previous level, since we are expeting an excetion I don't want to fill logs
        Level previousLevel = Logger.getLogger(ColumnTag.class).getLevel();
        Logger.getLogger(ColumnTag.class).setLevel(Level.OFF);

        try
        {
            runner.getResponse(request);
        }
        catch (HttpInternalErrorException e)
        {
            Assert.fail("Should not get any error also if \"foo\" media type is not defined. " + e.getMessage());
        }
        finally
        {
            Logger.getLogger(ColumnTag.class).setLevel(previousLevel);
        }

        // ok

    }

}