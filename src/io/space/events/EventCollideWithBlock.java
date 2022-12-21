package io.space.events;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import java.util.List;

public final class EventCollideWithBlock implements Event {
    private final Block block;
    private final BlockPos blockPos;
    private AxisAlignedBB boundingBox;
    private final List<AxisAlignedBB> boxes;
    private final IBlockState state;

    public EventCollideWithBlock(Block block, BlockPos pos, AxisAlignedBB boundingBox, IBlockState state, List<AxisAlignedBB> boxes) {
        this.block = block;
        this.blockPos = pos;
        this.boundingBox = boundingBox;
        this.state = state;
        this.boxes = boxes;
    }

    public Block getBlock() {
        return block;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    public List<AxisAlignedBB> getBoxes() {
        return boxes;
    }

    public IBlockState getState() {
        return state;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }
}
