package wraith.fwaystones.forge;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import wraith.fwaystones.registry.BlockEntityRendererRegister;

import java.util.function.Consumer;

public class WaystonesClient {
	public static void init(IEventBus eventBus) {
		eventBus.addListener((Consumer<EntityRenderersEvent.RegisterRenderers>) event -> {
			BlockEntityRendererRegister.register(event::registerBlockEntityRenderer);
		});
		ClientLifecycleEvent.CLIENT_SETUP.register(wraith.fwaystones.WaystonesClient::init);
	}
}
