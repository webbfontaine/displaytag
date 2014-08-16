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
package org.displaytag.filter;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.displaytag.tags.TableTagParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * J2ee 1.3 implementation of BufferedResponseWrapper. Need to extend HttpServletResponseWrapper for Weblogic
 * compatibility.
 * @author rapruitt
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public class BufferedResponseWrapper13Impl extends HttpServletResponseWrapper implements BufferedResponseWrapper
{

    /**
     * logger.
     */
    private static Logger log = LoggerFactory.getLogger(BufferedResponseWrapper13Impl.class);

    /**
     * The buffered response.
     */
    private CharArrayWriter outputWriter;

    /**
     * The outputWriter stream.
     */
    private SimpleServletOutputStream servletOutputStream;

    /**
     * The contentType.
     */
    private String contentType;

    /**
     * If state is set, allow getOutputStream() to return the "real" output stream, elsewhere returns a internal buffer.
     */
    private boolean state;

    /**
     * Writer has been requested.
     */
    private boolean outRequested;

    /**
     * @param httpServletResponse the response to wrap
     */
    public BufferedResponseWrapper13Impl(HttpServletResponse httpServletResponse)
    {
        super(httpServletResponse);
        this.outputWriter = new CharArrayWriter();
        this.servletOutputStream = new SimpleServletOutputStream();
    }

    /**
     * @see org.displaytag.filter.BufferedResponseWrapper#getContentType()
     */
    @Override
    public String getContentType()
    {
        return this.contentType;
    }

    /**
     * The content type is NOT set on the wrapped response. You must set it manually. Overrides any previously set
     * value.
     * @param theContentType the content type.
     */
    @Override
    public void setContentType(String theContentType)
    {
        if (state)
        {
            log.debug("Allowing content type");

            if (this.contentType != null && // content type has been set before
                this.contentType.indexOf("charset") > -1) // and it specified charset
            {
                // so copy the charset
                String charset = this.contentType.substring(this.contentType.indexOf("charset"));
                if (log.isDebugEnabled())
                {
                    log.debug("Adding charset: [" + charset + "]");
                }

                getResponse().setContentType(StringUtils.substringBefore(theContentType, "charset") + '=' + charset);
            }
            else
            {
                getResponse().setContentType(theContentType);
            }

        }
        this.contentType = theContentType;
    }

    /**
     * @see javax.servlet.ServletResponse#getWriter()
     */
    @Override
    public PrintWriter getWriter() throws IOException
    {

        if (state && !outRequested)
        {
            log.debug("getWriter() returned");

            // ok, exporting in progress, discard old data and go on streaming
            this.servletOutputStream.reset();
            this.outputWriter.reset();
            this.outRequested = true;
            return ((HttpServletResponse) getResponse()).getWriter();
        }

        return new PrintWriter(this.outputWriter);
    }

    /**
     * Flush the buffer, not the response.
     * @throws IOException if encountered when flushing
     */
    @Override
    public void flushBuffer() throws IOException
    {
        if (outputWriter != null)
        {
            this.outputWriter.flush();
            this.servletOutputStream.outputStream.reset();
        }
    }

    /**
     * @see javax.servlet.ServletResponse#getOutputStream()
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException
    {
        if (state && !outRequested)
        {
            log.debug("getOutputStream() returned");

            // ok, exporting in progress, discard old data and go on streaming
            this.servletOutputStream.reset();
            this.outputWriter.reset();
            this.outRequested = true;
            return ((HttpServletResponse) getResponse()).getOutputStream();
        }
        return this.servletOutputStream;
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void addHeader(String name, String value)
    {
        // if the "magic parameter" is set, a table tag is going to call getOutputStream()
        if (TableTagParameters.PARAMETER_EXPORTING.equals(name))
        {
            log.debug("Magic header received, real response is now accessible");
            state = true;
        }
        else
        {
            if (!ArrayUtils.contains(FILTERED_HEADERS, StringUtils.lowerCase(name)))
            {
                ((HttpServletResponse) getResponse()).addHeader(name, value);
            }
        }
    }

    /**
     * @see org.displaytag.filter.BufferedResponseWrapper#isOutRequested()
     */
    @Override
    public boolean isOutRequested()
    {
        return this.outRequested;
    }

    /**
     * @see org.displaytag.filter.BufferedResponseWrapper#getContentAsString()
     */
    @Override
    public String getContentAsString()
    {
        return this.outputWriter.toString() + this.servletOutputStream.toString();
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
     */
    @Override
    public void setDateHeader(String name, long date)
    {
        if (!ArrayUtils.contains(FILTERED_HEADERS, StringUtils.lowerCase(name)))
        {
            ((HttpServletResponse) getResponse()).setDateHeader(name, date);
        }
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
     */
    @Override
    public void addDateHeader(String name, long date)
    {
        if (!ArrayUtils.contains(FILTERED_HEADERS, StringUtils.lowerCase(name)))
        {
            ((HttpServletResponse) getResponse()).addDateHeader(name, date);
        }
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void setHeader(String name, String value)
    {
        if (!ArrayUtils.contains(FILTERED_HEADERS, StringUtils.lowerCase(name)))
        {
            ((HttpServletResponse) getResponse()).setHeader(name, value);
        }
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
     */
    @Override
    public void setIntHeader(String name, int value)
    {
        if (!ArrayUtils.contains(FILTERED_HEADERS, StringUtils.lowerCase(name)))
        {
            ((HttpServletResponse) getResponse()).setIntHeader(name, value);
        }
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
     */
    @Override
    public void addIntHeader(String name, int value)
    {
        if (!ArrayUtils.contains(FILTERED_HEADERS, StringUtils.lowerCase(name)))
        {
            ((HttpServletResponse) getResponse()).addIntHeader(name, value);
        }
    }

}