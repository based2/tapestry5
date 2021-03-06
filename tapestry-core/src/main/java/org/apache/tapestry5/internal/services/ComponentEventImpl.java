// Copyright 2006, 2007, 2008, 2010, 2011 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.internal.services;

import org.apache.tapestry5.ComponentEventCallback;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.internal.structure.ComponentPageElementResources;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.runtime.ComponentEvent;
import org.slf4j.Logger;

public class ComponentEventImpl extends EventImpl implements ComponentEvent
{
    private final String eventType;

    private final String originatingComponentId;

    private final EventContext context;

    private final ComponentPageElementResources elementResources;

	private final boolean exactParameterCountMatch;

    /**
     * @param eventType
     *            non blank string used to identify the type of event that was triggered
     * @param originatingComponentId
     *            the id of the component that triggered the event
     * @param context
     *            provides access to parameter values
     * @param handler
     *            invoked when a non-null return value is obtained from an event handler method
     * @param elementResources
     *            provides access to common resources and services
	 * @param exactParameterCountMatch
	 *            the {@link org.apache.tapestry5.ioc.annotations.Symbol} indicating an exact match
	 *            on parameter count in the {@link EventContext}.
	 * @param model
	 *            the {@link ComponentModel} of the component originating the event.
     * @param logger
     *            used to log method invocations
     */
    public ComponentEventImpl(String eventType, String originatingComponentId, EventContext context,
            ComponentEventCallback handler, ComponentPageElementResources elementResources,
			boolean exactParameterCountMatch, ComponentModel model, Logger logger)
    {
        super(handler, logger, elementResources);

        this.eventType = eventType;
        this.originatingComponentId = originatingComponentId;
        this.elementResources = elementResources;
        this.context = context;
		this.exactParameterCountMatch = exactParameterCountMatch && !model.handleActivationEventContext();
    }

    @Override
    public String toString()
    {
        return String.format("ComponentEvent[%s from %s]", eventType, originatingComponentId.length() == 0 ? "(self)"
                : originatingComponentId);
    }

    public boolean matches(String eventType, String componentId, int parameterCount)
    {
        if (isAborted())
            return false;

		boolean parameterCountMatch = exactParameterCountMatch ?
				context.getCount() == parameterCount : context.getCount() >= parameterCount;

		return this.eventType.equalsIgnoreCase(eventType) && parameterCountMatch
                 && (originatingComponentId.equalsIgnoreCase(componentId) || componentId.equals(""));
    }

    @SuppressWarnings("unchecked")
    public Object coerceContext(int index, String desiredTypeName)
    {
        if (index >= context.getCount())
            throw new IllegalArgumentException(String.format("Method %s has more parameters than there are context values for this component event.", getMethodDescription()));
        try
        {
            Class desiredType = elementResources.toClass(desiredTypeName);

            return context.get(desiredType, index);
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException(String.format("Exception in method %s, parameter #%d: %s", getMethodDescription(), index + 1, ex), ex);
        }
    }

    public Object[] getContext()
    {
        int count = context.getCount();

        Object[] result = new Object[count];

        for (int i = 0; i < count; i++)
            result[i] = context.get(Object.class, i);

        return result;
    }

    public EventContext getEventContext()
    {
        return context;
    }
}
