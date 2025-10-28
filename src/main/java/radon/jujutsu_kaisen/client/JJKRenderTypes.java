package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import cpw.mods.modlauncher.api.ITransformationService.Resource;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.client.render.block.SkyRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

public class JJKRenderTypes extends RenderType {
    private static final Map<ResourceLocation, TextureTarget> cached = new HashMap<>();

     private static final Function<ResourceLocation, RenderType> HOLLOW_WICKER_BASKET = Util.memoize((pLocation) ->
            create("hollow_wicker_basket", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, CompositeState.builder()
                  .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
           .setTextureState(new RenderStateShard.TextureStateShard(pLocation, false, false))
           .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
           .setCullState(RenderStateShard.NO_CULL)           // render both sides
           .setLightmapState(RenderStateShard.LIGHTMAP)
           .setOverlayState(RenderStateShard.NO_OVERLAY)
           .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)      // important for water blending
           .setWriteMaskState(RenderStateShard.COLOR_WRITE) // write color & depth
           .createCompositeState(false)
            ));
    private static final Function<ResourceLocation, RenderType> GLOW = Util.memoize((pLocation) ->
            create("glow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                    .setTextureState(new TextureStateShard(pLocation, false, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> ENERGY = Util.memoize((pLocation) ->
            create("energy", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(pLocation, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setOverlayState(OVERLAY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> EYES = Util.memoize((pLocation) ->
            create("six_eyes", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder()
                    .setShaderState(RENDERTYPE_EYES_SHADER)
                    .setTextureState(new TextureStateShard(pLocation, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false)));
    private static final RenderType UNLIMITED_VOID = create("unlimited_void", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256,
            false, false, RenderType.CompositeState.builder()
                    .setShaderState(new ShaderStateShard(JJKShaders::getUnlimitedVoidShader))
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
                            .add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build())
                    .createCompositeState(false));
    private static final RenderType SKY = create("sky", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256,
            false, false, RenderType.CompositeState.builder()
                    .setShaderState(new ShaderStateShard(JJKShaders::getSkyShader))
                    .setTextureState(new EmptyTextureStateShard(() -> {
                        TextureTarget target = SkyHandler.getTarget();

                        if (target != null) {
                            RenderSystem.setShaderTexture(0, target.getColorTextureId());
                        } else {
                            RenderSystem.setShaderTexture(0, 0);
                        }
                    }, () -> {}))
                    .createCompositeState(false));
    private static final RenderType LIGHTNING = create("lightning", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256,
            false, true, RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .createCompositeState(false));

    public JJKRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static RenderType hollow_wicker_basket(ResourceLocation pLocation) {
        return HOLLOW_WICKER_BASKET.apply(pLocation);
    }

    public static RenderType glow(ResourceLocation pLocation) {
        return GLOW.apply(pLocation);
    }

    public static RenderType energy(ResourceLocation pLocation) {
        return ENERGY.apply(pLocation);
    }

    public static @NotNull RenderType eyes(@NotNull ResourceLocation pLocation) {
        return EYES.apply(pLocation);
    }

    public static RenderType unlimitedVoid() {
        return UNLIMITED_VOID;
    }

    public static RenderType sky() {
        return SKY;
    }

    public static @NotNull RenderType lightning() {
        return LIGHTNING;
    }

    @Nullable
    private static TextureTarget fallback;

    public static TextureTarget get(ResourceLocation domain) {
        if (!cached.containsKey(domain)) {
            return fallback;
        }
        return cached.get(domain);
    }
}