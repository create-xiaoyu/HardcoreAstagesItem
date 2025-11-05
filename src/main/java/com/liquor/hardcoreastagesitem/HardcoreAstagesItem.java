package com.liquor.hardcoreastagesitem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod(HardcoreAstagesItem.MODID)
public class HardcoreAstagesItem {
    public static final String MODID = "hardcoreastagesitem";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static Map<String, BakedModel> replacedMap = new HashMap<>();

    public static boolean isExecuted = false;

    public HardcoreAstagesItem(IEventBus modEventBus) {

        UnknownItem.register(modEventBus);

        NeoForge.EVENT_BUS.register(RebakeModel.class);
        NeoForge.EVENT_BUS.addListener(this::onPlayerEnterWorld);

    }

    @SubscribeEvent
    private void onPlayerEnterWorld(RenderGuiEvent.Post event) {
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

                    for (Item itemName : unknownItems) {
                        ResourceLocation originResource = ResourceLocation.parse(String.valueOf(itemName));
                        ModelResourceLocation originModel = new ModelResourceLocation(originResource, "inventory");
                        BakedModel rawModel = modelManager.getModel(originModel);

                        replacedMap.put(String.valueOf(itemName), rawModel);

                        RebakeModel.replaceModel(originModel, replaceModel, modelManager);
                    }
                }
            }
            RebakeModel.reloadModel();
        }
    }
}
