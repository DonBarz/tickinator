package de.donbarz.tickinator;

import dev.lemonnik.fern_config.CConfig;
import dev.lemonnik.fern_config.CExporter;
import dev.lemonnik.fern_config.types.*;
import dev.lemonnik.fern_config.utils.CCategory;
import dev.lemonnik.fern_config.utils.CValue;
import dev.lemonnik.fern_config.utils.MaskType;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockConfig extends CConfig {

    @Override
    public @NotNull String getFileName() {
        return Tickinator.MOD_ID;
    }

    @Override
    protected @NotNull CExporter.Format getFormat() {
        return CExporter.Format.JSON5;
    }

    private static final CCategory COMPLEX_THINGS = CCategory.of("tickinator block entity suppressing", "Suppressed blocks entities WILL NOT function but also not impede performance.", "Very experimental, handle with care");

    //public final CEnum<TestEnum> test_enum = register(COMPLEX_THINGS, new CEnum<>("thats_enum", "Only values listed above this comment are acceptable", TestEnum.class, TestEnum.TASTY_ENUM));

    public final CMask include_mask = updateIncludeMask(new String[0]);

    public final CMask exclude_mask = updateExcludeMask(new String[0]);

    public CMask updateIncludeMask (String[] newMaskStr) {
        return register(COMPLEX_THINGS, new CMask(
                        "include_list",
                        "Add blocks here and their entities shall be suppressed",
                        BuiltInRegistries.BLOCK,
                        new CEnum<MaskType>(
                                "mask_type",
                                "",
                                MaskType.class,
                                MaskType.WHITELIST
                        ),
                        newMaskStr
                )
        );
    }

    public CMask updateExcludeMask (String[] newMaskStr) {
        return register(COMPLEX_THINGS, new CMask(
                        "exclude_list",
                        "Add blocks to exclude them from suppression. If added to both, this has priority",
                        BuiltInRegistries.BLOCK,
                        new CEnum<MaskType>(
                                "mask_type",
                                "",
                                MaskType.class,
                                MaskType.BLACKLIST
                        ),
                        newMaskStr
                )
        );
    }
}
