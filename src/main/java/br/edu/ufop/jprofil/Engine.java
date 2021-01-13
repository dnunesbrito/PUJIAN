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
package br.edu.ufop.jprofil;

/**
 * This class is used have the definitions to operators and other methods necessary 
 * to basics operation of Operator-Parser.
 * 
 * @author Darlan Nunes de Brito
 * @author Jônata Lucas Nogueira
 */

import java.util.HashMap;
import java.util.Map;

public class Engine {

    Map<String, Sym> symbols;
    Context context;

    /**
     * Empty class constructor
     */
    public Engine() {
        symbols = new HashMap<>();
        context = new Context(null);
    }

    /**
     * Print the Nodes separated by a delimiter
     * 
     * @param items Nodes to be printed
     * @param delim Character to separate the nodes
     * @return A StringBuffer with printed nodes separated by the delimiter
     */
    public static String itemsToString(Node[] items, String delim) {
        StringBuffer s = new StringBuffer();
        int i = 0;
        while (i < items.length) {
            s = s.append(items[i].toString());
            if (i < items.length - 1)
                s = s.append(delim);
            i++;
        }
        return s.toString();
    }

    /**
     * Create a symbolic variable without value only variable
     * 
     * @param varName the variable name
     * @return A {@link Engine.Sym} instance and set a symbolic variable on Engine environment
     */
    public Node makeSym(String varName) {
        if (symbols.containsKey(varName))
            return symbols.get(varName);
        Sym sym = new Sym(varName);
        symbols.put(varName, sym);
        return sym;
    }

    /**
     * Create a variable on Engine environment with name and val. This variable can be used
     * in a string expression
     * 
     * @param name The name of the variable
     * @param val A Node with the value of the variable
     */
    public void push(String name,Node val){
        Sym a = (Sym) makeSym(name);
        context.set(a, val);
    }

    /**
     * Create a new context with old context
     */
    public void push() {
        this.context = new Context(this.context);
    }

    // ***** Symbols *****

    /**
     * Remove a context
     */
    public void pop() {
        this.context = this.context.parent;
    }

    /**
     * Class to envelop other type declared on this class like Num and IntervalNum
     * the main goal of this classe is to be used to generalize the types.
     */
    public class Node {
        /**
         * Return the node
         * @return this The owned Node
         * @throws SemanticError 
         */
        public Node eval() throws SemanticError {
            return this;
        }

        /**
         * Overridable method to make the unary operation. Must be overridable to enveloped type like Num or IntervalNum
         * 
         * @param op A string with the unary operation
         * @return Nothing only throws a exception if unary op is not defined
         * @throws SemanticError 
         */
        public Node doUnaryOp(String op) throws SemanticError {
            throw new SemanticError(String.format("Operation '%s' not implemented.", op));
        }

        /**
         * Overridable method to make the binary operation. Must be overridable to enveloped type like Num or IntervalNum
         * @param op A string with the unary operation
         * @param other The right operator
         * @return A Node with operation result. If the operator is ";" return the own node.
         * @throws SemanticError 
         */
        public Node doBinOp(String op, Node other) throws SemanticError {
            if (";".equals(op)) {
                eval();
                return other.eval();
            }
            throw new SemanticError(String.format("Operation '%s' not implemented.", op));
        }

        /**
         * On construction
         * @return always true
         */
        public boolean isTrue() {
            return true;
        }
    }

    /**
     * Class to contain a real number
     */
    public class Num extends Node {
        double val;

        public Num(double val) {
            super();
            this.val = val;
        }

        @Override
        public String toString() {
            return Double.toString(val);
        }

        @Override
        public Node doUnaryOp(String op) throws SemanticError {
            if ("-".equals(op))
                return new Num(-this.val);
            if ("+".equals(op))
                return this;
            if ("--".equals(op))
                return new Num(this.val - 1);
            if ("++".equals(op))
                return new Num(this.val + 1);
            if ("!".equals(op)) {
                double n = val, r = 1;
                while (n > 1) r *= n--;
                return new Num(r);
            }
            if ("@".equals(op))
                return new Num(this.hashCode());
            return super.doUnaryOp(op);
        }

        @Override
        public Node doBinOp(String op, Node other) throws SemanticError {
            Num that = (Num) other;
            if ("+".equals(op))
                return new Num(val + that.val);
            if ("-".equals(op))
                return new Num(val - that.val);
            if ("*".equals(op))
                return new Num(val * that.val);
            if ("/".equals(op))
                return new Num(val / that.val);
            if ("%".equals(op))
                return new Num(val % that.val);
            if ("^".equals(op))
                return new Num(Math.pow(val, that.val));
            if ("==".equals(op))
                return new Num(val == that.val ? 1 : 0);
            if ("!=".equals(op))
                return new Num(val != that.val ? 1 : 0);
            if ("<=".equals(op))
                return new Num(val <= that.val ? 1 : 0);
            if ("<".equals(op))
                return new Num(val < that.val ? 1 : 0);
            if (">=".equals(op))
                return new Num(val >= that.val ? 1 : 0);
            if (">".equals(op))
                return new Num(val > that.val ? 1 : 0);
            return super.doBinOp(op, other);
        }

        @Override
        public boolean isTrue() {
            return this.val != 0;
        }
    }
    /**
     * Class to hold an interval Node
     */
    public class Inter extends Node{
        Interval val;
        
        public Inter(Interval val) {
            super();
            this.val = val;
        }
        
        public Inter(double inf, double sup){
            super();
            val = new Interval(inf,sup);
        }
        
        @Override
        public String toString(){
            return "[" + val.inf + "," + val.sup + "]";
        }
        @Override
        public Node doUnaryOp(String op) throws SemanticError {
            if ("=".equals(op))
                return new Inter(this.val);
            return super.doUnaryOp(op);
        }
        
        @Override
        public Node doBinOp(String op, Node other) throws SemanticError {
            Inter that = (Inter) other;
            if ("+".equals(op))
                return new Inter(val.add(that.val));
            return super.doBinOp(op, other);
        }

    }
                
    public class UnaryOp extends Node {
        String op;
        Node operand;

        public UnaryOp(String op, Node operand) {
            super();
            this.op = op;
            this.operand = operand;
        }

        @Override
        public String toString() {
            return "(" + op + " " + operand.toString() + ")";
        }

        @Override
        public Node eval() throws SemanticError {
            return operand.eval().doUnaryOp(op);
        }
    }

    public class BinOp extends Node {
        String op;
        Node left;
        Node right;

        public BinOp(String op, Node left, Node right) {
            super();
            this.op = op;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "(" + left.toString() + op + right.toString() + ")";
        }
        /**
         * If operator is "equal", set the contex list with the value creating a Sym and context.
         * If not get the left operator from context list.
         * @return Value of operation on a Node.
         * @throws SemanticError 
         */
        @Override
        public Node eval() throws SemanticError {
            Node l = ("=".equals(op)) ? left : left.eval(); //If operator is equal put left node on l if not get node from context variable 
            Node r = right.eval();
            return l.doBinOp(op, r);
        }
    }

    // ***** Functions *****

    public class Branch extends Node {
        // if head then body else tail
        Node head;
        Node body;
        Node tail;

        public Branch(Node head, Node body, Node tail) {
            super();
            this.head = head;
            this.body = body;
            this.tail = tail;
        }

        @Override
        public String toString() {
            return "if " + head.toString() + " then " + body.toString() + " else " + tail.toString() + " end";
        }

        @Override
        public Node eval() throws SemanticError {
            if (head.eval().isTrue())
                return body.eval();
            else
                return tail.eval();
        }
    }

    public class Sym extends Node {
        String val;

        public Sym(String val) {
            super();
            this.val = val;
        }

        @Override
        public String toString() {
            return val;
        }

        @Override
        public Node eval() throws SemanticError {
            Node val = context.get(this);
            if (val == null)
                throw new SemanticError(String.format("Undefined variable '%s'", this));
            return val;
        }

        /**
         * Create a context entry if the operator is equal if not call the super 
         * BinOp. This super must be implemented by specific class, i. e. Num class.
         * 
         * @param op String with operation
         * @param other right operand, left operand is this.
         * @return The Node that result from operation.
         * @throws SemanticError 
         */
        @Override
        public Node doBinOp(String op, Node other) throws SemanticError {
            if ("=".equals(op))
                return context.set(this, other);
            return super.doBinOp(op, other);
        }
    }

    // Context

    public class Context {
        Context parent;
        Map<Sym, Node> vars;

        public Context(Context parent) {
            this.parent = parent;
            this.vars = new HashMap<Sym, Node>();
        }

        public Node get(Sym sym) {
            if (vars.containsKey(sym))
                return vars.get(sym);
            if (parent != null)
                return parent.get(sym);
            return null;
        }

        public Node set(Sym sym, Node val) {
            vars.put(sym, val);
            return val;
        }
    }

    public class Composite extends Node {
        Sym head;
        Node[] args;

        public Composite(Sym head, Node[] args) {
            this.head = head;
            this.args = args;
        }

        @Override
        public String toString() {
            return this.head + "(" + itemsToString(args, ",") + ")";
        }

        @Override
        public Node eval() throws SemanticError {
            Node node = context.get(this.head);
            if (!(node instanceof Func))
                throw new SemanticError(String.format("'%s' is not a function.", this.head));
            Func func = (Func) node;
            if (func.args.length != this.args.length)
                throw new SemanticError("Argument mismatch.");
            Context newctx = new Context(context);
            for (int i = 0; i < func.args.length; i++)
                newctx.set(((Sym) func.args[i]), args[i].eval());
            Node result;
            Engine.this.context = newctx;
            try {
                result = func.body.eval();
            } finally {
                pop();
            }
            return result;
        }
    }

    // **** Constructor *****

    public class Func extends Node {
        protected Sym head;
        protected Node[] args;
        protected Node body;

        public Func(Sym head, Node[] args, Node body) {
            this.head = head;
            this.args = args;
            this.body = body;
        }

        @Override
        public Node eval() throws SemanticError {
            return context.set(head, this);
        }

        @Override
        public String toString() {
            return "fun " + head + "(" + itemsToString(args, ",") + ") is " + body + " end";
        }
    }
}
