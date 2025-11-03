package com.liquor.hardcoreastagesitem;

import com.liquor.hardcoreastagesitem.Items.UnknownItem;
import com.liquor.hardcoreastagesitem.commands.RebakeCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import java.lang.reflect.Field;
import java.util.*;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(HardcoreAstagesItem.MODID)
public class HardcoreAstagesItem {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "hardcoreastagesitem";

    private static boolean isExecuted = false;

    public static Map<String, BakedModel> replacedMap = new HashMap<>();

    private static List<String> preUnknownItemList = new ArrayList<>();

    public HardcoreAstagesItem(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (HardcoreAstagesItem) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        UnknownItem.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        NeoForge.EVENT_BUS.register(RebakeCommand.class);
        NeoForge.EVENT_BUS.addListener(this::onPlayerEnterWorld);

        preUnknownItemList.add("minecraft:iron_ingot");

    }

    public static List<String> getpreUnknownItemList() {
        return preUnknownItemList;
    }

    @SubscribeEvent
    private void onPlayerEnterWorld(RenderGuiEvent.Post event) {
        if (!isExecuted) {
            preUnknownItemList = GetUnknownItemList.getPreUnknownItemList();
            isExecuted = true;

            Minecraft minecraft = Minecraft.getInstance();
            ModelManager modelManager = minecraft.getModelManager();

            ResourceLocation unknownItemResource = ResourceLocation.parse("hardcoreastagesitem:unknown_item");
            ModelResourceLocation unknownModel =  new ModelResourceLocation(unknownItemResource, "inventory");
            BakedModel replaceModel = modelManager.getModel(unknownModel);

            for (String itemName : preUnknownItemList) {
                ResourceLocation originResource = ResourceLocation.parse(itemName);
                ModelResourceLocation originModel =  new ModelResourceLocation(originResource, "inventory");
                BakedModel rawModel = modelManager.getModel(originModel);

                replacedMap.put(itemName, rawModel);

                replaceModel(originModel, replaceModel, modelManager);
            }

            RebakeCommand.reloadModel();

        }

    }

    public static void replaceModel (ModelResourceLocation originModel, BakedModel replaceModel, ModelManager modelManager) {
        Minecraft minecraft = Minecraft.getInstance();
        try {
            Field modelsField = ModelManager.class.getDeclaredField("bakedRegistry");
            modelsField.setAccessible(true); // Get the access

            @SuppressWarnings("unchecked")
            Map<ModelResourceLocation, BakedModel> bakedRegistry =
                    (Map<ModelResourceLocation, BakedModel>) modelsField.get(modelManager);

            bakedRegistry.put(originModel, replaceModel);
            System.out.println("Replaced Success");

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Replaced Failed");
        }
        minecraft.getItemRenderer().onResourceManagerReload(minecraft.getResourceManager());

    }

}
