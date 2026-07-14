package net.croc.cc_vs_r;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import java.nio.file.Path;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;

public class RegistryConfigs {
    public static void register(ModLoadingContext modLoadingContext) {}

    public static class Config {
        static { }

        public static void loadConfig(ForgeConfigSpec config, Path path) {
            CommentedFileConfig file = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
            file.load();
            config.setConfig(file);
        }
    }
}

