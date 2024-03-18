package nordmods.uselessreptile.client.util.model_redirect;

import org.jetbrains.annotations.Nullable;

public record ModelRedirect(String texture, @Nullable String model, @Nullable String animation, @Nullable String saddle, boolean nametagAccessible) {
}
