package thelm.jaopca.compat.flux;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemFormType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "flux@[4.16.1,)")
public class FluxCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "copper", "gold", "iron", "lead", "osmium", "silver", "steel", "tin"));
	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "copper", "gold", "iron", "steel", "tin"));

	@Override
	public String getName() {
		return "flux_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		FluxHelper helper = FluxHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			if(!ArrayUtils.contains(MaterialType.DUSTS, type) && !TO_DUST_BLACKLIST.contains(material.getName())) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					helper.registerGrindingRecipe(
							new ResourceLocation("jaopca", "flux.material_to_dust."+material.getName()),
							materialLocation, 1, dustLocation, 1, 0F, 200);
				}
			}
			if(ArrayUtils.contains(MaterialType.INGOTS, type) && !TO_PLATE_BLACKLIST.contains(material.getName())) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", material.getName());
				if(api.getItemTags().contains(plateLocation)) {
					helper.registerCompactingRecipe(
							new ResourceLocation("jaopca", "flux.material_to_plate."+material.getName()),
							materialLocation, 1, plateLocation, 1, 0F, 200);
				}
			}
		}
	}
}
