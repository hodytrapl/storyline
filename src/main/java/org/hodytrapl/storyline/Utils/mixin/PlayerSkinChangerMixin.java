package org.hodytrapl.storyline.Utils.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.hodytrapl.storyline.Client_side.skin.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class PlayerSkinChangerMixin {

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void onGetSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        ResourceLocation customTexture = SkinTextures.getCurrent();
        if (customTexture == null) {
            return; // Нет кастомного скина — оставляем всё как есть
        }

        PlayerSkin original = cir.getReturnValue(); // Безопасно получаем оригинал
        if (original == null) {
            return;
        }

        // Создаём новый PlayerSkin с нашей текстурой, сохраняя модель (SLIM/WIDE) оригинала
        PlayerSkin customSkin = new PlayerSkin(
                customTexture,          // texture — наша текстура
                null,                   // textureUrl
                original.capeTexture(), // capeTexture — оставляем стандартный плащ
                original.elytraTexture(), // elytraTexture
                original.model(),       // model — SLIM или WIDE, как у игрока
                false                   // secure
        );

        cir.setReturnValue(customSkin);
    }
}
