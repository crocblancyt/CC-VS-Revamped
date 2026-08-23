package net.croc.cc_vs_r.utils;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import dan200.computercraft.api.lua.IComputerSystem;
import kotlin.Pair;
import net.croc.cc_vs_r.apis.PhysShipAPI;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

public class PhysicsTicksHandle implements ShipForcesInducer {
    private final HashMap<Integer, IComputerSystem> computers = new HashMap<>();

    public void applyForces(@NotNull PhysShip physShip) {
        physTickEvent(physShip);
        queuedForces(physShip);
    }

    private final ConcurrentLinkedQueue<Vector3dc> invariantForces = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Vector3dc> invariantTorques = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Vector3dc[]> invariantForcesToPos = new ConcurrentLinkedQueue<>();

    private final ConcurrentLinkedQueue<Vector3dc> rotDependentForces = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Vector3dc> rotDependentTorques = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Vector3dc[]> rotDependentForcesToPos = new ConcurrentLinkedQueue<>();

    public void applyInvariantForce(Vector3dc force) { invariantForces.add(force); }
    public void applyInvariantTorque(Vector3dc torque) { invariantTorques.add(torque); }
    public void applyInvariantForceToPos(Vector3dc force, Vector3dc pos) { invariantForcesToPos.add(new Vector3dc[]{force, pos}); }
    public void applyRotDependentForce(Vector3dc force) { rotDependentForces.add(force); }
    public void applyRotDependentTorque(Vector3dc torque) { rotDependentTorques.add(torque); }
    public void applyRotDependentForceToPos(Vector3dc force, Vector3dc pos) { rotDependentForcesToPos.add(new Vector3dc[]{force, pos}); }

    private void queuedForces(PhysShip physShip) {
       while (!invariantForces.isEmpty()) {
           physShip.applyInvariantForce(invariantForces.poll());
       }
       while (!invariantTorques.isEmpty()) {
           physShip.applyInvariantTorque(invariantTorques.poll());
       }
       while (!invariantForcesToPos.isEmpty()) {
           Vector3dc[] posForce = invariantForcesToPos.poll();
           physShip.applyInvariantForceToPos(posForce[0], posForce[1]);
       }
       while (!rotDependentForces.isEmpty()) {
           physShip.applyRotDependentForce(rotDependentForces.poll());
       }
       while (!rotDependentTorques.isEmpty()) {
           physShip.applyRotDependentTorque(rotDependentTorques.poll());
       }
       while (!rotDependentForcesToPos.isEmpty()) {
           Vector3dc[] posForce = rotDependentForcesToPos.poll();
           physShip.applyRotDependentForceToPos(posForce[0], posForce[1]);
       }
    }

    private void physTickEvent(PhysShip physShip) {
        long milliseconds = System.currentTimeMillis();
        PhysShipAPI api = new PhysShipAPI((PhysShipImpl)physShip);

        Object[] args = {api, milliseconds};

        computers.forEach((hashCode, computer) -> {
            computer.queueEvent("physics_ticks", args);
        });
    }

    public void subscribe(IComputerSystem system) {
        computers.put(system.hashCode(), system);
    }

    public void unsubscribe(IComputerSystem system) {
        if (!computers.containsKey(system.hashCode())) return;
        computers.remove(system.hashCode());
    }

    public static PhysicsTicksHandle getOrCreate(ServerShip ship) {
        PhysicsTicksHandle control = ship.getAttachment(PhysicsTicksHandle.class);
        if (control == null) {
            control = new PhysicsTicksHandle();
            ship.saveAttachment(PhysicsTicksHandle.class, control);
        }
        return control;
    }
}
