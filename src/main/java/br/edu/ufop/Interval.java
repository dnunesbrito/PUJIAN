/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ufop;

/**
 * Class used to define the encapsuled type Interval. This type
 * is used to make basic operations with interval.
 * 
 * @author Darlan Nunes de Brito
 * @author Jonata Lucas Nogueira
 */
public class Interval {
    private double inf;
    private double sup;

    public Interval() {
    }
    
    
    /**
     * Class constructor
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
     * 
     * @param interval 
     */
    public Interval(Interval interval){
        this.inf = interval.inf;
        this.sup = interval.sup;
    }
    
    public Interval(double degenarated){
        this.inf = degenarated;
        this.sup = degenarated;
    }
    
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
}
