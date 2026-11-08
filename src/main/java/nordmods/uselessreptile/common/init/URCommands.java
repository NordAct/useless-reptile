package nordmods.uselessreptile.common.init;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.spawn.DragonSpawnUtil;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;

import java.util.*;

public class URCommands {
    private static final LiteralArgumentBuilder<CommandSourceStack> PREFIX = Commands.literal(UselessReptile.MODID).requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));
    private static final DynamicCommandExceptionType ERROR_INVALID_DRAGON_ID = new DynamicCommandExceptionType(
            value -> Component.translatableEscape("command.uselessreptile.invalid_dragon_id", value)
    );
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> {
            dispatcher.register(
                    PREFIX.then(Commands.literal("triggerlightningchaser")
                            .then(Commands.argument("player", EntityArgument.player())
                                    .executes(context -> triggerLightningChaser(EntityArgument.getPlayer(context, "player"), context.getSource()))
                            )
                    )
            );
            dispatcher.register(
                    PREFIX.then(Commands.literal("listvariants")
                            .executes(context -> listAvailableVariants(BlockPos.containing(context.getSource().getPosition()), context.getSource().getLevel(), context.getSource()))
                            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                    .executes(context -> listAvailableVariants(BlockPosArgument.getBlockPos(context, "pos"), context.getSource().getLevel(), context.getSource()))
                                    .then(Commands.argument("dragon id", ResourceKeyArgument.key(URResourceKeys.DRAGON_VARIANT_TYPE))
                                            .executes(context -> {
                                                ResourceKey<DragonVariantType<?>> key = ResourceKeyArgument.getRegistryKey(context, "dragon id", URResourceKeys.DRAGON_VARIANT_TYPE, ERROR_INVALID_DRAGON_ID);
                                                return listAvailableVariantsForType(URRegistries.VARIANT_TYPE.get(key).orElseThrow().value(), BlockPosArgument.getBlockPos(context, "pos"), context.getSource().getLevel(), context.getSource());
                                            })
                                    )
                            )
                    )
            );
        });
    }

    private static int triggerLightningChaser(ServerPlayer player, CommandSourceStack source) {
        if (URModEvents.placeLightningChaser(player.level(), player)) {
            source.sendSuccess(() -> Component.translatable("command.uselessreptile.triggerlightningchaser.success", player.getName()), true);
            return 1;
        } else {
            source.sendFailure(Component.translatable("command.uselessreptile.triggerlightningchaser.fail"));
            return 0;
        }
    }

    private static int listAvailableVariantsForType(DragonVariantType<?> type, BlockPos pos, ServerLevel level, CommandSourceStack source) {
        Map<DragonVariant, Set<EntitySpawnReason>> variantAndReasons = new HashMap<>();
        for (EntitySpawnReason reason : EntitySpawnReason.values()) {
            DragonSpawnUtil.getAvailableVariants(level, pos, type, reason).forEach(variant -> {
                variantAndReasons.compute(variant, (v, reasons) -> {
                   if (reasons == null) {
                       Set<EntitySpawnReason> set = new HashSet<>();
                       set.add(reason);
                       return set;
                   }
                   reasons.add(reason);
                   return reasons;
                });
            });
        }
        MutableComponent output = Component.translatable(
                "command.uselessreptile.list_variants",
                Component.literal(pos.toShortString()).withStyle(ChatFormatting.RED),
                Component.literal(type.getId().toString()).withStyle(ChatFormatting.RED))
                .append(Component.literal(":\n").withStyle(ChatFormatting.GOLD))
                .withStyle(ChatFormatting.GOLD);
        if (variantAndReasons.isEmpty()) {
            output.append(Component.literal("- ").withStyle(ChatFormatting.WHITE))
                    .append(Component.translatable("command.uselessreptile.list_variants.none").withStyle(ChatFormatting.RED));
        } else {
            List<EntitySpawnReason> allReasons = List.of(EntitySpawnReason.values());
            variantAndReasons.forEach((variant, reasons) -> {
                output.append(
                        Component.literal("- ")
                                .append(Component.translatable(variant.common().variantNameKey()))
                                .append(
                                        Component.literal("(" + variant.common().name() + ")")
                                                .withStyle(s ->s.withColor(ChatFormatting.GREEN)
                                                        .withClickEvent(new ClickEvent.SuggestCommand("/summon " + type.getId().toString() + " ~ ~ ~ {Variant:\"" + variant.common().name() + "\"}"))
                                                        .withHoverEvent(new HoverEvent.ShowText(Component.translatable("command.uselessreptile.list_variants.summon")))
                                                )
                                ).append(": ")
                                .withStyle(ChatFormatting.WHITE)
                );
                if (reasons.containsAll(allReasons)) {
                    output.append(Component.translatable("command.uselessreptile.list_variants.any_reason").withStyle(ChatFormatting.AQUA));
                } else {
                    for (Iterator<EntitySpawnReason> iterator = reasons.iterator(); iterator.hasNext(); ) {
                        EntitySpawnReason reason = iterator.next();
                        output.append(Component.literal(reason.name()).withStyle(ChatFormatting.DARK_AQUA));
                        if (iterator.hasNext()) output.append(Component.literal(", ").withStyle(ChatFormatting.WHITE));
                    }
                }
                output.append("\n");
            });
        }
        source.sendSuccess(() -> output, false);
        return 1;
    }

    private static int listAvailableVariants(BlockPos pos, ServerLevel level, CommandSourceStack source) {
        URRegistries.VARIANT_TYPE.forEach(type -> listAvailableVariantsForType(type, pos, level, source));
        return 1;
    }
}
