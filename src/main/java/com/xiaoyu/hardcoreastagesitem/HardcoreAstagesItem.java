package com.xiaoyu.hardcoreastagesitem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod(HardcoreAstagesItem.MODID)
public class HardcoreAstagesItem {
    public static final String MODID = "hardcoreastagesitem";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Map<String, BakedModel> replacedMap = new HashMap<>();
    public boolean isExecuted = false;

    public HardcoreAstagesItem() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        UnknownItem.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(RebakeModel.class);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerEnterWorld);
    }

    @SubscribeEvent
    public void onPlayerEnterWorld(RenderGuiEvent.Post event) {
        if (!isExecuted) {
            isExecuted = true;
            Minecraft minecraft = Minecraft.getInstance();
            ModelManager modelManager = minecraft.getModelManager();

            ResourceLocation unknownItemResource = ResourceLocation.parse("hardcoreastagesitem:unknown_item");
            ModelResourceLocation unknownModel = new ModelResourceLocation(unknownItemResource, "inventory");
            BakedModel replaceModel = modelManager.getModel(unknownModel);

            if (ServerLifecycleHooks.getCurrentServer() != null) {
                for (ServerPlayer onlinePlayer : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                    List<Item> unknownItems = GetItemList.GetUnknownItemList(onlinePlayer);
                    LOGGER.debug("Get Unknown Item: {}", unknownItems);

                    for (Item item : unknownItems) {
                        ResourceLocation itemRegistryName = ForgeRegistries.ITEMS.getKey(item);
                        if (itemRegistryName != null) {
                            ModelResourceLocation originModel = new ModelResourceLocation(itemRegistryName, "inventory");
                            BakedModel rawModel = modelManager.getModel(originModel);

                            replacedMap.put(itemRegistryName.toString(), rawModel);
                            RebakeModel.replaceModel(originModel, replaceModel, modelManager);
                        }
                    }
                }
            }
            RebakeModel.reloadModel();
        }
    }
}