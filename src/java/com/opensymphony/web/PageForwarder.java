/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.web;

import java.io.IOException;

/* ====================================================================
 * The OpenSymphony Software License, Version 1.1
 *
 * (this license is derived and fully compatible with the Apache Software
 * License - see http://www.apache.org/LICENSE.txt)
 *
 * Copyright (c) 2001 The OpenSymphony Group. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        OpenSymphony Group (http://www.opensymphony.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "OpenSymphony" and "The OpenSymphony Group"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact license@opensymphony.com .
 *
 * 5. Products derived from this software may not be called "OpenSymphony"
 *    or "OSCore", nor may "OpenSymphony" or "OSCore" appear in their
 *    name, without prior written permission of the OpenSymphony Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.PageContext;


/**
 * Simple abstract class for easy forwarding servlets to other servlets.
 * This class does nothing clever, it can just be used to keep your code easier to manage.
 *
 * <p>Extend it to make an implementation and pass accross in the constructor the appropriate
 * objects. It is then easy to create methods that can be called from the
 * {@link javax.servlet.http.HttpServlet}.</p>
 *
 * <h3>Example</h3>
 *
 * <pre>
 * // Class implementation
 * class MyItemViewer extends PageForwarder {
 *
 *   void listItems(MyItem[] items) {
 *     set("items",items);
 *     forward("listpage.jsp");
 *   }
 *
 *   void displayItem(MyItem item) {
 *     set("item",item);
 *     forward("viewpage.jsp");
 *   }
 *
 * }
 *
 * // Servlet
 * public class MyServlet extends HttpServlet {
 *
 *   public void doGet(HttpServletRequest request,
 *                     HttpServletResponse response)
 *                     throws ServletException {
 *     MyForwarder f = new MyForwarder();
 *     f.init(this, request, response, "/path/to/jsppages/");
 *     MyItem item = // something.
 *     if (item!=null) {
 *       f.displayItem(item);
 *     }
 *     else {
 *       MyItem[] allItems = //something
 *       f.listItems(allItems);
 *     }
 *   }
 * }
 *
 * </pre>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision$
 */
public abstract class PageForwarder {
    //~ Instance fields ////////////////////////////////////////////////////////

    /**
         * Base URL (in context to web-app) to append to page URLs. (Include trailing slash).
         */
    public String baseUrl = "/";

    /**
         * Current page request.
         */
    protected HttpServletRequest request;

    /**
         * Resulting response with which to forward page.
         */
    protected HttpServletResponse response;

    /**
    * Servlet context of calling class.
    */
    protected ServletContext context;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Default constructor. After calling this, the init() method must be called by Servlet.
     */
    public PageForwarder() {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Initialize forwarder by setting required values.
     *
     * @param servlet Calling servlet class.
     * @param request Current page request.
     * @param response Resulting response with which to forward page.
     * @param baseUrl Base URL (in context to web-app) to append to page URLs. (Include trailing slash).
     */
    public void init(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response, String baseUrl) {
        this.context = servlet.getServletConfig().getServletContext();
        this.request = request;
        this.response = response;
        this.baseUrl = baseUrl;
    }

    /**
     * Initialize forwarder by setting required values.
     *
     * @param servlet Calling servlet class.
     * @param request Current page request.
     * @param response Resulting response with which to forward page.
     */
    public void init(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        init(servlet, request, response, "/");
    }

    /**
     * Initialize forwarder from JSP
     *
     * @param pageContext JSP pageContext
     * @param baseUrl Base URL (in context to web-app) to append to page URLs. (Include trailing slash).
     */
    public void init(PageContext pageContext, String baseUrl) {
        this.context = pageContext.getServletContext();
        this.request = (HttpServletRequest) pageContext.getRequest();
        this.response = (HttpServletResponse) pageContext.getResponse();
        this.baseUrl = baseUrl;
    }

    /**
     * Initialize forwarder from JSP
     *
     * @param pageContext JSP pageContext
     */
    public void init(PageContext pageContext) {
        init(pageContext, "/");
    }

    public void init(ActionContext actionContext, String baseUrl) {
        this.context = actionContext.getServletContext();
        this.request = actionContext.getRequest();
        this.response = actionContext.getResponse();
        this.baseUrl = baseUrl;
    }

    public void init(ActionContext actionContext) {
        init(actionContext, "/");
    }

    /**
     * Send HTTP error message.
     *
     * @param errorCode HTTP Error Code.
     * @param message Error message (may be null).
     *
     * @see javax.servlet.http.HttpServletResponse
     */
    protected void error(int errorCode, String message) throws ServletException {
        try {
            if ((message == null) || (message.length() == 0)) {
                response.sendError(errorCode);
            } else {
                response.sendError(errorCode, message);
            }
        } catch (IOException e) {
            throw new ServletException("Cannot send error msg.");
        }
    }

    /**
     * Forward request to specified page (under <code>baseUrl</code> directory).
     *
     * @param page Page to forward request to. Prepended to <code>baseUrl</code>.
     * @exception javax.servlet.ServletException Thrown if anything goes wrong
     *            in trying to forward the page (such as page not found).
     *
     * @see javax.servlet.RequestDispatcher#forward(ServletRequest, ServletResponse)
     */
    protected void forward(String page) throws ServletException {
        String url = baseUrl + page;

        if (url.startsWith("//")) {
            url = url.substring(1);
        }

        try {
            context.getRequestDispatcher(url).forward(request, response);
        } catch (IOException e) {
            throw new ServletException("Cannot forward to page '" + url + "' : " + e.toString());
        }
    }

    /**
     * Include specified page (under <code>baseUrl</code> directory).
     *
     * @param page Page to include. Prepended to <code>baseUrl</code>.
     * @exception javax.servlet.ServletException Thrown if anything goes wrong
     *            in trying to include the page (such as page not found).
     *
     * @see javax.servlet.RequestDispatcher#forward(ServletRequest, ServletResponse)
     */
    protected void include(String page) throws ServletException {
        String url = baseUrl + page;

        if (url.startsWith("//")) {
            url = url.substring(1);
        }

        try {
            context.getRequestDispatcher(url).include(request, response);
        } catch (IOException e) {
            throw new ServletException("Cannot include page '" + url + "' : " + e.toString());
        }
    }

    /**
             * Same as {@see #forward(java.lang.String)} except HTTP redirect is sent instead.
             * Note that attributes set using set() method will not be available to page.
             */
    protected void redirect(String page) throws ServletException {
        String url = baseUrl + page;

        if (url.startsWith("//")) {
            url = url.substring(1);
        }

        try {
            context.getRequestDispatcher(url).forward(request, response);
        } catch (IOException e) {
            throw new ServletException("Cannot redirect to page '" + url + "' : " + e.toString());
        }
    }

    /**
         * Set an attribute in the <code>HttpRequest</code> object.
         *
         * @param key Key to refer to stored attribute.
         * @param value Value of attribute.
         *
         * @see javax.servlet.ServletRequest#setAttribute(String, Object)
         */
    protected void set(String key, Object value) {
        request.setAttribute(key, value);
    }
}
