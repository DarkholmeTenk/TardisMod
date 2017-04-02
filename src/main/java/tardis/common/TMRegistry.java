package tardis.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.common.collect.Sets;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItem;
import io.darkcraft.darkcore.mod.helpers.RegistryHelper;
import io.darkcraft.darkcore.mod.helpers.RegistryHelper.CustomRegistryItem;
import io.darkcraft.darkcore.mod.helpers.RegistryHelper.RegistryIgnore;

import tardis.Configs;
import tardis.common.blocks.AdvancedLabBlock;
import tardis.common.blocks.BatteryBlock;
import tardis.common.blocks.ColorableBlock;
import tardis.common.blocks.ColorableOpenRoundelBlock;
import tardis.common.blocks.ComponentBlock;
import tardis.common.blocks.CompressedBlock;
import tardis.common.blocks.ConsoleBlock;
import tardis.common.blocks.CoreBlock;
import tardis.common.blocks.CraftableCSimBlock;
import tardis.common.blocks.CraftableSimBlock;
import tardis.common.blocks.DebugBlock;
import tardis.common.blocks.DecoBlock;
import tardis.common.blocks.DecoTransBlock;
import tardis.common.blocks.EngineBlock;
import tardis.common.blocks.ForceFieldBlock;
import tardis.common.blocks.GravityLiftBlock;
import tardis.common.blocks.InteriorDirtBlock;
import tardis.common.blocks.InternalDoorBlock;
import tardis.common.blocks.InternalMagicDoorBlock;
import tardis.common.blocks.LabBlock;
import tardis.common.blocks.LandingPadBlock;
import tardis.common.blocks.ManualBlock;
import tardis.common.blocks.ManualHelperBlock;
import tardis.common.blocks.SchemaBlock;
import tardis.common.blocks.SchemaComponentBlock;
import tardis.common.blocks.SchemaCoreBlock;
import tardis.common.blocks.ShieldBlock;
import tardis.common.blocks.SlabBlock;
import tardis.common.blocks.StairBlock;
import tardis.common.blocks.SummonerBlock;
import tardis.common.blocks.TardisBlock;
import tardis.common.blocks.TemporalAcceleratorBlock;
import tardis.common.blocks.TopBlock;
import tardis.common.items.ComponentItem;
import tardis.common.items.CraftingComponentItem;
import tardis.common.items.DecoratingTool;
import tardis.common.items.DimensionUpgradeItem;
import tardis.common.items.KeyItem;
import tardis.common.items.ManualItem;
import tardis.common.items.NameTagItem;
import tardis.common.items.SchemaItem;
import tardis.common.items.SonicScrewdriverItem;
import tardis.common.items.UpgradeChameleonItem;
import tardis.common.items.UpgradeItem;

public class TMRegistry
{
	public static TardisBlock					tardisBlock;
	public static TopBlock						tardisTopBlock;
	public static CoreBlock						tardisCoreBlock;
	public static ConsoleBlock					tardisConsoleBlock;
	public static EngineBlock					tardisEngineBlock;
	public static ComponentBlock				componentBlock;
	public static InternalDoorBlock				internalDoorBlock;
	public static SchemaBlock					schemaBlock;
	public static SchemaCoreBlock				schemaCoreBlock;
	public static SchemaComponentBlock			schemaComponentBlock;
	public static DebugBlock					debugBlock;
	public static LandingPadBlock				landingPad;
	public static GravityLiftBlock				gravityLift;
	public static ForceFieldBlock				forcefield;
	public static BatteryBlock					battery;
	public static InteriorDirtBlock				interiorDirtBlock;
	public static TemporalAcceleratorBlock		temporalAccelerator;
	public static ManualBlock					manualBlock;
	public static ManualHelperBlock				manualHelperBlock;
	public static SummonerBlock					summonerBlock;
	public static InternalMagicDoorBlock		magicDoorBlock;
	public static ShieldBlock					shieldBlock;
	public static LabBlock						labBlock;
	public static AdvancedLabBlock				advLab;

	public static DecoBlock						decoBlock;
	public static DecoTransBlock				decoTransBlock;
	public static StairBlock					stairBlock;
	public static SlabBlock						slabBlock;
	public static CompressedBlock				compressedBlock;

	@CustomRegistryItem("ColorableWall")
	public static ColorableBlock				colorableWallBlock;
	@CustomRegistryItem("ColorableFloor")
	public static ColorableBlock				colorableFloorBlock;
	@CustomRegistryItem("ColorableBrick")
	public static ColorableBlock				colorableBrickBlock;
	@CustomRegistryItem("ColorablePlank")
	public static ColorableBlock				colorablePlankBlock;
	@CustomRegistryItem("ColorableRoundel")
	public static ColorableBlock				colorableRoundelBlock;
	public static ColorableOpenRoundelBlock		colorableOpenRoundelBlock;

	@RegistryIgnore
	public static AbstractBlock					wallSimulacrumBlock;
	@RegistryIgnore
	public static AbstractBlock					floorSimulacrumBlock;
	@RegistryIgnore
	public static AbstractBlock					glassSimulacrumBlock;
	@RegistryIgnore
	public static AbstractBlock					brickSimulacrumBlock;
	@RegistryIgnore
	public static AbstractBlock					plankSimulacrumBlock;
	@RegistryIgnore
	public static AbstractBlock					decoSimulacrumBlock;

	public static HashSet<AbstractBlock>		unbreakableBlocks;

	public static SchemaItem					schemaItem;
	public static ComponentItem					componentItem;
	public static CraftingComponentItem			craftingComponentItem;
	public static KeyItem						keyItem;
	public static SonicScrewdriverItem			screwItem;
	public static ManualItem					manualItem;
	public static UpgradeItem					upgradeItem;
	public static NameTagItem					nameTag;
	public static DecoratingTool				decoTool;
	public static UpgradeChameleonItem			chameleonUpgradeItem;

	public static Map<Integer, AbstractItem>	dimensionUpgradeItems	= new HashMap<Integer, AbstractItem>();

	public static void init()
	{
		RegistryHelper.fillIn(TMRegistry.class);
		wallSimulacrumBlock = new CraftableCSimBlock(colorableWallBlock).register();
		floorSimulacrumBlock = new CraftableCSimBlock(colorableFloorBlock).register();
		brickSimulacrumBlock = new CraftableCSimBlock(colorableBrickBlock).register();
		plankSimulacrumBlock = new CraftableCSimBlock(colorablePlankBlock).register();
		glassSimulacrumBlock = new CraftableSimBlock(decoTransBlock).register();
		decoSimulacrumBlock = new CraftableSimBlock(decoBlock).register();

		unbreakableBlocks = Sets.newHashSet(tardisBlock, tardisTopBlock, tardisCoreBlock, tardisConsoleBlock, tardisEngineBlock, componentBlock, internalDoorBlock, decoBlock, decoTransBlock, interiorDirtBlock, schemaBlock,
				schemaCoreBlock, schemaComponentBlock, slabBlock, landingPad, labBlock, gravityLift, forcefield, battery, colorableWallBlock, colorableFloorBlock, colorableBrickBlock, colorablePlankBlock, colorableRoundelBlock,
				colorableOpenRoundelBlock, manualBlock, manualHelperBlock, shieldBlock, temporalAccelerator);

		if (!Configs.dimUpgradesIds[0].isEmpty())
			for (int i = 0; i < Configs.dimUpgradesIds.length; i++)
			{
				try
				{
					dimensionUpgradeItems.put(Integer.parseInt(Configs.dimUpgradesIds[i]), new DimensionUpgradeItem(Integer.parseInt(Configs.dimUpgradesIds[i])).register());
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
	}

	public static void initRecipes()
	{
		keyItem.initRecipes();
		componentItem.initRecipes();
		manualItem.initRecipes();
		labBlock.initRecipes();
		landingPad.initRecipes();
		forcefield.initRecipes();
		gravityLift.initRecipes();
		battery.initRecipes();
		interiorDirtBlock.initRecipes();
		craftingComponentItem.initRecipes();
		summonerBlock.initRecipes();
		upgradeItem.initRecipes();
		nameTag.initRecipes();
		decoTool.initRecipes();
		chameleonUpgradeItem.initRecipes();
		shieldBlock.initRecipes();
		CraftableSimBlock.initStaticRecipes();
		temporalAccelerator.initRecipes();
		compressedBlock.initRecipes();
	}
}
