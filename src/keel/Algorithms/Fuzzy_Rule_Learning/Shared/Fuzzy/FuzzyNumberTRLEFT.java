/** 
* <p> 
* @author Written by Luciano S�nchez (University of Oviedo) 20/01/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy;


public class FuzzyNumberTRLEFT extends Fuzzy {
	/** 
	* <p> 
	* Represents a right-angled triangular fuzzy number with the right-angle in left side. 
	* Each support member lower than center has the membership value of 1.0 and right represent the extreme of the triangular fuzzy number.
	* 
	* </p>
	*/	
	//the center and the extreme of the triangular fuzzy number
    double center, right;
    /** 
     * <p> 
     * A constructor for a left right-angled triangle fuzzy number, given the extremes. 
     * 
     * </p> 
     * @param c the center element of the triangular fuzzy number.
     * @param r the upper extreme of the triangular fuzzy number.
     */
    public FuzzyNumberTRLEFT(double c, double r) {
         center=c; right=r;
    }
    /** 
     * <p> 
     * A copy constructor for a left right-angled triangle fuzzy number, given other left right-angled triangle fuzzy number. 
     * 
     * </p> 
     * @param b to be copied.    
     */
    public FuzzyNumberTRLEFT(FuzzyNumberTRLEFT b) {
         center=b.center; right=b.right;
    }
    /** 
     * <p> 
     * Copies the FuzzyNumberTRLEFT parameter over the present instance. 
     * 
     * </p> 
     * @param pa a FuzzyNumberTRLEFT object to be copied
     */
    public void Asigna(FuzzyNumberTRLEFT b) {
        center=b.center; right=b.right;
    }
    /** 
     * <p> 
     *  Indicates whether some other object is "equal to" this one.
     * 
     * </p>
     * @param B the reference object with which to compare. 
     * @return true if this object is the same as the B argument; false otherwise. 
     */     
    public boolean equals(Fuzzy b) {
        if (!(b instanceof FuzzyNumberTRLEFT)) return false;
        FuzzyNumberTRLEFT bt=(FuzzyNumberTRLEFT) b;
        if (center!=bt.center) return false;
        if (right!=bt.right) return false;
        return true;
    }
    /** 
     * <p> 
     * Creates and returns a copy of this object.
     * 
     * </p>
     * @return a clone of this instance. 
     */    
    public Fuzzy clone() {
        return new FuzzyNumberTRLEFT(this);
    }
    /** 
     * <p> 
     *  Returns the membership level for the individual x.
     *  
     * 
     * </p>
     * @param x the individual which membership is to be calculated. 
     * @return the membership level for individual x. 
     */ 
    public double evaluateMembership(double x) {
        if (x<center) return 1;
        if (x<right) return (right-x)/(right-center);
        return 0;
    }
    /** 
     * <p> 
     *  Returns the centroid of the present fuzzy number. 	
     *  
     * </p>
     * @return the centroid of the present fuzzy number. 
     */  
    public double massCentre() {
        return center;
    }
    /** 
     * <p> 
     *  Creates and returns a FuzzyInterval with the extremes of the support set.
     *  
     * </p> 
     * @return an interval with the extremes of the support set. 
     */ 
    public FuzzyInterval support() {
        return new FuzzyInterval(NEGATIVEINF,right);
    }   
    /** 
     * <p> 
     *  Returns a printable version of the instance.   	
     *
     * </p>
     
     * @return a String with a printable version of the left right-angled triangular fuzzy number. 
     */	
    public String aString() {
        return "TRAPLE("+center+","+right+")";
    }
    
}
