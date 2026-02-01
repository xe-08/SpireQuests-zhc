package spireQuests.quests.maybelaterx;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.BloodVial;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

public class AutotransfusionQuest extends AbstractQuest {

    public AutotransfusionQuest() {
        super(QuestType.LONG, QuestDifficulty.HARD);
        new TriggerTracker<>(QuestTriggers.VICTORY, 3)
                .triggerCondition((x) -> ((float)AbstractDungeon.player.currentHealth / (float)AbstractDungeon.player.maxHealth) <= 0.3)
                .add(this);
        addReward(new QuestReward.RelicReward(new BloodVial()));

    }
}