// package radon.jujutsu_kaisen.mixin.client;

// import net.minecraft.client.player.AbstractClientPlayer;
// import net.minecraft.client.renderer.entity.player.PlayerRenderer;
// import net.minecraft.resources.ResourceLocation;
// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
// import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
// import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

// @Mixin(PlayerRenderer.class)
// public abstract class PlayerRendererMixin {

//     @Inject(
//         method = "getTextureLocation(Lnet/minecraft/client/player/AbstractClientPlayer;)Lnet/minecraft/resources/ResourceLocation;",
//         at = @At("RETURN"),
//         cancellable = true,
//         require = 0
//     )
//     private void overrideStolenSkin(AbstractClientPlayer player, CallbackInfoReturnable<ResourceLocation> cir) {
//         player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
//             ResourceLocation stolenSkin = cap.getStolenSkinTexture();
//             if (stolenSkin != null) {
//                 cir.setReturnValue(stolenSkin);
//             }
//         });
//     }
// }
