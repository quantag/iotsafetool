/*
 * Copyright 2023-2026 Quantag IT Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.iotsafe.exception;

/**
 * Unchecked exception raised when the card, the reader, or the applet does
 * not respond as expected.
 */
public class CardletException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CardletException(String message) {
        super(message);
    }
    
    public CardletException(Exception e) {
        super(e);
    }

}
