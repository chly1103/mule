/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.cxf.support;

import static org.mule.runtime.core.message.DefaultEventBuilder.EventImplementation.getVariableValueOrNull;
import static org.mule.runtime.module.cxf.CxfConstants.ATTACHMENTS;
import org.mule.runtime.core.NonBlockingVoidMuleEvent;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.module.cxf.CxfConstants;

import java.util.Collection;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * Copies any attachments which were stored by the proxy to the outbound CXF message.
 */
public class CopyAttachmentOutInterceptor extends AbstractPhaseInterceptor {

  public CopyAttachmentOutInterceptor() {
    super(Phase.SETUP);
  }

  public void handleMessage(Message message) throws Fault {
    Event event = (Event) message.getExchange().get(CxfConstants.MULE_EVENT);

    if (event == null || event instanceof NonBlockingVoidMuleEvent) {
      return;
    }

    Collection<Attachment> a = getVariableValueOrNull(ATTACHMENTS, event);
    if (a != null) {
      message.setAttachments(a);
    }
  }
}


