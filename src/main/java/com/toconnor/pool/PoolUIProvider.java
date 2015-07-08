package com.toconnor.pool;

import java.io.Serializable;

import com.toconnor.pool.data.Bootstrap;
import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

/**
 * PoolUIProvider
 */
public class PoolUIProvider extends UIProvider implements Serializable, SessionInitListener
{
	private static final long serialVersionUID = 1L;

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event)
    {
        if ("false".equals(event.getRequest().getParameter("mobile")))
        {
            return PoolUI.class;
        }
	    else if("true".equals(event.getRequest().getParameter("bootstrap")))
        {
	        //perform any system initialization
	        Bootstrap.init();
        }

        //TODO handle mobile devices better
//        if (event.getRequest().getHeader("user-agent").toLowerCase().contains("mobile"))
//        {
//            return MobileCheckUI.class;
//        }

        return PoolUI.class;
    }

	@Override
	public void sessionInit(SessionInitEvent event) throws ServiceException
	{
//		VaadinSession session = event.getSession();

//		session.addUIProvider(provider);
		event.getSession().addBootstrapListener(new BootstrapListener()
		{
			@Override
			public void modifyBootstrapPage(BootstrapPageResponse response)
			{
				Element script = new Element(Tag.valueOf("script"), "");
				script.attr("type", "text/javascript");
				script.attr("src", "/VAADIN/js/loginWidget.js");
				response.getDocument().head().appendChild(script);
			}

			@Override
			public void modifyBootstrapFragment(BootstrapFragmentResponse response)
			{
				//no-op
			}
		});
	}
}
