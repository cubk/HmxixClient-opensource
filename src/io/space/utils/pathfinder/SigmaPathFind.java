package io.space.utils.pathfinder;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class SigmaPathFind {
    private final SigmaVec3 startVec3;
    private final SigmaVec3 endVec3;
    private ArrayList<SigmaVec3> path = new ArrayList<>();
    private final ArrayList<Hub> hubs = new ArrayList<>();
    private final ArrayList<Hub> hubsToWork = new ArrayList<>();
    private final double minDistanceSquared = 9.0;
    private final boolean nearest = true;
    private static final SigmaVec3[] flatCardinalDirections = new SigmaVec3[]{new SigmaVec3(1.0, 0.0, 0.0), new SigmaVec3(-1.0, 0.0, 0.0), new SigmaVec3(0.0, 0.0, 1.0), new SigmaVec3(0.0, 0.0, -1.0)};

    public SigmaPathFind(SigmaVec3 startVec3, SigmaVec3 endVec3) {
        this.startVec3 = startVec3.addVector(0.0, 0.0, 0.0).floor();
        this.endVec3 = endVec3.addVector(0.0, 0.0, 0.0).floor();
    }

    public ArrayList<SigmaVec3> getPath() {
        return this.path;
    }

    public void compute() {
        this.compute(1000, 4);
    }

    public void compute(int loops, int depth) {
        this.path.clear();
        this.hubsToWork.clear();
        ArrayList<SigmaVec3> initPath = new ArrayList<SigmaVec3>();
        initPath.add(this.startVec3);
        this.hubsToWork.add(new Hub(this.startVec3, null, initPath, this.startVec3.squareDistanceTo(this.endVec3), 0.0, 0.0));
        block0: for (int i = 0; i < loops; ++i) {
            Collections.sort(this.hubsToWork, new CompareHub());
            int j = 0;
            if (this.hubsToWork.size() == 0) break;
            for (Hub hub : new ArrayList<Hub>(this.hubsToWork)) {
                SigmaVec3 loc2;
                if (++j > depth) continue block0;
                this.hubsToWork.remove(hub);
                this.hubs.add(hub);
                for (SigmaVec3 direction : flatCardinalDirections) {
                    SigmaVec3 loc = hub.getLoc().add(direction).floor();
                    if (SigmaPathFind.checkPositionValidity(loc, false) && this.addHub(hub, loc, 0.0)) break block0;
                }
                SigmaVec3 loc1 = hub.getLoc().addVector(0.0, 1.0, 0.0).floor();
                if ((!SigmaPathFind.checkPositionValidity(loc1, false) || !this.addHub(hub, loc1, 0.0)) && (!SigmaPathFind.checkPositionValidity(loc2 = hub.getLoc().addVector(0.0, -1.0, 0.0).floor(), false) || !this.addHub(hub, loc2, 0.0))) continue;
                break block0;
            }
        }
        if (this.nearest) {
            this.hubs.sort(new CompareHub());
            this.path = this.hubs.get(0).getPath();
        }
    }

    public static boolean checkPositionValidity(SigmaVec3 loc, boolean checkGround) {
        return SigmaPathFind.checkPositionValidity((int)loc.getX(), (int)loc.getY(), (int)loc.getZ(), checkGround);
    }

    public static boolean checkPositionValidity(int x, int y, int z, boolean checkGround) {
        BlockPos block1 = new BlockPos(x, y, z);
        BlockPos block2 = new BlockPos(x, y + 1, z);
        BlockPos block3 = new BlockPos(x, y - 1, z);
        return !SigmaPathFind.isBlockSolid(block1) && !SigmaPathFind.isBlockSolid(block2) && (SigmaPathFind.isBlockSolid(block3) || !checkGround) && SigmaPathFind.isSafeToWalkOn(block3);
    }

    private static boolean isBlockSolid(BlockPos block) {
        return Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()).isSolidFullCube() ||
                (Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockSlab) ||
                (Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockStairs)||
                (Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockCactus)||
                (Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockChest)||
                (Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockEnderChest)||
                (Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockSkull)||
                (Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockPane)||
                (Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockFence)||
                (Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockWall)||
                (Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockGlass)||
                (Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockPistonBase)||
                (Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockPistonExtension)||
                (Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockPistonMoving)||
                (Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockStainedGlass)||
                (Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockTrapDoor);
    }

    private static boolean isSafeToWalkOn(BlockPos block) {
        return !(Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockFence) && !(Minecraft.getMinecraft().world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockWall);
    }


    public Hub isHubExisting(SigmaVec3 loc) {
        for (Hub hub : this.hubs) {
            if (hub.getLoc().getX() != loc.getX() || hub.getLoc().getY() != loc.getY() || hub.getLoc().getZ() != loc.getZ()) continue;
            return hub;
        }
        for (Hub hub : this.hubsToWork) {
            if (hub.getLoc().getX() != loc.getX() || hub.getLoc().getY() != loc.getY() || hub.getLoc().getZ() != loc.getZ()) continue;
            return hub;
        }
        return null;
    }

    public boolean addHub(Hub parent, SigmaVec3 loc, double cost) {
        Hub existingHub = this.isHubExisting(loc);
        double totalCost = cost;
        if (parent != null) {
            totalCost += parent.getTotalCost();
        }
        if (existingHub == null) {
            if (loc.getX() == this.endVec3.getX() && loc.getY() == this.endVec3.getY() && loc.getZ() == this.endVec3.getZ() || this.minDistanceSquared != 0.0 && loc.squareDistanceTo(this.endVec3) <= this.minDistanceSquared) {
                this.path.clear();
                this.path = parent.getPath();
                this.path.add(loc);
                return true;
            }
            ArrayList<SigmaVec3> path = new ArrayList<SigmaVec3>(parent.getPath());
            path.add(loc);
            this.hubsToWork.add(new Hub(loc, parent, path, loc.squareDistanceTo(this.endVec3), cost, totalCost));
        } else if (existingHub.getCost() > cost) {
            ArrayList<SigmaVec3> path = new ArrayList<SigmaVec3>(parent.getPath());
            path.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent(parent);
            existingHub.setPath(path);
            existingHub.setSquareDistanceToFromTarget(loc.squareDistanceTo(this.endVec3));
            existingHub.setCost(cost);
            existingHub.setTotalCost(totalCost);
        }
        return false;
    }

    public static class CompareHub implements Comparator<Hub> {
        @Override
        public int compare(Hub o1, Hub o2) {
            return (int)(o1.getSquareDistanceToFromTarget() + o1.getTotalCost() - (o2.getSquareDistanceToFromTarget() + o2.getTotalCost()));
        }
    }

    private static class Hub {
        private SigmaVec3 loc = null;
        private Hub parent = null;
        private ArrayList<SigmaVec3> path;
        private double squareDistanceToFromTarget;
        private double cost;
        private double totalCost;

        public Hub(SigmaVec3 loc, Hub parent, ArrayList<SigmaVec3> path, double squareDistanceToFromTarget, double cost, double totalCost) {
            this.loc = loc;
            this.parent = parent;
            this.path = path;
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
            this.cost = cost;
            this.totalCost = totalCost;
        }

        public SigmaVec3 getLoc() {
            return this.loc;
        }

        public Hub getParent() {
            return this.parent;
        }

        public ArrayList<SigmaVec3> getPath() {
            return this.path;
        }

        public double getSquareDistanceToFromTarget() {
            return this.squareDistanceToFromTarget;
        }

        public double getCost() {
            return this.cost;
        }

        public void setLoc(SigmaVec3 loc) {
            this.loc = loc;
        }

        public void setParent(Hub parent) {
            this.parent = parent;
        }

        public void setPath(ArrayList<SigmaVec3> path) {
            this.path = path;
        }

        public void setSquareDistanceToFromTarget(double squareDistanceToFromTarget) {
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public double getTotalCost() {
            return this.totalCost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }
    }
}
