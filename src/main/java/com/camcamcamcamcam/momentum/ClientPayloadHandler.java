package com.camcamcamcamcam.momentum;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {

    public static void handleDataOnMain(final MomentumPayload data, final IPayloadContext context) {
        context.player().setData(Momentum.LAST_BLOCK_MINED, data.lastBlockMined());
        context.player().setData(Momentum.BLOCKS_MINED, data.blocksMined());
        context.player().setData(Momentum.LAST_ITEM_HELD, data.lastItemHeld());
    }
}