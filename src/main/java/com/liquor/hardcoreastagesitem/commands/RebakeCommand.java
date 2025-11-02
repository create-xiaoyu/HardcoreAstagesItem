package com.liquor.hardcoreastagesitem.commands;

import com.liquor.hardcoreastagesitem.HardcoreAstagesItem;
import com.liquor.hardcoreastagesitem.HardcoreAstagesItemClient;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@OnlyIn(Dist.CLIENT)
public class RebakeCommand {
    public static final Logger LOGGER = LogUtils.getLogger();
    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        // Register
        dispatcher.register(Commands.literal("reloadmodel")
                .requires(cs -> cs.hasPermission(0))
                .executes(context -> {
                    reloadmodel();
                    context.getSource().sendSuccess(
                            () -> Component.literal("Reload Model Successfully!"),
                            true
                    );
                    return 1;
                })
        );
    }

    private static void reloadmodel() {
        Minecraft minecraft = Minecraft.getInstance();
        ModelManager modelManager = minecraft.getModelManager();

        ResourceLocation UnknownItemResource = ResourceLocation.parse("hardcoreastagesitem:unknown_item");
        ResourceLocation OriginResource = ResourceLocation.parse( "minecraft:iron_ingot");

        ModelResourceLocation UnknownModel =  new ModelResourceLocation(UnknownItemResource, "inventory");
        ModelResourceLocation OriginModel =  new ModelResourceLocation(OriginResource, "inventory");

        BakedModel replaceModel = modelManager.getModel(OriginModel);
        replaceModel = HardcoreAstagesItem.Ironraw;

        HardcoreAstagesItem.replaceModel(OriginModel, replaceModel, modelManager);
    }
}
