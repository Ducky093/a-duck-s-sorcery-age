package radon.jujutsu_kaisen.capability.data.sorcerer;

import java.util.Locale;

import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.config.ConfigHolder;

public enum DomainConfiguration {
    SIZE(ValueType.FLOAT, 1.0f, ConfigHolder.SERVER.minimumDomainSize.get().floatValue(), ConfigHolder.SERVER.maximumDomainSize.get().floatValue()),
    SHELL_BALANCE(ValueType.BOOLEAN, false),
    SURE_HIT_TOGGLE(ValueType.BOOLEAN, false),
    SURE_HIT_ALLIES(ValueType.BOOLEAN, false),
    OPEN_BARRIER(ValueType.BOOLEAN, true);


    private final ValueType type;
    private final Object defaultValue;
    private final Object minimumValue;
    private final Object maximumValue;

    DomainConfiguration(ValueType type, Object defaultValue, Object minimumValue, Object maximumValue) {
        this.type = type;
        this.defaultValue = defaultValue;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    DomainConfiguration(ValueType type, Object defaultValue) {
        this(type, defaultValue, null, null);
    }

    public ValueType getType() {
        return type;
    }

    public <T> T 
    getDefault() {
        return (T) defaultValue;
    }

    public Object getMinimumValue() {
        return minimumValue;
    }

    public Object getMaximumValue() {
        return maximumValue;
    }

    public String getRawName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public Component getName() {
        return Component.translatable("domain_configuration.%s.%s".formatted(
                JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }

    public Component getDescription() {
        return Component.translatable("domain_configuration.%s.%s.description".formatted(
                JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }
}
