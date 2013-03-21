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

import com.thebuzzmedia.sjxp.XMLParser; /**
 * Interface used to describe a "rule" in SJXP.
 * <p/>
 * The most important part of a rule is its <code>locationPath</code>, this
 * literal {@link String} value is how the {@link XMLParser} matches up its
 * current position inside of an XML doc with any {@link IRule}s that want
 * information from that location.
 * <p/>
 * The <code>type</code> of the {@link IRule} indicates to the executing
 * {@link XMLParser} when the rule should be queried for a match against its
 * current position.
 * <p/>
 * All implementors must provide an implementation for the
 * <code>handleParsedXXX</code> method matching the <code>type</code> of rule
 * they have created. More specifically, if you are creating a
 * {@link Type#ATTRIBUTE} rule, you need to implement the
 * {@link #handleParsedAttribute(XMLParser, int, String, Object)} method; if you
 * are implementing a {@link Type#CHARACTER} rule, you need to implement the
 * {@link #handleParsedCharacters(XMLParser, String, Object)} method.
 * <h3>Rule Matching</h3>
 * Rules will execute every single time they match an element in an XML
 * document. There is no XPath-like expression system to tell them to only get
 * you the first, or 10th or every-other value from a document; you must
 * implement that logic yourself inside of the <code>handleParsedXXX</code>
 * handlers.
 * <h3>Instance Reuse</h3>
 * Instances of {@link IRule} are meant to be immutable and maintain no internal
 * state which makes them safe for reuse among multiple instances of
 * {@link XMLParser}.
 * <h3>Rule Format</h3>
 * The format of a location path is like a simple XPath rule with no
 * expressions, for example:
 *
 * <pre>
 * /library/book/title
 * </pre>
 *
 * would point the "title" element inside of the "book" element which is inside
 * the "library" element. If you are after a specific attribute of that element,
 * simply provide its name as an attribute argument.
 * <h3>Rule Format - Namespaces</h3>
 * Referring to a namespace-qualified element in an XML doc is easy; whether it
 * is part of the location path or an attribute name, all you have to do is
 * prefix the local name of the element with brackets ([]) and the full
 * namespace URI within the brackets, like:
 *
 * <pre>
 * /library/[http://w3.org/texts]book/title
 * </pre>
 *
 * In the example above, the "book" element is from a namespace defined by
 * "http://w3.org/texts". Inside the actual XML markup, it is likely written
 * with a friendly URI prefix that is defined at the top of the file, and would
 * look more like this: <em>
 * &lt;txt:books&gt;
 * </em> but using the URI prefixes is not exact, as they can change from
 * document to document, so SJXP requires that you reference the namespace using
 * the URI itself, and not a prefix.
 * <p/>
 * In the case where the attribute itself is namespace-qualified, like
 * <em>&lt;item rdf:about="blah" /&gt;</em>, you use the same notation for the
 * attribute name, in this case (assuming the official RDF namespace) the
 * attribute name argument you would actually return would look like this:
 *
 * <pre>
 *  [http://www.w3.org/1999/02/22-rdf-syntax-ns#]about
 * </pre>
 *
 * It can look a little confusing, but it is exact and won't lead to
 * impossible-to-debug scenarios.
 * <h3>Rule Format - Default Namespaces</h3>
 * Some XML files will define a default namespace using the <code>xmlns</code>
 * argument, by itself, in the header. If your document does this, any tag in
 * the document that isn't defined with a namespace prefix, will have to be
 * referenced with the default namespace because that is how the XML file is
 * technically defined.
 * <p/>
 * An example of this is Slashdot's RDF feed
 * (http://rss.slashdot.org/Slashdot/slashdot); a default namespace of
 * "http://purl.org/rss/1.0/" is defined, so all un-prefixed tags in the
 * document (like &lt;title&gt;, &lt;link&gt; or &lt;description&gt;) all need
 * to be qualified with that default URI, looking like this:
 *
 * <pre>
 *  [http://purl.org/rss/1.0/]title
 * </pre>
 *
 * when you define the location path for those parse elements.
 * <p/>
 * It is important to be aware of this aspect of XML files otherwise you will
 * run into scenarios where you can't understand why the parse value isn't being
 * passed to you.
 * <h3>Location Path &amp; Attribute Name Strictness</h3>
 * The implementation of SJXP is all based around strict name and namespace URI
 * matching. If you do not specify a namespace URI for your element or attribute
 * names, then only non-namespace-qualified elements will be looked for and
 * matched; and visa-versa.
 * <p/>
 * If the XML content you are parsing is sloppy and you aren't sure if the
 * values will be qualified correctly in every case, you will need to define 2
 * {@link IRule}s; 1 for non-namespace-qualified values and 1 for
 * namespace-qualified values.
 * <p/>
 * The SJXP library was purposefully designed to be pedantic to avoid "fuzzy"
 * behavior that becomes maddening to debug in edge-case scenarios where you
 * can't figure out why it is working one minute and breaking the next.
 * <p/>
 * Given the need of XML parsing in everything from video games to banking
 * applications, SJXP had to take a very conservative approach and be as
 * pedantic as possible so as not to hide any behavior from the caller.
 *
 * @param <T>
 *            The class type of any user-supplied object that the caller wishes
 *            to be passed through from one of the {@link XMLParser}'s
 *            <code>parse</code> methods directly to the handler when an
 *            {@link IRule} matches. This is typically a data storage mechanism
 *            like a DAO or cache used to store the parsed value in some
 *            valuable way, but it can ultimately be anything. If you do not
 *            need to make use of the user object, there is no need to
 *            parameterize the class.
 *
 * @author Riyad Kalla (software@thebuzzmedia.com)
 */


/**
 * Used to describe the type of the parse rule.
 */

public interface IRule<T> {

    /**
     * Used to get the type of the rule.
     * <p/>
     * The {@link com.thebuzzmedia.sjxp.XMLParser} uses this value to decide when to call this rule to
     * see if it matches the current position inside the doc and how to parse
     * out the values the rule wants.
     *
     * @return the type of the rule.
     */
    public ParsingMode getType();

    /**
     * Used to get the location path of the element inside the XML document that
     * this rule is interested in.
     * <p/>
     * This value is compared literally against the internal path state of the
     * {@link com.thebuzzmedia.sjxp.XMLParser} to see if they match before processing the rule. If you
     * have a rule that isn't executing, chances are your location path is
     * incorrect or mistyped or it is possible that your location path is
     * correct but you have implemented the wrong <code>handleXXX</code> method
     * so the default no-op one in {@link DefaultRule} is getting called.
     * <h3>Namespaces</h3>
     * Please refer to the class notes on the correct format used to define a
     * path element that is namespace-qualified by using brackets.
     * <p/>
     * Namespace qualifiers can be specified for both element paths and
     * attribute names.
     *
     * @return the location path of the element inside the XML document that
     *         this rule is interested in.
     */
    public String getLocationPath();

    /**
     * Used to get a list of attribute names that are to be parsed from the
     * element located at {@link #getLocationPath()}.
     * <p/>
     * If the rule type is {@link ParsingMode#CHARACTER}, the attribute name list
     * should be ignored.
     * <h3>Namespaces</h3>
     * Please refer to the class notes on the correct format used to define a
     * path element that is namespace-qualified by using brackets.
     * <p/>
     * Namespace qualifiers can be specified for both element paths and
     * attribute names.
     *
     * @return a list of attribute names that are to be parsed from the element
     *         located at {@link #getLocationPath()}.
     */
    public String[] getAttributeNames();

    /**
     * Handler method called by the {@link com.thebuzzmedia.sjxp.XMLParser} when an {@link IRule} of
     * type {@link ParsingMode#TAG} matches the parser's current location in the
     * document.
     * <p/>
     * This is a notification-style method, no data is parsed from the
     * underlying document, the handler is merely called to give custom handling
     * code a chance to respond to the matching open or close tag.
     *
     * @param parser
     *            The source {@link com.thebuzzmedia.sjxp.XMLParser} currently executing this rule.
     *            Providing access to the originating parser is handy if the
     *            rule wants to stop parsing by calling {@link com.thebuzzmedia.sjxp.XMLParser#stop()}
     *            .
     * @param isStartTag
     *            Used to indicate if this notification is being made because
     *            the START_TAG (<code>true</code>) was encountered or the
     *            END_TAG (<code>false</code>) was encountered.
     * @param userObject
     *            The user-supplied object passed through from the
     *            {@link com.thebuzzmedia.sjxp.XMLParser}'s <code>parse</code> method directly to this
     *            handler. This is typically a data storage mechanism like a DAO
     *            or cache used to hold parsed data or <code>null</code> if you
     *            do not need to make use of this pass-through mechanism and
     *            passed nothing to the {@link com.thebuzzmedia.sjxp.XMLParser} when you initiated the
     *            parse.
     */
    public void handleTag(XMLParser<T> parser, boolean isStartTag, T userObject);

    /**
     * Handler method called by the {@link XMLParser} when an {@link IRule} of
     * type {@link ParsingMode#ATTRIBUTE} matches the parser's current location in the
     * document.
     *
     * @param parser
     *            The source {@link XMLParser} currently executing this rule.
     *            Providing access to the originating parser is handy if the
     *            rule wants to stop parsing by calling {@link XMLParser#stop()}
     *            .
     * @param index
     *            The index of the attribute name (from
     *            {@link #getAttributeNames()}) that this value belongs to.
     * @param value
     *            The value for the given attribute.
     * @param userObject
     *            The user-supplied object passed through from the
     *            {@link XMLParser}'s <code>parse</code> method directly to this
     *            handler. This is typically a data storage mechanism like a DAO
     *            or cache used to hold parsed data or <code>null</code> if you
     *            do not need to make use of this pass-through mechanism and
     *            passed nothing to the {@link XMLParser} when you initiated the
     *            parse.
     *
     * @see #getLocationPath()
     * @see #getAttributeNames()
     */
    public void handleParsedAttribute(XMLParser<T> parser, int index,
                                      String value, T userObject);

    /**
     * Handler method called by the {@link XMLParser} when an {@link IRule} of
     * type {@link ParsingMode#CHARACTER} matches the parser's current location in the
     * document.
     * <p/>
     * This method is not called by the {@link XMLParser} until all the
     * character data has been coalesced together into a single {@link String}.
     * You don't need to worry about re-combining chunked text elements.
     *
     * @param parser
     *            The source {@link XMLParser} currently executing this rule.
     *            Providing access to the originating parser is handy if the
     *            rule wants to stop parsing by calling {@link XMLParser#stop()}
     *            .
     * @param text
     *            The character data contained between the open and close tags
     *            described by {@link #getLocationPath()}.
     * @param userObject
     *            The user-supplied object passed through from the
     *            {@link XMLParser}'s <code>parse</code> method directly to this
     *            handler. This is typically a data storage mechanism like a DAO
     *            or cache used to hold parsed data or <code>null</code> if you
     *            do not need to make use of this pass-through mechanism and
     *            passed nothing to the {@link XMLParser} when you initiated the
     *            parse.
     *
     * @see #getLocationPath()
     */
    public void handleParsedCharacters(XMLParser<T> parser, String text,
                                       T userObject);
}