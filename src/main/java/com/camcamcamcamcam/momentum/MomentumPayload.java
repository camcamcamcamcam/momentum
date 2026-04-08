package com.camcamcamcamcam.momentum;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MomentumPayload(String lastBlockMined, int blocksMined, String lastItemHeld) implements CustomPacketPayload {


    public static final CustomPacketPayload.Type<MomentumPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Momentum.MODID, "block_name"));

    public static final StreamCodec<ByteBuf, MomentumPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            MomentumPayload::lastBlockMined,
            ByteBufCodecs.VAR_INT,
            MomentumPayload::blocksMined,
            ByteBufCodecs.STRING_UTF8,
            MomentumPayload::lastItemHeld,
            MomentumPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
