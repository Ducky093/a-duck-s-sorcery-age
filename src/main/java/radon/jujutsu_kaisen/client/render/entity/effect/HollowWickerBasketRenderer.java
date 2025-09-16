package radon.jujutsu_kaisen.client.render.entity.effect;

import java.nio.channels.OverlappingFileLockException;

import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.HollowWickerBasketEntity;
import radon.jujutsu_kaisen.entity.effect.WaterballEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class HollowWickerBasketRenderer extends EntityRenderer<HollowWickerBasketEntity> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/hollow_wicker_basket.png");

    public HollowWickerBasketRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(HollowWickerBasketEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
     poseStack.pushPose();

        float radius = 1.25F; 
        int slices = 64;
        int stacks = 64;

        poseStack.scale(1f, 1f, 1f);
        poseStack.translate(0, entity.getEyeHeight(), 0);
        Matrix4f poseMatrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        for (int stack = 0; stack < stacks; stack++) {
            double lat0 = Math.PI * ((double) stack / stacks - 0.5);
            double lat1 = Math.PI * ((double) (stack + 1) / stacks - 0.5);

            double y0 = Math.sin(lat0);
            double y1 = Math.sin(lat1);

            double r0 = Math.cos(lat0);
            double r1 = Math.cos(lat1);

            for (int slice = 0; slice < slices; slice++) {
                double lon = 2.0 * Math.PI * ((double) slice / slices);
                double x0 = r0 * Math.cos(lon);
                double z0 = r0 * Math.sin(lon);

                double x1 = r1 * Math.cos(lon);
                double z1 = r1 * Math.sin(lon);

   
                float px0 = (float) (x0 * radius);
                float py0 = (float) (y0 * radius);
                float pz0 = (float) (z0 * radius);

                float px1 = (float) (x1 * radius);
                float py1 = (float) (y1 * radius);
                float pz1 = (float) (z1 * radius);

          
                float nx0 = (float) x0;
                float ny0 = (float) y0;
                float nz0 = (float) z0;

                float nx1 = (float) x1;
                float ny1 = (float) y1;
                float nz1 = (float) z1;

               
               int repeatU = 4;
                int repeatV = 4; 

                float u = ((float) slice / (float) slices) * repeatU;
                 float uNext = ((float) (slice + 1) / (float) slices) * repeatU;
                float v0 = (1.0f - (float) stack / (float) stacks) * repeatV;
                float v1 = (1.0f - (float) (stack + 1) / (float) stacks) * repeatV;

              
                builder.vertex(poseMatrix, px0, py0, pz0)
                        .color(255, 255, 255, 255)
                        .uv(u, v0)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(packedLight)
                        .normal(normalMatrix, nx0, ny0, nz0)
                        .endVertex();

                builder.vertex(poseMatrix, px1, py1, pz1)
                        .color(255, 255, 255, 255)
                        .uv(u, v1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(packedLight)
                        .normal(normalMatrix, nx1, ny1, nz1)
                        .endVertex();

            
                double lonNext = 2.0 * Math.PI * ((double) (slice + 1) / slices);
                double x0n = r0 * Math.cos(lonNext);
                double z0n = r0 * Math.sin(lonNext);
                float px0n = (float) (x0n * radius);
                float pz0n = (float) (z0n * radius);
                float nx0n = (float) x0n;
                float nz0n = (float) z0n;
             

                builder.vertex(poseMatrix, px0n, py0, pz0n)
                        .color(255, 255, 255, 255)
                        .uv(uNext, v0)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(packedLight)
                        .normal(normalMatrix, nx0n, ny0, nz0n)
                        .endVertex();

                double x1n = r1 * Math.cos(lonNext);
                double z1n = r1 * Math.sin(lonNext);
                float px1n = (float) (x1n * radius);
                float pz1n = (float) (z1n * radius);
                float nx1n = (float) x1n;
                float nz1n = (float) z1n;

                builder.vertex(poseMatrix, px1, py1, pz1)
                        .color(255, 255, 255, 255)
                        .uv(u, v1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(packedLight)
                        .normal(normalMatrix, nx1, ny1, nz1)
                        .endVertex();

                builder.vertex(poseMatrix, px1n, py1, pz1n)
                        .color(255, 255, 255, 255)
                        .uv(uNext, v1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(packedLight)
                        .normal(normalMatrix, nx1n, ny1, nz1n)
                        .endVertex();

                builder.vertex(poseMatrix, px0n, py0, pz0n)
                        .color(255, 255, 255, 255)
                        .uv(uNext, v0)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(packedLight)
                        .normal(normalMatrix, nx0n, ny0, nz0n)
                        .endVertex();
            }
        }

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
}

// helper
private void putVertex(VertexConsumer consumer, Matrix4f model, Matrix3f normalMat,
                       float x, float y, float z, float u, float v, int light) {
    Vector3f normal = new Vector3f(x, y, z);
    normal.normalize();

    consumer.vertex(model, x, y, z)
            .color(1.0f, 1.0f, 1.0f, 1.0f)   // full white, texture provides color
            .uv(u, v)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(light)
            .normal(normalMat, normal.x(), normal.y(), normal.z())
            .endVertex();
}
    private static float[] sphereVertex(float r, float phi, float theta) {
        float x = (float) (r * Math.sin(phi) * Math.cos(theta));
        float y = (float) (r * Math.cos(phi));
        float z = (float) (r * Math.sin(phi) * Math.sin(theta));
        return new float[]{x, y, z};
    }

    

    private static void addVertex(PoseStack poseStack, VertexConsumer consumer, float[] pos, float alpha, int light, float u, float v) {
        float len = Mth.sqrt(pos[0]*pos[0] + pos[1]*pos[1] + pos[2]*pos[2]);
        consumer.vertex(poseStack.last().pose(), pos[0], pos[1], pos[2])
            .color(1f, 1f, 1f, alpha) // pure white, let texture show fully
            .uv(u, v)
            .overlayCoords(0, 10)
            .uv2(light)
            .normal(poseStack.last().normal(), pos[0]/len, pos[1]/len, pos[2]/len)
            .endVertex();
    }


    @Override
    public ResourceLocation getTextureLocation(HollowWickerBasketEntity entity) {
        return TEXTURE;
    }
}
