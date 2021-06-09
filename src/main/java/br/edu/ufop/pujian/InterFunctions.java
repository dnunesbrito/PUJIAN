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
    
    private static final Interval PlusOne = new Interval(1.0);
    private static final Interval Zero = new Interval(0.0);
    private static final Interval Ln10 = new Interval(2.30258509299404568402);
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

    /**
     * Returns an interval of the absolute value.
     * 
     * @param A Operand to get absolute value.
     * @return {@link Interval} The absolute value
     */
    public static Interval IAbs(Interval A){
	if (A.getInf() > 0.0) return A;
	else if (A.getSup() < 0.0) return A.INeg();
	else {
            return new Interval(0.0,A.RAbs());
	}        
    }

    /**
     * Gives the square of an interval
     * @return {@link Interval} The square of an interval
     */
    public static Interval ISqr (Interval A)
    /**********************************************************************
     *  R = X^2
     */
    {
        Interval t1 = IAbs(A);
        return t1.mult(t1);
    }

    /**
     * Get the power of an interval by a number
     * @param A Interval to be powered.
     * @param n Exponent to power
     * @return {@link Interval} The result of operation
     */
    public static Interval IPowerN(Interval A,int n)
    /**********************************************************************
     *  R = X^n
     */
    {
            int i, absn;
            Interval y, z, xsqr;
            y = new Interval();
            z = new Interval();

            absn = (n < 0) ? (-n) : n;
            if (absn >= 2) {
                    xsqr = ISqr(A);
                    if (absn%2 != 0) y= xsqr.mult(A);
                    else xsqr.clone(y);
                    for (i = 3; i < absn; i += 2) {
                            y.clone(z);
                            y = z.mult(xsqr);
                    }
            }
            else if (absn%2 != 0) y = A;
            else PlusOne.clone(y);
            if (n < 0) y = PlusOne.div(y);
            return y;
    }


    /*
    VOID BiasLog (BIASINTERVAL * const pR,
							const BIASINTERVAL * const pX)
    /**********************************************************************
     *  R = Ln (X)
     *
    {
            REAL y_inf, y_sup;

            if (BiasInf (pX) <= 0.0)
                    _BiasError ("Log argument out of range"); /* BiasHullR (pR, & BiasNaN); *
            else {
                    y_inf = log (BiasInf (pX));
                    y_sup = log (BiasSup (pX));
                    y_inf = RoundDown (y_inf);
                    y_sup = RoundUp   (y_sup);
                    BiasHullRR (pR, & y_inf, & y_sup);
            }
    }
    */
    
    /**
     * Get the natural logarithm of an Interval
     * @param A {@link Interval} Interval to get log
     * @return {@link Interval} with the result of logarithm function
     */
    public static Interval Log(Interval A){
        if (A.getInf() <= 0)
            throw new ArithmeticException("Log argument out of range");
        else
            return new Interval(Math.log(A.getInf()),Math.log(A.getSup()));
    }
 
    /*    VOID BiasLog10 (BIASINTERVAL * const pR,
                                                                    const BIASINTERVAL * const pX)
    /**********************************************************************
     *  R = Log10 (X)
     *
    {
            BIASINTERVAL t1;

            if (BiasInf (pX) <= 0.0)
                    _BiasError ("Log10 argument out of range"); /* BiasHullR (pR, & BiasNaN); *
            else {
                    BiasLog (& t1, pX);
                    BiasDivII (pR, & t1, & BiasLn10Incl);
            }
    }
    */
    /**
     * Get the logarithm with an Interval in the base 10
     * @param A {@link Interval} Interval to get Log10
     * @return {@link Interval} with result
     */
    public static Interval Log10(Interval A){
        Interval result;
        if (A.getInf() <= 0.0)
            throw new ArithmeticException("Log10 argument out of range");
        else{
            result = Log(A);
            result = result.div(A);
        }
        return result;
    }
    /*VOID BiasExp (BIASINTERVAL * const pR,
                                                            const BIASINTERVAL * const pX)
    /**********************************************************************
     *  R = e^X
     *
    {
            REAL y_inf, y_sup;

            y_inf = exp (BiasInf (pX));
            y_sup = exp (BiasSup (pX));
            y_inf = RoundDown (y_inf);
            y_sup = RoundUp   (y_sup);
            if (y_inf < 0.0) y_inf = 0.0;
            BiasHullRR (pR, & y_inf, & y_sup);
    }*/
    /**
     * Get e^I
     * @param A {@link Interval} of the exponent
     * @return {@link Interval} with the result of the operation
     */
    public static Interval Exp(Interval A){
        Interval result = new Interval(Math.exp(A.getInf()),Math.exp(A.getSup()));
        if (result.getInf() < 0.0) result.set(0.0, result.getSup());
        return result;
    }
    public static Interval IPowerI (Interval pX,Interval pY)
    /**********************************************************************
     *  R = X^Y
     */
    {
      double x_inf = pX.getInf();
      double x_sup;
      Interval t1, t2 = new Interval();
      Interval result = null;

        if (x_inf < 0.0) throw new ArithmeticException("Power: Base is negative");
        if (x_inf == 0.0) {
            if (pY.getInf() <= 0.0) {
                throw new ArithmeticException("Power: Negative or zero exponent with zero base");
            } else if ((x_sup = pX.getSup()) == 0.0) {
                return Zero;
            } else {
                t2.set(x_sup, x_sup);
                t1 = Log (t2);
            }
        }else t1 = Log(pX);
        t2 = t1.mult(pY);//BiasMulII (& t2, & t1, pY);
        if (x_inf == 0.0) {
                t1 = Exp (t2);
                result = t1.Hull(Zero);//BiasHullRI (pR, & Zero, & t1);
        }
        else result = Exp(t2);//BiasExp (pR, & t2);
        return result;
            
    }

}
