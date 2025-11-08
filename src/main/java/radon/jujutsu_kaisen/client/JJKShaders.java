package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import radon.jujutsu_kaisen.JujutsuKaisen;

import java.io.IOException;


@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD , value = Dist.CLIENT)
public class JJKShaders {
    private static ShaderInstance domainShader;
    private static ShaderInstance skyShader;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                        new ResourceLocation(JujutsuKaisen.MOD_ID, "domain"), DefaultVertexFormat.POSITION_TEX),
                shader -> domainShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                        new ResourceLocation(JujutsuKaisen.MOD_ID, "sky"), DefaultVertexFormat.POSITION_TEX),
                shader -> skyShader = shader);
    }

    public static ShaderInstance getDomainShader() {
        return domainShader;
    }

    public static ShaderInstance getSkyShader() {
        return skyShader;
    }
}
