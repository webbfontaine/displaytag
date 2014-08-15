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
import java.util.List;


/**
 * Utility methods for dealing with html tags.
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public final class HtmlTagUtil
{

    /**
     * don't instantiate a new HtmlTagUtil.
     */
    private HtmlTagUtil()
    {
        // unused
    }

    /**
     * costruct a tag from a name and a collection of attributes.
     * @param tagName String tag name
     * @param attributes HtmlAttributeMap containing all the tag attributes
     * @return String open tag with attributes
     */
    public static String createOpenTagString(String tagName, HtmlAttributeMap attributes)
    {

        StringBuffer buffer = new StringBuffer();

        buffer.append(TagConstants.TAG_OPEN).append(tagName);

        if (attributes != null)
        {
            buffer.append(attributes.toString());
        }
        buffer.append(TagConstants.TAG_CLOSE);

        return buffer.toString();

    }

    /**
     * Strips html tags from a String.
     * @param str input string containing html tags (<code>null</code> is <strong>not </strong> handled)
     * @return input message without tags
     */
    public static String stripHTMLTags(String str)
    {
        // operate on chars to avoid heavy string operations on jdk 1.3
        int len = str.length();
        char[] value = str.toCharArray();
        StringBuffer dest = new StringBuffer(len + 16);
        boolean intag = false;

        for (int j = 0; j < len; j++)
        {
            char c = value[j];
            if (intag)
            {
                if (c == '>')
                {
                    intag = false;
                }
            }
            else
            {
                switch (c)
                {
                    case '"' :
                        dest.append("&quot;"); // encode quotes, this could be used as a tag attribute value
                        break;
                    case '<' :
                        intag = true;
                        break;
                    default :
                        dest.append(c);
                        break;
                }
            }
        }

        return dest.toString();
    }

    /**
     * Abbreviates a String which can contain html tags. Html tags are not counted in String length. It also try to
     * handle open tags and html entities.
     * @param str full String. <code>null</code> is handled by returning <code>null</code>
     * @param maxLength maximum number of characters (excluding tags)
     * @param byNumberOfWords if <code>true</code> maxLength will be the number of words returned, elsewhere will
     * represent the number of characters.
     * @return abbreviated String
     */
    public static String abbreviateHtmlString(String str, int maxLength, boolean byNumberOfWords)
    {
        if (str == null || str.length() <= maxLength)
        {
            // quick exit to avoid useless creation of a Stringbuffer
            return str;
        }

        int sz = str.length();
        StringBuffer buffer = new StringBuffer(sz);

        // some spaghetti code for quick & dirty tag handling and entity detection
        boolean inTag = false; // parsing a tag
        boolean inTagName = false; // parsing a tag name
        boolean endingTag = false; // parsing an ending tag
        int count = 0; // chars/words added
        boolean chopped = false; // result has been chopped?
        int entityChars = 0; // number of chars in parsed entity

        StringBuffer currentTag = new StringBuffer(5); // will contain a tag name

        List<String> openTags = new ArrayList<String>(5); // lit of unclosed tags found in the string

        int i;
        for (i = 0; i < sz; i++)
        {
            if (count >= maxLength)
            {
                chopped = true;
                break;
            }

            char c = str.charAt(i);

            if (c == '<')
            {
                inTag = true;
                inTagName = true;
            }
            else if (inTag)
            {
                if (inTagName && c == '/')
                {

                    if (currentTag.length() == 0)
                    {
                        // end tag found
                        endingTag = true;
                    }
                    else
                    {
                        // empty tag, reset and don't save
                        inTagName = false;
                    }

                    currentTag = new StringBuffer(5);
                }
                else if (inTagName && (c == ' ' || c == '>'))
                {
                    inTagName = false;

                    if (!endingTag)
                    {
                        openTags.add(currentTag.toString());
                    }
                    else
                    {
                        openTags.remove(currentTag.toString());
                    }
                    currentTag = new StringBuffer(5);
                    if (c == '>')
                    {
                        inTag = false;
                    }
                }
                else if (c == '>')
                {
                    inTag = false;
                }
                else if (inTagName)
                {
                    currentTag.append(c);
                }

            }
            else
            {

                if (byNumberOfWords)
                {
                    if (Character.isWhitespace(c))
                    {
                        count++;
                    }
                }
                else
                {
                    // handle entities
                    if (c == '&')
                    {
                        entityChars = 1;
                    }
                    else if (entityChars == 0)
                    {
                        count++;
                    }
                    else
                    {
                        // end entity
                        if (entityChars > 0 && c == ';')
                        {
                            entityChars = 0;
                            count++;
                        }
                        else
                        {
                            entityChars++;
                        }
                        if (entityChars > 5)
                        {
                            // assume an unescaped & if entity doesn't close after max 5 chars
                            count += entityChars;
                            entityChars = 0;
                        }
                    }
                }

            }

            if (inTag || (!byNumberOfWords || count < maxLength))
            {
                buffer.append(c);
            }
        }

        if (chopped)
        {
            buffer.append("...");
        }

        if (openTags.size() > 0)
        {
            // quickly fixes closed tags
            String remainingToken = str.substring(i);

            for (int j = openTags.size() - 1; j >= 0; j--)
            {
                String closingTag = "</" + openTags.get(j) + ">";

                // we only add closing tags that exists in the original String, so we don't have to understand
                // html/xhtml differences and keep a list of html unclosed tags
                if (remainingToken.indexOf(closingTag) > -1)
                {
                    buffer.append(closingTag);
                }
            }
        }

        return buffer.toString();
    }

}