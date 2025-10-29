package radon.jujutsu_kaisen.ability.cursed_speech;


import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;


public abstract class CursedSpeech extends Ability implements ICursedSpeech {
    @Override
    public Status isTriggerable(LivingEntity owner) {
        ISorcererData selfCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        if (selfCap.isThroatDamaged()) {
            return Status.THROAT;
        }
        return super.isTriggerable(owner);
    }

    // @Override
    // public void run(LivingEntity owner) {
        
    // }
}