/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
       
package br.edu.ufop.pujian;

import anasy.parser.SyntaxError;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Darlan Nunes de Brito
 * @author Jonata Lucas Nogueira
 */
public class testpujian {
    public static void main(String[] args) {
        Engine engine = new Engine();
        ParsePUJIAN parser = new ParsePUJIAN(engine);
        String s = "I{1,2}+B";
        Engine.Inter A,B;
        A = engine.new Inter(2,3);
        B = engine.new Inter(4,5);
        engine.push("B", B);
        engine.push("A", A);
        Engine.Node tree = engine.new Node();
        try {
            tree = parser.parse(s);
        } catch (SyntaxError ex) {
            Logger.getLogger(testpujian.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Tree: " + tree);
        try {
            A = (Engine.Inter) tree.eval();
            System.out.println(A);
        } catch (SemanticError ex) {
            Logger.getLogger(testpujian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
