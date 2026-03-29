package net.croc.cc_vs_r.apis;

import dan200.computercraft.api.lua.*;
import dan200.computercraft.core.computer.Computer;
import dan200.computercraft.shared.computer.blocks.ComputerBlock;
import kotlin.collections.CollectionsKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector4d;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.annotation.Nullable;
import java.util.*;

import net.croc.cc_vs_r.utils.LuaUtils;

public class ShipAPI implements ILuaAPI {
    @NotNull
    private final IComputerSystem system;

    public ShipAPI(@NotNull IComputerSystem system) {
        this.system = system;
    }

    @Nullable
    public String[] getNames() {
        String[] arrayOfString = new String[1];
        arrayOfString[0] = "ship";
        return arrayOfString;
    }

    /*
    public void startup() {
        try {
            if (PlatformUtils.exposePhysTick())
                PhysicsTicksEventHandler.Companion.getOrCreateControl((ServerShip)getShip());
        } catch (LuaException luaException) {}
        super.startup();
    }

    public void update() {
        try {
            if (PlatformUtils.exposePhysTick()) {
                LuaPhysShip[] data = PhysicsTicksEventHandler.Companion.getOrCreateControl((ServerShip)getShip()).getData();
                this.system.queueEvent("physics_ticks", Arrays.copyOf((Object[])data, data.length));
            }
        } catch (LuaException luaException) {}
        super.update();
    }

    public void shutdown() {
        try {
            if (PlatformUtils.exposePhysTick())
                PhysicsTicksEventHandler.Companion.getOrCreateControl((ServerShip)getShip());
        } catch (LuaException luaException) {}
        super.shutdown();
    }
    */

    @NotNull
    protected final ServerShip getShip() throws LuaException{
        ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos(this.system.getLevel(), this.system.getPosition());
        if (ship == null) { throw new LuaException("This computer is not on a Ship!"); }
        return ship;
    }

    @LuaFunction
    @NotNull
    public final List<List<Double>> getRotationMatrix() throws LuaException {
        Matrix4dc transform = getShip().getTransform().getShipToWorld();
        List<List<Double>> matrix = new ArrayList();
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
        return Math.atan2(rotMatrix.get(1).get(0), rotMatrix.get(1).get(1));
    }

    @LuaFunction
    public final double getYaw() throws LuaException {
        List<List<Double>> rotMatrix = getRotationMatrix();
        return Math.atan2(-rotMatrix.get(0).get(2), rotMatrix.get(2).get(2));
    }

    @LuaFunction
    public final double getPitch() throws LuaException {
        return -Math.asin(getRotationMatrix().get(1).get(2));
    }

    @LuaFunction
    public final Map<String, Double> getEulerAnglesXYZ() throws LuaException {
        return LuaUtils.toLua(getShip().getTransform().getShipToWorldRotation().getEulerAnglesXYZ(new Vector3d()));
    }

    @LuaFunction
    public final Map<String, Double> getEulerAnglesZYX() throws LuaException {
        return LuaUtils.toLua(getShip().getTransform().getShipToWorldRotation().getEulerAnglesZYX(new Vector3d()));
    }

    @LuaFunction
    public final Map<String, Double> getEulerAnglesZXY() throws LuaException {
        return LuaUtils.toLua(getShip().getTransform().getShipToWorldRotation().getEulerAnglesZXY(new Vector3d()));
    }

    @LuaFunction
    public final Map<String, Double> getEulerAnglesYXZ() throws LuaException {
        return LuaUtils.toLua(getShip().getTransform().getShipToWorldRotation().getEulerAnglesYXZ(new Vector3d()));
    }

    @LuaFunction
    public final Map<String, Double> getQuaternion() throws LuaException {
        return LuaUtils.toLua(getShip().getTransform().getShipToWorldRotation());
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
        return LuaUtils.toLua(getShip().getOmega());
    }

    @LuaFunction
    public final Map<String, Double> getVelocity() throws LuaException {
        return LuaUtils.toLua(getShip().getVelocity());
    }

    @LuaFunction
    public final Map<String, Double> getWorldspacePosition() throws LuaException {
        return LuaUtils.toLua(getShip().getTransform().getPositionInWorld());
    }

    @LuaFunction
    public final Map<String, Double> getShipyardPosition() throws LuaException {
        return LuaUtils.toLua(getShip().getTransform().getPositionInShip());
    }

    @LuaFunction
    public final Map<String, ?> getComputerPosition() throws LuaException {
        return LuaUtils.toLua(this.system.getPosition());
    }

    @LuaFunction
    public final String getComputerFacing() throws LuaException {
        BlockPos pos = this.system.getPosition();
        ServerLevel level = this.system.getLevel();
        return level.getBlockState(pos).getValue(HorizontalDirectionalBlock.FACING).toString();
    }

    @LuaFunction
    public final Map<String, Double> getScale() throws LuaException {
        return LuaUtils.toLua(getShip().getTransform().getShipToWorldScaling());
    }

    @LuaFunction
    public final Map<String, ?> getShipyardAABB() throws LuaException {
        AABBic area = getShip().getShipAABB();
        if (area == null) { throw new LuaException("No shipyard AABB"); }
        return LuaUtils.toLua(area);
    }

    @LuaFunction
    public final List<List<Double>> getTransformationMatrix() throws LuaException {
        Matrix4dc transform = getShip().getTransform().getShipToWorld();
        List<List<Double>> matrix = new ArrayList();
        for (int i = 0; i < 4; i++) {
            Vector4d row = transform.getRow(i, new Vector4d());
            List<Double> arrayOfDouble = new ArrayList();
            arrayOfDouble.add(row.x);
            arrayOfDouble.add(row.y);
            arrayOfDouble.add(row.z);
            arrayOfDouble.add(row.w);
            matrix.add(arrayOfDouble);
        }
        return matrix;
    }

    public final Vector3dc toVector(Map arg) throws LuaException {
        if (arg == null) { throw new LuaException("Invalid Argument! Expects either a vector or a table with x, y, and z keys!"); }

        return new Vector3d(
                (Double) arg.get("x"),
                (Double) arg.get("y"),
                (Double) arg.get("z")
        );
    }

}