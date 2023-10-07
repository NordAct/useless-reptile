package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.Arrays;
import java.util.List;

public class DragonRiderLayer<T extends URRideableDragonEntity> extends GeoLayerRenderer<T> {
    protected final float defaultOffsetX;
    protected final float defaultOffsetY;
    protected final float defaultOffsetZ;
    protected final String starterBone;
    protected final double defaultPitch;
    protected final List<String> ignore;

    public DragonRiderLayer(IGeoRenderer<T> entityRendererIn, float defaultOffsetX, float defaultOffsetY, float defaultOffsetZ, String starterBone, double defaultPitch, String[] ignore) {
        super(entityRendererIn);
        this.defaultOffsetX = defaultOffsetX;
        this.defaultOffsetY = defaultOffsetY;
        this.defaultOffsetZ = defaultOffsetZ;
        this.starterBone = starterBone;
        this.defaultPitch = defaultPitch;
        this.ignore = Arrays.stream(ignore).toList();
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn,
                       T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        matrixStackIn.push();
        LivingEntity passenger = getPassenger(entity);
        if (passenger != null) {
            float passengerYaw = (float) Math.toRadians(entity.getBodyYaw() + 180);
            GeoModel model = getEntityModel().getModel(getEntityModel().getModelResource(entity));
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
                        pitch += Math.toRadians(bone.getRotationX());
                        roll += Math.toRadians(bone.getRotationZ());
                        yaw += Math.toRadians(bone.getRotationY());
                        offsetX += bone.getPositionX();
                        offsetY -= bone.getLocalPosition().y;
                        offsetZ += bone.getPositionZ();
                    }
                    bone = bone.getParent();
                }
            }
            float rollDegrees = (float) Math.toDegrees(roll);
            float yawDegrees = (float) Math.toDegrees(yaw);

            Vec3d rotX = getRotationVector(MathHelper.wrapDegrees(rollDegrees), MathHelper.wrapDegrees(entity.getYaw(0.01F) - 90 + yawDegrees));

            Vec3d rotZ = getRotationVector(0, entity.getYaw(0.01F));
            matrixStackIn.translate(0, 1.5f, 0);
            matrixStackIn.multiply(new Quaternion(new Vec3f(rotX), (float) -pitch, false));
            matrixStackIn.multiply(new Quaternion(new Vec3f(rotZ), (float) -roll, false));
            matrixStackIn.multiply(new Quaternion(Vec3f.POSITIVE_Y, yawDegrees + passengerYaw, false));
            matrixStackIn.translate(-offsetZ * (float) rotZ.x + offsetX * (float) rotZ.z, -offsetY,
                    -offsetZ * (float) rotZ.z - offsetX * (float) rotZ.x);

            renderEntity(passenger, partialTicks, matrixStackIn, bufferIn, packedLightIn);
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
        return entity.getPrimaryPassenger();
    }
}
