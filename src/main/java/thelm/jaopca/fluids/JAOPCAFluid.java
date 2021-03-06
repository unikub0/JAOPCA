package thelm.jaopca.fluids;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.FluidAttributes;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.PlaceableFluid;
import thelm.jaopca.api.fluids.PlaceableFluidBlock;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public class JAOPCAFluid extends PlaceableFluid implements IMaterialFormFluid {

	private final IForm form;
	private final IMaterial material;
	protected final IFluidFormSettings settings;

	protected OptionalInt tickRate = OptionalInt.empty();
	protected OptionalDouble explosionResistance = OptionalDouble.empty();
	protected Optional<Boolean> canSourcesMultiply = Optional.empty();
	protected Optional<Boolean> canFluidBeDisplaced = Optional.empty();
	protected OptionalInt levelDecreasePerBlock = OptionalInt.empty();

	public JAOPCAFluid(IForm form, IMaterial material, IFluidFormSettings settings) {
		super(settings.getMaxLevelFunction().applyAsInt(material));
		this.form = form;
		this.material = material;
		this.settings = settings;
	}

	@Override
	public IForm getForm() {
		return form;
	}

	@Override
	public IMaterial getMaterial() {
		return material;
	}

	@Override
	public int getTickRate(IWorldReader world) {
		if(!tickRate.isPresent()) {
			tickRate = OptionalInt.of(settings.getTickRateFunction().applyAsInt(material));
		}
		return tickRate.getAsInt();
	}

	@Override
	protected float getExplosionResistance() {
		if(!explosionResistance.isPresent()) {
			explosionResistance = OptionalDouble.of(settings.getExplosionResistanceFunction().applyAsDouble(material));
		}
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	protected boolean canSourcesMultiply() {
		if(!canSourcesMultiply.isPresent()) {
			canSourcesMultiply = Optional.of(settings.getCanSourcesMultiplyFunction().test(material));
		}
		return canSourcesMultiply.get();
	}

	@Override
	protected int getLevelDecreasePerBlock(IWorldReader world) {
		if(!levelDecreasePerBlock.isPresent()) {
			levelDecreasePerBlock = OptionalInt.of(settings.getLevelDecreasePerBlockFunction().applyAsInt(material));
		}
		return levelDecreasePerBlock.getAsInt();
	}

	@Override
	protected FluidAttributes createAttributes() {
		return settings.getFluidAttributesCreator().create(this, settings);
	}

	@Override
	public Item getFilledBucket() {
		return FluidFormType.INSTANCE.getMaterialFormInfo(form, material).getBucketItem();
	}

	@Override
	protected PlaceableFluidBlock getFluidBlock() {
		return (PlaceableFluidBlock)FluidFormType.INSTANCE.getMaterialFormInfo(form, material).getMaterialFormFluidBlock().asBlock();
	}

	@Override
	public IFluidState getSourceState() {
		return getDefaultState().with(levelProperty, maxLevel);
	}
}
