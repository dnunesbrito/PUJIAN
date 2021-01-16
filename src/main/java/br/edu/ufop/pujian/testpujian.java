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
        String s = "A{(2.0+10),3.0}+B{4.0,5.0}";
        Engine.Inter A;
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
