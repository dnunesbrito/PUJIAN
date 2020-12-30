/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ufop.jprofil;

/**
 *
 * @author Darlan Nunes de Brito
 * @author Jônata Lucas Nogueira
 */
public class Pair {
    int start;
    int end;
    Pair(int s,int e){
        this.start = s;
        this.end = e;
    }
    Pair(){

    };
    static Pair push(int s,int e){
        Pair tmp = new Pair(s,e);
        return tmp;
    }

    @Override
    public String toString(){
        return "[" + this.start + "," + this.end + "]";
    }
}
