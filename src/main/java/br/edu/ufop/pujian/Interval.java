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
package br.edu.ufop.pujian;

   
/**
 * Class used to define the Interval type. This type
 * is used to make basic operations with intervals.
 * 
 * 
 * @author Darlan Nunes de Brito
 * @author Jônata Lucas Nogueira
 */
public class Interval extends Engine{
    /**
     * Public member to inferior limit of the interval
     */
    private double inf;
    
    /**
     * Get the lower bound of the interval
     * @return The lower bound of the interval
     */
    public double getInf(){
        return inf;
    }
    
    /**
     * Set the lower bound value of the interval
     * @param val Value to set the lower bound
     */
    public void setInf(double val){
        if (val < sup){
            inf = val;
        }else{
            inf = sup;
            sup = val;
        }
    }
    /**
     * Public member to superior limit of the interval
     */
    private double sup;

    /**
     * Get the upper bound of the interval
     * @return The lower bound of the interval
     */
    public double getSup(){
        return sup;
    }
    
    /**
     * Set the upper bound of the interval
     * @param val The upper bound of the interval
     */
    public void setSup(double val){
        if (val > inf){
            sup = val;
        }else{
            sup = inf;
            inf = val;
        }
    }

    
    /**
     * Empty class constructor
     */
    public Interval( ) {
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
    public Interval(double degenarated){
        this.inf = degenarated;
        this.sup = degenarated;
    }

    /**
     * Unary positive operation in this interval
     * 
     * @return Interval result
     */
    public Interval uniAdd(){
        Interval uniAdd = new Interval();
        uniAdd.inf = this.inf;
        uniAdd.sup = this.sup;
        
        return uniAdd;
    }
    
    /**
     * Unary negative operation in this interval
     * 
     * @return Interval result
     */
    public Interval uniSub(){
        Interval uniSub = new Interval();
        uniSub.inf = -this.sup;
        uniSub.sup = -this.inf;
        
        return uniSub;
    }
    
    /**
     * Increment this interval by one
     */
    public void increment(){
        this.inf=this.inf+1;
        this.sup=this.sup+1;
    }
    /**
     * Decrement this interval by one
     *
     */
    public void decrement(){
        this.inf = this.inf-1;
        this.sup = this.sup-1;
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
     * Subtract two intervals
     * 
     * @param A Interval to make subtraction
     * @return Interval result
     */
    public Interval sub(Interval A){
        Interval sub = new Interval(A);
        sub.inf = this.inf - A.sup;
        sub.sup = this.sup - A.inf;
        
        if(sub.inf > sub.sup){
            double tmp;
            tmp = sub.inf;
            sub.inf = sub.sup;
            sub.sup = tmp;
        }
        return sub;
    } 
    
    /**
     * Multiplies two intervals
     * 
     * @param A Interval to make multiplication
     * @return Interval result
     */
    public Interval mult(Interval A){
        double result[] = new double[4];
        result[0] = this.inf * A.inf;
        result[1] = this.inf * A.sup;
        result[2] = this.sup * A.inf;
        result[3] = this.sup * A.sup;
        
        Interval mult = new Interval();
        
        mult.inf = result[0];
        mult.sup = result[0];
        
        for (int i = 1; i < 4; i++) {
            if (result[i] < mult.inf) {
                mult.inf = result[i];
            } else if (result[i] > mult.sup){
                mult.sup = result[i];
            }
        }
        return mult;
    }
    
    /**
     * Divides two intervals
     * 
     * @param A Interval to make the division
     * @return Interval result
     */
    public Interval div(Interval A){ 
        if(A.inf > 0 || A.sup < 0){
            Interval x = new Interval(1/A.sup, 1/A.inf);
            Interval div = mult(x);
            return div;
        }else{
            throw new ArithmeticException("The interval contains zero number");
        }
    }
    
    /**
     * Potentiates an interval
     * 
     * @param power Double number to make the exponentiation 
     * @return Interval result
     */
    public Interval pow(double power){
        Interval pow = new Interval();
        if(this.inf > 0){
            pow.inf = Math.pow(this.inf, power);
            pow.sup = Math.pow(this.sup, power);
        }
        else if(this.sup < 0){
            pow.inf = Math.pow(this.sup, power);
            pow.sup = Math.pow(this.inf, power);
        }else{
            pow.inf = 0;
            double max = Math.abs(this.inf);
            if(this.sup > max)
                pow.sup = Math.abs(this.sup);
            else
                pow.sup = max;
        }
        return pow;
    }
    
    /**
     * Comparison if this interval is equals than interval A
     * 
     * @param A Interval to make the comparison if equals
     * @return 1.0 if this interval is equal than interval A and 0.0 if false
     */
    public double equals(Interval A){
        if(this.inf == A.inf && this.sup == A.sup)
            return 1.0;
        else
            return 0.0;
    }
    
    /**
     * Comparison if this interval is different of the interval A
     * 
     * @param A Interval to make the comparison if different
     * @return 1.0 if this interval is different than interval A and 0.0 if 
     * false
     */
    public double differ(Interval A){
        if(this.inf != A.inf && this.sup != A.sup)
            return 1.0;
        else
            return 0.0;
    }
    
    /**
     * Calculate the midpoint in this interval
     * 
     * @return Midpoint result
     */
    public double midpoint(){
        return (this.inf + this.sup)/2;
    }
    
    /**
     * Calculate the width in this interval
     * 
     * @return Width result
     */
    public double width(){
        return this.sup - this.inf;
    }
    
    /**
     * Calculate the magnitude in an interval
     * 
     * @return Magnitude result
     */
    public double magnitude(){
        return Math.max(Math.abs(inf), Math.abs(sup));
    }
    
    /**
     * Calculate the mignitude in this interval
     * 
     * @return Mignitude result
     */
    public double mignitude(){
        return Math.min(Math.abs(inf), Math.abs(sup));
    }
    
    /**
     * Calculate the absolute value in an interval
     * 
     * @return Absolute value result
     */
    public double abs(){
        return magnitude() - mignitude();
    }
    
    public void clone(Interval A){
        A.setInf(inf);
        A.setSup(sup);
    }
}
