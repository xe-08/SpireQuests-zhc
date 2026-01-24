package spireQuests.quests.soytheproton;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.vfx.RemoveChoiceCardEffect;

public class SpeedrunQuest extends AbstractQuest {
    private boolean cardsSelected = false;
    public SpeedrunQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.COMBAT_END, 3)
                .triggerCondition((x) -> GameActionManager.turn <= 4)
                .setFailureTrigger(QuestTriggers.ACT_CHANGE)
                .add(this);
    }

    public void onComplete() {
        AbstractDungeon.effectList.add(new RemoveChoiceCardEffect());
    }


    @Override
    public boolean canSpawn() {
        return AbstractDungeon.floorNum <= 9 || AbstractDungeon.floorNum > 17 && AbstractDungeon.floorNum <= 26;
    }
}
