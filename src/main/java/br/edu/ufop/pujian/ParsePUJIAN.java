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
package br.edu.ufop.pujian;

import br.edu.ufop.pujian.Engine.*;
import anasy.literals.*;
import anasy.operators.*;
import anasy.parser.*;
import anasy.whitespace.Whitespace;

import java.util.ArrayList;
import java.util.List;

/**
 * A parser to operations with {@link Interval}
 * 
 * Supports: 
 * <ul>
  * <li>infix comparisons: ==, !=, &lt;=, &lt;, ==, &gt;=, &st;</li>
 * <li>infix: +, -, *, /, %, ^</li>
 * <li>postfix: ! (factorial)</li>
 * <li>prefix: - (unary), @ (hashcode)</li>
 * <li>++, --</li>
 * <li>parantheses</li>
 * <li>if-then-else-end statements</li>
 * <li>function definitions</li>
 * <li>function invocation</li>
 * <li>declaration of new prefix/infix operators during compile-time</li>
 * </ul>
 *
 * @author Darlan Nunes de Brito
 * @author Jônata Lucas Nogueira
 */
public class ParsePUJIAN extends InteractiveParser<Node>{
    /**
     * The engine to contains operands, operators and methods to make operations
     */
    protected Engine engine;

    /**
     * Class constructor
     * 
     * @param engine contains operands, operators and methods to make operations
     */
    public ParsePUJIAN(Engine engine) {
        super();
        this.engine = engine;
    }

    /**
     * Allways permits peek the character before advance if current token is empty and 
     * @param stage 0-beforeAdvance/peek, 1-afterAdvance
     * @return True of False
     */
    @Override
    protected boolean canRefeed(int stage) {
        return curr == null && queryState >= 0 && stage == 0 && depth > 1;
    }

    @Override
    protected void init() {
        new Whitespace<>(this).setLevel(0);

        new RealNum<Node>(this, "<num>") {
            @Override
            public Node makeNode(String lexeme) {
                return engine.new Num(Double.parseDouble(lexeme));
            }
        }.setLevel(20);

        new Name<Node>(this, "<name>") {
            @Override
            public Node makeNode(String lexeme) {
                return engine.makeSym(lexeme);
            }
        }.setLevel(50);

        new IntervalNum<Node>(this, "<inter>"){ //Like it extends litral the constructor calls super constructor wihch will name type
            /**
             * Recieve an string of the type "inferior limit,superior limit" and returns an interval
             */
            @Override
            public Node makeNode(String lexeme) {
                String[] vals = lexeme.split(",");
                Double inf = Double.parseDouble(vals[0]);
                Double sup = Double.parseDouble(vals[1]);
                Interval val = new Interval(inf,sup);
                return engine.new Inter(val);
            }
        }.setLevel(50);
        
        new TokParentheses(this).setLevel(10);
        
        new TokBrakets(this).setLevel(10);

        new TokInfix(this, ";", 10).setLevel(10);

        new TokFun(this, 20).setLevel(10);

        new TokIf(this, 30).setLevel(10);

        /**
         * Initialize an token with left precedence equals to 40. As high left
         * precedence as operation is cast out first.i. e. "+" has lower precedence
         * level than "*". setleve Sets the recognition precedente, in this project
         * never is used is here just to eliminate warning.
         */
        new TokInfix(this, "==", 40).setLevel(10);
        new TokInfix(this, "=", 20).setLevel(10);
        new TokInfix(this, "!=", 40).setLevel(10);
        new TokInfix(this, "<=", 40).setLevel(10);
        new TokInfix(this, "<", 40).setLevel(10);
        new TokInfix(this, ">=", 40).setLevel(10);
        new TokInfix(this, ">", 40).setLevel(10);
        new TokPrefixInfix(this, "+", 100, 50).setLevel(10);
        new TokPrefixInfix(this, "-", 100, 50).setLevel(10);
        new TokInfix(this, "*", 60).setLevel(10);
        new TokInfix(this, "/", 60).setLevel(10);
        new TokInfix(this, "%", 60).setLevel(10);
        new TokInfix(this, "^", 70, true).setLevel(10);

        new TokPrefixPostfix(this, "++", 80, 80).setLevel(10);
        new TokPrefixPostfix(this, "--", 80, 80).setLevel(10);

        new Postfix<Node>(this, "!", 80) {
            @Override
            public Node makePostfixNode(Node operand) {
                return engine.new UnaryOp(name, operand);
            }
        };

        new Prefix<Node>(this, "@", 80) {
            @Override
            public Node makePrefixNode(Node operand) {
                return engine.new UnaryOp(name, operand);
            }
        };

        new Operator<Node>(this, "prefix", 0) {
            @Override
            public Node parse(Token<Node> token) throws SyntaxError {
                // prefix name pbp
                final Sym sym = (Sym) expression(1000, Sym.class);
                Num pbp = (Num) expression(1000, Num.class);
                // define new prefix operator: name=sys.varName, pbp=pbp.varName
                new Prefix<Node>(parser, sym.varName, (int) pbp.val) {
                    @Override
                    public Node makePrefixNode(Node operand) {
                        Node[] args = {operand};
                        return engine.new Composite(sym, args);
                    }
                };
                return engine.new Num(0);
            }
        };

        new Operator<Node>(this, "infix", 0) {
            @Override
            public Node parse(Token<Node> token) throws SyntaxError {
                // infix name lbp
                final Sym sym = (Sym) expression(1000, Sym.class);
                Num lbp = (Num) expression(1000, Num.class);
                // define new infix operator: name=sys.varName, lbp=lbp.varName
                new Infix<Node>(parser, sym.varName, (int) lbp.val) {
                    @Override
                    public Node makeInfixNode(Node left, Node right) {
                        Node[] args = {left, right};
                        return engine.new Composite(sym, args);
                    }
                };
                return engine.new Num(0);
            }
        };
    }

    class TokInfix extends Infix<Node> {
        public TokInfix(Parser<Node> parser, String name, int lbp) {
            super(parser, name, lbp);
        }

        public TokInfix(Parser<Node> parser, String name, int lbp, boolean rightAssoc) {
            super(parser, name, lbp, rightAssoc);
        }

        @Override
        public Node makeInfixNode(Node left, Node right) {
            return engine.new BinOp(name, left, right);
        }
    }

    class TokPrefixInfix extends PrefixInfix<Node> {
        public TokPrefixInfix(Parser<Node> parser, String name, int pbp, int lbp) {
            super(parser, name, pbp, lbp);
        }

        @Override
        public Node makePrefixNode(Node operand) {
            return engine.new UnaryOp(name, operand);
        }

        @Override
        public Node makeInfixNode(Node left, Node right) {
            return engine.new BinOp(name, left, right);
        }
    }

    class TokPrefixPostfix extends PrefixPostfix<Node> {
        public TokPrefixPostfix(Parser<Node> parser, String name, int pbp, int lbp) {
            super(parser, name, pbp, lbp);
        }

        @Override
        public Node makePrefixNode(Node operand) {
            return engine.new UnaryOp(name, operand);
        }

        @Override
        public Node makePostfixNode(Node operand) {
            return engine.new UnaryOp(name, operand);
        }
    }

    public class TokIf extends Operator<Node> {
        private final Tokind<Node> tokThen;
        private final Tokind<Node> tokElse;
        private final Tokind<Node> tokEnd;

        public TokIf(Parser<Node> parser, int ifbp) {
            super(parser, "if", ifbp);
            tokThen = Operator.make(parser, "then", 0);
            tokElse = Operator.make(parser, "else", 0);
            tokEnd = Operator.make(parser, "end", 0);
        }

        public Node makeIfNode(Node head, Node body, Node tail) {
            return engine.new Branch(head, body, tail);
        }

        @Override
        public Node parse(Token<Node> token) throws SyntaxError {
            // prefix form: if Head then Body else Tail end
            // head
            Node head = expression(lbp);
            // then E
            advance(tokThen);
            Node body = expression(tokThen.lbp());
            // else E
            advance(tokElse);
            Node tail = expression(tokElse.lbp());
            // end
            advance(tokEnd);
            return makeIfNode(head, body, tail);
        }

        @Override
        public Node parse(Token<Node> token, Node left) throws SyntaxError {
            // infix form: Body if Head else Tail
            Node head = expression(lbp);
            advance(tokElse);
            Node tail = expression(tokElse.lbp());
            return makeIfNode(head, left, tail);
        }
    }

    class TokParentheses extends Outfix<Node> {
        Tokind<Node> tokComma;

        public TokParentheses(Parser<Node> parser) {
            super(parser, "(", ")", 0);
            this.lbp = 200;
            this.tokComma = Operator.make(parser, ",", 0);
        }

        @Override
        public Node parse(Token<Node> token, Node left) throws SyntaxError {
            if (!(left instanceof Engine.Sym))
                throw new SyntaxError("Function name expected", input.getLoc());
            // function call or definition
            List<Node> exprs = new ArrayList<>();
            if (advanceIf(tokClose) == null)
                do
                    exprs.add(expression(0));
                while (advanceIf(tokComma) != null);
            advance(tokClose);
            return engine.new Composite((Engine.Sym) left, exprs.toArray(new Node[exprs.size()]));
        }
    }

    /**
     * Implents this later
     */
    class TokBrakets extends Outfix<Node> {
        Tokind<Node> tokComma;

        public TokBrakets(Parser<Node> parser) {
            super(parser, "{", "}", 0);
            this.lbp = 200;
            this.tokComma = Operator.make(parser, ",", 0);
        }

        @Override
        public Node parse(Token<Node> token, Node left) throws SyntaxError {
            if (!(left instanceof Engine.Sym))
                throw new SyntaxError("Function name expected", input.getLoc());
            // function call or definition
            List<Node> exprs = new ArrayList<>();
            if (advanceIf(tokClose) == null)
                do
                    exprs.add(expression(0));
                while (advanceIf(tokComma) != null);
            advance(tokClose);
            return engine.new Composite((Engine.Sym) left, exprs.toArray(new Node[exprs.size()]));
        }
    }

    class TokFun extends Operator<Node> {
        private final Tokind<Node> tokIs;
        private final Tokind<Node> tokEnd;

        public TokFun(Parser<Node> parser, int lbp) {
            super(parser, "fun", lbp);
            this.tokIs = Operator.make(parser, "is", 0);
            this.tokEnd = Operator.make(parser, "end", 0);
        }

        @Override
        public Node parse(Token<Node> token) throws SyntaxError {
            // fun <name> (<args>) is <body> end
            Node node = expression(0);
            if (!(node instanceof Composite))
                throw new SyntaxError("Expected argument list", input.getLoc());
            // parser is and body of the function
            advance(tokIs);
            Node body = expression(0);
            // end
            advance(tokEnd);
            //
            Composite c = (Composite) node;
            return engine.new Func(c.head, c.args, body);
        }
    }
    
}
