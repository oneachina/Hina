/**
 * @Author: oneachina
 * @link: github.com/oneachina
 */
package com.hinaclient.hina.utils.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WorldUtils {
    public static double getGroundY(Player player) {
        Level level = player.level();

        for (int i = 0; i < 5; i++) {
            BlockPos pos = new BlockPos(
                    (int) Math.floor(player.getX()),
                    (int) Math.floor(player.getY() - i),
                    (int) Math.floor(player.getZ())
            );

            BlockState state = level.getBlockState(pos);

            if (state.isAir()) {
                continue;
            }

            VoxelShape shape = state.getCollisionShape(level, pos);

            if (shape.isEmpty()) {
                continue;
            }

            double maxY = shape.max(Direction.Axis.Y);
            return pos.getY() + maxY;
        }

        return -999;
    }

    public static boolean isBlockUnder(Player player) {
        return getGroundY(player) != -999;
    }
}
