package radon.jujutsu_kaisen.client.model.base;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

import org.jetbrains.annotations.NotNull;

public class SkinModel<T extends LivingEntity> extends PlayerModel<T> {
    public SkinModel(ModelPart pRoot) {
        super(pRoot, false);
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        ISorcererData cap = entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        if (cap.hasToggled(JJKAbilities.HOLLOW_WICKER_BASKET.get())) {
            this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
            this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
            // Move arms in front of the chest
            this.rightArm.xRot = (float) Math.toRadians(-45);
            this.rightArm.yRot = (float) Math.toRadians(-10);
            this.rightArm.zRot = 0;

            this.leftArm.xRot = (float) Math.toRadians(-45);
            this.leftArm.yRot = (float) Math.toRadians(10);
            this.leftArm.zRot = 0;
        }
    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64);
    }

    public static LayerDefinition createSlimBodyLayer() {
        return LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, true), 64, 64);
    }

    public static LayerDefinition createInnerLayer() {
        return LayerDefinition.create(HumanoidModel.createMesh(LayerDefinitions.INNER_ARMOR_DEFORMATION, 0.0F), 64, 32);
    }

    public static LayerDefinition createOuterLayer() {
        return LayerDefinition.create(HumanoidModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION, 0.0F), 64, 32);
    }

    private HumanoidModel.ArmPose getArmPose(T pEntity, InteractionHand pHand) {
        ItemStack stack = pEntity.getItemInHand(pHand);

        if (stack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        } else {
            if (pEntity.getUsedItemHand() == pHand && pEntity.getUseItemRemainingTicks() > 0) {
                UseAnim anim = stack.getUseAnimation();


                
                if (anim == UseAnim.BLOCK) {
                    return HumanoidModel.ArmPose.BLOCK;
                }

                if (anim == UseAnim.BOW) {
                    return HumanoidModel.ArmPose.BOW_AND_ARROW;
                }

                if (anim == UseAnim.SPEAR) {
                    return HumanoidModel.ArmPose.THROW_SPEAR;
                }

                if (anim == UseAnim.CROSSBOW && pHand == pEntity.getUsedItemHand()) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }

                if (anim == UseAnim.SPYGLASS) {
                    return HumanoidModel.ArmPose.SPYGLASS;
                }

                if (anim == UseAnim.TOOT_HORN) {
                    return HumanoidModel.ArmPose.TOOT_HORN;
                }
            } else if (!pEntity.swinging && stack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(stack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }
            HumanoidModel.ArmPose forgePose = IClientItemExtensions.of(stack).getArmPose(pEntity, pHand, stack);
            if (forgePose != null) return forgePose;
            return HumanoidModel.ArmPose.ITEM;
        }
    }

    @Override
    public void prepareMobModel(@NotNull T pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
        this.rightArmPose = getArmPose(pEntity, InteractionHand.MAIN_HAND);
        this.leftArmPose = getArmPose(pEntity, InteractionHand.OFF_HAND);

        super.prepareMobModel(pEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
    }
}
