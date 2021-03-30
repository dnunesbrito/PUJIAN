/*
 * Copyright (C) 2021 darlan
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
 * This class is used to contains methods to make computaions with interval analysis functions
 * 
 * @author Darlan Nunes de Brito
 */
public class InterFunctions{
    
    /**
     * Determines which quadrant the angle is.
     * @param x Angle to be evaluated
     * @return 0, 1, 2, 3 which is what quadrant the angle is
     */
    private static int Quadrant (double x){
	/**********************************************************************
	 *  Calculation of the quadrant x is lying in (x in [0, 2Pi])
	 */
	if (x <= Math.PI/2) return 0;
	if (x <= Math.PI) return 1;
	if (x <= Math.PI + Math.PI/2) return 2;
	return 3;
    }

    /**
     * Scale the angle between 0 to two pi
     * @param A Interval angle to be scaled
     * @return The interval angled scaled from 0 to two pi.
     * @throws ArithmeticException 
     */
    private static Interval ScaleTo2Pi(Interval A) throws ArithmeticException{
        Interval B = new Interval(0.0,0.0);
        if (A.getInf() >= 0 & A.getSup() <= Math.PI/2){
            A.clone(B);
            return B;
        }
        double x_inf = A.getInf();
        if (x_inf < 0.0) {
		x_inf += 2*Math.PI;
	}
        double x_sup = A.getSup();
        if (x_sup > 2*Math.PI) {
		x_sup -= 2*Math.PI;
	}
        if (x_inf < 0.0)
            throw new ArithmeticException("ScaleTo2Pi (): inf < 0");
	if (x_sup > 2*Math.PI)
            throw new ArithmeticException("ScaleTo2Pi (): sup > 2Pi");

        B.setInf(x_inf);
        B.setSup(x_sup);
        return B;
    }
    
    /**
     * Function used to compute the sine of an interval angle
     * @param angle Interval angle to compute de sine.
     * @return The sine of the interval angle.
     * @throws ArithmeticException 
     */
    public static Interval sin(Interval angle) throws ArithmeticException{
        Interval cosResult = new Interval(0.0,0.0);
        if (angle.width() > 2*Math.PI)
            return cosResult;
	/* Reduction of x to [0, 2Pi] */
	cosResult = ScaleTo2Pi (angle);
	if (cosResult.getSup() >= cosResult.getInf() + 2*Math.PI) { /* security */
            cosResult.setInf(-1.0);
            cosResult.setSup(1.0);
            return cosResult;
	}
	/* x_inf, x_sup are now in the range [0, 2Pi]
	 * Quadrants:
	 *  0 = [0,Pi/2]
	 *  1 = [Pi/2,Pi]
	 *  2 = [Pi,3Pi/2]
	 *  3 = [3Pi/2,2Pi]
	 */
        int q_inf,q_sup;
	q_inf = Quadrant (cosResult.getInf());
	q_sup = Quadrant (cosResult.getSup());

	if ((q_inf == q_sup) && (cosResult.getSup() > cosResult.getInf() + Math.PI)) {
		cosResult.setInf(-1.0);
                cosResult.setSup(1.0);
		return cosResult;
	}
	switch ((q_sup << 2) + q_inf)
	{
	case 0:
	case 3:
	case 15:
		cosResult.setInf(Math.sin(angle.getInf()));
		cosResult.setSup(Math.sin(angle.getSup()));//x_sup = sin (x_sup);
		return cosResult;
	case 1:
	case 14:
		cosResult.setInf(-1.0);//y_inf = -1.0;
                cosResult.setSup(Math.max(Math.sin(angle.getInf()), Math.sin(angle.getSup())));
		return cosResult;
	case 2:
		/*y_inf = -1.0;
		x_sup = sin (x_sup);
		y_sup = RoundUp (x_sup);
		break;*/
            cosResult.setInf(-1.0);
            cosResult.setSup(Math.sin(angle.getSup()));
            return cosResult;
	case 4:
	case 11:
		/*y_sup = 1.0;
		x_inf = sin (x_inf);
		x_sup = sin (x_sup);
		x_inf = RoundDown (x_inf);
		x_sup = RoundDown (x_sup);
		y_inf = Minimum(x_inf, x_sup);
		break;*/
            cosResult.setSup(1.0);
            cosResult.setInf(Math.min(Math.sin(angle.getInf()), Math.sin(angle.getSup())));
            return cosResult;
	case 5:
	case 9:
	case 10:
		/*x_inf = sin (x_inf);
		x_sup = sin (x_sup);
		y_inf = RoundDown (x_sup);
		y_sup = RoundUp (x_inf);
		break;*/
            cosResult.setInf(Math.sin(angle.getInf()));
            cosResult.setSup(Math.sin(angle.getSup()));
            return cosResult;
	case 6:
	case 12:
		//y_inf = -1.0; y_sup = 1.0; break;
            cosResult.setInf(-1.0);
            cosResult.setSup(1.0);
            return cosResult;
	case 7:
		/*y_sup = 1.0;
		x_inf = sin (x_inf);
		y_inf = RoundDown (x_inf);
		break;*/
            cosResult.setSup(1.0);
            cosResult.setInf(Math.sin(angle.getInf()));
            return cosResult;
	case 8:
		/*y_sup = 1.0;
		x_sup = sin (x_sup);
		y_inf = RoundDown (x_sup);
		break;*/
            cosResult.setSup(1.0);
            cosResult.setInf(Math.sin(angle.getSup()));
            return cosResult;
	case 13:
		/*y_inf = -1.0;
		x_inf = sin (x_inf);
		y_sup = RoundUp (x_inf);
		break;*/
            cosResult.setInf(-1.0);
            cosResult.setSup(Math.sin(angle.getInf()));
            return cosResult;
	}
        
        cosResult.setInf(Math.sin(angle.getInf()));
        cosResult.setSup(Math.sin(angle.getSup()));
        return cosResult;
    }
}
