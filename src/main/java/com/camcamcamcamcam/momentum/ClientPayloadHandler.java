package com.camcamcamcamcam.momentum;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {

    public static void handleDataOnMain(final MomentumPayload data, final IPayloadContext context) {
        context.player().setData(Momentum.LAST_BLOCK_MINED_1, data.lastBlockMined1());
        context.player().setData(Momentum.BLOCKS_MINED_1, data.blocksMined1());
        context.player().setData(Momentum.LAST_BLOCK_MINED_2, data.lastBlockMined2());
        context.player().setData(Momentum.BLOCKS_MINED_2, data.blocksMined2());
        context.player().setData(Momentum.LAST_ITEM_HELD, data.lastItemHeld());
    }
}