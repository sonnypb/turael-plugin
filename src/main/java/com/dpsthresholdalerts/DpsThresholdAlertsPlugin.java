package com.dpsthresholdalerts;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.Notifier;
import com.google.common.collect.ImmutableSet;
import net.runelite.api.gameval.NpcID;


@Slf4j
@PluginDescriptor(
	name = "Dps Threshold Alerts",
	description = "Alerts when your damage exceeds a threshold",
	tags = {"combat", "dps", "alts", "alting"}
)
public class DpsThresholdAlertsPlugin extends Plugin
{
	private static final ImmutableSet<Integer> BOSSES = ImmutableSet.of(
			NpcID.ABYSSALSIRE_SIRE_STASIS_SLEEPING, NpcID.ABYSSALSIRE_SIRE_STASIS_AWAKE, NpcID.ABYSSALSIRE_SIRE_STASIS_STUNNED, NpcID.ABYSSALSIRE_SIRE_PUPPET, NpcID.ABYSSALSIRE_SIRE_WANDERING, NpcID.ABYSSALSIRE_SIRE_PANICKING, NpcID.ABYSSALSIRE_SIRE_APOCALYPSE,
			NpcID.HYDRABOSS, NpcID.HYDRABOSS_P1_TRANSITION, NpcID.HYDRABOSS_P2_TRANSITION, NpcID.HYDRABOSS_P3_TRANSITION, NpcID.HYDRABOSS_4, NpcID.HYDRABOSS_3, NpcID.HYDRABOSS_2, NpcID.HYDRABOSS_FINALDEATH,
			NpcID.BARROWS_AHRIM, NpcID.BARROWS_DHAROK, NpcID.BARROWS_GUTHAN, NpcID.BARROWS_KARIL, NpcID.BARROWS_TORAG, NpcID.BARROWS_VERAC,
			NpcID.ARAXXOR, NpcID.ARAXXOR_DEAD,
			NpcID.GB_MOSSGIANT,
			NpcID.CLANCUP_CALLISTO, NpcID.CALLISTO,
			NpcID.CERBERUS_ATTACKING, NpcID.CERBERUS_SITTING, NpcID.CERBERUS_RESETTING,
			NpcID.CHAOSELEMENTAL, NpcID.CLANCUP_CHAOSELEMENTAL,
			NpcID.CHAOS_FANATIC,
			NpcID.GODWARS_SARADOMIN_AVATAR, NpcID.CLANCUP_GODWARS_SARADOMIN_AVATAR,
			NpcID.CORP_BEAST,
			NpcID.CRAZY_ARCHAEOLOGIST,
			NpcID.CRYSTAL_HUNLLEF_MELEE, NpcID.CRYSTAL_HUNLLEF_RANGED, NpcID.CRYSTAL_HUNLLEF_MAGIC, NpcID.CRYSTAL_HUNLLEF_DEATH,
			NpcID.CRYSTAL_HUNLLEF_MELEE_HM, NpcID.CRYSTAL_HUNLLEF_RANGED_HM, NpcID.CRYSTAL_HUNLLEF_MAGIC_HM, NpcID.CRYSTAL_HUNLLEF_DEATH_HM,
			NpcID.DAGCAVE_RANGED_BOSS, NpcID.DAGCAVE_MAGIC_BOSS, NpcID.DAGCAVE_MELEE_BOSS, NpcID.CLANCUP_DAGCAVE_RANGED_BOSS, NpcID.CLANCUP_DAGCAVE_MAGIC_BOSS, NpcID.CLANCUP_DAGCAVE_MELEE_BOSS,
			NpcID.GARGBOSS_DUSK_SPAWN, NpcID.GARGBOSS_DAWN_SPAWN, NpcID.GARGBOSS_DUSK_PHASE1_DEFENSIVE, NpcID.GARGBOSS_DAWN_PHASE1, NpcID.GARGBOSS_DAWN_PHASE1_TRANSITION, NpcID.GARGBOSS_DUSK_PHASE1_TRANSITION, NpcID.GARGBOSS_DUSK_PHASE1_FLYTRANSITION,
			NpcID.GODWARS_BANDOS_AVATAR, NpcID.CLANCUP_GODWARS_BANDOS_AVATAR,
			NpcID.MOLE_GIANT, NpcID.CLANCUP_MOLE_GIANT,
			NpcID.HESPORI,
			NpcID.POH_MOUNTED_KQ, NpcID.KALPHITE_QUEEN, NpcID.KALPHITE_FLYINGQUEEN, NpcID.SWAN_KALPHITE_1, NpcID.SWAN_KALPHITE_2, NpcID.CLANCUP_KALPHITE_QUEEN, NpcID.CLANCUP_KALPHITE_FLYINGQUEEN,
			NpcID.KING_DRAGON, NpcID.TWOCATS_KBD_CUTSCENE, NpcID.CLANCUP_KING_DRAGON,
			NpcID.SLAYER_KRAKEN_BOSS, NpcID.KRAKEN_PET, NpcID.POH_KRAKEN_PET,
			NpcID.GODWARS_ARMADYL_AVATAR, NpcID.CLANCUP_GODWARS_ARMADYL_AVATAR,
			NpcID.GODWARS_ZAMORAK_AVATAR, NpcID.CLANCUP_GODWARS_ZAMORAK_AVATAR,
			NpcID.TRAIL_MIMIC_NONCOMBAT, NpcID.TRAIL_MIMIC_COMBAT,
			NpcID.NEX, NpcID.NEX_SPAWNING, NpcID.NEX_SOULSPLIT, NpcID.NEX_DEFLECT, NpcID.NEX_DYING,
			NpcID.NIGHTMARE_DEAD, NpcID.NIGHTMARE_PHASE_02, NpcID.NIGHTMARE_PHASE_03, NpcID.NIGHTMARE_WEAK_PHASE_01, NpcID.NIGHTMARE_WEAK_PHASE_02, NpcID.NIGHTMARE_WEAK_PHASE_03, NpcID.NIGHTMARE_BLAST, NpcID.NIGHTMARE_INITIAL, NpcID.NIGHTMARE_DYING,
			NpcID.HILLGIANT_BOSS,
			NpcID.SARACHNIS,
			NpcID.SCORPIA,
			NpcID.RAT_BOSS_NORMAL, NpcID.RAT_BOSS_INSTANCE,
			NpcID.CATA_BOSS,
			NpcID.SMOKE_DEVIL_BOSS,
			NpcID.INFERNO_TZKALZUK_PLACEHOLDER,
			NpcID.TZHAAR_FIGHTCAVE_SWARM_BOSS, NpcID.CLANCUP_TZHAAR_FIGHTCAVE_SWARM_BOSS,
			NpcID.CLANCUP_VENENATIS, NpcID.VENENATIS,
			NpcID.VETION, NpcID.VETION_2,
			NpcID.POH_MOUNTED_VORKATH, NpcID.VORKATH_SLEEPING_NOOP, NpcID.VORKATH_SLEEPING, NpcID.VORKATH_QUEST, NpcID.VORKATH,
			NpcID.ZALCANO, NpcID.ZALCANO_WEAK,
			NpcID.SNAKEBOSS_BOSS_RANGED, NpcID.SNAKEBOSS_BOSS_MELEE, NpcID.SNAKEBOSS_BOSS_MAGIC,
			NpcID.YAMA);
	@Inject
	private Client client;

	@Inject
	private DpsThresholdConfig config;
	@Inject
	private Notifier notifier;

	private int totalDamage = 0;

	@Override
	protected void startUp() throws Exception
	{
		totalDamage = 0;
		log.debug("DPS Threshold Alerts started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		totalDamage = 0;
		log.debug("DPS Threshold Alerts stopped!");
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
		Actor actor = hitsplatApplied.getActor();

		if (!(actor instanceof NPC)) {
			return;
		}

		Hitsplat hitsplat = hitsplatApplied.getHitsplat();

		final int npcId = ((NPC) actor).getId();

		if (!BOSSES.contains(npcId))
		{
			return;
		}

		if (!hitsplat.isMine())
		{
			return;
		}

		int hit = hitsplat.getAmount();

		totalDamage += hit;

		if (totalDamage >= config.thresholdValue()) {
			if (config.enableThresholdAlert()) {
				notifier.notify("Over Threshold: " + totalDamage);
			}
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC npc = npcDespawned.getNpc();

		if (npc.isDead() && BOSSES.contains(npc.getId()))
		{
			totalDamage = 0;
			log.debug("Boss died, resetting threshold counter");
		}
	}

	@Provides
	DpsThresholdConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DpsThresholdConfig.class);
	}
}
