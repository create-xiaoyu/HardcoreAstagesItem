package com.liquor.hardcoreastagesitem;

import com.liquor.hardcoreastagesitem.Items.UnknownItem;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import java.lang.reflect.Field;
import java.util.Map;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(HardcoreAstagesItem.MODID)
public class HardcoreAstagesItem {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "hardcoreastagesitem";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public HardcoreAstagesItem(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (HardcoreAstagesItem) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        modEventBus.addListener(this::onModelBaking);

        UnknownItem.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }


    private void onModelBaking(ModelEvent.BakingCompleted event) {
        LOGGER.debug("Baking...");

        ResourceLocation UnknownItemResource = ResourceLocation.parse("hardcoreastagesitem:unknown_item");
        ResourceLocation OriginResource = ResourceLocation.parse( "minecraft:iron_ingot");

        ModelResourceLocation UnknownModel =  new ModelResourceLocation(UnknownItemResource, "inventory");
        ModelResourceLocation OriginModel =  new ModelResourceLocation(OriginResource, "inventory");

        ModelManager modelManager = event.getModelManager();

        BakedModel replaceModel = modelManager.getModel(UnknownModel);

        try {
            Field modelsField = ModelManager.class.getDeclaredField("bakedRegistry");
            modelsField.setAccessible(true); // 允许访问私有字段

            @SuppressWarnings("unchecked")
            Map<ModelResourceLocation, BakedModel> bakedRegistry =
                    (Map<ModelResourceLocation, BakedModel>) modelsField.get(modelManager);

            bakedRegistry.put(OriginModel, replaceModel);
            System.out.println("Replaced Success");

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Replaced Failed");
        }
    }
}
