package io.github.techtastic.cc_vs.ship;

import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipPhysicsListener;
import org.valkyrienskies.core.api.world.PhysLevel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PhysTickEventHandler implements ShipPhysicsListener {
    private final ConcurrentLinkedQueue<Vector3dc> torques = new ConcurrentLinkedQueue<>();

    @Override
    public void physTick(PhysShip physShip, PhysLevel physLevel) {
        while (!this.torques.isEmpty()) {
            physShip.applyInvariantTorque(this.torques.poll());
        }
    }

    public void applyInvariantTorque(Vector3dc torque) {
        this.torques.add(torque);
    }
    public final void addComputer(int id) {}
    public void onServerTick() {}

    public static PhysTickEventHandler getOrCreate(ServerShip ship) {
        PhysTickEventHandler control = ship.getAttachment(PhysTickEventHandler.class);

        if (control == null) {
            control = new PhysTickEventHandler();
            ship.saveAttachment(PhysTickEventHandler.class, control);
        }

        return control;
    }
}