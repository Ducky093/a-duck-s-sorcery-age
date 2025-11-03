package radon.jujutsu_kaisen.client.ability;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.client.gui.overlay.AbilityOverlay;
import radon.jujutsu_kaisen.client.gui.screen.AbilityScreen;
import radon.jujutsu_kaisen.client.gui.screen.DomainScreen;
import radon.jujutsu_kaisen.client.gui.screen.ShadowInventoryScreen;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.entity.base.IJumpInputListener;
import radon.jujutsu_kaisen.entity.base.IRightClickInputListener;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.JumpInputListenerC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.RightClickInputListenerC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.TriggerAbilityC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.UntriggerAbilityC2SPacket;
import radon.jujutsu_kaisen.util.EntityUtil;

import java.awt.event.KeyEvent;


public class ClientAbilityHandler {
    private static @Nullable Ability channeled;
    private static @Nullable KeyMapping current;
    private static boolean isChanneling;
    private static boolean isRightDown;

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientAbilityHandlerForgeEvents {
        @SubscribeEvent
        public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
            LivingEntity entity = event.getEntity();

            ClientVisualHandler.ClientData data = ClientVisualHandler.get(entity);

            if (data == null) return;

            if (!(event.getRenderer().getModel() instanceof PlayerModel<?> player)) return;

            for (Ability ability : data.toggled) {
                if (!(ability instanceof ITransformation transformation)) continue;

                if (transformation.isReplacement()) {
                    switch (transformation.getBodyPart()) {
                        case HEAD -> {
                            player.head.visible = false;
                            player.hat.visible = false;
                        }
                        case BODY -> player.setAllVisible(false);
                        case RIGHT_ARM -> {
                            player.rightArm.visible = false;
                            player.rightSleeve.visible = false;
                        }
                        case LEFT_ARM -> {
                            player.leftArm.visible = false;
                            player.rightSleeve.visible = false;
                        }
                        case LEGS -> {
                            player.rightLeg.visible = false;
                            player.rightPants.visible = false;
                            player.leftLeg.visible = false;
                            player.leftPants.visible = false;
                        }
                    }
                }

                HumanoidModel.ArmPose pose = IClientItemExtensions.of(transformation.getItem()).getArmPose(event.getEntity(), InteractionHand.MAIN_HAND, transformation.getItem().getDefaultInstance());

                if (pose != null) {
                    if (transformation.getBodyPart() == ITransformation.Part.RIGHT_ARM) {
                        player.rightArmPose = pose;
                    } else if (transformation.getBodyPart() == ITransformation.Part.LEFT_ARM) {
                        player.leftArmPose = pose;
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (current != null) {
                boolean possiblyChanneling = channeled != null;

                if (possiblyChanneling) {
                    boolean isHeld = current.isDown();

                    if (isHeld) {
                        if (!isChanneling) {
                            //if (ClientAbilityHandler.trigger(channeled) == Ability.Status.SUCCESS) {
                                PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(channeled)));
                           // }
                        }
                        isChanneling = true;
                    } else if (isChanneling) {
                        AbilityHandler.untrigger(mc.player, channeled);
                        PacketHandler.sendToServer(new UntriggerAbilityC2SPacket(JJKAbilities.getKey(channeled)));

                        channeled = null;
                        current = null;
                        isChanneling = false;
                    }
                }
            }

            if (mc.player.getVehicle() instanceof IRightClickInputListener listener) {
                if (!isRightDown && mc.mouseHandler.isRightPressed()) {
                    listener.setDown(true);
                    PacketHandler.sendToServer(new RightClickInputListenerC2SPacket(true));

                    isRightDown = true;
                } else if (isRightDown && !mc.mouseHandler.isRightPressed()) {
                    listener.setDown(false);
                    PacketHandler.sendToServer(new RightClickInputListenerC2SPacket(false));

                    isRightDown = false;
                }
            }
        }

          private static void handleInput(int inputObj, int action) {
 Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (inputObj == KeyEvent.VK_SPACE || inputObj == GLFW.GLFW_KEY_SPACE) {
                if (action == InputConstants.PRESS || action == InputConstants.RELEASE) {
                    boolean down = action == InputConstants.PRESS;

                    if (mc.player.getVehicle() instanceof IJumpInputListener listener) {
                        listener.setJump(down);
                        PacketHandler.sendToServer(new JumpInputListenerC2SPacket(down));
                    } else if (mc.player.getFirstPassenger() instanceof IJumpInputListener listener) {
                        listener.setJump(down);
                        PacketHandler.sendToServer(new JumpInputListenerC2SPacket(down));
                    }
                }
            }

            if (action == InputConstants.PRESS) {
                if (JJKKeys.ACTIVATE_ABILITY.isDown()) {
                    Ability ability = AbilityOverlay.getSelected();

                    if (ability != null) {
                        if (ability.getActivationType(mc.player) == Ability.ActivationType.CHANNELED) {
                            channeled = ability;
                            current = JJKKeys.ACTIVATE_ABILITY;
                        } else {
                            //if (ClientAbilityHandler.trigger(ability) == Ability.Status.SUCCESS) {
                                PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
                            //}
                        }
                    }
                }

                if (JJKKeys.ACTIVATE_J2TSU.isDown()) {
                    Ability ability = AbilityOverlay.getSelected2();

                    if (ability != null) {
                        if (ability.getActivationType(mc.player) == Ability.ActivationType.CHANNELED) {
                            channeled = ability;
                            current = JJKKeys.ACTIVATE_J2TSU;
                        } else {
                            //if (ClientAbilityHandler.trigger(ability) == Ability.Status.SUCCESS) {
                                PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
                            //}
                        }
                    }
                }

                if (JJKKeys.ACTIVATE_RCT_OR_HEAL.isDown()) {
                    Ability rct = EntityUtil.getRCTTier(mc.player);

                    if (JJKAbilities.getType(mc.player) == JujutsuType.CURSE) {
                        channeled = JJKAbilities.HEAL.get();
                        current = JJKKeys.ACTIVATE_RCT_OR_HEAL;
                    } else if (rct != null) {
                        channeled = rct;
                        current = JJKKeys.ACTIVATE_RCT_OR_HEAL;
                    }
                }

                if (JJKKeys.ACTIVATE_CURSED_ENERGY_SHIELD.isDown()) {
                        channeled = JJKAbilities.CURSED_ENERGY_SHIELD.get();
                        current = JJKKeys.ACTIVATE_CURSED_ENERGY_SHIELD;
                }

                if (JJKKeys.DASH.isDown()) {
                    //if (ClientAbilityHandler.trigger(JJKAbilities.DASH.get()) == Ability.Status.SUCCESS) {
                        PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(JJKAbilities.DASH.get())));
                    //}
                }
                if (JJKKeys.QUICKDASH.isDown()) {
                    //if (ClientAbilityHandler.trigger(JJKAbilities.QUICKDASH.get()) == Ability.Status.SUCCESS) {
                        PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(JJKAbilities.QUICKDASH.get())));
                    //}
                }
            } else if (action == InputConstants.RELEASE) {
                if (current != null) {
                    boolean possiblyChanneling = channeled != null;

                    if (possiblyChanneling) {
                        if (inputObj == current.getKey().getValue()) {
                            AbilityHandler.untrigger(mc.player, channeled);
                            PacketHandler.sendToServer(new UntriggerAbilityC2SPacket(JJKAbilities.getKey(channeled)));

                            channeled = null;
                            current = null;
                            isChanneling = false;
                        }
                    }
                }
                if ((inputObj == JJKKeys.SHOW_ABILITY_MENU.getKey().getValue() && mc.screen instanceof AbilityScreen) ||
                        (inputObj == JJKKeys.SHOW_DOMAIN_MENU.getKey().getValue() && mc.screen instanceof DomainScreen) ||
                        (inputObj == JJKKeys.ACTIVATE_J2TSU.getKey().getValue() && mc.screen instanceof ShadowInventoryScreen)) {
                    mc.screen.onClose();
                }
            }
        }
         
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        handleInput(event.getKey(), event.getAction());
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton event) {
        handleInput(event.getButton(), event.getAction());
    }
    }

      

    // public static boolean isSuccess(Ability ability, Ability.Status status) {
    //     Minecraft mc = Minecraft.getInstance();
    //     LocalPlayer owner = mc.player;

    //     if (owner == null) return false;

    //     ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

    //     switch (status) {
    //         case ENERGY ->
    //                 mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.energy", JujutsuKaisen.MOD_ID)), false);
    //         case COOLDOWN ->
    //                 mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.cooldown", JujutsuKaisen.MOD_ID),
    //                         Math.max(1, cap.getRemainingCooldown(ability) / 20)), false);
    //         case BURNOUT ->
    //                 mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.burnout", JujutsuKaisen.MOD_ID),
    //                     cap.getBurnout() / 20), false);
    //         case DISABLE ->
    //                 mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.disable", JujutsuKaisen.MOD_ID)), false);     
    //         case FAILURE ->
    //                 mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.failure", JujutsuKaisen.MOD_ID)), false);
    //         case CHANT ->
    //                 mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.chant", JujutsuKaisen.MOD_ID)), false);
    //         case THROAT ->
    //                 mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.throat", JujutsuKaisen.MOD_ID), 
    //                 Math.max(1, cap.getThroatDamage() / 20)), false);
    //         case EMPTYINV ->
    //                 mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.emptyinv", JujutsuKaisen.MOD_ID)), false);
    //         }
    //     return status == Ability.Status.SUCCESS;
    // }

    public static Ability.Status trigger(Ability ability) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer owner = mc.player;

        if (owner == null) return Ability.Status.FAILURE;

        // DO NOT REMOVE
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return Ability.Status.FAILURE;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT) {
            Ability.Status status;

            //if (isSuccess(ability, (status = ability.isTriggerable(owner)))) {
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                ability.run(owner);
                ability.charge(owner);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            //}
           // return status;
        } else if (ability.getActivationType(owner) == Ability.ActivationType.TOGGLED) {
            //Ability.Status status;

            //if (isSuccess(ability, (status = ability.isTriggerable(owner))) | (status == Ability.Status.ENERGY && ability instanceof Ability.IAttack)) {
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                cap.toggle(ability);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            //}
            //return status;
        } else if (ability.getActivationType(owner) == Ability.ActivationType.CHANNELED) {
            //Ability.Status status;

            //if (isSuccess(ability, (status = ability.isTriggerable(owner))) || (status == Ability.Status.ENERGY && ability instanceof Ability.IAttack)) {
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                cap.channel(ability);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            //}
            //return status;
        }
        return Ability.Status.SUCCESS;
    }
}
