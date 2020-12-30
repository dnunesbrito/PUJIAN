/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
       
package br.edu.ufop.jprofil;


/**
 *
 * @author Darlan Nunes de Brito
 * @author Jonata Lucas Nogueira
 */
public class testjprofil {
    public static void main(String[] args) {
        Interval A = new Interval(1.0,2.0,"A");
        Interval B = new Interval(1.0,3.0,"B");
        Interval.eval("+");
        Interval.printInstances();
        String str = "A+(2*B/(4+2)*(3*8))";
        
        try{
            Interval.eval(str);
            System.out.println("Balanced "); 
        } catch (ArithmeticException e){
            System.out.println("Not Balanced ");
        }
    }
}
