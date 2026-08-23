package net.croc.cc_vs_r.apis;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.croc.cc_vs_r.utils.LuaUtils;
import net.croc.cc_vs_r.utils.PhysicsTicksHandle;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.physics_api.PoseVel;

public class PhysShipAPI {
    private final long id;

    private final double buoyantFactor;

    private final boolean doFluidDrag;

    private final boolean isStatic;

    private final double mass;

    private final double dynamicFrictionCoefficient;

    private final double staticFrictionCoefficient;

    private final double restitutionCoefficient;

    private final PoseVel poseVel;

    private final List<String> forceInducers;

    public PhysShipAPI(PhysShipImpl physShip) {
        this.id = physShip.getId();
        this.buoyantFactor = physShip.getBuoyantFactor();
        this.doFluidDrag = physShip.getDoFluidDrag();
        this.mass = physShip.getInertia().getShipMass();
        this.dynamicFrictionCoefficient = physShip.getRigidBodyReference().getDynamicFrictionCoefficient();
        this.staticFrictionCoefficient = physShip.getRigidBodyReference().getStaticFrictionCoefficient();
        this.restitutionCoefficient = physShip.getRigidBodyReference().getRestitutionCoefficient();
        this.poseVel = physShip.getPoseVel();
        this.isStatic = physShip.isStatic();
        List<String> forceInducers = new ArrayList<>();
        physShip.getForceInducers().forEach(forceInducer -> forceInducers.add(forceInducer.toString()));
        this.forceInducers = forceInducers;
    }

    @LuaFunction
    public final long getId() {
        return this.id;
    }

    @LuaFunction
    public final boolean isStatic() {
        return this.isStatic;
    }

    @LuaFunction
    public final double getMass() {
        return this.mass;
    }

    @LuaFunction
    public final double getBuoyantFactor() {
        return this.buoyantFactor;
    }

    @LuaFunction
    public final boolean getDoFluidDrag() {
        return this.doFluidDrag;
    }

    @LuaFunction
    public final List<String> getForceInducers() {
        return this.forceInducers;
    }

    @LuaFunction
    public final Map<String, Double> getPosition() {
        return LuaUtils.toLua(this.poseVel.getPos());
    }

    @LuaFunction
    public final Map<String, Double> getVelocity() {
        return LuaUtils.toLua(this.poseVel.getVel());
    }

    @LuaFunction
    public final Map<String, Double> getOmega() {
        return LuaUtils.toLua(this.poseVel.getOmega());
    }

    @LuaFunction
    public final Map<String, Double> getQuaternion() {
        return LuaUtils.toLua(this.poseVel.getRot());
    }

    @LuaFunction
    public final double getDynamicFrictionCoefficient() {
        return this.dynamicFrictionCoefficient;
    }

    @LuaFunction
    public final double getStaticFrictionCoefficient() {
        return this.staticFrictionCoefficient;
    }

    @LuaFunction
    public final double getRestitutionCoefficient() {
        return this.restitutionCoefficient;
    }
}
