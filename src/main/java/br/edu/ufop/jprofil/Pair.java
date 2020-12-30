/*
 * Copyright (C) 2020 darlan
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
