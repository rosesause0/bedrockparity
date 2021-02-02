package com.rosesause.bedrockpairity.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class LavaCauldronBlock extends CauldronBlock {

    /**
     *
     * @param properties
     */
    public LavaCauldronBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(LEVEL, Integer.valueOf(3)));
    }

    /**
     * Does less damage and fire ticks than regular lava
     */
    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (!entityIn.isImmuneToFire()) {
            entityIn.forceFireTicks(entityIn.getFireTimer() + 1);
            if (entityIn.getFireTimer() == 0) {
                entityIn.setFire(100);
            }

            entityIn.attackEntityFrom(DamageSource.IN_FIRE, 3.0f);
        }

        super.onEntityCollision(state, worldIn, pos, entityIn);
    }

    /**
     * Fills bucket with lava and when so it does this fun thing that turns the block back into a vanilla old cauldron
     *
     */
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack itemstack = player.getHeldItem(handIn);

        if (!itemstack.isEmpty()) {
            int i = state.get(LEVEL);
            Item item = itemstack.getItem();

            if (item == Items.BUCKET) {
                if (i == 3 && !worldIn.isRemote) {
                    if (!player.abilities.isCreativeMode) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            player.setHeldItem(handIn, new ItemStack(Items.LAVA_BUCKET));
                        } else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.LAVA_BUCKET))) {
                            player.dropItem(new ItemStack(Items.LAVA_BUCKET), false);
                        }
                    }

                    player.addStat(Stats.USE_CAULDRON);
                    this.setWaterLevel(worldIn, pos, state, 0);
                    worldIn.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    worldIn.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                }
                return ActionResultType.func_233537_a_(worldIn.isRemote);
            }
        }

        return ActionResultType.PASS;
    }

    /**
     * Unless lava rain exists then cool i guess
     */
    @Override
    public void fillWithRain(World worldIn, BlockPos pos) {

    }
}