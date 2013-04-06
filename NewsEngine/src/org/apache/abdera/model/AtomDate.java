/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  The ASF licenses this file to You
* under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.  For additional information regarding
* copyright in this work, please see the NOTICE file in the top level
* directory of this distribution.
*/
package org.apache.abdera.model;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p>Provides an implementation of the Atom Date Construct,
 * which is itself a specialization of the RFC3339 date-time.</p>
 *
 * <p>Per RFC4287:</p>
 *
 * <pre>
 *  3.3.  Date Constructs
 *
 *  A Date construct is an element whose content MUST conform to the
 *  "date-time" production in [RFC3339].  In addition, an uppercase "T"
 *  character MUST be used to separate date and time, and an uppercase
 *  "Z" character MUST be present in the absence of a numeric time zone
 *  offset.
 *
 *  atomDateConstruct =
 *     atomCommonAttributes,
 *     xsd:dateTime
 *
 *  Such date values happen to be compatible with the following
 *  specifications: [ISO.8601.1988], [W3C.NOTE-datetime-19980827], and
 *  [W3C.REC-xmlschema-2-20041028].
 *
 *  Example Date constructs:
 *
 *  &lt;updated>2003-12-13T18:30:02Z&lt;/updated>
 *  &lt;updated>2003-12-13T18:30:02.25Z&lt;/updated>
 *  &lt;updated>2003-12-13T18:30:02+01:00&lt;/updated>
 *  &lt;updated>2003-12-13T18:30:02.25+01:00&lt;/updated>
 *
 *  Date values SHOULD be as accurate as possible.  For example, it would
 *  be generally inappropriate for a publishing system to apply the same
 *  timestamp to several entries that were published during the course of
 *  a single day.
 *  </pre>
 *
 */
public final class AtomDate {

    private Date value = null;

    public AtomDate() {}

    /**
     * Create an AtomDate using the serialized string format (e.g. 2003-12-13T18:30:02Z).
     * @param value The serialized date/time value
     */
    public AtomDate(String value) {
        this(parse(value));
    }

    /**
     * Create an AtomDate using a java.util.Date
     * @param value The java.util.Date value
     */
    public AtomDate(Date value) {
        this.value = value;
    }

    /**
     * Create an AtomDate using a java.util.Calendar.
     * @param value The java.util.Calendar value
     */
    public AtomDate(Calendar value) {
        this(value.getTime());
    }

    /**
     * Create an AtomDate using the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * @param value The number of milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public AtomDate(long value) {
        this(new Date(value));
    }

    /**
     * Return the serialized string form of the Atom date
     * @return the serialized string form of the date as specified by RFC4287
     */
    public String getValue() {
        return format(value);
    }

    /**
     * Sets the value of the Atom date using the serialized string form
     * @param value The serialized string form of the date
     */
    public void setValue(String value) {
        this.value = parse(value);
    }

    /**
     * Sets the value of the Atom date using java.util.Date
     * @param date A java.util.Date
     */
    public void setValue(Date date) {
        this.value = date;
    }

    /**
     * Sets the value of the Atom date using java.util.Calendar
     * @param calendar a java.util.Calendar
     */
    public void setValue(Calendar calendar) {
        this.value = calendar.getTime();
    }

    /**
     * Sets the value of the Atom date using the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * @param timestamp The number of milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public void setValue(long timestamp) {
        this.value = new Date(timestamp);
    }

    /**
     * Returns the value of this Atom Date
     * @return A java.util.Date representing this Atom Date
     */
    public Date getDate() {
        return value;
    }

    /**
     * Returns the value of this Atom Date as a java.util.Calendar
     * @return A java.util.Calendar representing this Atom Date
     */
    public Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(value);
        return cal;
    }

    /**
     * Returns the value of this Atom Date as the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * @return The number of milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public long getTime() {
        return value.getTime();
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public boolean equals(Object obj) {
        boolean answer = false;
        if (obj instanceof Date) {
            Date d = (Date) obj;
            answer = (this.value.equals(d));
        } else if (obj instanceof String) {
            Date d = parse((String) obj);
            answer = (this.value.equals(d));
        } else if (obj instanceof Calendar) {
            Calendar c = (Calendar) obj;
            answer = (this.value.equals(c.getTime()));
        } else if (obj instanceof AtomDate) {
            Date d = ((AtomDate)obj).value;
            answer = (this.value.equals(d));
        }
        return answer;
    }

    /**
     * The masks used to validate and parse the input to this Atom date.
     * These are a lot more forgiving than what the Atom spec allows.
     * The forms that are invalid according to the spec are indicated.
     */
    private static final String[] masks = {
            "yyyy-MM-dd'T'HH:mm:ss.SSSz",
            "yyyy-MM-dd't'HH:mm:ss.SSSz",                         // invalid
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd't'HH:mm:ss.SSS'z'",                       // invalid
            "yyyy-MM-dd'T'HH:mm:ssz",
            "yyyy-MM-dd't'HH:mm:ssz",                             // invalid
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd't'HH:mm:ss'z'",                           // invalid
            "yyyy-MM-dd'T'HH:mmz",                                // invalid
            "yyyy-MM-dd't'HH:mmz",                                // invalid
            "yyyy-MM-dd'T'HH:mm'Z'",                              // invalid
            "yyyy-MM-dd't'HH:mm'z'",                              // invalid
            "yyyy-MM-dd",
            "yyyy-MM",
            "yyyy"
    };

    /**
     * Parse the serialized string form into a java.util.Date
     * @param date The serialized string form of the date
     * @return The created java.util.Date
     */
    public static Date parse(String date) {
        Date d = null;
        SimpleDateFormat sdf = new SimpleDateFormat();
        for (int n = 0; n < masks.length; n++) {
            try {
                sdf.applyPattern(masks[n]);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                sdf.setLenient(true);
                d = sdf.parse(date, new ParsePosition(0));
                if (d != null) break;
            } catch (Exception e) {}
        }
        if (d == null)
            throw new IllegalArgumentException();
        return d;
    }

    /**
     * Create the serialized string form from a java.util.Date
     * @param d A java.util.Date
     * @return The serialized string form of the date
     */
    public static String format (Date d) {
        StringBuffer iso8601 = new StringBuffer();
        SimpleDateFormat sdf = new SimpleDateFormat(masks[2]);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        sdf.format(d, iso8601, new FieldPosition(0));
        return iso8601.toString();
    }

    /**
     * Create a new Atom Date instance from the serialized string form
     * @param value The serialized string form of the date
     * @return The created AtomDate
     */
    public static AtomDate valueOf(String value) {
        return new AtomDate(value);
    }

    /**
     * Create a new Atom Date instance from a java.util.Date
     * @param value a java.util.Date
     * @return The created AtomDate
     */
    public static AtomDate valueOf(Date value) {
        return new AtomDate(value);
    }

    /**
     * Create a new Atom Date instance from a java.util.Calendar
     * @param value A java.util.Calendar
     * @return The created AtomDate
     */
    public static AtomDate valueOf(Calendar value) {
        return new AtomDate(value);
    }

    /**
     * Create a new Atom Date instance using the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * @param value The number of milliseconds since January 1, 1970, 00:00:00 GMT
     * @return The created AtomDate
     */
    public static AtomDate valueOf(long value) {
        return new AtomDate(value);
    }
}