package com.liquor.hardcoreastagesitem.commands;

import com.liquor.hardcoreastagesitem.GetUnknownItemList;
import com.liquor.hardcoreastagesitem.HardcoreAstagesItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import org.slf4j.Logger;
import java.util.List;

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

    public static void reloadmodel() {
        Minecraft minecraft = Minecraft.getInstance();
        ModelManager modelManager = minecraft.getModelManager();

        final List<String> preUnknownItemList = HardcoreAstagesItem.getpreUnknownItemList();
        final List<String> UnknownItemList = GetUnknownItemList.getUnknownItemList();

        for (String ItemName : preUnknownItemList) {
            if (UnknownItemList.contains(ItemName)) {
                continue;
            }else{
                ResourceLocation OriginResource = ResourceLocation.parse(ItemName);

                ModelResourceLocation OriginModel =  new ModelResourceLocation(OriginResource, "inventory");

                BakedModel replaceModel = HardcoreAstagesItem.replacedmap.get(ItemName);

                HardcoreAstagesItem.replaceModel(OriginModel, replaceModel, modelManager);
            }

        }

    }

}
