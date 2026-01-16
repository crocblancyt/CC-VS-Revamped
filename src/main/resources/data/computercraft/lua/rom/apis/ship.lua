
if pocket then
    return
end

for i, v in pairs(ship) do
    _ENV[i] = v or _ENV[i]
end

local function inverseQuat(q)
    return {w=q.w, x=-q.x, y=-q.y, z=-q.z}
end

local function vectorFromTable(v)
    return vector.new(v.x, v.y, v.z)
end

local function isVector(v)
    return (type(v) == "table") and (getmetatable(v) == getmetatable(vector.new()))
end

local function transformDirection(q, d)
    local u = vector.new(q.x, q.y, q.z)
    return u * (u * 2):dot(d) + d*(q.w * q.w - u:dot(u)) + (u * 2 * q.w):cross(d)
end

local function transformPosition(quat, pos, from, to)
    local dir = pos - from
    return to + transformDirection(quat, dir)
end

shipyardToWorld = {}
worldToShipyard = {}

function getWorldspaceAABB()
    local q = ship.getQuaternion()
    local aabb = ship.getShipyardAABB()

    local front = {top = {}, bottom = {}}
    local back = {top = {}, bottom = {}}
    
    front.top.right = shipyardToWorld.transformPosition(vector.new(aabb.max_x, aabb.max_y, aabb.max_z))
    front.top.left  = shipyardToWorld.transformPosition(vector.new(aabb.max_x, aabb.max_y, aabb.min_z))

    front.bottom.right  = shipyardToWorld.transformPosition(vector.new(aabb.max_x, aabb.min_y, aabb.max_z))
    front.bottom.left   = shipyardToWorld.transformPosition(vector.new(aabb.max_x, aabb.min_y, aabb.min_z))

    back.top.right  = shipyardToWorld.transformPosition(vector.new(aabb.min_x, aabb.max_y, aabb.max_z))
    back.top.left   = shipyardToWorld.transformPosition(vector.new(aabb.min_x, aabb.max_y, aabb.min_z))
    
    back.bottom.right   = shipyardToWorld.transformPosition(vector.new(aabb.min_x, aabb.min_y, aabb.max_z))
    back.bottom.left    = shipyardToWorld.transformPosition(vector.new(aabb.min_x, aabb.min_y, aabb.min_z))
    
    return {front=front, back=back}
end

function getShipyardOmega()
    local o = ship.getOmega()
    local q = ship.getQuaternion()
    return transformDirection(q, vectorFromTable(o))
end

function shipyardToWorld.transformPosition(pos)
    assert(isVector(pos) , "bad argument #1 'position' (vector expected, got "..type(pos)..")")
    
    local world = vectorFromTable(ship.getWorldspacePosition())
    local shipyard = vectorFromTable(ship.getShipyardPosition())
    local quat = ship.getQuaternion()
    
    return transformPosition(quat, pos, shipyard, world)
end

function worldToShipyard.transformPosition(pos)
    assert(isVector(pos) , "bad argument #1 'position' (vector expected, got "..type(pos)..")")

    local world = vectorFromTable(ship.getWorldspacePosition())
    local shipyard = vectorFromTable(ship.getShipyardPosition())
    local quat = ship.getQuaternion()
    
    return transformPosition(inverseQuat(quat), pos, world, shipyard)
end

function shipyardToWorld.transformDirection(dir)
    assert(isVector(dir) , "bad argument #1 'direction' (vector expected, got "..type(dir)..")")
    local quat = ship.getQuaternion()
    return transformDirection(quat, dir)
end

function worldToShipyard.transformDirection(dir)
    assert(isVector(dir) , "bad argument #1 'direction' (vector expected, got "..type(dir)..")")
    local quat = ship.getQuaternion()
    return transformDirection(inverseQuat(quat), dir)
end

function pullBlockChanges()
    local mass = ship.getMass()
    local shipyard = vectorFromTable(ship.getShipyardPosition())
    
    repeat sleep()
    until not (ship.getMass() == mass)

    sleep()
    
    local new_mass = ship.getMass()
    local change_mass = new_mass - mass

    local change_shipyard = vectorFromTable(ship.getShipyardPosition()) - shipyard
    local offset = change_shipyard / (change_mass / new_mass)

    local shipyard_pos = shipyard + offset
    
    local world_pos = shipyardToWorld.transformPosition(shipyard_pos)
    
    return world_pos, shipyard_pos - vector.new(0.5, 0.5, 0.5), change_mass
end
