package org.displaytag.util;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for org.displaytag.util.HtmlTagUtil.
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public class HtmlTagUtilTest
{

    /**
     * Test for the stripHTMLTags() method.
     */
    @Test
    public void testStripHtmlTagsSimple()
    {
        Assert.assertEquals("well done", HtmlTagUtil.stripHTMLTags("<strong>well</strong> done"));
    }

    /**
     * Test for the stripHTMLTags() method.
     */
    @Test
    public void testStripHtmlTagsNoHtml()
    {
        Assert.assertEquals("well done", HtmlTagUtil.stripHTMLTags("well done"));
    }

    /**
     * Test for the stripHTMLTags() method.
     */
    @Test
    public void testStripHtmlTagsWithQuote()
    {
        Assert.assertEquals("&quot;well&quot; done", HtmlTagUtil.stripHTMLTags("\"well\" done"));
    }

    /**
     * Test for the stripHTMLTags() method.
     */
    @Test
    public void testStripHtmlTagsMultiple()
    {
        Assert.assertEquals(
            "well done again",
            HtmlTagUtil.stripHTMLTags("<a href=\"go\"><strong>well <em>done</em></strong> again</a>"));
    }

    /**
     * Test for the stripHTMLTags() method.
     */
    @Test
    public void testStripHtmlUnbalancedOpenStart()
    {
        Assert.assertEquals("", HtmlTagUtil.stripHTMLTags("<well done"));
    }

    /**
     * Test for the stripHTMLTags() method.
     */
    @Test
    public void testStripHtmlUnbalancedOpenMiddle()
    {
        Assert.assertEquals("well ", HtmlTagUtil.stripHTMLTags("well <done"));
    }

    /**
     * Test for the stripHTMLTags() method.
     */
    @Test
    public void testStripHtmlUnbalancedClosed()
    {
        Assert.assertEquals("well> done", HtmlTagUtil.stripHTMLTags("well> done"));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByLengthNoHtml()
    {
        Assert.assertEquals("well...", HtmlTagUtil.abbreviateHtmlString("well done", 4, false));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByLengthShorter()
    {
        Assert.assertEquals("well done", HtmlTagUtil.abbreviateHtmlString("well done", 9, false));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByLengthHtmlShorter()
    {
        Assert.assertEquals(
            "<strong>well</strong> done",
            HtmlTagUtil.abbreviateHtmlString("<strong>well</strong> done", 10, false));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByLengthEntity()
    {
        Assert.assertEquals("&amp; wel...", HtmlTagUtil.abbreviateHtmlString("&amp; well done", 5, false));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByLengthBrokenEntity()
    {
        Assert.assertEquals("&amp;...", HtmlTagUtil.abbreviateHtmlString("&amp; well done", 1, false));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByLengthUnescapedAmpersand()
    {
        Assert.assertEquals("& well d...", HtmlTagUtil.abbreviateHtmlString("& well done", 8, false));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByLengthHtmlSimple()
    {
        Assert.assertEquals(
            "<strong>well...</strong>",
            HtmlTagUtil.abbreviateHtmlString("<strong>well done</strong>", 4, false));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByLengthHtmlNoClosingTag()
    {
        Assert.assertEquals("well<br> ...", HtmlTagUtil.abbreviateHtmlString("well<br> done", 5, false));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByLengthHtmlEndingTag()
    {
        Assert.assertEquals("well<br>", HtmlTagUtil.abbreviateHtmlString("well<br>", 5, false));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByLengthHtmlEmptyTag()
    {
        Assert.assertEquals("well <br/> ...", HtmlTagUtil.abbreviateHtmlString("well <br/> done", 6, false));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByLengthHtmlComplex()
    {
        Assert.assertEquals(
            "<a href=\"link\">well...</a>",
            HtmlTagUtil.abbreviateHtmlString("<a href=\"link\">well <strong>done</strong></a>", 4, false));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByNumberOfWordsNoHtml()
    {
        Assert.assertEquals("well...", HtmlTagUtil.abbreviateHtmlString("well done", 1, true));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByNumberOfWordsShorter()
    {
        Assert.assertEquals("well done", HtmlTagUtil.abbreviateHtmlString("well done", 2, true));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByNumberOfWordsHtmlShorter()
    {
        Assert.assertEquals(
            "<strong>well</strong> done",
            HtmlTagUtil.abbreviateHtmlString("<strong>well</strong> done", 3, true));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByLengthNumberOfWordsHtmlSimple()
    {
        Assert.assertEquals(
            "<strong>well...</strong>",
            HtmlTagUtil.abbreviateHtmlString("<strong>well done</strong>", 1, true));
    }

    /**
     * Test for the abbreviateHtmlString() method.
     */
    @Test
    public void testAbbreviateHtmlStringByNumberOfWordsHtmlComplex()
    {
        Assert.assertEquals(
            "<a href=\"link\">well...</a>",
            HtmlTagUtil.abbreviateHtmlString("<a href=\"link\">well <strong>done</strong></a>", 1, true));
    }

}