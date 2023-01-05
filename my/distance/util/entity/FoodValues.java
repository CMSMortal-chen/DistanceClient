package my.distance.util.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

/**
 * FoodValues is a utility class used to retrieve and hold food values.
 *
 * To get food values for any given food, use any of the static {@link #get} methods.
 *
 * <pre>
 * {@code
 * FoodValues appleFoodValues = FoodValues.get(new ItemStack(Items.apple));
 * }
 * </pre>
 */
public class FoodValues
{
    public static float realFoodExhaustionLevel;
    public final int hunger;
    public final float saturationModifier;

    public FoodValues(int hunger, float saturationModifier)
    {
        this.hunger = hunger;
        this.saturationModifier = saturationModifier;
    }

    public FoodValues(FoodValues other)
    {
        this(other.hunger, other.saturationModifier);
    }

    /**
     * @return The amount of saturation that the food values would provide.
     */
    public float getSaturationIncrement()
    {
        return Math.min(20, hunger * saturationModifier * 2f);
    }

    /**
     * See {}
     */
    public static FoodValues getUnmodified(ItemStack itemStack)
    {
        return getUnmodifiedFoodValues(itemStack);
    }

    /**
     * See {}
     */
    public static FoodValues get(ItemStack itemStack)
    {
        return getFoodValues(itemStack);
    }

    /**
     * See {}
     */
    public static FoodValues get(ItemStack itemStack, EntityPlayer player)
    {
        return getFoodValuesForPlayer(itemStack, player);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + hunger;
        result = prime * result + Float.floatToIntBits(saturationModifier);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FoodValues other = (FoodValues) obj;
        if (hunger != other.hunger)
            return false;
        return Float.floatToIntBits(saturationModifier) == Float.floatToIntBits(other.saturationModifier);
    }

    public static FoodValues getUnmodifiedFoodValues(ItemStack food)
    {
        if (food != null && food.getItem() != null)
        {
            if (food.getItem() instanceof ItemFood)
                return getItemFoodValues((ItemFood) food.getItem(), food);
        }
        return null;
    }

    private static FoodValues getItemFoodValues(ItemFood itemFood, ItemStack itemStack)
    {
        return new FoodValues(itemFood.getHealAmount(itemStack), itemFood.getSaturationModifier(itemStack));
    }

    public static FoodValues getFoodValuesForPlayer(ItemStack food, EntityPlayer player)
    {
        return getFoodValues(food);
    }
    public static FoodValues getFoodValues(ItemStack food)
    {
        return getUnmodifiedFoodValues(food);
    }
}
