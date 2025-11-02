package com.liquor.hardcoreastagesitem;

import com.alessandro.astages.core.client.manager.AClientItemManager;
import com.liquor.hardcoreastagesitem.Items.UnknownItem;
import com.liquor.hardcoreastagesitem.commands.RebakeCommand;
import com.liquor.hardcoreastagesitem.GetUnknownItemList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import java.lang.reflect.Field;
import java.security.PrivateKey;
import java.util.*;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(HardcoreAstagesItem.MODID)
public class HardcoreAstagesItem {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "hardcoreastagesitem";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static BakedModel Ironraw = null;

    public static Map<String, BakedModel> replacedmap = new HashMap<>();

    private static List<String> preUnknownItemList = new ArrayList<>();
    private static List<String> UnknownItemList = new ArrayList<>();

    public HardcoreAstagesItem(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (HardcoreAstagesItem) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        modEventBus.addListener(this::onModelBaking);
        modEventBus.addListener(this::commonSetup);

        UnknownItem.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        NeoForge.EVENT_BUS.register(RebakeCommand.class);

        preUnknownItemList.add("minecraft:iron_ingot");

    }

    public static List<String> getpreUnknownItemList() {
        return preUnknownItemList;
    }
    public static List<String> getUnknownItemList() {
        return UnknownItemList;
    }

    private void onModelBaking(ModelEvent.BakingCompleted event) {
        LOGGER.debug("Baking...");

        ModelManager modelManager = event.getModelManager();

        ResourceLocation UnknownItemResource = ResourceLocation.parse("hardcoreastagesitem:unknown_item");
        ModelResourceLocation UnknownModel =  new ModelResourceLocation(UnknownItemResource, "inventory");
        BakedModel replaceModel = modelManager.getModel(UnknownModel);

        for (String ItemName : preUnknownItemList) {
            ResourceLocation OriginResource = ResourceLocation.parse(ItemName);
            ModelResourceLocation OriginModel =  new ModelResourceLocation(OriginResource, "inventory");
            BakedModel rawModel = modelManager.getModel(OriginModel);

            replacedmap.put(ItemName, rawModel);

            replaceModel(OriginModel, replaceModel, modelManager);
        }

    }

    public static void replaceModel (ModelResourceLocation OriginModel, BakedModel replaceModel, ModelManager modelManager) {
        Minecraft minecraft = Minecraft.getInstance();
        try {
            Field modelsField = ModelManager.class.getDeclaredField("bakedRegistry");
            modelsField.setAccessible(true); // 允许访问私有字段

            @SuppressWarnings("unchecked")
            Map<ModelResourceLocation, BakedModel> bakedRegistry =
                    (Map<ModelResourceLocation, BakedModel>) modelsField.get(modelManager);

            bakedRegistry.put(OriginModel, replaceModel);
            System.out.println("Replaced Success");
            LOGGER.debug(bakedRegistry.get(OriginModel).toString());

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Replaced Failed");
        }
        minecraft.getItemRenderer().onResourceManagerReload(minecraft.getResourceManager());

    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.debug("Common Setup");
    }
}
