package com.camcamcamcamcam.momentum;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Momentum.MODID)
public class PayloadRegister {
    @SubscribeEvent // on the mod event bus
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1")
                .executesOn(HandlerThread.NETWORK);
        registrar.playBidirectional(
                MomentumPayload.TYPE,
                MomentumPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleDataOnMain,
                        ServerPayloadHandler::handleDataOnMain
                )
        );
    }
}
