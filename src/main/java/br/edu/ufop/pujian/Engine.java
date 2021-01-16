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
        
        /**
         * Real value of the Num instance
         */
        double val;

        /**
         * Class constructor
         * 
         * @param val Value of the Num instance
         */
        public Num(double val) {
            super();
            this.val = val;
        }

        /**
         * Returns a string with the method value
         * 
         * @return String with method value
         */
        @Override
        public String toString() {
            return Double.toString(val);
        }

        /**
         * Make a unary operation with one real number in an instance of Num
         * 
         * @param op String with operator that can be: <ul>
         * <li> + Positive unary </li>
         * <li> - Negative unary </li>
         * <li> ++ Increment </li>
         * <li> -- Decrement </li>
         * <li> ! Factorial </li>
         * </ul>
         * @return
         * @throws SemanticError 
         */
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

        /**
         * Make a unary operation with one real number in an instance of Num
         * 
         * @param op String with operator that can be: <ul>
         * <li> + Add two real numbers </li>
         * <li> - Subtract two real numbers </li>
         * <li> * Multiply two real numbers </li>
         * <li> / Divide two real numbers </li>
         * <li> % Modulo of two real numbers </li>
         * <li> ^ Power of two real numbers </li>
         * <li> == Test if two real numbers are equals </li>
         * <li> != Test if two real numbers are different </li>
         * <li> &lt;= Less than or equals </li>
         * <li> &lt; Lower than </li>
         * <li> &gt;= Greater than or equals </li>
         * <li> &gt; Greater than </li>
         * 
         * </ul>
         * @param other The {@link Engine.Node} with right operand
         * @return A {@link Engine.Node} with the operation results
         * @throws SemanticError 
         */
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
            if (",".equals(op))
                return new Inter(val, that.val);
            return super.doBinOp(op, other);
        }

        /**
         * Return True if value is different from zero
         * 
         * @return True if value is different from zero
         */
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
        
        /**
         * Class constructor
         * 
         * @param val An {@link Interval}
         */
        public Inter(Interval val) {
            super();
            this.val = val;
        }
        
        /**
         * Class constructor 
         * 
         * @param inf Inferior value of the interval
         * @param sup Superio value of the interval
         */
        public Inter(double inf, double sup){
            super();
            val = new Interval(inf,sup);
        }
        
        /**
         * Returns a String with inferior and superior interval
         * @return String with inferior and superior interval
         */
        @Override
        public String toString(){
            return "[" + val.inf + "," + val.sup + "]";
        }

        /**
         * Make a unary operation with one real number in an instance of Num
         * 
         * @param op String with operator that can be: <ul>
         * <li> + Positive unary </li>
         * <li> - Negative unary </li>
         * <li> ++ Increment </li>
         * <li> -- Decrement </li>
         * <li> ! Factorial </li>
         * </ul>
         * @return
         * @throws SemanticError 
         */
        @Override
        public Node doUnaryOp(String op) throws SemanticError {
            if ("=".equals(op))
                return new Inter(this.val);
            return super.doUnaryOp(op);
        }
        
        /**
         * Make a unary operation with one real number in an instance of Num
         * 
         * @param op String with operator that can be: <ul>
         * <li> + Add two {@link Interval} </li>
         * <li> - Subtract two {@link Interval} </li>
         * <li> * Multiply two {@link Interval} </li>
         * <li> / Divide two {@link Interval} </li>
         * <li> % Modulo of two {@link Interval} </li>
         * <li> ^ Power of two {@link Interval} </li>
         * <li> == Test if two {@link Interval} are equals </li>
         * <li> != Test if two {@link Interval} are different </li>
         * <li> &lt;= Less than or equals </li>
         * <li> &lt; Lower than </li>
         * <li> &gt;= Greater than or equals </li>
         * <li> &gt; Greater than </li>
         * 
         * </ul>
         * @param other The {@link Engine.Node} with right operand
         * @return A {@link Engine.Node} with the operation results
         * @throws SemanticError 
         */
        @Override
        public Node doBinOp(String op, Node other) throws SemanticError {
            Inter that = (Inter) other;
            if ("+".equals(op))
                return new Inter(val.add(that.val));
            return super.doBinOp(op, other);
        }

    }

    /**
     * A class to creates a Node with one literal and an unary operand
     */
    public class UnaryOp extends Node {
        /**
         * Operator
         */
        String op;
        
        /**
         * Operand
         */
        Node operand;

        /**
         * Class constructor. Set the operator and operand
         * @param op Operator
         * @param operand Operand
         */
        public UnaryOp(String op, Node operand) {
            super();
            this.op = op;
            this.operand = operand;
        }

        /**
         * 
         * @return A string with operator and operand inbetween parenthesis
         */
        @Override
        public String toString() {
            return "(" + op + " " + operand.toString() + ")";
        }

        /**
         * 
         * @return A {@link Engine.Node} with the result value
         * @throws SemanticError 
         */
        @Override
        public Node eval() throws SemanticError {
            return operand.eval().doUnaryOp(op);
        }
    }
    
    /**
     * A class to creates a Node with two literals and an binary operand
     */
    public class BinOp extends Node {
        String op;
        Node left;
        Node right;

        /**
         * Class constructor. Set the operator and operands
         * @param op Operator
         * @param left Operand
         * @param right Operand
         */
        public BinOp(String op, Node left, Node right) {
            super();
            this.op = op;
            this.left = left;
            this.right = right;
        }

        /**
         * 
         * @return A string with operator and operand inbetween parenthesis
         */
        @Override
        public String toString() {
            return "(" + left.toString() + op + right.toString() + ")";
        }
        /**
         * If operator is "equal", set the contex list with the value creating a Sym and context.
         * If not get the left operator from context list and make the operation
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
    // Not used yet than not documented
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

    /**
     * Stores a symbolic variable. Symbolic varible is only a name.
     */
    public class Sym extends Node {
        /**
         * String with variable name
         */
        String varName;

        /**
         * Creates a symbolic variable with the name in parameter
         * @param varName Variable name 
         */
        public Sym(String varName) {
            super();
            this.varName = varName;
        }

        /**
         * Return the variable name
         * @return 
         */
        @Override
        public String toString() {
            return varName;
        }

        /**
         * 
         * @return The {@link Engine.Node} with the numeric value of the varName variable
         * @throws SemanticError 
         */
        @Override
        public Node eval() throws SemanticError {
            Node val = context.get(this);
            if (val == null)
                throw new SemanticError(String.format("Undefined variable '%s'", this));
            return val;
        }

        /**
         * Create a context entry when the operator is equal if not call the super 
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

    /**
     * Class to store the map that link the value and operator name
     */
    public class Context {
        
        /**
         * The context parent variable can be created a chain of contexts (Not utterly understanded yet)
         */
        Context parent;
        
        /**
         * A hash map with a {@link Engine.Sym} as key and {@link Engine.Node} as value
         */
        Map<Sym, Node> vars;

        /**
         * Class constructor
         * @param parent The instance parent of the creating instance
         */
        public Context(Context parent) {
            this.parent = parent;
            this.vars = new HashMap<>();
        }

        /**
         * 
         * @param sym
         * @return The {@link Engine.Node} with the value of the {@link Engine.Sym} operand
         */
        public Node get(Sym sym) {
            if (vars.containsKey(sym))
                return vars.get(sym);
            if (parent != null)
                return parent.get(sym);
            return null;
        }

        /**
         * Set the value of a symbolic variable with the value in the val
         * 
         * @param sym {@link Engine.Sym} with the symbolic name of the operand
         * @param val {@link Engine.Node} with the value of the operand
         * @return {@link Engine.Node} with the value of the new operand
         */
        public Node set(Sym sym, Node val) {
            vars.put(sym, val);
            return val;
        }
    }

    /**
     * When the string has a {@link Engine.Sym} followed by a {@link Engine.Node} this is a composite. 
     * For example, A{2,3} or B(2,3) or C(4,5)
     */
    public class Composite extends Node {
        
        /**
         * The symbolic variable with the name.
         */
        Sym head;
        
        /**
         * The nodes after the symbolic variable.
         */
        Node[] args;
        
        /**
         * The brackets type afte the symbolic variable.
         */
        char bracketType;

        /**
         * Class constructor
         * 
         * @param head The symbolic variable with the name
         * @param args An array of nodes between the brakcets
         */
        public Composite(Sym head, Node[] args) {
            this.head = head;
            this.args = args;
        }

        /**
         * Class constructor
         * 
         * @param head The symbolic variable with the name
         * @param args An array of nodes between the brakcets
         * @param bracketType The brackets type
         */
        public Composite(Sym head, Node[] args, char bracketType) {
            this.head = head;
            this.args = args;
            this.bracketType = bracketType;
        }

        @Override
        public String toString() {
            return this.head + "(" + itemsToString(args, ",") + ")";
        }

        @Override
        public Node eval() throws SemanticError {
            if (Character.compare(bracketType, '[') == 0){
                Node node = context.get(this.head);
            }
            if (Character.compare(bracketType, '{') == 0){
                Node node = context.get(this.head);
                if(!(node instanceof Inter)){
                    Num inf = (Num)this.args[0].eval();
                    Num sup = (Num)this.args[1].eval();
                    //Engine.BinOp binop = (BinOp) this.args[0];
                    //Num inf = (Num) binop.left.eval();
                    //Num sup = (Num) binop.right.eval();
                    Inter internode = new Inter(inf.val,sup.val);
                    context.set(head, internode);
                    return internode;
                }                    
            }
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
    // Not understanded yet not necessary yet
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
