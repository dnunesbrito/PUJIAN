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
public class InterFunctions extends Interval{
    
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
	}else if(x_inf > 2*Math.PI){
            x_inf -= 2*Math.PI;
        }
        double x_sup = A.getSup();
        if (x_sup > 2*Math.PI) {
		x_sup -= 2*Math.PI;
	}
        if (x_inf < 0.0)
            throw new ArithmeticException("ScaleTo2Pi (): inf < 0");
	if (x_sup > 2*Math.PI)
            throw new ArithmeticException("ScaleTo2Pi (): sup > 2Pi");

        B.set(x_inf,x_sup);
        return B;
    }
    
    /**
     * Function used to compute the sine of an interval angle
     * @param angle Interval angle to compute de sine.
     * @return The sine of the interval angle.
     * @throws ArithmeticException 
     */
    public static Interval Sin(Interval angle) throws ArithmeticException{
        Interval cosResult = new Interval(Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
        if (angle.width() > 2*Math.PI)
            return cosResult;
	/* Reduction of x to [0, 2Pi] */
	angle = ScaleTo2Pi (angle);
	if (angle.getSup() >= angle.getInf() + 2*Math.PI) { /* security */
            cosResult.set(-1.0,1.0);
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
	q_inf = Quadrant (angle.getInf());
	q_sup = Quadrant (angle.getSup());


	if ((q_inf == q_sup) && (angle.getSup() > angle.getInf() + Math.PI)) {
		cosResult.set(-1.0,1.0);
		return cosResult;
	}
	switch ((q_sup << 2) + q_inf)
	{
	case 0:
	case 3:
	case 15:
            cosResult.set(Math.sin(angle.getInf()),Math.sin(angle.getSup()));//x_sup = Sin (x_sup);
            return cosResult;
	case 1:
	case 14:
            cosResult.set(-1.0,Math.max(Math.sin(angle.getInf()), Math.sin(angle.getSup())));
            return cosResult;
	case 2:
		/*y_inf = -1.0;
		x_sup = Sin (x_sup);
		y_sup = RoundUp (x_sup);
		break;*/
            cosResult.set(-1.0,Math.sin(angle.getSup()));
            return cosResult;
	case 4:
	case 11:
		/*y_sup = 1.0;
		x_inf = Sin (x_inf);
		x_sup = Sin (x_sup);
		x_inf = RoundDown (x_inf);
		x_sup = RoundDown (x_sup);
		y_inf = Minimum(x_inf, x_sup);
		break;*/
            cosResult.set(1.0,Math.min(Math.sin(angle.getInf()), Math.sin(angle.getSup())));
            return cosResult;
	case 5:
	case 9:
	case 10:
		/*x_inf = Sin (x_inf);
		x_sup = Sin (x_sup);
		y_inf = RoundDown (x_sup);
		y_sup = RoundUp (x_inf);
		break;*/
            cosResult.set(Math.sin(angle.getInf()),Math.sin(angle.getSup()));
            return cosResult;
	case 6:
	case 12:
		//y_inf = -1.0; y_sup = 1.0; break;
            cosResult.set(-1.0,1.0);
            return cosResult;
	case 7:
		/*y_sup = 1.0;
		x_inf = Sin (x_inf);
		y_inf = RoundDown (x_inf);
		break;*/
            cosResult.set(1.0,Math.sin(angle.getInf()));
            return cosResult;
	case 8:
		/*y_sup = 1.0;
		x_sup = Sin (x_sup);
		y_inf = RoundDown (x_sup);
		break;*/
            cosResult.set(1.0,Math.sin(angle.getSup()));
            return cosResult;
	case 13:
		/*y_inf = -1.0;
		x_inf = Sin (x_inf);
		y_sup = RoundUp (x_inf);
		break;*/
            cosResult.set(-1.0,Math.sin(angle.getInf()));
            return cosResult;
	}
        
        cosResult.set(Math.sin(angle.getInf()),Math.sin(angle.getSup()));
        return cosResult;
    }
    /**
     * Function used to compute the cossine of an interval angle
     * @param angle Interval angle to compute de sine.
     * @return The sine of the interval angle.
     * @throws ArithmeticException 
     */
    public static Interval Cos(Interval angle) throws ArithmeticException{
        Interval angle_added_pi_2;
        angle_added_pi_2 = new Interval(Math.PI/2);
        angle_added_pi_2 = angle_added_pi_2.add(angle);
        return Sin(angle_added_pi_2);
    }
    /**
     * Function used to compute the cossine of an interval angle
     * @param angle Interval angle to compute de sine.
     * @return The sine of the interval angle.
     * @throws ArithmeticException 
     */
    public static Interval Tan(Interval angle) throws ArithmeticException{
	/*Double x_inf, x_sup;
	Double y_inf, y_sup;
	Integer q_inf, q_sup;

	if (BiasDiam (pX) >= BiasTwoPi) {
		BiasHullRR (pR, & BiasNegInf, & BiasPosInf);
		return;
	}
	/* Reduction of x to [0, 2Pi] 
	ScaleTo2Pi(& x_inf, & x_sup, pX);
	if (x_sup >= x_inf + BiasTwoPi) { /* security 
		BiasHullRR (pR, & BiasNegInf, & BiasPosInf);
		return;
	}
	/* x_inf, x_sup are now in the range [0, 2Pi]
	 * Quadrants:
	 *  0 = [0,Pi/2]
	 *  1 = [Pi/2,Pi]
	 *  2 = [Pi,3Pi/2]
	 *  3 = [3Pi/2,2Pi]
	 *
	q_inf = Quadrant (x_inf);
	q_sup = Quadrant (x_sup);

	if ((q_inf == q_sup) && (x_sup > x_inf + BiasPi)) {
		BiasHullRR (pR, & BiasNegInf, & BiasPosInf);
		return;
	}
        */
        
        Interval tanResult = new Interval(Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
        if (angle.width() > 2*Math.PI)
            return tanResult;
	/* Reduction of x to [0, 2Pi] */
	angle = ScaleTo2Pi (angle);
	if (angle.getSup() >= angle.getInf() + 2*Math.PI) { /* security */
            tanResult.set(-1.0,1.0);
            return tanResult;
	}

        int q_inf, q_sup;
	/* x_inf, x_sup are now in the range [0, 2Pi]
	 * Quadrants:
	 *  0 = [0,Pi/2]
	 *  1 = [Pi/2,Pi]
	 *  2 = [Pi,3Pi/2]
	 *  3 = [3Pi/2,2Pi]
	 */
	q_inf = Quadrant (angle.getInf());
	q_sup = Quadrant (angle.getSup());

	if ((q_inf == q_sup) && (angle.getSup() > angle.getInf() + Math.PI)) {
		tanResult.set(-1.0,1.0);
		return tanResult;
	}
	switch ((q_sup << 2) + q_inf)
	{
            case 0:
            case 3:
            case 5:
            case 9:
            case 10:
            case 15:
                    /*x_inf = Tan (x_inf);
                    x_sup = Tan (x_sup);
                    y_inf = RoundDown (x_inf);
                    y_sup = RoundUp   (x_sup);*/
                tanResult.set(Math.tan(angle.getInf()),Math.tan(angle.getSup()));
                return tanResult;
            default:
                return tanResult;
	}
    }
    /**
     * Function used to compute the cotangent of an interval value
     * @param tanvalue Interval with the value to compute cotangent.
     * @return The cotangent of the interval angle.
     * @throws ArithmeticException 
     */
    public static Interval Cot(Interval tanvalue) throws ArithmeticException{
        Interval angle;
        tanvalue.add(Math.PI/2);
        angle = Tan(tanvalue);
        angle.INeg();
        return angle;
    }
    /**
     * Function used to compute the cotangent of an interval value
     * @param tanvalue Interval with the value to compute cotangent.
     * @return The cotangent of the interval angle.
     * @throws ArithmeticException 
     */
    public static Interval Asin(Interval tanvalue) throws ArithmeticException{
	/*REAL x_inf, x_sup;

	x_inf = BiasInf (pX); x_sup = BiasSup (pX);
	if ((x_inf < -1.0) || (x_sup > 1.0))
		_BiasError ("ArcSin argument out of range");
	x_inf = Asin (x_inf);
	x_sup = Asin (x_sup);
	x_inf = RoundDown (x_inf);
	x_sup = RoundUp   (x_sup);
	BiasHullRR (pR, & x_inf, & x_sup);*/
        Interval angle = new Interval();
        if(tanvalue.getInf() < -1.0 || tanvalue.getSup() > 1.0)
            throw new ArithmeticException("ArcSin argument out of range");
        angle.set(Math.asin(tanvalue.getInf()),Math.asin(tanvalue.getSup()));
        return angle;
    }
}
