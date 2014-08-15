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
package org.displaytag.decorator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.displaytag.model.Row;
import org.displaytag.model.RowIterator;
import org.displaytag.model.TableModel;
import org.displaytag.pagination.SmartListHelper;
import org.displaytag.properties.TableProperties;
import org.junit.Test;


/**
 * Test for TableDecorator with pagination. If you set up pagination and iterate through the entire page, you should
 * always be on the last row at the end. If you have grouped totals, the first group on a page other than the first
 * should start at the page offset, not at 0.
 * @author Robert West
 * @version $Revision: 1 $ ($Author: rwest $)
 */
public class TableDecoratorPaginationTest
{

    @Test
    public void testSinglePage()
    {
        List<Integer> rawData = new ArrayList<Integer>(10);
        List<Object> data = new ArrayList<Object>(10);
        for (int i = 1; i <= 10; i++)
        {
            rawData.add(i);
            data.add(new Row(i, i));
        }

        TableProperties props = TableProperties.getInstance(null);
        SmartListHelper helper = new SmartListHelper(data, data.size(), 10, props, false);
        helper.setCurrentPage(1);
        List fullList = helper.getListForCurrentPage();

        TableModel model = new TableModel(props, "", null);
        model.setRowListPage(fullList);
        model.setPageOffset(helper.getFirstIndexForCurrentPage());

        MultilevelTotalTableDecorator decorator = new MultilevelTotalTableDecorator();
        decorator.init(null, rawData, model);
        model.setTableDecorator(decorator);

        RowIterator iterator = model.getRowIterator(false);
        while (iterator.hasNext())
        {
            iterator.next();
        }

        assertEquals(decorator.isLastRow(), true);
    }

    @Test
    public void testFirstPage()
    {
        List<Integer> rawData = new ArrayList<Integer>(10);
        List<Object> data = new ArrayList<Object>(10);
        for (int i = 1; i <= 10; i++)
        {
            rawData.add(i);
            data.add(new Row(i, i));
        }

        TableProperties props = TableProperties.getInstance(null);
        SmartListHelper helper = new SmartListHelper(data, data.size(), 5, props, false);
        helper.setCurrentPage(1);
        List fullList = helper.getListForCurrentPage();

        TableModel model = new TableModel(props, "", null);
        model.setRowListPage(fullList);
        model.setPageOffset(helper.getFirstIndexForCurrentPage());

        MultilevelTotalTableDecorator decorator = new MultilevelTotalTableDecorator();
        decorator.init(null, rawData, model);
        model.setTableDecorator(decorator);

        RowIterator iterator = model.getRowIterator(false);
        while (iterator.hasNext())
        {
            iterator.next();
        }

        assertEquals(decorator.isLastRow(), true);
    }

    @Test
    public void testSecondPage()
    {
        List<Integer> rawData = new ArrayList<Integer>(10);
        List<Object> data = new ArrayList<Object>(10);
        for (int i = 1; i <= 10; i++)
        {
            rawData.add(i);
            data.add(new Row(i, i));
        }

        TableProperties props = TableProperties.getInstance(null);
        SmartListHelper helper = new SmartListHelper(data, data.size(), 5, props, false);
        helper.setCurrentPage(2);
        List fullList = helper.getListForCurrentPage();

        TableModel model = new TableModel(props, "", null);
        model.setRowListPage(fullList);
        model.setPageOffset(helper.getFirstIndexForCurrentPage());

        MultilevelTotalTableDecorator decorator = new MultilevelTotalTableDecorator();
        decorator.init(null, rawData, model);
        model.setTableDecorator(decorator);

        RowIterator iterator = model.getRowIterator(false);
        while (iterator.hasNext())
        {
            iterator.next();
        }

        assertEquals(decorator.isLastRow(), true);
    }
}
