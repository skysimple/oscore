/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.web;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;


/**
 * This class is a utility class to handle encoding of a given URL (presented
 * as a string).
 *
 * @author  <a href="mailto:joeo@epesh.com">Joseph B. Ottinger</a>
 * @version $Revision$
 */
public class ContextGenerator {
    //~ Instance fields ////////////////////////////////////////////////////////

    HttpServletRequest req = null;
    HttpServletResponse resp = null;
    String url = null;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
    * Constructor for the ContextGenerator object
    *
    * @param  req   The request object from the current servlet context
    * @param  resp  The response object from the current servlet context
    */
    public ContextGenerator(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    * Sets the Url attribute of the ContextGenerator object
    *
    * @param  url  The new Url value
    */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
    * Gets the Url attribute of the ContextGenerator object
    *
    * @param  url  the url to encode
    * @return      The Url value
    */
    public String getUrl(String url) {
        setUrl(url);

        return getUrl();
    }

    /**
    * Gets the Url attribute of the ContextGenerator object. If the url has a
    * protocol specifier, the url is returned unchanged. Otherwise, the url
    * should have the session id appended properly (if required).
    *
    * @return The encoded url value
    */
    public String getUrl() {
        String retval = url;

        if ((url.indexOf("://") == -1) && !url.startsWith("mailto:")) {
            StringBuffer sb = new StringBuffer(req.getContextPath());

            if (!url.startsWith("/")) {
                sb.append("/");
            }

            sb.append(url);
            retval = sb.toString();
        }

        return retval;
    }
}
