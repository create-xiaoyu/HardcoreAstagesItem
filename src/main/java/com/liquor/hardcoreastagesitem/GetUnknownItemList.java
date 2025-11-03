package com.liquor.hardcoreastagesitem;

import com.alessandro.astages.capability.ClientPlayerStage;
import com.alessandro.astages.integration.jei.AItemStagesJEIPlugin;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.util.*;

public class GetUnknownItemList {
    public static List<String> getPreUnknownItemList() {
        List<String> itemNameList = new ArrayList<>();
        try {
            Field itemList = AItemStagesJEIPlugin.class.getDeclaredField("ITEM_CACHE");
            itemList.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, List<ItemStack>> itemCache = (Map<String, List<ItemStack>>) itemList.get(null);
            for (String tstage : itemCache.keySet()) {
                for(ItemStack itemStacks : itemCache.get(tstage)) {
                    itemNameList.add(itemStacks.getItem().toString());
                }
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Replaced Failed");
        }
        return itemNameList;
    }

    public static List<String> getUnknownItemList() {
        List<String> itemNameList = new ArrayList<>();
        try {
            Field itemList = AItemStagesJEIPlugin.class.getDeclaredField("ITEM_CACHE");
            itemList.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, List<ItemStack>> itemCache = (Map<String, List<ItemStack>>) itemList.get(null);
            for (String tstage : itemCache.keySet()) {
                if (ClientPlayerStage.hasStage(tstage)) {
                    continue;
                }
                for(ItemStack itemStacks : itemCache.get(tstage)) {
                    itemNameList.add(itemStacks.getItem().toString());
                }
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Replaced Failed");
        }
        return itemNameList;
    }
}
