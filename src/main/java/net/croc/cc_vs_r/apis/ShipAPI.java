package net.croc.cc_vs_r.apis;

import dan200.computercraft.api.lua.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import net.croc.cc_vs_r.utils.PhysicsTicksHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import static net.croc.cc_vs_r.utils.LuaUtils.toLua;

public class ShipAPI implements ILuaAPI {
    private IComputerSystem system;

    private ServerShip ship;

    public ShipAPI(IComputerSystem system) {
        this.system = system;
    }

    public ShipAPI(ServerShip ship) {
        this.ship = ship;
    }

    @Nullable
    public String[] getNames() {
        return new String[]{"ship"};
    }

    @Override
    public void startup() {
        if (this.system == null) return;
        try {
            PhysicsTicksHandle.getOrCreate(getShip()).subscribe(this.system);
        } catch (LuaException luaException) {}
    }

    @Override
    public void shutdown() {
        if (this.system == null) return;
        try {
            PhysicsTicksHandle.getOrCreate(getShip()).unsubscribe(this.system);
        } catch (LuaException luaException) {}
    }

    @NotNull
    protected final ServerShip getShip() throws LuaException {
        if (ship != null) return ship;
        ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos(this.system.getLevel(), this.system.getPosition());
        if (ship == null)
            throw new LuaException("This computer is not on a Ship!");
        return ship;
    }

    @LuaFunction
    @NotNull
    public final List<List<Double>> getRotationMatrix() throws LuaException {
        Matrix4dc transform = getShip().getTransform().getShipToWorld();
        List<List<Double>> matrix = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Vector4d row = transform.getRow(i, new Vector4d());
            List<Double> arrayOfDouble = new ArrayList<>();
            arrayOfDouble.add(row.x);
            arrayOfDouble.add(row.y);
            arrayOfDouble.add(row.z);
            arrayOfDouble.add(row.w);
            matrix.add(i, arrayOfDouble);
        }
        return matrix;
    }

    @LuaFunction
    public final double getRoll() throws LuaException {
        List<List<Double>> rotMatrix = getRotationMatrix();
        return Math.atan2(rotMatrix.get(1).get(0),rotMatrix.get(1).get(1));
    }

    @LuaFunction
    public final double getYaw() throws LuaException {
        List<List<Double>> rotMatrix = getRotationMatrix();
        return Math.atan2(-rotMatrix.get(0).get(2),rotMatrix.get(2).get(2));
    }

    @LuaFunction
    public final double getPitch() throws LuaException {
        return -Math.asin(getRotationMatrix().get(1).get(2));
    }

    @LuaFunction
    public final Map<String, Double> getEulerAnglesXYZ() throws LuaException {
        return toLua(getShip().getTransform().getShipToWorldRotation().getEulerAnglesXYZ(new Vector3d()));
    }

    @LuaFunction
    public final Map<String, Double> getEulerAnglesZYX() throws LuaException {
        return toLua(getShip().getTransform().getShipToWorldRotation().getEulerAnglesZYX(new Vector3d()));
    }

    @LuaFunction
    public final Map<String, Double> getEulerAnglesZXY() throws LuaException {
        return toLua(getShip().getTransform().getShipToWorldRotation().getEulerAnglesZXY(new Vector3d()));
    }

    @LuaFunction
    public final Map<String, Double> getEulerAnglesYXZ() throws LuaException {
        return toLua(getShip().getTransform().getShipToWorldRotation().getEulerAnglesYXZ(new Vector3d()));
    }

    @LuaFunction
    public final Map<String, Double> getQuaternion() throws LuaException {
        return toLua(getShip().getTransform().getShipToWorldRotation());
    }

    @LuaFunction
    public final double getMass() throws LuaException {
        return getShip().getInertiaData().getMass();
    }

    @LuaFunction
    public final double getId() throws LuaException {
        return getShip().getId();
    }

    @LuaFunction
    public final String getSlug() throws LuaException {
        return getShip().getSlug();
    }

    @LuaFunction
    public final Map<String, Double> getOmega() throws LuaException {
        return toLua(getShip().getOmega());
    }

    @LuaFunction
    public final Map<String, Double> getVelocity() throws LuaException {
        return toLua(getShip().getVelocity());
    }

    @LuaFunction
    public final Map<String, Double> getWorldspacePosition() throws LuaException {
        return toLua(getShip().getTransform().getPositionInWorld());
    }

    @LuaFunction
    public final Map<String, Double> getShipyardPosition() throws LuaException {
        return toLua(getShip().getTransform().getPositionInShip());
    }

    @LuaFunction
    public final Map<String, ?> getComputerPosition() throws LuaException {
        return toLua(this.system.getPosition());
    }

    @LuaFunction
    public final String getComputerFacing() throws LuaException {
        BlockPos pos = this.system.getPosition();
        ServerLevel level = this.system.getLevel();
        return level.getBlockState(pos).getValue(HorizontalDirectionalBlock.FACING).toString();
    }

    @LuaFunction
    public final Map<String, Double> getScale() throws LuaException {
        return toLua(getShip().getTransform().getShipToWorldScaling());
    }

    @LuaFunction
    public final Map<String, ?> getShipyardAABB() throws LuaException {
        AABBic area = getShip().getShipAABB();
        if (area == null)
            throw new LuaException("No shipyard AABB");
        return toLua(area);
    }

    @LuaFunction
    public final Map<String, ?> getWorldspaceAABB() throws LuaException {
        return toLua((AABBic) getShip().getWorldAABB());
    }

    @LuaFunction
    public final List<List<Double>> getTransformationMatrix() throws LuaException {
        Matrix4dc transform = getShip().getTransform().getShipToWorld();
        List<List<Double>> matrix = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Vector4d row = transform.getRow(i, new Vector4d());
            List<Double> arrayOfDouble = new ArrayList<>();
            arrayOfDouble.add(row.x);
            arrayOfDouble.add(row.y);
            arrayOfDouble.add(row.z);
            arrayOfDouble.add(row.w);
            matrix.add(arrayOfDouble);
        }
        return matrix;
    }

    @LuaFunction
    public final Map<String, ?> transformPositionToWorld(double x, double y, double z) throws LuaException {
        return toLua(getShip().getShipToWorld().transformPosition(new Vector3d(x, y, z)));
    }

    @LuaFunction
    public final Map<String, ?> transformDirectionToWorld(double x, double y, double z) throws LuaException {
        return toLua(getShip().getShipToWorld().transformDirection(new Vector3d(x, y, z)));
    }

    @LuaFunction
    public final Map<String, ?> transformPositionToShip(double x, double y, double z) throws LuaException {
        return toLua(getShip().getWorldToShip().transformPosition(new Vector3d(x, y, z)));
    }

    @LuaFunction
    public final Map<String, ?> transformDirectionToShip(double x, double y, double z) throws LuaException {
        return toLua(getShip().getWorldToShip().transformDirection(new Vector3d(x, y, z)));
    }

    @LuaFunction
    public final Map<String, ?> getShipyardOmega() throws LuaException {
        ServerShip ship = getShip();
        return toLua(ship.getShipToWorld().transformDirection((Vector3d) ship.getVelocity()));
    }
}
