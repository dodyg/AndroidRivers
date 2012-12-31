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


public enum ParsingMode {
    /**
     * Type used to indicate a rule interested in START_TAG and END_TAG
     * events for the matching location path.
     * <p/>
     * This can be handy when no parsed data is needed from the underlying
     * XML, but rather a simple notification that the location path existed
     * in the XML (e.g. counting element occurrences).
     */
    TAG,
    /**
     * Type used to indicate that this rule describes 1 or more attribute
     * values that the caller wants parsed.
     */
    ATTRIBUTE,
    /**
     * Used to describe a rule that will be called
     *
     * Type used to indicate that this rule describes the character data
     * between an open and close tag that the caller wants parsed.
     */
    CHARACTER
}
