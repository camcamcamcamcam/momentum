package com.camcamcamcamcam.momentum;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MomentumPayload(String lastBlockMined1, int blocksMined1, String lastBlockMined2, int blocksMined2, String lastItemHeld) implements CustomPacketPayload {


    public static final CustomPacketPayload.Type<MomentumPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Momentum.MODID, "block_name"));

    public static final StreamCodec<ByteBuf, MomentumPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            MomentumPayload::lastBlockMined1,
            ByteBufCodecs.VAR_INT,
            MomentumPayload::blocksMined1,
            ByteBufCodecs.STRING_UTF8,
            MomentumPayload::lastBlockMined2,
            ByteBufCodecs.VAR_INT,
            MomentumPayload::blocksMined2,
            ByteBufCodecs.STRING_UTF8,
            MomentumPayload::lastItemHeld,
            MomentumPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
