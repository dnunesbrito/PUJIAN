/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
       
package br.edu.ufop.pujian;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Darlan Nunes de Brito
 * @author Jonata Lucas Nogueira
 */
public class testpujian {
    public static void main(String[] args){
        Engine engine = new Engine();
        ParsePUJIAN parser = new ParsePUJIAN(engine);
        Engine.Node tree = null;
        try {
            tree = parser.ReadNodesFromFile("StringPrograma.txt");
            /*System.out.println("cos(3.5*PI/2): " + Math.cos(3.5*Math.PI/2));
            System.out.println("cos(3.5*PI/2); " + Math.cos(3.5*Math.PI/2));*/
            engine.printContextVariable("C");
            engine.printContextVariable("D");
        } catch (SemanticError ex) {
            Logger.getLogger(testpujian.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
/*        String s = "A{3.13,3.15};cos(A)";
        Engine.Inter A;
/*        try {
            tree = parser.parse(s);
        } catch (SyntaxError ex) {
            Logger.getLogger(testpujian.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(engine.context.vars);
        System.out.println("Tree: " + tree);
        try {
            A = (Engine.Inter) tree.eval();
            engine.printContext();
        } catch (SemanticError ex) {
            Logger.getLogger(testpujian.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*Interval B;
        /*B = new Interval(Math.PI/4,Math.PI/2);
        A = InterFunctions.cos(B);
        System.out.println(A);*/
    }
}
