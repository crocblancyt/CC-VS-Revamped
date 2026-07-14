package net.croc.cc_vs_r.utils;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;

import java.util.Map;

public record LuaTeleportData(Map<?, ?> configs, ServerShip ship) {
    public Quaterniond getQuaternion(String key, Quaterniond default_value) {
        if (configs.get(key) instanceof Map<?, ?> map) {
            if (map.get("w") instanceof Double w && map.get("x") instanceof Double x &&
                    map.get("y") instanceof Double y && map.get("z") instanceof Double z) {

                return new Quaterniond(x,y,z,w);
            }
        }
        return default_value;
    }

    public Vector3d getVector(String key, Vector3d default_value) {
        if (configs.get(key) instanceof Map<?, ?> map) {
            if (map.get("x") instanceof Double x && map.get("y") instanceof Double y && map.get("z") instanceof Double z) {
                return new Vector3d(x,y,z);
            }
        }
        return default_value;
    }

    public String getString(String key, String default_value) {
        if (configs.get(key) instanceof String string) return string;
        return default_value;
    }

    public double getDouble(String key, double default_value) {
        if (configs.get(key) instanceof Double scale) return scale;
        return default_value;
    }

    public ShipTeleportDataImpl resolve() {
        Vector3d position = (Vector3d) ship.getTransform().getPositionInWorld();
        Quaterniond rotation = (Quaterniond) ship.getTransform().getShipToWorldRotation();
        Vector3d velocity = (Vector3d) ship.getVelocity();
        Vector3d omega = (Vector3d) ship.getOmega();
        String dimension = ship.getChunkClaimDimension();
        double scale = ship.getShipToWorld().getScale(new Vector3d()).x;


        return new ShipTeleportDataImpl(
                getVector("position", position),
                getQuaternion("rotation", rotation),
                getVector("velocity", velocity),
                getVector("omega", omega),
                getString("dimension", dimension),
                getDouble("scale", scale)
        );
    }
}