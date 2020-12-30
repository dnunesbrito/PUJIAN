/*
 * Copyright (C) 2020 
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

import java.util.*; 

/**
 * Class used to define the Interval type. This type
 * is used to make basic operations with intervals.
 * 
 * 
 * @author Darlan Nunes de Brito
 * @author Jônata Lucas Nogueira
 */
public class Interval {
    /**
     * Public member to inferior limit of the interval
     */
    public double inf;
    
    /**
     * Public member to inferior limit of the interval
     */
    public double sup;

    
    /**
     * Stores instance names and his values.
     */
    private static HashMap<String, Interval> variables = new HashMap<>();

    /**
     * Function to initialize variables names.
     * @param varName Instance name
     */
    private void init(String varName) {
        variables.put(varName+this.toString(), this);
    }
    /**
     * Empty class constructor
     */
    public Interval() {
    }
    
    
    /**
     * Class constructor with interval limits
     * 
     * @param inf Interval inferior limit
     * @param sup Interval superior limit
     * @param varName String with instance name
     */
    public Interval(double inf, double sup, String varName) {
        if(inf < sup){
            this.inf = inf;
            this.sup = sup;
        }
        else{
            this.inf = sup;
            this.sup = inf;
        }
        init(varName);
    }
    
    /**
     * Class constructor with an interval object
     * 
     * @param interval
     * @param newName String with instance name
     */
    public Interval(Interval interval,String newName){
        this.inf = interval.inf;
        this.sup = interval.sup;
        init(newName);
    }
    
    /**
     * Class constructor to a degenarated interval
     * 
     * @param degenarated Value of the degenarated interval
     * @param varName String with instance name
     */
    public Interval(double degenarated, String varName){
        this.inf = degenarated;
        this.sup = degenarated;
        init(varName);
    }
    
    /**
     * Method to evaluate a string with a mathematical expression
     * 
     * @param expr Mathematical expression to be evaluated.
     * @return Interval with the value of evaluated expression
     */
    public static Interval eval(String expr){
        Interval A = new Interval();
        Deque<Pair> idxs = new ArrayDeque<>();
        idxs = Interval.getBracketsIndexes(expr);
        if(!idxs.isEmpty())
            for(Pair idx : idxs){
                System.out.println(idx);
            }
        
        return A;
    }

    /**
     * Sum two intervals
     * 
     * @param A Interval to make sumation
     * @return Interval result
     */
    public Interval add(Interval A){
        Interval sum = new Interval();
        sum.inf = this.inf + A.inf;
        sum.sup = this.sup + A.sup;
        
        if(sum.inf > sum.sup){
            double tmp;
            tmp = sum.inf;
            sum.inf = sum.sup;
            sum.sup = tmp;
        }
        return sum;
    }
    /**
     * Function to print the list of instances names
     */
    public static void printInstances(){
        variables.keySet().stream().map((key) -> {
            System.out.print(key + ": ");
            return key;
        }).forEachOrdered((key) -> {
            System.out.println(variables.get(key).inf + "," + variables.get(key).sup + "]");
        });
    }
    /**
    * Get the expressions within the brackets in order that they appear on the expression,
    * the expression is a String object with a mathematical expression.
    * 
    * @param expr A string with a mathematical expression i.e. "A+B-(C*D)+(E/(F*G))
    * @return A {@link java.util.Deque} variable
    * @throws ArithmeticException 
    */
    static Deque<Pair> getBracketsIndexes(String expr) throws ArithmeticException
    { 
        // Using ArrayDeque is faster than using Stack class 
        Deque<Character> stackOpen 
            = new ArrayDeque<>(); 

        Deque<Pair> stackIdx = new ArrayDeque<>();
        Deque<Pair> stackFinalIdx = new ArrayDeque<>();

        CharSequence openBracket = ")";
        CharSequence closeBracket = "(";
        if(expr.contains(openBracket) && !expr.contains(closeBracket)){
            throw new ArithmeticException("Unbalaced brackets");
        }else if(!expr.contains(openBracket) && expr.contains(closeBracket)){
            throw new ArithmeticException("Unbalaced brackets");
        }
        
        // Traversing the Expression 
        for (int i = 0; i < expr.length(); i++)  
        { 
            char x = expr.charAt(i); 
  
            if (x == '(')  
            { 
                // Push the element in the stackOpen 
                stackOpen.push(x);
                stackIdx.push(Pair.push(i, Integer.MAX_VALUE));
                continue; 
            }

            if (x == ')') {
                //Check if there are opened brackets
                if(stackOpen.isEmpty())
                    throw new ArithmeticException("Unbalaced brackets");
                stackOpen.pop();
                Pair pair = stackIdx.pop();
                pair.end = i;
                stackFinalIdx.push(pair);                
            } 
        } 
        return stackFinalIdx;
    } 

}
