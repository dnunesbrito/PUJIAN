/*
 * Copyright (C) 2021 darlan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package anasy.literals;

import anasy.parser.Location;
import anasy.parser.Parser;
import anasy.parser.SyntaxError;
import anasy.parser.Token;

/**
 * Used to verify if the character sequence is an interval and return an interval if it is.
 * The literal interval must be as I{inferior limit,superior limit}
 * 
 * @author Darlan Nunes de Brito
 * @author Jônata Lucas Nogueira
 * @param <N> The tamplate type of the class.
 */
public abstract class IntervalNum<N>  extends Literal<N> {

    public IntervalNum(Parser<N> parser, String name) {
        super(parser, name);
    }

    @Override
    public Token<N> lex() throws SyntaxError {
        // Needs to be changed
        // digits.digits, .digits, digits.
        char c1 = input.peek(); //Gets one character from input string;
        char c2 = input.peek(1);
        char c3 = input.peek(2);
        if(Character.compare(c1, 'I') != 0 || Character.compare(c2, '{') != 0 && !Character.isDigit(c3)) return null; //If this character is a bracket assume that it is an interval;
        input.advance(2);
        Location lexloc = input.mark();
        input.advanceDigits(10);
        if (input.advanceIf('.')){ //Advance if the number is a decimal number
            input.advanceDigits(10);
        } 
        if (input.advanceIf(',')){ //Get the next digit after a comma
            input.advanceDigits(10);
        } 
        if (input.advanceIf('.')){ //Get the next digit after a dot
            input.advanceDigits(10);
        } 
        if (Character.isLetterOrDigit(input.peek()))
            throw new SyntaxError(String.format("Invalid digit '%c'", input.peek()), input.getLoc());
        Token<N> ltok = new Token<N>(this, lexloc, makeNode(input.extract()));
        if(!input.advanceIf('}'))
            throw new SyntaxError(String.format("Invalid interval entry missing }"), input.getLoc());
        return ltok;
    }

}
