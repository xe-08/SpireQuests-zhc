package spireQuests.quests.ramchops;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.ramchops.relics.MahjongRelic;
import spireQuests.util.Wiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static spireQuests.Anniv8Mod.makeID;

public class HashtagBlessedQuest extends AbstractQuest {

    private static final String ID = makeID(HashtagBlessedQuest.class.getSimpleName());

    public HashtagBlessedQuest(){
        super(QuestType.LONG, QuestDifficulty.NORMAL);

        new TriggerTracker<>(QuestTriggers.VICTORY, 1).triggerCondition(
                (x) -> AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss && AbstractDungeon.actNum == 2
        ).add(this);

        addReward(new QuestReward.RelicReward(new MahjongRelic()));

        this.isAutoComplete = true;
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum == 1;
    }


    @SpirePatch2(
            clz = AbstractRoom.class,
            method = "update"
    )
    public static class BlessingPatch{
        @SpireInsertPatch(locator = Locator.class)
        public static void SelectRandomEnemyForBlessing(AbstractRoom __instance){
            // if this quest exists
            HashtagBlessedQuest q = (HashtagBlessedQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && !quest.isCompleted() && !quest.isFailed())
                    .findAny()
                    .orElse(null);
            if(q != null) {
                Anniv8Mod.logger.info("Choosing random enemy to bless...");
                AbstractMonster blessTarget = getRandomNonMinionEnemy();

                if (blessTarget == null){
                    Anniv8Mod.logger.warn("Can't bless anyone because everyone is a minion.");
                }else{

                    boolean isBoss = AbstractDungeon.getMonsters().monsters.stream().anyMatch(
                            m -> m.type == AbstractMonster.EnemyType.BOSS
                    );

                    giveBlessing(blessTarget, isBoss);

                }

            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "applyStartOfCombatPreDrawLogic");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    enum MonsterBlessing{
        CURIO, //Boss and NonBoss
        STRENGTH, //Boss and NonBoss
        PAINSTAB, //NonBoss
        SPIKES, //NonBoss
        HEARTBEAT, //NonBoss
        METALLICIZE, //Boss and NonBoss, buff to 5
        ARTIFACT, //Boss and NonBoss
        PLATED, //Boss, buff to 8
        BUFFER, //Boss and NonBoss
        HIDE, //NonBoss, nerf to 3
        REGEN, //Boss and NonBoss, buff to 4
        MALLEABLE, //NonBoss
        CURL_UP //NonBoss, pair with Barricade
    }

    private static final List<MonsterBlessing> NORMAL_BLESSINGS = Arrays.asList(
            MonsterBlessing.CURIO,
            MonsterBlessing.STRENGTH,
            MonsterBlessing.METALLICIZE,
            MonsterBlessing.ARTIFACT,
            MonsterBlessing.BUFFER,
            MonsterBlessing.REGEN
    );

    private static final List<MonsterBlessing> BOSS_BLESSINGS = Collections.singletonList(
            MonsterBlessing.PLATED
    );

    private static final List<MonsterBlessing> NOT_BOSS_BLESSING = Arrays.asList(
            MonsterBlessing.PAINSTAB,
            MonsterBlessing.SPIKES,
            MonsterBlessing.HEARTBEAT,
            MonsterBlessing.HIDE,
            MonsterBlessing.MALLEABLE,
            MonsterBlessing.CURL_UP
    );

    public static void giveBlessing(AbstractMonster m, boolean isBoss){
        ArrayList<MonsterBlessing> blessings = new ArrayList<>(NORMAL_BLESSINGS);

        if(isBoss){
            Anniv8Mod.logger.info("Giving Boss Blessing...");
            blessings.addAll(BOSS_BLESSINGS);
        }else{
            Anniv8Mod.logger.info("Giving NonBoss Blessing...");
            blessings.addAll(NOT_BOSS_BLESSING);
        }

        int count = blessings.size();
        MonsterBlessing chosenBlessing = blessings.get(AbstractQuest.rng.random(count -1));

        switch (chosenBlessing){
            case CURIO:
                Wiz.applyToEnemy(m, new CuriosityPower(m, 1));
                break;
            case STRENGTH:
                Wiz.applyToEnemy(m, new StrengthPower(m, 3));
                break;
            case PAINSTAB:
                Wiz.applyToEnemy(m, new PainfulStabsPower(m));
                break;
            case SPIKES:
                Wiz.applyToEnemy(m, new ThornsPower(m, 1));
                break;
            case HEARTBEAT:
                Wiz.applyToEnemy(m, new BeatOfDeathPower(m, 1));
                break;
            case METALLICIZE:
                Wiz.applyToEnemy(m, new MetallicizePower(m, 5));
                break;
            case ARTIFACT:
                Wiz.applyToEnemy(m, new ArtifactPower(m, 3));
                break;
            case PLATED:
                Wiz.applyToEnemy(m, new PlatedArmorPower(m, 8));
                break;
            case BUFFER:
                Wiz.applyToEnemy(m, new BufferPower(m, 2));
                break;
            case HIDE:
                Wiz.applyToEnemy(m, new SharpHidePower(m, 3));
                break;
            case REGEN:
                Wiz.applyToEnemy(m, new RegenerateMonsterPower(m, 2));
                break;
            case MALLEABLE:
                Wiz.applyToEnemy(m, new MalleablePower(m));
                break;
            case CURL_UP:
                Wiz.applyToEnemy(m, new BarricadePower(m));
                Wiz.applyToEnemy(m, new CurlUpPower(m, 8));
                break;
        }
    }

    public static AbstractMonster getRandomNonMinionEnemy(){
        Object[] noMinionsList = AbstractDungeon.getMonsters().monsters.stream().filter(
                mon -> !mon.hasPower(MinionPower.POWER_ID)
        ).toArray();

        if (noMinionsList.length == 0){
            return null;
        }

        return (AbstractMonster) noMinionsList[AbstractQuest.rng.random(noMinionsList.length - 1)];
    }
}
