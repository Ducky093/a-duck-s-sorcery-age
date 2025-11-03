package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import net.minecraft.server.level.ServerLevel;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;


public class AbilityHandler {
    public static void untrigger(LivingEntity owner,Ability ability) {

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
        if (ability.getActivationType(owner) == Ability.ActivationType.TOGGLED) {
            if (cap.hasToggled(ability)) {
                cap.toggle(ability);
            }
        }
           else if (ability.getActivationType(owner) == Ability.ActivationType.DOMAIN) {
             if (owner instanceof Player player) {    
                if (cap.hasToggled(ability)) {
                    cap.toggle(ability);
                }
                return;
            }

            boolean enemyDomain = false;
            if (owner.level().isClientSide) return;
            for (DomainExpansionEntity domain : VeilHandler.getDomains((ServerLevel) owner.level(), owner.blockPosition())) {
                if (domain.getOwner() != owner) {
                    enemyDomain = true;
                }
            }
                    if (cap.hasToggled(ability)) {
                        if (enemyDomain == false) {
                            cap.toggle(ability);
                        }
                    }

        }
           else if (ability.getActivationType(owner) == Ability.ActivationType.CHANNELED) {
            if (cap.isChanneling(ability)) {
                cap.channel(ability);
            }
        }
    });
    }

    public static Ability.Status trigger(LivingEntity owner, Ability ability) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
        if (cap == null) return Ability.Status.FAILURE;
        Ability.Status status = ability.isTriggerable(owner);


        if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT ) {
            if (status == Ability.Status.SUCCESS) {
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                ability.charge(owner);
                ability.run(owner);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            }
        } else if (ability.getActivationType(owner) == Ability.ActivationType.TOGGLED) {
            if (status == Ability.Status.SUCCESS || (status == Ability.Status.ENERGY && ability instanceof Ability.IAttack)) {
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                cap.toggle(ability);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            }
            return status;

        } else if (ability.getActivationType(owner) == Ability.ActivationType.DOMAIN) {
            if (status == Ability.Status.SUCCESS || (status == Ability.Status.ENERGY && ability instanceof Ability.IAttack)) {
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                cap.toggle(ability);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            }
            return status;
        }

        else if (ability.getActivationType(owner) == Ability.ActivationType.CHANNELED) {
            if (status == Ability.Status.SUCCESS || (status == Ability.Status.ENERGY && ability instanceof Ability.IAttack)) {
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                cap.channel(ability);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            }
            return status;
        }
        return status;
    }
}
