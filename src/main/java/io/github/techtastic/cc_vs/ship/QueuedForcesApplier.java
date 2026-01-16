package io.github.techtastic.cc_vs.ship;

import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueuedForcesApplier implements ShipForcesInducer {
    private final ConcurrentLinkedQueue<Vector3dc> torques = new ConcurrentLinkedQueue<>();

    @Override
    public void applyForces(PhysShip physShip) { }

    public void applyInvariantTorque(Vector3dc torque) {
        this.torques.add(torque);
    }
    public final void addComputer(int id) {}
    public void onServerTick() {}

    public static PhysicsTicksEventHandler getOrCreate(ServerShip ship) {
        PhysicsTicksEventHandler control = ship.getAttachment(PhysicsTicksEventHandler.class);

        if (control == null) {
            control = new PhysicsTicksEventHandler();
            ship.saveAttachment(PhysicsTicksEventHandler.class, control);
        }

        return control;
    }
}