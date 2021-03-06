// Copyright 2009 The Apache Software Foundation
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

package org.apache.tapestry5.internal.translator;

import java.text.DecimalFormatSymbols;
import java.text.ParseException;

/**
 * Base class for parsing/formatting BigInteger and BigDecimal.
 *
 * @since 5.1.0.1
 */
public abstract class BigTypesFormatter implements NumericFormatter
{
    protected final DecimalFormatSymbols symbols;

    public BigTypesFormatter(DecimalFormatSymbols symbols)
    {
        this.symbols = symbols;
    }

    protected String toString(char ch)
    {
        return String.valueOf(ch);
    }

    public String toClient(Number value)
    {
        String normal = value.toString();

        // When formatting integers, we don't use the grouping seperator.

        return normal.replace('-', symbols.getMinusSign()).replace('.', symbols.getDecimalSeparator());
    }

    public Number parse(String clientValue) throws ParseException
    {
		// Allow for " " here since FF2/IE replaces \u00A0 (non breaking space) with \u0020 (normal space)
        String noGroups = clientValue.replace(toString(symbols.getGroupingSeparator()), "").replace(" ", "");
        String fixedNeg = noGroups.replace(symbols.getMinusSign(), '-');
        String fixedDec = fixedNeg.replace(symbols.getDecimalSeparator(), '.');

        try
        {
            return parseConvertedValue(fixedDec);
        }
        catch (NumberFormatException ex)
        {
            throw new ParseException(ex.getMessage(), 0);
        }
    }

    protected abstract Number parseConvertedValue(String converted);
}
