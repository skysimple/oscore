/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.web;


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
 *  notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *  if any, must include the following acknowledgment:
 *   "This product includes software developed by the
 *    OpenSymphony Group (http://www.opensymphony.com/)."
 *  Alternately, this acknowledgment may appear in the software itself,
 *  if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "OpenSymphony" and "The OpenSymphony Group"
 *  must not be used to endorse or promote products derived from this
 *  software without prior written permission. For written
 *  permission, please contact license@opensymphony.com .
 *
 * 5. Products derived from this software may not be called "OpenSymphony"
 *  or "OSCore", nor may "OpenSymphony" or "OSCore" appear in their
 *  name, without prior written permission of the OpenSymphony Group.
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.*;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ActionServlet extends HttpServlet {
    //~ Static fields/initializers /////////////////////////////////////////////

    private final static Log logger = LogFactory.getLog(ActionServlet.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    private Map actionMappings;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void init() throws ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("init()");
        }

        actionMappings = new HashMap();
        createActionMethodMappings(createActionClassMappings());
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            String mapping = trimExtention(request.getServletPath());
            ActionInvoker invoker = (ActionInvoker) actionMappings.get(mapping);

            if (invoker == null) {
                response.sendError(404);
            } else {
                invoker.perform(new ActionContext(this, request, response));
            }
        } catch (InvocationTargetException e) {
            request.setAttribute("javax.servlet.jsp.jspException", e.getTargetException());
            throw new ServletException(e.getTargetException());
        } catch (Exception e) {
            request.setAttribute("javax.servlet.jsp.jspException", e);
            throw new ServletException(e);
        }
    }

    /**
     * Read through init-params of Servlet. All params starting with / have the
     * corresponding class mapped (e.g. /blah = com.blah.MyController).
     */
    private Map createActionClassMappings() {
        Map result = new HashMap();
        ServletConfig config = getServletConfig();

        // Get list of all init-params for servlet.
        Enumeration initParams = config.getInitParameterNames();

        while (initParams.hasMoreElements()) {
            // Get key and value for each parameter.
            String paramKey = (String) initParams.nextElement();
            String paramValue = config.getInitParameter(paramKey);

            // If key does not start with /, ignore it.
            if ((paramKey.length() == 0) || (paramKey.charAt(0) != '/')) {
                continue;
            }

            try {
                // Load (but don't instantiate) Class and place in controllerClasses map.
                Class controllerClass;

                try {
                    controllerClass = Class.forName(paramValue);
                } catch (ClassNotFoundException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("loading class from thread's classloader");
                    }

                    controllerClass = Class.forName(paramValue, false, Thread.currentThread().getContextClassLoader());
                }

                // @todo: check class extends Controller and has public default constructor
                result.put(paramKey, controllerClass);

                if (logger.isDebugEnabled()) {
                    logger.debug("Loaded action mapping : " + paramKey + " -> " + paramValue);
                }
            } catch (Exception e) {
                // Could not load the class.
                logger.error("Failed to load action mapping : " + paramKey + " -> " + paramValue, e);
            }
        }

        return result;
    }

    private void createActionMethodMappings(Map classMappings) {
        Iterator classes = classMappings.keySet().iterator();

        while (classes.hasNext()) {
            String baseMapping = (String) classes.next();
            Class controllerClass = (Class) classMappings.get(baseMapping);
            Method[] methods = controllerClass.getDeclaredMethods();

            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];

                if (!method.getName().startsWith("do")) {
                    continue;
                }

                if (method.getParameterTypes().length > 0) {
                    continue;
                }

                ActionInvoker invoker = new ActionInvoker();
                String mapping = createMappingURL(baseMapping, method);
                invoker.mapping = mapping;
                invoker.cls = controllerClass;
                invoker.method = method;
                actionMappings.put(mapping, invoker);

                if (logger.isDebugEnabled()) {
                    logger.debug("Mapped " + mapping + " -> " + controllerClass.getName() + "." + method.getName() + "()");
                }
            }
        }
    }

    private String createMappingURL(String base, Method method) {
        StringBuffer result = new StringBuffer();
        result.append(base);

        if (base.charAt(base.length() - 1) != '/') {
            result.append('/');
        }

        String methodName = method.getName().substring(2);

        for (int i = 0; i < methodName.length(); i++) {
            char c = methodName.charAt(i);

            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('-');
                }

                c = Character.toLowerCase(c);
            }

            result.append(c);
        }

        return result.toString();
    }

    /**
     * Trim the file extention from URL.
     */
    private String trimExtention(String str) {
        int lastDot = str.lastIndexOf(".");
        int lastSlash = str.lastIndexOf("/");

        if ((lastDot > -1) && (lastDot > lastSlash)) {
            return str.substring(0, lastDot);
        } else {
            return str;
        }
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    private class ActionInvoker {
        Class cls;
        Method method;
        String mapping;

        void perform(ActionContext ctx) throws Exception {
            Controller controller = (Controller) cls.newInstance();
            controller.setActionContext(ctx);
            controller.init();
            method.invoke(controller, new Object[] {});
        }
    }
}
