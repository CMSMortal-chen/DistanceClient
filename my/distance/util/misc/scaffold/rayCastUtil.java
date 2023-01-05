package my.distance.util.misc.scaffold;

import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.optifine.reflect.Reflector;

import java.util.List;

public class rayCastUtil {
	static Minecraft mc = Minecraft.getMinecraft();
	
    public static Entity raycastEntity(final double range, final IEntityFilter entityFilter) {
        return raycastEntity(range,RotationUtil.prevRotations[0],RotationUtil.prevRotations[1],
                entityFilter);
    }

    private static Entity raycastEntity(final double range, final float yaw, final float pitch, final IEntityFilter entityFilter) {
        final Entity renderViewEntity = mc.getRenderViewEntity();

        if(renderViewEntity != null && mc.theWorld != null) {
            double blockReachDistance = range;
            final net.minecraft.util.Vec3 eyePosition = renderViewEntity.getPositionEyes(1F);

            final float yawCos = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
            final float yawSin = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
            final float pitchCos = -MathHelper.cos(-pitch * 0.017453292F);
            final float pitchSin = MathHelper.sin(-pitch * 0.017453292F);

            final Vec3 entityLook = new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
            final net.minecraft.util.Vec3 vector = eyePosition.addVector(entityLook.getX() * blockReachDistance, entityLook.getY() * blockReachDistance, entityLook.getZ() * blockReachDistance);
            final List<Entity> entityList = mc.theWorld.getEntitiesInAABBexcluding(renderViewEntity, renderViewEntity.getEntityBoundingBox().addCoord(entityLook.getX() * blockReachDistance, entityLook.getY() * blockReachDistance, entityLook.getZ() * blockReachDistance).expand(1D, 1D, 1D), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity :: canBeCollidedWith));

            Entity pointedEntity = null;

            for(final Entity entity : entityList) {
                if(!entityFilter.canRaycast(entity))
                    continue;

                final float collisionBorderSize = entity.getCollisionBorderSize();
                final AxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                final MovingObjectPosition movingObjectPosition = axisAlignedBB.calculateIntercept(eyePosition, vector);

                if(axisAlignedBB.isVecInside(eyePosition)) {
                    if(blockReachDistance >= 0.0D) {
                        pointedEntity = entity;
                        blockReachDistance = 0.0D;
                    }
                }else if(movingObjectPosition != null) {
                    final double eyeDistance = eyePosition.distanceTo(movingObjectPosition.hitVec);

                    if(eyeDistance < blockReachDistance || blockReachDistance == 0.0D) {
                        if(entity == renderViewEntity.ridingEntity && !Reflector.callBoolean(renderViewEntity, Reflector.ForgeEntity_canRiderInteract)) {
                            if(blockReachDistance == 0.0D)
                                pointedEntity = entity;
                        }else{
                            pointedEntity = entity;
                            blockReachDistance = eyeDistance;
                        }
                    }
                }
            }

            return pointedEntity;
        }

        return null;
    }

    public interface IEntityFilter {
        boolean canRaycast(final Entity entity);
    }
	
}
