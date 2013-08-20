package org.apache.tapestry5.alerts;

// Copyright 2011 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.json.JSONObject;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An Alert that may be presented to the user. The Alert has a message, but also includes
 * a severity (that controls how it is presented to the user), and a duration (that controls how long
 * it is presented to the user).
 *
 * @since 5.3
 *
 * Support for inside HTML in alerts
 */
public class Alert implements Serializable
{
    private static final AtomicLong idSource = new AtomicLong(System.currentTimeMillis());

    /**
     * A unique id (unique within this JVM and execution), used to identify an alert (used primarily
     * when individually dismissing an alert).
     */
    public final long id = idSource.getAndIncrement();

    public final Duration duration;

    public final Severity severity;

    public String message;

    /**
     * Alert with default duration of {@link Duration#SINGLE} and default severity
     * of {@link Severity#INFO}.
     */
    public Alert(String message)
    {
        this(Severity.INFO, message);
    }

    /**
     * Alert with default duration of {@link Duration#SINGLE}.
     */
    public Alert(Severity severity, String message)
    {
        this(Duration.SINGLE, severity, message);
    }

    public Alert(Duration duration, Severity severity, String message)
    {
        assert duration != null;
        assert severity != null;
        assert InternalUtils.isNonBlank(message);

        this.duration = duration;
        this.severity = severity;
        this.message = message;
    }

    public String toString()
    {
        return String.format("Alert[%s %s %s]",
                duration.name(),
                severity.name(),
                message);
    }

    public JSONObject toJSON(boolean markup)
    {
        boolean isMarkup = false;
        if (markup) {
            message = message.replaceAll("&lt;","<").replaceAll("&gt;",">");
        }

        JSONObject result = new JSONObject("message", message,
                "severity", severity.name().toLowerCase() );

        if (duration == Duration.TRANSIENT)
        {
            result.put("transient", true);
        }

        if (duration.persistent)
        {
            result.put("id", id);
        }

        if (markup) {
            result.put("markup", true);
        }

        return result;
    }
}