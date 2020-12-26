/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ufop.jprofil;

import java.util.HashMap;

/**
 * Class used to define the Interval type. This type
 * is used to make basic operations with interval.
 * 
 * @author Darlan Nunes de Brito
 * @author Jonata Lucas Nogueira
 */
public class Interval {
    private double inf;
    private double sup;
    
    private static HashMap<String, Interval> variables = new HashMap<>();

    public static Interval eval(String op){
        Interval A = variables.get(op);
        return A;
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
     */
    public Interval(double inf, double sup) {
        if(inf < sup){
            this.inf = inf;
            this.sup = sup;
        }
        else{
            this.inf = sup;
            this.sup = inf;
        }
    }
    
    /**
     * Class constructor with an interval object
     * 
     * @param interval 
     */
    public Interval(Interval interval){
        this.inf = interval.inf;
        this.sup = interval.sup;
    }
    
    /**
     * Class constructor to a degenarated interval
     * 
     * @param degenarated Value of the degenarated interval
     */
    public Interval(double degenarated, String name){
        this.inf = degenarated;
        this.sup = degenarated;
        variables.put(name, this);
    }
    
    /**
     * Sum this class with an interval
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
    
    public void getVariables(){
        variables.keySet().stream().map((key) -> {
            System.out.println(key);
            return key;
        }).forEachOrdered((key) -> {
            System.out.println(variables.get(key));
        });
    }
}
