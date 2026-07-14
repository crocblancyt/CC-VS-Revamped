
-- quaternion class

local quaternion = _ENV
local mt = {__index = quaternion}

local abs = math.abs
local max = math.max
local acos = math.acos
local sin = math.sin
local cos = math.cos
local sqrt = math.sqrt

local expect = dofile("rom/modules/main/cc/expect.lua").expect

-- private methods

local tolerance = 1e-5
local identity = {
    w = 1, x = 0, y = 0, z = 0
}

local function getPerpendicular(v)
    local axis = v:cross(vector.new(0,1,0))
    
    if axis:dot(axis) < tolerance then
        axis = v:cross(vector.new(1,0,0))
    end
    
    return axis:normalize()
end

local function isVector(value)
    return (type(value) == "table") and (getmetatable(value) == getmetatable(vector.new()))
end

local function isQuaternion(value)
    return (type(value) == "table") and (getmetatable(value) == mt)
end

-- public methods

function new(...)
    local args = {...}
    
    expect(1, args[1], "number", "table", "nil")
    expect(2, args[2], "number", "nil")
    expect(3, args[3], "number", "nil")
    expect(4, args[4], "number", "nil")

    local w, x, y, z
    
    if type(args[1]) == "table" then
        local args = args[1]

        if #args == 0 then
            w, x, y, z = args.w, args.x, args.y, args.z
        else
            w, x, y, z = unpack(args)
        end
    elseif #args == 0 then
        return new(identity)
    else
        w, x, y, z = ...
    end

    return setmetatable({
        w=w or 0,
        x=x or 0,
        y=y or 0,
        z=z or 0
    }, mt)
end

function fromAxisAngle(...)
    local args = {...}
    
    assert(isVector(args[1]), "bad argument #1 to '?' (vector expected, got "..type(args[1]).." )")
    expect(2, args[2], "number", "nil")
    

    local len = #args
    local axis, angle

    if len >= 2 then
        axis, angle = ...
    elseif len == 1 then -- extracing the direction vector & angle
        local omega = ...
        angle = omega:length()
        
        if angle == 0 then
            return new() -- no angle & no direction vector
        end

        axis = omega * (1 / angle)
    else
        return new()
    end
    
    local half_angle = angle / 2
    local sin_half = sin(half_angle)

    return new{
        w = cos(half_angle),
        x = axis.x * sin_half,
        y = axis.y * sin_half,
        z = axis.z * sin_half,
    };
end

function fromVectors(v1, v2)
    local len1, len2 = v1:length(), v2:length()
    
    assert(isVector(v1), "bad argument #1 to '?' (vector expected, got "..type(v1).." )")
    assert(isVector(v2), "bad argument #2 to '?' (vector expected, got "..type(v2).." )")
    
    assert(len1 > tolerance, "bad argument #1 to vector (length of 0)")
    assert(len2 > tolerance, "bad argument #2 to vector (length of 0)")

    v1 = v1 * (1 / len1)
    v2 = v2 * (1 / len2)
    
    local axis = v1:cross(v2)
    local angle = v1:dot(v2)
    
    local w, x, y, z
    
    if abs(axis:dot(axis)) < tolerance then -- invalid axis
        if angle > 0 then
            w, x, y, z = 1, 0, 0, 0
        else
            axis = getPerpendicular(v1)
            w, x, y, z = 0, axis.x, axis.y, axis.z
        end
    end
    
    return new{w=w, x=x, y=y, z=z}:normalize()
end

function fromMatrix(m)
	-- taken from: http://wiki.roblox.com/index.php?title=Quaternions_for_rotation#Quaternion_from_a_Rotation_Matrix
	local m11, m12, m13 = m[1][1], m[1][2], m[1][3]
    local m21, m22, m23 = m[2][1], m[2][2], m[2][3]
    local m31, m32, m33 = m[3][1], m[3][2], m[3][3]
	local trace = m11 + m22 + m33;
	if (trace > 0) then
		local s = sqrt(1 + trace);
		local r = 0.5 / s;
		return s * 0.5, {(m32 - m23) * r, (m13 - m31) * r, (m21 - m12) * r};
	else -- find the largest diagonal element
		local big = max(m11, m22, m33);
		if big == m11 then
			local s = sqrt(1 + m11 - m22 - m33);
			local r = 0.5 / s;
			return (m32 - m23) * r, {0.5 * s, (m21 + m12) * r, (m13 + m31) * r};
		elseif big == m22 then
			local s = sqrt(1 - m11 + m22 - m33);
			local r = 0.5 / s;
			return (m13 - m31) * r, {(m21 + m12) * r, 0.5 * s, (m32 + m23) * r};
		elseif big == m33 then
			local s = sqrt(1 - m11 - m22 + m33);
			local r = 0.5 / s;
			return (m21 - m12) * r, {(m13 + m31) * r, (m32 + m23) * r, 0.5 * s};
		end;
	end;
end;

-- meta-methods

function mt.__add(self, q)
    assert(isQuaternion(q), "attempt to add a 'quaternion' with a '"..type(q).."'")
    return new{
        w = self.w + q.w,
        x = self.x + q.x,
        y = self.y + q.y,
        z = self.z + q.z
    }
end

function mt.__sub(self, q)
    assert(isQuaternion(q), "attempt to sub a 'quaternion' with a '"..type(q).."'")
    return new{
        w = self.w - q.w,
        x = self.x - q.x,
        y = self.y - q.y,
        z = self.z - q.z
    }
end

function mt.__mul(self, q)
    assert(isQuaternion(q), "attempt to mul a 'quaternion' with a '"..type(q).."'")

    local q1, q2 = self, q
    local x1, y1, z1, w1 = q1.x, q1.y, q1.z, q1.w
    local x2, y2, z2, w2 = q2.x, q2.y, q2.z, q2.w
    
    return new{
        w = w1*w2 - x1*x2 - y1*y2 - z1*z2,
        x = w1*x2 + x1*w2 + y1*z2 - z1*y2,
        y = w1*y2 - x1*z2 + y1*w2 + z1*x2,
        z = w1*z2 + x1*y2 - y1*x2 + z1*w2,
    }
end

function mt.__div(self, q)
    assert(isQuaternion(q), "attempt to div a 'quaternion' with a '"..type(q).."'")
    return self * q:inverse()
end

function mt.__unm(self)
    return self:inverse()
end

function mt.__tostring(self)
    return self.w..'w '..self.x..'x '..self.y..'y '..self.z..'z';
end

-- instance methods

function quaternion:length()
    local w, x, y, z = self.w, self.x, self.y, self.z
    return sqrt(w*w + x*x + y*y + z*z)
end

function quaternion:normalize()
    local w, x, y, z = self.w, self.x, self.y, self.z
    local norm = sqrt(w*w + x*x + y*y + z*z)

    if norm == 0 then return new() end

    local inv_norm = 1 / norm
    
    return new{
        w = w * inv_norm,
        x = x * inv_norm,
        y = y * inv_norm,
        z = z * inv_norm
    }
end

function quaternion:conjugate()
    return new{
        w = self.w,
        x = -self.x,
        y = -self.y,
        z = -self.z
    }
end

function quaternion:inverse()
    local w, x, y, z = self.w, self.x, self.y, self.z
    local norm_sq = w*w + x*x + y*y + z*z
    
    if norm_sq == 1 then return self:conjugate() end
    if norm_sq == 0 then return new() end

    local inv_norm_sq = 1 / norm_sq
    
    return new{
        w = w * inv_norm_sq,
        x = -x * inv_norm_sq,
        y = -y * inv_norm_sq,
        z = -z * inv_norm_sq,
    }
end

function quaternion:transformVector(v)
    local q = self
    assert(isQuaternion(q), "bad argument #1 to '?' (quaternion expected, got "..type(q).." )")
    assert(isVector(v), "bad argument #2 to '?' (vector expected, got "..type(v).." )")

    -- math from: https://gamedev.stackexchange.com/questions/28395/rotating-vector3-by-a-quaternion
    local u = vector.new(q.x, q.y, q.z)
    return u * (u * 2):dot(v) + v*(q.w * q.w - u:dot(u)) + (u * 2 * q.w):cross(v)
end

function quaternion:toAxisAngle()
    self = self:normalize()
    
    local angle = acos(self.w) * 2
    local inv_sin_half = 1 / sin(angle / 2)
    local axis = vector.new(self.x, self.y, self.z) * inv_sin_half
    
    return axis, angle
end;

function quaternion:toMatrix(self)
    local w, i, j, k = self.w, self.x, self.y, self.z
    
	local m11 = 1 - 2*j^2 - 2*k^2;
	local m12 = 2*(i*j - k*w);
	local m13 = 2*(i*k + j*w);
	local m21 = 2*(i*j + k*w);
	local m22 = 1 - 2*i^2 - 2*k^2;
	local m23 = 2*(j*k - i*w);
	local m31 = 2*(i*k - j*w);
	local m32 = 2*(j*k + i*w);
	local m33 = 1 - 2*i^2 - 2*j^2;
        
	return {
        {m11, m12, m13},
        {m21, m22, m23},
        {m31, m32, m33}
    };
end;