package nordmods.uselessreptile.common.dragon_variant.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.init.URResourceKeys;
import org.jspecify.annotations.Nullable;

import java.util.*;

public record EquipmentModelData(Optional<Identifier> parent, Map<Holder<Item>, Equipment> equipment) {
    public static final Codec<EquipmentModelData> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.optionalFieldOf("parent").forGetter(EquipmentModelData::parent),
                    Codec.unboundedMap(Item.CODEC, Equipment.CODEC).fieldOf("equipment").forGetter(EquipmentModelData::equipment))
            .apply(instance, EquipmentModelData::new));

    public static final Codec<Holder<EquipmentModelData>> CODEC = RegistryFileCodec.create(URResourceKeys.DRAGON_EQUIPMENT, DIRECT_CODEC);

    //TODO
    // tooltip should be scrollable (like pages)
    public record Equipment(ModelData modelData, Optional<List<String>> hidBones, DragonInventory.Slot slot, Optional<Integer> maxPassengers) {
        public static final Codec<Equipment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        ModelData.CODEC.fieldOf("model_data").forGetter(Equipment::modelData),
                        ExtraCodecs.NON_EMPTY_STRING.listOf().optionalFieldOf("hid_bones").forGetter(Equipment::hidBones),
                        DragonInventory.Slot.CODEC.fieldOf("slot").forGetter(Equipment::slot),
                        Codec.INT.optionalFieldOf("max_passengers").forGetter(Equipment::maxPassengers)
                )
                .apply(instance, Equipment::new));

        public static Builder builder(DragonInventory.Slot slot) {
            return new Builder(slot);
        }

        public static class Builder {
            @Nullable private Identifier texture;
            @Nullable private Identifier model;
            @Nullable private Identifier animation;
            private boolean translucent = false;
            private final List<String> hidBones = new ArrayList<>();
            private final DragonInventory.Slot slot;
            private int maxPassengers;

            private Builder(DragonInventory.Slot slot) {
                this.slot = slot;
            }

            public Builder setTranslucent(boolean translucent) {
                this.translucent = translucent;
                return this;
            }

            public Builder setTexture(Identifier texture) {
                this.texture = texture;
                return this;
            }

            public Builder setModel(Identifier model) {
                this.model = model;
                return this;
            }

            public Builder setAnimation(Identifier animation) {
                this.animation = animation;
                return this;
            }

            public Builder addHidBones(String... bones) {
                this.hidBones.addAll(List.of(bones));
                return this;
            }

            public Builder addHidBones(Collection<String> bones) {
                this.hidBones.addAll(bones);
                return this;
            }

            public Builder setMaxPassengers(int maxPassengers) {
                this.maxPassengers = maxPassengers;
                return this;
            }

            public Equipment build() {
                if (texture == null) throw new  IllegalStateException("Texture must be specified");
                if (model == null) throw new  IllegalStateException("Model must be specified");
                if (animation == null) throw new  IllegalStateException("Animation must be specified");

                return new Equipment(
                        new ModelData(texture, model, animation, false, translucent),
                        hidBones.isEmpty() ? Optional.empty() : Optional.of(hidBones),
                        slot,
                        slot == DragonInventory.Slot.SADDLE && maxPassengers > 0 ? Optional.of(maxPassengers) : Optional.empty()
                );
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        @Nullable
        private Identifier parent;
        private final Map<Holder<Item>, Equipment> equipment = new HashMap<>();

        private Builder() {}

        public Builder setParent(Identifier parent) {
            this.parent = parent;
            return this;
        }

        public Builder addEquipment(Item item, Equipment equipment) {
            this.equipment.put(item.builtInRegistryHolder(), equipment);
            return this;
        }

        public EquipmentModelData build() {
            return new EquipmentModelData(Optional.ofNullable(parent), equipment);
        }
    }
}
