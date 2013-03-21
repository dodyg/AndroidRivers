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
package com.thebuzzmedia.sjxp;

/**
 * Unchecked exception used to unify and report everything that can go wrong
 * during an XML parse.
 * <p/>
 * Using this helps simplify caller code by allowing them to optionally catch
 * this unchecked exception. Each exception of this type will include a detailed
 * explanation of what caused the underlying exception to occur and avoids
 * pushing up the concerns of the underlying impl to the caller.
 * <p/>
 * 90% of the time you just want to parse XML and know if it succeeded or
 * failed, so SJXP simplifies for this scenario.
 * <p/>
 * For callers that do want to know exactly what went wrong, you can use
 * {@link #getCause()} to get the source exception that this one is wrapping.
 *
 * @author Riyad Kalla (software@thebuzzmedia.com)
 */
public class XMLParserException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Create a new exception with the given message.
     *
     * @param message
     *            The explanation of why the exception was thrown.
     */
    public XMLParserException(String message) {
        super(message);
    }

    /**
     * Create a new exception with the given message and cause.
     *
     * @param message
     *            The explanation of why the exception was thrown.
     * @param cause
     *            The underlying exception that occurred that caused this one to
     *            be created.
     */
    public XMLParserException(String message, Exception cause) {
        super(message, cause);
    }
}