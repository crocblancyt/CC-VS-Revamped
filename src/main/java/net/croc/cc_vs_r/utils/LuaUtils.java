package net.croc.cc_vs_r.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBic;

import java.util.HashMap;
import java.util.Map;

public class LuaUtils {
    public static Map<String, Double> toLua(Vec3 vec) {
        Map<String, Double> luaMap = new HashMap<>();
        luaMap.put("x", vec.x());
        luaMap.put("y", vec.y());
        luaMap.put("z", vec.z());
        return luaMap;
    };

    public static Map<String, Double> toLua(Vector3dc vec) {
        Map<String, Double> luaMap = new HashMap<>();
        luaMap.put("x", vec.x());
        luaMap.put("y", vec.y());
        luaMap.put("z", vec.z());
        return luaMap;
    }

    public static Map<String, Double> toLua(Quaterniondc q) {
        Map<String, Double> luaMap = new HashMap<>();
        luaMap.put("x", q.x());
        luaMap.put("y", q.y());
        luaMap.put("z", q.z());
        luaMap.put("w", q.w());
        return luaMap;
    }

    public static Map<String, ?> toLua(AABBic area) {
        Map<String, Object> luaMap = new HashMap<>();
        luaMap.put("min_x", area.minX());
        luaMap.put("min_y", area.minY());
        luaMap.put("min_z", area.minZ());
        luaMap.put("max_x", area.maxX());
        luaMap.put("max_y", area.maxY());
        luaMap.put("max_z", area.maxZ());
        return luaMap;
    };

    public static Map<String, ?> toLua(BlockPos vec) {
        Map<String, Object> luaMap = new HashMap<>();
        luaMap.put("x", vec.getX());
        luaMap.put("y", vec.getY());
        luaMap.put("z", vec.getZ());
        return luaMap;
    };
}