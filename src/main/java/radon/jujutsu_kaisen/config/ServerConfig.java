package radon.jujutsu_kaisen.config;

import net.minecraftforge.common.ForgeConfigSpec;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ServerConfig {
    public final ForgeConfigSpec.DoubleValue cursedEnergyAmount;
    public final ForgeConfigSpec.DoubleValue cursedEnergyRegenerationAmount;
    public final ForgeConfigSpec.DoubleValue maximumExperienceAmount;
    public final ForgeConfigSpec.DoubleValue cursedObjectEnergyForGrade;
    public final ForgeConfigSpec.IntValue reverseCursedTechniqueChance;
    public final ForgeConfigSpec.DoubleValue requiredExperienceForExperienced;
    public final ForgeConfigSpec.IntValue sorcererFleshRarity;
    public final ForgeConfigSpec.IntValue curseFleshRarity;
    public final ForgeConfigSpec.DoubleValue pointMultiplier;
    public final ForgeConfigSpec.DoubleValue experienceMultiplier;
    public final ForgeConfigSpec.DoubleValue minimumBodyStealEXP;
    public final ForgeConfigSpec.DoubleValue deathPenalty;
    public final ForgeConfigSpec.DoubleValue pointPenalty;
    public final ForgeConfigSpec.DoubleValue pvpGain;
    public final ForgeConfigSpec.IntValue blackFlashChance;
    public final ForgeConfigSpec.BooleanValue realisticShikigami;
    public final ForgeConfigSpec.BooleanValue realisticCurses;
    public final ForgeConfigSpec.BooleanValue playerBodySteal;
    public final ForgeConfigSpec.BooleanValue bodyStealEXPReset;
    public final ForgeConfigSpec.BooleanValue MBAEXPReset;
    public final ForgeConfigSpec.BooleanValue hrRequiredForISOH;
    public final ForgeConfigSpec.BooleanValue playerRequiredForRCT;

    public final ForgeConfigSpec.DoubleValue sorcererHealingAmount;
    public final ForgeConfigSpec.DoubleValue curseHealingAmount;
    public final ForgeConfigSpec.BooleanValue uniqueTechniques;
    public final ForgeConfigSpec.BooleanValue uniqueTraits;
    public final ForgeConfigSpec.BooleanValue destruction;
    public final ForgeConfigSpec.BooleanValue turboMode;
    public final ForgeConfigSpec.BooleanValue entitySlicing;
    public final ForgeConfigSpec.BooleanValue chantRequiredForWCS;

    public final ForgeConfigSpec.IntValue minimumVeilSize;
    public final ForgeConfigSpec.IntValue maximumVeilSize;
    public final ForgeConfigSpec.DoubleValue minimumDomainSize;
    public final ForgeConfigSpec.DoubleValue maximumDomainSize;

    public final ForgeConfigSpec.IntValue maximumChantCount;
    public final ForgeConfigSpec.IntValue maximumChantLength;
    public final ForgeConfigSpec.DoubleValue chantSimilarityThreshold;

    public final ForgeConfigSpec.IntValue simpleDomainCost;
    public final ForgeConfigSpec.IntValue simpleDomainEnlargementCost;
    public final ForgeConfigSpec.IntValue maximumUzumakiCost;
    public final ForgeConfigSpec.IntValue miniUzumakiCost;
    public final ForgeConfigSpec.IntValue maximumMeteorCost;
    public final ForgeConfigSpec.IntValue ceBombCost;
    public final ForgeConfigSpec.IntValue ceBlastCost;
    public final ForgeConfigSpec.IntValue quickDrawCost;
    public final ForgeConfigSpec.IntValue hollowWickerBasketCost;
    public final ForgeConfigSpec.IntValue fallingBlossomEmotionCost;
    public final ForgeConfigSpec.IntValue domainExpansionCost;
    public final ForgeConfigSpec.IntValue domainAmplificationCost;
    public final ForgeConfigSpec.IntValue zeroPointTwoSecondDomainExpansionCost;
    public final ForgeConfigSpec.IntValue rct2Cost;
    public final ForgeConfigSpec.IntValue rct3Cost;
    public final ForgeConfigSpec.IntValue outputRCTCost;
    public final ForgeConfigSpec.IntValue maximumCopiedTechniques;
    public final ForgeConfigSpec.IntValue maximumStolenTechniques;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> unlockableTechniques;

    public final ForgeConfigSpec.IntValue cursedEnergyNatureRarity;
    public final ForgeConfigSpec.IntValue curseRarity;
    public final ForgeConfigSpec.IntValue sixEyesRarity;
    public final ForgeConfigSpec.IntValue heavenlyRestrictionRarity;
    public final ForgeConfigSpec.IntValue vesselRarity;
    public final ForgeConfigSpec.IntValue rctOutputRarity;
    public final ForgeConfigSpec.IntValue perfectBodyRarity;
    public final ForgeConfigSpec.IntValue incarnatedRarity;

    public ServerConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Progression").push("progression");
        this.cursedEnergyAmount = builder.comment("Cursed energy amount (scales with experience)")
                .defineInRange("cursedEnergyAmount", 500.0F, 0.0F, 1000000.0F);
        this.cursedEnergyRegenerationAmount = builder.comment("Cursed energy regeneration amount (depends on food level)")
                .defineInRange("cursedEnergyRegenerationAmount", 0.6F, 0.0F, 100000.0F);
        this.maximumExperienceAmount = builder.comment("The maximum amount of experience one can obtain")
                .defineInRange("maximumExperienceAmount", 20000.0F, 1.0F, 1000000.0F);
        this.cursedObjectEnergyForGrade = builder.comment("The amount of energy consuming cursed objects gives to curses (multiplied by the grade of the object)")
                .defineInRange("cursedObjectEnergyForGrade", 100.0F, 1.0F, 1000.0F);
        this.reverseCursedTechniqueChance = builder.comment("The chance of unlocking reverse cursed technique when dying (smaller number equals bigger chance and the value is halved when holding a totem)")
                .defineInRange("reverseCursedTechniqueChance", 20, 1, 1000);
        this.requiredExperienceForExperienced = builder.comment("The amount of experience required for a player to be classified as experienced (for now means they can use domain amplification during a domain expansion)")
                .defineInRange("requiredExperienceForExperienced", 5000.0F, 1.0F, 100000.0F);
        this.sorcererFleshRarity = builder.comment("Rarity of sorcerers dropping flesh (bigger value means more rare)")
                .defineInRange("sorcererFleshRarity", 20, 0, 100000);
        this.curseFleshRarity = builder.comment("Rarity of curses dropping flesh (bigger value means more rare)")
                .defineInRange("curseFleshRarity", 20, 0, 100000);
        this.experienceMultiplier = builder.comment("Scale of experience you gain")
                        .defineInRange("experienceMultiplier", 1.0F, 0.0F, 100.0F);
        this.minimumBodyStealEXP = builder.comment("Minimum EXP before a body can be stolen")
                        .defineInRange("minimumBodyStealEXP", 0.0F, 0.0F, 100000.0F);
        this.pointMultiplier = builder.comment("Scale of ability points you gain")
                        .defineInRange("pointMultiplier", 0.1F, 0.0F, 100.0F);
        this.pointPenalty = builder.comment("Scale of points lost on death")
                        .defineInRange("pointPenalty", 0.0F, 0.0F, 100.0F);
        this.deathPenalty = builder.comment("Percentage of experience lost on death")
                .defineInRange("deathPenalty", 0.0F, 0.0F, 1.0F);
        this.pvpGain = builder.comment("Percentage of experience gained from player kills")
                .defineInRange("pvpGain", 1.0F, 0.0F, 999.0F);
        this.blackFlashChance = builder.comment("The chance of black flash (smaller number equals bigger chance)")
                .defineInRange("blackFlashChance", 150, 1, 1000);
        this.realisticShikigami = builder.comment("When enabled Ten Shadows shikigami will die permanently")
                .define("realisticShikigami", false);
        this.realisticCurses = builder.comment("When enabled curses only take damage from jujutsu attacks")
                .define("realisticCurses", true);
        this.playerBodySteal = builder.comment("When enabled Body Steal only works on players")
                .define("playerBodySteal", false);
        this.bodyStealEXPReset = builder.comment("Whether Body Steal should reset the EXP of the stolen player")
                .define("bodyStealEXPReset", true);
        this.MBAEXPReset = builder.comment("Whether Mythical Beast Amber should reset the EXP of the user after use")
                .define("MBAEXPReset", true);
        this.hrRequiredForISOH = builder.comment("Whether Heavenly Restriction is required to use the Inverted Spear of Heaven")
                .define("hrRequiredForISOH", false);
        this.playerRequiredForRCT = builder.comment("Whether Players must kill you in order for you to unlock RCT")
                .define("playerRequiredForRCT", false);
        builder.pop();
    

        builder.comment("Miscellaneous").push("misc");
        this.sorcererHealingAmount = builder.comment("The maximum amount of health sorcerers can heal per tick (scales with experience)")
                .defineInRange("sorcererHealingAmount", 0.1F, 0.0F, 2.5F);
        this.curseHealingAmount = builder.comment("The maximum amount of health curses can heal per tick (scales with experience)")
                .defineInRange("curseHealingAmount", 0.15F, 0.0F, 2.5F);
        this.uniqueTechniques = builder.comment("When enabled on servers every player will have a unique technique if any are available")
                .define("uniqueTechniques", true);
        this.uniqueTraits = builder.comment("When enabled on servers there can be only one six eyes, heavenly restriction and vessel")
                .define("uniqueTraits", true);
        this.destruction = builder.comment("When enabled abilities break blocks")
                .define("destruction", true);
        this.turboMode = builder.comment("When enabled abilities have no cooldowns for players")
                .define("turboMode", false);
        this.entitySlicing = builder.comment("When enabled entities are sliced by Dismantle (may cause shader/mod incompat)")
                .define("entitySlicing", false);
        this.chantRequiredForWCS = builder.comment("When enabled WCS must be chanted to 150%")
                .define("chantRequiredForWCS", true);
        builder.pop();

        builder.comment("Veils").push("veils");
        this.minimumVeilSize = builder.comment("Minimum size for a veil")
                .defineInRange("minimumVeilSize", 4, 4, 64);
        this.maximumVeilSize = builder.comment("Maximum size for a veil")
                .defineInRange("maximumVeilSize", 64, 32, 256);
        builder.pop();

        builder.comment("Domains").push("domains");
        this.minimumDomainSize = builder.comment("Minimum size for a domain")
                .defineInRange("minimumDomainSize", 0.5F, 0.2F, 1.0F);
        this.maximumDomainSize = builder.comment("Maximum size for a domain")
                .defineInRange("maximumDomainSize", 1.5F, 1.0F, 10.0F);
        builder.pop();

        builder.comment("Chants").push("chants");
        this.maximumChantCount = builder.comment("Maximum count for chants")
                .defineInRange("maximumChantCount", 5, 1, 16);
        this.maximumChantLength = builder.comment("Maximum length for a chant")
                .defineInRange("maximumChantLength", 24, 1, 256);
        this.chantSimilarityThreshold = builder.comment("Minimum difference between chants for them to be valid")
                .defineInRange("chantSimilarityThreshold", 0.25F, 0.0F, 1.0F);
        builder.pop();

        builder.comment("Abilities").push("abilities");
        this.simpleDomainCost = builder.comment("The amount of points simple domain costs to unlock")
                .defineInRange("simpleDomainCost", 50, 1, 10000);
        this.simpleDomainEnlargementCost = builder.comment("The amount of points simple domain enlargement costs to unlock")
                .defineInRange("simpleDomainEnlargementCost", 100, 1, 10000);
        this.quickDrawCost = builder.comment("The amount of points quick draw costs to unlock")
                .defineInRange("simpleDomainCost", 50, 1, 10000);
        this.fallingBlossomEmotionCost = builder.comment("The amount of points falling blossom emotion costs to unlock")
                .defineInRange("fallingBlossomEmotionCost", 50, 1, 10000);
        this.hollowWickerBasketCost = builder.comment("The amount of points hollow wicker basket costs to unlock")
                .defineInRange("hollowWickerBasketCost", 50, 1, 10000);
        this.domainExpansionCost = builder.comment("The amount of points domain expansion costs to unlock")
                .defineInRange("domainExpansionCost", 200, 1, 10000);
        this.domainAmplificationCost = builder.comment("The amount of points domain amplification costs to unlock")
                .defineInRange("domainAmplificationCost", 100, 1, 10000);
        this.zeroPointTwoSecondDomainExpansionCost = builder.comment("The amount of points 0.2s domain expasnion costs to unlock")
                .defineInRange("zeroPointTwoSecondDomainExpansionCost", 100, 1, 10000);
        this.miniUzumakiCost = builder.comment("The amount of points Mini Uzumaki costs to unlock")
                .defineInRange("miniUzumakiCost", 50, 1, 10000);
        this.maximumUzumakiCost = builder.comment("The amount of points Maximum: Uzumaki costs to unlock")
                .defineInRange("maximumUzumakiCost", 100, 1, 10000);
        this.maximumMeteorCost = builder.comment("The amount of points Maximum: Meteor costs to unlock")
                .defineInRange("maximumMeteorCost", 100, 1, 10000);
        this.ceBombCost = builder.comment("The amount of points Cursed Energy Bomb costs to unlock")
                .defineInRange("ceBombCost", 50, 1, 10000);
        this.ceBlastCost = builder.comment("The amount of points Cursed Energy Blast costs to unlock")
                .defineInRange("ceBlastCost", 50, 1, 10000);
        this.rct2Cost = builder.comment("The amount of points tier 2 RCT costs to unlock")
                .defineInRange("rct2Cost", 100, 1, 10000);
        this.rct3Cost = builder.comment("The amount of points tier 3 RCT costs to unlock")
                .defineInRange("rct3Cost", 200, 1, 10000);
        this.outputRCTCost = builder.comment("The amount of points output RCT costs to unlock")
                .defineInRange("outputRCTCost", 300, 1, 10000);
        this.maximumCopiedTechniques = builder.comment("The amount of techniques mimicry can copy")
                .defineInRange("maximumCopiedTechniques", 3, 1, 10000);
        this.maximumStolenTechniques = builder.comment("The amount of techniques that can be stolen")
                .defineInRange("maximumStolenTechniques", 2, 1, 10000);
        this.unlockableTechniques = builder.comment("Techniques that are unlockable by default")
                .defineList("unlockableTechniques", () -> List.of(
                                CursedTechnique.CURSE_MANIPULATION.name(),
                                CursedTechnique.LIMITLESS.name(),
                                CursedTechnique.SHRINE.name(),
                                CursedTechnique.CURSED_SPEECH.name(),
                                CursedTechnique.MIMICRY.name(),
                                CursedTechnique.DISASTER_FLAMES.name(),
                                CursedTechnique.DISASTER_TIDES.name(),
                                CursedTechnique.DISASTER_PLANTS.name(),
                                CursedTechnique.ANGEL.name(),
                                CursedTechnique.BRAIN_TRANSPLANT.name(),
                                CursedTechnique.IDLE_TRANSFIGURATION.name(),
                                CursedTechnique.TEN_SHADOWS.name(),
                                CursedTechnique.BOOGIE_WOOGIE.name(),
                                CursedTechnique.PROJECTION_SORCERY.name(),
                                CursedTechnique.RATIO.name(),
                                CursedTechnique.MYTHICAL_BEAST_AMBER.name(),
                                CursedTechnique.TECHNIQUELESS.name()
                        ),
                        ignored -> true
                );
        builder.pop();

        builder.comment("Rarity").push("rarity");
        this.cursedEnergyNatureRarity = builder.comment("Rarity of a cursed energy nature other than basic (bigger value = rarer)")
                .defineInRange("cursedEnergyNatureRarity", 5, 1, 1000000);
        this.curseRarity = builder.comment("Rarity of being a curse (bigger value = rarer)")
                .defineInRange("curseRarity", 10, 1, 1000000);
        this.sixEyesRarity = builder.comment("Rarity of having six eyes (bigger value = rarer)")
                .defineInRange("sixEyesRarity", 1, 1, 1000000);
        this.heavenlyRestrictionRarity = builder.comment("Rarity of heavenly restriction (bigger value = rarer)")
                .defineInRange("heavenlyRestrictionRarity", 10, 1, 1000000);
        this.vesselRarity = builder.comment("Rarity of being a vessel (bigger value = rarer)")
                .defineInRange("vesselRarity", 3, 1, 1000000);
        this.perfectBodyRarity = builder.comment("Rarity of having a perfect body (bigger value = rarer)")
                .defineInRange("perfectBodyRarity", 1, 1, 1000000);
        this.incarnatedRarity = builder.comment("Rarity of being incarnated (bigger value = rarer)")
                .defineInRange("incarnatedRarity", 10, 1, 1000000);
        this.rctOutputRarity = builder.comment("Rarity of being adept at RCT (bigger value = rarer)")
                .defineInRange("rctOutputRarity", 5, 1, 1000000);
        builder.pop();
    }

    public List<CursedTechnique> getUnlockableTechniques() {
        return this.unlockableTechniques.get().stream()
                .map(CursedTechnique::valueOf)
                .collect(Collectors.toList());
    }
}
