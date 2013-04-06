/**
 * Copyright 2011 The Buzz Media, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thebuzzmedia.sjxp.rule;

import com.thebuzzmedia.sjxp.XMLParser;

/**
 * Class used to provide a default implementation of a rule in SJXP.
 * <p/>
 * It is intended that you only ever need to use this class and not implement
 * your own {@link IRule} classes yourself (you are certainly welcome to
 * though).
 * <p/>
 * Rules all consist of the same boiler plate:
 * <ul>
 * <li>A {@link ParsingMode}, so we know if the rule wants to match character
 * data or attribute values.</li>
 * <li>A <code>locationPath</code>, which tells us the path to the element in
 * the XML document to match.</li>
 * <li>OPTIONAL: 1 or more <code>attributeNames</code> from the matching element
 * that we want values from.</li>
 * </ul>
 * All of that rudimentary behavior, along with some nice error-checking and an
 * easy-to-debug and caching <code>toString</code> implementation are provided
 * by this class so you can hit the ground running by simply creating an
 * instance of this class, passing it a location path and provided an
 * implementation for the handler you are interested in.
 * <p/>
 * An example would look like this:
 *
 * <pre>
 * new DefaultRule(ParsingMode.CHARACTER, &quot;/library/book/title&quot;) {
 * 	&#064;Override
 * 	public void handleParsedCharacters(XMLParser parser, String text, T userObject) {
 * 		// Handle the title text
 * 	}
 * };
 * </pre>
 *
 * <h3>Instance Reuse</h3>
 * Instances of {@link DefaultRule} are immutable and maintain no internal
 * state, so re-using the same {@link DefaultRule} among multiple instances of
 * {@link com.thebuzzmedia.sjxp.XMLParser} is safe.
 *
 * @param <T>
 *            The class type of any user-supplied object that the caller wishes
 *            to be passed through from one of the {@link com.thebuzzmedia.sjxp.XMLParser}'s
 *            <code>parse</code> methods directly to the handler when a rule
 *            matches. This is typically a data storage mechanism like a DAO or
 *            cache used to store the parsed value in some valuable way, but it
 *            can ultimately be anything. If you do not need to make use of the
 *            user object, there is no need to parameterize the class.
 *
 * @author Riyad Kalla (software@thebuzzmedia.com)
 */
public class DefaultRule<T> implements IRule<T> {
    private String toStringCache = null;

    private ParsingMode type;
    private String locationPath;
    private String[] attributeNames;

    /**
     * Create a new rule with the given values.
     *
     * @param type
     *            The type of the rule.
     * @param locationPath
     *            The location path of the element to target in the XML.
     * @param attributeNames
     *            An optional list of attribute names to parse values for if the
     *            type of this rule is {@link ParsingMode#ATTRIBUTE}.
     *
     * @throws IllegalArgumentException
     *             if <code>type</code> is <code>null</code>, if
     *             <code>locationPath</code> is <code>null</code> or empty, if
     *             <code>type</code> is {@link ParsingMode#ATTRIBUTE} and
     *             <code>attributeNames</code> is <code>null</code> or empty or
     *             if <code>type</code> is {@link ParsingMode#CHARACTER} and
     *             <code>attributeNames</code> <strong>is not</strong>
     *             <code>null</code> or empty.
     */
    public DefaultRule(ParsingMode type, String locationPath, String... attributeNames)
            throws IllegalArgumentException {
        if (type == null)
            throw new IllegalArgumentException("type cannot be null");
        if (locationPath == null || locationPath.length() == 0)
            throw new IllegalArgumentException(
                    "locationPath cannot be null or empty");
		/*
		 * Pedantic, while we could remove a single trailing slash easily
		 * enough, there is the very-small-chance the users has multiple
		 * trailing slashes... again easy to remove, but at this point they are
		 * being really sloppy and we are letting it slide. Instead, fire an
		 * exception up-front and teach people how the API behaves immediately
		 * and what is required. Makes everyone's lives easier.
		 */
        if (locationPath.charAt(locationPath.length() - 1) == '/')
            throw new IllegalArgumentException(
                    "locationPath cannot end in a trailing slash (/), please remove it.");
        if ((type == ParsingMode.ATTRIBUTE && (attributeNames == null || attributeNames.length == 0)))
            throw new IllegalArgumentException(
                    "ParsingMode.ATTRIBUTE was specified but attributeNames was null or empty. One or more attribute names must be provided for this rule if it is going to match any attribute values.");
		/*
		 * Pedantic, but it will warn the caller of what is likely an
		 * programming error condition very early on so they don't bang their
		 * head against the wall as to why the parser isn't picking up their
		 * attributes.
		 */
        if (type == ParsingMode.CHARACTER && attributeNames != null
                && attributeNames.length > 0)
            throw new IllegalArgumentException(
                    "ParsingMode.CHARACTER was specified, but attribute names were passed in. This is likely a mistake and can be fixed by simply not passing in the ignored attribute names.");

        this.type = type;
        this.locationPath = locationPath;
        this.attributeNames = attributeNames;
    }

    /**
     * Overridden to provide a nicely formatted representation of the rule for
     * easy debugging.
     * <p/>
     * As an added bonus, since {@link IRule}s are intended to be immutable, the
     * result of <code>toString</code> is cached on the first call and the cache
     * returned every time to avoid re-computing the completed {@link String}.
     *
     * @return a nicely formatted representation of the rule for easy debugging.
     */
    @Override
    public synchronized String toString() {
        if (toStringCache == null) {
            StringBuilder builder = null;

			/*
			 * toString is only used during debugging, so make the toString
			 * output of the rule pretty so it is easier to track in debug
			 * messages.
			 */
            if (attributeNames != null && attributeNames.length > 0) {
                builder = new StringBuilder();

                for (String name : attributeNames)
                    builder.append(name).append(',');

                // Chop the last stray comma
                builder.setLength(builder.length() - 1);
            }

            toStringCache = this.getClass().getName() + "[type=" + type
                    + ", locationPath=" + locationPath + ", attributeNames="
                    + (builder == null ? "" : builder.toString()) + "]";
        }

        return toStringCache;
    }

    public ParsingMode getType() {
        return type;
    }

    public String getLocationPath() {
        return locationPath;
    }

    public String[] getAttributeNames() {
        return attributeNames;
    }

    /**
     * Default no-op implementation. Please override with your own logic.
     *
     * @see IRule#handleTag(com.thebuzzmedia.sjxp.XMLParser, boolean, Object)
     */
    public void handleTag(XMLParser<T> parser, boolean isStartTag, T userObject) {
        // no-op impl
    }

    /**
     * Default no-op implementation. Please override with your own logic.
     *
     * @see IRule#handleParsedAttribute(com.thebuzzmedia.sjxp.XMLParser, int, String, Object)
     */
    public void handleParsedAttribute(XMLParser<T> parser, int index,
                                      String value, T userObject) {
        // no-op impl
    }

    /**
     * Default no-op implementation. Please override with your own logic.
     *
     * @see IRule#handleParsedCharacters(com.thebuzzmedia.sjxp.XMLParser, String, Object)
     */
    public void handleParsedCharacters(XMLParser<T> parser, String text,
                                       T userObject) {
        // no-op impl
    }
}