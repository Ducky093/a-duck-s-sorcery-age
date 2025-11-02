package radon.jujutsu_kaisen.config;

import net.minecraftforge.common.ForgeConfigSpec;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;

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
    public final ForgeConfigSpec.IntValue totemRCTChanceMult;
    public final ForgeConfigSpec.DoubleValue requiredExperienceForExperienced;
    public final ForgeConfigSpec.DoubleValue domainExpReq;
    public final ForgeConfigSpec.DoubleValue wcsExpMahoReq;
    public final ForgeConfigSpec.DoubleValue wcsExpOtherReq;
    public final ForgeConfigSpec.IntValue sorcererFleshRarity;
    public final ForgeConfigSpec.IntValue curseFleshRarity;
    public final ForgeConfigSpec.IntValue sorcererVillageSpawnRate;
    public final ForgeConfigSpec.IntValue curseVillageSpawnRate;
    public final ForgeConfigSpec.IntValue displayCaseSpawnRate;
    public final ForgeConfigSpec.IntValue displayCaseSpawnRange;
    public final ForgeConfigSpec.IntValue disasterCurseSpawnRate;
    public final ForgeConfigSpec.DoubleValue pointMultiplier;
    public final ForgeConfigSpec.DoubleValue experienceMultiplier;
    public final ForgeConfigSpec.DoubleValue minimumBodyStealEXP;
    public final ForgeConfigSpec.DoubleValue deathPenalty;
    public final ForgeConfigSpec.DoubleValue pointPenalty;
    public final ForgeConfigSpec.DoubleValue pvpGain;
    public final ForgeConfigSpec.IntValue minPoints;
    public final ForgeConfigSpec.IntValue maxPoints;
    public final ForgeConfigSpec.DoubleValue minEXP;
    public final ForgeConfigSpec.DoubleValue maxEXP;
    public final ForgeConfigSpec.IntValue blackFlashChanceRNG;
    public final ForgeConfigSpec.DoubleValue blackFlashPower;
    public final ForgeConfigSpec.DoubleValue blackFlashDmgCap;
    public final ForgeConfigSpec.BooleanValue incarnatedSimpleDomain;
    public final ForgeConfigSpec.BooleanValue realisticShikigami;
    public final ForgeConfigSpec.BooleanValue realisticCurses;
    public final ForgeConfigSpec.BooleanValue sorcererSaturation;
    public final ForgeConfigSpec.BooleanValue curseSaturation;
    public final ForgeConfigSpec.BooleanValue foodCERegen;
    public final ForgeConfigSpec.BooleanValue playerMimicry;
    public final ForgeConfigSpec.BooleanValue playerBodySteal;
    public final ForgeConfigSpec.BooleanValue bodyStealEXPReset;
    public final ForgeConfigSpec.BooleanValue mimicryBodyStealCompat;
    public final ForgeConfigSpec.BooleanValue bodyStealTraits;
    public final ForgeConfigSpec.BooleanValue bodyStealReroll;
    public final ForgeConfigSpec.BooleanValue MBAReroll;
    public final ForgeConfigSpec.BooleanValue MBAEXPReset;
    public final ForgeConfigSpec.BooleanValue MBADeath;
    public final ForgeConfigSpec.BooleanValue wcsCutAnything;
    public final ForgeConfigSpec.BooleanValue hrRequiredForISOH;
    public final ForgeConfigSpec.BooleanValue playerRequiredForRCT;
    public final ForgeConfigSpec.BooleanValue playerRequiredForGradeUp;

    public final ForgeConfigSpec.DoubleValue sorcererHealingAmount;
    public final ForgeConfigSpec.DoubleValue curseHealingAmount;
    public final ForgeConfigSpec.DoubleValue curseDamageMult;
    public final ForgeConfigSpec.DoubleValue curseDefenseMult;
    public final ForgeConfigSpec.DoubleValue sorcererDamageMult;
    public final ForgeConfigSpec.DoubleValue sorcererDefenseMult;
    public final ForgeConfigSpec.DoubleValue jujutsuDefenseMult;
    public final ForgeConfigSpec.DoubleValue hrDefenseMult;
    public final ForgeConfigSpec.DoubleValue limitlessNoSixEyesMult;
    public final ForgeConfigSpec.DoubleValue sixEyesMult;




    public final ForgeConfigSpec.BooleanValue uniqueTechniques;
    public final ForgeConfigSpec.BooleanValue uniqueTraits;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> uniqueTraitList;
    public final ForgeConfigSpec.BooleanValue destruction;
    public final ForgeConfigSpec.BooleanValue turboMode;
    public final ForgeConfigSpec.BooleanValue entitySlicing;
    public final ForgeConfigSpec.BooleanValue chantRequiredForWCS;

    public final ForgeConfigSpec.IntValue minimumVeilSize;
    public final ForgeConfigSpec.IntValue maximumVeilSize;
    public final ForgeConfigSpec.DoubleValue minimumDomainSize;
    public final ForgeConfigSpec.DoubleValue maximumDomainSize;

    public final ForgeConfigSpec.IntValue maximumChantCount;
    public final ForgeConfigSpec.IntValue minimumChantLength;
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
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> unlockableSorcererTechniques;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> unlockableCursedSpiritTechniques;

    public final ForgeConfigSpec.IntValue natureTraitModifier;
    public final ForgeConfigSpec.IntValue traitScalingModifier;
    public final ForgeConfigSpec.IntValue natureTraitCost;
    public final ForgeConfigSpec.IntValue traitRolls;
    public final ForgeConfigSpec.IntValue minTraits;
    public final ForgeConfigSpec.IntValue maxTraits;
    public final ForgeConfigSpec.IntValue noTraitWeight;
    public final ForgeConfigSpec.IntValue cursedEnergyNatureRarity;
    public final ForgeConfigSpec.IntValue curseRarity;
    public final ForgeConfigSpec.IntValue sixEyesWeight;
    public final ForgeConfigSpec.IntValue heavenlyRestrictionRarity;
    public final ForgeConfigSpec.IntValue vesselWeight;
    public final ForgeConfigSpec.IntValue rctOutputWeight;
    public final ForgeConfigSpec.IntValue perfectBodyWeight;
    public final ForgeConfigSpec.IntValue incarnatedWeight;
    public final ForgeConfigSpec.IntValue prodigyWeight;
    public final ForgeConfigSpec.IntValue cursedWombWeight;
    public final ForgeConfigSpec.IntValue deathPaintingWeight;

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
        this.reverseCursedTechniqueChance = builder.comment("The chance of unlocking reverse cursed technique when dying (smaller number equals bigger chance)")
                .defineInRange("reverseCursedTechniqueChance", 20, 1, 1000);
        this.totemRCTChanceMult = builder.comment("The amount the chance is divided by when holding a totem (raises chances of obtaining rct the higher it is) ")
                .defineInRange("totemRCTChanceMult", 4, 1, 1000);
        this.requiredExperienceForExperienced = builder.comment("The amount of experience required for a player to be classified as experienced (for now means they can use domain amplification during a domain expansion/wheel w domain amp)")
                .defineInRange("requiredExperienceForExperienced", 5000.0F, 1.0F, 1000000.0F);
        this.domainExpReq = builder.comment("Experience required to learn a Domain Expansion")
                .defineInRange("domainExpReq", 3000.0F, 0.0F, 1000000.0F);
        this.wcsExpMahoReq = builder.comment("Experience required to learn WCS from Mahoraga")
                .defineInRange("wcsExpMahoReq", 10000.0F, 0.0F, 1000000.0F);
        this.wcsExpOtherReq = builder.comment("Experience required to learn WCS from other sources")
                .defineInRange("wcsExpOtherReq", 10000.0F, 0.0F, 1000000.0F);
        this.sorcererFleshRarity = builder.comment("Rarity of sorcerers dropping flesh (bigger value means more rare, 0 to disable)")
                .defineInRange("sorcererFleshRarity", 20, 0, 100000);
        this.curseFleshRarity = builder.comment("Rarity of curses dropping flesh (bigger value means more rare, 0 to disable)")
                .defineInRange("curseFleshRarity", 20, 0, 100000);
        this.curseVillageSpawnRate = builder.comment("Rarity of curses spawning in villages (bigger value means more rare, 0 to disable)")
                .defineInRange("curseVillageSpawnRate", 8, 0, 100000);
        this.sorcererVillageSpawnRate = builder.comment("Rarity of sorcerers spawning in villages (bigger value means more rare, 0 to disable)")
                .defineInRange("sorcererVillageSpawnRate", 6, 0, 100000);
        this.displayCaseSpawnRate = builder.comment("Rarity of curses spawning from display cases (bigger value means more rare, 0 to disable)")
                .defineInRange("displayCaseRarity", 8, 0, 100000);
        this.displayCaseSpawnRange = builder.comment("Range in chunks curses can spawn from display cases (bigger value means more rare, 0 to disable)")
                .defineInRange("displayCaseSpawnRange", 8, 0, 100000);
        this.disasterCurseSpawnRate = builder.comment("Rarity of disaster curses (bigger value means more rare, 0 to disable)")
                .defineInRange("disasterCurseSpawnRate", 12, 0, 100000);
        this.experienceMultiplier = builder.comment("Scale of experience you gain")
                .defineInRange("experienceMultiplier", 1.0F, 0.0F, 100.0F);
        this.minimumBodyStealEXP = builder.comment("Minimum EXP before a body can be stolen")
                .defineInRange("minimumBodyStealEXP", 0.0F, 0.0F, 100000.0F);
        this.pointMultiplier = builder.comment("Scale of ability points you gain")
                .defineInRange("pointMultiplier", 0.12F, 0.0F, 100.0F);
        this.pointPenalty = builder.comment("Scale of points lost on death")
                .defineInRange("pointPenalty", 0.0F, 0.0F, 100.0F);
        this.deathPenalty = builder.comment("Percentage of experience lost on death")
                .defineInRange("deathPenalty", 0.0F, 0.0F, 1.0F);
        this.minPoints = builder.comment("Minimum points gained from battles")
                .defineInRange("minPoints", 1, 0, 9999999);
        this.maxPoints = builder.comment("Maximum points gained from battles")
                .defineInRange("maxPoints", 0, 0, 9999999);
        this.minEXP = builder.comment("Minimum experience gained from battles")
                .defineInRange("minExp", 1.0F, 0.0F, 9999999.0F);
        this.maxEXP = builder.comment("Maximum experience gained from battles (0 to disable)")
                .defineInRange("maxExp", 0.0F, 0.0F, 9999999.0F);
        this.pvpGain = builder.comment("Percentage of experience gained from player kills")
                .defineInRange("pvpGain", 1.0F, 0.0F, 999.0F);
        this.blackFlashChanceRNG = builder.comment("The chance of black flash (smaller number equals bigger chance)")
                .defineInRange("blackFlashChanceRNG", 200, 0, 1000);
        this.blackFlashPower = builder.comment("The multiplier a black flash ('power of' multiplier to bfs, canon by default)")
                .defineInRange("blackFlashPower", 2.5F, 1.0F, 1000.0F);
        this.blackFlashDmgCap = builder.comment("The maximum damage of a black flash attack (entire dmg not just the added dmg)")
                .defineInRange("blackFlashDmgCap", 40.0F, 0.0F, 999999.0F);
        this.incarnatedSimpleDomain = builder.comment("When enabled Incarnated Sorcerers may use Simple Domain")
                .define("incarnatedSimpleDomain", true);
        this.realisticShikigami = builder.comment("When enabled Ten Shadows shikigami will die permanently")
                .define("realisticShikigami", false);
        this.realisticCurses = builder.comment("When enabled curses only take damage from jujutsu attacks")
                .define("realisticCurses", true);
        this.sorcererSaturation = builder.comment("When enabled Sorcerers will always have their hunger filled.")
                .define("sorcererSaturation", true);
        this.curseSaturation = builder.comment("When enabled Curses will always have their hunger filled.")
                .define("curseSaturation", true);
        this.foodCERegen = builder.comment("When enabled Cursed Energy regeneration speed will scale off hunger.")
                .define("foodCERegen", true);
        this.playerBodySteal = builder.comment("When enabled Body Steal only works on players")
                .define("playerBodySteal", false);
        this.playerMimicry = builder.comment("When enabled Mimicry only works on players")
                .define("playerMimicry", false);
        this.mimicryBodyStealCompat = builder.comment("When enabled Mimicry and Body Steal may steal from each other.")
                .define("mimicryBodyStealCompat", true);
        this.bodyStealTraits = builder.comment("Whether Body Steal should steal traits, besides RCT Output and Heavenly Restriction.")
                .define("bodyStealTraits", true);
        this.bodyStealEXPReset = builder.comment("Whether Body Steal should reset the EXP of the stolen player")
                .define("bodyStealEXPReset", true);
        this.bodyStealReroll = builder.comment("Whether Body Steal should reroll the player.")
                .define("bodyStealReroll", false);
        this.MBADeath = builder.comment("Whether Mythical Beast Amber should kill the user after use")
                .define("MBADeath", true);
        this.MBAEXPReset = builder.comment("Whether Mythical Beast Amber should reset the EXP of the user after use")
                .define("MBAEXPReset", true);
        this.MBAReroll = builder.comment("Whether Mythical Beast Amber should reroll the player.")
                .define("MBAReroll", false);
        this.wcsCutAnything = builder.comment("Whether World Cutting Slash truly cuts the world (destroys indestructible blocks).")
                .define("wcsCutAnything", true);
        this.hrRequiredForISOH = builder.comment("Whether Heavenly Restriction is required to use the Inverted Spear of Heaven")
                .define("hrRequiredForISOH", false);
        this.playerRequiredForRCT = builder.comment("Whether Players must kill you in order for you to unlock RCT")
                .define("playerRequiredForRCT", false);
        this.playerRequiredForGradeUp = builder.comment("Whether Players must kill other players in order to Rank Up ")
                .define("playerRequiredForGradeUp", false);
        builder.pop();
    

        builder.comment("Miscellaneous").push("misc");
        this.sorcererHealingAmount = builder.comment("The maximum amount of health sorcerers can heal per tick (scales with experience)")
                .defineInRange("sorcererHealingAmount", 0.1F, 0.0F, 2.5F);
        this.curseHealingAmount = builder.comment("The maximum amount of health curses can heal per tick (scales with experience)")
                .defineInRange("curseHealingAmount", 0.15F, 0.0F, 2.5F);
        this.curseDamageMult = builder.comment("The multiplier on the damage NPC curses deal to you")
                .defineInRange("curseDamageMult", 0.7F, 0.0F, 9999.0F);
        this.curseDefenseMult = builder.comment("The multiplier on damage NPC curses take from you")
                .defineInRange("curseDefenseMult", 1.0F, 0.0F, 9999.0F);
        this.sorcererDamageMult = builder.comment("The multiplier on the damage NPC sorcerers deal to you")
                .defineInRange("sorcererDamageMult", 0.7F, 0.0F, 9999.0F);
        this.sorcererDefenseMult = builder.comment("The multiplier on damage NPC sorcerers take from you")
                .defineInRange("sorcererDefenseMult", 1.0F, 0.0F, 9999.0F);
        this.jujutsuDefenseMult = builder.comment("The multiplier to standard players' defense")
                .defineInRange("jujutsuDefenseMult", 1.0F, 0.0F, 9999.0F);
        this.hrDefenseMult = builder.comment("The multiplier to Heavenly Restriction players's defense (already higher outside of config)")
                .defineInRange("hrDefenseMult", 1.0F, 0.0F, 9999.0F);
        this.limitlessNoSixEyesMult = builder.comment("The multiplier to Limitless's costs without Six Eyes")
                .defineInRange("limitlessNoSixEyesMult", 1.0F, 0.0F, 9999.0F);
        this.sixEyesMult = builder.comment("The multiplier of drain decreases given to Six Eyes.")
                .defineInRange("sixEyesMult", 0.5F, 0.0F, 9999.0F);

        this.uniqueTechniques = builder.comment("When enabled on servers every player will have a unique technique if any are available")
                .define("uniqueTechniques", true);
        this.uniqueTraits = builder.comment("When enabled on servers there can be only one six eyes, heavenly restriction and vessel")
                .define("uniqueTraits", true);
        this.uniqueTraitList = builder.comment("Traits that will be Unique under the config")
                .defineList("uniqueTraitList", () -> List.of(
                                Trait.SIX_EYES.name(),
                                Trait.PERFECT_BODY.name()
                        ), ignored -> true);
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
        this.minimumChantLength = builder.comment("Maximum length for a chant")
                .defineInRange("minimumChantLength", 2, 1, 256);
        this.maximumChantLength = builder.comment("Maximum length for a chant")
                .defineInRange("maximumChantLength", 24, 1, 256);
        this.chantSimilarityThreshold = builder.comment("Minimum difference between chants for them to be valid")
                .defineInRange("chantSimilarityThreshold", 0.25F, 0.0F, 1.0F);
        builder.pop();

        builder.comment("Abilities").push("abilities");
        this.simpleDomainCost = builder.comment("The amount of points simple domain costs to unlock")
                .defineInRange("simpleDomainCost", 25, 1, 10000);
        this.simpleDomainEnlargementCost = builder.comment("The amount of points simple domain enlargement costs to unlock")
                .defineInRange("simpleDomainEnlargementCost", 50, 1, 10000);
        this.quickDrawCost = builder.comment("The amount of points quick draw costs to unlock")
                .defineInRange("simpleDomainCost", 25, 1, 10000);
        this.fallingBlossomEmotionCost = builder.comment("The amount of points falling blossom emotion costs to unlock")
                .defineInRange("fallingBlossomEmotionCost", 25, 1, 10000);
        this.hollowWickerBasketCost = builder.comment("The amount of points hollow wicker basket costs to unlock")
                .defineInRange("hollowWickerBasketCost", 25, 1, 10000);
        this.domainExpansionCost = builder.comment("The amount of points domain expansion costs to unlock")
                .defineInRange("domainExpansionCost", 200, 1, 10000);
        this.domainAmplificationCost = builder.comment("The amount of points domain amplification costs to unlock")
                .defineInRange("domainAmplificationCost", 50, 1, 10000);
        this.zeroPointTwoSecondDomainExpansionCost = builder.comment("The amount of points 0.2s domain expasnion costs to unlock")
                .defineInRange("zeroPointTwoSecondDomainExpansionCost", 75, 1, 10000);
        this.miniUzumakiCost = builder.comment("The amount of points Mini Uzumaki costs to unlock")
                .defineInRange("miniUzumakiCost", 50, 1, 10000);
        this.maximumUzumakiCost = builder.comment("The amount of points Maximum: Uzumaki costs to unlock")
                .defineInRange("maximumUzumakiCost", 75, 1, 10000);
        this.maximumMeteorCost = builder.comment("The amount of points Maximum: Meteor costs to unlock")
                .defineInRange("maximumMeteorCost", 100, 1, 10000);
        this.ceBombCost = builder.comment("The amount of points Cursed Energy Bomb costs to unlock")
                .defineInRange("ceBombCost", 25, 1, 10000);
        this.ceBlastCost = builder.comment("The amount of points Cursed Energy Blast costs to unlock")
                .defineInRange("ceBlastCost", 25, 1, 10000);
        this.rct2Cost = builder.comment("The amount of points tier 2 RCT costs to unlock")
                .defineInRange("rct2Cost", 50, 1, 10000);
        this.rct3Cost = builder.comment("The amount of points tier 3 RCT costs to unlock")
                .defineInRange("rct3Cost", 100, 1, 10000);
        this.outputRCTCost = builder.comment("The amount of points output RCT costs to unlock")
                .defineInRange("outputRCTCost", 75, 1, 10000);
        this.maximumCopiedTechniques = builder.comment("The amount of techniques mimicry can copy")
                .defineInRange("maximumCopiedTechniques", 3, 1, 10000);
        this.maximumStolenTechniques = builder.comment("The amount of techniques that can be stolen")
                .defineInRange("maximumStolenTechniques", 2, 1, 10000);
        this.unlockableSorcererTechniques = builder.comment("Techniques that are unlockable for sorcerers by default")
                .defineList("unlockableSorcererTechniques", () -> List.of(
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
                                CursedTechnique.TEN_SHADOWS.name(),
                                CursedTechnique.BOOGIE_WOOGIE.name(),
                                CursedTechnique.PROJECTION_SORCERY.name(),
                                CursedTechnique.RATIO.name(),
                                CursedTechnique.MYTHICAL_BEAST_AMBER.name(),
                                CursedTechnique.TECHNIQUELESS.name()
                        ),
                        ignored -> true
                );

        this.unlockableCursedSpiritTechniques = builder.comment("Techniques that are unlockable by Curses by default")
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
        this.natureTraitCost = builder.comment("Cursed Energy Nature trait count cost (affected by the minimum/max amount of traits)")
                .defineInRange("natureTraitCost", 1 ,0, 1000000);
        this.traitRolls = builder.comment("How many rolls are done to give players traits? (each minimum guaranteed trait takes a trait roll)")
                .defineInRange("traitRolls", 1, 0, 1000000);
        this.minTraits = builder.comment("The minimum amount of traits a player will start with")
                .defineInRange("minTraits", 0 ,0, 1000000);
        this.maxTraits = builder.comment("The maximum amount of traits a player can start with")
                .defineInRange("maxTraits", 6 ,0, 1000000);
        this.natureTraitModifier = builder.comment("The division to the chance of rolling a trait with a nature")
                .defineInRange("natureTraitModifier", 4 ,0, 1000000);
        this.traitScalingModifier = builder.comment("The division to the chance of rolling an additional trait per trait rolled")
                .defineInRange("traitScalingModifier", 8 ,0, 1000000);
        this.noTraitWeight = builder.comment("Weight of receiving no trait")
                .defineInRange("noTraitWeight", 30, 0, 1000000);
        this.cursedEnergyNatureRarity = builder.comment("Weight of a cursed energy nature other than basic (1/value chance, bigger value = rarer)")
                .defineInRange("cursedEnergyNatureRarity", 8, 0, 1000000);
        this.curseRarity = builder.comment("Rarity of being a curse (1/value chance, bigger value = rarer")
                .defineInRange("curseRarity", 4, 0, 1000000);
        this.sixEyesWeight = builder.comment("Weight of having six eyes (lower value = rarer)")
                .defineInRange("sixEyesWeight", 1, 0, 1000000);
        this.heavenlyRestrictionRarity = builder.comment("Rarity of heavenly restriction (1/value chance, bigger value = rarer")
                .defineInRange("heavenlyRestrictionRarity", 24, 0, 1000000);
        this.vesselWeight = builder.comment("Weight of being a vessel (lower value = rarer)")
                .defineInRange("vesselWeight", 3, 0, 1000000);
        this.perfectBodyWeight = builder.comment("Weight of having a perfect body (lower value = rarer)")
                .defineInRange("perfectBodyWeight", 1, 0, 1000000);
        this.incarnatedWeight = builder.comment("Weight of being incarnated (lower value = rarer)")
                .defineInRange("incarnatedWeight", 15, 0, 1000000);
        this.rctOutputWeight = builder.comment("Weight of being able to output your RCT (lower value = rarer)")
                .defineInRange("rctOutputWeight", 10, 0, 1000000);
        this.prodigyWeight = builder.comment("Weight of having immense development potential (lower value = rarer)")
                .defineInRange("prodigyWeight", 4, 0, 1000000);
        this.cursedWombWeight = builder.comment("Weight of forming as a Cursed Womb (lower value = rarer)")
                .defineInRange("cursedWombWeight", 10, 0, 1000000);
        this.deathPaintingWeight = builder.comment("Weight of having been born as a Death Painting (lower value = rarer)")
                .defineInRange("deathPaintingWeight", 8, 0, 1000000);
        builder.pop();
    }

    public List<CursedTechnique> getUnlockableTechniques(JujutsuType type) {
        if (type == JujutsuType.SORCERER) {
                return this.unlockableSorcererTechniques.get().stream()
                .map(CursedTechnique::valueOf)
                .collect(Collectors.toList());
        }
        else {
                return this.unlockableCursedSpiritTechniques.get().stream()
                .map(CursedTechnique::valueOf)
                .collect(Collectors.toList());
        }
        
        
    }
}
