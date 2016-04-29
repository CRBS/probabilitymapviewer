/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.slice;

/**
 * Contains information about Slice image
 * attributes
 * @author churas
 */
public class SliceIntensityDistribution {
    
    private double _minIntensity;
    private double _maxIntensity;
    private double _standardDeviation;
    private double _meanIntensity;
  

    public double getMinIntensity() {
        return _minIntensity;
    }

    public void setMinIntensity(double _minIntensity) {
        this._minIntensity = _minIntensity;
    }

    public double getMaxIntensity() {
        return _maxIntensity;
    }

    public void setMaxIntensity(double _maxIntensity) {
        this._maxIntensity = _maxIntensity;
    }

    public double getStandardDeviation() {
        return _standardDeviation;
    }

    public void setStandardDeviation(double _standardDeviation) {
        this._standardDeviation = _standardDeviation;
    }

    public double getMeanIntensity() {
        return _meanIntensity;
    }

    public void setMeanIntensity(double _meanIntensity) {
        this._meanIntensity = _meanIntensity;
    }    
 
    public String getScalingLimitsAsString(){
        double newMin = this._meanIntensity-(2*this._standardDeviation);
        double newMax = this._meanIntensity+(2*this._standardDeviation);
        if (newMax < newMin){
            return Long.toString(Math.round(newMax))+","
                    +Long.toString(Math.round(newMin));
        }
            return Long.toString(Math.round(newMin))+","
                    +Long.toString(Math.round(newMax));
    }
}
