/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.MetalEnum;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.util.IMetalObject;
import net.dries007.tfc.util.OreDictionaryHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemOreTFC extends ItemTFC implements IMetalObject
{
    private static final Map<Ore, ItemOreTFC> MAP = new HashMap<>();

    public static ItemOreTFC get(Ore ore)
    {
        return MAP.get(ore);
    }

    public static ItemStack get(Ore ore, Ore.Grade grade, int amount)
    {
        return new ItemStack(MAP.get(ore), amount, ore.graded ? grade.getMeta() : 0);
    }

    public static ItemStack get(Ore ore, int amount)
    {
        return new ItemStack(MAP.get(ore), amount);
    }

    public final Ore ore;

    public ItemOreTFC(Ore ore)
    {
        this.ore = ore;
        if (MAP.put(ore, this) != null) throw new IllegalStateException("There can only be one.");
        setMaxDamage(0);
        if (ore.metal != null)
        {
            setHasSubtypes(true);
            OreDictionaryHelper.register(this, "ore");
            OreDictionaryHelper.register(this, "ore", ore);
            for (Ore.Grade grade : Ore.Grade.values())
            {
                OreDictionaryHelper.registerMeta(this, grade.getMeta(), "ore", grade);
                OreDictionaryHelper.registerMeta(this, grade.getMeta(), "ore", ore, grade);
            }
        }
        else // Mineral
        {
            OreDictionaryHelper.register(this, "gem", ore);
            switch (ore.name())
            {
                case "lapis_lazuli":
                    OreDictionaryHelper.register(this, "gem", "lapis");
                    break;
                case "bituminous_coal":
                    OreDictionaryHelper.register(this, "gem", "coal");
                    break;
            }
        }
    }

    public Ore.Grade getGradeFromStack(ItemStack stack)
    {
        return Ore.Grade.byMetadata(stack.getItemDamage());
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        Ore.Grade grade = getGradeFromStack(stack);
        if (grade == Ore.Grade.NORMAL) return super.getTranslationKey(stack);
        return super.getTranslationKey(stack) + "." + grade.getName();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (!isInCreativeTab(tab)) return;
        if (ore.graded)
            for (Ore.Grade grade : Ore.Grade.values())
                items.add(new ItemStack(this, 1, grade.getMeta()));
        else
            items.add(new ItemStack(this));
    }

    @Override
    public MetalEnum getMetal(ItemStack stack)
    {
        return ore.metal;
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        return getGradeFromStack(stack).smeltAmount;
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.SMALL;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.HEAVY;
    }
}
