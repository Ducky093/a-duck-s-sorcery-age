package radon.jujutsu_kaisen.ability.mythical_beast_amber;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Transformation;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.UUID;

public class MythicalBeastAmber extends Transformation {
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("81461f5f-89d5-4cc9-8b25-17e7caac9255");
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("84341016-e56a-4b95-9fd5-42b36154c885");
    private static final UUID STEP_HEIGHT_UUID = UUID.fromString("654c65b5-dc0f-4092-8423-59cbe3d19682");
    private static final UUID ARMOR_UUID = UUID.fromString("486fd273-fdbc-4876-b0b8-af5a64bfb08a");
    private static final UUID ARMOR_TOUGHNESS_UUID = UUID.fromString("0be71dde-8aeb-4c5d-955f-d37325c31a94");


    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (JJKAbilities.hasToggled(owner, this)) {
            return target != null && !target.isDeadOrDying() && HelperMethods.RANDOM.nextInt(20) != 0;
        }
        return target != null && !target.isDeadOrDying() && HelperMethods.RANDOM.nextInt(5) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.5F;
    }

    @Override
    public boolean isReplacement() {
        return false;
    }

    @Override
    public Item getItem() {
        return JJKItems.MYTHICAL_BEAST_AMBER.get();
    }

    @Override
    public Part getBodyPart() {
        return Part.BODY;
    }

    @Override
    public void onRightClick(LivingEntity owner) {

    }

    @Override
    public void applyModifiers(LivingEntity owner) {
        EntityUtil.applyModifier(owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Attack damage", 3.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        EntityUtil.applyModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed", 0.35D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        EntityUtil.applyModifier(owner, ForgeMod.STEP_HEIGHT_ADDITION.get(), STEP_HEIGHT_UUID, "Step height addition", 2.0F, AttributeModifier.Operation.ADDITION);
        EntityUtil.applyModifier(owner, Attributes.ARMOR, ARMOR_UUID, "Armor", 30.0D, AttributeModifier.Operation.ADDITION);
        EntityUtil.applyModifier(owner, Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_UUID, "Armor toughness", 4.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void removeModifiers(LivingEntity owner) {
        EntityUtil.removeModifier(owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
        EntityUtil.removeModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID);
        EntityUtil.removeModifier(owner, ForgeMod.STEP_HEIGHT_ADDITION.get(), STEP_HEIGHT_UUID);
        EntityUtil.removeModifier(owner, Attributes.ARMOR, ARMOR_UUID);
        EntityUtil.removeModifier(owner, Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_UUID);
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        
    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }
}
