package net.croc.cc_vs_r;

import com.mojang.logging.LogUtils;
import dan200.computercraft.api.ComputerCraftAPI;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.valkyrienskies.core.impl.hooks.VSEvents;

import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.eventbus.api.IEventBus;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraft.client.model.geom.ModelLayerLocation;

import net.croc.cc_vs_r.apis.ShipAPI;

import static net.croc.cc_vs_r.Main.MOD_ID;

@Mod(MOD_ID)
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Main {
    public static final String MOD_ID = "cc_vs_r";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Main() {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
        RegistryConfigs.register(modLoadingContext);

        //LifecycleEvent.SERVER_STARTED.register(Main::init$lambda$0);
        ComputerCraftAPI.registerAPIFactory(ShipAPI::new);
        //VSEvents.INSTANCE.getShipLoadEvent().on(Main::onShipLoaded);
        //VSEvents.INSTANCE.getTickEndEvent().on(Main::onTick);
    }

    /*private static final void init$lambda$0(MinecraftServer it) {
        Intrinsics.checkNotNullExpressionValue(ServerContext.get(it), "get(...)");
        INSTANCE.setContext(ServerContext.get(it));
    }*/

    /*private static final void onShipLoaded(VSEvents.ShipLoadEvent event) {
        //event.getShip().setAttachment(PhysTickEventHandler.class, null);
    }*/

    /* static final void onTick(VSEvents.TickEndEvent event) {
        Iterable $this$forEach$iv = (Iterable)event.getWorld().getLoadedShips();
        int $i$f$forEach = 0;
        Iterator iterator = $this$forEach$iv.iterator();
        if (iterator.hasNext()) {
            Object element$iv = iterator.next();
            ShipObjectServer ship = (ShipObjectServer)element$iv;
            int $i$a$-forEach-CCVSMod$init$4$1 = 0;
            PhysicsTicksEventHandler.Companion.getOrCreateControl((ServerShip)ship).resetData();
        }
    }*/
}
