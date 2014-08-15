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
package org.displaytag.util;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.commons.lang3.ClassUtils;
import org.displaytag.model.Cell;
import org.displaytag.model.Column;
import org.displaytag.model.HeaderCell;
import org.displaytag.model.Row;
import org.displaytag.model.TableModel;
import org.displaytag.pagination.NumberedPage;
import org.displaytag.pagination.Pagination;
import org.displaytag.pagination.SmartListHelper;
import org.displaytag.properties.TableProperties;
import org.displaytag.tags.ColumnTag;
import org.junit.Assert;
import org.junit.Test;


/**
 * Check that toString() methods are constructed appropriately, uses the correct style and that there aren't stupid NPE
 * bugs in them.
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public class ToStringTest
{

    /**
     * ToString methods should be costructed using toStringBuilder and the <code>ShortToStringStyle.SHORT_STYLE</code>
     * style.
     * @param object test instance
     */
    private void checkToString(Object object)
    {
        String toString = object.toString();
        Assert.assertTrue(toString.startsWith(ClassUtils.getShortClassName(object, null)));
    }

    /**
     * ToString() test.
     */
    @Test
    public void testSmartListHelper()
    {
        checkToString(new SmartListHelper(new ArrayList<Object>(), 100, 10, TableProperties.getInstance(null), false));
    }

    /**
     * ToString() test.
     */
    @Test
    public void testNumberedPage()
    {
        checkToString(new NumberedPage(1, false));
    }

    /**
     * ToString() test.
     */
    @Test
    public void testPagination()
    {
        checkToString(new Pagination(null, null, null));
    }

    /**
     * ToString() test.
     */
    @Test
    public void testCell()
    {
        checkToString(new Cell(null));
    }

    /**
     * ToString() test.
     */
    @Test
    public void testHeaderCell()
    {
        checkToString(new HeaderCell());
    }

    /**
     * ToString() test.
     */
    @Test
    public void testColumn()
    {
        checkToString(new Column(new HeaderCell(), null, null));
    }

    /**
     * ToString() test.
     */
    @Test
    public void testRow()
    {
        checkToString(new Row(null, 0));
    }

    /**
     * ToString() test.
     */
    @Test
    public void testTableModel()
    {
        checkToString(new TableModel(null, null, null));
    }

    /**
     * ToString() test.
     */
    @Test
    public void testColumnTag()
    {
        checkToString(new ColumnTag());
    }

}
