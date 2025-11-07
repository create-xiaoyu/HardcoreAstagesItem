package com.xiaoyu.hardcoreastagesitem;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.HashMap;
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
            RebakeModel.onStageRemoved(null);
            RebakeModel.reloadModel();
        }
    }
}