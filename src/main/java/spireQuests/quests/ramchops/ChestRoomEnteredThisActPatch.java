package spireQuests.quests.ramchops;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import spireQuests.Anniv8Mod;
import spireQuests.ui.QuestBoardProp;

public class ChestRoomEnteredThisActPatch {

    @SpirePatch(
            clz = TreasureRoom.class,
            method = "onPlayerEntry"
    )

    public static class TreasureRoomPatch {
        @SpirePostfixPatch()
        public static void OnPlayerEntry() {
            ChestRoomEnteredThisAct.onEnterChestRoom();
        }
    }

    @SpirePatch2(
            clz = AbstractDungeon.class,
            method = "dungeonTransitionSetup"
    )
    public static class dungeonTransitionSetup {
        @SpirePostfixPatch
        public static void dungeonTransitionPostfix(){
            ChestRoomEnteredThisAct.onEnterNewAct();
        }
    }


}
