
if pocket then
    return
end

for i, v in pairs(ship) do
    _ENV[i] = v or _ENV[i]
end

shipyardToWorld = { }
worldToShipyard = { }

local function implementTransform(target, target_name, original_name)
    local method = ship[original_name]
    ship[original_name] = nil

    target[target_name] = function(v)
        local value = method(v.x, v.y, v.z)
        return vector.new(value.x, value.y, value.z)
    end
end

implementTransform(shipyardToWorld, "transformPosition", "transformPositionToWorld")
implementTransform(shipyardToWorld, "transformDirection", "transformDirectionToWorld")
implementTransform(worldToShipyard, "transformPosition", "transformPositionToShip")
implementTransform(worldToShipyard, "transformDirection", "transformDirectionToShip")

local function vectorFromTable(v)
    return vector.new(v.x, v.y, v.z)
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
