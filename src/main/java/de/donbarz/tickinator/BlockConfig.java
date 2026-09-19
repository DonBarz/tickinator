package de.donbarz.tickinator;

import dev.lemonnik.fern_config.CConfig;
import dev.lemonnik.fern_config.CExporter;
import dev.lemonnik.fern_config.impl.TestEnum;
import dev.lemonnik.fern_config.types.*;
import dev.lemonnik.fern_config.utils.CCategory;
import dev.lemonnik.fern_config.utils.FloatArray;
import dev.lemonnik.fern_config.utils.MaskType;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockConfig extends CConfig {

    @Override
    public @NotNull String getFileName() {
        return Tickinator.MOD_ID;
    }

    @Override
    protected @NotNull CExporter.Format getFormat() {
        return CExporter.Format.JSON5;
    }

    private static final CCategory COMPLEX_THINGS = CCategory.of("complex_category", "Unbasic values", "read carefully");

    //public final CEnum<TestEnum> test_enum = register(COMPLEX_THINGS, new CEnum<>("thats_enum", "Only values listed above this comment are acceptable", TestEnum.class, TestEnum.TASTY_ENUM));

    public final CMask include_mask = register(COMPLEX_THINGS, new CMask(
                    "include_list",
                    "Add blocks here and their entities shall be suppressed",
                    BuiltInRegistries.BLOCK,
                    new CEnum<MaskType>(
                            "mask_type",
                            "",
                            MaskType.class,
                            MaskType.WHITELIST
                    ),
                    "minecraft:furnace"
            )
    );

    public final CMask exclude_mask = register(COMPLEX_THINGS, new CMask(
                    "exclude_list",
                    "Exclude certain blocks. If you add a block to both lists, this will have priority",
                    BuiltInRegistries.BLOCK,
                    new CEnum<MaskType>(
                            "mask_type",
                            "",
                            MaskType.class,
                            MaskType.WHITELIST
                    ),
                    "*:*"
            )
    );
}
