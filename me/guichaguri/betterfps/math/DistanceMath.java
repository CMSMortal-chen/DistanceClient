package me.guichaguri.betterfps.math;

/**
 * 不知从哪里抄来的效率比JavaMath好
 *  ~14ms
 *
 * @author Unknow
 */
public class DistanceMath {
    private static final float BF_radFull = 6.2831855f;
    private static final int BF_SIN_BITS = 12;
    private static final int BF_SIN_MASK = ~(-1 << BF_SIN_BITS);
    private static final float BF_SIN_TO_COS = 1.5707964f;
    private static final int BF_SIN_MASK2 = BF_SIN_MASK >> 1;
    private static final int BF_SIN_COUNT = BF_SIN_MASK + 1;
    private static final int BF_SIN_COUNT2 = BF_SIN_MASK2 + 1;
    private static final float BF_radToIndex = BF_SIN_COUNT / BF_radFull;
    private static final float[] BF_sinHalf = new float[BF_SIN_COUNT2];
    static {
        for (int i = 0; i < BF_SIN_COUNT2; ++i) {
            BF_sinHalf[i] = (float)Math.sin((i + Math.min(1, i % (BF_SIN_COUNT / 4)) * 0.5) / (double)BF_SIN_COUNT * (double)BF_radFull);
        }
    }
    /**
     * sin looked up in a table
     */
    public static float sin(float p_76126_0_)
    {
        int index1 = (int)(p_76126_0_ * BF_radToIndex) & BF_SIN_MASK;
        int index2 = index1 & BF_SIN_MASK2;
        int mul = index1 == index2 ? 1 : -1;
        return BF_sinHalf[index2] * (float)mul;
    }

    /**
     * cos looked up in the sin table with the appropriate offset
     */
    public static float cos(float value)
    {
        return sin(value + BF_SIN_TO_COS);
    }
}
