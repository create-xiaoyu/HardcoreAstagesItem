package com.liquor.hardcoreastagesitem;

import com.alessandro.astages.core.ARestrictionManager;
import com.alessandro.astages.core.server.restriction.item.ABaseItemRestriction;
import com.alessandro.astages.core.server.restriction.item.AItemModRestriction;
import com.alessandro.astages.core.server.restriction.item.AItemRestriction;
import com.alessandro.astages.core.server.restriction.item.AItemTagRestriction;
import com.alessandro.astages.integration.jei.AItemStagesJEIPlugin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class GetUnknownItemList {
    public static List<ItemStack> getItemsForStage(String stage) {
        // 从JEI插件的缓存中获取指定阶段的物品列表
        // 注意：此方法依赖JEI插件的缓存机制，需确保JEI已加载且缓存已构建
        //List<ItemStack> items = AItemStagesJEIPlugin.ITEM_CACHE.get(stage);
        try {
            Field ItemList = AItemStagesJEIPlugin.class.getDeclaredField("ITEM_CACHE");
            ItemList.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, List<ItemStack>> itemCache = (Map<String, List<ItemStack>>) ItemList.get(null);
            HardcoreAstagesItem.LOGGER.debug(itemCache.get(stage).toString());

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Replaced Failed");
        }
        //return items != null ? items : List.of();
        return new ArrayList<>();
    }
}
