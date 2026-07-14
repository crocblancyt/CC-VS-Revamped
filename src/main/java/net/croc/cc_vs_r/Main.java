package net.croc.cc_vs_r;

import com.mojang.logging.LogUtils;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.shared.computer.core.ServerContext;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import net.croc.cc_vs_r.apis.*;

@Mod("cc_vs_r")
@EventBusSubscriber(modid = "cc_vs_r", bus = Mod.EventBusSubscriber.Bus.MOD)
public class Main {
    public static final String MOD_ID = "cc_vs_r";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static ServerContext context = null;

    public Main() {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        RegistryConfigs.register(modLoadingContext);
        ComputerCraftAPI.registerAPIFactory(ShipAPI::new);
        ComputerCraftAPI.registerAPIFactory(VSGameUtilsAPI::new);
        LifecycleEvent.SERVER_STARTED.register(minecraftServer -> context = ServerContext.get(minecraftServer));
    }
}
