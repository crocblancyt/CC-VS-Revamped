package net.croc.cc_vs_r.apis;

import dan200.computercraft.api.component.ComputerComponents;
import dan200.computercraft.api.lua.*;
import dan200.computercraft.shared.computer.blocks.ComputerBlockEntity;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.croc.cc_vs_r.utils.LuaTeleportData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.world.ServerShipWorld;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.assembly.ShipAssemblyKt;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class VSGameUtilsAPI implements ILuaAPI {
    @Nullable
    public String[] getNames() {
        return new String[]{"vscore"};
    }

    private final IComputerSystem system;

    public VSGameUtilsAPI(IComputerSystem system) {
        this.system = system;
    }

    @LuaFunction
    public final boolean isCommand() {
        if (system.getComponent(ComputerComponents.ADMIN_COMPUTER) == null) return false;
        return true;
    }

    public void assertIsCommand() throws LuaException {
        if (!isCommand()) {
            throw new LuaException("Computer does not have command access !");
        }
    }

    @LuaFunction
    public final ArrayList<ShipAPI> getAllShips() throws LuaException {
        assertIsCommand();
        ArrayList<ShipAPI> result = new ArrayList<>();

        for (Ship ship : VSGameUtilsKt.getAllShips(this.system.getLevel())) {
            result.add(new ShipAPI((ServerShip) ship, system));
        }

        return result;
    }

    @LuaFunction
    public final ShipAPI getShipManagingBlockPos(double x, double y, double z) throws LuaException {
        assertIsCommand();
        ServerShip ship = VSGameUtilsKt.getShipManagingPos(this.system.getLevel(), new BlockPos((int) x, (int) y, (int) z));
        if (ship == null) return null;
        return new ShipAPI(ship, system);
    }

    @LuaFunction
    public final ShipAPI getShipManagingEntity(String uuid_string) throws LuaException {
        assertIsCommand();
        ServerLevel level = this.system.getLevel();

        UUID uuid = null;

        try { uuid = UUID.fromString(uuid_string);
        } catch (IllegalArgumentException ignored) { }

        if (uuid == null) return null;

        ServerShip ship = (ServerShip) VSGameUtilsKt.getShipManaging(level.getEntity(uuid));
        if (ship == null) return null;
        return new ShipAPI(ship, system);
    }

    @LuaFunction
    public final ArrayList<ShipAPI> getShipsIntersecting(double x1, double y1, double z1, double x2, double y2, double z2) throws LuaException {
        assertIsCommand();
        AABB area = new AABB(x1, y1, z1, x2, y2, z2);

        ArrayList<ShipAPI> result = new ArrayList<>();

        VSGameUtilsKt.getShipsIntersecting(this.system.getLevel(), area).forEach((ship -> {
            result.add(new ShipAPI((ServerShip) ship, system));
        }));

        return result;
    }

    @LuaFunction
    public final ShipAPI getShipById(long shipId) throws LuaException {
        ServerShip ship = getShipFromId(shipId);
        return new ShipAPI(ship, system);
    }

    public ServerShip getShipFromId(long shipId) throws LuaException {
        ServerLevel level = system.getLevel();
        ServerShipWorld world = VSGameUtilsKt.getShipObjectWorld(level);

        ServerShip ship = world.getAllShips().getById(shipId);
        if (ship == null) throw new LuaException("Ship not found from id");

        return ship;
    }

    @LuaFunction
    public final void teleportShip(IArguments args) throws LuaException {
        assertIsCommand();
        ServerLevel level = this.system.getLevel();
        ServerShipWorld world = VSGameUtilsKt.getShipObjectWorld(level);

        long shipId = args.getLong(0);
        ServerShip ship = getShipFromId(shipId);

        Map<?, ?> configs = args.getTable(1);

        ShipTeleportDataImpl data = new LuaTeleportData(configs, ship).resolve();

        VSGameUtilsKt.getVsCore().teleportShip(world, ship, data);
    }

    @LuaFunction
    public final void renameShip(long shipId, String newName) throws LuaException {
        assertIsCommand();
        ServerShip ship = getShipFromId(shipId);
        VSGameUtilsKt.getVsCore().renameShip(ship, newName);
    }

    @LuaFunction
    public final void scaleShip(long shipId, double newScale) throws LuaException {
        assertIsCommand();

        ServerLevel level = this.system.getLevel();
        ServerShipWorld world = VSGameUtilsKt.getShipObjectWorld(level);

        ServerShip ship = getShipFromId(shipId);
        VSGameUtilsKt.getVsCore().scaleShip(world, ship, newScale);
    }

    @LuaFunction
    public final ShipAPI createShip(double x1, double y1, double z1, double x2, double y2, double z2) throws LuaException {
        assertIsCommand();
        ServerLevel level = this.system.getLevel();

        AABB area = new AABB(x1, y1, z1, x2, y2, z2);
        DenseBlockPosSet structure = new DenseBlockPosSet();

        for (double x = area.minX; x < area.maxX; x++) {
            for (double y = area.minY; y < area.maxY; y++) {
                for (double z = area.minZ; z < area.maxZ; z++) {
                    structure.add((int) x, (int) y, (int) z);
                }
            }
        }

        if (structure.isEmpty()) throw new LuaException("Area contains no blocks");

        ServerShip ship = ShipAssemblyKt.createNewShipWithBlocks(new BlockPos((int) x1, (int) y1, (int) z1), structure, level);
        return new ShipAPI(ship, system);
    }

    @LuaFunction
    public final void deleteShip(long shipId) throws LuaException {
        assertIsCommand();

        ServerLevel level = this.system.getLevel();
        ServerShipWorld world = VSGameUtilsKt.getShipObjectWorld(level);

        ServerShip ship = getShipFromId(shipId);
        ArrayList<ServerShip> ships = new ArrayList<>();
        ships.add(ship);

        VSGameUtilsKt.getVsCore().deleteShips(world, ships);
    }
}