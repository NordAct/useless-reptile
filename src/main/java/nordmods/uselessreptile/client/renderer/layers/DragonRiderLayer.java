package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.List;

public class DragonRiderLayer<T extends URRideableDragonEntity> extends GeoRenderLayer<T> {
    protected final float defaultOffsetX;
    protected final float defaultOffsetY;
    protected final float defaultOffsetZ;
    protected final String starterBone;
    protected final double defaultPitch;
    protected final List<String> ignore;

    public DragonRiderLayer(GeoRenderer<T> entityRendererIn, float defaultOffsetX, float defaultOffsetY, float defaultOffsetZ, String starterBone, double defaultPitch, List<String> ignore) {
        super(entityRendererIn);
        this.defaultOffsetX = defaultOffsetX;
        this.defaultOffsetY = defaultOffsetY;
        this.defaultOffsetZ = defaultOffsetZ;
        this.starterBone = starterBone;
        this.defaultPitch = defaultPitch;
        this.ignore = ignore;
    }
    public DragonRiderLayer(GeoRenderer<T> entityRendererIn, float defaultOffsetX, float defaultOffsetY, float defaultOffsetZ, String starterBone, double defaultPitch) {
        super(entityRendererIn);
        this.defaultOffsetX = defaultOffsetX;
        this.defaultOffsetY = defaultOffsetY;
        this.defaultOffsetZ = defaultOffsetZ;
        this.starterBone = starterBone;
        this.defaultPitch = defaultPitch;
        this.ignore = List.of();
    }

    @Override
    public void render(MatrixStack matrixStackIn, T entity, BakedGeoModel model, RenderLayer renderType,
                       VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        matrixStackIn.push();
        LivingEntity passenger = getPassenger(entity);
        if (passenger != null) {
            URRideableDragonEntity.passengers.remove(passenger.getUuid());
            float offsetX = defaultOffsetX;
            float offsetY = defaultOffsetY;
            float offsetZ = defaultOffsetZ;
            double pitch = Math.toRadians(defaultPitch);
            double roll = 0;
            double yaw = 0;
            if (model.getBone(starterBone).isPresent()) {
                GeoBone bone = model.getBone(starterBone).get();
                while (bone != null) {
                    if (!ignore.contains(bone.getName())) {
                        pitch += bone.getRotX();
                        roll += bone.getRotZ();
                        yaw += bone.getRotY();
                        offsetX += bone.getPosX() * 0.0625f;
                        offsetY -= bone.getModelPosition().y * 0.0625f;
                        offsetZ += bone.getPosZ() * 0.0625f;
                    }
                    bone = bone.getParent();
                }
            }
            float rollDegrees = (float) Math.toDegrees(roll);
            float yawDegrees = (float) Math.toDegrees(yaw);

            Vec3d rotX = getRotationVector(MathHelper.wrapDegrees(rollDegrees), MathHelper.wrapDegrees(entity.getYaw(0.01F) - 90 + yawDegrees));

            Vec3d rotZ = getRotationVector(0, entity.getYaw(0.01F));
            matrixStackIn.translate(-offsetZ * (float) rotZ.x + offsetX * (float) rotZ.z,
                    -offsetY + 1.5f,
                    -offsetZ * (float) rotZ.z - offsetX * (float) rotZ.x);
            matrixStackIn.multiply(new Quaternionf().setAngleAxis(-pitch, rotX.x, rotX.y, rotX.z));
            matrixStackIn.multiply(new Quaternionf().setAngleAxis(-roll, rotZ.x, rotZ.y, rotZ.z));
            matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotation((float) yaw));

            renderEntity(passenger, partialTick, matrixStackIn, bufferSource, packedLight);
            URRideableDragonEntity.passengers.add(passenger.getUuid());
        }
        matrixStackIn.pop();
    }


    public <E extends Entity> void renderEntity(E entityIn, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLight) {
        boolean isFirstPerson = MinecraftClient.getInstance().options.getPerspective().isFirstPerson();
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (isFirstPerson && entityIn == clientPlayer) return;

        EntityRenderer<? super E> render;
        EntityRenderDispatcher manager = MinecraftClient.getInstance().getEntityRenderDispatcher();

        render = manager.getRenderer(entityIn);
        matrixStack.push();
        try {
            render.render(entityIn, 0, partialTicks, matrixStack, bufferIn, packedLight);
        } catch (Throwable throwable1) {
            throw new CrashException(CrashReport.create(throwable1, "Rendering entity in world"));
        }
        matrixStack.pop();
    }

    protected final Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * ((float)Math.PI / 180);
        float g = -yaw * ((float)Math.PI / 180);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    protected LivingEntity getPassenger(T entity) {
        return entity.getControllingPassenger();
    }
}
