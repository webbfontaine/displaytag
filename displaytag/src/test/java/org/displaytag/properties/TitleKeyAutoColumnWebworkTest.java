package org.displaytag.properties;

import org.displaytag.localization.I18nResourceProvider;
import org.displaytag.localization.I18nWebworkAdapter;
import org.displaytag.localization.LocaleResolver;
import org.junit.Test;

import com.opensymphony.webwork.dispatcher.ServletDispatcher;


/**
 * I18n test with WebWork adapter.
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public class TitleKeyAutoColumnWebworkTest extends AbstractTitleKeyAutoColumnTest
{

    /**
     * @see org.displaytag.test.DisplaytagCase#getJspName()
     */
    @Override
    public String getJspName()
    {
        return super.getJspName() + ".webwork";
    }

    /**
     * @see org.displaytag.properties.AbstractTitleKeyTest#getExpectedSuffix()
     */
    @Override
    protected String getExpectedSuffix()
    {
        return " webwork";
    }

    /**
     * @see org.displaytag.properties.AbstractTitleKeyTest#getI18nResourceProvider()
     */
    @Override
    protected I18nResourceProvider getI18nResourceProvider()
    {
        return new I18nWebworkAdapter();
    }

    /**
     * @see org.displaytag.properties.AbstractTitleKeyTest#getResolver()
     */
    @Override
    protected LocaleResolver getResolver()
    {
        return new I18nWebworkAdapter();
    }

    /**
     * @see org.displaytag.test.DisplaytagCase#doTest(java.lang.String)
     */
    @Override
    @Test
    public void doTest() throws Exception
    {
        this.runner.registerServlet("*.webwork", ServletDispatcher.class.getName());
        super.doTest();
    }

}
