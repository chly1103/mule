/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.xml.functional;

import org.mule.tck.FunctionalTestCase;

import java.io.InputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public abstract class AbstractXmlFunctionalTestCase extends FunctionalTestCase
{

    public static final long TIMEOUT = 1000L;

    protected String getConfigAsString() throws IOException
    {
        return getResourceAsString(getConfigResources());
    }

    protected String getResourceAsString(String resource) throws IOException
    {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        assertNotNull(resource, is);
        return IOUtils.toString(is);
    }

}
