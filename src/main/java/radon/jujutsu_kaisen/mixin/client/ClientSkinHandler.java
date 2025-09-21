package radon.jujutsu_kaisen.mixin.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;

public class ClientSkinHandler {
    public static void handleSkinSync(ISorcererData oldCap, ISorcererData newCap) {
        GameProfile profile = newCap.getStolenSkinProfile();
        if (profile == null) return;

        SkinManager skinManager = Minecraft.getInstance().getSkinManager();
        skinManager.registerSkins(profile, (type, location, profileTexture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN) {
                oldCap.setStolenSkinTexture(location);
            }
        }, true);
    }
}
