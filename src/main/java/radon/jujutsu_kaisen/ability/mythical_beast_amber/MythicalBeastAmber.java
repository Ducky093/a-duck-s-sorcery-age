package radon.jujutsu_kaisen.ability.mythical_beast_amber;

import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Transformation;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.SetCursedEnergyColorC2SPacket;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MythicalBeastAmber extends Transformation {
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("81461f5f-89d5-4cc9-8b25-17e7caac9255");
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("84341016-e56a-4b95-9fd5-42b36154c885");
    private static final UUID STEP_HEIGHT_UUID = UUID.fromString("654c65b5-dc0f-4092-8423-59cbe3d19682");
    private static final UUID ARMOR_UUID = UUID.fromString("486fd273-fdbc-4876-b0b8-af5a64bfb08a");
    private static final UUID ARMOR_TOUGHNESS_UUID = UUID.fromString("0be71dde-8aeb-4c5d-955f-d37325c31a94");
    private final Set<LivingEntity> glowingEntities = new HashSet<>();  
    private int r;
     private int g;
      private int b;

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
    //if (!owner.level().isClientSide) return; // ONLY client

    Set<LivingEntity> newGlows = new HashSet<>();
    for (Entity entity : owner.level().getEntities(null, owner.getBoundingBox().inflate(15))) {
        if (entity.isAlive() && entity instanceof LivingEntity living && living != owner) {
            if (!glowingEntities.contains(living)) {
                living.setGlowingTag(true); // client-only glow
            }
            newGlows.add(living);
        }
    }
    for (LivingEntity prev : glowingEntities) {
        if (!newGlows.contains(prev) && prev.isAlive()) {
            prev.setGlowingTag(false);
        }
    }

    glowingEntities.clear();
    glowingEntities.addAll(newGlows);
}

    @Override
    public float getCost(LivingEntity owner) {
        return 4.0F;
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
        EntityUtil.applyModifier(owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Attack damage", 2.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        EntityUtil.applyModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed", 0.4D, AttributeModifier.Operation.MULTIPLY_TOTAL);
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
        
        Vector3f oldcolor = ParticleColors.getCursedEnergyColor(owner);
        this.r = (int) (oldcolor.x * 255.0D);
      
        this.g = (int) (oldcolor.y * 255.0D);
        this.b = (int) (oldcolor.z * 255.0D);

        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        
        int color = FastColor.ARGB32.color(255, Math.round(156), Math.round(95), Math.round(255));
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow(); 
        cap.setCursedEnergyColor(color);
       // PacketHandler.sendToServer(new SetCursedEnergyColorC2SPacket(color));
       
    }




    @Override
    public void onDisabled(LivingEntity owner) {
       // if (owner.level().isClientSide) {
        for (LivingEntity entity : glowingEntities) {
            if (entity.isAlive()) {
                entity.setGlowingTag(false);
            }
        }
        glowingEntities.clear();
    //}
        ISorcererData ownerCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        ownerCap.setExperience(0);
        int color = FastColor.ARGB32.color(255, Math.round(this.r), Math.round(this.g), Math.round(this.b));
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow(); 
        cap.setCursedEnergyColor(color);
        owner.kill();
    }
}
