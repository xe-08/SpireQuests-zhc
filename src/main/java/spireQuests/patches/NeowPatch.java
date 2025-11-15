package spireQuests.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import spireQuests.ui.QuestBoardProp;

import java.util.ArrayList;

public class NeowPatch {
    @SpirePatch(
            clz = NeowRoom.class,
            method = "render",
            paramtypez = SpriteBatch.class
    )
    public static class PostRender {
        @SpirePostfixPatch()
        public static void Render(NeowRoom original, SpriteBatch sb) {
            if (QuestBoardProp.questBoardProp != null) {
                QuestBoardProp.questBoardProp.render(sb);
            }
        }
    }

    @SpirePatch(
            clz = NeowRoom.class,
            method = "update"
    )
    public static class PostUpdate {
        @SpirePostfixPatch()
        public static void Update() {
            if (QuestBoardProp.questBoardProp != null) {
                QuestBoardProp.questBoardProp.update();
            }
        }
    }

    // We patch Exordium's constructor instead of NeowRoom's constructor because saving happens right after NeowRoom's
    // constructor is called, and we don't want that because it would make the quests generated for the board be saved
    // as seen immediately, causing inconsistencies if the user saves and reloads while still at Neow
    @SpirePatch(clz = Exordium.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractPlayer.class, ArrayList.class})
    @SpirePatch(clz = Exordium.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractPlayer.class, SaveFile.class})
    public static class ConstructorPatch {
        @SpirePostfixPatch
        public static void ConstructorPatch() {
            if (AbstractDungeon.currMapNode != null && AbstractDungeon.currMapNode.room instanceof NeowRoom) {
                QuestBoardProp.questBoardProp = new QuestBoardProp((float) Settings.WIDTH * 0.5F - 425.0F * Settings.xScale, AbstractDungeon.floorY + 189.0F * Settings.yScale, true);
            }
        }
    }
}