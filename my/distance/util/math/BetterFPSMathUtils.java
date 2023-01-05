package my.distance.util.math;

import me.guichaguri.betterfps.BetterFpsConfig;
import me.guichaguri.betterfps.BetterFpsHelper;
import me.guichaguri.betterfps.math.*;

public class BetterFPSMathUtils {
    public static float sin(float rad){
        float result;
        switch (BetterFpsHelper.helpers.get(BetterFpsConfig.getConfig().algorithm)) {
            case "rivens": {
                result = RivensMath.sin(rad);
                break;
            }
            case "taylors": {
                result = TaylorMath.sin(rad);
                break;
            }
            case "libgdx": {
                result = LibGDXMath.sin(rad);
                break;
            }
            case "rivens-full": {
                result = RivensFullMath.sin(rad);
                break;
            }
            case "rivens-half": {
                result = RivensHalfMath.sin(rad);
                break;
            }
            case "java": {
                result = JavaMath.sin(rad);
                break;
            }
            case "random": {
                result = RandomMath.sin(rad);
                break;
            }
            case "distance":{
                result = DistanceMath.sin(rad);
                break;
            }
            case "vanilla":
            default:{
                result = VanillaMath.sin(rad);
                break;
            }
        }
        return result;
    }
    public static float cos(float rad){
        float result;
        switch (BetterFpsHelper.helpers.get(BetterFpsConfig.getConfig().algorithm)) {
            case "rivens": {
                result = RivensMath.cos(rad);
                break;
            }
            case "taylors": {
                result = TaylorMath.cos(rad);
                break;
            }
            case "libgdx": {
                result = LibGDXMath.cos(rad);
                break;
            }
            case "rivens-full": {
                result = RivensFullMath.cos(rad);
                break;
            }
            case "rivens-half": {
                result = RivensHalfMath.cos(rad);
                break;
            }
            case "java": {
                result = (float) Math.cos(rad);
                break;
            }
            case "random": {
                result = RandomMath.cos(rad);
                break;
            }
            case "distance":{
                result = DistanceMath.cos(rad);
                break;
            }
            case "vanilla":
            default:{
                result = VanillaMath.cos(rad);
                break;
            }
        }
        return result;
    }
}
