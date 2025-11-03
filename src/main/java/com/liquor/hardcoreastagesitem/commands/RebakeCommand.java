package com.liquor.hardcoreastagesitem.commands;

import com.liquor.hardcoreastagesitem.GetUnknownItemList;
import com.liquor.hardcoreastagesitem.HardcoreAstagesItem;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

import java.util.List;

public class RebakeCommand {
    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        // Register
        dispatcher.register(Commands.literal("reloadmodel")
                .requires(cs -> cs.hasPermission(0))
                .executes(context -> {
                    reloadModel();
                    return 1;
                })
        );
    }

    public static void reloadModel() {
        Minecraft minecraft = Minecraft.getInstance();
        ModelManager modelManager = minecraft.getModelManager();

        final List<String> preUnknownItemList = HardcoreAstagesItem.getpreUnknownItemList();
        final List<String> unknownItemList = GetUnknownItemList.getUnknownItemList();

        for (String itemName : preUnknownItemList) {
            if (!unknownItemList.contains(itemName)) {
                ResourceLocation originResource = ResourceLocation.parse(itemName);

                ModelResourceLocation originModel =  new ModelResourceLocation(originResource, "inventory");

                BakedModel replaceModel = HardcoreAstagesItem.replacedMap.get(itemName);

                HardcoreAstagesItem.replaceModel(originModel, replaceModel, modelManager);
            }
        }
    }
}
